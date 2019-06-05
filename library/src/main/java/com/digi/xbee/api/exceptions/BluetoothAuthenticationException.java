/*
 * Copyright 2019, Digi International Inc.
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
 * This exception will be thrown when trying authenticate over Bluetooth
 * with an XBee device and there is an error.
 *
 * @since 1.3.0
 */
public class BluetoothAuthenticationException extends XBeeException {

	// Constants.
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "The Bluetooth authentication process failed.";

	/**
	 * Creates a {@code BluetoothAuthenticationException} with a default message
	 * as its error detail message.
	 */
	public BluetoothAuthenticationException() {
		super(DEFAULT_MESSAGE);
	}

	/**
	 * Creates a {@code BluetoothAuthenticationException} with the given cause
	 * of the exception.
	 *
	 * @param cause Cause of the exception.
	 */
	public BluetoothAuthenticationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a {@code BluetoothAuthenticationException} with the given
	 * message.
	 *
	 * @param message Message of the exception.
	 */
	public BluetoothAuthenticationException(String message) {
		super(message);
	}

	/**
	 * Creates a {@code BluetoothAuthenticationException} with the given message
	 * and cause.
	 *
	 * @param message Message of the exception.
	 * @param cause Cause of the exception.
	 */
	public BluetoothAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
}
