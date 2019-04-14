import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            new Server().start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}