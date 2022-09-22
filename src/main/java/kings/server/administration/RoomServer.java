package kings.server.administration;

import Exceptions.LoginExeption;
import Exceptions.SpielraumExistiertExeption;
import Exceptions.SpielraumVollExeption;
import javafx.util.Pair;
import kings.client.IClient;
import kings.server.room.EntranceRoom;
import kings.server.room.GameRoom;
import kings.server.room.Lobby;
import kings.server.room.Room;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * this class provides methods for the client, to move to another room
 */
public class RoomServer implements IRoomServer {
    Registry registry;
    Lobby lobby;
    EntranceRoom entranceRoom;
    dbController db;
    ArrayList<GameRoom> gameRooms;

    /**
     * initializes the necessary attributes
     *      the registry, so that players can change rooms
     *      lobby and entrance room, because those rooms are needed always
     *      a list for gameRooms
     * @param reg
     * @param eRoom
     * @param lobby
     */
    public RoomServer(Registry reg, EntranceRoom eRoom, Lobby lobby, dbController db) {
        this.registry = reg;
        this.lobby = lobby;
        entranceRoom = eRoom;
        this.db = db;

        gameRooms = new ArrayList<>();
    }
    /*
    public void setRegistry(Registry registry) throws RemoteException {
        this.registry = registry;
    }

    public void setRooms(EntranceRoom eRoom, Lobby lobby) throws RemoteException {
        this.lobby = lobby;
        entranceRoom = eRoom;
    }
*/
    /**
     * is executed by starting the client
     * it moves the player in the enterance room
     * @param player
     */
    public void playerEntering(IClient player) throws RemoteException {
        // move the player in the entrance room
        entranceRoom.enterRoom(player);
        System.out.println(player);
        System.out.println("moved player to the entrance room");
        player.setCurrentRoom(entranceRoom.getName());
    }

    /**
     * gets a player and moves it from one room to another
     * after moving the player, the method sets the correct chat room for the player
     * @param player
     * @param fromRoom  the room to leave
     * @param toRoom    the room to enter
     * @throws NotBoundException
     * @throws RemoteException
     */
    private void changeRoom(IClient player, Room fromRoom, Room toRoom) throws NotBoundException, RemoteException {
        // remove player from the room he came from
        fromRoom.leaveRoom(player);
        // add him to the room he wants to go
        toRoom.enterRoom(player);

        // give the possibility to chat in the new room
        player.connectToChatRoom(toRoom.getName());
        player.setCurrentRoom(toRoom.getName());
        System.out.println("player in eroom: ");
        entranceRoom.printPLayers();
        System.out.println("player in lobby: ");
        lobby.printPLayers();
        System.out.println("player in toRooom: ");
        toRoom.printPLayers();
        System.out.println("player in fromRooom: ");
        fromRoom.printPLayers();
        /*
        Iterator<IClient> it = player.iterator();
        IClient p;

        while (it.hasNext()) {
            p = it.next();
            fromRoom.leaveRoom(p);
            toRoom.enterRoom(p);
        }*/
    }

    /**
     * find a player in the gameRoom list => remove him => move him to the lobby
     * if the gameRoom is now empty => remove the gameRoom
     * @param player
     */
    public void leaveGameRoom(IClient player) throws NotBoundException, RemoteException {
        for (GameRoom gRoom : gameRooms) {
            // check if the player is in the game room
            if (gRoom.getPlayers().contains(player)) {
                changeRoom(player, gRoom, lobby);

                if (gRoom.getPlayers().size() == 0 || onlyBotsInTheRoom(gRoom)) {
                    System.out.println("delete the gameroom...");
                    deleteGameRoom(gRoom);
                }
                return;
            }
        }

    }

    private boolean onlyBotsInTheRoom(GameRoom gameRoom) {
        for (IClient player: gameRoom.getPlayers()) {
            if (!player.getClass().toString().equals("class kings.server.game.Bot")) {
                return false;
            }
        }
        return true;
    }

