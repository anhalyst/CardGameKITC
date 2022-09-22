package kings.server.game;

import kings.client.IClient;
import kings.client.Player;
import kings.server.administration.dbController;
import kings.server.room.GameRoom;
import org.junit.jupiter.api.Test;

import java.rmi.*;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class StackManagerTest {
    //  Registry r = LocateRegistry.createRegistry(1099);
    String m1;
    IClient player1;
    IClient player2;
    IClient player3;
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
    StackManager stackManager;
    dbController db;



    @Test

    void start() throws NotBoundException, RemoteException {
        db = new dbController("db");
        GameRoom gameRoom = new GameRoom(registry, db ,"room1", 4, 1, false);
        Player player1 = new Player("player1", "localhost");
        Player player2 = new Player("player2", "localhost");
        Player player3 = new Player("player3", "localhost");
        gameRoom.enterRoom(player1);
        gameRoom.enterRoom(player2);



/*        GameRoom gameRoom = new GameRoom(registry, "room1", 3, 2);
        gameRoom.enterRoom(player1);
        gameRoom.enterRoom(player2);
        gameRoom.enterRoom(player3);*/
        assertFalse(gameRoom.startGame());

        gameRoom.enterRoom(player3);
        assertTrue(gameRoom.startGame());
        gameRoom.startGame();


        assert(gameRoom.getPlayers().size()==4);

        Bot bot1 = new Bot(stackManager,"stackmanager",true);
        Bot bot2=new Bot(stackManager,"stackmanager",false);;
        ArrayList<IClient> p = new ArrayList<>();
        ArrayList<IClient> bots = new ArrayList<>();
        p.add(player1);
        p.add(player2);
        p.add(player3);
        bots.add(bot1);
        bots.add(bot2);
        StackManager stackManager = new StackManager(registry, db,"stackmanager");
        stackManager.start(p, bots);


    }

/*    void start() {
        ArrayList<IClient> p = new ArrayList<>();
        ArrayList<IClient> bots = new ArrayList<>();
        p.add(player1);
        p.add(player2);
        p.add(player3);
        bots.add(bot1);
        bots.add(bot2);
        StackManager stackManager = new StackManager(r, "stackmanager");
        stackManager.start(p, bots);
        assert(p.size()==5);



    }*/

    @Test
    void changeStack() throws NotBoundException, RemoteException {

        GameRoom gameRoom = new GameRoom(registry, db,"room1", 4, 1, true);
        Player player1 = new Player("player1", "localhost");
        Player player2 = new Player("player2", "localhost");
        Player player3 = new Player("player3", "localhost");
        gameRoom.enterRoom(player1);
        gameRoom.enterRoom(player2);
        gameRoom.enterRoom(player3);
        //  gameRoom.enterRoom(player2);
        //  gameRoom.enterRoom(player3);
        assertTrue(gameRoom.startGame());
        gameRoom.startGame();

        System.out.println(gameRoom.getPlayers().size());

        StackManager stackManager = new StackManager(registry, db,"stackmanager");
        stackManager.playerHands = gameRoom.getStackmanager().playerHands;

        Card card4 = new Card(13, "diamonds");
        Card card1 = new Card(4, "hearts");
        Card card2 = new Card(7, "clubs");


        //  stackManager.playerHands.get(player1).getKey().addCard(card4);

        //  System.out.println(stackManager.playerHands.get(player1).getKey().getCards());
        Card a = stackManager.playerHands.get(player1).getKey().getCards().get(0) ;
        Card b = stackManager.playerHands.get(player1).getKey().getCards().get(1) ;
        Card c = stackManager.playerHands.get(player1).getKey().getCards().get(2) ;
        Card d = stackManager.playerHands.get(player1).getKey().getCards().get(3) ;
        Card e = stackManager.playerHands.get(player1).getKey().getCards().get(4) ;
        Card f = stackManager.playerHands.get(player1).getKey().getCards().get(5) ;
        Card h = stackManager.playerHands.get(player1).getKey().getCards().get(6) ;
        stackManager.playerHands.get(player1).getKey().removeCard(a);
        stackManager.playerHands.get(player1).getKey().removeCard(b);
        stackManager.playerHands.get(player1).getKey().removeCard(c);
        stackManager.playerHands.get(player1).getKey().removeCard(d);
        stackManager.playerHands.get(player1).getKey().removeCard(e);
        stackManager.playerHands.get(player1).getKey().removeCard(f);
        stackManager.playerHands.get(player1).getKey().removeCard(h);

        stackManager.playerHands.get(player1).getKey().addCard(card4);
        //     System.out.println(stackManager.playerHands.get(player1).getKey().getCards());

        assertFalse(stackManager.changeStack(player1, 1,2));   // Case: contains King 13 (card4) King Violation from card4
        stackManager.playerHands.get(player1).getKey().removeCard(card4);
        stackManager.playerHands.get(player1).getKey().addCard(card2);

        assertFalse(stackManager.changeStack(player1, 1,2));


    }

    @Test
    void addCard() throws NotBoundException, RemoteException {
        GameRoom gameRoom = new GameRoom(registry,db ,"room1", 4, 1, true);
        Player player1 = new Player("player1", "localhost");
        Player player2 = new Player("player2", "localhost");
        Player player3 = new Player("player3", "localhost");
        gameRoom.enterRoom(player1);
        gameRoom.enterRoom(player2);
        gameRoom.enterRoom(player3);

        // GameRoom gameRoom = new GameRoom(registry, "room1", 1, 0);
        // gameRoom.enterRoom(player1);


        assertTrue(gameRoom.startGame());
        gameRoom.startGame();

        StackManager stackManager = new StackManager(registry, db,"stackmanager");
        stackManager.playerHands = gameRoom.getStackmanager().playerHands;

        Card card4 = new Card(18, "diamonds");
        Card card1 = new Card(4, "hearts");
        Card card2 = new Card(7, "clubs");

        stackManager.playerHands.get(player1).getKey().addCard(card1);
        assertTrue(stackManager.playerHands.get(player1).getKey().getCards().contains(card1));
        stackManager.playerHands.get(player1).getKey().addCard(card4);
        assertTrue(stackManager.playerHands.get(player1).getKey().getCards().contains(card4));
        stackManager.playerHands.get(player1).getKey().removeCard(card4);
        assertFalse(stackManager.playerHands.get(player1).getKey().getCards().contains(card4));

    }

    @Test
    void endTurn() throws RemoteException, NotBoundException {

        GameRoom gameRoom = new GameRoom(registry, db,"room1", 4, 1, true);
        Player player1 = new Player("player1", "localhost");
        Player player2 = new Player("player2", "localhost");
        Player player3 = new Player("player3", "localhost");
        gameRoom.enterRoom(player1);
        gameRoom.enterRoom(player2);
        gameRoom.enterRoom(player3);

      /*  Player player1 = new Player("player", registry );
        GameRoom gameRoom = new GameRoom(registry, "room1", 1, 0);
        gameRoom.enterRoom(player1);*/

        assertTrue(gameRoom.startGame());
        gameRoom.startGame();

        StackManager stackManager = new StackManager(registry, db,"stackmanager");
        stackManager.playerHands = gameRoom.getStackmanager().playerHands;

        stackManager.endTurn(player1);
    }

    @Test
    void getCards() throws NotBoundException, RemoteException {

        GameRoom gameRoom = new GameRoom(registry, db,"room1", 4, 1, true);
        Player player1 = new Player("player1", "localhost");
        Player player2 = new Player("player2", "localhost");
        Player player3 = new Player("player3", "localhost");
        gameRoom.enterRoom(player1);
        gameRoom.enterRoom(player2);
        gameRoom.enterRoom(player3);

/*        GameRoom gameRoom = new GameRoom(registry, "room1", 1, 0);
        gameRoom.enterRoom(player1);*/
        assertTrue(gameRoom.startGame());
        gameRoom.startGame();

        StackManager stackManager = new StackManager(registry, db,"stackmanager");
        stackManager.playerHands = gameRoom.getStackmanager().playerHands;

        Card card4 = new Card(18, "diamonds");
        Card card1 = new Card(4, "hearts");
        Card card2 = new Card(7, "clubs");

        Card a = stackManager.playerHands.get(player1).getKey().getCards().get(0) ;
        Card b = stackManager.playerHands.get(player1).getKey().getCards().get(1) ;
        Card c = stackManager.playerHands.get(player1).getKey().getCards().get(2) ;
        Card d = stackManager.playerHands.get(player1).getKey().getCards().get(3) ;
        Card e = stackManager.playerHands.get(player1).getKey().getCards().get(4) ;
        Card f = stackManager.playerHands.get(player1).getKey().getCards().get(5) ;
        Card h = stackManager.playerHands.get(player1).getKey().getCards().get(6) ;
        stackManager.playerHands.get(player1).getKey().removeCard(a);
        stackManager.playerHands.get(player1).getKey().removeCard(b);
        stackManager.playerHands.get(player1).getKey().removeCard(c);
        stackManager.playerHands.get(player1).getKey().removeCard(d);
        stackManager.playerHands.get(player1).getKey().removeCard(e);
        stackManager.playerHands.get(player1).getKey().removeCard(f);
        stackManager.playerHands.get(player1).getKey().removeCard(h);
        //System.out.println(stackManager.playerHands.get(player1).getKey().getCards());

        stackManager.playerHands.get(player1).getKey().addCard(card1);
        stackManager.playerHands.get(player1).getKey().addCard(card4);
        stackManager.playerHands.get(player1).getKey().addCard(card2);

        assertTrue(stackManager.playerHands.get(player1).getKey().getCards().get(0) == card2);
        assertTrue(stackManager.playerHands.get(player1).getKey().getCards().get(1) == card4);
        assertTrue(stackManager.playerHands.get(player1).getKey().getCards().get(2) == card1);

    }

    @Test
    void getLastCardsEachStall() throws RemoteException, NotBoundException {
        GameRoom gameRoom = new GameRoom(registry, db,"room1", 4, 1, true);
        Player player1 = new Player("player1", "localhost");
        Player player2 = new Player("player2", "localhost");
        Player player3 = new Player("player3", "localhost");
        gameRoom.enterRoom(player1);
        gameRoom.enterRoom(player2);
        gameRoom.enterRoom(player3);

/*        GameRoom gameRoom = new GameRoom(registry, "room1", 1, 0);
        gameRoom.enterRoom(player1);*/
        assertTrue(gameRoom.startGame());
        gameRoom.startGame();

        StackManager stackManager = new StackManager(registry, db,"stackmanager");
        stackManager.playerHands = gameRoom.getStackmanager().playerHands;

        Card card4 = new Card(18, "diamonds");
        Card card1 = new Card(4, "hearts");
        Card card2 = new Card(7, "clubs");

        System.out.println(stackManager.getLastCardsEachStall(player1));
        assertTrue(stackManager.getLastCardsEachStall(player1).size() != 0);

    }

    @Test
    void getFirstCardsEachStall() throws RemoteException, NotBoundException {
        GameRoom gameRoom = new GameRoom(registry, db,"room1", 4, 1, true);
        Player player1 = new Player("player1", "localhost");
        Player player2 = new Player("player2", "localhost");
        Player player3 = new Player("player3", "localhost");
        gameRoom.enterRoom(player1);
        gameRoom.enterRoom(player2);
        gameRoom.enterRoom(player3);

/*        GameRoom gameRoom = new GameRoom(registry, "room1", 1, 0);
        gameRoom.enterRoom(player1);*/
        assertTrue(gameRoom.startGame());
        gameRoom.startGame();

        StackManager stackManager = new StackManager(registry, db,"stackmanager");
        stackManager.playerHands = gameRoom.getStackmanager().playerHands;

        Card card4 = new Card(18, "diamonds");
        Card card1 = new Card(4, "hearts");
        Card card2 = new Card(7, "clubs");

        System.out.println(stackManager.getFirstCardsEachStall(player1));
        assertTrue(stackManager.getFirstCardsEachStall(player1).size() != 0);
    }

    @Test
    void turnInfo() throws RemoteException, NotBoundException {
        GameRoom gameRoom = new GameRoom(registry, db,"room1", 4, 1, true);
        Player player1 = new Player("player1", "localhost");
        Player player2 = new Player("player2", "localhost");
        Player player3 = new Player("player3", "localhost");
        gameRoom.enterRoom(player1);
        gameRoom.enterRoom(player2);
        gameRoom.enterRoom(player3);

/*        GameRoom gameRoom = new GameRoom(registry, "room1", 1, 0);
        gameRoom.enterRoom(player1);*/
        assertTrue(gameRoom.startGame());
        gameRoom.startGame();

        StackManager stackManager = new StackManager(registry, db,"stackmanager");
        stackManager.playerHands = gameRoom.getStackmanager().playerHands;

        Card card4 = new Card(18, "diamonds");
        Card card1 = new Card(4, "hearts");
        Card card2 = new Card(7, "clubs");

        System.out.println(stackManager.turnInfo());
        assertTrue(stackManager.turnInfo().size() != 0);

    }

    @Test
    void isMyTurn() throws NotBoundException, RemoteException {
        GameRoom gameRoom = new GameRoom(registry, db,"room1", 4, 1, true);
        Player player1 = new Player("player1", "localhost");
        Player player2 = new Player("player2", "localhost");
        Player player3 = new Player("player3", "localhost");
        gameRoom.enterRoom(player1);
        gameRoom.enterRoom(player2);
        gameRoom.enterRoom(player3);

/*        GameRoom gameRoom = new GameRoom(registry, "room1", 1, 0);
        gameRoom.enterRoom(player1);*/
        assertTrue(gameRoom.startGame());
        gameRoom.startGame();

        StackManager stackManager = new StackManager(registry, db,"stackmanager");
        stackManager.playerHands = gameRoom.getStackmanager().playerHands;

        Card card4 = new Card(18, "diamonds");
        Card card1 = new Card(4, "hearts");
        Card card2 = new Card(7, "clubs");

        System.out.println(stackManager.isMyTurn(player1));
        assertTrue(stackManager.isMyTurn(player1));
        assertFalse(stackManager.isMyTurn(player2));
    }

    @Test
    void chipsInfo() throws NotBoundException, RemoteException {

        GameRoom gameRoom = new GameRoom(registry, db,"room1", 4, 1, true);
        Player player1 = new Player("player1", "localhost");
        Player player2 = new Player("player2", "localhost");
        Player player3 = new Player("player3", "localhost");
        gameRoom.enterRoom(player1);
        gameRoom.enterRoom(player2);
        gameRoom.enterRoom(player3);

/*        GameRoom gameRoom = new GameRoom(registry, "room1", 1, 0);
        gameRoom.enterRoom(player1);*/
        assertTrue(gameRoom.startGame());
        gameRoom.startGame();

        StackManager stackManager = new StackManager(registry, db,"stackmanager");
        stackManager.playerHands = gameRoom.getStackmanager().playerHands;

        Card card4 = new Card(18, "diamonds");
        Card card1 = new Card(4, "hearts");
        Card card2 = new Card(7, "clubs");

        stackManager.chipsInfo();
    }

    @Test
    void getPot() throws NotBoundException, RemoteException {

        GameRoom gameRoom = new GameRoom(registry, db,"room1", 4, 1, true);
        Player player1 = new Player("player1", "localhost");
        Player player2 = new Player("player2", "localhost");
        Player player3 = new Player("player3", "localhost");
        gameRoom.enterRoom(player1);
        gameRoom.enterRoom(player2);
        gameRoom.enterRoom(player3);

/*        GameRoom gameRoom = new GameRoom(registry, "room1", 1, 0);
        gameRoom.enterRoom(player1);*/
        assertTrue(gameRoom.startGame());
        gameRoom.startGame();

        StackManager stackManager = new StackManager(registry, db,"stackmanager");
        stackManager.playerHands = gameRoom.getStackmanager().playerHands;

        assertTrue(stackManager.getPot()==0);

    }
}