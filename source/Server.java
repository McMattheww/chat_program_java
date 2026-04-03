import java.net.*;
import java.io.*;
import java.util.*;

public class Server {

    public static void main (String[] args){
        ServerSocket server = null;
        try {
            server = new ServerSocket(8888);
            server.setReuseAddress(true);

            while (true){
                Socket client = server.accept();
                System.out.println("New client connected" + client.getInetAddress().getHostAddress());

                ClientHandler clientSock = new ClientHandler(client);
                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (server != null) {
                try {
                    server.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
        }

        public void run(){

            InputStream in = null;
            OutputStream out = null;
            try {
                in = clientSocket.getInputStream();
                out = clientSocket.getOutputStream();

                byte[] buffer = new byte[1024];
                int bytes;
                while ((bytes = in.read(buffer)) != -1) {
                    System.out.println(buffer);
                }
                out.write(1000);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (out != null){
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                        clientSocket.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}










