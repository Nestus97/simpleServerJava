package pl.tin.server;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        var mainServerThread = new MainServerThread();
        mainServerThread.start();

        var scanner = new Scanner(System.in);

        while (true) {
            String command = scanner.next();

            switch (command) {
                case "exit":
                    mainServerThread.interrupt();
                    return;
            }
        }
    }
}