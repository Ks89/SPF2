/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/Ks89/git/SPF/SPFApp/sPFShared/src/main/aidl/it/polimi/spf/shared/aidl/SPFAppRegistrationCallback.aidl
 */
package it.polimi.spf.shared.aidl;
/**
 * Callback interface that allows SPF to notify the local application of the
 * outcome of the registration process involving the user.
 * 
 * @see #onRegistrationSuccess(String)
 * @see #onRegistrationFailure()
 * @author darioarchetti
 * 
 */
public interface SPFAppRegistrationCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements it.polimi.spf.shared.aidl.SPFAppRegistrationCallback
{
private static final java.lang.String DESCRIPTOR = "it.polimi.spf.shared.aidl.SPFAppRegistrationCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an it.polimi.spf.shared.aidl.SPFAppRegistrationCallback interface,
 * generating a proxy if needed.
 */
public static it.polimi.spf.shared.aidl.SPFAppRegistrationCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof it.polimi.spf.shared.aidl.SPFAppRegistrationCallback))) {
return ((it.polimi.spf.shared.aidl.SPFAppRegistrationCallback)iin);
}
return new it.polimi.spf.shared.aidl.SPFAppRegistrationCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_onRegistrationSuccess:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onRegistrationSuccess(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onRegistrationFailure:
{
data.enforceInterface(DESCRIPTOR);
this.onRegistrationFailure();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements it.polimi.spf.shared.aidl.SPFAppRegistrationCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
	 * Called by SPF if the user allows the local application to interact with
	 * SPF. The Access Token, needed to call the API of SPF, is passed here as a
	 * parameter.
	 * 
	 * @param accessToken
	 *            - the accessToken.
	 */
@Override public void onRegistrationSuccess(java.lang.String accessToken) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(accessToken);
mRemote.transact(Stub.TRANSACTION_onRegistrationSuccess, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Called when the user prevents the local application from interacting with
	 * SPF.
	 */
@Override public void onRegistrationFailure() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onRegistrationFailure, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onRegistrationSuccess = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onRegistrationFailure = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
/**
	 * Called by SPF if the user allows the local application to interact with
	 * SPF. The Access Token, needed to call the API of SPF, is passed here as a
	 * parameter.
	 * 
	 * @param accessToken
	 *            - the accessToken.
	 */
public void onRegistrationSuccess(java.lang.String accessToken) throws android.os.RemoteException;
/**
	 * Called when the user prevents the local application from interacting with
	 * SPF.
	 */
public void onRegistrationFailure() throws android.os.RemoteException;
}
