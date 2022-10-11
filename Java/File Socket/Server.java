import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    static ServerSocket serverSocket;
    static DataOutputStream dos;

    public static void main(String[] args) {

        try {
            serverSocket = new ServerSocket(8080);
            System.out.println("Server is running on port 8080");

            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                dos = new DataOutputStream(socket.getOutputStream());

                new Thread(() -> {
                    try {
                        File file = new File("C:\\Users\\ijlal\\Desktop\\Controller.java");
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        writer.write(String.valueOf(file.length()));
                        writer.newLine();
                        writer.flush();

                        FileInputStream fis = new FileInputStream(file);
                        byte[] buffer = new byte[1];

                        int prevProgress = 0;

                        System.out.print("\nProgress:-\n[");
                        //sending file in chunks
                        while (fis.read(buffer) > 0) {
                            dos.write(buffer);
                            dos.flush();

                            //progress
                            int progress = (int) ((fis.getChannel().position() * 70) / file.length());
                            if (progress != prevProgress) {
                                System.out.print("=");
                                prevProgress = progress;
                            }
                        }
                        System.out.println("]");
                        fis.close();
                        dos.close();
                        System.out.println("\nFile sent");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }
}
