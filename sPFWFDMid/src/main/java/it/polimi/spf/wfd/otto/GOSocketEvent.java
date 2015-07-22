package it.polimi.spf.wfd.otto;

import java.net.Socket;

import lombok.Getter;

/**
 * Created by Stefano Cappa on 22/07/15.
 */
public class GOSocketEvent {
    @Getter public String type;
    @Getter public Socket socket;

    public GOSocketEvent(String type, Socket socket) {
        this.type = type;
        this.socket = socket;
    }
}
