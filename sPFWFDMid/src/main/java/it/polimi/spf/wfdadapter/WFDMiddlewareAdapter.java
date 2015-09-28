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

package it.polimi.spf.wfdadapter;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import it.polimi.spf.framework.proximity.InboundProximityInterface;
import it.polimi.spf.framework.proximity.ProximityMiddleware;
import it.polimi.spf.framework.proximity.SPFRemoteInstance;
import it.polimi.spf.wfd.WfdMessage;
import it.polimi.spf.wfd.WfdMiddlewareListener;
import it.polimi.spf.wfd.WifiDirectMiddleware;

public class WFDMiddlewareAdapter implements ProximityMiddleware, WFDRemoteInstance.Factory {
    private final static String TAG = WFDMiddlewareAdapter.class.getSimpleName();

    private final WifiDirectMiddleware mMiddleware;
    private HandlerThread handlerThread;
    private WfdHandler handler;

    public static final ProximityMiddleware.Factory FACTORY = new Factory() {

        @Override
        public ProximityMiddleware createMiddleware(int goIntentFromSPFApp, Context context, InboundProximityInterface iface, String identifier) {
            Log.d(TAG, "ProximityMiddleware.Factory.createMiddleware with goIntentFromSPFApp: " + goIntentFromSPFApp);
            return new WFDMiddlewareAdapter(goIntentFromSPFApp, context, iface, identifier);
        }
    };

    private WFDMiddlewareAdapter(int goIntentFromSPFApp, Context context, InboundProximityInterface proximityInterface, String identifier) {
        Log.d(TAG, "WFDMiddlewareAdapter with goIntentFromSPFApp: " + goIntentFromSPFApp);

        WfdMiddlewareListener listener = new WFDMiddlewareListenerAdapter(proximityInterface, this);
        mMiddleware = new WifiDirectMiddleware(goIntentFromSPFApp, context, identifier, listener);
    }

    @Override
    public void connect() {
        if (!isConnected()) {
            handlerThread = new HandlerThread("wfd_middleware_adapter");
            handlerThread.start();
            handler = new WfdHandler(handlerThread.getLooper(), mMiddleware);
            mMiddleware.connect();
        } else {
            Log.w(TAG, "connect called but isConnected() == true");
        }
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            handlerThread.quit();
            handlerThread = null;
            handler = null;
            mMiddleware.disconnect();
        } else {
            Log.w(TAG, "disconnect called but isConnected() == false");
        }
    }

    @Override
    public boolean isConnected() {
        return mMiddleware.isConnected();
    }

    @Override
    public void sendSearchResult(String id, String uniqueIdentifier, String baseInfo) {
        WfdMessage message = new WfdMessage();
        message.put(WFDMessageContract.KEY_METHOD_ID, WFDMessageContract.ID_SEND_SEARCH_RESULT);
        message.put(WFDMessageContract.KEY_QUERY_ID, id);
        message.put(WFDMessageContract.KEY_SENDER_IDENTIFIER, uniqueIdentifier);
        message.put(WFDMessageContract.KEY_BASE_INFO, baseInfo);

        try {
            mMiddleware.sendMessageBroadcast(message);
        } catch (IOException e) {
            Log.e(TAG, "sendSearchResult Exception", e);
        }
    }

    @Override
    public void sendSearchSignal(String sender, String searchId, String query) {
        WfdMessage message = new WfdMessage();
        message.put(WFDMessageContract.KEY_METHOD_ID, WFDMessageContract.ID_SEND_SEARCH_SIGNAL);
        message.put(WFDMessageContract.KEY_QUERY_ID, searchId);
        message.put(WFDMessageContract.KEY_SENDER_IDENTIFIER, sender);
        message.put(WFDMessageContract.KEY_QUERY, query);

        try {
            mMiddleware.sendMessageBroadcast(message);
        } catch (IOException e) {
            Log.e(TAG, "sendSearchSignal Exception", e);
        }
    }

    @Override
    public SPFRemoteInstance createRemoteInstance(String identifier) {
        return new WFDRemoteInstance(mMiddleware, identifier);
    }

    @Override
    public void registerAdvertisement(String profile, long period) {
        if (handler == null) {
            return;
        }
        // Clear possible pending message
        handler.removeMessages(WfdHandler.SEND_ADVERTISING);
        // Queue message for next signal
        Message msg = handler.obtainMessage(WfdHandler.SEND_ADVERTISING);
        msg.getData().putString("profile", profile);
        msg.getData().putLong("period", period);
        // the first message is not delayed
        handler.sendMessage(msg);

    }

    @Override
    public void unregisterAdvertisement() {
        if (handler == null) {
            return;
        }
        handler.removeMessages(WfdHandler.SEND_ADVERTISING);
    }

    @Override
    public boolean isAdvertising() {
        return handler != null && handler.hasMessages(WfdHandler.SEND_ADVERTISING);
    }

    private static class WfdHandler extends Handler {
        static final int SEND_ADVERTISING = 1;
        private final WifiDirectMiddleware mMiddlewareRef;

        public WfdHandler(Looper looper, WifiDirectMiddleware mMiddleware) {
            super(looper);
            mMiddlewareRef = mMiddleware;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEND_ADVERTISING:
                    Log.d(TAG, "sending SPFAdvertising signal handleMessage");
                    long delay = msg.getData().getLong("period");
                    String profile = msg.getData().getString("profile");

                    WfdMessage wfdMsg = new WfdMessage();
                    wfdMsg.put(WFDMessageContract.KEY_METHOD_ID, WFDMessageContract.ID_SEND_SPF_ADVERTISING);
                    wfdMsg.put(WFDMessageContract.KEY_ADV_PROFILE, profile);

                    try {
                        mMiddlewareRef.sendMessageBroadcast(wfdMsg);
                    } catch (IOException e) {
                        Log.e(TAG, "Exception handleMessage msg", e);
                    }

                    Message msgNew = obtainMessage(SEND_ADVERTISING);
                    msgNew.getData().putString("profile", profile);
                    msgNew.getData().putLong("period", delay);
                    sendMessageDelayed(msgNew, delay);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

}
