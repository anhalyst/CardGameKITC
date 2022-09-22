package kings.client;

import Exceptions.SpielraumExistiertExeption;
import Exceptions.SpielraumVollExeption;
import kings.server.administration.IRoomServer;
import kings.server.administration.RoomServer;
import kings.server.administration.dbController;
import kings.server.chat.Chat;
import kings.server.chat.IChat;
import kings.server.room.EntranceRoom;
import kings.server.room.Lobby;
import kings.server.room.Room;
import org.junit.jupiter.api.Test;

import java.rmi.*;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    IClient player1;
    Registry registry = new Registry() {
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
    EntranceRoom eRom = new EntranceRoom();
    Lobby lobby = new Lobby();
    RoomServer roomServer;
    private kings.server.administration.dbController dbController;


    @Test
    void enterEntranceRoom() throws NotBoundException, RemoteException {
        Player player = new Player("player", "localhost" );
        eRom.enterRoom(player1);
        player.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player.enterEntranceRoom();



    }

    @Test
    void receiveMsg() throws NotBoundException, RemoteException {
/*        Player player = new Player("player", registry );
        Map.Entry<String,String> msg ;
        msg.put("player", "first msg");
        player.receiveMsg((Map.Entry<String, String>) msg);
        assert(player.messages.size() == 1);
        assert (player.getNewMessage() == msg);*/
    }

    @Test
    void connectToChatRoom() throws NotBoundException, RemoteException {
        Player player = new Player("player", "localhost");
        player.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player.connectToChatRoom("room");

    }

    @Test
    void getName() throws NotBoundException, RemoteException {
        Player player = new Player("player", "localhost" );
        player.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        assert(player.getName() == "player");
    }


    @Test
    void sendMsg() throws NotBoundException, RemoteException {
        Room room = new Room();
        Player player = new Player("player", "localhost" );

        player.chat = new Chat("chat", room );
        player.sendMsg("My message");
        HashMap<String, String> msg = new HashMap<String, String>();
        msg.put("player", "My message");
        assert(player.messages.size() == 1);
    }

    @Test
    void login() {
    }

    @Test
    void newMessage() throws NotBoundException, RemoteException {
        Player player = new Player("player", "localhost" );
        HashMap<String, String> msg = new HashMap<String, String>();
        msg.put("player", "first msg");
        HashMap<String, String> msg1 = new HashMap<String, String>();
        msg1.put("player", "second msg");

     //   System.out.println(player.messages.get(player.messages.size()-1));
        assert(player.messages.get((player.messages.size() - 1)) == msg1);

    }


    @Test
    void connectToGameManager() throws NotBoundException, RemoteException {
        Player player1 = new Player("player", "localhost" );
        player1.connectToGameManager("gamenanager");
    }

    @Test
    void disconnectFromGameManager() throws RemoteException, NotBoundException {
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.disconnectFromGameManager();
    }

    @Test
    void register() throws RemoteException, NotBoundException {
        kings.server.administration.dbController dbController = new dbController("db");
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.register("1234");
    }

    @Test
    void getNewMessage() throws NotBoundException, RemoteException {
        kings.server.administration.dbController dbController = new dbController("db");
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.getNewMessage();
    }

    @Test
    void newMessageProperty() throws NotBoundException, RemoteException {
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.newMessageProperty();
    }

    @Test
    void setNewMessage() throws NotBoundException, RemoteException {
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.setNewMessage(true);
    }

    @Test
    void storeRoom() throws NotBoundException, RemoteException {
        Map.Entry<String, String> roomInfo = new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return null;
            }

            @Override
            public String getValue() {
                return null;
            }

            @Override
            public String setValue(String value) {
                return null;
            }
        };
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.storeRoom(roomInfo);
    }

    @Test
    void createGameRoom() throws SpielraumExistiertExeption, NotBoundException, RemoteException {
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.createGameRoom(player1,"player",5,2, false);
    }

    @Test
    void updateRoomList() throws RemoteException, NotBoundException {
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.updateRoomList();
    }

    @Test
    void getGameRoomList() throws RemoteException, NotBoundException {
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.getGameRoomList();
    }

    @Test
    void getCurrentRoom() throws RemoteException, NotBoundException {
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.getCurrentRoom();
    }

    @Test
    void setCurrentRoom() throws RemoteException, NotBoundException {
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.getCurrentRoom();
    }

    @Test
    void enterGameRoom() throws SpielraumVollExeption, NotBoundException, RemoteException {
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.enterGameRoom("room1");
    }

    @Test
    void leaveGameRoom() throws NotBoundException, RemoteException {
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.leaveGameRoom();
    }

    @Test
    void getPlayersList() throws RemoteException, NotBoundException {
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.getPlayersList();
    }

    @Test
    void deleteAccount() throws NotBoundException, RemoteException {
        kings.server.administration.dbController dbController = new dbController("db");
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.deleteAccount();
    }

    @Test
    void updatePassword() throws RemoteException, NotBoundException {
        kings.server.administration.dbController dbController = new dbController("db");
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry,eRom,lobby,dbController);
        player1.updatePassword("1234", "123456");
    }

    @Test
    void startedProperty() throws NotBoundException, RemoteException {
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.startedProperty();
    }

    @Test
    void setStarted() throws NotBoundException, RemoteException {
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.setStarted(true);
    }

    @Test
    void startGame() throws RemoteException, NotBoundException {
        Player player1 = new Player("player", "localhost" );
        player1.roomServer = new RoomServer(registry, eRom, lobby, dbController);
        player1.startGame();
    }
}