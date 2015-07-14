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
package it.polimi.spf.framework.local;

import android.os.RemoteException;
import android.util.Log;

import it.polimi.spf.framework.SPF;
import it.polimi.spf.framework.Utils;
import it.polimi.spf.framework.proximity.SPFRemoteInstance;
import it.polimi.spf.framework.security.AppAuth;
import it.polimi.spf.framework.security.PermissionDeniedException;
import it.polimi.spf.framework.security.SPFSecurityMonitor;
import it.polimi.spf.framework.security.TokenNotValidException;
import it.polimi.spf.framework.services.ActivityInjector;

import it.polimi.spf.framework.services.SPFServiceRegistry;
import it.polimi.spf.shared.aidl.SPFProximityService;
import it.polimi.spf.shared.aidl.SPFSearchCallback;
import it.polimi.spf.shared.model.InvocationRequest;
import it.polimi.spf.shared.model.InvocationResponse;
import it.polimi.spf.shared.model.Permission;
import it.polimi.spf.shared.model.ProfileFieldContainer;
import it.polimi.spf.shared.model.SPFActivity;
import it.polimi.spf.shared.model.SPFError;
import it.polimi.spf.shared.model.SPFSearchDescriptor;

/**
 * @author darioarchetti
 * 
 */
/* package */class SPFProximityServiceImpl extends SPFProximityService.Stub {
	private final static String TAG = "SPFProximityService";
	private final SPFSecurityMonitor mSecurityMonitor = SPF.get().getSecurityMonitor();

	@Override
	public InvocationResponse executeRemoteService(String accessToken, String targetId, InvocationRequest request, SPFError err) throws RemoteException {
		Utils.logCall(TAG, "executeRemoteService", accessToken, targetId, request, err);

		try {
			mSecurityMonitor.validateAccess(accessToken, Permission.EXECUTE_REMOTE_SERVICES);
		} catch (TokenNotValidException e) {
			err.setCode(SPFError.TOKEN_NOT_VALID_ERROR_CODE);
			return null;
		} catch (PermissionDeniedException e) {
			err.setCode(SPFError.PERMISSION_DENIED_ERROR_CODE);
			return null;
		}

		try {
			SPFRemoteInstance target = SPF.get().getPeopleManager().getPerson(targetId);
			if (target == null) {
				err.setCode(SPFError.INSTANCE_NOT_FOUND_ERROR_CODE);
				return InvocationResponse.error("target cannot be found in PeopleManager");
			}

			return target.executeService(request);
		} catch (Throwable t) {
			Log.e(TAG, "Error executing service", t);
			err.setCode(SPFError.NETWORK_ERROR_CODE);
			return InvocationResponse.error("SPF Internal error while executing service. See SPF log for details");
		}
	}

	@Override
	public InvocationResponse sendActivity(String accessToken, String targetId, SPFActivity activity, SPFError err) {
		Utils.logCall(TAG, "sendActivity", accessToken, activity, err);

		try {
			mSecurityMonitor.validateAccess(accessToken, Permission.ACTIVITY_SERVICE);
		} catch (TokenNotValidException e) {
			err.setCode(SPFError.TOKEN_NOT_VALID_ERROR_CODE);
			return null;
		} catch (PermissionDeniedException e) {
			err.setCode(SPFError.PERMISSION_DENIED_ERROR_CODE);
			return null;
		}

		try {
			SPFRemoteInstance target = SPF.get().getPeopleManager().getPerson(targetId);
			if (target == null) {
				err.setCode(SPFError.INSTANCE_NOT_FOUND_ERROR_CODE);
				return InvocationResponse.error("target cannot be found in PeopleManager");
			}

			return target.sendActivity(activity);
		} catch (Throwable t) {
			Log.e(TAG, "Error sending activity", t);
			err.setCode(SPFError.NETWORK_ERROR_CODE);
			return InvocationResponse.error("SPF Internal error while sending activity. See SPF log for details");
		}
	}

	@Override
	public ProfileFieldContainer getProfileBulk(String accessToken, String targetId, String[] fields, SPFError err) throws RemoteException {
		Utils.logCall(TAG, "getProfileBulk", accessToken, targetId, fields, err);

		AppAuth auth;
		try {
			auth = mSecurityMonitor.validateAccess(accessToken, Permission.READ_REMOTE_PROFILES);
		} catch (TokenNotValidException e) {
			err.setCode(SPFError.TOKEN_NOT_VALID_ERROR_CODE);
			return null;
		} catch (PermissionDeniedException e) {
			err.setCode(SPFError.PERMISSION_DENIED_ERROR_CODE);
			return null;
		}

		if (targetId == null || fields == null) {
			err.setCode(SPFError.ILLEGAL_ARGUMENT_ERROR_CODE);
			return null;

		}
		SPFRemoteInstance targetInstance = SPF.get().getPeopleManager().getPerson(targetId);
		if (targetInstance == null) {
			err.setCode(SPFError.INSTANCE_NOT_FOUND_ERROR_CODE);
		}

		try {
			return targetInstance.getProfileBulk(fields, auth.getAppIdentifier());
		} catch (Throwable e) {
			err.setCode(SPFError.NETWORK_ERROR_CODE);
			return null;
		}
	}
	
	@Override
	public String startNewSearch(String accessToken, SPFSearchDescriptor descriptor, SPFSearchCallback callback, SPFError err) throws RemoteException {
		Utils.logCall(TAG, "startNewSearch", accessToken, descriptor, err);
		AppAuth auth;
		try {
			auth = mSecurityMonitor.validateAccess(accessToken, Permission.SEARCH_SERVICE);
		} catch (TokenNotValidException e) {
			err.setCode(SPFError.TOKEN_NOT_VALID_ERROR_CODE);
			return null;
		} catch (PermissionDeniedException e) {
			err.setCode(SPFError.PERMISSION_DENIED_ERROR_CODE);
			return null;
		}
		String appIdentifier = auth.getAppIdentifier();
		return SPF.get().getSearchManager().startSearch(appIdentifier, descriptor, callback);
	}

	@Override
	public void stopSearch(String accessToken, String queryId, SPFError err) throws RemoteException {
		Utils.logCall(TAG, "stopSearch", accessToken, queryId, err);

		try {
			mSecurityMonitor.validateAccess(accessToken, Permission.SEARCH_SERVICE);
		} catch (TokenNotValidException e) {
			err.setCode(SPFError.TOKEN_NOT_VALID_ERROR_CODE);
			return;
		} catch (PermissionDeniedException e) {
			err.setCode(SPFError.PERMISSION_DENIED_ERROR_CODE);
			return;
		}

		SPF.get().getSearchManager().stopSearch(queryId);
	}

	@Override
	public boolean lookup(String accessToken, String personIdentifier, SPFError err) throws RemoteException {
		Utils.logCall(TAG, "lookup", accessToken, personIdentifier, err);

		try {
			mSecurityMonitor.validateAccess(accessToken, Permission.SEARCH_SERVICE);
		} catch (TokenNotValidException e) {
			err.setCode(SPFError.TOKEN_NOT_VALID_ERROR_CODE);
			return false;
		} catch (PermissionDeniedException e) {
			err.setCode(SPFError.PERMISSION_DENIED_ERROR_CODE);
			return false;
		}

		return SPF.get().getPeopleManager().hasPerson(personIdentifier);
	}

	@Override
	public void injectInformationIntoActivity(String token, String target, SPFActivity activity, SPFError err) throws RemoteException {
		Utils.logCall(TAG, "injectInformationIntoActivity", token, target, activity, err);

		try {
			mSecurityMonitor.validateAccess(token, Permission.ACTIVITY_SERVICE);
		} catch (TokenNotValidException e) {
			err.setCode(SPFError.TOKEN_NOT_VALID_ERROR_CODE);
			return;
		} catch (PermissionDeniedException e) {
			err.setCode(SPFError.PERMISSION_DENIED_ERROR_CODE);
			return;
		}

		ActivityInjector.injectDataInActivity(activity, target);
	}


	//this is the implementation of the method definied in AIDL file in SPFShared
	@Override
	public InvocationResponse setGoIntent(int goIntent, String accessToken, String targetId, SPFError err) throws RemoteException {
		Log.d("SPFProximityServiceImpl", "setgointent " + goIntent + ", accessToken: " + accessToken + ", targetId: " + targetId);

//		try {
//			Log.d("SPFProximityServiceImpl", "mSecurityMonitor.validateAccess REQUEST");
//			mSecurityMonitor.validateAccess(accessToken, Permission.BECOME_GROUPOWNER);
//			Log.d("SPFProximityServiceImpl", "mSecurityMonitor.validateAccess OK");
//		} catch (TokenNotValidException e) {
//			Log.d("SPFProximityServiceImpl", "mSecurityMonitor.validateAccess ERROR TOKEN_NOT_VALID_ERROR_CODE");
//			err.setCode(SPFError.TOKEN_NOT_VALID_ERROR_CODE);
//			return null;
//		} catch (PermissionDeniedException e) {
//			Log.d("SPFProximityServiceImpl", "mSecurityMonitor.validateAccess ERROR PERMISSION_DENIED_ERROR_CODE");
//			err.setCode(SPFError.PERMISSION_DENIED_ERROR_CODE);
//			return null;
//		}

		Log.d("SPFProximityServiceImpl","requesting SPFRemoteInstance");

		try {
			SPFRemoteInstance target = SPF.get().getPeopleManager().getPerson(targetId);
			Log.d("SPFProximityServiceImpl", "target= " + target);
			if (target == null) {
				Log.d("SPFProximityServiceImpl", "SPFRemoteInstance ERROR INSTANCE_NOT_FOUND_ERROR_CODE");
				err.setCode(SPFError.INSTANCE_NOT_FOUND_ERROR_CODE);
				return InvocationResponse.error("target cannot be found in PeopleManager");
			}

			Log.d("SPFProximityServiceImpl", "Requesting setGoIntent to WFDMiddleware with gointent: " + goIntent);
			target.setGoIntent(goIntent);

			return InvocationResponse.result("goIntent sent to middleware");
		} catch (Throwable t) {
			Log.e("SPFProximityServiceImpl", "Error setting gointent", t);
			err.setCode(SPFError.NETWORK_ERROR_CODE);
			return InvocationResponse.error("SPF Internal error while setting gointent. See SPF log for details");
		}
	}
}
