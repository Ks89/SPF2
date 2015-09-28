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

import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.spf.wfd.actionlisteners.CustomDnsSdTxtRecordListener;
import it.polimi.spf.wfd.actionlisteners.CustomDnsServiceResponseListener;
import it.polimi.spf.wfd.actionlisteners.CustomizableActionListener;
import lombok.Getter;

/**
 * WifiDirectMiddleware class provides a coordination layer that handles the discovery of peer,
 * the creation of the P2P Group and mediates the communication to the upper layer of the system.
 * In particular, this last function is made with a series of adapters whose aim is to adapt the
 * Wi-Fi Direct middleware interface to the ones required by SPF.
 */
public class WifiDirectMiddleware implements WifiP2pManager.ConnectionInfoListener,
        CustomDnsServiceResponseListener.CallbackToMiddleware {

    private final static String TAG = WifiDirectMiddleware.class.getSimpleName();

    private final Context mContext;
    private final WfdMiddlewareListener mListener;
    private final WfdBroadcastReceiver mReceiver = new WfdBroadcastReceiver(this);

    private WifiP2pManager mManager;
    private Channel mChannel;
    private WifiP2pDnsSdServiceRequest mServiceRequest;
    private WifiP2pDnsSdServiceInfo mInfo;

    @Getter
    private boolean connected = false;
    private boolean isGroupCreated = false;
    private final String myIdentifier;

    private int goIntent;

    private GroupActor mGroupActor;
    private ServerSocket mServerSocket;
    private int mPort;

    public WifiDirectMiddleware(int goIntentFromSPFApp, Context context, String identifier, WfdMiddlewareListener listener) {
        Log.d(TAG, "WifiDirectMiddleware with goIntentFromSPFApp: " + goIntentFromSPFApp);

        this.mContext = context;
        this.mListener = listener;
        this.myIdentifier = identifier;
        this.goIntent = goIntentFromSPFApp;
    }

    public void connect() {
        try {
            mServerSocket = new ServerSocket(0);
        } catch (IOException e) {
            mListener.onError();
            return;
        }
        mPort = mServerSocket.getLocalPort();
        mReceiver.register(mContext);
        mManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(mContext, Looper.getMainLooper(), null);


        this.startRegistration();
        this.discoverService();


        connected = true;
    }

    /**
     * Registers a local service.
     */
    private void startRegistration() {
        //  Create a string map containing information about your service.
        Map<String, String> mRecordMap = new HashMap<>();
        mRecordMap.put(Configuration.PORT, Integer.toString(mPort));
        mRecordMap.put(Configuration.IDENTIFIER, myIdentifier);

        // Service information.  Pass it an instance name, service type
        // Configuration.SERVICE_REG_TYPE , and the map containing
        // information other devices will want once they connect to this one.
        mInfo = WifiP2pDnsSdServiceInfo.newInstance(Configuration.SERVICE_INSTANCE + myIdentifier,
                Configuration.SERVICE_REG_TYPE, mRecordMap);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.addLocalService(mChannel, mInfo,
                new CustomizableActionListener(
                        this.mContext,
                        "startRegistration",
                        "Added Local Service",
                        null,
                        "Failed to add a service",
                        "Failed to add a service",
                        true)); //important: sets true to get detailed message when this method fails

    }

    /**
     * Method to discover services and put the results
     * in {@link it.polimi.spf.wfd.ServiceList}.
     * This method updates also the discovery menu item.
     */
    private void discoverService() {

		/*
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */
        mManager.setDnsSdResponseListeners(mChannel,
                new CustomDnsServiceResponseListener(this), new CustomDnsSdTxtRecordListener());

        // After attaching listeners, create a service request and initiate
        // discovery.
        mServiceRequest = WifiP2pDnsSdServiceRequest.newInstance();

        //initiates discovery
        mManager.addServiceRequest(mChannel, mServiceRequest,
                new CustomizableActionListener(
                        this.mContext,
                        "discoverService",
                        "Added service discovery request",
                        null,
                        "Failed adding service discovery request",
                        "Failed adding service discovery request",
                        true)); //important: sets true to get detailed message when this method fails

        //starts services discovery
        mManager.discoverServices(mChannel,
                new CustomizableActionListener(
                        this.mContext,
                        "discoverService",
                        "Service discovery initiated",
                        null,
                        "Failed starting service discovery",
                        "Failed starting service discovery",
                        true)); //important: sets true to get detailed message when this method fails
    }


    public void disconnect() {
        mReceiver.unregister();

        mManager.removeServiceRequest(mChannel, mServiceRequest, new CustomizableActionListener(
                this.mContext,
                "disconnect",
                "RemoveServiceRequest success",
                null,
                "RemoveServiceRequest failure",
                "RemoveServiceRequest failure",
                false)); //important: sets false if you don't want detailed messages when this method fails


        mManager.removeLocalService(mChannel, mInfo, new CustomizableActionListener(
                this.mContext,
                "disconnect",
                "RemoveLocalService success",
                null,
                "RemoveLocalService failure",
                "RemoveLocalService failure",
                false)); //important: sets false if you don't want detailed messages when this method fails


        try {
            mServerSocket.close();
        } catch (IOException e) {
            WfdLog.d(TAG, "IOException when closing server socket", e);
        }

        mManager.cancelConnect(mChannel, new CustomizableActionListener(
                this.mContext,
                "disconnect",
                "CancelConnect success",
                null,
                "CancelConnect failure",
                "CancelConnect failure",
                false)); //important: sets false if you don't want detailed messages when this method fails

        mManager.removeGroup(mChannel, new CustomizableActionListener(
                this.mContext,
                "disconnect",
                "RemoveGroup success",
                null,
                "RemoveGroup failure",
                "RemoveGroup failure",
                true)); //important: sets true to get detailed message when this method fails

        connected = false;

        if (mGroupActor != null) {
            mGroupActor.disconnect();
            mGroupActor = null;
        }

        ServiceList.getInstance().getServiceList().clear();

    }


    private void createGroup() {
        WfdLog.d(TAG, "createGroup()");
        if (isGroupCreated || !connected) {
            WfdLog.d(TAG, "group already created or middleware not started");
            return;
        }
        WfdLog.d(TAG, "attempt to create group");
        WifiP2pConfig config = new WifiP2pConfig();
        String deviceAddress = selectDeviceAddess();
        if (deviceAddress == null) {
            WfdLog.d(TAG, "no device address eligible for connection");
            return;
        }
        WfdLog.d(TAG, "connect target device found, device address: " + deviceAddress);
        config.deviceAddress = deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = this.goIntent;

        Log.d(TAG, "Group with config.groupOwnerIntent= " + config.groupOwnerIntent);

        isGroupCreated = true;
        mManager.connect(mChannel, config, new CustomizableActionListener(
                this.mContext,
                "createGroup",
                "Connect success",
                null,
                "Connect failure",
                "Connect failure",
                true)); //important: sets true to get detailed message when this method fails
    }

    private String selectDeviceAddess() {
        String eligibleAddress = null;
        String eligibleIdentifier = null;


        List<WiFiP2pService> serviceList = ServiceList.getInstance().getServiceList();

        for (WiFiP2pService service : serviceList) {
            if (service.getPort() != WiFiP2pService.INVALID && service.getIdentifier() != null) {
                if (service.getDevice() != null && service.getDevice().isGroupOwner()) {
                    return service.getPeerAddress();
                }

                String eligibleId = service.getIdentifier();
                if (!eligibleId.equals(myIdentifier)) {
                    if (eligibleIdentifier != null && eligibleId.compareTo(eligibleIdentifier) < 0) {
                        eligibleAddress = service.getPeerAddress();
                        eligibleIdentifier = eligibleId;
                    } else if (eligibleIdentifier == null) {
                        eligibleAddress = service.getPeerAddress();
                        eligibleIdentifier = eligibleId;
                    }
                }
            }
        }
        return eligibleAddress;
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (mGroupActor != null) {
            return;
        }

        if (info == null) {
            return;
        }

        WfdLog.d(TAG, "connection info available");

//		if (!info.groupFormed) {
//			Log.d(TAG,"createGroup: onConnectionInfoAvailable");
//			createGroup();
//			return;
//		}

        WfdLog.d(TAG, "group formed");

        if (info.isGroupOwner) {
            instantiateGroupOwner();
        } else {
            mManager.requestGroupInfo(mChannel, new GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    if (group == null) {
                        // happens when the go goes away and the
                        // framework does not have time to update the
                        // connection loss
                        return;
                    }
                    WifiP2pDevice groupOwnerDevice = group.getOwner();

                    Log.d(TAG, "requestGroupInfo - groupOwnerDeviceaddress: " + groupOwnerDevice.deviceAddress);

                    WiFiP2pService service = ServiceList.getInstance().getServiceByDevice(groupOwnerDevice);

                    Log.d(TAG, "requestGroupInfo - service: " + service);

                    if (service == null) {
                        Log.e(TAG, "service is null");
                        mManager.removeGroup(mChannel, null);
                    } else {
                        int destPort = service.getPort();
                        //TODO NULLPointerException perche' info==null (se ricordo bene)
                        instantiateGroupClient(info.groupOwnerAddress, destPort);
                    }
                }
            });
        }
    }

    private void instantiateGroupClient(InetAddress groupOwnerAddress, int destPort) {
        WfdLog.d(TAG, "Instantiating group client's logic");
        mGroupActor = new GroupClientActor(groupOwnerAddress, destPort, actorListener, myIdentifier);
        mGroupActor.connect();

    }

    private void instantiateGroupOwner() {
        WfdLog.d(TAG, "Instantiating group owner's logic");
        mGroupActor = new GroupOwnerActor(mServerSocket, myIdentifier, actorListener);
        mGroupActor.connect();
    }

    public void onNetworkConnected() {
        WfdLog.d(TAG, "onNetworkConnected(): requesting connection info");
        mManager.requestConnectionInfo(mChannel, this);

    }

    public void onNetworkDisconnected() {
        WfdLog.d(TAG, "onNetworkDisconnected");
        if (mGroupActor != null) {
            mGroupActor.disconnect();
        }
        isGroupCreated = false;
        mGroupActor = null;
        mManager.removeGroup(mChannel, null);
        mManager.requestConnectionInfo(mChannel, this);
    }

    private final GroupActorListener actorListener = new GroupActorListener() {

        @Override
        public void onMessageReceived(WfdMessage msg) {
            WfdLog.d(TAG, "GroupActorListener.onMessageReceived - msg= " + msg);
            mListener.onMessageReceived(msg);
        }

        @Override
        public void onError() {
            Log.e(TAG, "GroupActorListener.onError");
            if (mGroupActor != null) {
                mGroupActor.disconnect();
            }
            isGroupCreated = false;
            mGroupActor = null;
            mManager.removeGroup(mChannel, null);
            mManager.requestConnectionInfo(mChannel, WifiDirectMiddleware.this);
        }

        @Override
        public WfdMessage onRequestMessageReceived(WfdMessage msg) {
            Log.d(TAG, "GroupActorListener.onRequestMessageReceived - msg= " + msg);
            return mListener.onRequestMessageReceived(msg);
        }

        @Override
        public void onInstanceFound(String identifier) {
            Log.d(TAG, "GroupActorListener.onInstanceFound - identifier= " + identifier);
            mListener.onInstanceFound(identifier);
        }

        @Override
        public void onInstanceLost(String identifier) {
            Log.e(TAG, "GroupActorListener.onInstanceLost - identifier= " + identifier);
            mListener.onInstanceLost(identifier);
        }
    };

    public void sendMessage(WfdMessage msg, String targetId) throws IOException {
        msg.setSenderId(myIdentifier);
        msg.setReceiverId(targetId);
        GroupActor tmp = mGroupActor;
        if (tmp == null) {
            throw new IOException("Group not yet instantiated");
        }
        tmp.sendMessage(msg);

    }

    public void sendMessageBroadcast(WfdMessage msg) throws IOException {
        msg.setSenderId(myIdentifier);
        msg.setReceiverId(WfdMessage.BROADCAST_RECEIVER_ID);
        GroupActor tmp = mGroupActor;
        if (tmp == null) {
            throw new IOException("Group not yet instantiated");
        }
        tmp.sendMessage(msg);
    }

    public WfdMessage sendRequestMessage(WfdMessage msg, String targetId) {
        msg.setSenderId(myIdentifier);
        msg.setReceiverId(targetId);
        msg.setType(WfdMessage.TYPE_REQUEST);
        try {
            GroupActor tmp = mGroupActor;
            if (tmp == null) {
                return null;
            }
            return tmp.sendRequestMessage(msg);
        } catch (IOException e) {
            WfdLog.d(TAG, "sendRequestMessage( ) error", e);
            return null;
        }
    }


    /**
     * Method to set goIntent in Wi-Fi Direct Middleware.
     *
     * @param goIntent
     */
    public void setGoIntent(int goIntent) {
        this.goIntent = goIntent;
    }


    /**
     * method defined in interface {@link CustomDnsServiceResponseListener.CallbackToMiddleware} and
     * used in {@link CustomDnsServiceResponseListener}
     */
    @Override
    public boolean onIsGroupCreated() {
        return isGroupCreated;
    }

    /**
     * method defined in interface {@link CustomDnsServiceResponseListener.CallbackToMiddleware} and
     * used in {@link CustomDnsServiceResponseListener}
     */
    @Override
    public void onCreateGroup() {
        createGroup();
    }
}