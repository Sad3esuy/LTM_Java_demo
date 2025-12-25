package day_20_11;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test2_2 {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("Host name: " + ip.getHostName());
        System.out.println("Dia chi IP: " + ip.getHostAddress());

        ip = InetAddress.getByName("www.hutech.edu.vn");
        System.out.println("Host name: " + ip.getHostName());
        System.out.println("Dia chi IP: " + ip.getHostAddress());

        System.out.println("All address of google: ");
        InetAddress sw[] = InetAddress.getAllByName("www.google.com");
        for(int i = 0;i<sw.length;i++){
            System.out.println(sw[i]);
        }
    }
}
