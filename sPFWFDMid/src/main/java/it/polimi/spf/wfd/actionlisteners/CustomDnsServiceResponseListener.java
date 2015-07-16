///*
// * Copyright 2015 Stefano Cappa
// *
// * This file is part of SPF.
// *
// * SPF is free software: you can redistribute it and/or modify it under the
// * terms of the GNU Lesser General Public License as published by the Free
// * Software Foundation, either version 3 of the License, or (at your option)
// * any later version.
// *
// * SPF is distributed in the hope that it will be useful, but WITHOUT ANY
// * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
// * more details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with SPF.  If not, see <http://www.gnu.org/licenses/>.
// *
// */
//
//package it.polimi.spf.wfd.actionlisteners;
//
//import android.net.wifi.p2p.WifiP2pDevice;
//import android.net.wifi.p2p.WifiP2pManager;
//import android.util.Log;
//
//import it.polimi.spf.wfd.Configuration;
//import it.polimi.spf.wfd.WfdLog;
//
///**
// * A custom Bonjour's DnsSdServiceResponseListener used to update the UI when a service is available.
// * <p></p>
// * This class use Bonjour Prot
// * <p></p>
// * Created by Stefano Cappa on 16/07/15.
// */
//public class CustomDnsServiceResponseListener implements WifiP2pManager.DnsSdServiceResponseListener {
//
//    private static final String TAG = "DnsRespListener";
//
//    @Override
//    public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
//        // A service has been discovered. Is this our app?
//        WfdLog.d(TAG, "onDnsSdServiceAvailable ServiceResponseAvailable: " + instanceName + ", regType: " +
//                registrationType + ", device: " + srcDevice);
//
////        if (instanceName.equalsIgnoreCase(Configuration.SERVICE_INSTANCE)) {
////
////            // update the UI and add the item the discovered device.
////            WiFiP2pServicesFragment fragment = TabFragment.getWiFiP2pServicesFragment();
////            if (fragment != null) {
////                WiFiServicesAdapter adapter = fragment.getMAdapter();
////                WiFiP2pService service = new WiFiP2pService();
////                service.setDevice(srcDevice);
////                service.setInstanceName(instanceName);
////                service.setServiceRegistrationType(registrationType);
////
////                ServiceList.getInstance().addServiceIfNotPresent(service);
////
////                if (adapter != null) {
////                    adapter.notifyItemInserted(ServiceList.getInstance().getSize() - 1);
////                }
////                Log.d(TAG, "onDnsSdServiceAvailable " + instanceName);
////            }
////        }
//    }
//}
