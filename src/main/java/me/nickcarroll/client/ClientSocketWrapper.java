package me.nickcarroll.client;

import me.nickcarroll.message.Message;
import me.nickcarroll.message.TransactedMessage;

import java.io.IOException;
import java.util.concurrent.BlockingDeque;

public interface ClientSocketWrapper {

    boolean sendMessage(Message message) throws IOException;

    int receiveMessages(BlockingDeque<TransactedMessage> inbox) throws IOException;

    void close();
}
