package kings.server.administration;

import Exceptions.LoginExeption;
import Exceptions.SpielraumExistiertExeption;
import Exceptions.SpielraumVollExeption;
import kings.client.IClient;
import kings.client.Player;
import kings.server.room.EntranceRoom;
import kings.server.room.GameRoom;
import kings.server.room.Lobby;
import org.junit.jupiter.api.Test;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static org.junit.jupiter.api.Assertions.*;

public class RoomServerTest {
    dbController db;
    private dbController dbController;

    Registry registry1= new Registry() {
        @Override
        public Remote lookup(String name) throws RemoteException, NotBoundException, AccessException {
            return null;
        }

        @Override
        public void bind(String name, Remote obj) throws RemoteException, AlreadyBoundException, AccessException {

        }

        @Override
        public void unbind(String name) throws RemoteException, NotBoundException, AccessException {

        }

        @Override
        public void rebind(String name, Remote obj) throws RemoteException, AccessException {

        }

        @Override
        public String[] list() throws RemoteException, AccessException {
            return new String[0];
        }
    };

    //Registry registry1 = LocateRegistry.createRegistry(1099);

    EntranceRoom entranceRoomroom1 = new EntranceRoom();
    /*
        Player player1 = new Player("player1", registry1);
        Player player2 = new Player("player2", registry1);
        Player player3 = new Player("player3", registry1);
    */
    Lobby lobby = new Lobby();
    // ArrayList<GameRoom> gameRooms;
    RoomServer roomServer = new RoomServer(registry1,entranceRoomroom1, lobby, db);;
    RoomServer roomServer2 = new RoomServer(registry1,entranceRoomroom1, lobby, db);

    RoomServerTest() throws NotBoundException, RemoteException {
    }

    /**
     * @throws RemoteException
     * @throws NotBoundException
     */
    @Test
    void playerEntering() throws RemoteException, NotBoundException {
        //  Registry registry1 = LocateRegistry.createRegistry(1099);
        Player player1 = new Player("player1", "localhost");
        Player player2 = new Player("player2", "localhost");
        Player player3 = new Player("player3", "localhost");

        EntranceRoom room1 = new EntranceRoom();
        Lobby lobby = new Lobby();
        RoomServer roomServer = new RoomServer(registry1, room1, lobby, db);
        roomServer.playerEntering(player1);
        assertTrue(roomServer.entranceRoom.getPlayers().contains(player1));
        roomServer.entranceRoom.leaveRoom(player1);
        assertFalse(roomServer.entranceRoom.getPlayers().contains(player1));


    }

    /**
     * @throws RemoteException
     * @throws NotBoundException
     * @throws SpielraumExistiertExeption
     * @throws SpielraumVollExeption
     */
    @Test
    void leaveGameRoom() throws RemoteException, NotBoundException, SpielraumExistiertExeption, SpielraumVollExeption {
        //  Registry registry1 = LocateRegistry.createRegistry(1099);
        Player player1 = new Player("player1", "localhost");
        Player player2 = new Player("player2", "localhost");
        Player player3 = new Player("player3", "localhost");

        EntranceRoom room1 = new EntranceRoom();
        Lobby lobby = new Lobby();
        RoomServer roomServer = new RoomServer(registry1, room1, lobby, db);
        roomServer.playerEntering(player1);
        roomServer.playerEntering(player2);
        roomServer.lobby.enterRoom(player1);
        roomServer.lobby.enterRoom(player2);
        roomServer.createGameRoom(player1, "g1", 5, 1, true);
        roomServer.enterGameRoom(player2, "g1");

        System.out.println(roomServer.gameRooms.get(0).getPlayers());
        assertTrue(roomServer.gameRooms.get(0).getPlayers().contains(player1));
        roomServer.leaveGameRoom(player1);
        System.out.println(roomServer.gameRooms);




    }

    /**
     * @throws NotBoundException
     * @throws RemoteException
     * @throws SpielraumExistiertExeption
     */
    @Test
    void createGameRoom() throws NotBoundException, RemoteException, SpielraumExistiertExeption {
        // Registry registry1 = LocateRegistry.createRegistry(1099);
        Player player1 = new Player("player1", "localhost");
        Player player2 = new Player("player2", "localhost");
        Player player3 = new Player("player3", "localhost");

        roomServer2.createGameRoom(player3, "room1", 5, 2, false);
        assert(roomServer2.gameRooms.size() == 0);  // player 3 not in Lobby Test
        roomServer2.lobby.enterRoom(player3);
        roomServer2.createGameRoom(player3, "room2", 5, 2, false);
        //  assert(roomServer2.gameRooms.size() == 1);
        roomServer2.createGameRoom(player3, "room2", 5, 2, false);
        //  assert(roomServer2.gameRooms.size() == 1);  // "room2" name already exist test


    }

