package pl.tin.server;

import lombok.SneakyThrows;
import pl.tin.server.events.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
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

    private List<Room> rooms = new ArrayList<>();

    private List<ReaderClientThread> readerThreads = new ArrayList<>();
    private List<WriterClientThread> writerThreads = new ArrayList<>();

    private ConnectionAccepterThread connectionAccepterThread;
    private int lastClientId = 1;
    private int lastRoomId = 0;


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
                    int roomId = drawRequest.getRoomId();

                    for(var room : rooms)
                    {
                        if(room.getRoomId() == roomId)
                            addToHistory(room, scribblePart);
                    }
                    for (var writerThread : writerThreads) {
                        if (writerThread.getClientId() != scribblePart.getScribblerId()
                                && writerThread.getRoomId() == roomId)
                        {
                            writerThread.enqueueDrawRequest(drawRequest);
                        }
                    }
                }
                else if (request instanceof UndoRequest) {
                    var undoRequest = (UndoRequest)request;
                    int roomId = undoRequest.getRoomId();

                    for(var room : rooms)
                    {
                        if(room.getRoomId() == roomId) { performUndo(room, undoRequest.getScribblerId()); }
                    }

                    for (var writerThread : writerThreads) {
                        if(writerThread.getRoomId() == roomId) { writerThread.enqueueUndoRequest(undoRequest); }
                    }
                }
                else if (request instanceof CreateRoomRequest) {
                    var createRoomRequest = (CreateRoomRequest)request;
                    Room room = new Room(++lastRoomId, ((CreateRoomRequest) request).getRoomName());
                    rooms.add(room);

                    for(var writerThread : writerThreads) {
                        if(writerThread.getClientId() == createRoomRequest.getClientId()) {
                            writerThread.setRoomId(lastRoomId);
                            writerThread.newRoomScribble();
                            writerThread.enqueueCreateRoomRequest(createRoomRequest);
                        }
                    }
                    for(var readerThread : readerThreads) {
                        if(readerThread.getClientId() == createRoomRequest.getClientId()) {
                            readerThread.setRoomId(lastRoomId);
                        }
                    }

                }
                else if (request instanceof JoinRoomRequest) {
                    var joinRoomRequest = (JoinRoomRequest)request;

                    for(var writerThread : writerThreads) {
                        if(writerThread.getClientId() == joinRoomRequest.getClientId()) {
                            for(var room : rooms) {
                                if(room.getRoomId() == joinRoomRequest.getRoomId()) {
                                    writerThread.setInitialScribblesHistory(room.getScribblesHistory());
                                    writerThread.setRoomId(joinRoomRequest.getRoomId());
                                    writerThread.enqueueJoinRoomRequest(joinRoomRequest);
                                }
                            }
                        }
                    }

                    for(var readerThread : readerThreads) {
                        if(readerThread.getClientId() == joinRoomRequest.getClientId()) {
                            readerThread.setRoomId(joinRoomRequest.getRoomId());
                        }
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

    private void addToHistory(Room room, ScribblePart scribblePart) {
        Scribble lastScribble = CollectionHelpersKt.findLast(
                room.getScribblesHistory(),
                scribble -> scribble.getScribblerId() == scribblePart.getScribblerId()
        );

        if (lastScribble != null && !lastScribble.isCompleted()) {
            lastScribble.addPixels(scribblePart.getPixels());
            lastScribble.setCompleted(scribblePart.isEnd());
        }
        else room.getScribblesHistory().add(new Scribble(scribblePart));
    }

    private void performUndo(Room room, int scribblerId) {
        Scribble lastScribble = CollectionHelpersKt.findLast(
                room.getScribblesHistory(),
                scribble -> scribble.getScribblerId() == scribblerId
        );
        room.getScribblesHistory().remove(lastScribble);
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
        var writerThread = new WriterClientThread(generatedClientId, clientSocket, rooms);

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

    public void enqueueCreateRoomRequest(CreateRoomRequest createRoomRequest) throws InterruptedException {
        requestsToDistribute.put(createRoomRequest);
    }

    public void enqueueJoinRoomRequest(JoinRoomRequest joinRoomRequest) throws  InterruptedException {
        requestsToDistribute.put(joinRoomRequest);
    }
}