package me.nickcarroll.server;


import me.nickcarroll.message.Message;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

/**
 * RequestHandler listens on a socket and echoes back any messages received.
 */
public class RequestHandler implements Runnable {

    private static Logger logger = Logger.getLogger(RequestHandler.class.getName());

    private final ServerSocketWrapper socket;
    private final ExecutorService executorService;

    public RequestHandler(ServerSocketWrapper socket, ExecutorService executorService) {
        this.socket = socket;
        this.executorService = executorService;
    }

    /**
     * Handles a message request received on the input stream which is then echoed back through the output
     * stream.
     */
    public void run() {
        try {
            while (!socket.isClosed()) {
                Message message = socket.receiveMessage();
                if (message != null) {
                    logger.info("Received: " + message);
                    socket.sendMessage(message);
                    logger.info("Sent: " + message);
                } else {
                    // Close socket if no messages were received.
                    // NOTE: This is a simplifying assumption that no messages means the client has been shutdown
                    // and the application lifecyle has come to an end.  Otherwise a shutdown message should be
                    // detected and handled accordingly.
                    socket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }
}
