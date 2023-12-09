import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        boolean status = true;

        ServerSocket serverSocket = null;
        NetworkUtils networkUtils = new NetworkUtils();
        Socket clientSocket = null;

        while (status) {
            int answer = networkUtils.serverOptionText();
            if (answer == 1) {
                serverSocket = networkUtils.runServer(port);
                clientSocket = networkUtils.connectTo();
            } else if (answer == 2) {
                networkUtils.sendMsg(clientSocket);
            } else if (answer == 3) {
                networkUtils.sendFile(clientSocket);
            } else if (answer == 4) {
                networkUtils.receiveMsg(clientSocket);
            } else if (answer == 5) {
                networkUtils.receiveFile(clientSocket);
            } else if (answer == 6) {
                networkUtils.closeConnection(serverSocket);
            }
        }

    }


}
