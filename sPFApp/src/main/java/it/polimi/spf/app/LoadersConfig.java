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

package it.polimi.spf.app;

/**
 * Class that contains all ids and constants related to loaders.
 * Created by Stefano Cappa on 20/10/15.
 */
public class LoadersConfig {

    //Convention used: every class has id in the form XY.
    //Where X is the class number and Y is the loader's number inside the class X.
    //It's only a convention, but this prevent to use the same id for different loader creating many problems.

    /**
     * Constants used in {@link it.polimi.spf.app.fragments.profile.ProfileFragment}
     */
    public static final int LOAD_PROFILE_LOADER_ID = 0;
    public static final int SAVE_PROFILE_LOADER_ID = 1;

    /**
     * Constants used in {@link it.polimi.spf.app.fragments.personas.PersonasFragment}
     */
    public static final int CREATE_PERSONA_LOADER = 10;
    public static final int LOAD_PERSONAS_LOADER = 11;
    public static final int DELETE_PERSONA_LOADER = 12;
    public static final String EXTRA_PERSONA = "persona";

    /**
     * Constants used in {@link it.polimi.spf.app.fragments.contacts.PeopleFragment}
     */
    public static final int LOAD_CONTACTS_LOADER = 20;
    public static final int LOAD_REQUEST_LOADER = 21;

    /**
     * Constants used in {@link it.polimi.spf.app.fragments.contacts.CircleFragment}
     */
    public static final int LOAD_CIRCLE_LOADER = 30;
    public static final int ADD_CIRCLE_LOADER = 31;
    public static final int DELETE_CIRCLE_LOADER = 32;
    public static final String EXTRA_CIRCLE = "circle";

    /**
     * Constants used in {@link it.polimi.spf.app.fragments.NotificationFragment}
     */
    public final static int MESSAGE_LOADER_ID = 40;
    public final static int MESSAGE_DELETER_ID = 41;
    public static final String EXTRA_MESSAGE_ID = "messageId";

    /**
     * Constants used in {@link it.polimi.spf.app.fragments.appmanager.AppManagerFragment}
     */
    public static final int APP_LOADER = 50;

    /**
     * Constants used in {@link it.polimi.spf.app.fragments.ActivityFragment}
     */
    public static final int LOAD_LIST_LOADER_ID = 60;
}
