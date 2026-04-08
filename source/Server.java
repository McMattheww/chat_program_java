import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server {

    public static void main (String[] args){

        //start the server on port 5000
        ServerSocket server = null;
        List<ClientHandler> clients = new ArrayList<>();
        try {
            server = new ServerSocket(5000);
            server.setReuseAddress(true);

            //endless loop while server is running
            while (true){
                //accept client connection and create socket
                Socket clientSock = server.accept();
                //print connected host info
                System.out.println("New client connected" + client.getInetAddress().getHostAddress());

                //create a new clientHandler object and create new thread for it
                ClientHandler client = new ClientHandler(clientSock);
                clients.add(client);
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

    public static class ClientHandler implements Runnable {
        public final Socket clientSocket;

        //keep track of the client's socket during construction
        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
        }

        private void broadcast(String message) {
            for (ClientHandler client : clients) {
                client.out.println(message);
            }
        }

        private void closeConnection() {
            try {
                clients.remove(this);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //method runs when the new thread is created
        public void run(){

            InputStream in = null;
            OutputStream out = null;
            String name;
            String message;
            try {
                //create client IO streams
                in = new BufferReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream());

                name = in.readLine();
                System.out.println(name + " has been assigned");


                while ((message = in.readLine()) != null) {
                    
                    broadcast(name + ": " + message);
                
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                closeConnection();
            }
        }
    }
}










