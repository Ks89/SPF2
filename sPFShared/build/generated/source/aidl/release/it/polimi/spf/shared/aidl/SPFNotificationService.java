/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/Ks89/AndroidStudioProjects/SPF/sPFShared/src/main/aidl/it/polimi/spf/shared/aidl/SPFNotificationService.aidl
 */
package it.polimi.spf.shared.aidl;
/**
 * Interface exposed by SPF to allows local applications to interact with the
 * Notification API.
 * 
 * TODO #Documentation
 * 
 * @author darioarchetti
 * 
 */
public interface SPFNotificationService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements it.polimi.spf.shared.aidl.SPFNotificationService
{
private static final java.lang.String DESCRIPTOR = "it.polimi.spf.shared.aidl.SPFNotificationService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an it.polimi.spf.shared.aidl.SPFNotificationService interface,
 * generating a proxy if needed.
 */
public static it.polimi.spf.shared.aidl.SPFNotificationService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof it.polimi.spf.shared.aidl.SPFNotificationService))) {
return ((it.polimi.spf.shared.aidl.SPFNotificationService)iin);
}
return new it.polimi.spf.shared.aidl.SPFNotificationService.Stub.Proxy(obj);
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
case TRANSACTION_saveTrigger:
{
data.enforceInterface(DESCRIPTOR);
it.polimi.spf.shared.model.SPFTrigger _arg0;
if ((0!=data.readInt())) {
_arg0 = it.polimi.spf.shared.model.SPFTrigger.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.lang.String _arg1;
_arg1 = data.readString();
it.polimi.spf.shared.model.SPFError _arg2;
_arg2 = new it.polimi.spf.shared.model.SPFError();
long _result = this.saveTrigger(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeLong(_result);
if ((_arg2!=null)) {
reply.writeInt(1);
_arg2.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_deleteTrigger:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
java.lang.String _arg1;
_arg1 = data.readString();
it.polimi.spf.shared.model.SPFError _arg2;
_arg2 = new it.polimi.spf.shared.model.SPFError();
boolean _result = this.deleteTrigger(_arg0, _arg1, _arg2);
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
case TRANSACTION_deleteAllTrigger:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
it.polimi.spf.shared.model.SPFError _arg1;
_arg1 = new it.polimi.spf.shared.model.SPFError();
boolean _result = this.deleteAllTrigger(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_listTrigger:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
it.polimi.spf.shared.model.SPFError _arg1;
_arg1 = new it.polimi.spf.shared.model.SPFError();
java.util.List<it.polimi.spf.shared.model.SPFTrigger> _result = this.listTrigger(_arg0, _arg1);
reply.writeNoException();
reply.writeTypedList(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getTrigger:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
java.lang.String _arg1;
_arg1 = data.readString();
it.polimi.spf.shared.model.SPFError _arg2;
_arg2 = new it.polimi.spf.shared.model.SPFError();
it.polimi.spf.shared.model.SPFTrigger _result = this.getTrigger(_arg0, _arg1, _arg2);
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
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements it.polimi.spf.shared.aidl.SPFNotificationService
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
@Override public long saveTrigger(it.polimi.spf.shared.model.SPFTrigger trigger, java.lang.String accessToken, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((trigger!=null)) {
_data.writeInt(1);
trigger.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(accessToken);
mRemote.transact(Stub.TRANSACTION_saveTrigger, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
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
@Override public boolean deleteTrigger(long triggerId, java.lang.String token, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(triggerId);
_data.writeString(token);
mRemote.transact(Stub.TRANSACTION_deleteTrigger, _data, _reply, 0);
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
@Override public boolean deleteAllTrigger(java.lang.String token, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(token);
mRemote.transact(Stub.TRANSACTION_deleteAllTrigger, _data, _reply, 0);
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
@Override public java.util.List<it.polimi.spf.shared.model.SPFTrigger> listTrigger(java.lang.String token, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<it.polimi.spf.shared.model.SPFTrigger> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(token);
mRemote.transact(Stub.TRANSACTION_listTrigger, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(it.polimi.spf.shared.model.SPFTrigger.CREATOR);
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
@Override public it.polimi.spf.shared.model.SPFTrigger getTrigger(long triggerId, java.lang.String token, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
it.polimi.spf.shared.model.SPFTrigger _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(triggerId);
_data.writeString(token);
mRemote.transact(Stub.TRANSACTION_getTrigger, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = it.polimi.spf.shared.model.SPFTrigger.CREATOR.createFromParcel(_reply);
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
}
static final int TRANSACTION_saveTrigger = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_deleteTrigger = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_deleteAllTrigger = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_listTrigger = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getTrigger = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public long saveTrigger(it.polimi.spf.shared.model.SPFTrigger trigger, java.lang.String accessToken, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException;
public boolean deleteTrigger(long triggerId, java.lang.String token, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException;
public boolean deleteAllTrigger(java.lang.String token, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException;
public java.util.List<it.polimi.spf.shared.model.SPFTrigger> listTrigger(java.lang.String token, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException;
public it.polimi.spf.shared.model.SPFTrigger getTrigger(long triggerId, java.lang.String token, it.polimi.spf.shared.model.SPFError err) throws android.os.RemoteException;
}
