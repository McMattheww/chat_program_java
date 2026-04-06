import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


class Client {

    public static void main(String[] args)
    {
        //try connecting to server, for now the server is limited to one on localhost
        try (Socket socket = new Socket("localhost", 8888)) {

            //assign the byte input and output streams for the server
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            //create outputHandler and start thread to handle output to server
            OutputHandler outputHandler = new OutputHandler(out);
            new Thread(outputHandler).start();

            //variables to hold the buffer
            byte[] buffer = new byte[2048];
            int bytes;

            //read bytes to display incoming chat messages in infinite loop
            while ((bytes = in.read(buffer)) != -1) {
                System.out.println(Arrays.toString(buffer));
            }

        }//error connecting to server
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class OutputHandler implements Runnable {
        private final DataOutputStream output;

        public OutputHandler(DataOutputStream out){ this.output = out;}

        public void run(){
            //keep getting input from user in loop, then send to output
            Scanner sc = new Scanner(System.in);


            String line;
            try {
                //while the user does not type "quit"
                while (!Objects.equals(line = sc.nextLine(), "quit")) {
                    //get next line of input, send to server as bytes
                    output.write(line.getBytes(StandardCharsets.UTF_8));
                    output.flush();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
