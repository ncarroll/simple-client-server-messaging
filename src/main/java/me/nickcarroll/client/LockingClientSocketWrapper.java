package me.nickcarroll.client;


import me.nickcarroll.message.Message;
import me.nickcarroll.message.TransactedMessage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

/**
 * LockingClientSocketWrapper uses explicit locking to manage thread safety on a shared socket resource.
 * The main benefit for using a ReadWriteLock in this scenario is the non-blocking functionality
 * of the tryLock() method.  If a lock could not be held within a default time period then the
 * caller can just try again.
 */
public class LockingClientSocketWrapper implements ClientSocketWrapper {

    private static Logger logger = Logger.getLogger(LockingClientSocketWrapper.class.getName());

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock read = lock.readLock();
    private final Lock write = lock.writeLock();

    private final PrintWriter output;
    private final BufferedInputStream input;
    private Socket socket;

    public LockingClientSocketWrapper(Socket socket) throws IOException {
        this.socket = socket;
        this.output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.input = new BufferedInputStream(socket.getInputStream());
    }

    /**
     * Tries to establish a write lock to send the specified message.
     *
     * @param message The message to print to the output stream.
     * @return True if a lock could be established to send the message, otherwise false.
     * @throws IOException Throws IOException.
     */
    public boolean sendMessage(Message message) throws IOException {
        if (write.tryLock()) {
            try {
                writeMessage(message);
                logger.info("Sent: " + message);
                return true;
            } finally {
                write.unlock();
            }
        }
        return false;
    }

    /**
     * Tries to establish a read lock to receive all messages queued on the input stream.
     * Converts each message to an immutable TransactedMessage
     * object that is added to the shared BlockingDeque.
     *
     * @param inbox A shared BlockingDeque for queuing messages to be processed for statistics reporting.
     * @return Number of messages received off the input stream.
     * @throws IOException Throws IOException
     */
    public int receiveMessages(BlockingDeque<TransactedMessage> inbox) throws IOException {
        int count = 0;
        if (read.tryLock()) {
            try {
                while (input.available() > 0) {
                    Message message = readMessage();
                    logger.info("Received: " + message);
                    TransactedMessage transactedMessage = new TransactedMessage(message.getId(),
                            message.getSentTimestamp(), System.nanoTime());
                    inbox.add(transactedMessage);
                    count++;
                }
            } finally {
                read.unlock();
            }
        }
        return count;
    }

    /**
     * Convert a message to xml and print to the output stream.
     *
     * @param message The message to send across the output stream.
     */
    private void writeMessage(Message message) {
        output.println(message.getAsXML());
        output.flush();
    }

    /**
     * Reads xml from the input stream and converts it to a Message.
     *
     * @return A message from the input stream.
     * @throws IOException Throws IOException
     */
    private Message readMessage() throws IOException {
        StringBuffer buffer = new StringBuffer();
        int ch;
        while ((ch = input.read()) != '\n' && ch != -1) {
            buffer.append((char) ch);
        }
        return Message.fromXML(buffer.toString());
    }

    public void close() {
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
