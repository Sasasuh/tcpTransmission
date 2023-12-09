import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public interface NetworkOperations {
    int clientOptionsText();
    int serverOptionText();

    boolean checkSocket(Socket socket);

    //client connection
    Socket connectTo(String name, int port);

    ServerSocket runServer(int port);

    //Server connection
    Socket connectTo();

    void sendMsg(Socket socket) throws IOException;

    void sendFile(Socket socket) throws IOException;

    void receiveMsg(Socket socket) throws IOException;

    void receiveFile(Socket socket) throws IOException;

    Socket closeConnection(Socket socket) throws IOException;

    ServerSocket closeConnection(ServerSocket serverSocket) throws IOException;
}
