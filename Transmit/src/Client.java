import java.io.*;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {
        NetworkUtils networkUtils = new NetworkUtils();

        String name = "localhost";
        int port = 8080;
        boolean status = true;
        Socket socket = null;

        while (status) {
            int answer = networkUtils.clientOptionsText();

            if (answer == 1) {
                socket = networkUtils.connectTo(name, port);
            } else if (answer == 2) {
                networkUtils.sendMsg(socket);
            } else if (answer == 3) {
                networkUtils.sendFile(socket);
            } else if (answer == 4) {
                networkUtils.receiveMsg(socket);
            } else if (answer == 5) {
                networkUtils.receiveFile(socket);
            } else if (answer == 6) {
                networkUtils.closeConnection(socket);
                status = false;
            }
        }
    }
}