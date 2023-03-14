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
                String[] arguments = request.split(" ");
				logRequest(request);

                // Parse message and handle request
                if (arguments[0].equals("show")) 
				{
                    showItems();
                } 
				else if (arguments[0].equals("item")) 
				{
                    if (arguments.length < 2) 
					{
                        out.println("Usage: item <itemname>");
                    } 
					else 
					{
                        addItem(arguments[1], items);
                    }
                } 
				else if (arguments[0].equals("bid")) 
				{
                    if (arguments.length < 3) 
					{
                        out.println("Usage: bid <itemname> <bidamount>");
                    } 
					else 
					{
                        placeBid(arguments[1], Double.parseDouble(arguments[2]), clientSocket.getInetAddress().getHostAddress());
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
				for (String object : items.keySet()) 
				{
					double highestBid = items.get(object);
					String bidder = "<no bids>";
					if (highestBid > 0) 
					{
						bidder = highestBidders.get(object);
					}
					out.printf("%s : %.1f : %s%n", object, highestBid, bidder);
				}
			}
		}

		private void addItem(String object, HashMap<String, Double> items) 
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
				by converting "object" "toLowerCase".
			*/
			if (copiedMap.containsKey(object.toLowerCase())) 
			{
				out.println("Item already exists!");
			} 
			else //Otherwise treat this item as a new item and append it into "items" HashMap
			{
				items.put(object, 0.0);
				out.println("Success.");
			}
		}
		

		private void placeBid(String object, double bidAmount, String bidder) 
		{
			if (items.containsKey(object) == false) 
			{
				out.println("Item not found.");
			}
			else 
			{
				double highestBid = items.get(object);
				if (bidAmount <= highestBid) 
				{
					out.println("Rejected.");
				} 
				else 
				{
					items.put(object, bidAmount);
					highestBidders.put(object, bidder);
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

