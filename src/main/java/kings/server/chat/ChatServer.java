package kings.server.chat;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import kings.server.administration.Server;

public class ChatServer {

    static Registry registry;

    /**
     * registry is hand over once by the server
     * @param registry
     */
    public static void setRegistry(Registry registry) {
        ChatServer.registry = registry;
    }

    /**
     * creates a new stub for a chat room and binds it to the registry
     * this is done for every new Chat Room (see in Chat constructor)
     * @param chat
     */
    public static void createServer(Chat chat) {
        try {
            //Registry registry = LocateRegistry.getRegistry(1099);
            IChat stubChat = (IChat) UnicastRemoteObject.exportObject(chat, 0);

            // bind name = "Chat" + <the name of the chat room>
            registry.bind("Chat" + chat.chatRoomName, stubChat);
            System.out.println("Chat Server ready");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
