package day_27_11;

import javax.imageio.IIOException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class BT1 {
    public static void main(String[] args) throws IIOException {
        String hostname = "time.nist.gov";
        try {
            Socket theSocket = new Socket(hostname, 13);
            InputStream timeStream = theSocket.getInputStream();
            StringBuilder time = new StringBuilder();
            int c;
            while ((c = timeStream.read()) != -1) {
                time.append((char) c);
            }
            String timeString = time.toString().trim();
            System.out.println("It is " + timeString + " at " + hostname);
        } catch (UnknownHostException ex) {
            System.err.println(ex);
        }catch (IOException ex)
        {
            System.err.println(ex);
        }
    }
}
