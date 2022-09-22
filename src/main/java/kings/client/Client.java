package kings.client;

import kings.server.chat.IChat;
import kings.server.administration.IRoomServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;


public class Client{
    static Registry registry;
    static IRoomServer roomServer;
    static IChat chat;
    static String uuid = "Bernd"; //UUID.randomUUID().toString();
/*
    public static void main(String[] args) {
        try {
            registry = LocateRegistry.getRegistry("131.246.32.108", 1099);
            IClient p = new Player(uuid, registry);
            // this method has to be called, to introduce the client to the server
            p.enterEntranceRoom();

            p.exampleCalls();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
/*
    public Player createNewPlayer(String name) throws RemoteException, NotBoundException {
        registry = LocateRegistry.getRegistry(1099);
        Player p = new Player(name, registry);
        p.enterEntranceRoom();
        return p;
    }
*/

}
