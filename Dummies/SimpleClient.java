import java.io.*;
import java.net.*;

public class SimpleClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1234);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String message = "Hello, server!";
            out.println(message);

            String response = in.readLine();
            System.out.println("Server response: " + response);

            in.close();
            out.close();
            socket.close();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
