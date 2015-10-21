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

package it.polimi.spf.wfd.listeners;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import it.polimi.spf.wfd.Configuration;
import it.polimi.spf.wfd.ServiceList;
import it.polimi.spf.wfd.WiFiP2pService;
import it.polimi.spf.wfd.WifiDirectMiddleware;
import it.polimi.spf.wfd.util.WfdLog;

/**
 * A custom Bonjour's DnsSdServiceResponseListener used to update the UI when a service is available.
 * <p></p>
 * This class uses Bonjour Protocol
 * <p></p>
 * Created by Stefano Cappa on 16/07/15.
 */
public class CustomDnsServiceResponseListener implements WifiP2pManager.DnsSdServiceResponseListener {
    private static final String TAG = CustomDnsServiceResponseListener.class.getSimpleName();
    private WifiDirectMiddleware wifiDirectMiddleware;

    /**
     * CustomDnsServiceResponseListener constructor
     *
     * @param wifiDirectMiddleware
     */
    public CustomDnsServiceResponseListener(WifiDirectMiddleware wifiDirectMiddleware) {
        this.wifiDirectMiddleware = wifiDirectMiddleware;
    }

    @Override
    public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
        // A service has been discovered. Is this our app?
        Log.d(TAG, "onDnsSdServiceAvailable, instanceName:" + instanceName + ", registrationType:" + registrationType + ",srcDevice:" + srcDevice);
        if (instanceName.startsWith(Configuration.SERVICE_INSTANCE)) {
            //services are added in CustomDnsSdTxtRecordListener
            //and here are updated with device name and other informations.
            WiFiP2pService service = ServiceList.getInstance().getServiceByDeviceAddress(srcDevice.deviceAddress);
            //Indeed, Here i'm updating the device with the human-friendly version from the DnsTxtRecord, assuming one arrived.
            if (service != null) {
                service.setDevice(srcDevice);
                //And i also update this additional informations
                service.setInstanceName(instanceName);
                service.setServiceRegistrationType(registrationType);

                wifiDirectMiddleware.notifyServiceToGui(WifiDirectMiddleware.SERVICES_ADD, service);
            }
            Log.d(TAG, "onDnsSdServiceAvailable " + instanceName);

            if (!wifiDirectMiddleware.isGroupCreated() && !wifiDirectMiddleware.isAutonomous()) {
                WfdLog.d(TAG, "createGroup: onDnsSdServiceAvailable");
                wifiDirectMiddleware.createGroup();
            }
        }
    }
}
