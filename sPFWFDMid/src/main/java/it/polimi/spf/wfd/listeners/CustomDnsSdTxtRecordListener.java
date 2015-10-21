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

import java.util.Map;

import it.polimi.spf.wfd.Configuration;
import it.polimi.spf.wfd.ServiceList;
import it.polimi.spf.wfd.WiFiP2pService;
import it.polimi.spf.wfd.util.WfdLog;

/**
 * A custom CustomDnsSdTxtRecordListener.
 * <p></p>
 * Created by Stefano Cappa on 16/07/15.
 */
public class CustomDnsSdTxtRecordListener implements WifiP2pManager.DnsSdTxtRecordListener {

    private static final String TAG = "DnsSdRecordListener";

    @Override
    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        WfdLog.d(TAG, "DnsSdTxtRecord available:\n\n" + fullDomainName + "\n\n" + txtRecordMap + "\n\n" + srcDevice);
        Log.d(TAG, "onDnsSdTxtRecordAvail: " + srcDevice.deviceName + " is " +
                txtRecordMap.get(Configuration.TXTRECORD_PROP_AVAILABLE));

        String identifier = txtRecordMap.get(Configuration.IDENTIFIER);
        String portString = txtRecordMap.get(Configuration.PORT);

        WiFiP2pService service = new WiFiP2pService();
        service.setDevice(srcDevice);
        service.setPeerAddress(srcDevice.deviceAddress);

        int port;
        try {
            port = Integer.parseInt(portString);
            service.setPort(port);
        } catch (Throwable t) {
            return;
        }

        service.setIdentifier(identifier);

        ServiceList.getInstance().addServiceIfNotPresent(service);
    }
}