    /**
     * @throws NotBoundException
     * @throws RemoteException
     * @throws SpielraumVollExeption
     * @throws SpielraumExistiertExeption
     */
    @Test
    void enterGameRoom() throws NotBoundException, RemoteException, SpielraumVollExeption, SpielraumExistiertExeption {
        //  Registry registry1 = LocateRegistry.createRegistry(1099);
        Player player1 = new Player("player1", "localhost");
        Player player2 = new Player("player2", "localhost");
        Player player3 = new Player("player3", "localhost");

        GameRoom groom = new GameRoom(registry1, db ,"room1", 5, 2, true) ;
        Player player4 = new Player("player4", "localhost");
        roomServer.lobby.enterRoom(player4);
        roomServer.lobby.enterRoom(player2);
        roomServer.createGameRoom(player2, "room1", 5, 2, true); // create groom
        roomServer.enterGameRoom(player4, "room1");
        assertFalse(roomServer.lobby.getPlayers().contains(player4)); // player4 and 2 leave the lobby
        assertFalse(roomServer.lobby.getPlayers().contains(player2));
        assertTrue(roomServer.gameRooms.contains(groom)); ;
    }

    /**
     * @throws NotBoundException
     * @throws RemoteException
     * @throws LoginExeption
     */
    @Test
    void login() throws NotBoundException, RemoteException, LoginExeption {

        dbController = new dbController("db");
        //  Registry registry1 = LocateRegistry.createRegistry(1099);
        IClient player = new Player("player", "localhost");
        EntranceRoom room1 = new EntranceRoom();
        Lobby lobby = new Lobby();
        room1.enterRoom(player);
        RoomServer roomServer = new RoomServer(registry1, room1, lobby, dbController);
        roomServer.insertNewPlayer("player","1234");
        roomServer.login(player, "player", "1234");
    }

    /**
     * @throws RemoteException
     * @throws NotBoundException
     */
    @Test
    void insertNewPlayer() throws RemoteException, NotBoundException {
        dbController = new dbController("db");
        //  Registry registry1 = LocateRegistry.createRegistry(1099);
        IClient player = new Player("player", "localhost");
        EntranceRoom room1 = new EntranceRoom();
        Lobby lobby = new Lobby();
        room1.enterRoom(player);
        RoomServer roomServer = new RoomServer(registry1, room1, lobby, dbController);
        roomServer.insertNewPlayer("player","1234");
    }

    /**
     * @throws RemoteException
     * @throws NotBoundException
     */
    @Test
    void deletePlayer() throws RemoteException, NotBoundException {
        dbController = new dbController("db");
        //  Registry registry1 = LocateRegistry.createRegistry(1099);
        IClient player = new Player("player", "localhost");
        EntranceRoom room1 = new EntranceRoom();
        Lobby lobby = new Lobby();
        room1.enterRoom(player);
        RoomServer roomServer = new RoomServer(registry1, room1, lobby, dbController);
        roomServer.insertNewPlayer("player","1234");
        roomServer.deletePlayer(player);
    }


    /**
     * @throws RemoteException
     * @throws NotBoundException
     */
    @Test
    void passwordCheck() throws RemoteException, NotBoundException {
        dbController = new dbController("db");
        //  Registry registry1 = LocateRegistry.createRegistry(1099);
        IClient player = new Player("player", "localhost");
        EntranceRoom room1 = new EntranceRoom();
        Lobby lobby = new Lobby();
        room1.enterRoom(player);
        RoomServer roomServer = new RoomServer(registry1, room1, lobby, dbController);
        roomServer.insertNewPlayer("player","1234");
        roomServer.passwordCheck("player", "1234");
    }


    /**
     * @throws RemoteException
     * @throws NotBoundException
     */
    @Test
    void startGame() throws RemoteException, NotBoundException {
        dbController = new dbController("db");
        Registry registry1 = LocateRegistry.createRegistry(1099);
        IClient player = new Player("player", "localhost");
        EntranceRoom room1 = new EntranceRoom();
        Lobby lobby = new Lobby();
        room1.enterRoom(player);
        RoomServer roomServer = new RoomServer(registry1, room1, lobby, dbController);
        roomServer.insertNewPlayer("player","1234");
        roomServer.startGame(player);
    }


    /**
     * @throws RemoteException
     * @throws NotBoundException
     */
    @Test
    void updateRoomList() throws RemoteException, NotBoundException {
        dbController = new dbController("db");
        //  Registry registry1 = LocateRegistry.createRegistry(1099);
        IClient player = new Player("player", "localhost");
        EntranceRoom room1 = new EntranceRoom();
        Lobby lobby = new Lobby();
        room1.enterRoom(player);
        RoomServer roomServer = new RoomServer(registry1, room1, lobby, dbController);
        roomServer.insertNewPlayer("player","1234");
        roomServer.updateRoomList(player);
    }


    /**
     * @throws RemoteException
     * @throws NotBoundException
     */
    @Test
    void updatePassword() throws RemoteException, NotBoundException {
        dbController = new dbController("db");
        // Registry registry1 = LocateRegistry.createRegistry(1099);
        IClient player = new Player("player", "localhost");
        EntranceRoom room1 = new EntranceRoom();
        Lobby lobby = new Lobby();
        room1.enterRoom(player);
        RoomServer roomServer = new RoomServer(registry1, room1, lobby, dbController);
        roomServer.insertNewPlayer("player","1234");
        roomServer.updatePassword(player, "1234", "123456");
    }

    /**
     *
     */
    @Test
    void topHighScore() {
    }
}
