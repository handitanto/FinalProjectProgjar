import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;

    private BufferedReader reader;
    private PrintWriter writer;
    private boolean isGameFinished;
    private String name;

    public Player() {
        isGameFinished = false;
    }

    public void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to the server.");
            System.out.println("Enter a username:");
            String in = getInputFromUser();
            name = in;

            // Start a separate thread to handle server messages
            new ServerListener().start();

            // Main game loop
            while (!isGameFinished) {
                String input = getInputFromUser();
                writer.println(input);

                if (input.equals("quit")) {
                    isGameFinished = true;
                }
            }

            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getInputFromUser() {
        BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
        String input = "";

        try {
            input = userInputReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return input;
    }

    private class ServerListener extends Thread {
        public void run() {
            try {
                while (true) {
                    String message = reader.readLine();

                    if(message == null){
                        continue;
                    } else if (message.startsWith("MOVE,")) {
                        String move = message.substring(5);
                        System.out.println("Opponent's move: " + move);
                    } else if (message.startsWith("CHAT:")) {
                        System.out.println(message.substring(5));
                    } else {
                        System.out.println(message);
                    }

                    if (message.startsWith("Congratulations") || message.startsWith("Game over")) {
                        isGameFinished = true;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Player client = new Player();
        client.connectToServer();
    }
}
