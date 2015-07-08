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


import android.net.wifi.p2p.WifiP2pDevice;

import lombok.Getter;
import lombok.Setter;

/**
 * A structure to hold service information.
 */
public class WiFiP2pService {

    public static final int INVALID = -999;

    @Getter
    @Setter
    private WifiP2pDevice device;
    @Getter
    @Setter
    private String identifier = null;
    @Getter
    @Setter
    private int port = INVALID;
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
