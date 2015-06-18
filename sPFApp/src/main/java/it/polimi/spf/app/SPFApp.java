/* 
 * Copyright 2014 Jacopo Aliprandi, Dario Archetti
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

//import it.polimi.spf.alljoyn.AlljoynProximityMiddleware;
import it.polimi.spf.framework.ExceptionLogger;
import it.polimi.spf.framework.SPFContext;
import it.polimi.spf.wfdadapter.WFDMiddlewareAdapter;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.format.Time;
import android.widget.RemoteViews;

/**
 * {@link Application} subclass that serves two main purposes:
 * <ul>
 * <li>Initializing SPF singleton with a context reference</li>
 * <li>Local broadcaster for application-wide events</li>
 * <ul>
 * 
 * @author aliprax
 * 
 */
public class SPFApp extends Application {


	private static final String STOPSPF = "it.polimi.spf.app.stop";

	private RemoteViews contentView;

	private NotificationCompat.Builder builder;
	private Notification notification;
	private PendingIntent pendingIntentStop;
	private boolean mIsOn = false;
	private Context mContext;

	private NotificationManager notificationManager;



	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize SPF
		//SPFContext.initialize(this, AlljoynProximityMiddleware.FACTORY);
		// Use this line to initialize SPF on Wi-Fi Direct
		SPFContext.initialize(this, WFDMiddlewareAdapter.FACTORY);
		SPFContext.get().setAppRegistrationHandler(new PopupAppRegistrationHandler());
		
		// Set notification to show when SPF service is in foreground
//		Intent intent = new Intent(this, MainActivity.class);
//		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//		Notification n = new Notification.Builder(this)
//			.setSmallIcon(R.drawable.ic_launcher)
//			.setTicker("spf is active")
//			.setContentTitle("SPF")
//			.setContentText("SPF is active.")
//			.setContentIntent(pIntent)
//			.build();



		//******
		//per mostare l'ora e i minuti della notifica
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();

		Intent stopSpf = new Intent(STOPSPF);
    	this.pendingIntentStop = PendingIntent.getBroadcast(this, 0, stopSpf, 0);

		//intent per il clic sulla notifica
		Intent notificationIntent = new Intent(this, MainActivity.class);
//		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		this.contentView = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.notification_layout);

			builder = new NotificationCompat.Builder(this);
			builder.setContentIntent(pIntent);
			builder.setTicker("spf is active");
			builder.setSmallIcon(R.drawable.ic_launcher);
			builder.setAutoCancel(true);

		this.contentView.setOnClickPendingIntent(R.id.stopSpfButton, this.pendingIntentStop);

		this.notification = builder.build();

			// Set data in the RemoteViews programmatically.notification_lollipop
			this.setContentViewWithMinimalElements(today);

        /* Workaround: Need to set the content view here directly on the notification.
         * NotificationCompatBuilder contains a bug that prevents this from working on platform
         * versions HoneyComb.
         * See https://code.google.com/p/android/issues/detail?id=30495
         */
			notification.contentView = contentView;

			// Add a big content view to the notification if supported.
			// Support for expanded notifications was added in API level 16.
			// (The normal contentView is shown when the notification is collapsed, when expanded the
			// big content view set here is displayed.)
//			if (Build.VERSION.SDK_INT >= 16) {
//				// Inflate and set the layout for the expanded notification view
//				RemoteViews expandedView = new RemoteViews(this.getPackageName(), R.layout.notification_expanded);
//				expandedView.setImageViewResource(R.id.imageView, R.drawable.notification_lollipop_beta3);
////        expandedView.setTextViewText(R.id.filaname_notification, download.getFileName());
////        expandedView.setTextViewText(R.id.message_notification, dms.getResources().getString(R.string.notification_completed_download));
//				expandedView.setTextViewText(R.id.time_notification, today.format("%k:%M").toString());
//				expandedView.setTextViewText(R.id.count_notification, this.numberOfDownloadingFiles + "");
//
//				notification.bigContentView = this.setButtonAndOnClickInNotification(expandedView);
//			}

//			this.notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

//			notificationManager.notify(69990, notification);








		//********

		SPFContext.get().setServiceNotification(notification);
		
		// Set exception logger to log uncaught exceptions
		ExceptionLogger.installAsDefault(this);

	}



	/**
	 * Setta contentview usando solamente l'immagine, il tempo e il numero delle notifiche,
	 * nome applicazione e un messaggio generico di avvio.
	 */
	private void setContentViewWithMinimalElements(Time today) {
		// Set data in the RemoteViews programmatically.notification_lollipop
		contentView.setImageViewResource(R.id.imageView, R.drawable.ic_launcher);
		contentView.setTextViewText(R.id.filaname_notification, "SPF");
		contentView.setTextViewText(R.id.message_notification, "SPF is active.");
//		contentView.setTextViewText(R.id.time_notification, today.format("%k:%M").toString());
	}
}
