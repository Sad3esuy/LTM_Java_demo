package day_4_12;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class UDPServerChat {
    public static void main(String[] args) {
        int portServer = 8888;
        List<InetSocketAddress> clients = new ArrayList<>();
        try {
            DatagramSocket socket = new DatagramSocket(portServer);
            System.out.println("Server is running on port " + portServer);
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (true) {
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                InetAddress addr = packet.getAddress();
                int port = packet.getPort();
                InetSocketAddress client = new InetSocketAddress(addr, port);
                if (!clients.contains(client)) {
                    clients.add(client);
                    System.out.println("New client joined: " + addr + ":" + port);
                }
                System.out.println("Received from " + addr + ":" + port + ": " + message);
                if (message.equalsIgnoreCase("bye")) {
                    clients.remove(client);
                    System.out.println("Client left: " + addr + ":" + port);
                }
                // Broadcast the message to all clients
                for (InetSocketAddress c : clients) {
                    DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.length(), c.getAddress(), c.getPort());
                    socket.send(sendPacket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
