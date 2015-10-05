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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import it.polimi.spf.wfd.otto.GOEvent;
import it.polimi.spf.wfd.otto.GOSocketEvent;
import it.polimi.spf.wfd.otto.NineBus;

class ServerSocketAcceptor extends Thread {
    private static final String TAG = ServerSocketAcceptor.class.getSimpleName();

    private final ServerSocket serverSocket;
    private boolean closed;

    public ServerSocketAcceptor(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.setName("ServerSocketAcceptor");
    }

    public void recycle() {
        this.closed = true;
        interrupt();
    }

    @Override
    public void run() {
        WfdLog.d(TAG, "ServerSocketAcceptor's variable 'serverSocket' has port: " + serverSocket.getLocalPort());

        Socket s = null;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                WfdLog.d(TAG, "accept(): waiting for a new client");
                s = serverSocket.accept();
                WfdLog.d(TAG, "incoming connection");
                WfdLog.d(TAG, "IP Address: " + s.getInetAddress());
                NineBus.get().post(new GOSocketEvent("onStartGoInternalClient", s));
            }
        } catch (IOException e) {
            WfdLog.e(TAG, "ServerSocketAcceptor IOException", e);
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e1) {
                    WfdLog.e(TAG, "ServerSocketAcceptor IOException while closing socket 's' ", e1);
                }
            }
        } finally {
            WfdLog.d(TAG, "ServerSocketAcceptor exiting while loop in run()");
            if (!closed) {
                WfdLog.d(TAG, "signalling error to groupOwnerActor");
                NineBus.get().post(new GOEvent("onServerSocketError"));
            }
        }
    }
}
