package ATM_18_12;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTran extends UnicastRemoteObject implements KATMInterface {

    // Class nội bộ để lưu thông tin của từng khách hàng
    class Account {
        String name;
        String CCCD;
        String pass;
        double money;
        double interest;
        List<String> transactionHistory;
        public Account(String name, String CCCD, String pass, double money) {
            this.name = name;
            this.CCCD = CCCD;
            this.pass = pass;
            this.money = money;
            this.interest = 0; // Mặc định lãi suất
            this.transactionHistory = new ArrayList<>(); // Khởi tạo List
            // Ghi lại giao dịch ban đầu (tạo tài khoản)
            this.transactionHistory.add("CREATE: So du ban dau: " + money);
        }
    }

    // "Cơ sở dữ liệu" giả lập lưu trong RAM, Key là ID, Value là Account
    private Map<Integer, Account> database = new HashMap<>();

    // Constructor của Server không cần tham số info user, chỉ khởi tạo kết nối
    public UserTran() throws RemoteException {
        super();
    }

    @Override
    public byte[] createUser(int id, String name, String CCCD, String pass, double money) throws RemoteException {
        try {
            if (database.containsKey(id)) {
                return "Lỗi: ID này đã tồn tại!".getBytes();
            }
            // Tạo tài khoản mới và đưa vào map
            Account newAcc = new Account(name, CCCD, pass, money);
            database.put(id, newAcc);

            String msg = "Tạo tài khoản thành công cho: " + name + ". Số dư: " + money;
            return msg.getBytes();
        } catch (Exception e) {
            return ("Lỗi Server: " + e.getMessage()).getBytes();
        }
    }

    // Bên trong UserTran.java
    @Override
    public byte[] transaction(int id, double moneyTran, String type) throws RemoteException {
        try {
            if (!database.containsKey(id)) {
                return "Lỗi: Tài khoản không tồn tại!".getBytes();
            }

            Account acc = database.get(id);
            String message = "";
            String historyEntry = ""; // Chuỗi để ghi lịch sử

            if (type.equalsIgnoreCase("GUI")) {
                acc.money += moneyTran;
                message = "Gửi tiền thành công. So du moi: " + acc.money;
                historyEntry = "GUI: +" + moneyTran + ". So du: " + acc.money; // Ghi lịch sử
            } else if (type.equalsIgnoreCase("RUT")) {
                if (acc.money >= moneyTran) {
                    acc.money -= moneyTran;
                    message = "Rut tien thanh cong. So du moi: " + acc.money;
                    historyEntry = "RUT: -" + moneyTran + ". So du: " + acc.money; // Ghi lịch sử
                } else {
                    message = "Lỗi: So du khong du!";
                }
            } else {
                message = "Lỗi: Loại giao dịch không hợp lệ (Dùng 'GUI' hoặc 'RUT')";
            }

            // CHỈ THÊM LỊCH SỬ NẾU GIAO DỊCH THÀNH CÔNG
            if (historyEntry.length() > 0) {
                acc.transactionHistory.add(historyEntry);
            }

            return message.getBytes();

        } catch (Exception e) {
            return ("Lỗi giao dịch: " + e.getMessage()).getBytes();
        }
    }
    public byte[] getHistory(int id) throws RemoteException {
        if (!database.containsKey(id)) {
            return "Lỗi: Tài khoản không tồn tại.".getBytes();
        }

        Account acc = database.get(id);

        if (acc.transactionHistory.isEmpty()) {
            return "Không có lịch sử giao dịch.".getBytes();
        }

        // Nối tất cả các giao dịch thành một chuỗi duy nhất, mỗi giao dịch một dòng
        StringBuilder sb = new StringBuilder();
        sb.append("--- LICH SU GIAO DICH (ID: ").append(id).append(") ---\n");

        for (String entry : acc.transactionHistory) {
            sb.append(entry).append("\n");
        }

        return sb.toString().getBytes();
    }
    // THAY ĐỔI: Phương thức Đăng nhập
    @Override
    public int login(int id, String pass) throws RemoteException {
        if (!database.containsKey(id)) {
            // ID không tồn tại
            return -1;
        }

        Account acc = database.get(id);

        if (acc.pass.equals(pass)) {
            // Đăng nhập thành công, trả về ID
            return id;
        } else {
            // Sai mật khẩu
            return -2;
        }
    }
}