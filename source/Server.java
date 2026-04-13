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

        emojis.put(":grin:", "\uD83D\uDE01");
        emojis.put(":wink:", "\uD83D\uDE09");
        emojis.put(":cry:", "\uD83D\uDE22");
        emojis.put(":angry:", "\uD83D\uDE20");
        emojis.put(":surprised:", "\uD83D\uDE2E");
        emojis.put(":cool:", "\uD83D\uDE0E");
        emojis.put(":kiss:", "\uD83D\uDE18");

        emojis.put(":thinking:", "\uD83E\uDD14");
        emojis.put(":clap:", "\uD83D\uDC4F");
        emojis.put(":ok:", "\uD83D\uDC4C");
        emojis.put(":wave:", "\uD83D\uDC4B");
        emojis.put(":pray:", "\uD83D\uDE4F");
        emojis.put(":muscle:", "\uD83D\uDCAA");
        emojis.put(":eyes:", "\uD83D\uDC40");

        emojis.put(":skull:", "\uD83D\uDC80");
        emojis.put(":poop:", "\uD83D\uDCA9");
        emojis.put(":ghost:", "\uD83D\uDC7B");
        emojis.put(":alien:", "\uD83D\uDC7D");
        emojis.put(":robot:", "\uD83E\uDD16");
        emojis.put(":cat:", "\uD83D\uDC31");
        emojis.put(":dog:", "\uD83D\uDC36");
        emojis.put(":unicorn:", "\uD83E\uDD84");
        emojis.put(":dragon:", "\uD83D\uDC09");
        emojis.put(":pizza:", "\uD83C\uDF55");

        emojis.put(":burger:", "\uD83C\uDF54");
        emojis.put(":fries:", "\uD83C\uDF5F");
        emojis.put(":coffee:", "\u2615");
        emojis.put(":beer:", "\uD83C\uDF7A");
        emojis.put(":cake:", "\uD83C\uDF70");
        emojis.put(":gift:", "\uD83C\uDF81");
        emojis.put(":balloon:", "\uD83C\uDF88");
        emojis.put(":star:", "\u2B50");
        emojis.put(":sparkles:", "\u2728");
        emojis.put(":sun:", "\u2600\uFE0F");

        emojis.put(":moon:", "\uD83C\uDF19");
        emojis.put(":cloud:", "\u2601\uFE0F");
        emojis.put(":rainbow:", "\uD83C\uDF08");
        emojis.put(":snowman:", "\u2603\uFE0F");
        emojis.put(":zap:", "\u26A1");
        emojis.put(":check:", "\u2705");
        emojis.put(":cross:", "\u274C");
        emojis.put(":question:", "\u2753");
        emojis.put(":exclamation:", "\u2757");
        emojis.put(":lock:", "\uD83D\uDD12");

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










