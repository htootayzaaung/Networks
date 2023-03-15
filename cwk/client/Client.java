import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client 
{
    private static final String HOSTNAME = "localhost";
    private static final int PORT_NUMBER = 6500; // Replace with your chosen port number

    public static void main(String[] args) 
	{
        // Shows a summary of all the Usage and command-line instructions
        if (args.length < 1) 
        {
            System.out.println("Usage: java Client <command>\njava Client show - show items in the auction\njava Client item <itemname> - add items to the auction\njava Client bid <itemname> <bidamount> - bid the item by name and bidamount");
            return;
        }

        // Connect to the server and send command to it
        try (Socket socket = new Socket(HOSTNAME, PORT_NUMBER);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) 
			{

            if (args[0].equalsIgnoreCase("show") && args.length == 1) 
			{
                // Send command to the server
                out.println("show");

                // Wait for and process response from the server
                String response;
                while ((response = in.readLine()) != null) 
				{
                    System.out.println(response);
                }{}
            } 
			else if (args[0].equalsIgnoreCase("item")) 
			{
                if (args.length != 2) 
				{
                    System.out.println("Usage: java Client item <itemname>");
                    return;
                }

                // Send command to the server
                out.println("item " + args[1]);

                // Wait for and process response from the server
                String response = in.readLine();
                System.out.println(response);
            } 
			else if (args[0].equalsIgnoreCase("bid")) 
			{
                if (args.length != 3) 
				{
                    System.out.println("Usage: java Client bid <itemname> <bidamount>");
                    return;
                }
                
                double bidAmount = Double.parseDouble(args[2]);

                // Send command to the server
                if (bidAmount > 0)
                {
                    out.println("bid " + args[1] + " " + bidAmount);
                    // Wait for and process response from the server
                    String response = in.readLine();
                    System.out.println(response);
                }
                else
                {
                    System.out.println("Invalid <bidamount> - only <bidamount> greater than 0 is accepted!");
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