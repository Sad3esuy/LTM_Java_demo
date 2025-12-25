package ATM_18_12;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ATMServer {

    private static final int RMI_PORT = 1099;
    private static final String SERVICE_NAME = "ATM_Service";

    public static void main(String[] args) {
        try {
            // 1. Tạo RMI Registry (Cơ chế tìm kiếm dịch vụ)
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);

            // 2. Khởi tạo đối tượng xử lý (Remote Object)
            UserTran engine = new UserTran();

            // 3. Đăng ký tên dịch vụ vào Registry
            registry.rebind(SERVICE_NAME, engine);

            System.out.println("✅ ATM Server đang chạy và sẵn sàng nhận kết nối tại port " + RMI_PORT + ".");
            System.out.println("Tên dịch vụ đã đăng ký: " + SERVICE_NAME);

            //

        } catch (Exception e) {
            System.err.println("Lỗi Server ATM: " + e.getMessage());
            e.printStackTrace();
        }
    }
}