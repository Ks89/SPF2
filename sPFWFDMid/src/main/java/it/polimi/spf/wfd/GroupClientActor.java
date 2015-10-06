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

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import it.polimi.spf.wfd.otto.NineBus;
import it.polimi.spf.wfd.otto.goEvent.GOConnectionEvent;

/**
 * GroupClientActor is the class that implements the role of a standard group member,
 * as such its solely duty is to connect to the group owner and offer functions for sending
 * and receiving messages through its socket connections.
 */
class GroupClientActor extends GroupActor {

    private static final String TAG = "GroupClientActor";
    private final InetAddress groupOwnerAddress;
    private final int destPort;
    private Socket socket;

    public GroupClientActor(InetAddress groupOwnerAddress, int destPort,
                            GroupActorListener listener, String myIdentifier) {
        super(listener, myIdentifier);
        this.groupOwnerAddress = groupOwnerAddress;
        this.destPort = destPort;

        NineBus.get().register(this);
        this.setName("GroupClientActor");
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            WfdLog.e(TAG, "IOException during socket.close", e);
        }
    }

    private void establishConnection() throws IOException {
        WfdMessage msg = new WfdMessage();
        msg.setType(WfdMessage.TYPE_CONNECT);
        msg.setSenderId(super.myIdentifier);
        WfdLog.d(TAG, "Sending connection message... ");
        sendMessage(msg);
    }

    @Override
    public void connect() {
        this.start();
    }

    @Override
    public void disconnect() {
        try {
            WfdLog.d(TAG, "Disconnect called");
            this.interrupt();
            socket.close();
        } catch (IOException e) {
            WfdLog.d(TAG, "error on closing socket", e);
        }
    }

    @Override
    void sendMessage(WfdMessage msg) throws IOException {
        WfdLog.d(TAG, "Sending message: " + msg);
        WfdOutputStream outstream = new WfdOutputStream(socket.getOutputStream());
        outstream.writeMessage(msg);
    }

    @Override
    public void run() {
        WfdInputStream inStream;
        try {
            WfdLog.d(TAG, "Opening socket connection");
            socket = new Socket();
            socket.bind(null);
            SocketAddress remoteAddr = new InetSocketAddress(groupOwnerAddress, destPort);
            socket.connect(remoteAddr, 5000);
            inStream = new WfdInputStream(socket.getInputStream());
            establishConnection();
            WfdLog.d(TAG, "Entering read loop");
            while (!this.isInterrupted()) {
                WfdMessage msg = inStream.readMessage();
                WfdLog.d(TAG, "message received");
                super.handle(msg);
            }
        } catch (Throwable e) {
            WfdLog.d(TAG, "error in the run loop", e);
        } finally {
            closeSocket();
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                GroupClientActor.super.onError();
            }
        });
    }

    @Subscribe
    public void onGOActorActionEvent(GOConnectionEvent event) {
        switch (event.getAction()) {
            case GOConnectionEvent.CONNECT_STRING:
                WfdLog.d(TAG, "Connect event received");
                this.connect();
                break;
            case GOConnectionEvent.DISCONNECT_STRING:
                WfdLog.d(TAG, "Disconnect event received");
                this.disconnect();
                break;
            default:
                WfdLog.d(TAG, "Unknown GOConnectionEvent");
        }
    }
}