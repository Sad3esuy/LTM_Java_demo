package day_18_12;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

public class Sender {
    public static void main(String[] args) {

        int mcPort = 12345;
        String mcIPStr = "230.0.0.1";

        // Sử dụng try-with-resources để tự động đóng tài nguyên (Scanner và Socket)
        try (Scanner scanner = new Scanner(System.in);
             MulticastSocket socket = new MulticastSocket()) {

            InetAddress group = InetAddress.getByName(mcIPStr);
            boolean check = true;

            System.out.println("Multicast Sender is running. Enter 'q' to quit.");

            // Sửa lỗi: Dùng while(check)
            while (check) {
                System.out.print("Nhap gi: ");
                String msg = scanner.nextLine();

                // Sửa lỗi: So sánh chuỗi dùng .equals()
                if(msg.equalsIgnoreCase("q")){
                    check = false; // Đặt biến thoát vòng lặp
                    System.out.println("Exiting sender...");
                    continue; // Bỏ qua phần gửi tin nhắn
                }

                byte[] buf = msg.getBytes();

                DatagramPacket packet =
                        new DatagramPacket(buf, buf.length, group, mcPort);

                socket.send(packet);
                System.out.println("Multicast message sent!");
            }

            // Socket và Scanner sẽ tự động đóng khi thoát khỏi khối try

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}