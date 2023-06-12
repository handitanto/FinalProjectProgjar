import java.io.*;
import java.net.*;
import java.util.*;

public class MultiroomGameServer {
    private static final int PORT = 12345;
    private static final int MAX_ROOMS = 3;

    private ServerSocket serverSocket;
    private List<Room> rooms;

    public MultiroomGameServer() {
        rooms = new ArrayList<>();
        for (int i = 0; i < MAX_ROOMS; i++) {
            rooms.add(new Room(i + 1));
        }
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                PlayerHandler playerHandler = new PlayerHandler(socket);

                // Check if the player wants to join a specific room
                String roomCode = playerHandler.getRoomCode();
                if (roomCode != null) {
                    Room room = findRoomByCode(roomCode);
                    if (room != null && !room.isFull()) {
                        room.addPlayer(playerHandler);
                        playerHandler.setRoom(room);
                        playerHandler.send("Successfully joined room " + room.getId());
                        System.out.println("Player connected and joined room " + room.getId());
                        continue;
                    }
                }

                // If no specific room or invalid code, assign player to an available room
                Room room = findAvailableRoom();
                if (room != null) {
                    room.addPlayer(playerHandler);
                    playerHandler.setRoom(room);
                    playerHandler.send("Successfully joined room " + room.getId());
                    playerHandler.send("Room code " + room.getCode());
                    System.out.println("Player connected and assigned to room " + room.getId());
                } else {
                    playerHandler.send("No available rooms. Please try again later.");
                    playerHandler.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Room findAvailableRoom() {
        for (Room room : rooms) {
            if (!room.isFull()) {
                return room;
            }
        }
        return null;
    }

    private Room findRoomByCode(String code) {
        for (Room room : rooms) {
            if (room.getCode().equals(code)) {
                return room;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        MultiroomGameServer server = new MultiroomGameServer();
        server.start();
    }
}
