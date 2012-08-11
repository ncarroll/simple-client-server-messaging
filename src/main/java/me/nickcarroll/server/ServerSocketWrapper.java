package me.nickcarroll.server;

import me.nickcarroll.message.Message;

import java.io.IOException;

public interface ServerSocketWrapper {

    void sendMessage(Message message);

    Message receiveMessage() throws IOException;

    boolean isClosed();

    void close() throws IOException;
}
