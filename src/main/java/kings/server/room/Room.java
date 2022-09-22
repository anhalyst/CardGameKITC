package kings.server.room;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import kings.client.IClient;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class Room {

    private ArrayList<IClient> players = new ArrayList<>();
    String name;


    /**
     * inserts a player into the player ArrayList
     * player joins a room => add player to the ArrayList
     * @param player
     */
    public void enterRoom(IClient player) {
        players.add(player);
    }

    /**
     * removes a player from the player ArrayList
     * player leaves a room => remove player from the ArrayList
     * @param player
     */
    public void leaveRoom(IClient player) {
        players.remove(player);
    }

    public ArrayList<IClient> getPlayers() {
        return players;
    }

    public String getPlayersName() throws RemoteException {
        ArrayList<String> result = new ArrayList<>();
        for (IClient p: players) {
            result.add(p.getName());
        }
        return String.join(", ", result);
    }

    public String getName() {
        return name;
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public void printPLayers() {
        players.forEach(iClient -> {
            try {
                System.out.println(iClient.getName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }
}
