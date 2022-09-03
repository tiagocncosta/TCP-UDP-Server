package UDP2;

import UDP.utils.Messages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import static UDP2.utils.Messages.*;



public class Client {
    private DatagramSocket clientSocket;

    private BufferedReader reader = null;
    private byte[] buffer;
    int portNumber;
    String host;

    public static void main(String[] args) throws IOException {

        Client client = new Client();
        client.initializeClient();
        try {
            client.communicateWithServer(client.host, client.portNumber);
        } catch (UnknownHostException ex) {
            System.err.println(ERROR_HOST);
        } catch (NumberFormatException ex) {
            System.err.println(ERROR_PORT);
        } catch (SocketTimeoutException exception) {
            System.err.println(ERROR_TIMEOUT);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        } finally {
            if (client.clientSocket != null) {
                client.clientSocket.close();
            }
        }
    }



    private void initializeClient() throws IOException {
        try {
            reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(CLIENT_PORT);
            String port = reader.readLine();
            portNumber = Integer.parseInt(port);
            System.out.println(HOST);
            host = reader.readLine();
            clientSocket = new DatagramSocket();
        } catch (IOException e) {
            System.out.println(e.getMessage());


        }
    }


    private void communicateWithServer(String host, int port) throws IOException {
        String message;
        System.out.println(SEND_MESSAGE);
        while (!(message = reader.readLine()).equalsIgnoreCase(Messages.EXIT)) {
            sendMessage(host, port, message);
            receiveMessage();
            System.out.println(SEND_MESSAGE);
        }
        sendMessage(host,port,message);

    }

    private void sendMessage(String host, int port, String message) throws IOException {

        buffer = message.getBytes();
        DatagramPacket clientMessage = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(host), port);
        clientSocket.send(clientMessage);

    }

    private void receiveMessage() throws IOException {

        byte[] serverMessage = new byte[1024];

        DatagramPacket receiveMessage = new DatagramPacket(serverMessage, serverMessage.length);

        clientSocket.receive(receiveMessage);

        String messageReceived = new String(receiveMessage.getData(), 0, receiveMessage.getLength());

        System.out.println(SERVER_RESPONSE + messageReceived);

    }
}
