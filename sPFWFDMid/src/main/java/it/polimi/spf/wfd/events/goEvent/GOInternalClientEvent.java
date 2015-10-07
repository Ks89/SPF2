package it.polimi.spf.wfd.events.goEvent;

import java.net.Socket;

import lombok.Getter;

/**
 * Created by Stefano Cappa on 22/07/15.
 */
public class GOInternalClientEvent extends GOEvent {
    @Getter
    public Socket socket;

    public GOInternalClientEvent(String type, Socket socket) {
        super(type);
        this.socket = socket;
    }
}
