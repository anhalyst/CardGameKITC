package kings.server.room;

import kings.client.IClient;
import kings.client.Player;
import org.junit.jupiter.api.Test;

import java.rmi.*;
import java.rmi.registry.Registry;

import static org.junit.jupiter.api.Assertions.*;

public class RoomTest {
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
    Room room1 = new Room();
    Room room2 = new Room();
    //GameRoom room3 = new GameRoom("room3", 4,2 );
    IClient player1;
    IClient p1;
    IClient p2;
    IClient p3;


    /**
     * Test if the Player can enter in a Room.
     * For exmaple player1 enters the Room1. The size of the room before the player enter is 0.
     * Then after the palyer1 enter the room, the size of room1 will return  1
     */
    @Test
    void enterRoom() {
        this.room1.enterRoom(player1);
        assert (room1.getPlayers().size() == 1);
    }

    /**
     * Test if the player can leave the room.
     * For exmaple player1 leaves the Room1. The size of the room will be 0
     */
    @Test
    void leaveRoom() {
        this.room1.leaveRoom(player1);
        assert(room1.getPlayers().size() == 0);
    }

    /**
     * Test to get the Players of a room.
     * For example, we add the 3 players p1, p2 and p3 in the room2.
     * Then we test if the size of room2 is 3.
     */
    @Test
    void getPlayer() {
        this.room2.enterRoom(this.p1);
        this.room2.enterRoom(this.p2);
        this.room2.enterRoom(this.p3);
        assert(room2.getPlayers().size() == 3);

    }

    /**
     * Test to get the name of the room.
     *
     */
    @Test
    void getName() {
        room2.name ="room2";

        assert(this.room2.getName() == "room2");

    }


    /**
     * @throws RemoteException
     * @throws NotBoundException
     *
     * Test to get the names of all players in a room.
     * Example: We add 3 player p1 p2 and p3 in room3, and try to get the names.
     */
    @Test
    void getPlayersName() throws RemoteException, NotBoundException {

        Player p1 = new Player("p1","localhost" );
        Player p2 = new Player("p2","localhost" );
        Player p3 = new Player("p3","localhost" );
        this.room2.enterRoom(p1);
        this.room2.enterRoom(p2);
        this.room2.enterRoom(p3);
        System.out.println(room2.getPlayersName());
        assert(room2.getPlayersName().equals("p1, p2, p3"));
        assert(room2.getPlayers().size() == 3);
    }

    /**
     * Test to get the players in a room.
     * We add 3 players and test if they are in the Output(Array) of getPlayers.
     */
    @Test
    void getPlayers() throws NotBoundException, RemoteException {
        Player p1 = new Player("p1","localhost" );
        Player p2 = new Player("p2","localhost" );
        Player p3 = new Player("p3","localhost" );
        this.room2.enterRoom(p1);
        this.room2.enterRoom(p2);
        this.room2.enterRoom(p3);
        assert(room2.getPlayers().size() == 3);
        assert(room2.getPlayers().contains(p1));
        assert(room2.getPlayers().contains(p2));
        assert(room2.getPlayers().contains(p3));
    }


    /**
     *Print the players in the console
     */
    @Test
    void printPLayers() throws NotBoundException, RemoteException {
        Player p1 = new Player("p1","localhost" );
        Player p2 = new Player("p2","localhost" );
        Player p3 = new Player("p3","localhost" );
        this.room2.enterRoom(p1);
        this.room2.enterRoom(p2);
        this.room2.enterRoom(p3);
        room2.printPLayers();

    }
}