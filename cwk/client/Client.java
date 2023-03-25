import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client 
{
    private static final String HOSTNAME = "localhost";
    private static final int PORT_NUMBER = 6500; // Replace with chosen port number the same as Sever.java

    public static void main(String[] args) 
	{
        // Shows a summary of all the Usage and command-line instructions
        if (args.length < 1) 
        {
            System.out.println("Usage: java Client <command>\njava Client show - show items in the auction\njava Client item <itemname> - add items to the auction\njava Client bid <itemname> <bidamount> - bid the item by name and bidamount");
            return;
        }

        // Connect to the server by creating a scoket and using the HOSTNAME and PORT_NUMBER so that command can be sent to it
        try (Socket socket = new Socket(HOSTNAME, PORT_NUMBER);
            BufferedReader recieveServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter sendServer = new PrintWriter(socket.getOutputStream(), true)) 
		{

            if (args[0].equalsIgnoreCase("show") && args.length == 1) 
			{
                // Send command to the server
                sendServer.println("show");

                // Wait for and process response from the server
                String serverResponse;
                while ((serverResponse = recieveServer.readLine()) != null) 
				{
                    System.out.println(serverResponse);
                }{}
            } 
			else if (args[0].equalsIgnoreCase("item")) 
			{
                if (args.length == 2)
                {
                    // Send command to the server
                    sendServer.println(args[0] + " " + args[1]);

                    // Wait for and process response from the server
                    String serverResponse = recieveServer.readLine();
                    System.out.println(serverResponse);
                }
                else if (args.length != 2) 
				{
                    System.out.println("Usage: java Client item <itemname>");
                    return;
                }

            } 
			else if (args[0].equalsIgnoreCase("bid")) 
			{
                if (args.length == 3)
                {
                    double newBid = Double.parseDouble(args[2]);

                    // Send command to the server
                    if (newBid > 0)
                    {
                        sendServer.println(args[0] + " " + args[1] + " " + newBid);
                        // Wait for and process response from the server
                        String serverResponse = recieveServer.readLine();
                        System.out.println(serverResponse);
                    }
                    else if (newBid <= 0)
                    {
                        System.out.println("Invalid <bidamount> - only <bidamount> greater than 0 is accepted!");
                        return;
                    }
                }
                else if (args.length != 3) 
				{
                    System.out.println("Usage: java Client bid <itemname> <bidamount>");
                    return;
                }
            } 
			else 
			{
                System.out.println("Invalid command: " + args[0]);
            }

        } 
		catch (IOException exception) 
		{
            System.err.println("Error: " + exception.getMessage());
        }
    }
}