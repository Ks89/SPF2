/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/Ks89/AndroidStudioProjects/SPF/sPFShared/src/main/aidl/it/polimi/spf/shared/aidl/LocalServiceManager.aidl
 */
package it.polimi.spf.shared.aidl;
/**
 * Interface exposed by SPF that allows local applications to register services to
 * be made available for execution, and to execute the services of local
 * applications.
 */
public interface LocalServiceManager extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements it.polimi.spf.shared.aidl.LocalServiceManager
{
private static final java.lang.String DESCRIPTOR = "it.polimi.spf.shared.aidl.LocalServiceManager";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an it.polimi.spf.shared.aidl.LocalServiceManager interface,
 * generating a proxy if needed.
 */
public static it.polimi.spf.shared.aidl.LocalServiceManager asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof it.polimi.spf.shared.aidl.LocalServiceManager))) {
return ((it.polimi.spf.shared.aidl.LocalServiceManager)iin);
}
return new it.polimi.spf.shared.aidl.LocalServiceManager.Stub.Proxy(obj);
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
case TRANSACTION_sendActivityLocally:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
it.polimi.spf.shared.model.SPFActivity _arg1;
if ((0!=data.readInt())) {
_arg1 = it.polimi.spf.shared.model.SPFActivity.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
it.polimi.spf.shared.model.SPFError _arg2;
_arg2 = new it.polimi.spf.shared.model.SPFError();
it.polimi.spf.shared.model.InvocationResponse _result = this.sendActivityLocally(_arg0, _arg1, _arg2);
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
case TRANSACTION_executeLocalService:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
it.polimi.spf.shared.model.InvocationRequest _arg1;
if ((0!=data.readInt())) {
_arg1 = it.polimi.spf.shared.model.InvocationRequest.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
it.polimi.spf.shared.model.SPFError _arg2;
_arg2 = new it.polimi.spf.shared.model.SPFError();
it.polimi.spf.shared.model.InvocationResponse _result = this.executeLocalService(_arg0, _arg1, _arg2);
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
case TRANSACTION_registerService:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
it.polimi.spf.shared.model.SPFServiceDescriptor _arg1;
if ((0!=data.readInt())) {
_arg1 = it.polimi.spf.shared.model.SPFServiceDescriptor.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
it.polimi.spf.shared.model.SPFError _arg2;
_arg2 = new it.polimi.spf.shared.model.SPFError();
boolean _result = this.registerService(_arg0, _arg1, _arg2);
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
case TRANSACTION_unregisterService:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
it.polimi.spf.shared.model.SPFServiceDescriptor _arg1;
if ((0!=data.readInt())) {
_arg1 = it.polimi.spf.shared.model.SPFServiceDescriptor.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
it.polimi.spf.shared.model.SPFError _arg2;
_arg2 = new it.polimi.spf.shared.model.SPFError();
boolean _result = this.unregisterService(_arg0, _arg1, _arg2);
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
case TRANSACTION_injectInformationIntoActivity:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
it.polimi.spf.shared.model.SPFActivity _arg1;
if ((0!=data.readInt())) {
_arg1 = it.polimi.spf.shared.model.SPFActivity.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
it.polimi.spf.shared.model.SPFError _arg2;
_arg2 = new it.polimi.spf.shared.model.SPFError();
this.injectInformationIntoActivity(_arg0, _arg1, _arg2);
reply.writeNoException();
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
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
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements it.polimi.spf.shared.aidl.LocalServiceManager
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
	 * Dispatches a {@link SPFActivity} to locally. The actual target of
	 * the activity will be established by SPF according to the activity
	 * verb. To perfom this call, the local application must be granted
	 * {@link Permission#ACTIVITY_SERVICE}
	 * 
	 * @param accessToken
	 *            - the token provided to the local app by SPF upon
	 *            registration
	 * @param activity
	 *            - the activity to dispatch
	 * @param error
	 *            - the container for error that may occur during
	 *            execution
	 * @return the {@link InvocationResponse} containing the result of
	 *         the invocation. See
	 *         {@link ClientExecutionService#sendActivity(SPFActivity)}
	 */
@Override public it.polimi.spf.shared.model.InvocationResponse sendActivityLocally(java.lang.String accessToken, it.polimi.spf.shared.model.SPFActivity activity, it.polimi.spf.shared.model.SPFError error) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
it.polimi.spf.shared.model.InvocationResponse _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(accessToken);
if ((activity!=null)) {
_data.writeInt(1);
activity.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_sendActivityLocally, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = it.polimi.spf.shared.model.InvocationResponse.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
if ((0!=_reply.readInt())) {
error.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Dispatches an {@link InvocationRequest} to a service of an
	 * application registered on the local instance of SPF. To perform
	 * this call, the local application needs to be granted
	 * {@link Permission#EXECUTE_LOCAL_SERVICES}
	 * 
	 * @param accessToken
	 *            - the token provided to the local app by SPF upon
	 *            registration
	 * 
	 * @param request
	 *            - the {@link InvocationRequest} to dispatch
	 * @param error
	 *            - the container for error that may occur during
	 *            execution
	 * @return the {@link InvocationResponse} containing the result of
	 *         the invocation. See
	 *         {@link ClientExecutionService#executeService(InvocationRequest)}
	 */
@Override public it.polimi.spf.shared.model.InvocationResponse executeLocalService(java.lang.String accessToken, it.polimi.spf.shared.model.InvocationRequest request, it.polimi.spf.shared.model.SPFError error) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
it.polimi.spf.shared.model.InvocationResponse _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(accessToken);
if ((request!=null)) {
_data.writeInt(1);
request.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_executeLocalService, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = it.polimi.spf.shared.model.InvocationResponse.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
if ((0!=_reply.readInt())) {
error.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Registers a service in the local instance of SPF. After
	 * registration, external application, both local and remote, will
	 * be allowed to execute the service. To perform this call, the
	 * local application must be granted
	 * {@link Permission#REGISTER_SERVICES}
	 * 
	 * @param accessToken
	 *            - the token provided to the local app by SPF upon
	 *            registration
	 * 
	 * @param descriptor
	 *            - the descriptor of the service to register in SPF
	 * @param error
	 *            - the container for error that may occur during
	 *            execution
	 * @return true if the service was correctly registered
	 */
@Override public boolean registerService(java.lang.String accessToken, it.polimi.spf.shared.model.SPFServiceDescriptor descriptor, it.polimi.spf.shared.model.SPFError error) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(accessToken);
if ((descriptor!=null)) {
_data.writeInt(1);
descriptor.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_registerService, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
if ((0!=_reply.readInt())) {
error.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Unregisters a service from SPF. To perform this call, the local
	 * application must be granted {@link Permission#REGISTER_SERVICES}
	 * 
	 * @param accessToken
	 *            - the token provided to the local app by SPF upon
	 *            registration
	 * 
	 * @param descriptor
	 *            - the descriptor of the service to register in SPF
	 * @param error
	 *            - the container for error that may occur during
	 *            execution
	 * @return true if the service was correctly unregistered
	 */
@Override public boolean unregisterService(java.lang.String accessToken, it.polimi.spf.shared.model.SPFServiceDescriptor descriptor, it.polimi.spf.shared.model.SPFError error) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(accessToken);
if ((descriptor!=null)) {
_data.writeInt(1);
descriptor.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_unregisterService, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
if ((0!=_reply.readInt())) {
error.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	 * Fill in all the information that should be automatically injected
	 * into an {@link SPFActivity}. To perform this call, the local
	 * application must be granted {@link Permission#ACTIVITY_SERVICE}
	 * 
	 * @param accessToken
	 *            - the token provided to the local app by SPF upon
	 *            registration
	 * 
	 * @param activity
	 *            - the activity to inject the information into.
	 * @param error
	 *            - the container for error that may occur during
	 *            execution
	 */
@Override public void injectInformationIntoActivity(java.lang.String accessToken, it.polimi.spf.shared.model.SPFActivity activity, it.polimi.spf.shared.model.SPFError error) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(accessToken);
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
}
static final int TRANSACTION_sendActivityLocally = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_executeLocalService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_registerService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_unregisterService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_injectInformationIntoActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
/**
	 * Dispatches a {@link SPFActivity} to locally. The actual target of
	 * the activity will be established by SPF according to the activity
	 * verb. To perfom this call, the local application must be granted
	 * {@link Permission#ACTIVITY_SERVICE}
	 * 
	 * @param accessToken
	 *            - the token provided to the local app by SPF upon
	 *            registration
	 * @param activity
	 *            - the activity to dispatch
	 * @param error
	 *            - the container for error that may occur during
	 *            execution
	 * @return the {@link InvocationResponse} containing the result of
	 *         the invocation. See
	 *         {@link ClientExecutionService#sendActivity(SPFActivity)}
	 */
public it.polimi.spf.shared.model.InvocationResponse sendActivityLocally(java.lang.String accessToken, it.polimi.spf.shared.model.SPFActivity activity, it.polimi.spf.shared.model.SPFError error) throws android.os.RemoteException;
/**
	 * Dispatches an {@link InvocationRequest} to a service of an
	 * application registered on the local instance of SPF. To perform
	 * this call, the local application needs to be granted
	 * {@link Permission#EXECUTE_LOCAL_SERVICES}
	 * 
	 * @param accessToken
	 *            - the token provided to the local app by SPF upon
	 *            registration
	 * 
	 * @param request
	 *            - the {@link InvocationRequest} to dispatch
	 * @param error
	 *            - the container for error that may occur during
	 *            execution
	 * @return the {@link InvocationResponse} containing the result of
	 *         the invocation. See
	 *         {@link ClientExecutionService#executeService(InvocationRequest)}
	 */
public it.polimi.spf.shared.model.InvocationResponse executeLocalService(java.lang.String accessToken, it.polimi.spf.shared.model.InvocationRequest request, it.polimi.spf.shared.model.SPFError error) throws android.os.RemoteException;
/**
	 * Registers a service in the local instance of SPF. After
	 * registration, external application, both local and remote, will
	 * be allowed to execute the service. To perform this call, the
	 * local application must be granted
	 * {@link Permission#REGISTER_SERVICES}
	 * 
	 * @param accessToken
	 *            - the token provided to the local app by SPF upon
	 *            registration
	 * 
	 * @param descriptor
	 *            - the descriptor of the service to register in SPF
	 * @param error
	 *            - the container for error that may occur during
	 *            execution
	 * @return true if the service was correctly registered
	 */
public boolean registerService(java.lang.String accessToken, it.polimi.spf.shared.model.SPFServiceDescriptor descriptor, it.polimi.spf.shared.model.SPFError error) throws android.os.RemoteException;
/**
	 * Unregisters a service from SPF. To perform this call, the local
	 * application must be granted {@link Permission#REGISTER_SERVICES}
	 * 
	 * @param accessToken
	 *            - the token provided to the local app by SPF upon
	 *            registration
	 * 
	 * @param descriptor
	 *            - the descriptor of the service to register in SPF
	 * @param error
	 *            - the container for error that may occur during
	 *            execution
	 * @return true if the service was correctly unregistered
	 */
public boolean unregisterService(java.lang.String accessToken, it.polimi.spf.shared.model.SPFServiceDescriptor descriptor, it.polimi.spf.shared.model.SPFError error) throws android.os.RemoteException;
/**
	 * Fill in all the information that should be automatically injected
	 * into an {@link SPFActivity}. To perform this call, the local
	 * application must be granted {@link Permission#ACTIVITY_SERVICE}
	 * 
	 * @param accessToken
	 *            - the token provided to the local app by SPF upon
	 *            registration
	 * 
	 * @param activity
	 *            - the activity to inject the information into.
	 * @param error
	 *            - the container for error that may occur during
	 *            execution
	 */
public void injectInformationIntoActivity(java.lang.String accessToken, it.polimi.spf.shared.model.SPFActivity activity, it.polimi.spf.shared.model.SPFError error) throws android.os.RemoteException;
}
