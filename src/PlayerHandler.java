import java.io.*;
import java.net.*;
import java.util.*;

public class PlayerHandler extends Thread {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Room room;

    public PlayerHandler(Socket socket) {
        this.socket = socket;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRoomCode() throws IOException {
        send("Enter room code (leave blank to join any room):");
        return reader.readLine().toUpperCase();
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void send(String message) {
        writer.println(message);
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                // Process the player's input here
                // You can implement game logic and send updates to other players in the room
                // For simplicity, let's just broadcast the player's input to other players
                // room.broadcast(inputLine);

                // tic tac toe game logic
                int currentPlayer = room.getCurrentPlayer();
                int currentPlayerIndex = currentPlayer % 2;
                int opponentIndex = (currentPlayer + 1) % 2;

                // Send board state to the current player
                room.players.get(currentPlayerIndex).getWriter().println(room.getBoardState());

                // Prompt the current player to make a move
                room.players.get(currentPlayerIndex).getWriter().println("Your move (row,column):");
                String move = room.players.get(currentPlayerIndex).getReader().readLine();

                if (move.equals("quit")) {
                    room.players.get(currentPlayerIndex).getWriter().println("You have quit the game.");
                    room.players.get(opponentIndex).getWriter().println("Your opponent has quit the game.");
                    break;
                }

                if (room.isValidMove(move)) {
                    room.updateBoard(move, currentPlayerIndex);
                    room.players.get(currentPlayerIndex).getWriter().println("MOVE," + move);
                    room.players.get(currentPlayerIndex).getWriter().println("MOVE," + move);

                    if (room.checkWin(currentPlayerIndex)) {
                        room.players.get(currentPlayerIndex).getWriter().println("Congratulations! You win!");
                        room.players.get(opponentIndex).getWriter().println("Game over. You lose.");
                        break;
                    }

                    if (room.isBoardFull()) {
                        room.players.get(currentPlayerIndex).getWriter().println("Game over. It's a draw.");
                        room.players.get(opponentIndex).getWriter().println("Game over. It's a draw.");
                        break;
                    }

                    currentPlayer++;
                } else {
                    room.players.get(currentPlayerIndex).getWriter().println("INVALID_MOVE");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Player disconnected, remove from room and close resources
            if (room != null) {
                room.removePlayer(this);
            }
            close();
        }
    }

    
    public BufferedReader getReader(){
        return reader;
    }
    public PrintWriter getWriter(){
        return writer;
    }
    public static void main(String[] args) {
        
    }
}