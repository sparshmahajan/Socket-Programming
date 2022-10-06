import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    Socket socket;
    BufferedReader reader;
    BufferedWriter writer;
    String userName;

    public Client(Socket socket, String userName) {
        try {
            this.socket = socket;
            this.reader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new java.io.OutputStreamWriter(socket.getOutputStream()));
            this.userName = userName;
        } catch (Exception e) {
            closeConnection(this.socket, this.reader, this.writer);
        }
    }

    public void sendMessage() {
        //current Thread will be used to send messages
        try {
            writer.write(userName);
            writer.newLine();
            writer.flush();

            Scanner scanner = new Scanner(System.in);
            String message;
            while (socket.isConnected()) {
                message = scanner.nextLine();
                writer.write(userName + ": " + message);
                writer.newLine();
                writer.flush();
            }
        } catch (Exception e) {
            closeConnection(this.socket, this.reader, this.writer);
        }
    }

    public void listenToServer() {

        //new thread will be used to listen to the server for incoming messages
        new Thread(() -> {
            String message;
            while (socket.isConnected()) {
                try {
                    message = reader.readLine();
                    System.out.println(message);
                } catch (Exception e) {
                    //if an exception occurs (Server disconnects), close the connection
                    System.out.println("Server disconnected");
                    closeConnection(this.socket, this.reader, this.writer);
                }
            }
        }).start();
    }

    /**
     * Method to close the connection in case of an exception
     * @param socket the socket to close
     * @param reader bufferedReader to close
     * @param writer bufferedWriter to close
     */
    public void closeConnection(Socket socket, BufferedReader reader, BufferedWriter writer) {
        try {
            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();

            //closing the application
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name:");

        //waits for the user to enter his name
        String userName = scanner.nextLine();

        try {
            //create a socket and connect to the server
            Socket socket = new Socket("localhost", 1234);
            Client client = new Client(socket, userName);

            //start listening to the server (Asynchronously)
            client.listenToServer();
            //start sending messages to the server
            client.sendMessage();
        } catch (Exception e) {
            System.out.println("Error: Server is not running");
        }

    }
}
