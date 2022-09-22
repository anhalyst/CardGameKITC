package kings.server.chat;

import kings.client.IClient;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface IChat extends Remote {
    //public void storeMessage(String message, String playerName) throws RemoteException;
    public void storeMessage(String message, IClient player) throws RemoteException;
    //public List<Map.Entry<String, String>> getNewMessages() throws RemoteException;
    //public int amountMessages() throws RemoteException;

}
