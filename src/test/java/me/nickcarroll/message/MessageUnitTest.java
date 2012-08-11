package me.nickcarroll.message;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(BlockJUnit4ClassRunner.class)
public class MessageUnitTest {

    @Test
    public void shouldFormatMessageAsSimpleXMLWithMessageIDAndTimestamp() {
        int id = 0;
        Message message = new Message(id, 1268283588142811000L);
        assertThat(message.getAsXML(), equalTo("<message id=\"0\" sentTimestamp=\"1268283588142811000\"/>"));

        id = 100;
        message = new Message(id, 1268283588142864000L);
        assertThat(message.getAsXML(), equalTo("<message id=\"100\" sentTimestamp=\"1268283588142864000\"/>"));
    }

    @Test
    public void shouldCreateMessageFromXML() {
        Message message = Message.fromXML("<message id=\"100\" sentTimestamp=\"1268283588142864000\"/>");
        assertThat(message.getId(), equalTo(100));
        assertThat(message.getSentTimestamp(), equalTo(1268283588142864000L));
        assertThat(message.toString(), equalTo("Message[100,1268283588142864000]"));

        message = Message.fromXML("<message id=\"1000000\" sentTimestamp=\"1268283588142811000\"/>");
        assertThat(message.getId(), equalTo(1000000));
        assertThat(message.getSentTimestamp(), equalTo(1268283588142811000L));
        assertThat(message.toString(), equalTo("Message[1000000,1268283588142811000]"));
    }

    @Test
    public void shouldThrowExceptionIfMessageCannotBeParsedUsingRegularExpression() {
        try {
            Message.fromXML("INVALID MESSAGE");
            // Should not get here!
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), equalTo("Unable to parse message: INVALID MESSAGE"));
        }
    }
}
