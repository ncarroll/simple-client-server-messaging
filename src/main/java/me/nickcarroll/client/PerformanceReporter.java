package me.nickcarroll.client;

import me.nickcarroll.message.TransactedMessage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

/**
 * Performance reporter displays performance statistics to console.  Statistics include
 * round trip time, average round trip time, and throughput rate.
 */
public class PerformanceReporter implements Runnable {

    private static Logger logger = Logger.getLogger(PerformanceReporter.class.getName());

    private final BlockingDeque<TransactedMessage> inbox;
    private final int numberOfMessages;
    private ExecutorService executorService;
    private ClientSocketWrapper socket;
    private Long averageRoundTripTimeInNanos;

    private Long startTime;

    public PerformanceReporter(BlockingDeque<TransactedMessage> inbox,
                               int numberOfMessages,
                               ExecutorService executorService,
                               ClientSocketWrapper socket) {
        this.inbox = inbox;
        this.numberOfMessages = numberOfMessages;
        this.executorService = executorService;
        this.socket = socket;
    }

    public void run() {
        int messagesReceived = 0;
        while (messagesReceived < numberOfMessages) {
            TransactedMessage transactedMessage = inbox.poll();
            if (transactedMessage != null) {
                if (messagesReceived == 0) {
                    startTime = transactedMessage.getSentTimestamp();
                }
                messagesReceived++;
                logRoundTripTime(transactedMessage);
                logAverageRoundTripTime(transactedMessage);
                logThroughputRate(messagesReceived, transactedMessage);
            }
        }
        logger.info("Received: " + messagesReceived + " messages");
        shutdown();
    }

    /**
     * Round trip time for current message being processed.
     *
     * @param transactedMessage Performace statistics for the current message being processed.
     */
    private void logRoundTripTime(TransactedMessage transactedMessage) {
        logger.info("Round trip time for message[" + transactedMessage.getMessageId() + "]: "
                + transactedMessage.getRoundTripTimeInNanos() + " ns");
    }

    /**
     * Average round trip time for number of messages received.
     *
     * @param transactedMessage Performace statistics for the current message being processed.
     */
    private void logAverageRoundTripTime(TransactedMessage transactedMessage) {
        if (averageRoundTripTimeInNanos == null) {
            averageRoundTripTimeInNanos = transactedMessage.getRoundTripTimeInNanos();
        } else {
            averageRoundTripTimeInNanos = (averageRoundTripTimeInNanos + transactedMessage.getRoundTripTimeInNanos()) / 2;
        }
        logger.info("Average round trip time: " + averageRoundTripTimeInNanos + " ns");
    }

    /**
     * Throughput rate is calculated as the number of messages received divided by the time between
     * the first message received and the current message being processed.
     *
     * @param messagesReceived  Current number of messages received.
     * @param transactedMessage Performance statistics for a specific message.
     */
    private void logThroughputRate(int messagesReceived, TransactedMessage transactedMessage) {
        long duration = transactedMessage.getReceivedTimestamp() - startTime;
        BigDecimal durationInSeconds = new BigDecimal(duration)
                .divide(new BigDecimal("1000000000"), 6, RoundingMode.HALF_UP);

        BigDecimal throughputRate = new BigDecimal(messagesReceived)
                .divide(durationInSeconds, 0, RoundingMode.HALF_UP);

        logger.info("Running throughput rate: " + throughputRate + " transactions/sec");
    }

    /**
     * Close socket and shutdown executor service.
     */
    private void shutdown() {
        socket.close();
        executorService.shutdown();
    }
}
