package kings.server.administration;

import kings.server.chat.ChatServer;
import kings.server.chat.IChat;
import kings.server.room.*;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {

    static Registry registry;

    /**
     * creates:
     *      - the registry
     *      - the entrance room
     *      - the lobby
     *      - the RoomServer
     * binds the room server to the registry, so the player can switch rooms
     *
     * @param args
     * @throws RemoteException
     * @throws AlreadyBoundException
     */
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {

        try {
            registry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // database
        String url = "jdbc:sqlite:sqlite/db/KingsCorners.sqlite";

        dbController db = new dbController(url);


        ChatServer.setRegistry(registry);

        EntranceRoom eRoom = new EntranceRoom();
        Lobby lobby = new Lobby();

        RoomServer roomServer = new RoomServer(registry, eRoom, lobby, db);
        IRoomServer stubRoomServer = (IRoomServer) UnicastRemoteObject.exportObject(roomServer, 0);

        // bind name = "Chat" + <the name of the chat room>
        registry.bind("RoomServer", stubRoomServer);



    }
}
