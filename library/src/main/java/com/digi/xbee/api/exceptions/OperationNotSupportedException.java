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
 * This exception will be thrown when the operation performed is not supported 
 * by the XBee device.
 * 
 * @see XBeeDeviceException
 */
public class OperationNotSupportedException extends XBeeDeviceException {

	// Constants
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "The requested operation is not supported by either the connection interface or the XBee device.";

	/**
	 * Creates an {@code OperationNotSupportedException} with 
	 * {@value #DEFAULT_MESSAGE} as its error detail message.
	 */
	public OperationNotSupportedException() {
		super(DEFAULT_MESSAGE);
	}
	
	/**
	 * Creates an {@code OperationNotSupportedException} with the specified 
	 * message.
	 * 
	 * @param message The associated message.
	 */
	public OperationNotSupportedException(String message) {
		super(message);
	}
	
	/**
	 * Creates an {@code OperationNotSupportedException} with the specified 
	 * message and cause.
	 * 
	 * @param message The associated message.
	 * @param cause The cause of this exception.
	 * 
	 * @see Throwable
	 */
	public OperationNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}
}
