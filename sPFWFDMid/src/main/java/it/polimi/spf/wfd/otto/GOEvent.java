package it.polimi.spf.wfd.otto;

import lombok.Getter;

/**
 * Created by Stefano Cappa on 22/07/15.
 */
public class GOEvent {
    @Getter
    public String type;

    public GOEvent(String type) {
        this.type = type;
    }
}
