import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientsHandler implements Runnable {

    //variables for every client that connects
    Socket socket;
    BufferedReader reader;
    BufferedWriter writer;
    String userName;

    ClientsHandler(Socket socket) {
        try {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.userName = reader.readLine();

            //add the client to the list of clients
            Server.clientHandlers.add(this);
            System.out.println(userName + " connected");

            //send a message to all clients that a new client has connected
            Server.broadcastMessage(userName + " has joined the chat", userName);
        } catch (Exception e) {

            //if an exception occurs, close the connection
            Server.closeClient(userName, socket, reader, writer, this);
        }
    }

    /**
     * This Method runs on a background thread
     */
    @Override
    public void run() {
        String message;
        while (this.socket.isConnected()) {
            try {
                message = reader.readLine();
                Server.broadcastMessage(message, userName);
            } catch (Exception e) {
                System.out.println(userName + " disconnected");

                //if an exception occurs, close the connection
                Server.closeClient(userName, socket, reader, writer, this);
            }
        }
    }

}
