package pl.tin.server;

import lombok.Getter;
import lombok.SneakyThrows;
import pl.tin.server.events.Request;
import pl.tin.server.events.DrawRequest;
import pl.tin.server.events.UndoRequest;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * A thread which reads data from particular client and requests them
 * to be broadcasted by main server thread
 */
public class ReaderClientThread extends Thread {

    @Getter private int clientId;
    private Socket socket;
    private MainServerThread mainServerThread;
    private DataInputStream inputStream;

    public ReaderClientThread(int clientId, Socket socket, MainServerThread mainServerThread) throws IOException {
        this.clientId = clientId;
        this.socket = socket;
        this.mainServerThread = mainServerThread;
        inputStream = new DataInputStream(socket.getInputStream());
    }

    @Override
    @SneakyThrows(IOException.class)
    public void run() {
        try {
            while (!Thread.interrupted()) {
                var requestCode = inputStream.readInt();

                switch (requestCode) {
                    case Request.DRAW_REQUEST:
                        var drawRequest = readDrawRequest();
                        mainServerThread.enqueueDrawRequest(drawRequest);
                        break;

                    case Request.UNDO_REQUEST:
                        var undoRequest = readUndoRequest();
                        mainServerThread.enqueueUndoRequest(undoRequest);
                        break;
                }
            }
        }
        catch (InterruptedException | EOFException e) {
            //just end the loop
        }
        System.out.println("ReaderThread has ended");
    }

    private DrawRequest readDrawRequest() throws IOException, InterruptedException {
        var scribblePart = readScribblePart();
        return new DrawRequest(scribblePart);
    }

    private UndoRequest readUndoRequest() throws IOException {
        var scribblerId = inputStream.readInt();
        return new UndoRequest(scribblerId);
    }

    private ScribblePart readScribblePart() throws InterruptedException, IOException {
        try {
            List<Pixel> pixels = new ArrayList<>();
            int pixelsCount = inputStream.readInt();

            System.out.println(pixelsCount);

            for (int i = 0; i < pixelsCount; i++) {
                pixels.add(new Pixel(
                    inputStream.readInt(),
                    inputStream.readInt(),
                    inputStream.readInt(),
                    inputStream.readInt(),
                    inputStream.readInt()
                ));
            }
            boolean isEnd = inputStream.readBoolean();
            return new ScribblePart(clientId, pixels, isEnd);

        }
        catch (SocketException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedException();
        }
        catch (EOFException e) {
            System.out.println("Klient o ID = " + clientId +  " rozłączył się");
            Thread.currentThread().interrupt();
            throw new InterruptedException();
        }
    }

    @SneakyThrows(IOException.class)
    public void close() {
        //TODO zastanowić się
        socket.close();
    }
}