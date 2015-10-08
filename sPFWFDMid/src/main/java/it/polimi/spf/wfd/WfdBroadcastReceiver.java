/*
 * Copyright 2014 Jacopo Aliprandi, Dario Archetti
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

import it.polimi.spf.wfd.events.MidConnectionEvent;
import it.polimi.spf.wfd.events.NineBus;
import it.polimi.spf.wfd.util.WfdLog;

class WfdBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = WfdBroadcastReceiver.class.getSimpleName();
    static final String P2P_ENABLED = "P2P_ENABLED";
    static final String P2P_DISABLED = "P2P_DISABLED";
    static final String PEERS_CHANGED = "PEERS_CHANGED";
    static final String NETWORK_CONNECTED = "NETWORK_CONNECTED";
    static final String NETWORK_DISCONNECTED = "NETWORK_DISCONNECTED";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                this.onP2pStateChanged(intent);
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                this.onPeersChanged(intent);
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                this.onConnectionChanged(intent);
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                this.onThisDeviceChanged(intent);
                break;
            default:
                break;
        }
    }

    private void onP2pStateChanged(Intent intent) {
        int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        WfdLog.d(TAG, "P2P state changed - " + state);
        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            WfdLog.d(TAG, "Wi-Fi Direct Enabled");
            NineBus.get().post(new MidConnectionEvent(P2P_ENABLED));
        } else {
            WfdLog.e(TAG, "Wi-Fi Direct Disabled");
            NineBus.get().post(new MidConnectionEvent(P2P_DISABLED));
        }
    }

    private void onPeersChanged(Intent intent) {
        WfdLog.d(TAG, "P2P peers changed");
        NineBus.get().post(new MidConnectionEvent(PEERS_CHANGED));
    }

    private void onConnectionChanged(Intent intent) {
        //call on connection info received
        NetworkInfo netInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        if (netInfo.isConnected()) {
            // It's a connect
            NineBus.get().post(new MidConnectionEvent(NETWORK_CONNECTED));
        } else {
            // It's a disconnect
            NineBus.get().post(new MidConnectionEvent(NETWORK_DISCONNECTED));
        }
    }

    private void onThisDeviceChanged(Intent intent) {
        WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
        WfdLog.d(TAG, "Device status - " + device.status);
    }

    public void register(Context mContext) {
        IntentFilter intentFilter = new IntentFilter();

        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mContext.registerReceiver(this, intentFilter);
    }

    public void unregister(Context mContext) {
        mContext.unregisterReceiver(this);
    }
}
