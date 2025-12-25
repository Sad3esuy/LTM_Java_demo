package day_27_11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BT4 {
    public static void main(String[] args) {
        int port = 2021;
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                try (Socket clientSocket = socket;
                     // 1. Khởi tạo BufferedReader để ĐỌC dữ liệu từ Client
                     BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     // 2. Khởi tạo PrintWriter để GHI dữ liệu về Client
                     PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    // Đọc một dòng dữ liệu từ Client gửi lên
                    String clientMessage = reader.readLine();

                    if (clientMessage != null) {
                        System.out.println("=> Received: " + clientMessage);

                        // 3. Xử lý: Chuyển dữ liệu nhận được thành chữ hoa (Uppercase)
                        String upperCaseMessage = clientMessage.toUpperCase();

                        // 4. Gửi kết quả trở lại Client
                        writer.println(upperCaseMessage);
                        System.out.println("<= Sent back (UPPERCASE): " + upperCaseMessage);
                    } else {
                        System.out.println("Client disconnected without sending data.");
                    }

                    // Socket clientSocket sẽ tự động đóng khi ra khỏi khối try
                } catch (IOException e) {
                    System.err.println("Error processing client request: " + e.getMessage());
                }
            }
        } catch (IOException ex) {
            System.err.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}