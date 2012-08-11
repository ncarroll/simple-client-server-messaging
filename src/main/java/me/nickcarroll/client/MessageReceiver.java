package me.nickcarroll.client;

import me.nickcarroll.message.TransactedMessage;

import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.logging.Logger;

/**
 * MessageReceiver delegates message retrieval to LockingClientSocketWrapper which uses
 * explicit locking on the shared socket resource.
 */
public class MessageReceiver implements Runnable {

    private static Logger logger = Logger.getLogger(MessageReceiver.class.getName());

    private final ClientSocketWrapper socket;
    private final BlockingDeque<TransactedMessage> inbox;
    private int numberOfMessagesToReceive;

    public MessageReceiver(ClientSocketWrapper socket,
                           BlockingDeque<TransactedMessage> inbox,
                           int numberOfMessagesToReceive) {
        this.socket = socket;
        this.inbox = inbox;
        this.numberOfMessagesToReceive = numberOfMessagesToReceive;
    }

    public void run() {
        int messagesReceived = 0;
        while (messagesReceived < numberOfMessagesToReceive) {
            try {
                messagesReceived += socket.receiveMessages(inbox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("No more messages to receive");
    }
}