    /**
     * is executed by leaveGameRoom
     *
     * removes a given gameRoom from the gameRooms list
     * @param gameRoom
     */
    private void deleteGameRoom(GameRoom gameRoom) {
        try {
            registry.unbind("GameManager" + gameRoom.getName());
            registry.unbind("Chat" + gameRoom.getName());
        }
        catch (NotBoundException | RemoteException  e) {
            e.printStackTrace();
        }
        gameRooms.remove(gameRoom);

    }

    /**
     * creates a new game room
     * @param player
     * @param name
     * @param capacity
     * @param amountBots
     * @param botMode
     * @throws NotBoundException
     * @throws RemoteException
     */
    public boolean createGameRoom(IClient player, String name, int capacity, int amountBots, boolean botMode) throws NotBoundException, RemoteException, SpielraumExistiertExeption {
        // check if the player, that wants to create a gameRoom is in the lobby
        if (!lobby.getPlayers().contains(player)) {
            // the player is not in the lobby
            System.out.println("player not in lobby");
            return false;
        }

        // check if a game room with this name already exists
        if (!checkUniqueness(name)) {
            System.out.println("Name is not unique");
            return false;
//            throw new SpielraumExistiertExeption("Der angegebene Name des Raums wird bereits benutzt");
        }

        // more than six players are not allowed
        if (capacity > 6) {
            System.out.println("only 6 players max allowed (bots included)");
            return false;
        }

        // the player is in the lobby
        System.out.println("player is in lobby");

        // create a new gameRoom and append it to the gameRoom list
        GameRoom newRoom = new GameRoom(registry, db, name, capacity, amountBots, botMode);
        gameRooms.add(newRoom);

        // move the player to the new gameRoom
        changeRoom(player, lobby, gameRooms.get(gameRooms.size()-1));
        return true;
    }

    /**
     * checks if the room name for a new game room already exists
     * the names Lobby and EntranceRoom are blocked by default, because they are used interally
     * @param name
     * @return
     */
    private boolean checkUniqueness(String name) {
        if (name.equals("Lobby") || name.equals("EntranceRoom")) {
            // the room is not allowed to be named Lobby or EntranceRoom
            return false;
        }
        for (GameRoom gRoom : gameRooms) {
            if (gRoom.getName().equals(name)) {
                // another game room has this name
                return false;
            }
        }

        // the name is unique
        return true;
    }

    /**
     * takes a player and inserts it to an existing game room
     * checks if the game room exists
     * @param player
     * @param name
     */
    public void enterGameRoom(IClient player, String name) throws NotBoundException, RemoteException, SpielraumVollExeption {
        // check if the player is in the lobby
        if (!lobby.getPlayers().contains(player)) {
            System.out.println("player is not in lobby");
            return;
        }

        // check if a gameRoom with the name exists AND there are free places
        for (GameRoom gRoom : gameRooms) {
            if (gRoom.getName().equals(name)) {
                if (gRoom.getCapacity() > gRoom.getPlayers().size()) {
                    changeRoom(player, lobby, gRoom);
                    System.out.println(player.getName() + " is now in game Room");
                }
                else {
                    //changeRoom(player, gRoom, lobby);
                    throw new SpielraumVollExeption("Der angegebene Kapazität des Raums wurde bereits erreicht");
                }
            }
        }
    }


    public boolean startGame(IClient player) {
        // iterate over game rooms to find the room with this player
        for (GameRoom gameRoom: gameRooms) {
            if (gameRoom.getPlayers().contains(player)) {

                System.out.println("game started");

                // is false, if capacity is not reached
                return gameRoom.startGame();
            }
        }

        return false;

    }

