/* 
 * Copyright 2014 Jacopo Aliprandi, Dario Archetti
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

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import it.polimi.spf.framework.ExceptionLogger;
import it.polimi.spf.framework.SPF;
import it.polimi.spf.framework.SPFContext;
import it.polimi.spf.wfdadapter.WFDMiddlewareAdapter;

/**
 * {@link Application} subclass that serves two main purposes:
 * <ul>
 * <li>Initializing SPF singleton with a context reference</li>
 * <li>Local broadcaster for application-wide events</li>
 * <ul>
 */
public class SPFApp extends Application {
    private static final String TAG = SPFApp.class.getSimpleName();
    private static final String STOPSPF = "it.polimi.spf.app.stop";

    private RemoteViews contentView;
    private Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();


        //Intent for the stop button in the notification layout
        Intent stopSpf = new Intent(STOPSPF);
        PendingIntent pendingIntentStop = PendingIntent.getBroadcast(this, 0, stopSpf, 0);


        //intent for the click inside the notification area
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //set my custom contentView with a layout
        this.contentView = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.notification_layout);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(pIntent);
        builder.setTicker(getResources().getString(R.string.notification_ticker));
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setAutoCancel(true);


        //------------------------------------------------------------------------------------
        // when you click on buttons in a notification, the actions will be implemented in
        // SPSService in the Framework's module.
        //add the onclickpendingintent to the button
        this.contentView.setOnClickPendingIntent(R.id.stopSpfButton, pendingIntentStop);
        //------------------------------------------------------------------------------------

        //build the notification
        this.notification = builder.build();

        // Set data in the RemoteViews programmatically
        this.setContentViewWithMinimalElements();

        /* Workaround: Need to set the content view here directly on the notification.
         * NotificationCompatBuilder contains a bug that prevents this from working on platform
         * versions HoneyComb.
         * See https://code.google.com/p/android/issues/detail?id=30495
         */
        notification.contentView = contentView;

        // Initialize SPF
        //SPFContext.initialize(this, AlljoynProximityMiddleware.FACTORY);
        // Use this line to initialize SPF on Wi-Fi Direct
        SPFContext.initialize(this, 0, true, WFDMiddlewareAdapter.FACTORY);
        SPFContext.get().setAppRegistrationHandler(new PopupAppRegistrationHandler());

        SPFContext.get().setServiceNotification(notification);

        // Set exception logger to log uncaught exceptions
        ExceptionLogger.installAsDefault(this);
    }


    public void initSPF(int goIntent, boolean isAutonomous) {
        // Initialize SPF
        // Use this line to initialize SPF on Wi-Fi Direct
        SPF.get().disconnect();

        SPFContext.initializeForcedNoSingleton(this, goIntent, isAutonomous, WFDMiddlewareAdapter.FACTORY);
        SPFContext.get().setAppRegistrationHandler(new PopupAppRegistrationHandler());

        SPFContext.get().setServiceNotification(notification);

        // Set exception logger to log uncaught exceptions
        ExceptionLogger.installAsDefault(this);
    }

    public void updateIdentifier(int goIntent) {
        Log.d(TAG, "Called updateIdentifier with goIntent: " + goIntent);
        SPF.get().updateIdentifier(goIntent);
    }

    /**
     * Method to set the contentview with Title, Text and Image.
     */
    private void setContentViewWithMinimalElements() {
        // Set data in the RemoteViews programmatically
        contentView.setImageViewResource(R.id.imageView, R.drawable.ic_launcher);
        contentView.setTextViewText(R.id.title_text_notification, getResources().getString(R.string.notification_title));
        contentView.setTextViewText(R.id.message_notification, getResources().getString(R.string.notification_text));
    }
}
