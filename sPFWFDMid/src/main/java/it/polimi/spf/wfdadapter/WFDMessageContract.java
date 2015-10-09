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
package it.polimi.spf.wfdadapter;

public interface WFDMessageContract {

	String KEY_METHOD_ID = "methodName";
	String KEY_REQUEST = "request";
	String KEY_RESPONSE = "request";
	String KEY_TOKEN = "token";
	String KEY_ADV_PROFILE = "advProfile";
	String KEY_FIELD_IDENTIFIERS = "fieldIdentifiers";
	String KEY_APP_IDENTIFIER = "appIdentifier";
	String KEY_SENDER_IDENTIFIER = "senderIdentifier";
	String KEY_ACTION = "action";
	String KEY_QUERY_ID = "queryId";
	String KEY_QUERY = "query";
	String KEY_BASE_INFO = "baseInfo";
	String KEY_ACTIVITY = "activity";

	int ID_EXECUTE_SERVICE = 0;
	int ID_GET_PROFILE_BULK = 1;
	int ID_SEND_CONTACT_REQUEST = 2;
	int ID_SEND_NOTIFICATION = 3;
	int ID_SEND_SEARCH_SIGNAL = 4;
	int ID_SEND_SEARCH_RESULT = 5;
	int ID_SEND_SPF_ADVERTISING = 6;
	int ID_SEND_ACTIVITY = 7;
}
