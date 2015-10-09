package it.polimi.spf.wfd.events;

import lombok.Getter;

/**
 * Created by Stefano Cappa on 22/07/15.
 */
public abstract class Event {
    @Getter
    private String type;

    public Event(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.getType() + "";
    }
}
