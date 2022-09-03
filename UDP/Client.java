package UDP;

import UDP.utils.Messages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Client {

    private static final int BUFFER_SIZE = 1024;
    private static final int TIMEOUT = 10000;

    private DatagramSocket clientSocket;
    private BufferedReader reader;

    public static void main(String[] args) {
        Client udpClient = new Client();

        try {
            udpClient.start();
        } catch (UnknownHostException ex) {
            System.err.println(Messages.ERROR_HOST);
        } catch (NumberFormatException ex) {
            System.err.println(Messages.ERROR_PORT);
        } catch (SocketTimeoutException exception) {
            System.err.println(Messages.ERROR_TIMEOUT);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        } finally {
            if (udpClient.clientSocket != null) {
                udpClient.clientSocket.close();
            }
        }
    }

    private void start() throws IOException, NumberFormatException {
        reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print(Messages.HOSTNAME);
        String host = reader.readLine();

        System.out.print(Messages.PORT);
        int port = Integer.parseInt(reader.readLine());

        communicateWithServer(host, port);
    }

    private void communicateWithServer(String host, int port) throws IOException {
        String message;
        System.out.println(Messages.INSERT_COMMAND);
        while (!(message = reader.readLine()).equalsIgnoreCase(Messages.EXIT)) {
            sendDatagram(host, port, message);
            receiveDatagram();
            System.out.println(Messages.INSERT_COMMAND);
        }

    }

    private void sendDatagram(String host, int port, String message) throws IOException {
        clientSocket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(host);
        byte[] bytes = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, address, port);
        clientSocket.send(sendPacket);
    }

    private void receiveDatagram() throws IOException {
        byte[] receiveData = new byte[BUFFER_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        clientSocket.setSoTimeout(TIMEOUT);
        clientSocket.receive(receivePacket);

        String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.print(response);


    }
}
