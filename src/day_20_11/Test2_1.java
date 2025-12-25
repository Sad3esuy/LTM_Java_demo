package day_20_11;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Test2_1 {
    public static void main(String[] args) {
        try{
            Scanner x = new Scanner(System.in);
            System.out.print("Nhap ten mien can kiem tra: ");
            String str = x.nextLine();

            InetAddress host = InetAddress.getByName(str);
            System.out.println("Host name: " + host.getHostName());
            System.out.println("Dia chi IP: " + host.getHostAddress());

            byte[] b = host.getAddress();
            int i = b[0] >= 0 ? b[0]: 256 + b[0];
            if(i>0 && i<=126){
                System.out.println(host + " thuoc dia chi lop A");
            }
            if(i>=128 && i<=191){
                System.out.println(host + " thuoc dia chi lop B");
            }
            if(i>=192 && i<=223){
                System.out.println(host + " thuoc dia chi lop C");
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
