package it.polimi.spf.app.fragments.groupinfo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Class that represent a list of Services as ArrayList of {@link DeviceGuiElement}
 * Created by Stefano Cappa on 21/10/15.
 */
public class ServicesGuiList {

    @Getter
    private final List<DeviceGuiElement> services;

    private static final ServicesGuiList instance = new ServicesGuiList();

    public static ServicesGuiList get() {
        return instance;
    }

    private ServicesGuiList() {
        services = new ArrayList<>();
    }

}
