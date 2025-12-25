package day_4_12;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class VD {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String s = "On essaie...";
        byte[] data = s.getBytes("ASCII");

        try{
            InetAddress ia = InetAddress.getByName("it.hutech.edu.vn");
            int port = 7;
            DatagramPacket dp = new DatagramPacket(data,data.length,ia,port);
        }catch(UnknownHostException ex){
        }
    }

}
