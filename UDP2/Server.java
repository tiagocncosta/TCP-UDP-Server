package UDP2;

import javax.sound.sampled.Port;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


import static UDP2.utils.Messages.*;

public class Server {
    private DatagramSocket servidorSocket;
    private byte[] buffer = new byte[256];

    private ArrayList<String> motivationalQuotes = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.initializeServe(8081);
        try {
            server.communicateWithClient();
        }catch (SocketException e){
            System.out.println("socket is closed!");
        }

    }


    public void initializeServe(int port) throws IOException {
        servidorSocket = new DatagramSocket(port);
    }

    public void communicateWithClient() throws IOException {
        getMessages();
        while (servidorSocket.isBound()) {
            receiveMessage();
        }
    }
        private void getMessages() throws IOException {
            Files.lines(Paths.get("/Users/tiagocosta/Documents/Mindera_exercises/Academy/Bootcamp/Network/src/UDP2/MotivationalQuotes"))
                    .forEach(motivationalQuotes::add);
        }

        private void receiveMessage() throws IOException {
            DatagramPacket receiveClientPacket = new DatagramPacket(buffer, buffer.length);
            System.out.println(WAITING_PACKET);
            servidorSocket.receive(receiveClientPacket);//  o servidor recebe a mensagem do cliente para dentro do receiveClientPacket

            String clientMessage = new String(receiveClientPacket.getData(), 0, receiveClientPacket.getLength());
            if(clientMessage.equalsIgnoreCase("exit")){
                System.out.println("Server is closing");
                closeServer();
                return;
            }

            System.out.println(CLIENT_SENT + clientMessage);

            String serverResponse = MESSAGE_RECEIVED + clientMessage;

            if(clientMessage.equalsIgnoreCase("HIT ME")){
                serverResponse = motivationalQuotes.get((int) (Math.random() * motivationalQuotes.size()));
            }

            InetAddress clientAdress = receiveClientPacket.getAddress(); //depois de receber a mensagem conseguimos obter o port e o adress
            int clientPort = receiveClientPacket.getPort();// ja temos o port e o adress para onde responder

            sendMessageToClient(serverResponse,clientAdress, clientPort);

            }

        private void sendMessageToClient(String serverResponse, InetAddress clientAdress, int port) throws IOException {
            byte[] serverResponseBytes = serverResponse.getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendServidorPacket = new DatagramPacket(serverResponseBytes, serverResponseBytes.length, clientAdress, port);
            servidorSocket.send(sendServidorPacket);

        }

        private void closeServer(){
        servidorSocket.close();
        }

    }


