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

import java.util.ArrayList;
import java.util.List;

import it.polimi.spf.wfd.util.WfdLog;
import lombok.Getter;

/**
 * Class that represents a list of {@link it.polimi.spf.wfd.WiFiP2pService }.
 * This list contains all the device found during discovery phase of the wifi direct protocol.
 * This class use Singleton pattern.
 * <p></p>
 * Created by Stefano Cappa on 07/08/15.
 */
public class ServiceList {
    private static final String TAG = ServiceList.class.getSimpleName();
    private static final String AP_APPENDIX = "AP"; //or GO in case of Wifi direct
    private static final String SLAVE_APPENDIX = "U"; //or client in case of wifi direct

    @Getter
    private final List<WiFiP2pService> serviceList;

    private static final ServiceList instance = new ServiceList();

    /**
     * Method to get the instance of this class.
     *
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


    /**
     * Method to add a service inside the list in a secure way.
     * The service is added only if isn't already inside the list.
     *
     * @param service {@link WiFiP2pService} to add.
     */
    public void addServiceIfNotPresent(WiFiP2pService service) {
        WfdLog.d(TAG, "addServiceIfNotPresent BEGIN, with size = " + serviceList.size());

        if (service == null) {
            return;
        }

        boolean add = true;
        for (WiFiP2pService element : serviceList) {
            if (element.getDevice().equals(service.getDevice())
                    && element.getInstanceName().equals(service.getInstanceName())) {
                add = false; //already in the list
            }
        }

        if (add) {
            serviceList.add(service);
        }

        WfdLog.d(TAG, "addServiceIfNotPresent END, with size = " + serviceList.size());
    }

    /**
     * Method to get a service from the list, using only the device.
     * This method use only the deviceAddress, not the device name, because sometimes Android doesn't
     * get the name, but only the mac address.
     *
     * @param device WifiP2pDevice that you want to use to search the service.
     * @return The WiFiP2pService associated to the device or null, if the device isn't in the list.
     */
    public WiFiP2pService getServiceByDevice(WifiP2pDevice device) {
        if (device == null) {
            return null;
        }

        WfdLog.d(TAG, "groupownerdevice passed to getServiceByDevice: " + device.deviceName + ", " + device.deviceAddress);

        WfdLog.d(TAG, "servicelist size: " + serviceList.size());

        for (WiFiP2pService element : serviceList) {
            WfdLog.d(TAG, "element in list: " + element.getDevice().deviceName + ", " + element.getDevice().deviceAddress);
            WfdLog.d(TAG, "element passed : " + device.deviceName + ", " + device.deviceAddress);

            if (element.getDevice().deviceAddress.equals(device.deviceAddress)) {
                WfdLog.d(TAG, "getServiceByDevice if satisfied : " + device.deviceAddress + ", " + element.getDevice().deviceAddress);
                return element;
            }
        }

        WfdLog.d(TAG, "servicelist size: " + serviceList.size());

        return null;
    }

    public List<WiFiP2pService> selectValidServices(String myIdentifier) {
        List<WiFiP2pService> validServiceList = new ArrayList<>();
        for (WiFiP2pService service : ServiceList.getInstance().getServiceList()) {
            if (service != null && service.getPort() != WiFiP2pService.INVALID
                    && service.getIdentifier() != null && !service.getIdentifier().equals(myIdentifier)) {

                validServiceList.add(service);

                WfdLog.d(TAG, "--> ValidServiceList: --OK --: " + service.getIdentifier() + "," + service.getPeerAddress());
            } else {
                if (service != null) {
                    WfdLog.d(TAG, "--> ValidServiceList: --NOT--: " + service.getIdentifier() + "," + service.getPeerAddress());
                }
            }
        }
        return validServiceList;
    }

    public List<WiFiP2pService> getValidGroupOwners(String myIdentifier) {
        List<WiFiP2pService> validServiceList = this.selectValidServices(myIdentifier);
        List<WiFiP2pService> validGoList = new ArrayList<>();
        for (WiFiP2pService service : validServiceList) {
            if (service.getIdentifier().startsWith(AP_APPENDIX)) {
                WfdLog.d(TAG, "--> ValidGroupOwnersList: --OK --: " + service.getIdentifier() + "," + service.getPeerAddress());
                validGoList.add(service);
            }
        }
        return validGoList;
    }

    public List<WiFiP2pService> getValidClients(String myIdentifier) {
        List<WiFiP2pService> validServiceList = this.selectValidServices(myIdentifier);
        List<WiFiP2pService> validGoList = new ArrayList<>();
        for (WiFiP2pService service : validServiceList) {
            if (!service.getIdentifier().startsWith(AP_APPENDIX)) {
                WfdLog.d(TAG, "--> ValidClientsList: --OK --: " + service.getIdentifier() + "," + service.getPeerAddress());
                validGoList.add(service);
            }
        }
        return validGoList;
    }

    public boolean containsDevice(WifiP2pDevice device) {
        for (WiFiP2pService serv : serviceList) {
            if (serv.getDevice().deviceAddress.equals(device.deviceAddress)) {
                return true;
            }
        }
        return false;
    }

    public WiFiP2pService getServiceByDeviceAddress(String deviceAddress) {
        for (WiFiP2pService serv : serviceList) {
            if (serv.getDevice().deviceAddress.equals(deviceAddress)) {
                return serv;
            }
        }
        return null;
    }
}