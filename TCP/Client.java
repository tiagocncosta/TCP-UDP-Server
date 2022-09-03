package TCP;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static TCP.utils.Message.*;

public class Client {

    public static void main(String[] args) throws IOException {
        Client client=new Client();
        try {
            client.start();
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    private void start() throws IOException {
        Socket socket = new Socket("localhost", 1234);
        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

        try {

            Scanner scanner = new Scanner(System.in);

            while (true){
                String msgToSend = scanner.nextLine();

                bufferedWriter.write(msgToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                System.out.println(SERVER + bufferedReader.readLine());

                if (msgToSend.equalsIgnoreCase(EXIT)){
                    break;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if (socket!= null){
                    if (inputStreamReader != null){
                    inputStreamReader.close();
                    } if (outputStreamWriter != null){
                        outputStreamWriter.close();
                    } if (bufferedReader != null){
                        bufferedReader.close();
                    } if (bufferedWriter != null){
                        bufferedWriter.close();
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
