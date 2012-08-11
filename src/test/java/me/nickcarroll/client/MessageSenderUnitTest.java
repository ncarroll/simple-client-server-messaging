package me.nickcarroll.client;

import me.nickcarroll.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class MessageSenderUnitTest {

    @Test
    public void shouldTryAndSendAMessageAgainIfMessageCouldNotBeSent() throws IOException {
        LockingClientSocketWrapper readWriteLockSocket = mock(LockingClientSocketWrapper.class);
        int numberOfMessagesToSend = 3;

        when(readWriteLockSocket.sendMessage(any(Message.class))).thenReturn(true);
        when(readWriteLockSocket.sendMessage(any(Message.class))).thenReturn(false);
        when(readWriteLockSocket.sendMessage(any(Message.class))).thenReturn(true);
        when(readWriteLockSocket.sendMessage(any(Message.class))).thenReturn(true);

        MessageSender messageSender = new MessageSender(readWriteLockSocket, numberOfMessagesToSend);
        messageSender.run();

        verify(readWriteLockSocket, atLeast(numberOfMessagesToSend)).sendMessage(any(Message.class));
    }
}
