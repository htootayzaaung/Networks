import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressExample {
    public static void main(String[] args) {
        try {
            // Get the IP address of a hostname
            InetAddress address1 = InetAddress.getByName("www.google.com");
            System.out.println("IP-address by hostname: " + address1.getHostAddress() + "\n");

            // Get all IP addresses associated with a hostname
            InetAddress[] addresses = InetAddress.getAllByName("www.yahoo.com");
            for (int i = 0; i < addresses.length; i++) {
                System.out.println("IP-Address " + (i + 1) + ": " + addresses[i].getHostAddress());
            }

            // Get the local IP address of the device in which it is currently running
            InetAddress address3 = InetAddress.getLocalHost();
            System.out.println("\nLocal host IP-Address: " + address3.getHostAddress());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}

/*
 * Local host: When a program connects to the local host it is connecting to the device itself, rather
 * than to another device on the network.
 */

 /*
  * It is common for popular websites like Yahoo to have multiple IP addresses associated with their 
  * domain name. This is often done for load balancing purposes, to ensure that incoming requests are 
  * distributed evenly among multiple servers or data centers.
  * 
  * The program can use any of these IP addresses to establish a connection to Yahoo's servers and 
  * retrieve data or content from the website.
  */
