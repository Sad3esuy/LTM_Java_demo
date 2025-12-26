package HTCK_25_12;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ServerChungKhoan extends UnicastRemoteObject implements IChungKhoan {

    private double indexHanoi = 100.0;
    private double indexHCM = 500.0;
    private double indexDaNang = 200.0;
    private final Random rand = new Random();

    public ServerChungKhoan() throws Exception {
        super(1098);
        startUpdateThread();
    }

    private void startUpdateThread() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    indexHanoi += (rand.nextDouble() * 10) - 5;
                    indexHCM += (rand.nextDouble() * 10) - 5;
                    indexDaNang += (rand.nextDouble() * 10) - 5;

                    if (indexHanoi < 0) indexHanoi = 0;
                    if (indexHCM < 0) indexHCM = 0;
                    if (indexDaNang < 0) indexDaNang = 0;

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    @Override
    public String layThongTinSan() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String thoiGian = sdf.format(new Date());

        StringBuilder sb = new StringBuilder();
        sb.append("--- CẬP NHẬT LÚC: ").append(thoiGian).append(" ---\n");
        sb.append(String.format("1. Hà Nội (HNX):   %.2f\n", indexHanoi));
        sb.append(String.format("2. Tp.HCM (HOSE):  %.2f\n", indexHCM));
        sb.append(String.format("3. Đà Nẵng (DNX):  %.2f\n", indexDaNang));

        return sb.toString();
    }

    public static void main(String[] args) {
        try {
            // --- CHỌN IP ---
            String serverIP = null;

            if (args.length > 0) {
                serverIP = args[0];
                System.out.println(">>> Sử dụng IP từ tham số: " + serverIP);
            } else {
                java.util.Enumeration<java.net.NetworkInterface> nets = java.net.NetworkInterface.getNetworkInterfaces();
                while (nets.hasMoreElements() && serverIP == null) {
                    java.net.NetworkInterface net = nets.nextElement();

                    // Bỏ qua adapter ảo (Docker/Hyper-V/VMware/VirtualBox)
                    String name = net.getDisplayName().toLowerCase();
                    if (name.contains("docker") || name.contains("hyper-v") || name.contains("vmware") || name.contains("virtual")) {
                        continue;
                    }

                    if (!net.isUp() || net.isLoopback() || net.isVirtual()) continue;
                    java.util.Enumeration<InetAddress> addrs = net.getInetAddresses();
                    while (addrs.hasMoreElements()) {
                        InetAddress addr = addrs.nextElement();
                        if (addr.isLoopbackAddress()) continue;
                        if (addr instanceof java.net.Inet4Address && addr.isSiteLocalAddress()) {
                            serverIP = addr.getHostAddress();
                            break;
                        }
                    }
                }

                if (serverIP == null) {
                    serverIP = InetAddress.getLocalHost().getHostAddress();
                }

                System.out.println(">>> Tự động phát hiện IP LAN: " + serverIP);
            }

            // --- CHỌN PORT ---
            int startPort = 1099;
            if (args.length > 1) {
                try {
                    startPort = Integer.parseInt(args[1]);
                } catch (NumberFormatException ignore) {
                    System.out.println(">>> Tham số port không hợp lệ, dùng mặc định 1099");
                }
            }

            // Ép RMI sử dụng IP này để giao tiếp ra ngoài
            System.setProperty("java.rmi.server.hostname", serverIP);

            Registry reg = null;
            int port = startPort;
            int maxAttempts = 10;

            for (int attempt = 0; attempt < maxAttempts; attempt++) {
                try {
                    reg = LocateRegistry.createRegistry(port);
                    System.out.println(">>> SERVER ĐANG KHỞI ĐỘNG TẠI IP: " + serverIP + " PORT: " + port);
                    break;
                } catch (Exception e) {
                    String msg = e.getMessage();
                    if (msg != null && msg.contains("Port already in use")) {
                        System.out.println(">>> Port " + port + " đã được sử dụng, thử port " + (port + 1));
                        port++;
                    } else {
                        throw e;
                    }
                }
            }

            if (reg == null) {
                throw new Exception("Không thể tìm thấy port trống sau " + maxAttempts + " lần thử");
            }

            ServerChungKhoan server = new ServerChungKhoan();
            reg.rebind("DichVuChungKhoan", server);

            System.out.println(">>> Hệ thống chứng khoán sẵn sàng phục vụ!");
            System.out.println(">>> Client có thể kết nối từ máy khác trong mạng LAN");
            System.out.println(">>> Lưu ý: Nhập đúng IP " + serverIP + " và port " + port + " trên client (mở inbound TCP cho " + port + " và 1098)");

        } catch (Exception e) {
            System.err.println("Lỗi Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}