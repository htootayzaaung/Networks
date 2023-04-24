import java.net.*;
import java.io.*;

public class KnockKnockServer 
{
    private ServerSocket serverSocket = null;
    private KnockKnockProtocol kkp = null;

    // Constructor to create a new ServerSocket object on port 2323 and a new KnockKnockProtocol object
    public KnockKnockServer() 
    {
        try 
        {
            serverSocket = new ServerSocket(2323);
        } 
        catch (IOException e) 
        {
            System.err.println("Could not listen on port: 2323.");
            System.exit(1);
        }
        kkp = new KnockKnockProtocol();
    }

    // Method to run the server
    public void runServer() 
    {
        Socket clientSocket = null;

        while (true) 
        {
            try 
            {
                // Accept an incoming client connection
                clientSocket = serverSocket.accept();
            } 
            catch (IOException e) 
            {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            // Print the client port number to console
            System.out.println("clientSocket port: " + clientSocket.getPort());

            try 
            {
                // Create input and output streams for the client connection
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine, outputLine;

                // Send the first message to the client (game start)
                outputLine = kkp.processInput(null);
                out.println(outputLine);

                // Continue the game while the client is sending input
                while ((inputLine = in.readLine()) != null) 
                {
                    outputLine = kkp.processInput(inputLine);
                    out.println(outputLine);

                    // End the game if the client sends "Bye."
                    if (outputLine.equals("Bye.")) 
                    {
                        break;
                    }
                }

                // Close the streams and the client socket
                out.close();
                in.close();
                clientSocket.close();
            } 
            catch (IOException e) 
            {
                System.out.println(e);
            }
        }
    }

    // Main method to create a new KnockKnockServer object and run the server
    public static void main(String[] args) 
    {
        KnockKnockServer kks = new KnockKnockServer();
        kks.runServer();
    }
}
