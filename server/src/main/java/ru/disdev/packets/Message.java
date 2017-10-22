package ru.disdev.packets;

import ru.disdev.commons.Key;
import ru.disdev.commons.Packet;

@Key(0)
public class Message extends Packet {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
