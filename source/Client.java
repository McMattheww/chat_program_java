import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


class Client {

    public static void main(String[] args)
    {
        try (Socket socket = new Socket("localhost", 8888)) {

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            byte[] data = "hello world".getBytes(StandardCharsets.UTF_8);
            out.write(data);
            out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
