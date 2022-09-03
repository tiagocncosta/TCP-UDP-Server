package TCP2;

import TCP2.Command.Shout;
import TCP2.Command.Whisper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Server {

    public List<ClientHandler> clientHandlerList;

    ServerSocket serverSocket;

    ExecutorService executorService;


    public static void main(String[] args) {
        Server server = new Server();
        server.startServer(8081);

    }

    private void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            clientHandlerList = new ArrayList<>();
            executorService = Executors.newCachedThreadPool();

            acceptClient();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Server is running");
    }

    private void acceptClient() {
        System.out.println("Server is accepting Clients");
        Socket socket = null;
        try {
            socket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(socket);
            clientHandlerList.add(clientHandler);
            executorService.submit(clientHandler);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            acceptClient();
        }
    }


    public void removeClient(ClientHandler clientHandler) throws IOException {
        clientHandler.sendMessageUser("See ya soon ;)");
        clientHandler.socket.close();
        clientHandlerList.remove(clientHandler);
    }


    public void broadCast(ClientHandler clientHandler, String message) {
        if (message.startsWith("/shout")) {
            Shout shout = new Shout();
            shout.execute(this, clientHandler);
            return;
        }
        clientHandlerList.stream()
                .filter(client -> !clientHandler.equals(client))
                .forEach(client -> {
                    try {
                        client.sendMessageUser(clientHandler.name + " : " + message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void whisper(ClientHandler clientHandler, String message) {
        if (message.startsWith("/whisper")) {
            Whisper whisper = new Whisper();
            whisper.execute(this, clientHandler);
            return;
        }

        String userName = message.substring(message.indexOf("<") + 1, message.indexOf(">"));
        String messageToSend = message.substring(message.indexOf(">") + 1);
        clientHandlerList.stream()
                .filter(client -> userName.equalsIgnoreCase(client.name))
                .forEach(client -> {
                    try {
                        client.sendMessageUser(clientHandler.name + " (whispered) : " + messageToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void lista(ClientHandler clientHandler) throws IOException {
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < clientHandlerList.size(); i++) {
            names.add(clientHandlerList.get(i).getName());
        }
        System.out.println(names);
        clientHandler.sendMessageUser("The users in chat are " + names);
    }

    public void help(ClientHandler clientHandler) throws IOException {
        String commands = "/help - > Gives you all the commands \n" +
                "/list - > Gives you the list of persons online \n" +
                "/quit - > Turns off your chat \n" +
                "/whisper <userName> - > let you whisper anyone on the chat";

        clientHandler.sendMessageUser(commands);
    }

    public class ClientHandler implements Runnable {
        private final Socket socket;
        private BufferedWriter out;
        private BufferedReader in;

        private String line;
        private String name;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        private void startBuffers() throws IOException {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        private void handleClient() throws IOException {
            sendMessageUser(name + " ,write your thoughts : ");
            readMessageFromUser();
            handleClient();
        }

        private void sendMessageUser(String message) throws IOException {
            out.write(message);
            out.newLine();
            out.flush();
        }

        public String getMessage() {
            return line;
        }

        public String getName() {
            return name;
        }

        private void readMessageFromUser() {
            try {
                this.line = in.readLine(); //blocking method
                if (line == null || line.equalsIgnoreCase("/quit")) {
                    removeClient(this);
                    return;
                }

                System.out.println(name + " ,sent: ".concat(line));
                if (line.startsWith("/whisper")) {
                    whisper(this, line);
                    return;
                }
                if (line.startsWith("/list")) {
                    lista(this);
                    return;
                }
                if (line.startsWith("/help")) {
                    help(this);
                } else {
                    broadCast(this, line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void greetClient() throws IOException {
            System.out.println("New client arrived");
            sendMessageUser("Welcome to our chat !" + "\n" + "Enter a name for the chat :");
            String name = in.readLine();
            this.name = name;
        }

        @Override
        public void run() {
            try {
                startBuffers();
                greetClient();
                handleClient();
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                // e.printStackTrace();
            }
        }


    }
}
