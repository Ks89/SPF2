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

package it.polimi.spf.wfd.groups;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import de.greenrobot.event.EventBus;
import it.polimi.spf.wfd.WfdMessage;
import it.polimi.spf.wfd.events.goEvent.GOConnectionEvent;
import it.polimi.spf.wfd.events.goEvent.GOErrorEvent;
import it.polimi.spf.wfd.events.goEvent.GOInternalClientEvent;
import it.polimi.spf.wfd.listeners.GroupActorListener;
import it.polimi.spf.wfd.util.WfdLog;

/**
 * GroupOwnerActor class adds an additional layer over the socket
 * connection for handling the specific functions of a group owner, that include the group
 * management as well as the routing of messages within the group.
 */
public class GroupOwnerActor extends GroupActor {
    private static final String TAG = GroupOwnerActor.class.getSimpleName();

    private final ServerSocket serverSocket;
    private final Map<String, GOInternalClient> goInternalClients = new Hashtable<>();

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    /**
     * this is a semaphore to handle client's connection and disconnection. the
     * aim is to serialize these operation in order to achieve total order and
     * consistency between instance discovery messages.
     */
    private final Semaphore connectionSemaphore = new Semaphore(1);

    public GroupOwnerActor(int port, GroupActorListener listener, String myIdentifier) throws IOException {
        super(listener, myIdentifier);
        serverSocket = new ServerSocket(port);

        EventBus.getDefault().register(this);
        this.setName("GroupOwnerActor");
    }

    //called from GoInternalClient
    void onClientConnected(String identifier, GOInternalClient gOInternalClient) throws InterruptedException {
        WfdLog.d(TAG, "New client connected id : " + identifier);
        EventBus.getDefault().post(new GOConnectionEvent(GOConnectionEvent.CONNECTED));
        connectionSemaphore.acquire();
        Set<String> clients = new HashSet<>(goInternalClients.keySet());
        clients.add(super.myIdentifier);
        GOInternalClient c = goInternalClients.put(identifier, gOInternalClient);
        signalNewInstanceToGroup(identifier);
        signalGroupToNewClient(gOInternalClient, clients);
        connectionSemaphore.release();
        if (c != null) {
            c.recycle();
        }
    }

    //called from GoInternalClient
    void onClientDisconnected(String identifier) throws InterruptedException {
        connectionSemaphore.acquire();
        EventBus.getDefault().post(new GOConnectionEvent(GOConnectionEvent.DISCONNECTED));
        WfdLog.d(TAG, "Client lost id : " + identifier);
        GOInternalClient c = null;
        if (identifier != null) {
            c = goInternalClients.remove(identifier);
            if (c != null) {
                signalInstanceLossToGroup(identifier);
            }
        }
        connectionSemaphore.release();
        if (c != null) {
            c.recycle();
        }
    }

    private void signalGroupToNewClient(GOInternalClient goInternalClient, Collection<String> clients) {
        for (String id : clients) {
            WfdMessage msg = new WfdMessage();
            msg.senderId = super.myIdentifier;
            msg.type = WfdMessage.TYPE_INSTANCE_DISCOVERY;
            msg.put(WfdMessage.ARG_IDENTIFIER, id);
            msg.put(WfdMessage.ARG_STATUS, WfdMessage.INSTANCE_FOUND);
            goInternalClient.sendMessage(msg);
        }
    }

    private void signalNewInstanceToGroup(String identifier) {
        WfdMessage msg = new WfdMessage();
        msg.receiverId = WfdMessage.BROADCAST_RECEIVER_ID;
        msg.senderId = identifier;
        msg.type = WfdMessage.TYPE_INSTANCE_DISCOVERY;
        msg.put(WfdMessage.ARG_IDENTIFIER, identifier);
        msg.put(WfdMessage.ARG_STATUS, WfdMessage.INSTANCE_FOUND);
        sendBroadcastSignal(msg);
    }

    private void signalInstanceLossToGroup(String lostIdentifier) {
        WfdMessage msg = new WfdMessage();
        msg.receiverId = WfdMessage.BROADCAST_RECEIVER_ID;
        msg.senderId = lostIdentifier;
        msg.type = WfdMessage.TYPE_INSTANCE_DISCOVERY;
        msg.put(WfdMessage.ARG_IDENTIFIER, lostIdentifier);
        msg.put(WfdMessage.ARG_STATUS, WfdMessage.INSTANCE_LOST);
        sendBroadcastSignal(msg);
    }

