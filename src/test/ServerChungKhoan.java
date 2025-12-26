package test;

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
        super();
        startUpdateThread();
    }

    private void startUpdateThread() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    // Random tăng giảm nhẹ
                    indexHanoi += (rand.nextDouble() * 20) - 10;
                    indexHCM += (rand.nextDouble() * 20) - 10;
                    indexDaNang += (rand.nextDouble() * 20) - 10;

                    // Không để âm
                    if(indexHanoi < 0) indexHanoi = 0;
                    if(indexHCM < 0) indexHCM = 0;
                    if(indexDaNang < 0) indexDaNang = 0;

                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
        });
        t.start();
    }

    @Override
    public String layThongTinSan() {
        // ... (Giữ nguyên code cũ của hàm này) ...
        return "Text data...";
    }

    // --- CẬP NHẬT HÀM MỚI ---
    @Override
    public double[] layDuLieuChart() {
        // Trả về mảng chứa 3 giá trị
        return new double[]{indexHanoi, indexHCM, indexDaNang};
    }

    public static void main(String[] args) {
        try {
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind("DichVuChungKhoan", new ServerChungKhoan());
            System.out.println("SERVER: Đang chạy...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}