import java.net.*;
import java.io.*;

public class SimpleServer {

    public static void main(String[] args) throws IOException {

        // Create a new ServerSocket on a specified port
        int port = 1234;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server listening on port " + port);

        while (true) {
            // Listen for a connection using the accept() method
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected from " + clientSocket.getInetAddress().getHostName());

            // Set up Socket input and output streams for I/O
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Communicate using the agreed protocol
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client: " + inputLine);
                out.println("Server received: " + inputLine);
                if (inputLine.equals("bye")) {
                    break;
                }
            }

            // Close the connection
            out.close();
            in.close();
            clientSocket.close();
            System.out.println("Connection closed");
        }
    }
}

/*
    A recipe for a simple server is:

        1.) Create a new "ServerSocket" on a port.
        2.) Listen for a connection using the "accept()" method, which waits until the client connects, when
            it returns a "Socket" object.
        3.) Set up a "Socket" input and output streams for I/O.
        4.) Communicate using the agreed protocol.
        5.) Client, server or both close the connection.
        
*/