package me.nickcarroll.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Server {

    private static final int PORT = 8080;
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private static Logger logger = Logger.getLogger(Server.class.getName());

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        ServerSocketWrapper socket = new SimpleServerSocketWrapper(serverSocket.accept());
        logger.info("Created new socket: " + socket);
        executorService.execute(new RequestHandler(socket, executorService));
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
