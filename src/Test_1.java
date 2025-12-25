import java.io.File;
import java.io.FileInputStream;
import java.io.IOException; // Chỉ cần thư viện này cho Exception

public class Test_1 {
    public static void main(String[] args) {
        // Lưu ý: Đường dẫn nên dùng "/" hoặc "\\" thay vì "//" dù Java có thể tự hiểu
        String path = "C:/Users/Sadmesuy/Downloads/rang.txt";
        File file = new File(path);

        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] tempByteArray = new byte[2]; // Đệm chứa 2 byte mỗi lần đọc
            int byteCount = -1;

            int nth = 0;
            // Đọc tối đa 2 byte mỗi lần và lưu vào mảng tempByteArray
            while ((byteCount = fis.read(tempByteArray)) != -1) {
                nth++;
                System.out.println("--- Lần đọc thứ: " + nth + " ---");
                System.out.println(">> Số byte đọc được: " + byteCount);

                for (int i = 0; i < byteCount; i++) {
                    // & 0xff để chuyển byte (có dấu) sang số dương (0-255)
                    int code = tempByteArray[i] & 0xff;
                    System.out.println("Byte gốc: " + tempByteArray[i] + "\t-\tMã ASCII: " + code);
                }
            }
            fis.close(); // Đừng quên đóng luồng file!
        } catch (IOException e) { // SỬA Ở ĐÂY: Bắt lỗi IOException
            e.printStackTrace();
        }
    }
}