package pl.tin.server;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import pl.tin.server.events.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * A thread which hangs on blocking queue until there is something to send
 * to the client, and then sends it.
 */
public class WriterClientThread extends Thread {

    private BlockingQueue<Request> requestsToDistribute = new LinkedBlockingDeque<>();
    private List<Room> rooms;
    @Getter private int clientId;
    @Getter @Setter private int roomId;
    private Socket socket;
    private DataOutputStream outputStream;
    @Setter private List<Scribble> initialScribblesHistory;

    public WriterClientThread(int clientId, Socket socket, List<Room> rooms) throws IOException {
        this.clientId = clientId;
        this.socket = socket;
        this.roomId = 0;
        this.rooms = rooms;
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    @SneakyThrows(IOException.class)
    public void run() {
        try {
            sendRoomsOptions();

            while (!Thread.interrupted()) {
                var request = requestsToDistribute.take();

                if (request instanceof DrawRequest) {
                    sendDrawRequest((DrawRequest)request);
                }
                else if (request instanceof UndoRequest) {
                    sendUndoRequest((UndoRequest)request);
                }
                else if (request instanceof CreateRoomRequest) {      //STWORZ POKOJ
                    sendCreateRoomRequest((CreateRoomRequest)request);
                }
                else if (request instanceof JoinRoomRequest) {      //DOLACZ DO POKOJU
                    sendJoinRoomRequest((JoinRoomRequest)request);
                }
            }
        }
        catch (InterruptedException | SocketException e) {
            //just end the loop
        }

        System.out.println("WriterThread has ended");
    }

    private void sendRoomsOptions() throws  IOException, InterruptedException {
        outputStream.writeInt(Request.LOGIN_USER);           // REQUEST 0 - LOGIN_USER
        outputStream.writeInt(rooms.size());
        for(Room room : rooms)
        {
            outputStream.writeInt(room.getRoomId());
            outputStream.writeInt(room.getRoomName().length());
            outputStream.writeBytes(room.getRoomName());
        }
    }

    private void sendStartInfo() throws IOException, InterruptedException {
        outputStream.writeInt(Request.JOIN_ROOM_REQUEST);           // REQUEST 4 - JOIN_ROOM (also after create room)
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

    private void sendCreateRoomRequest(CreateRoomRequest createRoomRequest) throws IOException, InterruptedException {
        // TODO tu ma byc juz stworzony pokoj i zmienione id pokoju w writer i reader
        sendStartInfo();
    }

    private void sendJoinRoomRequest(JoinRoomRequest joinRoomRequest) throws IOException, InterruptedException {
        sendStartInfo();
    }

    public void newRoomScribble(){
        initialScribblesHistory = new LinkedList<>();
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

    public void enqueueCreateRoomRequest(CreateRoomRequest createRoomRequest) throws InterruptedException {
        requestsToDistribute.put(createRoomRequest);
    }

    public void enqueueJoinRoomRequest(JoinRoomRequest joinRoomRequest) throws InterruptedException {
        requestsToDistribute.put(joinRoomRequest);
    }

    @SneakyThrows(IOException.class)
    public void close() {
        //TODO zastanowić się
        socket.close();
    }
}
