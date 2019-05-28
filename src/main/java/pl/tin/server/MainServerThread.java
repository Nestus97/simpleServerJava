package pl.tin.server;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.DataOutputStream;
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

    private BlockingQueue<ScribblePart> scribblePartsToBroadcast = new LinkedBlockingDeque<>();
    private List<ReaderClientThread> readerThreads = new ArrayList<>();
    private List<WriterClientThread> writerThreads = new ArrayList<>();

    private ConnectionAccepterThread connectionAccepterThread;
    private int lastClientId = 1;

    @Getter private List<Scribble> scribblesHistory = new LinkedList<>();
    private Scribble currentScribble = null;

    @Override
    public void run() {
        connectionAccepterThread = new ConnectionAccepterThread(this);
        connectionAccepterThread.start();

        try {
            while (!Thread.interrupted()) {
                var scribblePart = scribblePartsToBroadcast.take();

                addToHistory(scribblePart);

                for (var writerThread : writerThreads) {
                    if (writerThread.getClientId() != scribblePart.getScribblerId())
                        writerThread.enqueueToSend(scribblePart);
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
        if (currentScribble == null) {
            currentScribble = new Scribble(scribblePart);
            scribblesHistory.add(currentScribble);
        } else {
            currentScribble.addPixels(scribblePart.getPixels());
        }
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

    public void enqueueToBroadcast(ScribblePart scribblePart) throws InterruptedException {
        scribblePartsToBroadcast.put(scribblePart);
    }
}