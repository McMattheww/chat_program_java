import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server {
    static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    private static String checkEmojis(String message) {
        Map<String, String> emojis = new HashMap<>();

        emojis.put(":thumbsup:", "\uD83D\uDC4D");
        emojis.put(":smile:", "\uD83D\uDE04");
        emojis.put(":heart:", "\u2764\uFE0F");
        emojis.put(":laugh:", "\uD83D\uDE06");
        emojis.put(":sad:", "\uD83D\uDE1E");
        emojis.put(":fire:", "\uD83D\uDD25");

        for (String key : emojis.keySet()) {
            message = message.replace(key, emojis.get(key));
        }

        return message;

    }

    private static void privateBroadcast(String targetName, String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client.name != null && client.name.equalsIgnoreCase(targetName)) {
                    client.out.println("(DM from " + sender.name + "): " + message);
                    sender.out.println("(DM to " + targetName + "): " + message);
                    return;
                }
            }
        }
        //user not found
        sender.out.println("User '" + targetName + "' not found.");
        
    }


    private static void broadcast(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                try{
                    client.out.println(message);
                } catch (Exception e) {
                    System.out.println("Failed to send message to a client");
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main (String[] args){

        //start the server on port 5000
        ServerSocket server = null;
        try {
            server = new ServerSocket(5000);
            server.setReuseAddress(true);

            //endless loop while server is running
            while (true){
                //accept client connection and create socket
                Socket clientSock = server.accept();
                //print connected host info
                System.out.println("New client connected " + clientSock.getInetAddress().getHostAddress());

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
        BufferedReader in;
        String name;



        //keep track of the client's socket during construction
        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
        }


        private void closeConnection() {
            try {
                clients.remove(this);
                broadcast(name + " has left the chat.", this);
                System.out.println(name + " has been unassigned.");

                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //method runs when the new thread is created
        public void run(){

            name = "";
            String message;

            try {
                //create client IO streams
                this.out = new PrintWriter(clientSocket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                //wait for client to send their username
                name = in.readLine();
                if (name == null) return; //client disconnected
                System.out.println(name + " has been assigned");
                broadcast(name + " has joined the chat!", this);

                //wait for messages in a loop, broadcast rto other clients
                while ((message = in.readLine()) != null) {
                    
                    if (message.equalsIgnoreCase("/quit")) break;

                    message = checkEmojis(message);

                    if (message.startsWith("@")) {
                        //finds index of where the name ends
                        int spaceIndex = message.indexOf(" ");

                        if (spaceIndex != -1) {
                            //extract target
                            String target = message.substring(1, spaceIndex);
                            //extract message
                            String dmMessage = message.substring(spaceIndex + 1);

                            privateBroadcast(target, dmMessage, this);
                        } else {
                            out.println("Invalid DM format. Try @<username> <message>");
                        }
                    } else {
                        broadcast(name + ": " + message, this);
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










