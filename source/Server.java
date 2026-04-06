import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server {

    public static void main (String[] args){

        //start the server on port 8888
        ServerSocket server = null;
        try {
            server = new ServerSocket(8888);
            server.setReuseAddress(true);

            //endless loop while server is running
            while (true){
                //accept client connection and create socket
                Socket client = server.accept();
                //print connected host info
                System.out.println("New client connected" + client.getInetAddress().getHostAddress());

                //create a new clientHandler object and create new thread for it
                ClientHandler clientSock = new ClientHandler(client);
                new Thread(clientSock).start();
            }
        }
        //error starting the server
        catch (IOException e) {
            e.printStackTrace();
        }
        //once server closes, never called now because of the previous while true loop
        finally {
            if (server != null) {
                try {
                    //close server if it was successfully opened
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

        //keep track of the client's socket during construction
        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
        }

        //method runs when the new thread is created
        public void run(){

            InputStream in = null;
            OutputStream out = null;
            try {
                //get client input/output byte streams
                in = clientSocket.getInputStream();
                out = clientSocket.getOutputStream();

                //create buffer to receive input from client
                byte[] buffer = new byte[1024];
                int bytes;
                String line;
                //while buffer read from client is not -1 size
                while ((bytes = in.read(buffer)) != -1) {
                    //keep reading input and output to the server
                    line = new String(buffer, StandardCharsets.UTF_8);
                    System.out.println(line);
                    //TODO: instead of printing input from clients to the terminal, the input should be redirected
                    // to the output of every active client socket
                }
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










