import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatClient extends UnicastRemoteObject implements ChatClientInterface {
    private final String username;
    private final ChatServerInterface chatServer;
    private final JTextArea chatArea;
    private final JTextField messageField;

    protected ChatClient(String username, ChatServerInterface chatServer) throws RemoteException {
        super();
        this.username = username;
        this.chatServer = chatServer;
        this.chatArea = new JTextArea();
        this.messageField = new JTextField();

        initializeUI();
        chatServer.registerClient(this, username);
    }

    private void initializeUI() {
        JFrame frame = new JFrame("Chat Client - " + username);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                    try {
                        sendMessage(message);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                    messageField.setText("");
                }
            }
        });
        inputPanel.add(sendButton, BorderLayout.EAST);

        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setSize(400, 300);
        frame.setVisible(true);
    }

    public void sendMessage(String message) throws RemoteException {
        chatServer.broadcastMessage(username + ": " + message);
    }

    @Override
    public void receiveMessage(String message) throws RemoteException {
        chatArea.append(message + "\n");
    }

    public static void main(String[] args) {
        try {
            String username = args[0];
            ChatServerInterface chatServer = (ChatServerInterface) java.rmi.Naming.lookup("rmi://localhost/ChatServer");
            ChatClient chatClient = new ChatClient(username, chatServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
