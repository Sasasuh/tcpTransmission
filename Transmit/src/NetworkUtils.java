import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class NetworkUtils implements NetworkOperations {

    // Scanner for user input
    Scanner scanner = new Scanner(System.in);

    // Output stream for sending data
    OutputStream output;

    // Input stream for receiving data
    InputStream input;

    // Server socket for running a server
    ServerSocket serverSocket;

    // Client socket for connecting to a server or accepting client connections
    Socket clientSocket;

    /**
     * Displays menu options for the client.
     *
     * @return The selected option
     */
    @Override
    public int clientOptionsText() {
        System.out.println("1. Connect to localhost\n2. Send message to server\n3. Send file to server\n4. Receive message from server\n5. Receive file from server\n6. Close connection with server");
        System.out.print("Choose the option: ");

        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            return clientOptionsText();
        }
    }

    /**
     * Displays menu options for the server.
     *
     * @return The selected option.
     */
    @Override
    public int serverOptionText() {
        System.out.println("1. Run server\n2. Send message to client\n3. Send file to client\n4. Receive message from client\n5. Receive file from client\n6. Close connection with client");
        System.out.print("Choose the option: ");

        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            return clientOptionsText();
        }
    }

    /**
     * Checks if a socket is valid and not closed.
     *
     * @param socket The socket to check.
     * @return True if the socket is valid and not closed, false otherwise.
     */
    @Override
    public boolean checkSocket(Socket socket) {
        if (socket == null || socket.isClosed()) {
            System.out.println("No socket/Socket closed");
            return false;
        }
        return true;
    }


    /**
     * Connects to a specified server.
     *
     * @param name The server's name or IP address.
     * @param port The port number to connect to.
     * @return The connected socket.
     */
    @Override
    public Socket connectTo(String name, int port) {
        Socket socket = null;
        try {
            socket = new Socket(name, port);
            System.out.println("[Client] Connected to the server: " + socket.getInetAddress());
        } catch (Exception e) {
            System.out.println("Can't find local server, try again");
        }
        return socket;
    }

    /**
     * Runs a server on the specified port.
     *
     * @param port The port number to run the server on.
     * @return The server socket.
     */
    @Override
    public ServerSocket runServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("[Server] Running server on " + port + " port");
        } catch (IOException e) {
            System.out.println("[Server] Error starting the server: " + e.getMessage());
        }
        return serverSocket;
    }


    /**
     * Accepts a connection from a client.
     *
     * @return The client socket.
     */
    @Override
    public Socket connectTo() {
        try {
            System.out.println("[Server] Waiting for the connection...");
            clientSocket = serverSocket.accept();
            System.out.println("[Server] Connected with " + clientSocket.getInetAddress());
        } catch (IOException e) {
            System.out.println("[Server] Error accepting client connection: " + e.getMessage());
        }

        return clientSocket;
    }

    /**
     * Sends a message to the connected socket.
     *
     * @param socket The socket to send the message to.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void sendMsg(Socket socket) throws IOException {
        if (checkSocket(socket)) {
            output = socket.getOutputStream();

            System.out.print("Write the msg to send: ");
            String msg = scanner.nextLine();
            output.write(msg.getBytes());
            System.out.println("[Client] Message was sent");
        }
    }

    /**
     * Sends a file to the connected socket.
     * !!!IMPORTANT!!! Closing connection after file transfer
     *
     * @param socket The socket to send the file to.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void sendFile(Socket socket) throws IOException {
        if (checkSocket(socket)) {
            System.out.println("Write the path of the file you want to send: ");
            String filePath = scanner.nextLine();

            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);

            BufferedInputStream bis = new BufferedInputStream(fis);
            output = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(output);
            dos.writeUTF(file.getName());

            byte[] content;
            long fileLenght = file.length();
            long currentReaded = 0;
            while (currentReaded != fileLenght) {
                int size = 10000;
                if (fileLenght - currentReaded >= size) {
                    currentReaded += size;
                } else {
                    size = (int) (fileLenght - currentReaded);
                    currentReaded = fileLenght;
                }
                content = new byte[size];
                bis.read(content, 0, size);
                output.write(content);
                System.out.println("Sending file ... " + (100 * currentReaded) / fileLenght + "% completed");
            }
            output.flush();
            System.out.println("File transfer is done");
            socket.close();
        }
    }

    /**
     * Receives a message from the connected socket and prints it.
     *
     * @param socket The socket to receive the message from.
     * @throws IOException If an I/O error occurs.
     */

    @Override
    public void receiveMsg(Socket socket) throws IOException {
        if (checkSocket(socket)) {
            input = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int response = input.read(buffer);
            String constructResponse = new String(buffer, 0, response);
            System.out.println(constructResponse);
        }
    }


    /**
     * Receives a file from the connected socket and saves it to the user's home directory.
     *
     * @param socket The socket to receive the file from.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void receiveFile(Socket socket) throws IOException {
        if (checkSocket(socket)) {
            byte[] content = new byte[10000];
            input = socket.getInputStream();
            DataInputStream dis = new DataInputStream(input);
            String filename = dis.readUTF();
            FileOutputStream fos = new FileOutputStream(System.getProperty("user.home") + "\\" + filename);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            int bytesRead = 0;
            while ((bytesRead = input.read(content)) != -1) {
                bos.write(content, 0, bytesRead);
            }


            bos.close();
            fos.close();
            System.out.println("File saved successfully!");
        }
    }

    /**
     * Closes the connection from the client side.
     *
     * @param socket The socket to close.
     * @return The closed socket.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public Socket closeConnection(Socket socket) throws IOException {
        socket.close();
        System.out.println("Connection w/ server is closed");
        return socket;
    }

    /**
     * Closes the connection from the server side.
     *
     * @param serverSocket The server socket to close.
     * @return The closed server socket.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public ServerSocket closeConnection(ServerSocket serverSocket) throws IOException {
        serverSocket.close();
        System.out.println("Connection w/ client is closed");
        return serverSocket;
    }
}