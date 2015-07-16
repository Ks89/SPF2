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

/**
 * Class to configure some important attribute.
 * <p></p>
 * Created by Stefano Cappa on 16/07/15.
 */
public class Configuration {


    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    public static final String SERVICE_INSTANCE = "spf_";
    public static final String IDENTIFIER = "identifier";
    public static final String PORT = "port";


    public static final int GROUPOWNER_PORT = 4545;
    public static final int CLIENT_PORT = 5000;
    public static final int THREAD_COUNT = 20; //maximum number of clients that this GO can manage
    public static final int THREAD_POOL_EXECUTOR_KEEP_ALIVE_TIME = 10; //don't touch this!!!

    public static final String TXTRECORD_PROP_AVAILABLE = "available";

    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int FIRSTMESSAGEXCHANGE = 0x400 + 2;

    public static final String MESSAGE_READ_MSG = "MESSAGE_READ";
    public static final String FIRSTMESSAGEXCHANGE_MSG = "FIRSTMESSAGEXCHANGE";

}
