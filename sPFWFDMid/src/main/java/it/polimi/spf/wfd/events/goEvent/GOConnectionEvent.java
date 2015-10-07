package it.polimi.spf.wfd.events.goEvent;

/**
 * Created by Stefano Cappa on 22/07/15.
 */
public class GOConnectionEvent extends GOEvent {

    public final static String CONNECTED = "CONNECTED";
    public final static String DISCONNECTED = "DISCONNECTED";

    public GOConnectionEvent(String type) {
        super(type);
    }

    public String getStatus() {
        return super.getType();
    }
}
