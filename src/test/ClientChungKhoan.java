package test;

import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientChungKhoan extends JFrame {
    private ChartPanel pnlBieuDo; // Panel tự vẽ
    private IChungKhoan dichVuServer;

    public ClientChungKhoan() {
        setTitle("Biểu Đồ Chứng Khoán Trực Tuyến (RMI)");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tạo panel biểu đồ và thêm vào Frame
        pnlBieuDo = new ChartPanel();
        add(pnlBieuDo, BorderLayout.CENTER);

        ketNoiVaChay();
    }

    private void ketNoiVaChay() {
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            dichVuServer = (IChungKhoan) reg.lookup("DichVuChungKhoan");

            // Timer cập nhật mỗi 1 giây
            Timer timer = new Timer(1000, e -> {
                try {
                    // Lấy dữ liệu mảng số từ Server
                    double[] data = dichVuServer.layDuLieuChart();

                    // Cập nhật dữ liệu cho Panel vẽ và yêu cầu vẽ lại
                    pnlBieuDo.updateData(data[0], data[1], data[2]);

                } catch (Exception ex) {
                    setTitle("Mất kết nối Server!");
                }
            });
            timer.start();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage());
        }
    }

    // --- LỚP CON ĐỂ VẼ BIỂU ĐỒ ---
    class ChartPanel extends JPanel {
        private double hn, hcm, dn;
        private final int MAX_VALUE = 800; // Giá trị trần để chia tỉ lệ (truc Y)

        // Hàm nhận dữ liệu mới và vẽ lại
        public void updateData(double hn, double hcm, double dn) {
            this.hn = hn;
            this.hcm = hcm;
            this.dn = dn;
            repaint(); // Gọi hàm này để Swing chạy lại paintComponent
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Chuyển sang Graphics2D để vẽ đẹp hơn
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int barWidth = 80; // Độ rộng cột
            int spacing = (width - (3 * barWidth)) / 4; // Khoảng cách giữa các cột

            // Vẽ nền
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            // Vẽ tiêu đề
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString("BIỂU ĐỒ CHỈ SỐ INDEX THỜI GIAN THỰC", width/2 - 150, 30);

            // --- VẼ CỘT 1: HÀ NỘI ---
            drawBar(g2d, "Hà Nội", hn, Color.RED, spacing, height);

            // --- VẼ CỘT 2: TP.HCM ---
            drawBar(g2d, "TP.HCM", hcm, Color.BLUE, spacing * 2 + barWidth, height);

            // --- VẼ CỘT 3: ĐÀ NẴNG ---
            drawBar(g2d, "Đà Nẵng", dn, Color.GREEN, spacing * 3 + barWidth * 2, height);
        }

        private void drawBar(Graphics2D g2, String name, double value, Color c, int x, int hPanel) {
            int barWidth = 80;
            int bottomMargin = 50; // Chừa lề dưới để viết chữ

            // Tính chiều cao cột dựa trên tỉ lệ (Giả sử Max Index là 800)
            int barHeight = (int) ((value / MAX_VALUE) * (hPanel - 100));

            // Tọa độ Y (Lưu ý: Swing gốc tọa độ (0,0) ở góc trên cùng bên trái)
            int y = hPanel - barHeight - bottomMargin;

            // Vẽ cột
            g2.setColor(c);
            g2.fillRect(x, y, barWidth, barHeight);

            // Vẽ viền cột
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, barWidth, barHeight);

            // Viết giá trị lên đầu cột
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(String.format("%.1f", value), x + 15, y - 5);

            // Viết tên sàn dưới chân cột
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.drawString(name, x + 15, hPanel - 30);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientChungKhoan().setVisible(true));
    }
}