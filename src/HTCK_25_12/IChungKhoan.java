package HTCK_25_12;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChungKhoan extends Remote {
    String layThongTinSan() throws RemoteException;
}