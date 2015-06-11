/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/Ks89/git/SPF/SPFApp/sPFShared/src/main/aidl/it/polimi/spf/shared/aidl/SPFSecurityService.aidl
 */
package it.polimi.spf.shared.aidl;
public interface SPFSecurityService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements it.polimi.spf.shared.aidl.SPFSecurityService
{
private static final java.lang.String DESCRIPTOR = "it.polimi.spf.shared.aidl.SPFSecurityService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an it.polimi.spf.shared.aidl.SPFSecurityService interface,
 * generating a proxy if needed.
 */
public static it.polimi.spf.shared.aidl.SPFSecurityService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof it.polimi.spf.shared.aidl.SPFSecurityService))) {
return ((it.polimi.spf.shared.aidl.SPFSecurityService)iin);
}
return new it.polimi.spf.shared.aidl.SPFSecurityService.Stub.Proxy(obj);
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
case TRANSACTION_registerApp:
{
data.enforceInterface(DESCRIPTOR);
it.polimi.spf.shared.model.AppDescriptor _arg0;
if ((0!=data.readInt())) {
_arg0 = it.polimi.spf.shared.model.AppDescriptor.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
it.polimi.spf.shared.aidl.SPFAppRegistrationCallback _arg1;
_arg1 = it.polimi.spf.shared.aidl.SPFAppRegistrationCallback.Stub.asInterface(data.readStrongBinder());
this.registerApp(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterApp:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.unregisterApp(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements it.polimi.spf.shared.aidl.SPFSecurityService
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
	 * Performs a request for the registration of the local app in SPF.
	 * 
	 * @param descriptor
	 *            - the {@link AppDescriptor} of the local app
	 * @param callback
	 *            - the callback that will be used to notify of the outcome of
	 *            the registration procedure.
	 */
@Override public void registerApp(it.polimi.spf.shared.model.AppDescriptor descriptor, it.polimi.spf.shared.aidl.SPFAppRegistrationCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((descriptor!=null)) {
_data.writeInt(1);
descriptor.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerApp, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	 * Unregisters the local app from SPF.
	 * 
	 * @param accessToken
	 *            - the token that was provided to the app by SPF upon
	 *            registration.
	 */
@Override public void unregisterApp(java.lang.String accessToken) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(accessToken);
mRemote.transact(Stub.TRANSACTION_unregisterApp, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_registerApp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_unregisterApp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
/**
	 * Performs a request for the registration of the local app in SPF.
	 * 
	 * @param descriptor
	 *            - the {@link AppDescriptor} of the local app
	 * @param callback
	 *            - the callback that will be used to notify of the outcome of
	 *            the registration procedure.
	 */
public void registerApp(it.polimi.spf.shared.model.AppDescriptor descriptor, it.polimi.spf.shared.aidl.SPFAppRegistrationCallback callback) throws android.os.RemoteException;
/**
	 * Unregisters the local app from SPF.
	 * 
	 * @param accessToken
	 *            - the token that was provided to the app by SPF upon
	 *            registration.
	 */
public void unregisterApp(java.lang.String accessToken) throws android.os.RemoteException;
}
