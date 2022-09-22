package kings.server.game;

import kings.client.IClient;
import kings.client.Player;
import kings.server.administration.dbController;
import kings.server.room.GameRoom;
import org.junit.jupiter.api.Test;

import java.rmi.*;
import java.rmi.registry.Registry;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;
class BotTest {
    IClient player1;
    IClient player2;
    IClient player3;
    dbController db;

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
    StackManager stackManager = new StackManager(r, db ,"stackmanager");

    BotTest() throws RemoteException {
    }


    @Test
    void botTurn() throws RemoteException, NotBoundException {
        db = new dbController("bot");
        Card card4 = new Card(13, "diamonds");
        ArrayList cards = new ArrayList();
        cards.add(card4);
        Hand h = new Hand(cards);
        LinkedHashMap<IClient, AbstractMap.SimpleEntry<Hand, Boolean>> playerHands = new LinkedHashMap<>();
        playerHands.put(player1, new AbstractMap.SimpleEntry<>(h, true));

        stackManager.playerHands = playerHands;
        //  System.out.println(stackManager.playerHands.keySet());



        Bot bot = new Bot(stackManager, "bot1", true);
        Bot bot2 = new Bot(stackManager, "bot1", false);

        ArrayList<IClient> p = new ArrayList<>();
        ArrayList<IClient> bots = new ArrayList<>();
        p.add(player1);
        p.add(player2);
        p.add(player3);

        bots.add(bot);
        bots.add(bot2);

        //  GameRoom gameRoom = new GameRoom(r, "room1", 3, 2);
        System.setProperty("java.rmi.server.hostname","25.53.84.200");
        GameRoom gameRoom = new GameRoom(r, db,"room1", 3, 1, true);
        Player player1 = new Player("player1", "localhost");
        Player player2 = new Player("player2", "localhost");
        Player player3 = new Player("player3", "localhost");
        gameRoom.enterRoom(player1);
        gameRoom.enterRoom(player2);
        gameRoom.enterRoom(player3);

        System.out.println(gameRoom.getPlayers());

        stackManager.start(gameRoom.getPlayers(), bots);


        bot2.botTurn();
        bot.botTurn();

        //stackManager.start(p, bots);

        System.out.println(stackManager.playerHands.size());
        System.out.println("################################################################################");

/*
        GameRoom gameRoom = new GameRoom(r, "room1", 3, 2);
        stackManager.start(gameRoom.getPlayers(), gameRoom.getPlayers());
        Bot bot = new Bot(stackManager, "bot1", true);
        bot.botTurn();
*/

    }

    @Test
    void receiveMsg() {
    }

    @Test
    void newMessage() throws RemoteException {
        Bot bot = new Bot(stackManager, "bot1", true);
        bot.newMessage();


    }

    @Test
    void enterEntranceRoom() throws RemoteException {
        Bot bot = new Bot(stackManager, "bot1", true);
        bot.enterEntranceRoom();
    }

    @Test
    void connectToChatRoom() throws NotBoundException, RemoteException {
        Bot bot = new Bot(stackManager, "bot1", true);
        bot.connectToChatRoom("room");
    }

    @Test
    void connectToGameManager() throws RemoteException, NotBoundException {
        Bot bot = new Bot(stackManager, "bot1", true);
        bot.connectToGameManager("room");
    }

    @Test
    void disconnectFromGameManager() throws RemoteException {
        Bot bot = new Bot(stackManager, "bot1", true);
        bot.disconnectFromGameManager();
    }

    @Test
    void getName() throws RemoteException {
        Bot bot = new Bot(stackManager, "bot", true);
        assertTrue(bot.getName()=="bot");
    }


    @Test
    void storeRoom() {
    }

    @Test
    void getCurrentRoom() throws RemoteException {
        Bot bot = new Bot(stackManager, "bot1", true);
        bot.getCurrentRoom();
    }

    @Test
    void setCurrentRoom() throws RemoteException {
        Bot bot = new Bot(stackManager, "bot1", true);
        bot.setCurrentRoom("room");
    }

    @Test
    void setStarted() {
    }
}