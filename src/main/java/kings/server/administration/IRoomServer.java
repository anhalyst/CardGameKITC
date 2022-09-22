package kings.server.administration;

import Exceptions.LoginExeption;
import Exceptions.SpielraumExistiertExeption;
import Exceptions.SpielraumVollExeption;
import kings.client.IClient;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface IRoomServer extends Remote {
    void playerEntering(IClient player) throws RemoteException;
    boolean createGameRoom(IClient player, String name, int capacity, int amountBots, boolean botLevel) throws RemoteException, NotBoundException, SpielraumExistiertExeption;
    boolean login(IClient player, String name, String pwd) throws NotBoundException, RemoteException, LoginExeption;
    boolean logout(IClient player) throws NotBoundException, RemoteException;
    void enterGameRoom(IClient player, String name) throws RemoteException, NotBoundException, SpielraumVollExeption;
    boolean startGame(IClient player) throws RemoteException;
    void leaveGameRoom(IClient player) throws NotBoundException, RemoteException;
    boolean insertNewPlayer(String name, String pass) throws RemoteException;
    void deletePlayer(IClient player) throws RemoteException, NotBoundException;
    boolean passwordCheck(String username, String password) throws RemoteException;
    void updateRoomList(IClient player) throws RemoteException;
    boolean updatePassword (IClient player, String oldPassword, String newPassword) throws RemoteException;
    List<Map.Entry<String, Integer>> topHighScore() throws RemoteException;
}
