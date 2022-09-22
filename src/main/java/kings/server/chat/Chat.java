package kings.server.chat;

import java.rmi.RemoteException;
import java.util.*;

import kings.client.IClient;
import kings.server.room.Room;

public class Chat implements IChat {
    Room room;
    String chatRoomName;
    java.util.List<java.util.Map.Entry<String, String>> messages;

    /**
     * sets the name and the room (e.g. lobby, gameRoom1, gameRoom2, ...) this chatroom belongs to
     * the room is needed to access the players list, to broadcast messages to all players in the room
     *
     * ChatServer.createServer(this) creates a entry in the registry to access this new chatroom
     * @param name
     * @param room
     */
    public Chat(String name, Room room) {
        chatRoomName = name;

        // the room, that this chat belongs to
        this.room = room;
        messages = new java.util.ArrayList<>();

        // directly access the class ChatServer
        ChatServer.createServer(this);
    }

    /**
     * gets a message from a client, stores it and broadcasts it to all players in the room
     * @param message
     * @param player
     * @throws RemoteException
     */
    public synchronized void storeMessage(String message, IClient player/* String playerName*/) throws RemoteException {
        // if the player is not in the room => the player can not write messages into the chat
        if (!room.getPlayers().contains(player)) {
            return;
        }
        // store a new message in the map
        //Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>(message, playerName);
        Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>(message, player.getName());
        messages.add(entry);

        broadcast();

    }

    /**
     * iterates over players in the room to send them the new message
     * @throws RemoteException
     */
    private void broadcast() throws RemoteException {
        System.out.println("amount of players in the room: " + room.getPlayers().size());
        Map.Entry<String,String> msg = messages.get(messages.size()-1);
        String name = msg.getValue();
        // iterate over all player in the room to send the new messages
        for (IClient iClient : room.getPlayers()) {
            //it.next().receiveMsg(getNewMessages(messages.size()-1));

            // return the latest message
            if(!iClient.getName().equals(name)){
                iClient.receiveMsg(msg);
                iClient.newMessage();
                System.out.println("server sent message to client");
            }
        }
    }
}
