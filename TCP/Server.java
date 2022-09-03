package TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import TCP.utils.Message;

import static TCP.utils.Message.*;

public class Server {

    List<String> messages;

    public Server() {
        this.messages = new ArrayList<>();
    }

    public void start() throws IOException {
        getMessages();

        while (true) {
            try {
                ServerSocket serverSocket = new ServerSocket(1234);
                Socket clientSocket  = serverSocket.accept();
                InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(clientSocket.getOutputStream());

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                while (true) {
                    String msgFromClient = bufferedReader.readLine();

                    System.out.println(CLIENT_MESSAGE + msgFromClient);

                    if (!msgFromClient.equalsIgnoreCase(HIT_ME)) {
                        bufferedWriter.write(MSG_RECEIVED_FROM_CLIENT + msgFromClient);
                    }

                    if (msgFromClient.equalsIgnoreCase(HIT_ME)) {
                        bufferedWriter.write(getRandomMessage());
                    }


                    bufferedWriter.newLine();

                    bufferedWriter.flush();

                    if (msgFromClient.equalsIgnoreCase(EXIT)) {
                        clientSocket.close();
                        inputStreamReader.close();
                        outputStreamWriter.close();
                        bufferedReader.close();
                        bufferedWriter.close();
                        break;
                    }
                }




            } catch (IOException e) {
                e.printStackTrace();

            }

        }
    }

    private void getMessages() throws IOException {
        Files.lines(Paths.get("/Users/tiagocosta/Documents/Mindera_exercises/Academy/Bootcamp/Network/src/TCP/MotivationalQuotes"))
                .forEach(messages::add);
    }

    private String getRandomMessage() {
        int index = (int) (Math.random() * messages.size());
        return messages.get(index);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();

    }
}
