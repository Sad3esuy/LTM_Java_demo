import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BT_4_1 {
    public static void main(String[] args) throws IOException {
        String fileName = "C:/Users/Sadmesuy/Downloads/rang.txt";
        long n = 50000;
        try {
            //ghi voi Stream vùng đệm
            long t = System.currentTimeMillis();
            FileOutputStream fo = new FileOutputStream(fileName);
            BufferedOutputStream bo = new BufferedOutputStream(fo);
            for (int i = 0; i < n; i++) {
                bo.write(i);
            }
            bo.close();
            t = System.currentTimeMillis()-t;
            System.out.println("Thời gian ghi co vung dem " + t + " ms");
            //ghi khong co Stream vùng đệm
            t = System.currentTimeMillis();
            fo = new FileOutputStream(fileName);
            for (int i = 0; i < n; i++) {
                fo.write(i);
            }
            fo.close();
            t = System.currentTimeMillis()-t;
            System.out.println("Thời gian ghi khong co vung dem " + t + " ms");
        }catch (IOException e) {
            System.err.println("Error!");
        }
    }
}

