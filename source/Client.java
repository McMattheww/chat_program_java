import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

class Client {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("Enter IP address of chat server to connect to: ");
            String IP = sc.nextLine();
            if (Objects.equals(IP, "quit")){break;}
            //try connecting to server,
            System.out.println("Connecting to server at " + IP);
            try (Socket socket = new Socket(IP, 5000)) {

                //assign the byte input and output streams for the server
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                String name = "";
                while (name.isEmpty()) {
                    System.out.println("Enter Name:");
                    name = sc.nextLine();
                }
                System.out.println("Welcome " + name);
                //send the client's name to the server
                out.write(name.getBytes(StandardCharsets.UTF_8));
                out.flush();

                //create outputHandler and start thread to handle output to server
                InputHandler inputHandler = new InputHandler(in);
                new Thread(inputHandler).start();

                String line = sc.nextLine();
                while (!"quit".equalsIgnoreCase(line)) {
                    //get next line of input, send to server as bytes
                    out.write(line.getBytes(StandardCharsets.UTF_8));
                    out.flush();
                    line = sc.nextLine();
                }
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
        private final DataInputStream input;
        private String name;

        public InputHandler(DataInputStream in){this.input = in;}

        public void run(){
            byte[] buffer = new byte[2048]; int bytes;

            //read bytes to display incoming chat messages
            try {
                while ((bytes = input.read(buffer)) != -1) {
                    System.out.println(Arrays.toString(buffer));
                    buffer = new byte[2048];
                }
            } catch (IOException e) {

            }

        }

    }

}
