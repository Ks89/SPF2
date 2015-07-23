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

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.util.Log;

import com.squareup.otto.Subscribe;

import it.polimi.spf.wfd.otto.GOEvent;
import it.polimi.spf.wfd.otto.GOSocketEvent;
import it.polimi.spf.wfd.otto.NineBus;

/**
 * GroupOwnerActor class adds an additional layer over the socket
 * connection for handling the specific functions of a group owner, that include the group
 * management as well as the routing of messages within the group.
 */
class GroupOwnerActor extends GroupActor {

    private static final String TAG = "GroupOwnerActor";
    private final ServerSocket serverSocket;
    private ServerSocketAcceptor acceptor;
    private final Map<String, GOInternalClient> gOInternalClients = new Hashtable<>();

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    public GroupOwnerActor(ServerSocket serverSocket, String myIdentifier,
                           GroupActorListener listener) {
        super(listener, myIdentifier);
        this.serverSocket = serverSocket;
        NineBus.get().register(this);
    }

    @Override
    public void connect() {
        acceptor = new ServerSocketAcceptor(/*this,*/ serverSocket);
        acceptor.start();
    }

    void disconnect() {
        acceptor.recycle();
        for (String id : gOInternalClients.keySet()) {
            gOInternalClients.get(id).recycle();
        }
        gOInternalClients.clear();
    }

    /*
     * this is a semaphore to handle client's connection and disconnection. the
     * aim is to serialize these operation in order to achieve total order and
     * consistency between instance discovery messages.
     */
    private final Semaphore connectionSemaphore = new Semaphore(1);

    public void onClientConnected(String identifier,
                                  GOInternalClient gOInternalClient) throws InterruptedException {
        WfdLog.d(TAG, "New client connected id : " + identifier);
        connectionSemaphore.acquire();
        Set<String> clients = new HashSet<>(gOInternalClients.keySet());
        clients.add(getIdentifier());
        GOInternalClient c = gOInternalClients
                .put(identifier, gOInternalClient);
        signalNewInstanceToGroup(identifier);
        signalGroupToNewClient(gOInternalClient, clients);
        connectionSemaphore.release();
        if (c != null) {
            c.recycle();
        }
    }

    public void onClientDisconnected(String identifier)
            throws InterruptedException {
        connectionSemaphore.acquire();
        WfdLog.d(TAG, "Client lost id : " + identifier);
        GOInternalClient c = gOInternalClients.remove(identifier);
        if (c != null) {
            signalInstanceLossToGroup(identifier);
        }
        connectionSemaphore.release();
        if (c != null) {
            c.recycle();
        }
    }

    private void signalGroupToNewClient(GOInternalClient gOInternalClient,
                                        Collection<String> clients) {
        for (String id : clients) {
            WfdMessage msg = new WfdMessage();
            msg.senderId = getIdentifier();
            msg.type = WfdMessage.TYPE_INSTANCE_DISCOVERY;
            msg.put(WfdMessage.ARG_IDENTIFIER, id);
            msg.put(WfdMessage.ARG_STATUS, WfdMessage.INSTANCE_FOUND);
            gOInternalClient.sendMessage(msg);
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

    public void onServerSocketError() {
        disconnect();
        super.onError();
    }

    public void onMessageReceived(final WfdMessage msg) {
        threadPool.execute(new Runnable() {

            @Override
            public void run() {
                if (msg.getReceiverId().equals(getIdentifier())) {
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
        GOInternalClient c = gOInternalClients.get(receiverId);
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
        ArrayList<String> idSet = new ArrayList<>(
                gOInternalClients.keySet());
        idSet.remove(msg.getSenderId());
        if (!msg.getSenderId().equals(getIdentifier())) {
            handle(msg);
        }
        for (String id : idSet) {
            sendUnicastMsg(msg, id);
        }
    }

    @Override
    public synchronized void sendMessage(WfdMessage msg) {
        msg.setSenderId(getIdentifier());
        String receiverId = msg.getReceiverId();
        if (receiverId.equals(WfdMessage.BROADCAST_RECEIVER_ID)) {
            sendBroadcastSignal(msg);
        } else {
            sendUnicastMsg(msg, receiverId);
        }
    }

    @Subscribe
    public void onGoEvent(GOEvent event) {
        Log.d(TAG, "GoEvent recevived with type: " + event.getType());
        this.onServerSocketError();
    }

    @Subscribe
    public void onGoSocketEvent(GOSocketEvent event) {
        Log.d(TAG, "GOSocketEvent recevived with type: " + event.getType());
        new GOInternalClient(event.getSocket(), this).start();
    }

}