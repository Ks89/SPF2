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
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import it.polimi.spf.wfd.events.Event;
import it.polimi.spf.wfd.events.HandlerSendBroadcastEvent;
import it.polimi.spf.wfd.events.MidConnectionEvent;
import it.polimi.spf.wfd.events.NineBus;
import it.polimi.spf.wfd.events.goEvent.GOConnectActionEvent;
import it.polimi.spf.wfd.events.goEvent.GOConnectionEvent;
import it.polimi.spf.wfd.exceptions.GroupException;
import it.polimi.spf.wfd.groups.GroupActor;
import it.polimi.spf.wfd.groups.GroupClientActor;
import it.polimi.spf.wfd.groups.GroupOwnerActor;
import it.polimi.spf.wfd.listeners.CustomDnsSdTxtRecordListener;
import it.polimi.spf.wfd.listeners.CustomDnsServiceResponseListener;
import it.polimi.spf.wfd.listeners.CustomizableActionListener;
import it.polimi.spf.wfd.listeners.GroupActorListener;
import it.polimi.spf.wfd.listeners.WfdMiddlewareListener;
import it.polimi.spf.wfd.util.WfdLog;
import it.polimi.spf.wfdadapter.WFDMiddlewareAdapter;
import lombok.Getter;
import lombok.Setter;

/**
 * WifiDirectMiddleware class provides a coordination layer that handles the discovery of peer,
 * the creation of the P2P Group and mediates the communication to the upper layer of the system.
 * In particular, this last function is made with a series of adapters whose aim is to adapt the
 * Wi-Fi Direct middleware interface to the ones required by SPF.
 */
public class WifiDirectMiddleware implements WifiP2pManager.ConnectionInfoListener {
    private final static String TAG = WifiDirectMiddleware.class.getSimpleName();
    private static final int THIS_DEVICE_IS_GO = 15;

    private final Context mContext;
    private final String myIdentifier;

    private final WfdMiddlewareListener mListener;
    private final WfdBroadcastReceiver mReceiver;
    private WifiP2pManager mManager;
    private Channel mChannel;

    private HandlerThread handlerThread;
    @Getter
    private WfdHandler wfdHandler;

    private ScheduledExecutorService scheduler = null;


    @Getter
    private boolean connected = false;

    @Getter
    private boolean isGroupCreated = false;
    private boolean eternalConnect = false;

    @Setter
    private boolean proximityKilledByUser = false;

    @Setter
    private int goIntent;

    @Getter
    private boolean isAutonomous;

    private GroupActor mGroupActor;
    private int mPort;

    public WifiDirectMiddleware(Context context, int goIntentFromSPFApp, boolean isAutonomous, String identifier, WfdMiddlewareListener listener) {
        WfdLog.d(TAG, "WifiDirectMiddleware (isAutonomous= " + isAutonomous +
                ") with goIntentFromSPFApp: " + goIntentFromSPFApp);
        this.mContext = context;
        this.mReceiver = new WfdBroadcastReceiver();
        this.mListener = listener;
        this.myIdentifier = identifier;
        this.goIntent = goIntentFromSPFApp;
        this.isAutonomous = isAutonomous;
    }

