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

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import it.polimi.spf.wfd.otto.GOEvent;
import it.polimi.spf.wfd.otto.GOSocketEvent;
import it.polimi.spf.wfd.otto.NineBus;

class ServerSocketAcceptor extends Thread {
    private static final String TAG = "ServerSocketAcceptor";

    private final int THREAD_COUNT = 10;

    private final ServerSocket serverSocket;
    private boolean closed;

    public ServerSocketAcceptor(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void recycle() {
        this.closed = true;
        interrupt();
    }


    /**
     * A ThreadPool for client sockets.
     */
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            Configuration.THREAD_COUNT, Configuration.THREAD_COUNT,
            Configuration.THREAD_POOL_EXECUTOR_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());


    /**
     * Method to close the group owner sockets and kill this entire thread.
     */
    public void closeSocketAndKillThisThread() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException during close Socket", e);
            }
            pool.shutdown();
        }
    }


    @Override
    public void run() {
        Socket s;
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
        } finally {
            WfdLog.d(TAG, "ServerSocketAcceptor exiting while loop in run()");
            if (!closed) {
                WfdLog.d(TAG, "signalling error to groupOwnerActor");
                NineBus.get().post(new GOEvent("onServerSocketError"));
            }
            pool.shutdownNow();
        }
    }

}
