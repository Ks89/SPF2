/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/Ks89/git/SPF/SPFApp/sPFShared/src/main/aidl/it/polimi/spf/shared/aidl/SPFProximityService.aidl
 */
package it.polimi.spf.shared.aidl;
/**
 * Interface that allows the interaction between the local application and
 * remote instances of SPF. Interactions include:
 * <ul>
 * <li>Execution of SPF Services</li>
 * <li>Read of information from remote profiles</li>
 * <li>Search for people in proximity</li>
 * </ul>
 */
public interface SPFProximityService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements it.polimi.spf.shared.aidl.SPFProximityService
{
private static final java.lang.String DESCRIPTOR = "it.polimi.spf.shared.aidl.SPFProximityService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an it.polimi.spf.shared.aidl.SPFProximityService interface,
 * generating a proxy if needed.
 */
public static it.polimi.spf.shared.aidl.SPFProximityService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof it.polimi.spf.shared.aidl.SPFProximityService))) {
return ((it.polimi.spf.shared.aidl.SPFProximityService)iin);
}
return new it.polimi.spf.shared.aidl.SPFProximityService.Stub.Proxy(obj);
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
case TRANSACTION_executeRemoteService:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
it.polimi.spf.shared.model.InvocationRequest _arg2;
if ((0!=data.readInt())) {
_arg2 = it.polimi.spf.shared.model.InvocationRequest.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
it.polimi.spf.shared.model.SPFError _arg3;
_arg3 = new it.polimi.spf.shared.model.SPFError();
it.polimi.spf.shared.model.InvocationResponse _result = this.executeRemoteService(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
if ((_arg3!=null)) {
reply.writeInt(1);
_arg3.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_sendActivity:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
it.polimi.spf.shared.model.SPFActivity _arg2;
if ((0!=data.readInt())) {
_arg2 = it.polimi.spf.shared.model.SPFActivity.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
it.polimi.spf.shared.model.SPFError _arg3;
_arg3 = new it.polimi.spf.shared.model.SPFError();
it.polimi.spf.shared.model.InvocationResponse _result = this.sendActivity(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
if ((_arg3!=null)) {
reply.writeInt(1);
_arg3.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_injectInformationIntoActivity:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
it.polimi.spf.shared.model.SPFActivity _arg2;
if ((0!=data.readInt())) {
_arg2 = it.polimi.spf.shared.model.SPFActivity.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
it.polimi.spf.shared.model.SPFError _arg3;
_arg3 = new it.polimi.spf.shared.model.SPFError();
this.injectInformationIntoActivity(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
if ((_arg2!=null)) {
reply.writeInt(1);
_arg2.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
if ((_arg3!=null)) {
reply.writeInt(1);
_arg3.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getProfileBulk:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String[] _arg2;
_arg2 = data.createStringArray();
it.polimi.spf.shared.model.SPFError _arg3;
_arg3 = new it.polimi.spf.shared.model.SPFError();
it.polimi.spf.shared.model.ProfileFieldContainer _result = this.getProfileBulk(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
if ((_arg3!=null)) {
reply.writeInt(1);
_arg3.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_startNewSearch:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
it.polimi.spf.shared.model.SPFSearchDescriptor _arg1;
if ((0!=data.readInt())) {
_arg1 = it.polimi.spf.shared.model.SPFSearchDescriptor.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
it.polimi.spf.shared.aidl.SPFSearchCallback _arg2;
_arg2 = it.polimi.spf.shared.aidl.SPFSearchCallback.Stub.asInterface(data.readStrongBinder());
it.polimi.spf.shared.model.SPFError _arg3;
_arg3 = new it.polimi.spf.shared.model.SPFError();
java.lang.String _result = this.startNewSearch(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeString(_result);
if ((_arg3!=null)) {
reply.writeInt(1);
_arg3.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_stopSearch:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
it.polimi.spf.shared.model.SPFError _arg2;
_arg2 = new it.polimi.spf.shared.model.SPFError();
this.stopSearch(_arg0, _arg1, _arg2);
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
case TRANSACTION_lookup:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
it.polimi.spf.shared.model.SPFError _arg2;
_arg2 = new it.polimi.spf.shared.model.SPFError();
boolean _result = this.lookup(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
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
private static class Proxy implements it.polimi.spf.shared.aidl.SPFProximityService
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
// SPF Service execution
/**
	 * Dispatches a request of invocation of a SPFService to a remote instance of SPF.
	 * 
	 * @param accessToken
	 *            - the access token provided by SPF upon registration
	 * @param target
	 *            - the identifier of the remote instance
	 * @param request
	 *            - an {@link InvocationRequest} with the identifier of the
	 *            service to invoke and the array of parameters
	 * @param err
	 *            - a container to notify errors that may occur
	 * @return an {@link InvocationResponse} with the outcome of the invocation
	 */
@Override public it.polimi.spf.shared.model.InvocationResponse executeRemoteService(java.lang.String accessToken, java.lang.String target, it.polimi.spf.shared.model.InvocationRequest request, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
it.polimi.spf.shared.model.InvocationResponse _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(accessToken);
_data.writeString(target);
if ((request!=null)) {
_data.writeInt(1);
request.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_executeRemoteService, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = it.polimi.spf.shared.model.InvocationResponse.CREATOR.createFromParcel(_reply);
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
	 * Dispatches a SPFActivity to a remote instance of SPF. To perform this
	 * call, the local application must be granted
	 * {@link Permission#ACTIVITY_SERVICE}
	 * 
	 * @param accessToken
	 *            - the access token provided by SPF upon registratiob
	 * @param target
	 *            - the identifier of the target instance.
	 * @param activity
	 *            - the activity to dispatch
	 * @param err
	 *            - a container to notify errors that may occur
	 * @return an {@link InvocationResponse} with the outcome of the invocation
	 *         (see
	 *         {@link ClientExecutionService#sendActivity(it.polimi.spf.shared.model.SPFActivity)}
	 */
@Override public it.polimi.spf.shared.model.InvocationResponse sendActivity(java.lang.String accessToken, java.lang.String target, it.polimi.spf.shared.model.SPFActivity activity, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
it.polimi.spf.shared.model.InvocationResponse _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(accessToken);
_data.writeString(target);
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
	 * Fill in all the information that should be automatically injected into an
	 * {@link SPFActivity}. To perform this call, the local application must be
	 * granted {@link Permission#ACTIVITY_SERVICE}
	 * 
	 * @param accessToken
	 *            - the token provided to the local app by SPF upon registration
	 * @param target
	 *            - the target of the SPFActivity
	 * @param activity
	 *            - the activity to inject the information into.
	 * @param error
	 *            - the container for error that may occur during execution
	 */
@Override public void injectInformationIntoActivity(java.lang.String accessToken, java.lang.String target, it.polimi.spf.shared.model.SPFActivity activity, it.polimi.spf.shared.model.SPFError error) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(accessToken);
_data.writeString(target);
if ((activity!=null)) {
_data.writeInt(1);
activity.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_injectInformationIntoActivity, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
activity.readFromParcel(_reply);
}
if ((0!=_reply.readInt())) {
error.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
}
// SPF Profile information retrieval
/**
	 * Retrieves the values of a set of {@link ProfileField} from the remote
	 * instance identified by the given identifier. To perform this call, the
	 * local application must be granted {@link Permission#READ_REMOTE_PROFILES}
	 * 
	 * @param accessToken
	 *            - the access token provided by SPF upon registration
	 * @param target
	 *            - the identifier of the remote instance
	 * @param fieldIdentifiers
	 *            - an array containing the identifier of the
	 *            {@link ProfileField} to read
	 * @param err
	 *            - a container to notify errors that may occur
	 * @return a {@link ProfileFieldContainer} with the value of requested
	 *         fields.
	 */
@Override public it.polimi.spf.shared.model.ProfileFieldContainer getProfileBulk(java.lang.String accessToken, java.lang.String target, java.lang.String[] fieldIdentifiers, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
it.polimi.spf.shared.model.ProfileFieldContainer _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(accessToken);
_data.writeString(target);
_data.writeStringArray(fieldIdentifiers);
mRemote.transact(Stub.TRANSACTION_getProfileBulk, _data, _reply, 0);
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
// SPF Search 
/**
	 * Starts a new search for people in proximity; events will be dispatched to
	 * a callback previously registered with
	 * {@link #registerCallback(String, SPFSearchCallback, SPFError)}. To
	 * perform this call, the local application must be granted
	 * {@link Permission#SEARCH_SERVICE}
	 * 
	 * @param accessToken
	 *             the access token provided by SPF upon registration
	 * @param searchDescriptor
	 *             the {@link SPFServiceDescriptor} containing the
	 *            configuration of the search
	 * @param err
	 *             a container to notify errors that may occur
	 * @return the identifier of the search, that can be used with
	 *         {@link #stopSearch(String, String, SPFError)}
	 * @see SPFSearchCallback
	 */
@Override public java.lang.String startNewSearch(java.lang.String accessToken, it.polimi.spf.shared.model.SPFSearchDescriptor searchDescriptor, it.polimi.spf.shared.aidl.SPFSearchCallback callback, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(accessToken);
if ((searchDescriptor!=null)) {
_data.writeInt(1);
searchDescriptor.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_startNewSearch, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
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
	 * Stops a search previously started with
	 * {@link #startNewSearch(String, it.polimi.spf.shared.model.SPFSearchDescriptor, SPFError)}
	 * . To perform this call, the local application must be granted
	 * {@link Permission#SEARCH_SERVICE}
	 * 
	 * @param accessToken
	 *             the access token provided by SPF at registration
	 * @param searchId
	 *             the id of the search to stop, as returned by
	 *            {@link #startNewSearch(String, it.polimi.spf.shared.model.SPFSearchDescriptor, SPFError)}
	 * @param err
	 *             a container to notify errors that may occur
	 */
@Override public void stopSearch(java.lang.String accessToken, java.lang.String queryId, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(accessToken);
_data.writeString(queryId);
mRemote.transact(Stub.TRANSACTION_stopSearch, _data, _reply, 0);
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
/**
	 * Checks if a person previously found is still available in proximity. To
	 * perform this call, the local application must be granted
	 * {@link Permission#SEARCH_SERVICE}
	 * 
	 * @param accessToken
	 *             the access token provided by SPF upon registration
	 * @param personIdentifier
	 *             the identifier of the person to check
	 * @param err
	 *             a container to notify errors that may occur
	 * @return true if the person is still available, false otherwise
	 */
@Override public boolean lookup(java.lang.String accessToken, java.lang.String personIdentifier, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(accessToken);
_data.writeString(personIdentifier);
mRemote.transact(Stub.TRANSACTION_lookup, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
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
}
static final int TRANSACTION_executeRemoteService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_sendActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_injectInformationIntoActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getProfileBulk = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_startNewSearch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_stopSearch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_lookup = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
}
// SPF Service execution
/**
	 * Dispatches a request of invocation of a SPFService to a remote instance of SPF.
	 * 
	 * @param accessToken
	 *            - the access token provided by SPF upon registration
	 * @param target
	 *            - the identifier of the remote instance
	 * @param request
	 *            - an {@link InvocationRequest} with the identifier of the
	 *            service to invoke and the array of parameters
	 * @param err
	 *            - a container to notify errors that may occur
	 * @return an {@link InvocationResponse} with the outcome of the invocation
	 */
public it.polimi.spf.shared.model.InvocationResponse executeRemoteService(java.lang.String accessToken, java.lang.String target, it.polimi.spf.shared.model.InvocationRequest request, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException;
/**
	 * Dispatches a SPFActivity to a remote instance of SPF. To perform this
	 * call, the local application must be granted
	 * {@link Permission#ACTIVITY_SERVICE}
	 * 
	 * @param accessToken
	 *            - the access token provided by SPF upon registratiob
	 * @param target
	 *            - the identifier of the target instance.
	 * @param activity
	 *            - the activity to dispatch
	 * @param err
	 *            - a container to notify errors that may occur
	 * @return an {@link InvocationResponse} with the outcome of the invocation
	 *         (see
	 *         {@link ClientExecutionService#sendActivity(it.polimi.spf.shared.model.SPFActivity)}
	 */
public it.polimi.spf.shared.model.InvocationResponse sendActivity(java.lang.String accessToken, java.lang.String target, it.polimi.spf.shared.model.SPFActivity activity, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException;
/**
	 * Fill in all the information that should be automatically injected into an
	 * {@link SPFActivity}. To perform this call, the local application must be
	 * granted {@link Permission#ACTIVITY_SERVICE}
	 * 
	 * @param accessToken
	 *            - the token provided to the local app by SPF upon registration
	 * @param target
	 *            - the target of the SPFActivity
	 * @param activity
	 *            - the activity to inject the information into.
	 * @param error
	 *            - the container for error that may occur during execution
	 */
public void injectInformationIntoActivity(java.lang.String accessToken, java.lang.String target, it.polimi.spf.shared.model.SPFActivity activity, it.polimi.spf.shared.model.SPFError error) throws android.os.RemoteException;
// SPF Profile information retrieval
/**
	 * Retrieves the values of a set of {@link ProfileField} from the remote
	 * instance identified by the given identifier. To perform this call, the
	 * local application must be granted {@link Permission#READ_REMOTE_PROFILES}
	 * 
	 * @param accessToken
	 *            - the access token provided by SPF upon registration
	 * @param target
	 *            - the identifier of the remote instance
	 * @param fieldIdentifiers
	 *            - an array containing the identifier of the
	 *            {@link ProfileField} to read
	 * @param err
	 *            - a container to notify errors that may occur
	 * @return a {@link ProfileFieldContainer} with the value of requested
	 *         fields.
	 */
public it.polimi.spf.shared.model.ProfileFieldContainer getProfileBulk(java.lang.String accessToken, java.lang.String target, java.lang.String[] fieldIdentifiers, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException;
// SPF Search 
/**
	 * Starts a new search for people in proximity; events will be dispatched to
	 * a callback previously registered with
	 * {@link #registerCallback(String, SPFSearchCallback, SPFError)}. To
	 * perform this call, the local application must be granted
	 * {@link Permission#SEARCH_SERVICE}
	 * 
	 * @param accessToken
	 *             the access token provided by SPF upon registration
	 * @param searchDescriptor
	 *             the {@link SPFServiceDescriptor} containing the
	 *            configuration of the search
	 * @param err
	 *             a container to notify errors that may occur
	 * @return the identifier of the search, that can be used with
	 *         {@link #stopSearch(String, String, SPFError)}
	 * @see SPFSearchCallback
	 */
public java.lang.String startNewSearch(java.lang.String accessToken, it.polimi.spf.shared.model.SPFSearchDescriptor searchDescriptor, it.polimi.spf.shared.aidl.SPFSearchCallback callback, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException;
/**
	 * Stops a search previously started with
	 * {@link #startNewSearch(String, it.polimi.spf.shared.model.SPFSearchDescriptor, SPFError)}
	 * . To perform this call, the local application must be granted
	 * {@link Permission#SEARCH_SERVICE}
	 * 
	 * @param accessToken
	 *             the access token provided by SPF at registration
	 * @param searchId
	 *             the id of the search to stop, as returned by
	 *            {@link #startNewSearch(String, it.polimi.spf.shared.model.SPFSearchDescriptor, SPFError)}
	 * @param err
	 *             a container to notify errors that may occur
	 */
public void stopSearch(java.lang.String accessToken, java.lang.String queryId, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException;
/**
	 * Checks if a person previously found is still available in proximity. To
	 * perform this call, the local application must be granted
	 * {@link Permission#SEARCH_SERVICE}
	 * 
	 * @param accessToken
	 *             the access token provided by SPF upon registration
	 * @param personIdentifier
	 *             the identifier of the person to check
	 * @param err
	 *             a container to notify errors that may occur
	 * @return true if the person is still available, false otherwise
	 */
public boolean lookup(java.lang.String accessToken, java.lang.String personIdentifier, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException;
}
