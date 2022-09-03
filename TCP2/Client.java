package TCP2;


import java.io.*;
import java.net.Socket;

/**
 * Hero is the main entity we'll be using to . . .
 * <p>
 * Please see the {@link TCP2/Server} class for starting this server
 *
 * @author Captain America
 * <p>
 * Copyright [2022] [Captain America]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.* *
 */
public class Client {
    Socket socket;
    BufferedWriter socketWriter;
    BufferedReader consoleReader;

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.startConsoleReader();
        client.handleServer();

    }

    private void startConsoleReader() {
        this.consoleReader = new BufferedReader(new InputStreamReader(System.in));
    }

    private void handleServer() {
        connectToServer();
        //   System.out.println("Connected");
        startListenToServer();
        //   System.out.println("Listening");
        communicateWitServer();
        //   System.out.println("After comm");
        close();
    }

    private void startListenToServer() {
        try {
            new Thread(new ServerListener(socket.getInputStream())).start();
        } catch (IOException e) {
            handleServer();
        }
    }

    private void communicateWitServer() {
        try {
            if (!socket.isClosed()) {
                sendMessages();

                communicateWitServer();
            }
        } catch (IOException e) {
            System.out.println("Hum... seems that the server is dead");
            handleServer();
        }
    }


    private void sendMessages() throws IOException {
        String message = readFromConsole("\r");
        socketWriter.write(message);
        socketWriter.newLine();
        socketWriter.flush();
        if (message.equalsIgnoreCase("/quit")) {
            System.out.println("bye bye");
            close();
        }

    }


    private void connectToServer() {
        String hostName = "localhost"; //readFromConsole("Hello! What's the host of the server?");
        int port = 8081;//getPortNumber();
        try {
            this.socket = new Socket(hostName, port);
            this.socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("Hum... seems that the server is dead");
            connectToServer();
        }
    }

    private int getPortNumber() {
        try {
            return Integer.parseInt(readFromConsole("And how about port to connect with"));
        } catch (NumberFormatException e) {
            System.out.println("that is not a number. Start counting 1, 2 , 3... these are numbers");
            return getPortNumber();
        }
    }

    private String readFromConsole(String question) {
        String message = null;
        System.out.println(question);
        try {
            message = consoleReader.readLine();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return message;
    }

    private void close() {
        try {
            System.out.println("Closing socket");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private class ServerListener implements Runnable {
        BufferedReader serverReader;

        public ServerListener(InputStream inputStream) {
            this.serverReader = new BufferedReader(new InputStreamReader(inputStream));
        }

        @Override
        public void run() {
            try {
                readMessage();
            } catch (IOException e) {
            }
        }

        private void readMessage() throws IOException {
            //     System.out.println("reading");
            String readMessageFromServer = serverReader.readLine();
            System.out.println(readMessageFromServer);
            readMessage();

        }
    }

}
