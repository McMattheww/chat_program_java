import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

class Client {

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
                InputHandler inputHandler = new InputHandler(in);
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

    void close(){}
    
    private static class InputHandler implements Runnable {
        public final BufferedReader input;
        public String name;

        public InputHandler(BufferedReader in){this.input = in;}

        public void run(){


            //read and display incoming chat messages
            String message;
            try {
                while ((message = input.readLine()) != null) {
                    System.out.println(message);
                }

                //fixed server death here. 
                System.out.println("Server disconnected. Exiting...");
                System.exit(0);

            } catch (IOException e) {
                System.out.println("Connection Lost. Exiting...");
                System.exit(0);
            }

        }

    }

}
