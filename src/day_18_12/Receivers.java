package day_18_12;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;

public class Receivers {
    public static void main(String[] args) {
        int mcPort = 12345;
        String mcIPStr = "230.1.1.1";
        // THAY ĐỔI: Thường không cần chỉ định, Java sẽ tự động tìm
        // Nhưng nếu cần, hãy điền IP của card mạng cục bộ bạn muốn dùng (ví dụ: "192.168.1.10")
        String localIPStr = "192.168.51.168";

        MulticastSocket mcSocket = null;

        try {
            InetAddress mcIPAddress = InetAddress.getByName(mcIPStr);

            // 1. Khởi tạo MulticastSocket tại cổng (Port)
            mcSocket = new MulticastSocket(mcPort);

            // 2. CHỈ ĐỊNH GIAO DIỆN MẠNG CỤC BỘ (Nếu cần)
            // Nếu localIPStr không rỗng, chúng ta dùng nó để bind Socket
            if (!localIPStr.isEmpty()) {
                InetAddress localAddress = InetAddress.getByName(localIPStr);

                // Set interface cho MulticastSocket để nó biết lắng nghe trên giao diện nào
                // Điều này quan trọng trong môi trường máy chủ có nhiều NIC (card mạng)
                NetworkInterface netIf = NetworkInterface.getByInetAddress(localAddress);
                if (netIf != null) {
                    mcSocket.setNetworkInterface(netIf);
                    System.out.println("Binding to interface: " + netIf.getDisplayName());
                } else {
                    System.out.println("Warning: Cannot find network interface for IP " + localIPStr);
                }
            }

            System.out.println("Multicast Receiver running at: " + mcSocket.getLocalSocketAddress());

            // 3. Tham gia nhóm Multicast
            mcSocket.joinGroup(mcIPAddress);

            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            System.out.println("Waiting for multicast messages...");

            // 4. VÒNG LẶP ĐỂ LIÊN TỤC NHẬN TIN NHẮN (Khắc phục lỗi code cũ chỉ nhận 1 lần)
            while (true) {
                mcSocket.receive(packet);

                // Lấy thông tin người gửi
                String senderAddress = packet.getAddress().getHostAddress();

                // Chuyển byte thành chuỗi
                String msg = new String(packet.getData(), 0, packet.getLength());

                System.out.println("(Multicast Receiver) Received from "
                        + senderAddress + ": " + msg);

                // Reset độ dài gói tin cho lần nhận tiếp theo
                packet.setLength(1024);
            }

        } catch (SocketException e) {
            System.err.println("Lỗi Socket: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Đảm bảo đóng socket và rời nhóm khi kết thúc
            if (mcSocket != null) {
                try {
                    InetAddress mcIPAddress = InetAddress.getByName(mcIPStr);
                    mcSocket.leaveGroup(mcIPAddress);
                    mcSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}