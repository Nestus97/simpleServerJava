package pl.tin.server;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
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
    private List<Scribble> initialScribblesHistory;

    public WriterClientThread(int clientId, List<Scribble> initialScribblesHistory, Socket socket) throws IOException {
        this.clientId = clientId;
        this.initialScribblesHistory = initialScribblesHistory;
        this.socket = socket;
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    @SneakyThrows(IOException.class)
    public void run() {
        try {
            sendStartInfo();

            while (!Thread.interrupted()) {
                var scribblePart = scribblePartsToSend.take();
                sendScribblePart(scribblePart);
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

    private void sendStartInfo() throws IOException {
        outputStream.writeInt(clientId);
        outputStream.writeInt(initialScribblesHistory.size());
        for (Scribble scribble : initialScribblesHistory) {
            sendScribblePart(scribble);
        }
    }

    private void sendScribblePart(ScribblePart scribblePart) throws IOException {
        outputStream.writeInt(scribblePart.getPixels().size());
        outputStream.writeInt(scribblePart.getScribblerId());

        for (var pixel : scribblePart.getPixels()) {
            outputStream.writeInt(pixel.getX());
            outputStream.writeInt(pixel.getY());
            outputStream.writeByte(pixel.getR());
            outputStream.writeByte(pixel.getG());
            outputStream.writeByte(pixel.getB());
        }
        outputStream.writeBoolean(scribblePart.isEnd());
    }

    public void enqueueToSend(ScribblePart scribblePart) throws InterruptedException {
        scribblePartsToSend.put(scribblePart);
    }

    @SneakyThrows(IOException.class)
    public void close() {
        socket.close();
    }
}
