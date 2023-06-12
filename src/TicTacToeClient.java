import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TicTacToeClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;

    private BufferedReader reader;
    private PrintWriter writer;
    private boolean isGameFinished;

    public TicTacToeClient() {
        isGameFinished = false;
    }

    public void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to the server.");

            // Start a separate thread to handle server messages
            new ServerListener().start();

            // Main game loop
            while (!isGameFinished) {
                String input = getInputFromUser();
                writer.println(input);

                if (input.equals("quit")) {
                    break;
                }
            }

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
                    String serverMessage = reader.readLine();

                    if (serverMessage == null) {
                        break;
                    }

                    if (serverMessage.startsWith("MOVE")) {
                        String[] moveParts = serverMessage.split(",");
                        int row = Integer.parseInt(moveParts[1]);
                        int col = Integer.parseInt(moveParts[2]);

                        // Your code to handle the opponent's move

                    } else if (serverMessage.equals("INVALID_MOVE")) {
                        System.out.println("Invalid move! Please try again.");
                    } else if (serverMessage.equals("GAME_OVER")) {
                        // Your code to handle game over scenario
                        isGameFinished = true;
                        break;
                    } else {
                        System.out.println(serverMessage);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        TicTacToeClient client = new TicTacToeClient();
        client.connectToServer();
    }
}
