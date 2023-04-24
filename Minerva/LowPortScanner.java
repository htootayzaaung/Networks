import java.net.*;			 
import java.io.*;					

public class LowPortScanner {

  public static void main( String[] args ) 
  {
    String hostname = "localhost";
    if( args.length > 0 ) hostname = args[0];

    for( int i = 1; i < 1024; i++ ) //iterates through all the possible port numbers
    {
    try 
    {
      Socket s = new Socket(hostname,i);
      /*
       * If the connection is successful, it prints a message to the console indicating that there is a server on that port.
      */
      System.out.println("There is a server on port " + i + " of " + hostname); 
    }
    catch( UnknownHostException ex ) 
    {
      System.err.println(ex);
      break;
    }
    catch( IOException ex )
    {

    }
    } 

  }  

}  