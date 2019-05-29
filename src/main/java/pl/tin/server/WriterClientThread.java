package pl.tin.server;

import lombok.Getter;
import lombok.SneakyThrows;
import pl.tin.server.events.Request;
import pl.tin.server.events.DrawRequest;
import pl.tin.server.events.UndoRequest;

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

    private BlockingQueue<Request> requestsToDistribute = new LinkedBlockingDeque<>();
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
                var request = requestsToDistribute.take();

                if (request instanceof DrawRequest) {
                    sendDrawRequest((DrawRequest)request);
                }
                else if (request instanceof UndoRequest) {
                    sendUndoRequest((UndoRequest)request);
                }
            }
        }
        catch (InterruptedException | SocketException e) {
            //just end the loop
        }

        System.out.println("WriterThread has ended");
    }

    private void sendStartInfo() throws IOException, InterruptedException {
        outputStream.writeInt(clientId);

        outputStream.writeInt(initialScribblesHistory.size());
        for (Scribble scribble : initialScribblesHistory) {
            sendScribblePart(scribble);
        }
    }

    private void sendDrawRequest(DrawRequest drawRequest) throws IOException, InterruptedException {
        outputStream.writeInt(Request.DRAW_REQUEST);
        sendScribblePart(drawRequest.getScribblePart());
    }

    private void sendUndoRequest(UndoRequest undoRequest) throws IOException {
        outputStream.writeInt(Request.UNDO_REQUEST);
        outputStream.writeInt(undoRequest.getScribblerId());
    }

    private void sendScribblePart(ScribblePart scribblePart) throws IOException, InterruptedException {
        try {
            outputStream.writeInt(scribblePart.getPixels().size());
            outputStream.writeInt(scribblePart.getScribblerId());

            for (var pixel : scribblePart.getPixels()) {
                outputStream.writeInt(pixel.getX());
                outputStream.writeInt(pixel.getY());
                outputStream.writeInt(pixel.getR());
                outputStream.writeInt(pixel.getG());
                outputStream.writeInt(pixel.getB());
            }
            outputStream.writeBoolean(scribblePart.isEnd());
        }
        catch (SocketException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedException();
        }
    }

    public void enqueueDrawRequest(DrawRequest drawRequest) throws InterruptedException {
        requestsToDistribute.put(drawRequest);
    }

    public void enqueueUndoRequest(UndoRequest undoRequest) throws InterruptedException {
        requestsToDistribute.put(undoRequest);
    }

    @SneakyThrows(IOException.class)
    public void close() {
        //TODO zastanowić się
        socket.close();
    }
}
