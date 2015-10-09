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

package it.polimi.spf.wfd.events;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Extends Otto Bus to extend it with the ability to post on the main thread
 */
public class NineBus extends Bus {

    private final Handler mainThread = new Handler(Looper.getMainLooper());
    private static NineBus bus;

    private NineBus(ThreadEnforcer t) {
        super(t);
    }

    public synchronized static Bus get() {
        if (bus == null) {
            bus = new NineBus(ThreadEnforcer.ANY);
        }
        return bus;
    }

    @Override
    public void post(final Object event) {
        Log.d(event.getClass().getSimpleName(), event.toString());

        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    NineBus.super.post(event);
                }
            });
        }
    }
}
