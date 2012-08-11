package me.nickcarroll.message;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class TransactedMessageUnitTest {

    @Test
    public void shouldCalculateTotalRoundTripTimeAsDifferenceBetweenSentAndReceivedTimestamps() {
        int id = 0;
        long sentTimestamp = 1268283588142811000L;
        long receivedTimestamp = 1268283588142864000L;

        TransactedMessage transactedMessage = new TransactedMessage(id, sentTimestamp, receivedTimestamp);
        assertThat(transactedMessage.getMessageId(), equalTo(id));
        assertThat(transactedMessage.getRoundTripTimeInNanos(), equalTo(receivedTimestamp - sentTimestamp));
        assertThat(transactedMessage.getSentTimestamp(), equalTo(sentTimestamp));
        assertThat(transactedMessage.getReceivedTimestamp(), equalTo(receivedTimestamp));
        assertThat(transactedMessage.toString(), equalTo("TransactedMessage[0,1268283588142811000,1268283588142864000]"));

        id = 1;
        sentTimestamp = 1268284212819680000L;
        receivedTimestamp = 1268284212819733000L;

        transactedMessage = new TransactedMessage(id, sentTimestamp, receivedTimestamp);
        assertThat(transactedMessage.getMessageId(), equalTo(id));
        assertThat(transactedMessage.getRoundTripTimeInNanos(), equalTo(receivedTimestamp - sentTimestamp));
        assertThat(transactedMessage.getSentTimestamp(), equalTo(sentTimestamp));
        assertThat(transactedMessage.getReceivedTimestamp(), equalTo(receivedTimestamp));
        assertThat(transactedMessage.toString(), equalTo("TransactedMessage[1,1268284212819680000,1268284212819733000]"));
    }
}
