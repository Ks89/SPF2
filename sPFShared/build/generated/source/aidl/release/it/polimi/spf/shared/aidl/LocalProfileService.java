/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/Ks89/git/SPF/SPFApp/sPFShared/src/main/aidl/it/polimi/spf/shared/aidl/LocalProfileService.aidl
 */
package it.polimi.spf.shared.aidl;
/**
 * Interface exposed by SPF to allow local applications to manage the user
 * profile, allowing to read and write values in bulk. To access methods of this
 * interface, applications need to be granted
 * {@link Permission#READ_LOCAL_PROFILE} and/or
 * {@link Permission#WRITE_LOCAL_PROFILE}
 */
public interface LocalProfileService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements it.polimi.spf.shared.aidl.LocalProfileService
{
private static final java.lang.String DESCRIPTOR = "it.polimi.spf.shared.aidl.LocalProfileService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an it.polimi.spf.shared.aidl.LocalProfileService interface,
 * generating a proxy if needed.
 */
public static it.polimi.spf.shared.aidl.LocalProfileService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof it.polimi.spf.shared.aidl.LocalProfileService))) {
return ((it.polimi.spf.shared.aidl.LocalProfileService)iin);
}
return new it.polimi.spf.shared.aidl.LocalProfileService.Stub.Proxy(obj);
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
case TRANSACTION_getValueBulk:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String[] _arg1;
_arg1 = data.createStringArray();
it.polimi.spf.shared.model.SPFError _arg2;
_arg2 = new it.polimi.spf.shared.model.SPFError();
it.polimi.spf.shared.model.ProfileFieldContainer _result = this.getValueBulk(_arg0, _arg1, _arg2);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
if ((_arg2!=null)) {
reply.writeInt(1);
_arg2.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_setValueBulk:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
it.polimi.spf.shared.model.ProfileFieldContainer _arg1;
if ((0!=data.readInt())) {
_arg1 = it.polimi.spf.shared.model.ProfileFieldContainer.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
it.polimi.spf.shared.model.SPFError _arg2;
_arg2 = new it.polimi.spf.shared.model.SPFError();
this.setValueBulk(_arg0, _arg1, _arg2);
reply.writeNoException();
if ((_arg2!=null)) {
reply.writeInt(1);
_arg2.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements it.polimi.spf.shared.aidl.LocalProfileService
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
	 * Reads a bulk of values from the profile of the local user. To
	 * perform this call, the local application needs to be granted
	 * {@link Permission#READ_LOCAL_PROFILE}
	 * 
	 * @param accessToken
	 *            - the access token granted by SPF to the local
	 *            application
	 * @param profileFieldIdentifiers
	 *            - the identifiers of the fields to retrieve
	 * @param err
	 *            - the container for errors that may occur during the
	 *            execution of the call
	 * @return a {@link ProfileFieldContainer} with the values of the
	 *         fields retrieved from the user profile
	 */
@Override public it.polimi.spf.shared.model.ProfileFieldContainer getValueBulk(java.lang.String accessToken, java.lang.String[] profileFieldIdentifiers, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
it.polimi.spf.shared.model.ProfileFieldContainer _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(accessToken);
_data.writeStringArray(profileFieldIdentifiers);
mRemote.transact(Stub.TRANSACTION_getValueBulk, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = it.polimi.spf.shared.model.ProfileFieldContainer.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
if ((0!=_reply.readInt())) {
err.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Writes a bulk of values to the local profile. To perform this
	 * call, the local application needs to be granted
	 * {@link Permission#WRITE_LOCAL_PROFILE}
	 * 
	 * @param accessToken
	 *            - the accessToken granted by SPF to the local
	 *            application
	 * @param container
	 *            - the {@link ProfileFieldContainer} with the
	 *            containing the modified values to write
	 * @param err
	 *            - the container for errors that may occur during the
	 *            execution of the call
	 */
@Override public void setValueBulk(java.lang.String accessToken, it.polimi.spf.shared.model.ProfileFieldContainer container, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(accessToken);
if ((container!=null)) {
_data.writeInt(1);
container.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_setValueBulk, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
err.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getValueBulk = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_setValueBulk = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
/**
	 * Reads a bulk of values from the profile of the local user. To
	 * perform this call, the local application needs to be granted
	 * {@link Permission#READ_LOCAL_PROFILE}
	 * 
	 * @param accessToken
	 *            - the access token granted by SPF to the local
	 *            application
	 * @param profileFieldIdentifiers
	 *            - the identifiers of the fields to retrieve
	 * @param err
	 *            - the container for errors that may occur during the
	 *            execution of the call
	 * @return a {@link ProfileFieldContainer} with the values of the
	 *         fields retrieved from the user profile
	 */
public it.polimi.spf.shared.model.ProfileFieldContainer getValueBulk(java.lang.String accessToken, java.lang.String[] profileFieldIdentifiers, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException;
/**
	 * Writes a bulk of values to the local profile. To perform this
	 * call, the local application needs to be granted
	 * {@link Permission#WRITE_LOCAL_PROFILE}
	 * 
	 * @param accessToken
	 *            - the accessToken granted by SPF to the local
	 *            application
	 * @param container
	 *            - the {@link ProfileFieldContainer} with the
	 *            containing the modified values to write
	 * @param err
	 *            - the container for errors that may occur during the
	 *            execution of the call
	 */
public void setValueBulk(java.lang.String accessToken, it.polimi.spf.shared.model.ProfileFieldContainer container, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException;
}
