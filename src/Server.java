import java.io.*;
import java.util.*;
import java.net.*;

public class Server {
    private Vector<ClientHandler> clientHandlers = new Vector<>();
    private int clientNumber = 0;
    private int port = 1234;

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("Nasluchiwanie na porcie " + port);

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Zglosil sie nowy klient : " + socket);
            ClientHandler clientHandler = new ClientHandler(socket, "client " + clientNumber++);
            clientHandlers.add(clientHandler);
            new Thread(clientHandler).start();
        }
    }

    class ClientHandler implements Runnable {
        private String name;
        private final DataInputStream dis;
        private final DataOutputStream dos;
        private Socket socket;

        public ClientHandler(Socket socket, String name) throws IOException {
            this.socket = socket;
            this.name = name;
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                while (true) {
                    int messageLength = dis.readInt();

                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < messageLength; i++) {
                        stringBuilder.append(dis.readChar());
                    }
                    String message = stringBuilder.toString();

                    if (message.equals("exit")) {
                        System.out.println(name + " zostal odlaczony od czatu...");

                        for (ClientHandler mc : clientHandlers) {
                            if (!(mc.name.equals(name))) {
                                mc.dos.writeUTF(name + " odlaczyl sie od chatu!");
                            }
                        }
                        dos.writeUTF("Zostales odlaczony od chatu!");
                        socket.close();
                        break;
                    }

                    for (ClientHandler mc : clientHandlers) {
                        mc.dos.writeUTF(name + ": " + message);
                    }
                }
            }
            catch (EOFException e) {
                System.out.println("Klient sie rozlaczyl: " + name + " !!!");
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            try {
                clientHandlers.remove(this);
                socket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

