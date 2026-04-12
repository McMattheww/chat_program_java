import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

class Client {
    BufferedReader in;
    PrintWriter out;
    InputHandler inputHandler;
    Socket socket;


    public Client(){ in = null; out = null; socket = null;}

    //called by the GUI to have the client send a string to the server
    public void send_message(String message){
        out.println(message);
    }

    //called by the GUI to have the client start receiving messages and print to console
    public void receive_messages(){
        if (in != null) {
            this.inputHandler = new InputHandler(in, this.socket);
            new Thread(inputHandler).start();
        }
    }

    //called by GUI to try and initiate a connection using the provided IP and name.
    public void connect(String IP, String name){
        System.out.println("Connecting to server at " + IP);

        try {
            disconnect();

            socket = new Socket(IP, 5000);

            //assign input and output streams for the server to accept strings
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Welcome " + name);
            //send username to server
            out.println(name);

            receive_messages();

        }//error connecting to server
        catch (IOException e){
            System.out.println("Error connecting to server at " + IP);
        }
    }

    //called by gui to close the current connection, if one exists
    public void disconnect() {
        try {
            if (socket != null && socket.isConnected()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing connection with server ");
        }
    }



    //receive input from server and send as output to console
    private static class InputHandler implements Runnable {
        public final BufferedReader input;
        Socket socket;
        public String name;

        public InputHandler(BufferedReader in, Socket sock) {
            this.input = in;
            this.socket = sock;
        }

        public void run() {

            //read and display incoming chat messages
            String message;
            try {


                while ((message = input.readLine()) != null) {
                    System.out.println(message);
                }


                // fixed server death here.
                // Matthew note- because the GUI client allows for reconnection after a lost connection,
                // I removed the system.exit statements, and instead just ensured the socket is closed

                // Rylan note - I tried reconnecting and it didn't work. :(

                System.out.println("Server disconnected. Attempting to reconnect... (type 'quit' to exit)");
                try {

                    if (socket != null && socket.isConnected()) {
                        socket.close();
                    }
                } catch (IOException e) {
                    System.out.println("Something unexpected happened (Exception)");
                }
            } catch (IOException e) {
                System.out.println("Connection Lost. Exiting...");
                try {
                    if (socket != null && socket.isConnected()) {
                        socket.close();
                    }
                } catch (IOException e2) {
                    System.out.println("Something unexpected happened (Exception)");
                }
            }

        }
    }



    //main function called when Client.class is run directly, runs the client in the terminal
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);


        while (true) {  //prompt user to enter IP of the chat server they want to connect to
            System.out.println("Enter IP address of chat server to connect to: ");
            String IP = sc.nextLine();

            //if user types "quit" instead of an IP, exit program
            if (IP.equalsIgnoreCase("quit")){break;}

            //try connecting to server
            System.out.println("Connecting to server at " + IP);
            try (Socket socket = new Socket(IP, 5000)) {

                //assign input and output streams for the server to accept strings
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                //get client's username
                String name = "";
                while (name.isEmpty()) {
                    System.out.println("Enter Name:");
                    name = sc.nextLine();
                }

                System.out.println("Welcome " + name);
                //send username to server
                out.println(name);


                //create inputHandler and start thread to handle input from server
                InputHandler inputHandler = new InputHandler(in, socket);
                new Thread(inputHandler).start();

                // get user input and send to server, until the user types "quit"
                String line;
                while (true) { // previous version was buggy
                    line = sc.nextLine();

                    if ("quit".equalsIgnoreCase(line)){
                        out.println("/quit");
                        break;
                    }

                    out.println(line);
                }
                //user has typed quit, close socket, exit program
                socket.close();
                break;
            }//error connecting to server
            catch (IOException e){
                System.out.println("Error connecting to server at " + IP);
            }
        }
        sc.close();
    }

}

