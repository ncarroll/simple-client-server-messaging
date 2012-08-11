package me.nickcarroll.client;

import me.nickcarroll.message.Message;
import me.nickcarroll.message.TransactedMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class MessageReceiverUnitTest {

    @Test
    public void shouldListenOnSocketAndReceiveMessages() {
        int numberOfMessagesToReceive = 10;
        ClientSocketWrapper socket = new StubClientSocketWrapper(numberOfMessagesToReceive);
        BlockingDeque<TransactedMessage> inbox = new LinkedBlockingDeque<TransactedMessage>();
        MessageReceiver messageReceiver = new MessageReceiver(socket, inbox, numberOfMessagesToReceive);
        messageReceiver.run();

        assertThat(inbox.size(), equalTo(numberOfMessagesToReceive));
    }

    private class StubClientSocketWrapper implements ClientSocketWrapper {
        private int numberOfMessages;

        public StubClientSocketWrapper(int numberOfMessages) {
            this.numberOfMessages = numberOfMessages;
        }

        public boolean sendMessage(Message message) throws IOException {
            return true;
        }

        public int receiveMessages(BlockingDeque<TransactedMessage> inbox) throws IOException {
            for (int i = 0; i < numberOfMessages; i++) {
                inbox.add(new TransactedMessage(i, System.nanoTime(), System.nanoTime()));
            }
            return inbox.size();
        }

        public void close() {
        }
    }
}
