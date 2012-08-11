package me.nickcarroll.server;


import me.nickcarroll.message.Message;

import java.io.*;
import java.net.Socket;

public class SimpleServerSocketWrapper implements ServerSocketWrapper {

    private final Socket socket;
    private final BufferedReader input;
    private final PrintWriter output;

    public SimpleServerSocketWrapper(Socket socket) throws IOException {
        this.socket = socket;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

    }

    public void sendMessage(Message message) {
        output.println(message.getAsXML());
        output.flush();
    }

    public Message receiveMessage() throws IOException {
        String xml;
        if ((xml = input.readLine()) != null) {
            return Message.fromXML(xml);
        }
        return null;
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public void close() throws IOException {
        socket.close();
    }
}
