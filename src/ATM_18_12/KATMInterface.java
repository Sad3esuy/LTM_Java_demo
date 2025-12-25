package ATM_18_12;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface KATMInterface extends Remote {
    // Tạo user mới, trả về thông báo dạng byte[]
    public byte[] createUser(int id, String name, String CCCD, String pass, double money) throws RemoteException;

    // Thực hiện giao dịch (Gửi/Rút), trả về thông báo byte[]
    public byte[] transaction(int id, double moneyTran, String type) throws RemoteException;
    public byte[] getHistory(int id) throws RemoteException;
    public int login(int id, String pass) throws RemoteException;
}