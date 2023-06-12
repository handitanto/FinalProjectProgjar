import java.io.*;
import java.net.*;
import java.util.*;

public class Room {
    private int id;
    private String code;
    private char[] board = new char[9];
    protected List<PlayerHandler> players;
    private static final int MAX_PLAYERS = 2;
    private int currentPlayer;

    public Room(int id) {
        this.id = id;
        this.code = generateRoomCode();
        players = new ArrayList<>();
        currentPlayer = 0;
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < 9; i++) {
            board[i] = '-';
        }
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public synchronized boolean isFull() {
        return players.size() >= MAX_PLAYERS;
    }

    public synchronized void addPlayer(PlayerHandler player) {
        players.add(player);
    }

    public synchronized void removePlayer(PlayerHandler player) {
        players.remove(player);
    }

    public synchronized void broadcast(String message) {
        for (PlayerHandler player : players) {
            player.send(message);
        }
    }

    public void updateBoard(String move, int playerIndex) {
        int row = Character.getNumericValue(move.charAt(0));
        int col = Character.getNumericValue(move.charAt(2));

        int position = row * 3 + col;
        board[position] = (playerIndex == 0) ? 'X' : 'O';
    }

    private String generateRoomCode() {
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            char randomChar = (char) (random.nextInt(26) + 'A');
            codeBuilder.append(randomChar);
        }
        return codeBuilder.toString();
    }

    public String getBoardState() {
        StringBuilder boardState = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            boardState.append(board[i]);
            if ((i + 1) % 3 == 0) {
                boardState.append("\n");
            }
        }

        return boardState.toString();
    }

    public boolean isValidMove(String move) {
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

    public boolean checkWin(int playerIndex) {
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

    public boolean isBoardFull() {
        for (int i = 0; i < 9; i++) {
            if (board[i] == '-') {
                return false;
            }
        }
        return true;
    }

    public int getCurrentPlayer(){
        return currentPlayer;
    }

    public static void main(String[] args) {
        TicTacToeServer server = new TicTacToeServer();
        server.startServer();
    }

}