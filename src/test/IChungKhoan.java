package test;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChungKhoan extends Remote {
    // Hàm cũ trả về String (giữ lại nếu muốn hiển thị text)
    String layThongTinSan() throws RemoteException;

    // Hàm MỚI: Trả về mảng 3 số thực [Hanoi, HCM, DaNang] để vẽ biểu đồ
    double[] layDuLieuChart() throws RemoteException;
}