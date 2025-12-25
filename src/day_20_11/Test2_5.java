package day_20_11;

import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Test2_5 {
    public static void main(String[] args) throws MalformedURLException{
        String s = "https://zingnews.vn";
        String thisLine;
        try{
            URL u = new URL(s);
            try{
                URLConnection uc = u.openConnection();
                DataInputStream dis = new DataInputStream(uc.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(dis));
                while ((thisLine = br.readLine()) != null){
                    System.out.println(thisLine);
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }catch (MalformedURLException e){
            System.err.println(e);
        }
    }
}