    //called from the GOInternalClient
    void onMessageReceived(final WfdMessage msg) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (msg.getReceiverId().equals(myIdentifier)) {
                    handle(msg);
                } else {
                    route(msg);
                }
            }
        });
    }

    private void route(WfdMessage msg) {
        final String receiverId = msg.receiverId;
        if (receiverId.equals(WfdMessage.BROADCAST_RECEIVER_ID)) {
            sendBroadcastSignal(msg);
        } else {
            sendUnicastMsg(msg, receiverId);
        }
    }

    private void sendUnicastMsg(WfdMessage msg, final String receiverId) {
        GOInternalClient c = goInternalClients.get(receiverId);
        if (c != null) {
            c.sendMessage(msg);
        }
    }

    private void sendBroadcastSignal(WfdMessage msg) {
        if (!msg.getReceiverId()
                .equals(WfdMessage.BROADCAST_RECEIVER_ID)) {
            Log.e(TAG, "Illegal message in sendBroadcastSignal");
            return;
        }
        ArrayList<String> idSet = new ArrayList<>(goInternalClients.keySet());
        idSet.remove(msg.getSenderId());
        if (!msg.getSenderId().equals(super.myIdentifier)) {
            handle(msg);
        }
        for (String id : idSet) {
            sendUnicastMsg(msg, id);
        }
    }

    @Override
    void connect() {
        this.start();
    }

    @Override
    public void run() {
        WfdLog.d(TAG, "ServerSocketAcceptor's variable 'serverSocket' has port: " + serverSocket.getLocalPort());

        Socket s = null;

        try {
            while (!currentThread().isInterrupted()) {
                WfdLog.d(TAG, "accept(): waiting for a new client");
                s = serverSocket.accept();
                WfdLog.d(TAG, "incoming connection");
                WfdLog.d(TAG, "IP Address: " + s.getInetAddress());
                EventBus.getDefault().post(new GOInternalClientEvent("onStartGoInternalClient", s));
            }
        } catch (IOException e) {
            WfdLog.e(TAG, "serverSocket.accept - IOException", e);
            if (s != null && !s.isClosed()) {
                try {
                    s.close();
                } catch (IOException e1) {
                    WfdLog.e(TAG, "serverSocket.accept IOException while closing socket 's' ", e1);
                }
            }
        } finally {
            WfdLog.d(TAG, "serverSocket.accept exiting while loop in run()");
            if (!serverSocket.isClosed()) {
                WfdLog.d(TAG, "signalling error to groupOwnerActor");
                EventBus.getDefault().post(new GOErrorEvent("onServerSocketError"));
            }

        }
    }

    @Override
    protected void disconnect(boolean withError) {
        super.disconnect(withError);
        EventBus.getDefault().unregister(this);

        WfdLog.d(TAG, "GroupOwnerActor Disconnecting...");
        try {
            serverSocket.close();
        } catch (IOException e) {
            if (!withError) {
                WfdLog.d(TAG, "ServerSocket force-closed in GroupOwnerActor", e);
            } else {
                WfdLog.e(TAG, "ServerSocket error in GroupOwnerActor while closing", e);
            }
        }
        for (String id : goInternalClients.keySet()) {
            goInternalClients.get(id).recycle();
        }
        goInternalClients.clear();
    }

    @Override
    public synchronized void sendMessage(WfdMessage msg) {
        msg.setSenderId(super.myIdentifier);
        String receiverId = msg.getReceiverId();
        if (receiverId.equals(WfdMessage.BROADCAST_RECEIVER_ID)) {
            sendBroadcastSignal(msg);
        } else {
            sendUnicastMsg(msg, receiverId);
        }
    }


    public void onEvent(GOErrorEvent event) {
        WfdLog.d(TAG, "GOErrorEvent received with type: " + event.getType());
        this.disconnect(true); //true = with errors
        super.onError();
    }

    public void onEvent(GOInternalClientEvent event) {
        WfdLog.d(TAG, "GOInternalClientEvent received with type: " + event.getType());
        new GOInternalClient(event.getSocket(), this).start();
    }
}