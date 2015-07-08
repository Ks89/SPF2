/*
Copyright 2015 Stefano Cappa, Politecnico di Milano

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package it.polimi.spf.wfd;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Class that represents a list of {@link it.polimi.spf.wfd.WiFiP2pService }.
 * This list contains all the device found during discovery phase of the wifi direct protocol.
 * This class use Singleton pattern.
 * <p></p>
 * Created by Stefano Cappa on 04/02/15.
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
        for(WiFiP2pService serv : serviceList) {
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