
import java.io.*;

public class Test_3 {
    private static final String FILE_PATH = "C:/Users/Sadmesuy/Downloads/rang.txt";
    public static void main(String[] args) throws IOException {
        DataOutputStream dout = new DataOutputStream(new FileOutputStream(FILE_PATH));
        dout.writeDouble(1.1);
        dout.writeInt(555);
        dout.writeBoolean(true);
        dout.writeChar('4');

        DataInputStream din = new DataInputStream(new FileInputStream(FILE_PATH));
        System.out.println(din.readDouble());
        System.out.println(din.readInt());
        System.out.println(din.readBoolean());
        System.out.println(din.readChar());
    }
}