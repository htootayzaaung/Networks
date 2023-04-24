import java.net.*;

public class Lookup
{
    private InetAddress inet = null;
    
    public void resolve(String host)
    {
        try
        {
            inet = InetAddress.getByName(host);

            System.out.println("Host name: " + inet.getCanonicalHostName());
            System.out.println("IP Address: " + inet.getHostAddress());
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        Lookup lookup = new Lookup();
        lookup.resolve(args[0]);
    }
}

/*
 * Run:
 * java Lookup www.google.com
 * java Lookup www.amazon.com
 */