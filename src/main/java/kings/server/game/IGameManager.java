package kings.server.game;

import javafx.util.Pair;
import kings.client.IClient;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface IGameManager extends Remote {
    ArrayList<Card> getCards(IClient player) throws RemoteException;

    boolean changeStack(IClient player, int fromStall, int toStall) throws RemoteException;
    boolean addCard(IClient player,Card c, int stallIndex) throws RemoteException;
    boolean endTurn(IClient player) throws RemoteException;
    ArrayList<Card> getLastCardsEachStall(IClient player) throws RemoteException;
    ArrayList<Card> getFirstCardsEachStall(IClient player) throws RemoteException;
    LinkedHashMap<String, Boolean> turnInfo() throws RemoteException;
    boolean isMyTurn(IClient player) throws RemoteException;
    LinkedHashMap<String, Pair<Integer, Integer>> chipsInfo() throws RemoteException;
    int getPot() throws RemoteException;
}
