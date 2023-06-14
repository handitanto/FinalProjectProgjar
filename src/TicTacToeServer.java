import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TicTacToeServer {
    private static final int PORT = 12345;

    private ServerSocket serverSocket;
    private List<Room> rooms;

    public TicTacToeServer() {
        rooms = new ArrayList<>();
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Waiting for players to join...");

            while (true) {
                Socket playerSocket1 = serverSocket.accept();
                Socket playerSocket2 = serverSocket.accept();
                Room room = new Room(playerSocket1, playerSocket2);
                rooms.add(room);
                room.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Room extends Thread {
        private Socket playerSocket1;
        private Socket playerSocket2;
        private PrintWriter writer1;
        private PrintWriter writer2;
        private BufferedReader reader1;
        private BufferedReader reader2;
        private char[] board;
        private int currentPlayer;

        public Room(Socket playerSocket1, Socket playerSocket2) {
            this.playerSocket1 = playerSocket1;
            this.playerSocket2 = playerSocket2;
            currentPlayer = 0;
            board = new char[9];
        }

        public void run() {
            try {
                writer1 = new PrintWriter(playerSocket1.getOutputStream(), true);
                writer2 = new PrintWriter(playerSocket2.getOutputStream(), true);
                reader1 = new BufferedReader(new InputStreamReader(playerSocket1.getInputStream()));
                reader2 = new BufferedReader(new InputStreamReader(playerSocket2.getInputStream()));

                writer1.println("Welcome, Player 1! You are Player 1.");
                writer2.println("Welcome, Player 2! You are Player 2.");

                initializeBoard();
<<<<<<< Updated upstream

                // Main game loop
                while (true) {
                    sendBoardState(0);
                    sendPrompt(0);
                    String move = reader1.readLine();

                    if (move.equals("quit")) {
                        writer1.println("You have quit the game.");
                        writer2.println("Your opponent has quit the game.");
                        break;
                    }

                    if (isValidMove(move)) {
                        updateBoard(move, 0);
                        sendMoveToPlayers(move);

                        if (checkWin(0)) {
                            writer1.println("Congratulations! You win!");
                            writer2.println("Game over. You lose.");
                            break;
                        }

                        if (isBoardFull()) {
                            writer1.println("Game over. It's a draw.");
                            writer2.println("Game over. It's a draw.");
                            break;
                        }

                        sendBoardState(1);
                        sendPrompt(1);
                        move = reader2.readLine();

=======
                int turn = 1;

                // Main game loop
                while (true) {
                    if(turn == 1){
                        sendBoardState(0);
                        sendPrompt(0);
                        String move = reader1.readLine();
    
                        if (move.equals("quit")) {
                            writer1.println("You have quit the game.");
                            writer2.println("Your opponent has quit the game.");
                            break;
                        }
    
                        if (isValidMove(move)) {
                            updateBoard(move, 0);
                            sendMoveToPlayers(move);
    
                            if (checkWin(0)) {
                                writer1.println("Congratulations! You win!");
                                writer2.println("Game over. You lose.");
                                break;
                            }
    
                            if (isBoardFull()) {
                                writer1.println("Game over. It's a draw.");
                                writer2.println("Game over. It's a draw.");
                                break;
                            }
    
                            sendBoardState(1);
                            sendPrompt(1);
                            turn = 2;
                        } else if (move.startsWith("t")) {
                            String chatMessage = move.substring(2);
                            writer1.println("CHAT: Player " + 1 + ": " + chatMessage);
                            writer2.println("CHAT: Player " + 1 + ": " + chatMessage);
                        } else {
                            writer1.println("INVALID_MOVE");
                        }
                    } else {
                        // Start player 2
                        String move = reader2.readLine();
    
>>>>>>> Stashed changes
                        if (move.equals("quit")) {
                            writer2.println("You have quit the game.");
                            writer1.println("Your opponent has quit the game.");
                            break;
                        }
<<<<<<< Updated upstream

                        if (isValidMove(move)) {
                            updateBoard(move, 1);
                            sendMoveToPlayers(move);

=======
    
                        if (isValidMove(move)) {
                            updateBoard(move, 1);
                            sendMoveToPlayers(move);
    
>>>>>>> Stashed changes
                            if (checkWin(1)) {
                                writer2.println("Congratulations! You win!");
                                writer1.println("Game over. You lose.");
                                break;
                            }
<<<<<<< Updated upstream

=======
    
>>>>>>> Stashed changes
                            if (isBoardFull()) {
                                writer2.println("Game over. It's a draw.");
                                writer1.println("Game over. It's a draw.");
                                break;
                            }
<<<<<<< Updated upstream
                        } else {
                            writer2.println("INVALID_MOVE");
                        }
                    } else {
                        writer1.println("INVALID_MOVE");
=======
                            turn = 1;
                        } else if (move.startsWith("t")) {
                            String chatMessage = move.substring(2);
                            writer1.println("CHAT: Player " + 2 + ": " + chatMessage);
                            writer2.println("CHAT: Player " + 2 + ": " + chatMessage);
                        } else {
                            writer2.println("INVALID_MOVE");
                        }
>>>>>>> Stashed changes
                    }
                }

                playerSocket1.close();
                playerSocket2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void initializeBoard() {
            for (int i = 0; i < 9; i++) {
                board[i] = '-';
            }
        }

        private void sendBoardState(int playerIndex) {
            PrintWriter writer = (playerIndex == 0) ? writer1 : writer2;

            StringBuilder boardState = new StringBuilder();
            for (int i = 0; i < 9; i++) {
                boardState.append(board[i]);
                if ((i + 1) % 3 == 0) {
                    boardState.append("\n");
                }
            }

            writer.println(boardState.toString());
        }

        private void sendPrompt(int playerIndex) {
            PrintWriter writer = (playerIndex == 0) ? writer1 : writer2;
            writer.println("Your move (row,column):");
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

        private void sendMoveToPlayers(String move) {
            writer1.println("MOVE," + move);
            writer2.println("MOVE," + move);
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
    }

    public static void main(String[] args) {
        TicTacToeServer server = new TicTacToeServer();
        server.startServer();
    }
}