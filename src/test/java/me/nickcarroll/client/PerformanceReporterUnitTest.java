package me.nickcarroll.client;

import me.nickcarroll.message.TransactedMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class PerformanceReporterUnitTest {

    @Test
    public void shouldPollForTransactedMessagesFromTheInboxBlockingDeque() {
        BlockingDeque<TransactedMessage> inbox = mock(BlockingDeque.class);
        ExecutorService executorService = mock(ExecutorService.class);
        ClientSocketWrapper socket = mock(ClientSocketWrapper.class);

        when(inbox.poll()).thenReturn(new TransactedMessage(0, 0, 1000L));

        PerformanceReporter performanceReporter = new PerformanceReporter(inbox, 5, executorService, socket);
        performanceReporter.run();

        verify(inbox, atLeast(5)).poll();
    }
}
