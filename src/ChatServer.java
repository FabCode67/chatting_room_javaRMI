import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ChatServer extends UnicastRemoteObject implements ChatServerInterface {
    private final List<ChatClientInterface> clients = new ArrayList<>();

    protected ChatServer() throws RemoteException {
        super();
    }

    @Override
    public synchronized void registerClient(ChatClientInterface client, String username) throws RemoteException {
        clients.add(client);
        broadcastMessage(username + " has joined the chat.");
    }

    @Override
    public synchronized void broadcastMessage(String message) throws RemoteException {
        for (ChatClientInterface client : clients) {
            client.receiveMessage(message);
        }
    }

    public static void main(String[] args) {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(5000);
            ChatServer chatServer = new ChatServer();
            java.rmi.Naming.rebind("ChatServer", chatServer);
            System.out.println("Chat Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
