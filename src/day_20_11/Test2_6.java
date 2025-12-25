package day_20_11;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class Test2_6 {
    public static void main(String[] args){
        try{
            URL u = new URL("http://www.somehost.com/cgi-bin/acgi");
            URLConnection uc = u.openConnection();
            uc.setDoOutput(true);
            DataOutputStream dos;
            dos = new DataOutputStream(uc.getOutputStream());
            String s = "Here is some data";
            dos.writeBytes(s);
        }catch (Exception e){
            System.err.println(e);
        }
    }
}
