package kings.server.room;

import kings.client.IClient;
import kings.server.administration.dbController;
import kings.server.game.StackManager;
import org.junit.jupiter.api.Test;

import java.rmi.*;
import java.rmi.registry.Registry;

import static org.junit.jupiter.api.Assertions.*;

public class GameRoomTest {

    private String name;
    private int capacity;
    private int bots;
    Registry r = new Registry() {
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
    IClient p1;
    dbController db;
    Room room2 = new Room();

    /**
     * Test to get the capacity of a gameroom
     */
    @Test
    void getCapacity() {


        GameRoom gameRoom = new GameRoom(r, db,"room1", 3, 2, false);

        assert(gameRoom.getCapacity() == 3);
    }

    @Test
    void startGame() throws NotBoundException, RemoteException {
        GameRoom gameRoom2 = new GameRoom(r, db ,"room1", 3, 2, false);
        gameRoom2.enterRoom(p1);
        assertFalse(gameRoom2.startGame());

/*        Player p1 = new Player("p1",r );
        Player p2 = new Player("p2",r );
        Player p3 = new Player("p3",r );
        this.room2.enterRoom(p1);
        this.room2.enterRoom(p2);
        this.room2.enterRoom(p3);
        StackManager sm = new StackManager(r, db, name);
        gameRoom2.sm = sm;
        gameRoom2.startGame();*/
    }

    @Test
    void endGame() {
        GameRoom gameRoom2 = new GameRoom(r, db ,"room1", 3, 2, false);
        gameRoom2.endGame();
    }

    /**
     * Test to get the stackmanager of a gameroom
     */
    @Test
    void getStackmanager() {
        GameRoom gameRoom2 = new GameRoom(r, db ,"room1", 3, 2, false);
        StackManager sm = new StackManager(r, db, name);
        gameRoom2.sm = sm;
        assertTrue(gameRoom2.getStackmanager()==sm);
    }
}
