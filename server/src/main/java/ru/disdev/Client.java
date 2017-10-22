package ru.disdev;


import io.netty.channel.Channel;

public class Client {

    private final String name;
    private final Channel channel;

    public Client(String name, Channel channel) {
        this.name = name;
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public Channel getChannel() {
        return channel;
    }
}
