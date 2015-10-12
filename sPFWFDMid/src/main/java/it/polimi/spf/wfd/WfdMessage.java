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
package it.polimi.spf.wfd;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.polimi.spf.wfd.exceptions.MessageException;

public class WfdMessage {

    private static final String KEY_SENDER_ID = "senderId";
    private static final String KEY_RECEIVER_ID = "receiverId";
    private static final String KEY_MSG_TYPE = "type";
    private static final String KEY_TIMESTAMP = "sequenceNumber";
    private static final String KEY_MSG_CONTENT = "msgContent";

    /**
     * Used when a message is addressed to the whole group.
     */
    public static final String BROADCAST_RECEIVER_ID = "SEND_TO_ALL";

    /**
     * Used when no target is needed: e.g. connection does not need receiver and
     * instance discovery does not need sender.
     */
    private static final String UNKNOWN_RECEIVER_ID = "UNKNOWN_RECEIVER_ID";

    /*
     * Message types
     */
    public static final String TYPE_CONNECT = "CONNECT";
    public static final String TYPE_SIGNAL = "SIGNAL";
    public static final String TYPE_REQUEST = "REQUEST";
    public static final String TYPE_RESPONSE = "RESPONSE";
    public static final String TYPE_RESPONSE_ERROR = "response_error";
    public static final String TYPE_INSTANCE_DISCOVERY = "DISCOVERY";

	/*
     * Discovery messages parameters (used internally).
	 */
    /**
     * Content name for discovery messages: the identifier of the instance that
     * was found or lost.
     */
    public static final String ARG_IDENTIFIER = "identifier";
    /**
     * Content name for discovery messages: boolean that indicates if the
     * instance is found (true) or lost (false);
     */
    public static final String ARG_STATUS = "status";
    /**
     * Value for {@link WfdMessage#ARG_STATUS}:
     */
    public static final boolean INSTANCE_FOUND = true;
    /**
     * Value for {@link WfdMessage#ARG_STATUS}
     */
    public static final boolean INSTANCE_LOST = false;

    public String receiverId = UNKNOWN_RECEIVER_ID;// default
    public String senderId = UNKNOWN_RECEIVER_ID;
    public String type = TYPE_SIGNAL;// default

    /**
     * Holds the payload of the message.
     */
    private JsonObject msgContent;

    /*
     * if the type is request or response , it is used to associate the pair of
     * messages.
     */
    private long sequenceNumber = -1;

    /**
     * Constructs an empty message
     */
    public WfdMessage() {
        msgContent = new JsonObject();
    }

    /**
     * Returns the string representation for this message.
     */
    public String toString() {
        JsonObject msgJSON = new JsonObject();
        msgJSON.addProperty(KEY_SENDER_ID, senderId);
        msgJSON.addProperty(KEY_RECEIVER_ID, receiverId);
        msgJSON.addProperty(KEY_TIMESTAMP, sequenceNumber);
        msgJSON.addProperty(KEY_MSG_TYPE, type);
        msgJSON.add(KEY_MSG_CONTENT, msgContent);
        Gson g = new Gson();
        return g.toJson(msgJSON);
    }

    /**
     * Returns a {@link WfdMessage} given its string representation. If the
     * string cannot be parsed returns null.
     *
     * @param str
     * @return
     */
    public static WfdMessage fromString(String str) throws MessageException {
        if (str == null) {
            throw new MessageException(MessageException.Reason.NULL_MESSAGE);
        }
        JsonObject o = new JsonParser().parse(str).getAsJsonObject();
        WfdMessage msg = new WfdMessage();
        msg.msgContent = o.getAsJsonObject(KEY_MSG_CONTENT);
        msg.type = o.get(KEY_MSG_TYPE).getAsString();
        if (msg.type.equals(TYPE_REQUEST) || msg.type.equals(TYPE_RESPONSE)) {
            msg.sequenceNumber = o.get(KEY_TIMESTAMP).getAsLong();
        } else {
            msg.sequenceNumber = -1;
        }
        msg.receiverId = o.get(KEY_RECEIVER_ID).getAsString();
        msg.senderId = o.get(KEY_SENDER_ID).getAsString();
        return msg;

    }

    /**
     * @param senderId - the identifier to set
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    /**
     * @param targetId -the identifier to set
     */
    public void setReceiverId(String targetId) {
        receiverId = targetId;
    }

    /**
     * @return the receiverId
     */
    public String getReceiverId() {
        return receiverId;
    }

    /**
     * @return the senderId
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * Set the type of the message.
     *
     * @param msgType
     */
    public void setType(String msgType) {
        this.type = msgType;
    }

    /**
     * @return the type of the message
     */
    public String getType() {
        return type;
    }

    /**
     * @param clock
     */
    public void setSequenceNumber(long clock) {
        this.sequenceNumber = clock;
    }

    /**
     * @return
     */
    public long getTimestamp() {

        return sequenceNumber;
    }

    /**
     * Maps name to value.
     *
     * @param name  - the name of the mapping, not null
     * @param value - the value of the mapping, not null
     */
    public void put(String name, String value) {
        msgContent.addProperty(name, value);
    }

    /**
     * Return the String associated to the specified name.
     *
     * @param name
     * @return - the associated string
     */
    public String getString(String name) {
        return msgContent.get(name).getAsString();
    }

    /**
     * Maps name to value
     *
     * @param name  - the name of the mapping
     * @param value - the value of the mapping
     */
    public void put(String name, boolean value) {
        msgContent.addProperty(name, value);
    }

    /**
     * @param name
     * @return the value, or false if the mapping does not exist or cannot be
     * coerced to a boolean
     */
    public boolean getBoolean(String name) {
        return msgContent.get(name).getAsBoolean();
    }

    /**
     * Maps name to value
     *
     * @param name  - the name of the mapping
     * @param value - the value of the mapping
     */
    public void put(String name, int value) {
        msgContent.addProperty(name, value);
    }

    /**
     * Return the integer mapped to the specified name. If the mappings does not
     * exists returns the provided defaultValue.
     *
     * @param name
     * @return
     */
    public int getInt(String name) {

        return msgContent.get(name).getAsInt();

    }

    public JsonObject getJsonObject(String name) {
        return msgContent.get(name).getAsJsonObject();
    }

    public void put(String name, JsonElement value) {
        msgContent.add(name, value);
    }
}
