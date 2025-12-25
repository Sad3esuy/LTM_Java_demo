package day_4_12;

import java.net.DatagramSocket;

public class UDPPortScanner {
    public static void main(String[] args) {
        for(int port = 0; port <= 65535; port++) {
            try {
                DatagramSocket socket = new DatagramSocket(port);
                socket.close();
            } catch (java.net.SocketException e) {
                System.out.println("Port " + port + " is in use.");
            }
        }
    }
}