    /**
     * checks if a player uses the correct credentials
     * if the player is already logged in => log the player out and continue
     * the player has to be in the entrance room to execute this
     * if they are correct => move the player to the lobby
     * @param player
     * @param name
     * @param pwd
     * @throws NotBoundException
     * @throws RemoteException
     * @return
     */
    public boolean login(IClient player, String name, String pwd) throws NotBoundException, RemoteException, LoginExeption {
        // player is already logged in => log the player out
        Pair<Room, IClient> oldPlayer = checkLoginState(player.getName());
        System.out.println("check login state");
        if (oldPlayer != null) {
            // remove the player from the room list
            //changeRoom(player, currentLocation, entranceRoom);
            oldPlayer.getKey().leaveRoom(oldPlayer.getValue());
            System.out.println("player logged out");
        }

        // player is not in the entrance room => no login possible
        if (!entranceRoom.getPlayers().contains(player)) {
            System.out.println("player is not in entrance room");
            throw new LoginExeption("Der Spieler befindet sich nicht im Vorraum und kann sich daher nicht einloggen");
        }

        if (db.passwordCheck(name, pwd)) {
            System.out.println("try to log in...");
            changeRoom(player, entranceRoom, lobby);

            // set the chat room of the player to the lobby chat room
            player.connectToChatRoom("Lobby");
            System.out.println("logged in and changed room to lobby");
            return true;
        }
        else {
            System.out.println("wrong credentials");
            throw new LoginExeption("Die eingebenen Login Daten sind falsch");
        }

    }

    /**
     * is remotly executed by the player
     *
     * moves the player from the lobby back to the entrance room
     * it is not allowed to logout from a gameroom
     * @param player
     * @return logging out worked => true; else => false;
     * @throws NotBoundException
     * @throws RemoteException
     */
    public boolean logout(IClient player) throws NotBoundException, RemoteException {
        // check if player is in lobby
        if (lobby.getPlayers().contains(player)) {
            // move the player to the entranceRoom
            changeRoom(player, lobby, entranceRoom);
            return true;
        }

        return false;
    }

    /**
     * checks if a player is already logged in
     * has to be done with the name, because player objects can be different and the name is unique
     * @param playerName
     * @return
     */
    private Pair<Room, IClient> checkLoginState(String playerName) {
        IClient oldPlayer;
        // player is in lobby => player is logged in
        ArrayList<IClient> lobbyPlayer = lobby.getPlayers();

        oldPlayer = checkPlayerList(lobbyPlayer, playerName);
        if (oldPlayer != null) {
            return new Pair<>(lobby, oldPlayer);
        }

        // player is in a gameRoom => player is logged in
        for (GameRoom gRoom : gameRooms) {
            ArrayList<IClient> gameRoomPlayer = gRoom.getPlayers();

            oldPlayer = checkPlayerList(gameRoomPlayer, playerName);
            if (oldPlayer != null) {
                return new Pair<>(gRoom, oldPlayer);
            }
        }

        return null;
    }

    /**
     * check if a player (identified by the name) is in a list of players
     * @param playerList
     * @param playerName
     * @return
     */
    private IClient checkPlayerList(ArrayList<IClient> playerList, String playerName) {
        for (IClient player : playerList) {
            try {
                if (player.getName().equals(playerName)) {
                    return player;
                }
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void updateRoomList(IClient player) throws RemoteException {
        for (GameRoom room: gameRooms) {
            Map.Entry<String, String> roomInfo = new AbstractMap.SimpleEntry<>(room.getName(), " - Kapazität: " + room.getPlayers().size() + "/" + room.getCapacity() + "- Spieler: " + room.getPlayersName());
            player.storeRoom(roomInfo);
        }
    }

    // ########### method wrapper for the db ###########
    @Override
    public boolean insertNewPlayer(String name, String pass) throws RemoteException{
        if (db.nameAlreadyExists(name)){
            return false;
        } else {
            db.insertNewPlayer(name, pass);
            return true;
        }
    }

    @Override
    public void deletePlayer(IClient player) throws NotBoundException, RemoteException {
        db.deletePlayer(player.getName());
//        changeRoom(player, lobby, entranceRoom);
    }

    public boolean passwordCheck(String username, String password) {
        return db.passwordCheck(username, password);
    }

    @Override
    public boolean updatePassword(IClient player, String oldPassword, String newPassword) throws RemoteException {
        if (passwordCheck(player.getName(), oldPassword)){
            db.updatePassword(player.getName(), newPassword);
            return true;
        }
        return false;
    }

    @Override
    public List<Map.Entry<String, Integer>> topHighScore() throws RemoteException {
        return db.topHighScore(5);
    }
}
