<<<<<<< HEAD
import java.io.*;
import java.util.*;
import java.net.*;

// klsa Server
public class Server
{

    // Vector do przetrzymywania aktywnych klientow
    static Vector<ClientHandler> ar = new Vector<>();

    // licznik dla klientow
    static int i = 0;

    public static void main(String[] args) throws IOException
    {
        // server nasluchuje na porcie 1234
        ServerSocket ss = new ServerSocket(1234);

        Socket s = null;

        // petla zbierajaca nowych klientow
        while (true)
        {
            // czeka na klienta
            s = ss.accept();

            System.out.println("Zglosil sie nowy klient : " + s);

            // input i output dla kazdego z klientow
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            System.out.println("tworze nowy watek dla tego klienta...");

            // tworzenie nowego obiektu ClientHandler dla nowego klienta
            ClientHandler klient = new ClientHandler(s,"client " + i, dis, dos);

            // tworzenie watku dla nowego klienta
            Thread t = new Thread(klient);

            System.out.println("Dodanie tego klienta do listy aktywnych klientow");

            // dodanie klienta do listy aktywnych klientow
            ar.add(klient);

            // start tego watku.
            t.start();

            // inkrementacja i dla nowego klienta
            // potrzebne do nazwy klienta :)
            i++;
        }
    }
}

// klasa ClientHandler
class ClientHandler implements Runnable
{
    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    // constructor
    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.name = name;
        this.dis = dis;
        this.dos = dos;
        this.isloggedin=true;
    }

    @Override
    public void run() {

        String received;
        while(this.isloggedin)
        {
            try
            {
                // odbior ciagu znakow
                received = dis.readUTF();

                if(received.equals("exit")){
                    System.out.println(this.name+" zostal odlaczony od czatu...");
                    for (ClientHandler mc : Server.ar)
                    {
                        if((!(mc.name.equals(this.name)))&&mc.isloggedin==true)
                        {
                            mc.dos.writeUTF(this.name+" odlaczyl sie od chatu!");
                        }
                    }
                    this.dos.writeUTF("exit");
                    this.isloggedin=false;
                    synchronized (this.s){
                        this.s.close();
                    }
                    break;
                }

                // vector ar przetrzymuje aktywnych klientow
                for (ClientHandler mc : Server.ar)
                {
                    if((!(mc.name.equals(this.name)))&&mc.isloggedin==true)
                    {
                        mc.dos.writeUTF(this.name+" : "+ received);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try
        {

            // zamykanie zasobow
            this.dis.close();
            this.dos.close();
           /* int k=0;
            for (ClientHandler mc : Server.ar)
            {
                if(mc.name.equals(this.name))
                {
                    Server.ar.remove(k);
                }
                k++;
            } */

        }catch(IOException e){
            e.printStackTrace();
        }
    }
=======
import java.io.*;
import java.util.*;
import java.net.*;

// klsa Server
public class Server
{

    // Vector do przetrzymywania aktywnych klientow
    static Vector<ClientHandler> ar = new Vector<>();

    // licznik dla klientow
    static int i = 0;

    public static void main(String[] args) throws IOException
    {
        // server nasluchuje na porcie 1234
        ServerSocket ss = new ServerSocket(1234);

        Socket s = null;

        // petla zbierajaca nowych klientow
        while (true)
        {
            // czeka na klienta
            s = ss.accept();

            System.out.println("Zglosil sie nowy klient : " + s);

            // input i output dla kazdego z klientow
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            System.out.println("tworze nowy watek dla tego klienta...");

            // tworzenie nowego obiektu ClientHandler dla nowego klienta
            ClientHandler klient = new ClientHandler(s,"client " + i, dis, dos);

            // tworzenie watku dla nowego klienta
            Thread t = new Thread(klient);

            System.out.println("Dodanie tego klienta do listy aktywnych klientow");

            // dodanie klienta do listy aktywnych klientow
            ar.add(klient);

            // start tego watku.
            t.start();

            // inkrementacja i dla nowego klienta
            // potrzebne do nazwy klienta :)
            i++;
        }
    }
}

// klasa ClientHandler
class ClientHandler implements Runnable
{
    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    // constructor
    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.name = name;
        this.dis = dis;
        this.dos = dos;
        this.isloggedin=true;
    }

    @Override
    public void run() {

        String received;
        while(this.isloggedin)
        {
            try
            {
                // odbior ciagu znakow
                received = dis.readUTF();

                if(received.equals("exit")){
                    System.out.println(this.name+" zostal odlaczony od czatu...");
                    for (ClientHandler mc : Server.ar)
                    {
                        if((!(mc.name.equals(this.name)))&&mc.isloggedin==true)
                        {
                            mc.dos.writeUTF(this.name+" odlaczyl sie od chatu!");
                        }
                    }
                    this.dos.writeUTF("exit");
                    this.isloggedin=false;
                    synchronized (this.s){
                        this.s.close();
                    }
                    break;
                }

                // vector ar przetrzymuje aktywnych klientow
                for (ClientHandler mc : Server.ar)
                {
                    if((!(mc.name.equals(this.name)))&&mc.isloggedin==true)
                    {
                        mc.dos.writeUTF(this.name+" : "+ received);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try
        {

            // zamykanie zasobow
            this.dis.close();
            this.dos.close();
           /* int k=0;
            for (ClientHandler mc : Server.ar)
            {
                if(mc.name.equals(this.name))
                {
                    Server.ar.remove(k);
                }
                k++;
            } */

        }catch(IOException e){
            e.printStackTrace();
        }
    }
>>>>>>> a9ab4973a92f120f0cf39aeecc013e3405fe054a
}