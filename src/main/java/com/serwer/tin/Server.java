package com.serwer.tin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;


public class Server {
    private Vector<ClientHandler> clientHandlers = new Vector<>();
    private int clientNumber = 0;
    private int port = 1234;
    private Vector<ArtPart> history = new Vector<>();

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        initHistory();

        System.out.println("Nasluchiwanie na porcie " + port);

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Zglosil sie nowy klient : " + socket);
            ClientHandler clientHandler = new ClientHandler(socket, ++clientNumber , "client " + clientNumber);
            clientHandlers.add(clientHandler);
            new Thread(clientHandler).start();
        }
    }
    // do wersji podstawowej poczatkowy rysunek
    public void initHistory(){
        ArtPart artPart = new ArtPart(true);
        Pixel p;
        for(int i=0; i<50; i++){
            for(int j=0; j<50; j++){
                p = new Pixel(i+100, j+100, (byte)100, (byte)100, (byte)100);
                artPart.addPixel(p);
            }
        }
        this.history.add(artPart);
        wypiszNowemu();
    }

    public void wypiszNowemu(){
        /*
        //przeslanie ilosci mazniec
        System.out.println("IloscMazniec: " + history.size());
        Vector<Pixel> pixels;
        for(ArtPart art: history){
            pixels = art.getPixels();
            // przeslanie wielkosci mazniecia
            System.out.println("WielkoscMazniecia: " + pixels.size());
            // przeslanie ID osoby ktora maznela
            System.out.println("UserID: " + art.getUserID());
            for(Pixel pixel: pixels){
                // przeslanie informacji o kazdym pixelu
                System.out.println("x: " + pixel.getX() + ", y: " + pixel.getY() + ", r: " + pixel.getR() + ", g: " + pixel.getG() + ", b: " + pixel.getB());
            }
            // przeslanie informacji czy to koniec danego mazniecia
            System.out.println("CzyKoniec: " + art.isEnd());
        }
        // przeslanie ID nowego uzytkownika
        System.out.println("TwojeID: " + ++clientNumber);
        */
    }

    class ClientHandler implements Runnable {
        private String name;
        private final DataInputStream dis;
        private final DataOutputStream dos;
        private Socket socket;
        private int id;
        private boolean listening;

        public ClientHandler(Socket socket, int id, String name) throws IOException {
            this.id = id;
            this.socket = socket;
            this.name = name;
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
            this.listening = true;
            startInfo();
        }

        // funkcja przesylajaca historie mazniec nowemu clientowi
        public void startInfo() throws IOException {
            int lenght = history.size();
            //przeslanie ilosci mazniec
            this.dos.writeInt(lenght);
            Vector<Pixel> pixels;
            for(ArtPart art: history){
                pixels = art.getPixels();
                // przeslanie wielkosci mazniecia
                this.dos.writeInt(pixels.size());
                // przeslanie ID osoby ktora maznela
                this.dos.writeInt(art.getUserID());
                for(Pixel pixel: pixels){
                    // przeslanie informacji o kazdym pixelu
                    this.dos.writeInt(pixel.getX());
                    this.dos.writeInt(pixel.getY());
                    this.dos.writeByte(pixel.getR());
                    this.dos.writeByte(pixel.getG());
                    this.dos.writeByte(pixel.getB());
                }
                // przeslanie informacji czy to koniec danego mazniecia
                this.dos.writeBoolean(art.isEnd());
            }
            // przeslanie ID nowego uzytkownika
            this.dos.writeInt(this.id);
        }

        @Override
        public void run() {
           /* try {
                while (listening) {
                    int messageLength = dis.readInt();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < messageLength; i++) {
                        stringBuilder.append((char)dis.readByte());
                    }
                    String message = stringBuilder.toString();

                    if (message.equals("exit")) {
                        System.out.println(name + " zostal odlaczony od czatu...");

                        for (ReaderClientThread mc : clientHandlers) {
                            if (!(mc.name.equals(name))) {
                                mc.dos.writeUTF(name + " odlaczyl sie od chatu!");
                            }
                        }
                        dos.writeUTF("Zostales odlaczony od chatu!");
                        socket.close();
                        break;
                    }

                    for (ReaderClientThread mc : clientHandlers) {
                        if(mc.name.equals(name)){
                            mc.dos.writeInt(5 + messageLength);
                            mc.dos.writeBytes("You: " + message);
                        }
                        else{
                            mc.dos.writeInt(name.length() + 2 + messageLength);
                            mc.dos.writeBytes(name + ": " + message);
                        }
                    }
                }
            }
            catch (EOFException | SocketException e) {
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
        */
        }
    }
}
