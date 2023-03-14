import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Server 
{
    private static final int PORT_NUMBER = 6500; //any port number between 6000 and 6999

	/*
	 * A server listens on a port for incoming connections and and a client connects on a specific port to
	 * establish a communication channel.
	 */

    private static final int THREAD_POOL_SIZE = 30; //number of threads

	/*
	*	A thread pool is a collection of pre-initialized threads that are available to perform a set of tasks. 
	*	In a multithreaded program, instead of creating a new thread it reuses the existing threads. 
	* 	It determines how many tasks can be executed concurrently. 
	*/

	// This is the HashMap for the items
    private static final HashMap<String, Double> items = new HashMap<>();
	private static final HashMap<String, String> highestBidders = new HashMap<>();

    public static void main(String[] args) 
	{
        ServerSocket serverSocket = null;
        ExecutorService executorService = null;

        try 
		{
            // Create a server socket i.e an endpoint that listens for incoming client connections using the TCP protocol.
            serverSocket = new ServerSocket(PORT_NUMBER);
            System.out.println("Server listening to port: " + PORT_NUMBER);

            // Create thread pool with a size specified
            executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            // Listens and accept incoming client connections thus creating a multi-threaded server that can handle multiple client connections
            while (true) 
			{
                Socket clientSocket = serverSocket.accept();				// opens only if a client connection is recieved
                executorService.submit(new ClientHandler(clientSocket));	// creates a new thread to handle each client connection
            }

        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        } 
		finally 
		{
            // Clean up resources
            if (executorService != null) 
			{
                executorService.shutdown();
            }
            if (serverSocket != null) 
			{
                try 
				{
                    serverSocket.close();
                } 
				catch (Exception e) 
				{
                    e.printStackTrace();
                }
            }
        }
    }

    static class ClientHandler implements Runnable 
	{
        private final Socket clientSocket;
        private final BufferedReader in;
        private final PrintWriter out;
		private PrintWriter logger;

        public ClientHandler(Socket clientSocket) throws IOException 
		{
            this.clientSocket = clientSocket;
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
			this.logger = new PrintWriter(new FileWriter("log.txt", true), true);
        }

        @Override
        public void run() 
		{
            try 
			{
                // Read message from client
                String request = in.readLine();
                String[] parts = request.split(" ");
				logRequest(request);

                // Parse message and handle request
                if (parts[0].equals("show")) 
				{
                    showItems();
                } 
				else if (parts[0].equals("item")) 
				{
                    if (parts.length < 2) 
					{
                        out.println("Usage: item <itemname>");
                    } 
					else 
					{
                        addItem(parts[1], items);
                    }
                } 
				else if (parts[0].equals("bid")) 
				{
                    if (parts.length < 3) 
					{
                        out.println("Usage: bid <itemname> <bidamount>");
                    } 
					else 
					{
                        String itemName = parts[1];
                        double bidAmount = Double.parseDouble(parts[2]);
                        placeBid(itemName, bidAmount, clientSocket.getInetAddress().getHostAddress());
                    }
                } 
				else 
				{
                    out.println("Invalid command");
                }

            } 
			catch (Exception e) 
			{
                e.printStackTrace();
            } 
			finally 
			{
                // Clean up resources
                try 
				{
                    clientSocket.close();
                } 
				catch (Exception e) 
				{
                    e.printStackTrace();
                }
            }
        }

		private void showItems() 
		{
			if (items.isEmpty()) 
			{
				out.println("There are currently no items in this auction.");
			} 
			else 
			{
				for (String itemName : items.keySet()) 
				{
					double highestBid = items.get(itemName);
					String bidder = "<no bids>";
					if (highestBid > 0) 
					{
						bidder = highestBidders.get(itemName);
					}
					out.printf("%s : %.1f : %s%n", itemName, highestBid, bidder);
				}
			}
		}

		private void addItem(String itemName, HashMap<String, Double> items) 
		{
			//Creates an empty local HashMap that copies all the items in the HashMap "items"
			HashMap<String, Double> copiedMap = new HashMap<>();

			//Converts all the keys from the global Hashmap items toLowerCase
			for (String key : items.keySet()) 
			{
				copiedMap.put(key.toLowerCase(), items.get(key));
			}

			/*
				Checks if the name of a key already exists in the "items" and "copiedMap" (which are element-wise equivalent)
				by converting "itemName" "toLowerCase".
			*/
			if (copiedMap.containsKey(itemName.toLowerCase())) 
			{
				out.println("Item already exists!");
			} 
			else //Otherwise treat this item as a new item and append it into "items" HashMap
			{
				items.put(itemName, 0.0);
				out.println("Success.");
			}
		}
		

		private void placeBid(String itemName, double bidAmount, String bidder) 
		{
			if (!items.containsKey(itemName)) 
			{
				out.println("Item not found.");
			}
			else 
			{
				double highestBid = items.get(itemName);
				if (bidAmount <= highestBid) 
				{
					out.println("Rejected.");
				} 
				else 
				{
					items.put(itemName, bidAmount);
					highestBidders.put(itemName, bidder);
					out.println("Accepted.");
				}
			}
		}

		private void logRequest(String request) 
		{
			String logMessage = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy|HH:mm:ss")) + "|" + clientSocket.getInetAddress().getHostAddress() + "|" + request;
			logger.println(logMessage);
		}
    }
}