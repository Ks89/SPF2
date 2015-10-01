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

package it.polimi.spf.wfd.actionlisteners;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import it.polimi.spf.wfd.WfdLog;

/**
 * Extremely customizable WifiP2pManager.ActionListener.
 * The only parameter of the constructor the can't be null, is context.
 * If tag==null, this class chooses a default tag "ActionListenerTag".
 * <p></p>
 * For example, if successToast==null, the Toasts in onSuccess will never displayed.
 * <p></p>
 * Created by Stefano Cappa on 16/07/15.
 */
public class CustomizableActionListener implements WifiP2pManager.ActionListener {

    private final Context context;
    private final String successLog, successToast, failLog, failToast, tag;
    private final boolean deepCheckOnFailure;


    /**
     * Constructor of CustomizableActionListener.
     * successLog, successToast, failLog, failToast can be == null, and if this happens the associated action is skipped.
     *
     * @param context      Context necessary to display Toasts.
     * @param tag          String that represents the tag for Log.d, but if is == null, this constructor uses "ActionListenerTag" as tag.
     * @param successLog   String that represent the message for Log.d in onSuccess
     * @param successToast String that represent the message for Toasts in onSuccess
     * @param failLog      String that represent the message for Log.d in onFailure. The failure code will be added automatically.
     * @param failToast    String that represent the message for Toasts in onFailure
     */
    public CustomizableActionListener(@NonNull Context context,
                                      String tag,
                                      String successLog, String successToast,
                                      String failLog, String failToast, boolean deepCheckOnFailure) {
        this.context = context;
        this.successLog = successLog;
        this.successToast = successToast;
        this.failLog = failLog;
        this.failToast = failToast;
        this.deepCheckOnFailure = deepCheckOnFailure;

        if (tag == null) {
            this.tag = "ActionListenerTag";
        } else {
            this.tag = tag;
        }
    }

    @Override
    public void onSuccess() {
        if (successLog != null) {
            WfdLog.d(tag, successLog);
        }
        if (context != null && successToast != null) {
            Toast.makeText(context, successToast, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(int reason) {

        //if i want a detailed log messages, pass
        //deepCheckOnFailure=true to the constructor of this class
        if (deepCheckOnFailure) {
            // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
            switch (reason) {
                case WifiP2pManager.P2P_UNSUPPORTED:
                    Log.e(tag, failLog + ", reason " + reason + " -> P2P isn't supported on this device.");
                    break;
                case WifiP2pManager.BUSY:
                    Log.e(tag, failLog + ", reason " + reason + " -> Busy");
                    break;
                case WifiP2pManager.ERROR:
                    Log.e(tag, failLog + ", reason " + reason + " -> Error");
                    break;
                case WifiP2pManager.NO_SERVICE_REQUESTS:
                    /*
                     * Indicates that the {@link #discoverServices} failed because no service
                     * requests are added. Use addServiceRequest in WifiP2pManager.java to add a service
                     * request.
                     */
                    Log.e(tag, failLog + ", reason " + reason + " -> No service request");
                    break;
                default:
                    Log.e(tag, failLog + ", reason " + reason + " -> ERROR!!! Unknown reason code!!!!");
                    break;
            }
        } else {
            if (failLog != null) {
                Log.e(tag, failLog + ", reason: " + reason);
            }
            if (context != null && failToast != null) {
                Toast.makeText(context, failToast, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
