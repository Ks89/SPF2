
package it.polimi.spf.wfd;


import android.net.wifi.p2p.WifiP2pDevice;

import lombok.Getter;
import lombok.Setter;

/**
 * A structure to hold service information.
 */
public class WiFiP2pService {
    @Getter
    @Setter
    private WifiP2pDevice device;
    @Getter
    @Setter
    private String identifier = null;
    @Getter
    @Setter
    private String port = null;
    @Getter
    @Setter
    private String peerAddress = null;
    @Getter
    @Setter
    private String instanceName = null;
    @Getter
    @Setter
    private String serviceRegistrationType = null;
}
