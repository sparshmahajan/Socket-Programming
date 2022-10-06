import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    ServerSocket serverSocket;
    static ArrayList<ClientsHandler> clientHandlers;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        clientHandlers = new ArrayList<>();
    }

    /**
     * Method to start the server and receive connections
     */
    public void startServer() {
        try {
            //server socket will run indefinitely for new clients
            while (!serverSocket.isClosed()) {
                //execution will stop here until a client connects
                Socket socket = serverSocket.accept();
                //when a client connects, a new thread will be created to handle the client
                Thread thread = new Thread(new ClientsHandler(socket));
                thread.start();
            }
        } catch (Exception e) {
            closeServer(this.serverSocket);
        }
    }

    /**
     * Method to Close the server in case of an exception
     *
     * @param serverSocket the server socket to close
     */
    public void closeServer(ServerSocket serverSocket) {
        if (serverSocket != null) try {
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to broadcast a message to all clients
     *
     * @param message the message to be sent
     */
    public static void broadcastMessage(String message, String sender) {
        for (ClientsHandler clients : Server.clientHandlers) {

            //send the message to all clients except the sender
            if (clients.userName.equals(sender)) continue;

            try {
                clients.writer.write(message);
                clients.writer.newLine();
                clients.writer.flush();
            } catch (Exception e) {
                closeClient(clients.userName, clients.socket, clients.reader, clients.writer, clients);
            }
        }
    }

    /**
     * Method to close a client connection
     *
     * @param username       the username of the client
     * @param socket         the socket of the client
     * @param reader         bufferedReader of the client
     * @param writer         bufferedWriter of the client
     * @param clientsHandler list of clients
     */
    public static void closeClient(
            String username,
            Socket socket,
            BufferedReader reader,
            BufferedWriter writer,
            ClientsHandler clientsHandler
    ) {
        try {
            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            clientHandlers.remove(clientsHandler);
            broadcastMessage(username + " has left the chat", username);

            //closing the current thead if the client is disconnected
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            //create a server socket
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Server started...");
            Server server = new Server(serverSocket);

            //start the server
            server.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
