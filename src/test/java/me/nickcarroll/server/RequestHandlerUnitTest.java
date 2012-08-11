package me.nickcarroll.server;

import me.nickcarroll.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InOrder;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class RequestHandlerUnitTest {

    @Test
    public void shouldShutDownIfSocketConnectionWithClientIsClosed() throws IOException {
        ServerSocketWrapper socket = mock(ServerSocketWrapper.class);
        ExecutorService executorService = mock(ExecutorService.class);

        when(socket.isClosed()).thenReturn(true);

        RequestHandler requestHandler = new RequestHandler(socket, executorService);
        requestHandler.run();

        verify(executorService).shutdown();
    }

    @Test
    public void shouldReceiveAndEchoBackAMessage() throws IOException {
        ServerSocketWrapper socket = mock(ServerSocketWrapper.class);
        ExecutorService executorService = mock(ExecutorService.class);

        Message message = Message.fromXML("<message id=\"100\" sentTimestamp=\"1268283588142864000\"/>");

        when(socket.isClosed()).thenReturn(false).thenReturn(true);
        when(socket.receiveMessage()).thenReturn(message);

        RequestHandler requestHandler = new RequestHandler(socket, executorService);
        requestHandler.run();

        InOrder inorder = inOrder(socket, executorService);

        inorder.verify(socket).isClosed();
        inorder.verify(socket).sendMessage(message);
        inorder.verify(socket).isClosed();
        inorder.verify(executorService).shutdown();
    }

    @Test
    public void shouldCloseSocketIfNoMessageReceived() throws IOException {
        ServerSocketWrapper socket = mock(ServerSocketWrapper.class);
        ExecutorService executorService = mock(ExecutorService.class);

        when(socket.isClosed()).thenReturn(false).thenReturn(true);
        when(socket.receiveMessage()).thenReturn(null);

        RequestHandler requestHandler = new RequestHandler(socket, executorService);
        requestHandler.run();

        InOrder inorder = inOrder(socket, executorService);

        inorder.verify(socket).isClosed();
        inorder.verify(socket).close();
        inorder.verify(socket).isClosed();
        inorder.verify(executorService).shutdown();
    }

    @Test
    public void shouldShutDownExecutorServiceIfIOExceptionIsThrown() throws IOException {
        ServerSocketWrapper socket = mock(ServerSocketWrapper.class);
        ExecutorService executorService = mock(ExecutorService.class);

        when(socket.isClosed()).thenReturn(false);
        when(socket.receiveMessage()).thenThrow(new IOException("Connection unavailable!"));

        RequestHandler requestHandler = new RequestHandler(socket, executorService);
        requestHandler.run();

        verify(executorService).shutdown();
    }
}
