package day_27_11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket; // 1. Thêm import Socket cho gọn code
import java.net.UnknownHostException;

public class BT3 {
    public static void main(String[] args) {
        // 2. Sửa hostname thành "localhost" để chạy trên cùng máy tính
        // Nếu Server ở máy khác, hãy điền IP của máy đó (ví dụ: "192.168.1.15")
        String hostname = "127.20.10.4";
        int port = 2021;

        System.out.println("Dang ket noi den " + hostname + ":" + port + "...");

        try (Socket socket = new Socket(hostname, port)) {
            // 3. Thông báo đã kết nối thành công
            System.out.println("Da ket noi thanh cong!");

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            // Đọc dữ liệu từ server gửi về
            String time = reader.readLine();
            System.out.println("Server tra ve: " + time);
            //ten may server


        } catch (UnknownHostException ex) {
            System.err.println("Khong tim thay Server: " + ex.getMessage());
        } catch (IOException ex) {
            // 4. In lỗi chi tiết hơn nếu Server chưa bật
            System.err.println("Loi ket noi (Server da bat chua?): " + ex.getMessage());
        }
    }
}