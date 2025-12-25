package day_27_11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BT5_Server {
    public static void main(String[] args) {
        int port = 2021;

        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                try (Socket clientSocket = socket;
                     BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    // 1. Đọc 3 thành phần riêng biệt từ Client: Toán hạng 1, Toán tử, Toán hạng 2
                    String strOperand1 = reader.readLine();
                    String operator = reader.readLine();
                    String strOperand2 = reader.readLine();

                    double result = Double.NaN; // Giá trị mặc định cho lỗi (Not a Number)
                    String responseMessage = "";

                    if (strOperand1 == null || operator == null || strOperand2 == null) {
                        responseMessage = "ERROR: Thieu du lieu dau vao (Can 3 dong: So1, Toan tu, So2).";
                    } else {
                        try {
                            // Chuyển đổi chuỗi thành số thực (double)
                            double operand1 = Double.parseDouble(strOperand1.trim());
                            double operand2 = Double.parseDouble(strOperand2.trim());

                            // Log dữ liệu nhận được
                            System.out.println("=> Received: " + operand1 + " " + operator.trim() + " " + operand2);

                            // 2. Thực hiện phép tính dựa trên Toán tử
                            switch (operator.trim()) {
                                case "+":
                                    result = operand1 + operand2;
                                    break;
                                case "-":
                                    result = operand1 - operand2;
                                    break;
                                case "*":
                                    result = operand1 * operand2;
                                    break;
                                case "/":
                                    if (operand2 != 0) {
                                        result = operand1 / operand2;
                                    }else if (operand1 == 0) {
                                        responseMessage = "ERROR: Phep toan khong xac dinh.";
                                        result = Double.NaN; // Đặt lỗi
                                    }
                                    else {
                                        responseMessage = "ERROR: Khong the chia cho 0.";
                                        result = Double.NaN; // Đặt lỗi
                                    }
                                    break;
                                default:
                                    responseMessage = "ERROR: Toan tu khong hop le (" + operator.trim() + "). Su dung +, -, *, /.";
                                    result = Double.NaN; // Đặt lỗi
                                    break;
                            }

                            // 3. Chuẩn bị tin nhắn trả về
                            if (!Double.isNaN(result)) {
                                responseMessage = String.valueOf(result); // Thành công, gửi kết quả
                            }

                        } catch (NumberFormatException e) {
                            responseMessage = "ERROR: Dinh dang so khong hop le.";
                        }
                    }

                    // 4. Gửi kết quả (hoặc thông báo lỗi) trở lại Client
                    writer.println(responseMessage);
                    System.out.println("<= Sent back: " + responseMessage);

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