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

    public static final boolean WFDLOG = true;

    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    public static final String SERVICE_INSTANCE = "spf_";
    public static final String IDENTIFIER = "identifier";
    public static final String PORT = "port";
    public static final String TXTRECORD_PROP_AVAILABLE = "available";

}
