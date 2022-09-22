package kings.server.room;

import kings.client.IClient;
import kings.server.administration.dbController;
import kings.server.chat.Chat;
import kings.server.game.Bot;
import kings.server.game.IGameManager;
import kings.server.game.StackManager;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class GameRoom extends Room {
    Registry registry;
    int capacity;
    int amountBots = 0;
    Chat chat;

    public StackManager sm;
    private ArrayList<IClient> bots;

    /**
     * creates a new gameRoom with a new chat
     * @param name
     * @param capacity
     * @param amountOfBots
     */
    public GameRoom(Registry registry, dbController db, String name, int capacity, int amountOfBots, boolean botLevel) {
        this.registry = registry;
        // @TODO set max capacity + amountOfBots!
        this.name = name;
        this.capacity = capacity;
        this.amountBots = amountOfBots;

        chat = new Chat(name, this);

        // create a new stackmanager
        sm = new StackManager(registry, db, name);


        createBots(amountOfBots, botLevel);



        try {
            // create the stub object and bind it to the registry
            IGameManager stubGameManager = (IGameManager) UnicastRemoteObject.exportObject(sm, 0);
            registry.bind("GameManager" + name, stubGameManager);
        }
        catch (AlreadyBoundException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean startGame() {
        // if room is full => game can start
        // @TODO remove -amountOfBots, when gui is updated
        if (capacity-amountBots == getPlayers().size()) {
            //System.out.println("[GameRoom startGame()] starting the game");
            sm.start(getPlayers(), bots);
            return true;
        }

        return false;
    }

    public int getCapacity() {
        return capacity;
    }


    /**
     * is executed by addCard of the StackManager
     */
    public void endGame() {
        for (IClient player: getPlayers()) {
            try {
                // disconnect from the gameManager
                player.disconnectFromGameManager();
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }

            // move the player back to the lobby

        }
    }

    private void createBots(int amount, boolean lvl) {
        bots = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            // @TODO use a uuid as name
            bots.add(new Bot(sm, "Bot" + i, lvl));
        }
    }

    public StackManager getStackmanager(){
        return sm;
    }
/*
    public void deleteGameRoom() {
        // move all players to the lobby
        RoomServer.changeRoom(players, "Lobby", name);

        gameServer.remove(this);
    }

    public void addBots(int amount) {
        amountBots += amount;
        /*
        for (int i = 0; i < amountOfBots; i++) {
            Bot bot = new Bot(uniqueID)
            players.append(bot)
        }
         *//*
    }
    */
}
