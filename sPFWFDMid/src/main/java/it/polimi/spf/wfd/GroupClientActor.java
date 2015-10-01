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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * GroupClientActor is the class that implements the role of a standard group member,
 * as such its solely duty is to connect to the group owner and offer functions for sending
 * and receiving messages through its socket connections.
 */
class GroupClientActor extends GroupActor implements Runnable {

    private static final String TAG = "GroupClientActor";
    private final InetAddress groupOwnerAddress;
    private final int destPort;
    private Socket socket;
    private final Thread thread;

    public GroupClientActor(InetAddress groupOwnerAddress, int destPort,
                            GroupActorListener listener, String myIdentifier) {
        super(listener, myIdentifier);
        this.groupOwnerAddress = groupOwnerAddress;
        this.destPort = destPort;

        thread = new Thread(this);
    }

    public void connect() {

        thread.start();
    }

    public void disconnect() {
        try {
            WfdLog.d(TAG, "Disconnect called");
            thread.interrupt();
            socket.close();
        } catch (IOException e) {
            WfdLog.d(TAG, "error on closing socket", e);
        }
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
        msg.setSenderId(getIdentifier());
        WfdLog.d(TAG, "Sending connection message... ");
        sendMessage(msg);
    }

    @Override
    void sendMessage(WfdMessage msg) throws IOException {
        WfdLog.d(TAG, "Sending message");
        WfdOutputStream outstream = new WfdOutputStream(
                socket.getOutputStream());
        outstream.writeMessage(msg);
    }


    @Override
    public void run() {
        WfdInputStream inStream;
        try {
            WfdLog.d(TAG, "Opening socket connection");
            socket = new Socket();
            SocketAddress remoteAddr = new InetSocketAddress(
                    groupOwnerAddress, destPort);
            socket.connect(remoteAddr, 1000);
            inStream = new WfdInputStream(socket.getInputStream());
            establishConnection();
            WfdLog.d(TAG, "Entering read loop");
            while (!thread.isInterrupted()) {
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
}