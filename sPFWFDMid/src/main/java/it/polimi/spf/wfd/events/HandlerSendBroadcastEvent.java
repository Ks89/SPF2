package it.polimi.spf.wfd.events;

import it.polimi.spf.wfd.WfdMessage;
import lombok.Getter;

/**
 * Created by Ks89 on 09/10/15.
 */
public class HandlerSendBroadcastEvent extends Event {

    @Getter
    private WfdMessage message;

    public HandlerSendBroadcastEvent(String type, WfdMessage message) {
        super(type);
        this.message = message;
    }
}
