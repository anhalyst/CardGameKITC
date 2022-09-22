package kings.server.chat;

import kings.client.Player;
import kings.server.room.Room;
import org.junit.jupiter.api.Test;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ChatTest {
    private Registry register1;
    Player player1;
    Room r1 = new Room() ;
    Registry registry;




    {
        try {
            player1 = new Player("player1", "localhost");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    public ChatTest() throws RemoteException {
    }

    /**
     * @throws RemoteException
     * @throws NotBoundException
     */
    @Test
    void storeMessage() throws RemoteException, NotBoundException {
        Player player = new Player("player", "localhost" );
        r1.enterRoom(player);
        Chat chat = new Chat("chat", r1);

        HashMap<String, String> msg = new HashMap<String, String>();
        msg.put("player", "the message");

        chat.storeMessage("the message", player);

        assertTrue(chat.messages.size() == 1);



    }
}