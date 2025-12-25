package day_4_12;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DatagramEx {
    public static void main(String[] args) {
        String s = "Hello";
        byte[] data = s.getBytes();
        try{
            InetAddress ia = InetAddress.getByName("www.hutech.edu.vn");
            int port = 7;
            DatagramPacket dp;
            dp = new DatagramPacket(data, data.length, ia, port);
            System.out.println("Address: " + dp.getAddress());
            System.out.println("Port: " + dp.getPort());
            System.out.println("Length: " + dp.getLength());
            System.out.println("Data: ");
            s = new String(dp.getData(), dp.getOffset(), dp.getLength());
            System.out.println(s);
        }catch(UnknownHostException e){
            System.err.println(e);
        }
    }
}
