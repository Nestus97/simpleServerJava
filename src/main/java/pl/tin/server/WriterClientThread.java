package pl.tin.server;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * A thread which hangs on blocking queue until there is something to send
 * to the client, and then sends it.
 */
public class WriterClientThread extends Thread {

    private BlockingQueue<ScribblePart> scribblePartsToSend = new LinkedBlockingDeque<>();
    @Getter private int clientId;
    private Socket socket;
    private DataOutputStream outputStream;

    public WriterClientThread(int clientId, Socket socket) throws IOException {
        this.clientId = clientId;
        this.socket = socket;
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    private int counter = 0;

    @Override
    @SneakyThrows(IOException.class)
    public void run() {
        try {
            while (!Thread.interrupted()) {
                var scribblePart = scribblePartsToSend.take();

                outputStream.writeInt(scribblePart.getPixels().size());
                outputStream.writeInt(clientId);

                for (var pixel : scribblePart.getPixels()) {
                    outputStream.writeInt(pixel.getX());
                    outputStream.writeInt(pixel.getY());
                    outputStream.writeByte(pixel.getR());
                    outputStream.writeByte(pixel.getG());
                    outputStream.writeByte(pixel.getB());
                }
                outputStream.writeBoolean(scribblePart.isEnd());
//                System.out.println("writer (" + clientId + ") " + counter++);
            }
        }
        catch (InterruptedException e) {
            //just end the loop
            e.printStackTrace();
        }
        catch (SocketException e) {
            e.printStackTrace();
        }

        System.out.println("WriterThread has ended");

    }

    public void enqueueToSend(ScribblePart scribblePart) throws InterruptedException {
        scribblePartsToSend.put(scribblePart);
    }

    @SneakyThrows(IOException.class)
    public void close() {
        socket.close();
    }
}
