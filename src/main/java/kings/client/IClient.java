package kings.client;

import javafx.beans.property.BooleanProperty;
import kings.server.room.GameRoom;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface IClient extends Remote {
    Map.Entry<String, String> receiveMsg(Map.Entry<String, String> msg) throws RemoteException;
    //void receiveMsg(String msg) throws RemoteException;

    void newMessage() throws RemoteException;
    void enterEntranceRoom() throws RemoteException;
    void connectToChatRoom(String currentRoom) throws RemoteException, NotBoundException;
    void connectToGameManager(String currentRoom) throws RemoteException, NotBoundException;
    void disconnectFromGameManager() throws RemoteException;
    String getName() throws RemoteException;

    void storeRoom(Map.Entry<String, String> roomInfo) throws RemoteException;

    String getCurrentRoom() throws RemoteException;
    void setCurrentRoom(String newRoom) throws RemoteException;
    void setStarted(boolean value) throws RemoteException;

    void setNewTurn(boolean value) throws RemoteException;
    void setGameEnded(boolean value) throws RemoteException;
    void logout() throws RemoteException, NotBoundException;
}
