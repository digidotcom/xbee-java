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
package com.digi.xbee.api.models;

/**
 * This class represents a User Data Relay message containing the source
 * interface and the content (data) of the message.
 *
 * @since 1.3.0
 */
public class UserDataRelayMessage {

	// Variables.
	private final XBeeLocalInterface localInterface;
	private final byte[] data;

	/**
	 * Class constructor. Instantiates a new object of type
	 * {@code UserDataRelayMessage} with the given parameters.
	 *
	 * @param localInterface The source XBee local interface.
	 * @param data Byte array containing the data of the message.
	 *
	 * @throws NullPointerException if {@code localInterface == null}.
	 *
	 * @see XBeeLocalInterface
	 */
	public UserDataRelayMessage(XBeeLocalInterface localInterface, byte[] data) {
		if (localInterface == null)
			throw new NullPointerException("XBee local interface cannot be null.");

		this.localInterface = localInterface;
		this.data = data;
	}

	/**
	 * Returns the source interface that sent this message.
	 *
	 * @return The source interface that sent this message.
	 *
	 * @see XBeeLocalInterface
	 */
	public XBeeLocalInterface getSourceInterface() {
		return localInterface;
	}
	
	/**
	 * Returns a byte array containing the data of the message.
	 *
	 * @return A byte array containing the data of the message or {@code null}
	 *         if the message does not have any data.
	 */
	public byte[] getData() {
		return data;
	}
	
	/**
	 * Returns the data of the message in string format.
	 *
	 * @return The data of the message in string format or {@code null} if the
	 *         message does not have any data.
	 */
	public String getDataString() {
		return data == null ? null : new String(data);
	}
}
