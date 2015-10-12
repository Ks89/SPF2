/*
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
import android.os.Message;
import android.util.Log;

import it.polimi.spf.wfd.events.HandlerSendBroadcastEvent;
import it.polimi.spf.wfd.events.NineBus;
import it.polimi.spf.wfdadapter.WFDMessageContract;

/**
 * Created by Stefano Cappa on 09/10/15.
 */
public class WfdHandler extends Handler {
    private static final String TAG = WfdHandler.class.getSimpleName();
    public static final int SEND_ADVERTISING = 1;

    public WfdHandler(Looper looper) {
        super(looper);
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

                //post the event to the WifiDirectMiddleware
                NineBus.get().post(new HandlerSendBroadcastEvent("sendsMessageBroadcast", wfdMsg));

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