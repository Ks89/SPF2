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
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import it.polimi.spf.framework.proximity.InboundProximityInterface;
import it.polimi.spf.framework.proximity.ProximityMiddleware;
import it.polimi.spf.framework.proximity.SPFRemoteInstance;
import it.polimi.spf.wfd.WfdHandler;
import it.polimi.spf.wfd.WfdMessage;
import it.polimi.spf.wfd.WifiDirectMiddleware;
import it.polimi.spf.wfd.exceptions.GroupException;
import it.polimi.spf.wfd.listeners.WfdMiddlewareListener;
import it.polimi.spf.wfd.util.WfdLog;

public class WFDMiddlewareAdapter implements ProximityMiddleware, WFDRemoteInstance.Factory {
    private final static String TAG = WFDMiddlewareAdapter.class.getSimpleName();

    private final WifiDirectMiddleware mMiddleware;


    public static final ProximityMiddleware.Factory FACTORY = new Factory() {

        @Override
        public ProximityMiddleware createMiddleware(Context context, int goIntentFromSPFApp, boolean isAutonomous, InboundProximityInterface iface, String identifier) {
            Log.d(TAG, "ProximityMiddleware.Factory.createMiddleware with goIntentFromSPFApp: " + goIntentFromSPFApp);
            return new WFDMiddlewareAdapter(context, goIntentFromSPFApp, isAutonomous, iface, identifier);
        }
    };

    private WFDMiddlewareAdapter(Context context, int goIntentFromSPFApp, boolean isAutonomous, InboundProximityInterface proximityInterface, String identifier) {
        Log.d(TAG, "WFDMiddlewareAdapter with goIntentFromSPFApp: " + goIntentFromSPFApp);

        WfdMiddlewareListener listener = new WFDMiddlewareListenerAdapter(proximityInterface, this);
        mMiddleware = new WifiDirectMiddleware(context, goIntentFromSPFApp, isAutonomous, identifier, listener);
    }

    @Override
    public void connect() {
        WfdLog.d(TAG, "Connect() called in WFDMiddlewareAdapter");
        mMiddleware.init();
        mMiddleware.connect();
    }

    @Override
    public void disconnect() {
        mMiddleware.disconnect();
    }

    @Override
    public boolean isConnected() {
        return mMiddleware.isConnected();
    }

    @Override
    public void sendSearchResult(String id, String uniqueIdentifier, String baseInfo) {
        WfdLog.d(TAG, "sendSearchResult called");
        WfdMessage message = new WfdMessage();
        message.put(WFDMessageContract.KEY_METHOD_ID, WFDMessageContract.ID_SEND_SEARCH_RESULT);
        message.put(WFDMessageContract.KEY_QUERY_ID, id);
        message.put(WFDMessageContract.KEY_SENDER_IDENTIFIER, uniqueIdentifier);
        message.put(WFDMessageContract.KEY_BASE_INFO, baseInfo);

        try {
            mMiddleware.sendMessageBroadcast(message);
        } catch (GroupException e) {
            Log.e(TAG, "GroupException", e);
        } catch (IOException e) {
            Log.e(TAG, "sendSearchResult IOException", e);
        }
    }

    @Override
    public void sendSearchSignal(String sender, String searchId, String query) {
        WfdLog.d(TAG, "sendSearchSignal called");
        WfdMessage message = new WfdMessage();
        message.put(WFDMessageContract.KEY_METHOD_ID, WFDMessageContract.ID_SEND_SEARCH_SIGNAL);
        message.put(WFDMessageContract.KEY_QUERY_ID, searchId);
        message.put(WFDMessageContract.KEY_SENDER_IDENTIFIER, sender);
        message.put(WFDMessageContract.KEY_QUERY, query);

        try {
            mMiddleware.sendMessageBroadcast(message);
        } catch (GroupException e) {
            Log.e(TAG, "GroupException", e);
        } catch (IOException e) {
            Log.e(TAG, "sendSearchSignal IOException", e);
        }
    }

    @Override
    public SPFRemoteInstance createRemoteInstance(String identifier) {
        WfdLog.d(TAG, "createRemoteInstance called");
        //TODO THIS TWO CODELINES SHOULD BE MOVED OUTSIDE
        mMiddleware.showConnectedMessage();
        //the client is finally connected to his GO and i notify this to the remote gui
        mMiddleware.notifyConnectedDeviceToGui(WifiDirectMiddleware.CLIENTS_ADD, identifier);
        return new WFDRemoteInstance(mMiddleware, identifier);
    }

    @Override
    public void registerAdvertisement(String profile, long period) {
        WfdLog.d(TAG, "registerAdvertisement called");
        if (mMiddleware.getWfdHandler() == null) {
            WfdLog.e(TAG, "mMiddleware.getWfdHandler() is null");
            return;
        }
        // Clear possible pending message
        mMiddleware.getWfdHandler().removeMessages(WfdHandler.SEND_ADVERTISING);
        // Queue message for next signal
        Message msg = mMiddleware.getWfdHandler().obtainMessage(WfdHandler.SEND_ADVERTISING);
        msg.getData().putString("profile", profile);
        msg.getData().putLong("period", period);
        // the first message is not delayed
        mMiddleware.getWfdHandler().sendMessage(msg);

    }

    @Override
    public void unregisterAdvertisement() {
        WfdLog.d(TAG, "unregisterAdvertisement called");
        if (mMiddleware.getWfdHandler() == null) {
            return;
        }
        mMiddleware.getWfdHandler().removeMessages(WfdHandler.SEND_ADVERTISING);
    }

    @Override
    public boolean isAdvertising() {
        WfdLog.d(TAG, "isAdvertising called");
        return mMiddleware.getWfdHandler() != null
                && mMiddleware.getWfdHandler().hasMessages(WfdHandler.SEND_ADVERTISING);
    }

    @Override
    public void notifyProximityStatus(boolean isForceKilled) {
        mMiddleware.setProximityKilledByUser(isForceKilled);
    }
}