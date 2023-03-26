import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server
{
	/**
	* The port number to be used for communication is 6500.
	* A server listens on a port for incoming connections and and a client connects on a specific port to
	* establish a communication channel.
	*/

    private static final int PORT_NUMBER = 6500; 

	/**
	* A thread pool is a collection of pre-initialized threads that are available to perform a set of tasks.
	* In a multithreaded program, instead of creating a new thread it reuses the existing threads.
	* It determines how many tasks can be executed concurrently.
	*/

    private static final int THREAD_POOL_SIZE = 30; 

	/**
	* Two global HashMaps that have the same key: "object" but different values for each HashMap "price" and 
	* "bidder". <object, price> and <object, bidder>
	*/

    private static final HashMap<String, Double> items = new HashMap<>();			
	private static final HashMap<String, String> highestBidders = new HashMap<>();	

    public static void main(String[] args)
	{
        ServerSocket serverSocket = null;			//to be connected to the port
        ExecutorService executorService = null;		//a thread-pool to be created

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
		catch (Exception exception)
		{
            //exception.printStackTrace();	//provides more detailed information about the location and sequence of method calls that led up to the exception being thrown.
			System.err.println("Error: " + exception.getMessage()); //after the developmental stage
        }
		finally //Cleaning up reosurces
		{
			// if statements checks whether or not these resources were initialized before attempting to shut them down or close them to avoid a "NullPointerException"
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
				catch (Exception exception)
				{
                    //exception.printStackTrace();	//provides more detailed information about the location and sequence of method calls that led up to the exception being thrown.
					System.err.println("Error: " + exception.getMessage()); //after the developmental stage
                }
            }
        }
    }

	/**
	* A nested static class that implements the Runnable interface and defines the task that will be executed by each thread
	* that is created to handle client requests.
	*/
    static class ClientHandler implements Runnable
	{
        private final Socket clientSocket;				//client socket for thread
        private final BufferedReader serverIn;			//BufferredReader used to read input from client
        private final PrintWriter serverOut;			//PrintWriter to send output to client
		private PrintWriter txtWriter;					//PrintWriter to write on a file

		/**
    	* Creates a new instance of the ClientHandler class with the specified client socket, and initializes the
    	* BufferedReader and PrintWriter objects for communication with the client.
    	*
    	* @param clientSocket the client socket associated with the thread
    	* @throws IOException if an I/O error occurs while creating the BufferedReader or PrintWriter objects
    	*/
        public ClientHandler(Socket clientSocket) throws IOException
		{
            this.clientSocket = clientSocket;
            this.serverIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.serverOut = new PrintWriter(clientSocket.getOutputStream(), true);
			this.txtWriter = new PrintWriter(new FileWriter("log.txt", true), true);
        }

		/**
    	* Implementation of the run() method defined in the Runnable interface. This method is executed when the thread is started.
    	*/
        @Override
		public void run()
		{
    		try
			{
        		// Read message from client
        		String request = serverIn.readLine();
				
				//checks null pointer exception, breaks out of the prorgam in such eventuality
        		if (request == null)
				{
            		return;
        		}
				else
				{
					//splits the arguments into parts using a blank-space as a delimeter
            		String[] arguments = request.split(" ");
					
            		if (arguments[0].equalsIgnoreCase("show"))
					{
						writeRequestTxt(request);
						showItems();
            		}
					else if (arguments[0].equalsIgnoreCase("item"))
					{
						writeRequestTxt(request);
                		if (arguments.length < 2)
						{
                    		serverOut.println("Usage: item <itemname>");
                		}
						else
						{
                    		addItem(arguments[1], items);
                		}
            		}
					else if (arguments[0].equalsIgnoreCase("bid"))
					{
						writeRequestTxt(request);
                		if (arguments.length < 3)
						{
                    		serverOut.println("Usage: bid <itemname> <bidamount>");
                		}
						else
						{
                    		placeBid(arguments[1], Double.parseDouble(arguments[2]), clientSocket.getInetAddress().getHostAddress());
                		}
            		}
					else
					{
                		serverOut.println("Invalid command");
            		}
        		}
    		}
			catch (Exception exception)
			{
        		//exception.printStackTrace();	//provides more detailed information about the location and sequence of method calls that led up to the exception being thrown.
				System.err.println("Error: " + exception.getMessage()); //after the developmental stage
    		}
			finally
			{
        		// Clean up resources
        		try
				{
            		clientSocket.close();
        		}
				catch (Exception exception)
				{
            		//exception.printStackTrace();	//provides more detailed information about the location and sequence of method calls that led up to the exception being thrown.
					System.err.println("Error: " + exception.getMessage()); //after the developmental stage
        		}
    		}
		}

		/**
		* Displays a list of items in the auction and their current highest bids and bidders, if any.
		* Example: <object> : 10.0 : 127.0.0.1
		* If there are no items in the auction, a message is displayed indicating that there are no items.
		*/
		private void showItems()
		{
			if (items.isEmpty())
			{
				serverOut.println("There are currently no items in this auction.");
			}
			else
			{
				for (String object : items.keySet())
				{
					double highestBid = items.get(object);
					String bidderIP = "<no bids>";
					if (highestBid > 0)
					{
						bidderIP = highestBidders.get(object);
					}
					serverOut.printf("%s : %.1f : %s%n", object, highestBid, bidderIP);
				}
			}
		}

		/**
		* Adds a new item to the auction with the specified name and initial price of 0.0
		*
		* @param object the name of the item to add to the auction (case-insensitive)
		* @param items the global HashMap that contains all the items in the auction
		*/
		private void addItem(String object, HashMap<String, Double> items)
		{
			//Creates an empty local HashMap that copies all the items in the HashMap "items"
			HashMap<String, Double> copiedMap = new HashMap<>();

			//Converts all the keys from the global Hashmap items toLowerCase and also copies their respective values
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
				serverOut.println("Item already exists!");
				return;
			}
			else //Otherwise treat this item as a new item and append it into "items" HashMap
			{
				items.put(object, 0.0);
				serverOut.println("Success.");
				return;
			}
		}

		/**
		* Places a bid on the specified item, updating its price and highest bidder if the bid is higher than the current highest bid.
		*
		* @param object the name of the item to place a bid on (case-insensitive)
		* @param newBid the amount of the bid
		* @param bidderIP the IP address of the bidder
		*/
		private void placeBid(String object, double newBid, String bidderIP)
		{
			//Create a copied HashMap of the global "items" HashMap
			HashMap<String, Double> copiedMap = new HashMap<>(items);

			//Iterate over the copied items hashmap to find the key that is letter-wise equivalent to "object"
			String equivalentKey = null;
			for (String key : copiedMap.keySet())
			{
				if (key.equalsIgnoreCase(object))
				{
					equivalentKey = key;
					break;
				}
			}

			//Check if equivalent key was found
			if (equivalentKey == null)
			{
				serverOut.println("Item not found.");
				return;
			}

			//Check if the bid is higher than the current highest bid for the item
			double currentBid = copiedMap.get(equivalentKey);
			if (newBid <= currentBid)
			{
				serverOut.println("Rejected.");
				return;
			}

			//Update the "items" and "highestBidders" HashMaps with the new bid and bidder IP address
			items.put(equivalentKey, newBid);
			highestBidders.put(equivalentKey, bidderIP);
			serverOut.println("Accepted.");
			return;
		}

		/**
		* Writes a log message to the request.txt file in the format "dd-MM-yyyy|HH:mm:ss|client IP address|java Client [request]".
		*
		* @param request the request made by the client
		*/
    	private void writeRequestTxt(String request)
		{
			//formatting a "logMessage"
            String logMessage = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy|HH:mm:ss")) + "|" + clientSocket.getInetAddress().getHostAddress() + "|" + request;
            //writes the "logMessage" to the log.txt file
			txtWriter.println(logMessage);
    	}
    }
}
