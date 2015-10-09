package it.polimi.spf.wfd;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import it.polimi.spf.wfd.events.HandlerSendBroadcastEvent;
import it.polimi.spf.wfd.events.NineBus;
import it.polimi.spf.wfdadapter.WFDMessageContract;

/**
 * Created by Ks89 on 09/10/15.
 */
public class WfdHandler extends Handler {
    private static final String TAG = WfdHandler.class.getSimpleName();
    public static final int SEND_ADVERTISING = 1;

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

                //post the event to the WifiDirectMiddleware
                NineBus.get().post(new HandlerSendBroadcastEvent("sendMessageBroadcast", wfdMsg));


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