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

import java.io.IOException;
import java.net.Socket;

import it.polimi.spf.wfd.util.WfdLog;
import it.polimi.spf.wfd.WfdMessage;
import it.polimi.spf.wfd.groups.streams.WfdInputStream;
import it.polimi.spf.wfd.groups.streams.WfdOutputStream;

/**
 * A GOInternalClient represents the server side connection of a GroupClientActor.
 * They are created by the GroupOwnerActor that accepts the incoming connections and uses
 * them to send messages and to be notified of received ones. A GOInternalClient has to monitor
 * the connections and notify the group owner when a peer leaves the network.
 */
class GOInternalClient extends Thread {
    private static final String TAG = GOInternalClient.class.getSimpleName();
    private final GroupOwnerActor groupOwnerActor;
    private final Socket socket;
    private String identifier;
    private volatile boolean closed = false;

    GOInternalClient(Socket socket, GroupOwnerActor groupOwnerActor) {
        this.socket = socket;
        this.groupOwnerActor = groupOwnerActor;
        this.setName("GOInternalClient");
    }

    void recycle() {
        try {
            socket.close();
        } catch (IOException e) {
            WfdLog.d(TAG, "Error while closing socket in GOInternalClient");
        }
        if (isAlive()) {
            closed = true;
            interrupt();
        }
    }

    @Override
    public void run() {
        WfdInputStream inStream;
        WfdMessage connmsg;
        try {
            WfdLog.d(TAG, "Get input stream from socket");
            inStream = new WfdInputStream(socket.getInputStream());
            connmsg = waitForConnectionMsg(inStream);
        } catch (Exception e) {
            WfdLog.d(TAG, "Exception in the read loop", e);
            return;
        }
        if (attachToGroupOwner(connmsg)) {
            enterReadLoop(inStream);
        }
        if (!closed) {
            try {
                groupOwnerActor.onClientDisconnected(identifier);
            } catch (InterruptedException e) {
                WfdLog.e(TAG, "Error while running the GOInternalClient", e);
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            WfdLog.e(TAG, "Error while closing socket in run() of GOInternalClient", e);
        }
    }

    private void enterReadLoop(WfdInputStream inStream) {
        WfdLog.d(TAG, "Entering read loop");
        try {
            while (!interrupted()) {
                WfdMessage msg = inStream.readMessage();
                WfdLog.d(TAG, "message read: " + msg);
                groupOwnerActor.onMessageReceived(msg);
            }
        } catch (Exception e) {
            WfdLog.e(TAG, "Error in enterReadLoop() in GOInternalClient", e);
        }
        WfdLog.d(TAG, "Exiting while loop: " + identifier);
    }

    private boolean attachToGroupOwner(WfdMessage connmsg) {
        if (connmsg != null && connmsg.getType().equals(WfdMessage.TYPE_CONNECT)) {
            this.identifier = connmsg.getSenderId();
            WfdLog.d(TAG, "Attaching GOInternalClient to groupOwner id: " + identifier);
            try {
                groupOwnerActor.onClientConnected(identifier, this);
                return true;
            } catch (InterruptedException e) {
                WfdLog.e(TAG, "Could not attach to group owner", e);
                return false;
            }
        } else {
            WfdLog.d(TAG, "invalid connection message " + connmsg);
            return false;
        }
    }

    private WfdMessage waitForConnectionMsg(WfdInputStream inStream)
            throws InterruptedException {
        WfdMessage connmsg;
        WfdLog.d(TAG, "read connection message");
        connmsg = inStream.readMessage((long) 60000);
        return connmsg;
    }

    synchronized void sendMessage(WfdMessage msg) {
        WfdOutputStream outStream;
        try {
            WfdLog.d(TAG, "Sending message: " + msg);
            outStream = new WfdOutputStream(socket.getOutputStream());
            outStream.writeMessage(msg);
        } catch (Throwable tr) {
            WfdLog.e(TAG, "Error on sending message", tr);
        }
    }

}