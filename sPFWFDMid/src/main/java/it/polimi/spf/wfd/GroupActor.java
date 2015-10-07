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

import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import it.polimi.spf.wfd.otto.NineBus;
import it.polimi.spf.wfd.otto.goEvent.GOConnectionEvent;

/**
 * GroupActor is the abstract class that defines the interface in common to a group owners
 * and standard group members. It holds the logic that allows the middleware to deliver
 * messages to the application. This class extends Thread, because all subclasses
 * perform network operations that cannot be executed on the UI Thread.
 */
abstract class GroupActor extends Thread {
    private static final String TAG = GroupActor.class.getSimpleName();

    private static final long REQUEST_TIMEOUT = 60000;
    protected final String myIdentifier;
    private final GroupActorListener listener;

    private final Semaphore requestSemaphore = new Semaphore(1, true);
    private final ResponseHolder respHolder = new ResponseHolder(REQUEST_TIMEOUT);

    /**
     * Object to be registered on {@link it.polimi.spf.wfd.otto.NineBus}.
     * We need it to make extending classes inherit "@Subscribe" methods.
     */
    private Object busListener;

    GroupActor(GroupActorListener listener, String identifier) {
        this.myIdentifier = identifier;
        this.listener = listener;

        busListener = new Object() {
            @Subscribe
            public void onGOActorActionEvent(GOConnectionEvent event) {
                switch (event.getAction()) {
                    case GOConnectionEvent.CONNECT_STRING:
                        WfdLog.d(TAG, "Connect event received");
                        connect();
                        break;
                    case GOConnectionEvent.DISCONNECT_STRING:
                        WfdLog.d(TAG, "Disconnect event received");
                        disconnect(false); //normal  disconnection without errors
                        break;
                    default:
                        WfdLog.d(TAG, "Unknown GOConnectionEvent");
                }
            }
        };

        NineBus.get().register(busListener);
    }

    public WfdMessage sendRequestMessage(WfdMessage msg) throws IOException {
        WfdLog.d(TAG, "Sending request message");
        try {
            requestSemaphore.acquire();
            msg.setSequenceNumber(respHolder.assignRequestSequenceId());
            sendMessage(msg);
            WfdMessage response = respHolder.get();
            requestSemaphore.release();
            return response;
        } catch (InterruptedException e) {
            WfdLog.d(TAG, "interrupted when acquiring request semaphore");
        }
        return null;
    }

    void onError() {
        WfdLog.d(TAG, "onError()");
        listener.onError();
    }

    void handle(WfdMessage msg) {
        String type = msg.getType();
        switch (type) {
            case WfdMessage.TYPE_INSTANCE_DISCOVERY:
                onInstanceDiscovery(msg);
                break;
            case WfdMessage.TYPE_SIGNAL:
                deliverToApplication(msg);
                break;
            case WfdMessage.TYPE_REQUEST:
                onRequestReceived(msg);
                break;
            case WfdMessage.TYPE_RESPONSE:
                onResponseReceived(msg);
                break;
            case WfdMessage.TYPE_RESPONSE_ERROR:
                onResponseReceived(msg);
                break;
        }
    }

    private void onInstanceDiscovery(WfdMessage msg) {
        String identifier = msg.getString(WfdMessage.ARG_IDENTIFIER);
        boolean status = msg.getBoolean(WfdMessage.ARG_STATUS);
        if (status) { //is equivalent to "status==WfdMessage.INSTANCE_FOUND"
            WfdLog.d(TAG, "Instance found: " + identifier);
            listener.onInstanceFound(identifier);
        } else {
            WfdLog.d(TAG, "Instance lost: " + identifier);
            listener.onInstanceLost(identifier);
        }
    }

    private void deliverToApplication(WfdMessage msg) {
        WfdLog.d(TAG, "deliverying message to application");
        try {
            listener.onMessageReceived(msg);
        } catch (Throwable e) {
            WfdLog.e(TAG, "Delivery to application failed", e);
        }
    }

    private void onRequestReceived(WfdMessage msg) {
        WfdLog.d(TAG, "request message received");
        WfdMessage response;
        try {
            response = listener.onRequestMessageReceived(msg);
            response.setSequenceNumber(msg.getTimestamp());
            response.setType(WfdMessage.TYPE_RESPONSE);
            response.setReceiverId(msg.getSenderId());
            response.setSenderId(myIdentifier);
            sendMessage(response);
        } catch (Throwable tr) {
            WfdLog.e(TAG, "onRequestReceived error", tr);
            response = new WfdMessage();
            response.setReceiverId(msg.getSenderId());
            response.setSenderId(myIdentifier);
            response.setType(WfdMessage.TYPE_RESPONSE_ERROR);
            response.setSequenceNumber(msg.getTimestamp());
            try {
                sendMessage(response);
            } catch (IOException e) {
                WfdLog.e(TAG, "IOException onRequestReceived", e);
            }
        }
    }

    private void onResponseReceived(WfdMessage msg) {
        WfdLog.d(TAG, "Request received");
        respHolder.set(msg);
    }

    protected void disconnect(boolean withError) {
        if (busListener != null) {
            NineBus.get().unregister(busListener);
        }
        busListener = null;
    }

    abstract void sendMessage(WfdMessage msg) throws IOException;

    abstract void connect();

    public abstract void run();
}
