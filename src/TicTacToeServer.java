import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TicTacToeServer {
    private static final int PORT = 12345;

    private ServerSocket serverSocket;
    private Socket[] playerSockets;
    private PrintWriter[] playerWriters;
    private BufferedReader[] playerReaders;
    private char[] board;
    private int currentPlayer;

    public TicTacToeServer() {
        playerSockets = new Socket[2];
        playerWriters = new PrintWriter[2];
        playerReaders = new BufferedReader[2];
        board = new char[9];
        currentPlayer = 0;
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Waiting for players to join...");

            for (int i = 0; i < 2; i++) {
                playerSockets[i] = serverSocket.accept();
                playerWriters[i] = new PrintWriter(playerSockets[i].getOutputStream(), true);
                playerReaders[i] = new BufferedReader(new InputStreamReader(playerSockets[i].getInputStream()));

                playerWriters[i].println("Welcome, Player " + (i + 1) + "! You are Player " + (i + 1) + ".");
            }

            System.out.println("Both players have joined. Starting the game.");

            initializeBoard();

            // Main game loop
            while (true) {
                int currentPlayerIndex = currentPlayer % 2;
                int opponentIndex = (currentPlayer + 1) % 2;

                // Send board state to the current player
                playerWriters[currentPlayerIndex].println(getBoardState());

                // Prompt the current player to make a move
                playerWriters[currentPlayerIndex].println("Your move (row,column):");
                String move = playerReaders[currentPlayerIndex].readLine();

                if (move.equals("quit")) {
                    playerWriters[currentPlayerIndex].println("You have quit the game.");
                    playerWriters[opponentIndex].println("Your opponent has quit the game.");
                    break;
                }

                if (isValidMove(move)) {
                    updateBoard(move, currentPlayerIndex);
                    playerWriters[currentPlayerIndex].println("MOVE," + move);
                    playerWriters[opponentIndex].println("MOVE," + move);

                    if (checkWin(currentPlayerIndex)) {
                        playerWriters[currentPlayerIndex].println("Congratulations! You win!");
                        playerWriters[opponentIndex].println("Game over. You lose.");
                        break;
                    }

                    if (isBoardFull()) {
                        playerWriters[currentPlayerIndex].println("Game over. It's a draw.");
                        playerWriters[opponentIndex].println("Game over. It's a draw.");
                        break;
                    }

                    currentPlayer++;
                } else {
                    playerWriters[currentPlayerIndex].println("INVALID_MOVE");
                }
            }

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeBoard() {
        for (int i = 0; i < 9; i++) {
            board[i] = '-';
        }
    }

    private String getBoardState() {
        StringBuilder boardState = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            boardState.append(board[i]);
            if ((i + 1) % 3 == 0) {
                boardState.append("\n");
            }
        }

        return boardState.toString();
    }

    private boolean isValidMove(String move) {
        if (move.length() != 3) {
            return false;
        }

        int row = Character.getNumericValue(move.charAt(0));
        int col = Character.getNumericValue(move.charAt(2));

        if (row < 0 || row > 2 || col < 0 || col > 2) {
            return false;
        }

        int position = row * 3 + col;
        return board[position] == '-';
    }

    private void updateBoard(String move, int playerIndex) {
        int row = Character.getNumericValue(move.charAt(0));
        int col = Character.getNumericValue(move.charAt(2));

        int position = row * 3 + col;
        board[position] = (playerIndex == 0) ? 'X' : 'O';
    }

    private boolean checkWin(int playerIndex) {
        char symbol = (playerIndex == 0) ? 'X' : 'O';

        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i * 3] == symbol && board[i * 3 + 1] == symbol && board[i * 3 + 2] == symbol) {
                return true;
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (board[i] == symbol && board[i + 3] == symbol && board[i + 6] == symbol) {
                return true;
            }
        }

        // Check diagonals
        if ((board[0] == symbol && board[4] == symbol && board[8] == symbol)
                || (board[2] == symbol && board[4] == symbol && board[6] == symbol)) {
            return true;
        }

        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 9; i++) {
            if (board[i] == '-') {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        TicTacToeServer server = new TicTacToeServer();
        server.startServer();
    }
}
