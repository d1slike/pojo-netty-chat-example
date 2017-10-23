package ru.disdev;


import io.netty.channel.ChannelId;
import ru.disdev.commons.transport.Server;
import ru.disdev.packets.Disconnection;
import ru.disdev.packets.Message;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    private static final Map<ChannelId, Client> clientsMap = new ConcurrentHashMap<>();

    public static void main(String... args) {
        Charset charset = Charset.forName("UTF-16LE");
        Server server = Server.builder()
                .stringCharset(charset)
                .useLE(true)
                .port(1234)
                .onException((channel, throwable) -> throwable.printStackTrace())
                .notMappedDataFunction(byteBuf -> {
                    int length = byteBuf.readIntLE();
                    return byteBuf.readCharSequence(length, charset).toString();
                })
                .onNotMappedData((channel, o) -> {
                    if (o instanceof String) {
                        Client client = new Client(o.toString(), channel);
                        Message message =
                                formatMessage("System", client.getName() + " подключился");
                        broadcast(message);
                        clientsMap.put(channel.id(), client);
                        log(client.getName() + " connected!");
                    }
                }).build();

        server.subscribe(Message.class, (channel, message) -> {
            Client client = clientsMap.get(channel.id());
            if (client != null) {
                broadcast(formatMessage(client.getName(), message.getMessage()));
                log(client.getName() + " : " + message.getMessage());
            }
        }).subscribe(Disconnection.class, (channel, disconnection) -> {
            Client client = clientsMap.get(channel.id());
            if (client != null) {
                Message message =
                        formatMessage("System", client.getName() + " отключился =(");
                clientsMap.remove(channel.id());
                broadcast(message);
                channel.disconnect();
                log(client.getName() + " disconnected!");
            }
        }).start();
    }

    private static void log(String log) {
        System.out.println(log);
    }

    private static Message formatMessage(String sender, String content) {
        Message message = new Message();
        message.setMessage(sender + "%&%" + content);
        return message;
    }

    private static void broadcast(Object object) {
        clientsMap.forEach((key, c) -> c.getChannel().writeAndFlush(object));
    }
}
