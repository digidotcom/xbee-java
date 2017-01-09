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
package com.digi.xbee.api.models;

import java.util.regex.Pattern;

import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents an IMEI address used by cellular devices.
 * 
 * <p>This address is only applicable for:</p>
 * <ul>
 *   <li>Cellular</li>
 * </ul>
 * 
 * @since 1.2.0
 */
public class XBeeIMEIAddress {

	// Constants
	private static final String ERROR_IMEI_NULL = "IMEI address cannot be null.";
	private static final String ERROR_IMEI_TOO_LONG = "IMEI address cannot be longer than 8 bytes.";
	private static final String ERROR_IMEI_INVALID = "Invalid IMEI address.";
	
	private static final int HASH_SEED = 23;
	
	private static final String IMEI_PATTERN = "^\\d{0,15}$";
	
	// Variables
	private byte[] address;
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code XBeeIMEIAddress} with the given parameter.
	 * 
	 * @param address The IMEI address as byte array.
	 * 
	 * @throws IllegalArgumentException If {@code address.length > 8}.
	 * @throws NullPointerException If {@code address == null}.
	 * 
	 * @see #XBeeIMEIAddress(String)
	 */
	public XBeeIMEIAddress(byte[] address) {
		if (address == null)
			throw new NullPointerException(ERROR_IMEI_NULL);
		if (address.length > 8)
			throw new IllegalArgumentException(ERROR_IMEI_TOO_LONG);
		
		generateByteAddress(address);
	}
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code XBeeIMEIAddress} with the given parameters.
	 * 
	 * @param address The IMEI address as string.
	 * 
	 * @throws IllegalArgumentException If the given address doesn't match the
	 *                                  IMEI address pattern.
	 * @throws NullPointerException If {@code address == null}.
	 * 
	 * @see #XBeeIMEIAddress(byte[])
	 */
	public XBeeIMEIAddress(String address) {
		if (address == null)
			throw new NullPointerException(ERROR_IMEI_NULL);
		
		if (!Pattern.matches(IMEI_PATTERN, address))
			throw new IllegalArgumentException(ERROR_IMEI_INVALID);
		
		byte[] byteAddress = HexUtils.hexStringToByteArray(address);
		
		generateByteAddress(byteAddress);
	}
	
	/**
	 * Generates and saves the IMEI byte address based on the given byte array.
	 * 
	 * @param byteAddress The byte array used to generate the final IMEI byte 
	 *                    address.
	 */
	private void generateByteAddress(byte[] byteAddress) {
		this.address = new byte[8];
		
		int diff = 8 - byteAddress.length;
		for (int i = 0; i < diff; i++)
			this.address[i] = 0;
		for (int i = diff; i < 8; i++)
			this.address[i] = byteAddress[i - diff];
	}
	
	/**
	 * Retrieves the IMEI address value.
	 * 
	 * @return IMEI address value.
	 */
	public String getValue() {
		return HexUtils.byteArrayToHexString(address).substring(1);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof XBeeIMEIAddress))
			return false;
		XBeeIMEIAddress addr = (XBeeIMEIAddress)obj;
		return addr.getValue().equals(getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = HASH_SEED;
		for (byte b:getValue().getBytes())
			hash = hash * (hash + b);
		return hash;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getValue();
	}
}
