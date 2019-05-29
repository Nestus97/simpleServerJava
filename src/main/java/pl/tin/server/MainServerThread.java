package pl.tin.server;

import lombok.Getter;
import lombok.SneakyThrows;
import pl.tin.server.events.Request;
import pl.tin.server.events.DrawRequest;
import pl.tin.server.events.UndoRequest;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * A thread responsible for managing child client-handling threads,
 * i.e. accept new-drawn scribble parts from reader threads and broadcast
 * them to writer threads
 */
public class MainServerThread extends Thread {

    private BlockingQueue<Request> requestsToDistribute = new LinkedBlockingDeque<>();

    private List<ReaderClientThread> readerThreads = new ArrayList<>();
    private List<WriterClientThread> writerThreads = new ArrayList<>();

    private ConnectionAccepterThread connectionAccepterThread;
    private int lastClientId = 1;

    @Getter private List<Scribble> scribblesHistory = new LinkedList<>();

    @Override
    public void run() {
        connectionAccepterThread = new ConnectionAccepterThread(this);
        connectionAccepterThread.start();

        try {
            while (!Thread.interrupted()) {
                var request = requestsToDistribute.take();

                if (request instanceof DrawRequest) {
                    var drawRequest = (DrawRequest)request;
                    var scribblePart = drawRequest.getScribblePart();

                    addToHistory(scribblePart);

                    for (var writerThread : writerThreads) {
                        if (writerThread.getClientId() != scribblePart.getScribblerId())
                            writerThread.enqueueDrawRequest(drawRequest);
                    }
                }
                else if (request instanceof UndoRequest) {
                    var undoRequest = (UndoRequest)request;

                    performUndo(undoRequest.getScribblerId());

                    for (var writerThread : writerThreads) {
                        writerThread.enqueueUndoRequest(undoRequest);
                    }
                }
            }
        }
        catch (InterruptedException e) {
            //just end the loop
        }

        System.out.println("MainServerThread has ended");
        interruptChildThreads();
    }

    private void addToHistory(ScribblePart scribblePart) {
        Scribble lastScribble = CollectionHelpersKt.findLast(
                scribblesHistory,
                scribble -> scribble.getScribblerId() == scribblePart.getScribblerId()
        );

        if (lastScribble != null && !lastScribble.isCompleted()) {
            lastScribble.addPixels(scribblePart.getPixels());
            lastScribble.setCompleted(scribblePart.isEnd());
        }
        else scribblesHistory.add(new Scribble(scribblePart));
    }

    private void performUndo(int scribblerId) {
        Scribble lastScribble = CollectionHelpersKt.findLast(
                scribblesHistory,
                scribble -> scribble.getScribblerId() == scribblerId
        );
        scribblesHistory.remove(lastScribble);
    }

    private void interruptChildThreads() {
        connectionAccepterThread.close();

        for (var readerThread : readerThreads) {
            readerThread.close();
        }

        for (var writerThread : writerThreads) {
            writerThread.close();
        }
    }

    @SneakyThrows(IOException.class)
    public void registerClient(Socket clientSocket) {
        int generatedClientId = lastClientId++;
        var readerThread = new ReaderClientThread(generatedClientId, clientSocket, this);
        var writerThread = new WriterClientThread(generatedClientId, scribblesHistory, clientSocket);

        System.out.println("Połączył się klient ID = " + generatedClientId);

        writerThreads.add(writerThread);
        readerThreads.add(readerThread);

        writerThread.start();
        readerThread.start();
    }

    public void enqueueDrawRequest(DrawRequest drawRequest) throws InterruptedException {
        requestsToDistribute.put(drawRequest);
    }

    public void enqueueUndoRequest(UndoRequest undoRequest) throws InterruptedException {
        requestsToDistribute.put(undoRequest);
    }
}