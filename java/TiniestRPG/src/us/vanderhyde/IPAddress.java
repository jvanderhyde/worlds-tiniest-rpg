//Find the IP address of the local machine
//Created by James Vanderhyde, 26 December 2014

package us.vanderhyde;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Tries to find an identifying IP address.
 * @author James Vanderhyde
 */
public class IPAddress
{
    /**
     * Tries to find an IP address first by accessing a web page,
     * and then using the Java InetAddress system.
     * @return A string representation of an IP address.
     */
    public static String getHostAddress()
    {
        String address = null;
        try
        {
            address = getHostAddressFromWeb();
        }
        catch (IOException e)
        {
        }
        if (address != null)
            return address;
        address = getHostAddressFromJava();
        return address;
    }
    
    /**
     * Tries to find an IP address by accessing a web page.
     * The web page is vanderhyde.us/ip.php.
     * The web page responds with one line of text: the IP address of the remote host.
     * @return A string representation of an IP address.
     * @throws IOException If there is a problem reading from the web page.
     */
    public static String getHostAddressFromWeb() throws IOException
    {
        String address;
        try
        {
            URL source = new URL("http://vanderhyde.us/ip.php");
            BufferedReader in = new BufferedReader(new InputStreamReader(source.openConnection().getInputStream()));
            address = in.readLine();
        }
        catch (MalformedURLException e)
        {
            address = null;
        }
        return address;
    }
    
    /**
     * 
     * @return A string representation of an IP address.
     */
    public static String getHostAddressFromJava()
    {
        String ip;
        try
        {
            InetAddress addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress();
        }
        catch (UnknownHostException e)
        {
            ip = "unknown";
            String msg = e.getMessage();
            if (msg != null)
            {
                String[] parts = msg.split(":");
                if (parts != null && parts.length>0)
                    ip = parts[0];
            }
        }
        return ip;
    }
    
    
}
