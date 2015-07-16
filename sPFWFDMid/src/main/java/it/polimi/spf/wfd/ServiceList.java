/*
 * Copyright 2015 Stefano Cappa
 *
 * This file is part of SPF.
 *
 * SPF is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * SPF is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SPF.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package it.polimi.spf.wfd;

import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Class that represents a list of {@link it.polimi.spf.wfd.WiFiP2pService }.
 * This list contains all the device found during discovery phase of the wifi direct protocol.
 * This class use Singleton pattern.
 * <p></p>
 * Created by Stefano Cappa on 07/08/15.
 */
public class ServiceList {

    @Getter private final List<WiFiP2pService> serviceList;

    private static final ServiceList instance = new ServiceList();

    /**
     * Method to get the instance of this class.
     * @return instance of this class.
     */
    public static ServiceList getInstance() {
        return instance;
    }

    /**
     * Private constructor, because is a singleton class.
     */
    private ServiceList() {
        serviceList = new ArrayList<>();
    }


    public boolean containsService(WifiP2pDevice device) {
        for(WiFiP2pService serv : serviceList) {
            if(serv.getDevice().equals(device)) {
                return true;
            }
        }
        return false;
    }

    public WiFiP2pService getServiceByDevice(WifiP2pDevice device) {
        Log.d("ServiceList", "device passed: " + device.deviceName + ", " + device.deviceAddress);
        for(WiFiP2pService serv : serviceList) {
            Log.d("ServiceList", "element in list: " + serv.getDevice().deviceName + ", " + serv.getDevice().deviceAddress);
            if(serv.getDevice().equals(device)) {
                return serv;
            }
        }
        return null;
    }

    public WiFiP2pService getServiceByAddress(String address) {
        for(WiFiP2pService serv : serviceList) {
            if(serv.getPeerAddress().equals(address)) {
                return serv;
            }
        }
        return null;
    }
}