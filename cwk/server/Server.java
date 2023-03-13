import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;
import java.util.HashMap;

public class Server 
{
    private static final int PORT = 6000; // choose a port number between 6000 and 6999
    private static final int THREAD_POOL_SIZE = 30;

	/*
		A thread pool is a collection of pre-initialized threads that are available to perform a set of tasks. 
		In a multithreaded program, creating a new thread is expensive in terms of performance, so using a thread 
		pool can help reduce overhead by reusing existing threads. The thread pool size is the number of threads 
		in the thread pool, and it determines how many tasks can be executed concurrently. If there are more tasks 
		than threads, the excess tasks are queued and executed when a thread becomes available. The optimal thread
		pool size depends on various factors, such as the nature of the tasks, the hardware resources available, 
		and the desired level of concurrency. In general, a larger thread pool size can improve performance up 
		to a certain point, beyond which further increases can result in diminishing returns or even performance 
		degradation due to thread contention and context switching overhead.
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
            // Create server socket
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server listening on port " + PORT);

            // Create thread pool
            executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            // Accept incoming client connections
            while (true) 
			{
                Socket clientSocket = serverSocket.accept();
                executorService.submit(new ClientHandler(clientSocket));
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

        public ClientHandler(Socket clientSocket) throws IOException 
		{
            this.clientSocket = clientSocket;
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        }

        @Override
        public void run() 
		{
            try 
			{
                // Read message from client
                String request = in.readLine();
                String[] parts = request.split(" ");

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
                        addItem(parts[1]);
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

		private void addItem(String itemName) 
		{
			if (items.containsKey(itemName)) 
			{
				out.println("Item already exists.");
			} 
			else 
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
    }
}