    /**
     * Method called by {@link WFDMiddlewareAdapter#connect()}
     */
    public void init() {
        WfdLog.d(TAG, "Called init()");

        initAndStartHandler();

        try {
            mPort = this.requestAvailablePortFromOs();
        } catch (IOException e) {
            mListener.onError();
            return;
        }

        mManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(mContext, Looper.getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                WfdLog.d(TAG, "Channel disconnected");
            }
        });

        mReceiver.register(mContext);
        NineBus.get().register(this);
    }

    private void initAndStartHandler() {
        WfdLog.d(TAG, "Handler in WifiDirectMiddleware initialized and started");
        handlerThread = new HandlerThread("wfd_middleware_handler_thread");
        handlerThread.start();
        wfdHandler = new WfdHandler(handlerThread.getLooper());
    }

    /**
     * Method to connect. Called by {@link it.polimi.spf.wfdadapter.WFDMiddlewareAdapter#connect()}.
     */
    public void connect() {
        if (isConnected()) {
            Log.w(TAG, "Connect called but isConnected() == true");
            return;
        }

        this.startRegistration();
        this.discoverService();

        WfdLog.d(TAG, "-----> " + isAutonomous + " , " + goIntent);
        if (this.isAutonomous() && this.goIntent == 15) {
            this.createGroup();
        }

        this.connected = true;
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
        WifiP2pDnsSdServiceInfo mInfo = WifiP2pDnsSdServiceInfo.newInstance(
                Configuration.SERVICE_INSTANCE + myIdentifier,
                Configuration.SERVICE_REG_TYPE, mRecordMap);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.addLocalService(mChannel, mInfo,
                new CustomizableActionListener(
                        this.mContext,
                        TAG,
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
        WifiP2pDnsSdServiceRequest mServiceRequest = WifiP2pDnsSdServiceRequest.newInstance();

        //initiates discovery
        mManager.addServiceRequest(mChannel, mServiceRequest,
                new CustomizableActionListener(
                        this.mContext,
                        TAG,
                        "Added service discovery request",
                        null,
                        "Failed adding service discovery request",
                        "Failed adding service discovery request",
                        true)); //important: sets true to get detailed message when this method fails

        //starts services discovery
        mManager.discoverServices(mChannel,
                new CustomizableActionListener(
                        this.mContext,
                        TAG,
                        "Service discovery initiated",
                        null,
                        "Failed starting service discovery",
                        "Failed starting service discovery",
                        true)); //important: sets true to get detailed message when this method fails
    }


    public void disconnect() {
        if (!isConnected()) {
            Log.e(TAG, "disconnect called but isConnected() == false");
            return;
        }

        if (handlerThread != null) {
            handlerThread.quit();
        }
        handlerThread = null;
        wfdHandler = null;

        mReceiver.unregister(mContext);
        NineBus.get().unregister(this);

        mManager.clearServiceRequests(mChannel, new CustomizableActionListener(
                this.mContext,
                TAG,
                "ClearServiceRequests success",
                null,
                "ClearServiceRequests failure",
                "ClearServiceRequests failure",
                false)); //important: sets false if you don't want detailed messages when this method fails

        mManager.clearLocalServices(mChannel, new CustomizableActionListener(
                this.mContext,
                TAG,
                "ClearLocalServices success",
                null,
                "ClearLocalServices failure",
                "ClearLocalServices failure",
                false)); //important: sets false if you don't want detailed messages when this method fails

        mManager.cancelConnect(mChannel, new CustomizableActionListener(
                this.mContext,
                TAG,
                "CancelConnect success",
                null,
                "CancelConnect failure",
                "CancelConnect failure",
                false)); //important: sets false if you don't want detailed messages when this method fails

        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                WfdLog.d(TAG, "RemoveGroup success");
            }

            @Override
            public void onFailure(int reason) {
                WfdLog.e(TAG, "RemoveGroup failure, with reason: " + reason);
            }
        });

        this.connected = false;

        if (mGroupActor != null) {
            this.postEvent(new GOConnectActionEvent(GOConnectActionEvent.DISCONNECT_STRING));
            mGroupActor = null;
        }

        ServiceList.getInstance().getServiceList().clear();
    }

    private void becomeStandardGroupOwner() {
        List<WiFiP2pService> validClients = ServiceList.getInstance().getValidClients(myIdentifier);
        if (validClients == null || validClients.size() == 0) {
            WfdLog.d(TAG, "no device address eligible for connection");
            return;
        }

        //TODO TODO FIXME FIXME FIXME in this first impl i choose the first element in the list
        String deviceAddress = validClients.get(0).getPeerAddress();
        this.establishStandardConnection(validClients, deviceAddress);
    }

    private void becomeAutonomousGroupOwner() {
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                WfdLog.d(TAG, "CreateGroup success");
            }

            @Override
            public void onFailure(int reason) {
                WfdLog.e(TAG, "CreateGroup failure");
            }
        });
    }

    public void createGroup() {
        WfdLog.d(TAG, "createGroup()");
        if (isGroupCreated || !this.connected) {
            WfdLog.d(TAG, "group already created or middleware not started");
            return;
        }
        WfdLog.d(TAG, "attempt to create group");

        isGroupCreated = true;

        if (goIntent == THIS_DEVICE_IS_GO) {
            WfdLog.d(TAG, "This device want to be an autonomous GO, because has gointent: " + goIntent);

            if (isAutonomous) {
                this.becomeAutonomousGroupOwner();
            } else {
                this.becomeStandardGroupOwner();
            }

        } else {
            WfdLog.d(TAG, "This device want to be a client, because has gointent: " + goIntent);

            List<WiFiP2pService> validGroupOwners = ServiceList.getInstance().getValidGroupOwners(myIdentifier);
            if (validGroupOwners == null || validGroupOwners.size() == 0) {
                WfdLog.d(TAG, "no device address eligible for connection");
                return;
            }

            //TODO TODO FIXME FIXME FIXME in this first impl i choose the first element in the list
            //TODO but i should check between all the GO in proximity and I should choose the correct one.
            String deviceAddress = validGroupOwners.get(0).getPeerAddress();
            this.establishStandardConnection(validGroupOwners, deviceAddress);
        }
    }

    private void establishStandardConnection(List<WiFiP2pService> servicesList, String destinationAddress) {
        WfdLog.d(TAG, "connect target device found, " +
                "device address: " + destinationAddress + " device name: " + servicesList.get(0).getInstanceName() +
                " device id: " + servicesList.get(0).getIdentifier() + ", and goIntent: " + goIntent);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = destinationAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = this.goIntent;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                WfdLog.d(TAG, "Connect success");
            }

            @Override
            public void onFailure(int reason) {
                WfdLog.e(TAG, "Connect failure");
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (mGroupActor != null) {
            Log.e(TAG, "Error onConnectionInfoAvailable, returning...");
            return;
        }

        if (info == null) {
            Log.e(TAG, "Error onConnectionInfoAvailable, info==null, returning...");
            return;
        }

        WfdLog.d(TAG, "connection info available, with info.isGroupOwner " + info.isGroupOwner);

        //here the connection info is available, but groups aren't created yet.
        //Groups are really available only in GroupOwnerInfoListener and GroupClientInfoListener
        //after the mManager.requestGroupInfo
        if (info.isGroupOwner) {
            mManager.requestGroupInfo(mChannel, new GroupOwnerInfoListener(info));
        } else {
            mManager.requestGroupInfo(mChannel, new GroupClientInfoListener(info));
        }
    }

    private void instantiateGroupClient(InetAddress groupOwnerAddress, int destPort) {
        WfdLog.d(TAG, "Instantiating group client's logic");
        mGroupActor = new GroupClientActor(groupOwnerAddress, destPort, actorListener, myIdentifier);
        this.postEvent(new GOConnectActionEvent(GOConnectActionEvent.CONNECT_STRING));

        if (scheduler != null) {
            WfdLog.d(TAG, "scheduler killed");
            scheduler.shutdown();
            scheduler = null;
        }
        this.eternalConnect = true;
    }

    private void instantiateGroupOwner() throws IOException {
        WfdLog.d(TAG, "Instantiating group owner's logic");
        mGroupActor = new GroupOwnerActor(mPort, actorListener, myIdentifier);
        this.postEvent(new GOConnectActionEvent(GOConnectActionEvent.CONNECT_STRING));

        if (scheduler != null) {
            WfdLog.d(TAG, "scheduler killed");
            scheduler.shutdown();
            scheduler = null;
        }

        this.eternalConnect = true;
    }

    public void onNetworkConnected() {
        WfdLog.d(TAG, "onNetworkConnected(): requesting connection info");
        if (mManager != null) {
            mManager.requestConnectionInfo(mChannel, this);
        }
    }

    public void onNetworkDisconnected() {
        WfdLog.d(TAG, "onNetworkDisconnected");
        if (mGroupActor != null) {
            WfdLog.d(TAG, "Disconnecting");
            this.postEvent(new GOConnectActionEvent(GOConnectActionEvent.DISCONNECT_STRING));
        }
        isGroupCreated = false;
        mGroupActor = null;
        if (mManager != null) {
            mManager.removeGroup(mChannel, null);
        }

        if (eternalConnect && !proximityKilledByUser) {
            if(!isAutonomous) {
                WfdLog.d(TAG, "Eternal connect is active...Reconnecting...");
                scheduler = Executors.newScheduledThreadPool(1);
                eternalConnect();
            }
        }
    }

    private void eternalConnect() {
        final Runnable beeper = new Runnable() {
            public void run() {
                WfdLog.d(TAG, "Eternal Connect is creating a new connection, please wait...");
                disconnect();
                init();
                connect();
            }
        };
        final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 10, 15, TimeUnit.SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() {
                beeperHandle.cancel(true);
            }
        }, 15, TimeUnit.SECONDS);
    }


    private final GroupActorListener actorListener = new GroupActorListener() {

        @Override
        public void onMessageReceived(WfdMessage msg) {
            WfdLog.d(TAG, "GroupActorListener.onMessageReceived - msg= " + msg);
            mListener.onMessageReceived(msg);
        }

        @Override
        public void onError() {
            WfdLog.e(TAG, "GroupActorListener.onError");
            if (mGroupActor != null) {
                postEvent(new GOConnectActionEvent(GOConnectActionEvent.DISCONNECT_STRING));
            }
            isGroupCreated = false;
            mGroupActor = null;
            mManager.removeGroup(mChannel, null);
            mManager.requestConnectionInfo(mChannel, WifiDirectMiddleware.this);
        }

        @Override
        public WfdMessage onRequestMessageReceived(WfdMessage msg) {
            WfdLog.d(TAG, "GroupActorListener.onRequestMessageReceived - msg= " + msg);
            return mListener.onRequestMessageReceived(msg);
        }

        @Override
        public void onInstanceFound(String identifier) {
            WfdLog.d(TAG, "GroupActorListener.onInstanceFound - identifier= " + identifier);
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
        if (mGroupActor == null) {
            throw new IOException("Group not yet instantiated");
        }
        mGroupActor.sendMessage(msg);

    }

    public void sendMessageBroadcast(WfdMessage msg) throws IOException, GroupException {
        msg.setSenderId(myIdentifier);
        msg.setReceiverId(WfdMessage.BROADCAST_RECEIVER_ID);
        if (mGroupActor == null) {
            throw new GroupException(GroupException.Reason.NOT_INSTANTIATED_YET, "Group not yet instantiated");
        }
        mGroupActor.sendMessage(msg);
    }

    public WfdMessage sendRequestMessage(WfdMessage msg, String targetId) {
        msg.setSenderId(myIdentifier);
        msg.setReceiverId(targetId);
        msg.setType(WfdMessage.TYPE_REQUEST);
        try {
            if (mGroupActor == null) {
                return null;
            }
            return mGroupActor.sendRequestMessage(msg);
        } catch (IOException e) {
            WfdLog.d(TAG, "sendRequestMessage( ) error", e);
            return null;
        }
    }

    private int requestAvailablePortFromOs() throws IOException {
        ServerSocket mServerSocket = new ServerSocket(0);
        int assignedPort = mServerSocket.getLocalPort();

        //close this socket, but i want to maintain in a global variable the assigned port
        try {
            mServerSocket.close();
        } catch (IOException e) {
            WfdLog.d(TAG, "IOException during mServerSocket.close().");
        }

        return assignedPort;
    }

    private void postEvent(Event event) {
        NineBus.get().post(event);
    }

    @Subscribe
    public void onMidConnectionEvent(MidConnectionEvent e) {
        switch (e.getType()) {
            case WfdBroadcastReceiver.NETWORK_CONNECTED:
                this.onNetworkConnected();
                break;
            case WfdBroadcastReceiver.NETWORK_DISCONNECTED:
                this.onNetworkDisconnected();
                break;
            case WfdBroadcastReceiver.P2P_ENABLED:
                Log.d(TAG, "Wi-Fi Direct enabled");
                break;
            case WfdBroadcastReceiver.P2P_DISABLED:
                Toast.makeText(mContext, "Wi-Fi Direct not enabled!", Toast.LENGTH_SHORT).show();
                break;
            case WfdBroadcastReceiver.PEERS_CHANGED:
                //TODO if necessary
                break;
            default:
                WfdLog.d(TAG, "Unknown onMidConnectionEvent");
        }
    }

    @Subscribe
    public void onClientConnectedToGO(GOConnectionEvent e) {
        switch (e.getType()) {
            case GOConnectionEvent.CONNECTED:
                if (mContext != null) {
                    Toast.makeText(mContext, "Client connected", Toast.LENGTH_SHORT).show();
                }
                break;
            case GOConnectionEvent.DISCONNECTED:
                if (mContext != null) {
                    Toast.makeText(mContext, "Client disconnected", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                WfdLog.d(TAG, "Error, unknown GOConnectionEvent's state");
        }
    }

    /**
     * subscribe to catch events posted by {@link WfdHandler}.
     */
    @Subscribe
    public void onSendBroadcastMessage(HandlerSendBroadcastEvent e) {
        try {
            this.sendMessageBroadcast(e.getMessage());
        } catch (GroupException e1) {
            WfdLog.e(TAG, "GroupException " + e.getMessage());
        } catch (IOException e1) {
            WfdLog.e(TAG, "handleMessage IOException", e1);
        }
    }


    public class GroupClientInfoListener implements WifiP2pManager.GroupInfoListener {
        private WifiP2pInfo info;

        public GroupClientInfoListener(WifiP2pInfo info) {
            this.info = info;
        }

        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {
            if (group == null) {
                // happens when the go goes away and theif (group == null) {
                // happens when the go goes away and the
                // framework does not have time to update the
                // connection loss
                return;
            }
            WifiP2pDevice groupOwnerDevice = group.getOwner();

            WfdLog.d(TAG, "requestGroupInfo - groupOwnerDeviceaddress: " + groupOwnerDevice.deviceAddress);

            WiFiP2pService service = ServiceList.getInstance().getServiceByDevice(groupOwnerDevice);

            WfdLog.d(TAG, "requestGroupInfo - service: " + service);

            if (service == null) {
                Log.e(TAG, "service is null");
                mManager.removeGroup(mChannel, null);
            } else {
                int destPort = service.getPort();
                instantiateGroupClient(info.groupOwnerAddress, destPort);
            }
        }
    }

    public class GroupOwnerInfoListener implements WifiP2pManager.GroupInfoListener {
        private WifiP2pInfo info;

        public GroupOwnerInfoListener(WifiP2pInfo info) {
            this.info = info;
        }

        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {
            try {
                instantiateGroupOwner();
            } catch (IOException e) {
                WfdLog.e(TAG, "Impossible to instantiate the GroupOwner Actor!", e);
            }
        }
    }
}