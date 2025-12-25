package day_20_11;

import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Test2_4 {
    public static void main(String[] args) throws MalformedURLException{
        String s = "https://fpt.edu.vn";
        String thisLine;
        try{
            URL u = new URL(s);
            try{
                DataInputStream dis = new DataInputStream(u.openStream());
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
