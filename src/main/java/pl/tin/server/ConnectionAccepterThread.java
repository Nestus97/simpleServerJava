package pl.tin.server;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

/**
 * A thread responsible for accepting incoming connections and
 * registering client handlers to main server thread
 */
public class ConnectionAccepterThread extends Thread {

    public static final int PORT_NUMBER = 1234;

    private MainServerThread mainServerThread;
    private ServerSocket serverSocket;

    public ConnectionAccepterThread(MainServerThread mainServerThread) {
        this.mainServerThread = mainServerThread;
    }

    @Override
    @SneakyThrows(IOException.class)
    public void run() {
        System.out.println("Nasłuchiwanie na porcie " + PORT_NUMBER);
        serverSocket = new ServerSocket(ConnectionAccepterThread.PORT_NUMBER);
        try {
            while (!Thread.interrupted()) {
                var clientSocket = serverSocket.accept();
                mainServerThread.registerClient(clientSocket);
            }
        }
        catch (SocketException e) {
            //just end the loop
        }
        System.out.println("ConnectionAccepterThread has ended");
    }

    @SneakyThrows(IOException.class)
    public void close() {
        //TODO zastanowić się
        serverSocket.close();
    }
}