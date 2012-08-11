package me.nickcarroll.client;

import me.nickcarroll.message.TransactedMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Client requests for user to specify number of messages to send to Server on port 8080.
 * Three threads are executed.  The first sends the specified number of messages to Server.
 * The second listens on the socket and receives messages.  The messages are added to a
 * BlockingDeque called inbox which is processed by the third thread to display round trip time
 * for a message, average round trip time, and throughput rate.
 */
public class Client {

    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private final int numberOfMessages;

    public Client(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }

    public void start() throws IOException {
        LockingClientSocketWrapper readWriteLockSocket = new LockingClientSocketWrapper(new Socket(HOST, PORT));
        final BlockingDeque<TransactedMessage> inbox = new LinkedBlockingDeque<TransactedMessage>();

        executorService.execute(new MessageSender(readWriteLockSocket, numberOfMessages));
        executorService.execute(new MessageReceiver(readWriteLockSocket, inbox, numberOfMessages));
        executorService.execute(new PerformanceReporter(inbox, numberOfMessages, executorService, readWriteLockSocket));
    }

    public static void main(String[] args) {
        Integer messagesToSend = null;
        while (messagesToSend == null) {
            System.out.print("Enter number of messages to send: ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            try {
                messagesToSend = Integer.valueOf(br.readLine());
            } catch (IOException ioe) {
                System.exit(1);
            } catch (NumberFormatException nfe) {
                System.out.println("Sorry, but you did not enter a valid number.  Try again.");
            }
        }

        Client client = new Client(messagesToSend);
        try {
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
