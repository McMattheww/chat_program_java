import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


class Client {

    public static void main(String[] args)
    {
        try (Socket socket = new Socket("localhost", 8888)) {

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            byte[] data = "hello world".getBytes(StandardCharsets.UTF_8);
            out.write(data);
            out.flush();
            byte[] buffer = new byte[2048];
            int bytes;
            //read bytes to display incoming chat messages
            while ((bytes = in.read(buffer)) != -1) {
                System.out.println(Arrays.toString(buffer));
            }
            //create seperate thread to create chat messages and send to output
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
