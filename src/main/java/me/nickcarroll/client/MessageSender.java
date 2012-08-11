package me.nickcarroll.client;


import me.nickcarroll.message.Message;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * MessageSender delegates message sending to LockingClientSocketWrapper which uses
 * explicit locking on the shared socket resource.
 */
public class MessageSender implements Runnable {

    private static Logger logger = Logger.getLogger(MessageSender.class.getName());

    private final ClientSocketWrapper socket;
    private final int numberOfMessagesToSend;

    public MessageSender(ClientSocketWrapper socket, int numberOfMessagesToSend) {
        this.socket = socket;
        this.numberOfMessagesToSend = numberOfMessagesToSend;
    }

    public void run() {
        int id = 0;
        while (id < numberOfMessagesToSend) {
            try {
                if (socket.sendMessage(new Message(id, System.nanoTime()))) {
                    id++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        logger.info("No more messages to send");
    }
}
