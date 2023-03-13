import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client 
{
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 6000; // Replace with your chosen port number

    public static void main(String[] args) 
	{
        // Check command line arguments
        if (args.length < 1) {
            System.out.println("Usage: java Client <command>");
            return;
        }

        // Connect to the server and send command to it
        try (Socket socket = new Socket(HOSTNAME, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) 
			 {

            String command = args[0];

            if (command.equals("show")) 
			{
                // Send command to the server
                out.println("show");

                // Wait for and process response from the server
                String response;
                while ((response = in.readLine()) != null) 
				{
                    System.out.println(response);
                }{
            } 
			else if (command.equals("item")) 
			{
                if (args.length < 2) 
				{
                    System.out.println("Usage: java Client item <itemname>");
                    return;
                }
                String itemName = args[1];

                // Send command to the server
                out.println("item " + itemName);

                // Wait for and process response from the server
                String response = in.readLine();
                System.out.println(response);
            } 
			else if (command.equals("bid")) 
			{
                if (args.length < 3) 
				{
                    System.out.println("Usage: java Client bid <itemname> <bidamount>");
                    return;
                }
                String itemName = args[1];
                double bidAmount = Double.parseDouble(args[2]);

                // Send command to the server
                out.println("bid " + itemName + " " + bidAmount);

                // Wait for and process response from the server
                String response = in.readLine();
                System.out.println(response);
            } 
			else if (command.equals("status")) 
			{
                // Send command to the server
                out.println("status");

                // Wait for and process response from the server
                String response;
                while ((response = in.readLine()) != null) 
				{
                    System.out.println(response);
                }
            } 
			else 
			{
                System.out.println("Invalid command: " + command);
            }

        } 
		catch (IOException e) 
		{
            System.err.println("Error: " + e.getMessage());
        }
    }
}
