import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class Main {

    private static void tcpServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("Server is waiting on connection on port 9090...");

        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket.getInetAddress());

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        Thread readerThread = new Thread(() -> {
            try {
                while (true) {
                    String message = in.readLine();
                    if (message == null || message.startsWith("end")) {
                        System.out.println("Ending TCP server...");
                        break;
                    }
                    System.out.println("Received message from client: " + message);
                }
            } catch (IOException e) {
                System.out.println("Error reading: " + e.getMessage());
            }
        });

        Thread writerThread = new Thread(() -> {
            try {
                BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String input = consoleIn.readLine();
                    if (input.equals("end")) {
                        break;
                    }
                    out.println(input);
                }
            } catch (IOException e) {
                System.out.println("Error writing: " + e.getMessage());
            }
        });

        readerThread.start();
        writerThread.start();

        try {
            readerThread.join();
            writerThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted: " + e.getMessage());
        }

        clientSocket.close();
        serverSocket.close();
    }

    private static void udpServer() throws IOException {
        DatagramSocket serverSocket = new DatagramSocket(9090);
        byte[] buffer = new byte[1024];
        InetAddress clientAddress = null;
        int clientPort = -1;

        // Thread for receiving messages from client
        Thread readerThread = new Thread(() -> {
            try {
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    serverSocket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("Received message from client: " + message);

                    // Store client address and port on first receive
                    if (clientAddress == null) {
                        clientAddress = packet.getAddress();
                        clientPort = packet.getPort();
                    }

                    if (message.startsWith("end")) {
                        System.out.println("Ending UDP server...");
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error receiving: " + e.getMessage());
            }
        });

        // Thread for sending messages to client
        Thread writerThread = new Thread(() -> {
            try {
                BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    // Wait until client is known
                    if (clientAddress[0] == null) {
                        Thread.sleep(100);
                        continue;
                    }
                    String input = consoleIn.readLine();
                    if (input.equals("end")) {
                        byte[] endBuffer = input.getBytes();
                        DatagramPacket endPacket = new DatagramPacket(endBuffer, endBuffer.length, clientAddress[0], clientPort[0]);
                        serverSocket.send(endPacket);
                        break;
                    }
                    byte[] sendBuffer = input.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress[0], clientPort[0]);
                    serverSocket.send(sendPacket);
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Error sending: " + e.getMessage());
            }
        });

        readerThread.start();
        writerThread.start();

        try {
            readerThread.join();
            writerThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted: " + e.getMessage());
        }

        serverSocket.close();
    }

    public static void main(String[] args) {
        try {
//            tcpServer();
            udpServer();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}