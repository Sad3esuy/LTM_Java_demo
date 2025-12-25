package day_4_12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class UDPClientChat {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int portServer = 9999;
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress serverAddr = InetAddress.getByName(serverAddress);

            // Thread for receiving messages
            Thread receiver = new Thread(() -> {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    while (true) {
                        socket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("Received: " + message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiver.start();

            // Main thread for sending messages
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String message;
            while (true) {
                System.out.print("You: ");
                message = in.readLine();
                if (message.equalsIgnoreCase("bye")) {
                    byte[] sendData = message.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr, portServer);
                    socket.send(sendPacket);
                    break;
                }
                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr, portServer);
                socket.send(sendPacket);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
