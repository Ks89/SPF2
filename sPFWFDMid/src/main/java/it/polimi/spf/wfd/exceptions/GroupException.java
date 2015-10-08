package it.polimi.spf.wfd.exceptions;

/**
 * Created by Stefano Cappa on 04/08/15.
 */

import lombok.Getter;

/**
 * Exception for Groups.
 */
public class GroupException extends Exception {

    public enum Reason {NOT_INSTANTIATED_YET}

    @Getter
    private Reason reason;

    public GroupException() {
        super();
    }

    /**
     * Constructor
     *
     * @param message String message
     * @param cause   The throwable object
     */
    public GroupException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     *
     * @param message String message
     */
    public GroupException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param cause String message
     */
    public GroupException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor
     *
     * @param reason Enumeration that represents the exception's reason.
     */
    public GroupException(Reason reason) {
        this.reason = reason;
    }
}