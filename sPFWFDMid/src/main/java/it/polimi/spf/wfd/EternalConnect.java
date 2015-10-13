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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import it.polimi.spf.wfd.events.EternalConnectEvent;
import it.polimi.spf.wfd.events.NineBus;
import it.polimi.spf.wfd.util.WfdLog;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Stefano Cappa on 13/10/15.
 */
public class EternalConnect {
    private static final String TAG = EternalConnect.class.getSimpleName();
    private static final int MAX_ETERNAL_COUNT = 4;

    @Getter
    @Setter
    private boolean eternalConnectStatus = false;
    @Getter
    @Setter
    private int eternalCounter = 0;
    private ScheduledExecutorService scheduler = null;

    private static final EternalConnect instance = new EternalConnect();

    /**
     * Method to get the instance of this class.
     *
     * @return instance of this class.
     */
    public static EternalConnect get() {
        return instance;
    }

    /**
     * Private constructor, because is a singleton class.
     */
    private EternalConnect() {
    }

    public void eternalConnect() {
        scheduler = Executors.newScheduledThreadPool(1);

        final Runnable runnable = new Runnable() {
            //to execute into another Thread
            public void run() {
                WfdLog.d(TAG, "Eternal Connect is creating a new connection, please wait...");
                if (eternalCounter >= MAX_ETERNAL_COUNT) {
                    WfdLog.d(TAG, "Eternal Connect count has reached the max value. " +
                            "Terminating Eternal Connect...");
                    if (scheduler != null) {
                        WfdLog.d(TAG, "scheduler killed");
                        scheduler.shutdown();
                        scheduler = null;
                    }
                    eternalCounter = 0;
                } else {
                    WfdLog.d(TAG, "Eternal Counter = " + eternalCounter);

                    NineBus.get().post(new EternalConnectEvent("Start another eternal connect cycle"));

                    eternalCounter++;
                }
            }
        };

        final ScheduledFuture<?> eternalHandle = scheduler.scheduleAtFixedRate(runnable, 10, 15, TimeUnit.SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() {
                eternalHandle.cancel(true);
            }
        }, 15, TimeUnit.SECONDS);
    }

    /**
     * Call this method if you want to stop the Eternal Connect procedure.
     * Call this only if you have created a connection.
     */
    public void eternalCompletedSuccessfully() {
        this.killScheduler();
        this.eternalCounter = 0;
        this.eternalConnectStatus = true;
    }

    /**
     * Kill the scheduler to stop the Eternal Connect in any case.
     */
    private void killScheduler() {
        if (scheduler != null) {
            WfdLog.d(TAG, "scheduler killed");
            scheduler.shutdown();
            scheduler = null;
        }
    }
}
