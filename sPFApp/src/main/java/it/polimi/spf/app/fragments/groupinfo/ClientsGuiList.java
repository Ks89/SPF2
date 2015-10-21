package it.polimi.spf.app.fragments.groupinfo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Class that represent a list of Clients as ArrayList of {@link DeviceGuiElement}
 * Created by Stefano Cappa on 21/10/15.
 */
public class ClientsGuiList {

    @Getter
    private final List<DeviceGuiElement> clients;

    private static final ClientsGuiList instance = new ClientsGuiList();

    public static ClientsGuiList get() {
        return instance;
    }

    private ClientsGuiList() {
        clients = new ArrayList<>();
    }


    /**
     * Method to add a client inside the list in a secure way.
     * The client is added only if isn't already inside the list.
     *
     * @param device {@link DeviceGuiElement} to add.
     */
    public void addClientIfNotPresent(DeviceGuiElement device) {
        boolean add = true;
        for (DeviceGuiElement element : clients) {
            if (element != null
                    && element.getName().equals(device.getName())
                    && element.getAddress().equals(device.getAddress())) {
                add = false; //already in the list
            }
        }

        if (add) {
            clients.add(device);
        }
    }
}
