package it.polimi.spf.wfd.events.goEvent;

/**
 * Created by Stefano Cappa on 22/07/15.
 */
public class GOConnectionEvent extends GOEvent {

    public final static String CONNECT_STRING = "CONNECT";
    public final static String DISCONNECT_STRING = "DISCONNECT";
    public final static String UNKNOWN_STRING = "UNKNOWN";

    public enum Connection {
        CONNECT(CONNECT_STRING),
        DISCONNECT(DISCONNECT_STRING),
        UNKNOWN(UNKNOWN_STRING);

        private final String text;

        Connection(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public GOConnectionEvent(String type) {
        super(type);
    }

    public String getAction() {
        return Connection.valueOf(super.getType()).text;
    }
}
