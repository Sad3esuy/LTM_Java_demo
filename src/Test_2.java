
import java.io.*;

public class Test_2 {
    private static final String FILE_PATH = "C:/Users/Sadmesuy/Downloads/rang.txt";
    public static void main(String[] args) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(FILE_PATH);
            out = new FileOutputStream("C:/Users/Sadmesuy/Downloads/rang_copy.txt");
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}