import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server {

    static List<ClientHandler> clients;
    public static void main (String[] args){

        //start the server on port 5000
        ServerSocket server = null;
        clients = new ArrayList<>();
        try {
            server = new ServerSocket(5000);
            server.setReuseAddress(true);

            //endless loop while server is running
            while (true){
                //accept client connection and create socket
                Socket clientSock = server.accept();
                //print connected host info
                System.out.println("New client connected" + clientSock.getInetAddress().getHostAddress());

                //create a new clientHandler object and create new thread for it
                ClientHandler client = new ClientHandler(clientSock);
                clients.add(client);
                new Thread(client).start();
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
        PrintWriter out;


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
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //method runs when the new thread is created
        public void run(){

            String name;
            String message;
            BufferedReader in = null;
            PrintWriter out = null;
            try {
                //create client IO streams
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.out = new PrintWriter(clientSocket.getOutputStream(), true);

                //wait for client to send their username
                name = in.readLine();
                System.out.println(name + " has been assigned");

                //wait for messages in a loop, broadcast rto other clients
                while ((message = in.readLine()) != null) {
                    broadcast(name + ": " + message);
                    for (ClientHandler client : clients) {
                        client.out.println(message);
                    }
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










