import java.io.*;
import java.net.*;
import java.util.*;

public class DailyAdviceServer {

    // The list of advice that can be sent to clients
    private String[] adviceList = {
        "Take smaller bites",
        "Go for the tight jeans. No they do NOT make you look fat",
        "One word: inappropriate",
        "Just for today, be honest. Tell your boss what you *really* think",
        "You might want to rethink that haircut"
    };

    // Randomly select an advice from the list
    private String getAdvice() {
        int random = (int) (Math.random() * adviceList.length);
        return adviceList[random];
    }

    // Main method to run the server
    public void runServer() {
        try {
            // Create a server socket on port 4242
            ServerSocket serverSock = new ServerSocket(4242);

            while (true) {
                // Accept a client connection and create a socket
                Socket sock = serverSock.accept();

                // Get the client IP address and print it to console
                InetAddress inet = sock.getInetAddress();
                System.out.println("Connection made from " + inet.getHostName());

                // Send a single line of text (an advice) to the client
                PrintWriter writer = new PrintWriter(sock.getOutputStream());
                String advice = getAdvice();
                writer.println(advice);  // Write to client
                writer.close();

                // Print the advice to console as a local server echo
                System.out.println(advice);
                sock.close();  // Close the socket
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public static void main(String[] args) {
        DailyAdviceServer server = new DailyAdviceServer();
        server.runServer();
    }
}

/*

1.)ServerSocket serverSock = new ServerSocket(4242); - Creates a server socket that listens for incoming client connections on port 4242.

2.)Socket sock = serverSock.accept(); - Waits for an incoming client connection on the server socket. When a client connects, the accept() 
method returns a new socket object representing the connection to the client.

3.)InetAddress inet = sock.getInetAddress(); - Retrieves the IP address of the connected client.

4.)PrintWriter writer = new PrintWriter(sock.getOutputStream()); - Creates a new PrintWriter object that writes to the output stream of the 
socket. This will be used to send data back to the client.

5.)String advice = getAdvice(); - Generates a random advice string to send to the client.

6.)writer.println(advice); - Sends the advice string to the client by writing it to the socket's output stream.

7.)writer.close(); - Closes the PrintWriter object, flushing any remaining data to the output stream.

8.)sock.close(); - Closes the socket connection to the client.

*/