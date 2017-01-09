/**
 * Copyright 2017, Digi International Inc.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES 
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR 
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES 
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN 
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF 
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.digi.xbee.api.exceptions;

/**
 * Generic XBee API exception. This class and its subclasses indicate
 * conditions that an application might want to catch. This exception can be 
 * thrown when any problem related to the XBee device occurs.
 * 
 * @see Exception
 */
public class XBeeException extends Exception {

	// Constants
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an {@code XBeeException} with {@code null} as its error 
	 * detail message.
	 */
	public XBeeException() {
		super();
	}
	
	/**
	 * Creates an {@code XBeeException} with the specified cause.
	 * 
	 * @param cause The cause of this exception.
	 * 
	 * @see Throwable
	 */
	public XBeeException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates an {@code XBeeException} with the specified message.
	 * 
	 * @param message The associated message.
	 */
	public XBeeException(String message) {
		super(message);
	}

	/**
	 * Creates an {@code XBeeException} with the specified message and cause.
	 * 
	 * @param message The associated message.
	 * @param cause The cause of this exception.
	 * 
	 * @see Throwable
	 */
	public XBeeException(String message, Throwable cause) {
		super(message, cause);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Throwable#getCause()
	 */
	@Override
	public Throwable getCause() {
		return super.getCause();
	}
}
