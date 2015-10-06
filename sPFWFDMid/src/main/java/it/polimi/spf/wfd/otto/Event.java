package it.polimi.spf.wfd.otto;

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
}
