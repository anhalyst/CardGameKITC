package kings.server.room;

import kings.server.chat.Chat;

import java.util.concurrent.ConcurrentHashMap;

public class Lobby extends Room {
    ConcurrentHashMap<String, Integer> leaderboard;
    Chat chat;

    /**
     * creates a new chat with the name Lobby for this room
     */
    public Lobby() {
        name = "Lobby";
        chat = new Chat("Lobby", this);
    }

    /*
    public void updateLeaderboard() {
        // @TODO get db entry
        //leaderboard = dbEntry;
    }
     */


}
