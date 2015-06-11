/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/Ks89/git/SPF/SPFApp/sPFShared/src/main/aidl/it/polimi/spf/shared/aidl/ClientExecutionService.aidl
 */
package it.polimi.spf.shared.aidl;
/**
 * Callback interface that allows SPF to communicate with applications that
 * registered services. In particular, SPF uses this interface to dispatch
 * {@link SPFActivity} and {@link InvocationRequest} for services registered by
 * the local application.
 */
public interface ClientExecutionService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements it.polimi.spf.shared.aidl.ClientExecutionService
{
private static final java.lang.String DESCRIPTOR = "it.polimi.spf.shared.aidl.ClientExecutionService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an it.polimi.spf.shared.aidl.ClientExecutionService interface,
 * generating a proxy if needed.
 */
public static it.polimi.spf.shared.aidl.ClientExecutionService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof it.polimi.spf.shared.aidl.ClientExecutionService))) {
return ((it.polimi.spf.shared.aidl.ClientExecutionService)iin);
}
return new it.polimi.spf.shared.aidl.ClientExecutionService.Stub.Proxy(obj);
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
case TRANSACTION_executeService:
{
data.enforceInterface(DESCRIPTOR);
it.polimi.spf.shared.model.InvocationRequest _arg0;
if ((0!=data.readInt())) {
_arg0 = it.polimi.spf.shared.model.InvocationRequest.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
it.polimi.spf.shared.model.InvocationResponse _result = this.executeService(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_sendActivity:
{
data.enforceInterface(DESCRIPTOR);
it.polimi.spf.shared.model.SPFActivity _arg0;
if ((0!=data.readInt())) {
_arg0 = it.polimi.spf.shared.model.SPFActivity.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
it.polimi.spf.shared.model.InvocationResponse _result = this.sendActivity(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements it.polimi.spf.shared.aidl.ClientExecutionService
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
	 * Dispatches an {@link InvocationRequest} to the local application
	 * to perform the execution.
	 * 
	 * @param request
	 *            - the request containing the detail of the invocation
	 * @return an {@link InvocationResponse} containing the result of
	 *         the invocation
	 */
@Override public it.polimi.spf.shared.model.InvocationResponse executeService(it.polimi.spf.shared.model.InvocationRequest request) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
it.polimi.spf.shared.model.InvocationResponse _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((request!=null)) {
_data.writeInt(1);
request.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_executeService, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = it.polimi.spf.shared.model.InvocationResponse.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Dispatches a {@link SPFActivity} to the SPF Service exposing this
	 * interface
	 * 
	 * @param activity
	 *            - the activity to handle
	 * @return an {@link InvocationResponse} containing the result of
	 *         the invocation, in particular <code>true</code> if the
	 *         Activity was correctly handled, <code>false</code> if the
	 *         activity was not handled but no error occurred, or the
	 *         error in case of failure.
	 */
@Override public it.polimi.spf.shared.model.InvocationResponse sendActivity(it.polimi.spf.shared.model.SPFActivity activity) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
it.polimi.spf.shared.model.InvocationResponse _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((activity!=null)) {
_data.writeInt(1);
activity.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_sendActivity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = it.polimi.spf.shared.model.InvocationResponse.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_executeService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_sendActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
/**
	 * Dispatches an {@link InvocationRequest} to the local application
	 * to perform the execution.
	 * 
	 * @param request
	 *            - the request containing the detail of the invocation
	 * @return an {@link InvocationResponse} containing the result of
	 *         the invocation
	 */
public it.polimi.spf.shared.model.InvocationResponse executeService(it.polimi.spf.shared.model.InvocationRequest request) throws android.os.RemoteException;
/**
	 * Dispatches a {@link SPFActivity} to the SPF Service exposing this
	 * interface
	 * 
	 * @param activity
	 *            - the activity to handle
	 * @return an {@link InvocationResponse} containing the result of
	 *         the invocation, in particular <code>true</code> if the
	 *         Activity was correctly handled, <code>false</code> if the
	 *         activity was not handled but no error occurred, or the
	 *         error in case of failure.
	 */
public it.polimi.spf.shared.model.InvocationResponse sendActivity(it.polimi.spf.shared.model.SPFActivity activity) throws android.os.RemoteException;
}
