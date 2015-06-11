/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/Ks89/AndroidStudioProjects/SPF/sPFShared/src/main/aidl/it/polimi/spf/shared/aidl/SPFSearchCallback.aidl
 */
package it.polimi.spf.shared.aidl;
/**
 * Callback interface for local applications to be notified of events occurred
 * while discovering people in proximity. A callback is registered in SPF using
 * {@link SPFProximityService#registerCallback(String, SPFSearchCallback, SPFError)}
 * , and after search is started with
 * {@link SPFProximityService#startNewSearch(String, SPFSearchDescriptor, SPFError)}
 * , SPF will use the methods of this callback to notify the local app.
 */
public interface SPFSearchCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements it.polimi.spf.shared.aidl.SPFSearchCallback
{
private static final java.lang.String DESCRIPTOR = "it.polimi.spf.shared.aidl.SPFSearchCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an it.polimi.spf.shared.aidl.SPFSearchCallback interface,
 * generating a proxy if needed.
 */
public static it.polimi.spf.shared.aidl.SPFSearchCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof it.polimi.spf.shared.aidl.SPFSearchCallback))) {
return ((it.polimi.spf.shared.aidl.SPFSearchCallback)iin);
}
return new it.polimi.spf.shared.aidl.SPFSearchCallback.Stub.Proxy(obj);
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
case TRANSACTION_onSearchStart:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onSearchStart(_arg0);
return true;
}
case TRANSACTION_onSearchStop:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onSearchStop(_arg0);
return true;
}
case TRANSACTION_onSearchError:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onSearchError(_arg0);
return true;
}
case TRANSACTION_onSearchResultReceived:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
it.polimi.spf.shared.model.BaseInfo _arg2;
if ((0!=data.readInt())) {
_arg2 = it.polimi.spf.shared.model.BaseInfo.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
this.onSearchResultReceived(_arg0, _arg1, _arg2);
return true;
}
case TRANSACTION_onSearchResultLost:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.onSearchResultLost(_arg0, _arg1);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements it.polimi.spf.shared.aidl.SPFSearchCallback
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
	 * Called by SPF when a search starts.
	 * 
	 */
@Override public void onSearchStart(java.lang.String queryId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(queryId);
mRemote.transact(Stub.TRANSACTION_onSearchStart, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
/**
	 * Called by SPF when a search is stopped, either by the local app with
	 * {@link SPFProximityService#stopSearch(String, String, it.polimi.spf.shared.model.SPFError)}
	 * or because it was completed.
	 * 
	 */
@Override public void onSearchStop(java.lang.String queryId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(queryId);
mRemote.transact(Stub.TRANSACTION_onSearchStop, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
/**
	 * Called by SPF when an error occurred while discovering person in
	 * proximity search. The search was stopped as a consequence.
	 * 
	 * @param queryId
	 *            - the id of the search that was stopped after an error.
	 */
@Override public void onSearchError(java.lang.String queryId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(queryId);
mRemote.transact(Stub.TRANSACTION_onSearchError, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
/**
	 * Called when a person is found in proximity.
	 * 
	 * @param uniqueIdentifier - the identifier of the found instance
	 * @param baseInfo - the {@link BaseInfo} of the found person
	 */
@Override public void onSearchResultReceived(java.lang.String queryId, java.lang.String userIdentifier, it.polimi.spf.shared.model.BaseInfo baseInfo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(queryId);
_data.writeString(userIdentifier);
if ((baseInfo!=null)) {
_data.writeInt(1);
baseInfo.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_onSearchResultReceived, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
/**
	 * Called when a result previously found is no more available.
	 * 
	 * @param uniqueIdentifier
	 *            - the identifier of the instance which is no more available
	 */
@Override public void onSearchResultLost(java.lang.String queryId, java.lang.String userIdentifier) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(queryId);
_data.writeString(userIdentifier);
mRemote.transact(Stub.TRANSACTION_onSearchResultLost, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_onSearchStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onSearchStop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onSearchError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onSearchResultReceived = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onSearchResultLost = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
/**
	 * Called by SPF when a search starts.
	 * 
	 */
public void onSearchStart(java.lang.String queryId) throws android.os.RemoteException;
/**
	 * Called by SPF when a search is stopped, either by the local app with
	 * {@link SPFProximityService#stopSearch(String, String, it.polimi.spf.shared.model.SPFError)}
	 * or because it was completed.
	 * 
	 */
public void onSearchStop(java.lang.String queryId) throws android.os.RemoteException;
/**
	 * Called by SPF when an error occurred while discovering person in
	 * proximity search. The search was stopped as a consequence.
	 * 
	 * @param queryId
	 *            - the id of the search that was stopped after an error.
	 */
public void onSearchError(java.lang.String queryId) throws android.os.RemoteException;
/**
	 * Called when a person is found in proximity.
	 * 
	 * @param uniqueIdentifier - the identifier of the found instance
	 * @param baseInfo - the {@link BaseInfo} of the found person
	 */
public void onSearchResultReceived(java.lang.String queryId, java.lang.String userIdentifier, it.polimi.spf.shared.model.BaseInfo baseInfo) throws android.os.RemoteException;
/**
	 * Called when a result previously found is no more available.
	 * 
	 * @param uniqueIdentifier
	 *            - the identifier of the instance which is no more available
	 */
public void onSearchResultLost(java.lang.String queryId, java.lang.String userIdentifier) throws android.os.RemoteException;
}
