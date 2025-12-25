package day_20_11;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class Test2_7 {
    public static void main(String[] args){
        try{
            URL u = new URL("https://www.hutech.edu.vn");
            URLConnection uc = u.openConnection();
            uc.connect();
            String key = null;
            for(int n = 1; (key = uc.getHeaderFieldKey(n)) != null; n++) {
                System.out.println(key + " : " + uc.getHeaderField(key));
            }
        }catch (Exception e){
            System.err.println(e);
        }
    }
}
