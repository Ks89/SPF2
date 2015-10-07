package it.polimi.spf.wfd.events.goEvent;

import it.polimi.spf.wfd.events.Event;

/**
 * Created by Stefano Cappa on 22/07/15.
 */
abstract class GOEvent extends Event {
    public GOEvent(String type) {
        super(type);
    }
}
