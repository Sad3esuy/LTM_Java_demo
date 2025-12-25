package ATM_18_12;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ATMClient {
    private static final int RMI_PORT = 1099;
    private static final String SERVICE_NAME = "ATM_Service";
    private static final String HOST = "localhost";

    // THAY ĐỔI: Biến lưu ID của người dùng đã đăng nhập
    private static int loggedInId = -1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        KATMInterface stub = null;

        try {
            Registry registry = LocateRegistry.getRegistry(HOST, RMI_PORT);
            stub = (KATMInterface) registry.lookup(SERVICE_NAME);

            System.out.println("✅ Kết nối Server thành công.");

            while (true) {
                System.out.println("\n--- MENU ---");

                // Hiển thị menu tùy theo trạng thái đăng nhập
                if (loggedInId == -1) {
                    System.out.println("1. Đăng nhập"); // THAY ĐỔI
                    System.out.println("2. Tạo Tài khoản mới");
                    System.out.println("3. Thoát");
                } else {
                    System.out.println("Bạn đang đăng nhập với ID: " + loggedInId);
                    System.out.println("1. Giao dịch (Gửi/Rút)"); // THAY ĐỔI
                    System.out.println("2. Xem Lịch sử giao dịch"); // THAY ĐỔI
                    System.out.println("3. Đăng xuất"); // THAY ĐỔI
                }

                System.out.print("Chọn chức năng: ");
                String choice = scanner.nextLine();

                if (loggedInId == -1) {
                    // Menu khi chưa đăng nhập
                    if (choice.equals("1")) {
                        handleLogin(scanner, stub);
                    } else if (choice.equals("2")) {
                        handleCreateUser(scanner, stub);
                    } else if (choice.equals("3")) {
                        System.out.println("Client thoát chương trình.");
                        break;
                    } else {
                        System.out.println("Lựa chọn không hợp lệ.");
                    }
                } else {
                    // Menu khi đã đăng nhập
                    if (choice.equals("1")) {
                        handleTransaction(scanner, stub);
                    } else if (choice.equals("2")) {
                        handleHistory(scanner, stub);
                    } else if (choice.equals("3")) {
                        loggedInId = -1; // Đăng xuất
                        System.out.println("Đã đăng xuất thành công.");
                    } else {
                        System.out.println("Lựa chọn không hợp lệ.");
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("\nLỗi Client ATM: Không thể kết nối hoặc lỗi gọi hàm từ xa.");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    // --- HÀM XỬ LÝ ĐĂNG NHẬP MỚI ---
    private static void handleLogin(Scanner scanner, KATMInterface stub) throws RemoteException {
        System.out.println("\n--- ĐĂNG NHẬP ---");
        System.out.print("Nhập STK (số nguyên): ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Nhập Mật khẩu: ");
        String pass = scanner.nextLine();

        int result = stub.login(id, pass);

        if (result > 0) {
            loggedInId = result;
            System.out.println(">> Đăng nhập thành công! Chào mừng STK: " + loggedInId);
        } else if (result == -2) {
            System.out.println(">> Đăng nhập thất bại: Sai mật khẩu.");
        } else {
            System.out.println(">> Đăng nhập thất bại: ID không tồn tại.");
        }
    }
    /**
     * Xử lý chức năng Tạo tài khoản
     */
    private static void handleCreateUser(Scanner scanner, KATMInterface stub) throws RemoteException {
        System.out.println("\n--- TẠO TÀI KHOẢN ---");
        System.out.print("Nhập STK(số nguyên): ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Nhập Tên: ");
        String name = scanner.nextLine();
        System.out.print("Nhập CCCD: ");
        String cccd = scanner.nextLine();
        System.out.print("Nhập Mật khẩu: ");
        String pass = scanner.nextLine();
        System.out.print("Nhập Số tiền ban đầu: ");
        double money = Double.parseDouble(scanner.nextLine());

        // Gọi phương thức từ xa
        byte[] response = stub.createUser(id, name, cccd, pass, money);

        // Hiển thị phản hồi từ Server
        System.out.println("Server trả lời: " + new String(response));
    }
    // Hàm Giao dịch (SỬA: Không yêu cầu nhập ID, dùng loggedInId)
    private static void handleTransaction(Scanner scanner, KATMInterface stub) throws RemoteException {
        System.out.println("\n--- GIAO DỊCH (STK: " + loggedInId + ") ---");

        // ID được lấy từ biến đã đăng nhập
        int id = loggedInId;

        System.out.print("Nhập loại giao dịch (GUI/RUT): ");
        String type = scanner.nextLine().toUpperCase();
        System.out.print("Nhập số tiền giao dịch: ");
        double amount = Double.parseDouble(scanner.nextLine());

        byte[] response = stub.transaction(id, amount, type);
        System.out.println("Server trả lời: " + new String(response));
    }

    // Hàm Xem lịch sử (SỬA: Không yêu cầu nhập ID, dùng loggedInId)
    private static void handleHistory(Scanner scanner, KATMInterface stub) throws RemoteException {
        System.out.println("\n--- LỊCH SỬ GIAO DỊCH (STK: " + loggedInId + ") ---");

        // ID được lấy từ biến đã đăng nhập
        int id = loggedInId;

        byte[] response = stub.getHistory(id);
        System.out.println(new String(response));
    }
}