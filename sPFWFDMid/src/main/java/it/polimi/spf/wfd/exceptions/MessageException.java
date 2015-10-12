/*
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

package it.polimi.spf.wfd.exceptions;

/**
 * Created by Stefano Cappa on 12/10/15.
 */

import lombok.Getter;

/**
 * Exception for Messages.
 */
public class MessageException extends Exception {

    public enum Reason {NULL_MESSAGE}

    @Getter
    private Reason reason;

    public MessageException() {
        super();
    }

    /**
     * Constructor
     *
     * @param message String message
     * @param cause   The throwable object
     */
    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     *
     * @param message String message
     */
    public MessageException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param cause String message
     */
    public MessageException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor
     *
     * @param reason Enumeration that represents the exception's reason.
     */
    public MessageException(Reason reason) {
        this.reason = reason;
    }
}