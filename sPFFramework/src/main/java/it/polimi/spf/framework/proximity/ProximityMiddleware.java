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
package it.polimi.spf.framework.proximity;

import android.content.Context;

/**
 * Interface for components that provides proximity capabilities to SPF.
 */
public interface ProximityMiddleware {

	/**
	 * Factory to create instances of {@link ProximityMiddleware}
	 */
	interface Factory {

		/**
		 * @param context - the application context
		 * @param iface - the interface to use for incoming requests
		 * @param identifier - the spf instance identifier to be used while advertising the service
		 * @return ProximityMiddleware
		 */
		ProximityMiddleware createMiddleware(Context context, InboundProximityInterface iface, String identifier);

	}

	void connect();

	void disconnect();

	boolean isConnected();

	void sendSearchResult(String queryId, String uniqueIdentifier, String baseInfo);

	void sendSearchSignal(String sender, String queryId, String query);

	void registerAdvertisement(String advertisedProfile, long sendPeriod);

	void unregisterAdvertisement();

	boolean isAdvertising();
}
