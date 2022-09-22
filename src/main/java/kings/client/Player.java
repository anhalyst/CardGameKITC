package kings.client;

import Exceptions.LoginExeption;
import Exceptions.SpielraumExistiertExeption;
import Exceptions.SpielraumVollExeption;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Pair;
import kings.server.chat.IChat;
import kings.server.administration.IRoomServer;
import kings.server.game.Card;
import kings.server.game.IGameManager;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Player implements IClient, Serializable {
    Registry registry;
    IRoomServer roomServer;
    String currentRoom;
    IChat chat;
    IGameManager gm;
    String name;
    private final BooleanProperty newMessage = new SimpleBooleanProperty(false);
    private final BooleanProperty started = new SimpleBooleanProperty(false);
    private boolean madeMove = false;
    private final BooleanProperty newTurn = new SimpleBooleanProperty(false);
    private final BooleanProperty gameEnded = new SimpleBooleanProperty(false);

    ArrayList<Map.Entry<String, String>> messages;
    private ArrayList<Map.Entry<String, String>> gameRoomList = new ArrayList<>();

    /**
     * creates a remote object, so that the server can use methods of the player (currently receiveMsg)
     * @param name
     * @throws RemoteException
     */
    public Player(String name, String ipAddr) throws RemoteException, NotBoundException {
        this.name = name;
        this.registry = LocateRegistry.getRegistry(ipAddr, 1099);



        // access methods from the correct room
        roomServer = (IRoomServer) registry.lookup("RoomServer");



        messages = new ArrayList<>();
        messages.add(new AbstractMap.SimpleEntry<>("", ""));
        UnicastRemoteObject.exportObject(this, 0);


        enterEntranceRoom();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void enterEntranceRoom() throws RemoteException {
        // enter the enteranceRoom
        roomServer.playerEntering(this);
    }

    /**
     * recieves messages from the currently entered chat room
     * is remote executed by the server
     * @return
     */
    public Map.Entry<String, String> receiveMsg(Map.Entry<String, String> msg) {
        System.out.println(msg);
        messages.add(msg);
        return msg;
    }

    /**
     * sets the correct chat room for the player
     * is executed by the room server
     * @param currentRoom
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void connectToChatRoom(String currentRoom) throws RemoteException, NotBoundException {
        // get messages from the correct chat
        chat = (IChat) registry.lookup("Chat"+currentRoom);
    }

    /**
     * is executed by connectPlayers() from StackManager
     *
     * gets access to the gameManager
     * @param currentRoom
     * @throws NotBoundException
     * @throws RemoteException
     */
    public void connectToGameManager(String currentRoom) throws NotBoundException, RemoteException {
        setGameEnded(false);
        gm = (IGameManager) registry.lookup("GameManager"+currentRoom);
    }

    /**
     * is executed by endGame() from StackManager
     *
     * marks, that the game is over and the player is set to the lobby
     * moves the player to the lobby again
     */
    public void disconnectFromGameManager() throws RemoteException {
        setGameEnded(true);
        gm = null;

        try {
            roomServer.leaveGameRoom(this);
        }
        catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void sendMsg(String msg) throws RemoteException {
        //chat.storeMessage(msg, name);
        chat.storeMessage(msg, this);

    }

    public boolean login(String pwd) throws NotBoundException, RemoteException {
        try {
            return roomServer.login(this, this.name, pwd);
        }
        catch (LoginExeption e) {
            return false;
        }
    }

    public boolean register(String pwd) throws RemoteException {
        return roomServer.insertNewPlayer(name, pwd);
    }

    @Override
    public void newMessage() throws RemoteException {
        newMessage.set(true);
        System.out.println("newMessage set to true");
    }

    public Map.Entry<String, String> getNewMessage() {
        return messages.get(messages.size() - 1);
    }

    public BooleanProperty newMessageProperty() {
        return newMessage;
    }

    public void setNewMessage(boolean value) {
        newMessage.set(value);
    }

    @Override
    public void storeRoom(Map.Entry<String, String> roomInfo) throws RemoteException {
        gameRoomList.add(roomInfo);
    }

    public boolean createGameRoom(IClient player, String name, int capacity, int amountBots, boolean botMode) throws NotBoundException, RemoteException, SpielraumExistiertExeption {
        return roomServer.createGameRoom(player, name, capacity, amountBots,botMode);
    }


    public void updateRoomList() throws RemoteException {
        gameRoomList.clear();
        roomServer.updateRoomList(this);
    }

    public ArrayList<Map.Entry<String, String>> getGameRoomList() throws RemoteException {
        updateRoomList();
        return gameRoomList;
    }

    @Override
    public String getCurrentRoom() throws RemoteException {
        return currentRoom;
    }
    @Override
    public void setCurrentRoom(String newRooom) throws RemoteException {
        this.currentRoom = newRooom;
    }

    public void enterGameRoom(String room) throws NotBoundException, RemoteException, SpielraumVollExeption {
        roomServer.enterGameRoom(this, room);
    }

    public void leaveGameRoom() throws NotBoundException, RemoteException {
        roomServer.leaveGameRoom(this);
    }

    public String getPlayersList() throws RemoteException {
        ArrayList<Map.Entry<String, String>> gameRoomList = getGameRoomList();
        for (Map.Entry<String, String> room: gameRoomList) {
            if (room.getKey().equals(currentRoom)) {
                int index =  room.getValue().indexOf("Spieler");
                return room.getValue().substring(index);
            }
        }
        return "";
    }

    public void deleteAccount() throws NotBoundException, RemoteException {
        roomServer.deletePlayer(this);
    }

    public boolean updatePassword(String oldPassword, String newPassword) throws RemoteException {
        return roomServer.updatePassword(this, oldPassword, newPassword);
    }

    public BooleanProperty startedProperty() {
        return started;
    }

    @Override
    public void setStarted(boolean value) {
        started.set(value);
    }

    public void startGame() throws RemoteException {
        roomServer.startGame(this);
    }

    public ArrayList<Card> getCards() throws RemoteException {
        try {
            ArrayList<Card> result = gm.getCards(this);
            return result;
        } catch (NullPointerException e) {
            setGameEnded(true);
            return new ArrayList<>();
        }
    }

    public boolean changeStack(int fromStall, int toStall) throws RemoteException {
        return gm.changeStack(this, fromStall, toStall);
    }

    public boolean addCard(Card c, int stallIndex) throws RemoteException {
        return gm.addCard(this, c, stallIndex);
    }

    public boolean endTurn() throws RemoteException {
        return gm.endTurn(this);
    }

    public ArrayList<Card> getLastCardsEachStall() throws RemoteException {
        return gm.getLastCardsEachStall(this);
    }

    public ArrayList<Card> getFirstCardsEachStall() throws RemoteException{
        return gm.getFirstCardsEachStall(this);
    }

    public LinkedHashMap<String, Boolean> turnInfo() throws RemoteException {
        return gm.turnInfo();
    }

    public boolean isMyTurn() throws RemoteException {
        return gm.isMyTurn(this);
    }

    public BooleanProperty newTurnProperty() {
        return newTurn;
    }

    @Override
    public void setNewTurn(boolean value) throws RemoteException {
        newTurn.set(value);
    }

    public BooleanProperty gameEndedProperty() {
        return gameEnded;
    }

    @Override
    public void setGameEnded(boolean value) throws RemoteException {
        gameEnded.set(value);
    }

    @Override
    public void logout() throws RemoteException, NotBoundException {
        roomServer.logout(this);
    }

    public LinkedHashMap<String, Pair<Integer, Integer>> chipsInfo() throws RemoteException {
        return gm.chipsInfo();
    }

    public int getPot() throws RemoteException {
        return gm.getPot();
    }

    public ArrayList<Map.Entry<String, Integer>> topHighScore() throws RemoteException {
        return new ArrayList<>(roomServer.topHighScore());
    }
}

