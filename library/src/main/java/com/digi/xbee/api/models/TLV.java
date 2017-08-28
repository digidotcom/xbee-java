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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a TLV, a data structure which uses type, length and
 * value elements to define a data object.
 * 
 * @since 1.2.1
 */
public class TLV {

	// Constants.
	private static final String ERROR_NULL = "Parameter cannot be null";
	private static final String ERROR_INVALID_LENGTH = "Invalid length, must be at least %d bytes";
	private static final String ERROR_INVALID_VALUE_LENGTH = "Invalid value length, must be %d bytes";

	// Variables.
	private int type;
	private byte[] value;

	/**
	 * Class constructor. Instantiates a new {@code TLV} with the given type
	 * and value.
	 * 
	 * @param type TLV type.
	 * @param value TLV value in network-byte order (big endian).
	 * 
	 * @throws NullPointerException if {@code value == null}.
	 */
	public TLV(int type, byte[] value) {
		if (value == null)
			throw new NullPointerException(ERROR_NULL);

		this.type = type;
		this.value = value;
	}

	/**
	 * Class constructor. Instantiates a new {@code TLV} with the given byte
	 * array.
	 * 
	 * @param tlv Byte array containing the TLV.
	 * 
	 * @throws IllegalArgumentException if the length is invalid.
	 * @throws NullPointerException if {@code tlv == null}.
	 */
	public TLV(byte[] tlv) {
		// Sanity checks.
		if (tlv == null)
			throw new NullPointerException(ERROR_NULL);
		if (tlv.length < 3)
			throw new IllegalArgumentException(String.format(ERROR_INVALID_LENGTH, 3));

		type = tlv[0];

		int length;
		int index;
		// Check if the length is larger than 0xFF.
		if (ByteUtils.byteToInt(tlv[1]) == 0xFF) {
			if (tlv.length < 259)
				throw new IllegalArgumentException(String.format(ERROR_INVALID_LENGTH, 259));
			length = ByteUtils.byteArrayToInt(Arrays.copyOfRange(tlv, 2, 4));
			index = 4;
		} else {
			length = ByteUtils.byteToInt(tlv[1]);
			index = 2;
		}
		if (tlv.length - index != length)
			throw new IllegalArgumentException(String.format(ERROR_INVALID_VALUE_LENGTH, length));
		value = Arrays.copyOfRange(tlv, index, index + length);
	}

	/**
	 * Retrieves the TLV type.
	 * 
	 * @return The TLV type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Retrieves the TLV value in network-byte order (big-endian).
	 * 
	 * @return The TLV value.
	 */
	public byte[] getValue() {
		return value;
	}

	/**
	 * Retrieves the TLV in byte array format.
	 * 
	 * @return The TLV in byte array format.
	 */
	public byte[] toByteArray() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			data.write(type);
			if (value.length >= 0xFF) {
				data.write(0xFF);
				data.write(ByteUtils.shortToByteArray((short)value.length));
			} else
				data.write(value.length);
			data.write(value);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data.toByteArray();
	}

	@Override
	public String toString() {
		return "Type: " + HexUtils.byteToHexString((byte)type) + 
				", length: " + value.length + 
				", value: " + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(value));
	}

	/**
	 * Parses the given TLVs in byte array format.
	 * 
	 * @param tlvs TLVs in byte array format.
	 * 
	 * @return The list of TLVs.
	 * 
	 * @throws IllegalArgumentException
	 */
	public static ArrayList<TLV> parseTLV(byte[] tlvs) throws IllegalArgumentException {
		// Verify the minimum length.
		if (tlvs.length > 0 && tlvs.length < 3)
			throw new IllegalArgumentException("TLV length must be at least 3 bytes.");
		ArrayList<TLV> list = new ArrayList<TLV>();
		int i = 0;
		while (i < tlvs.length) {
			byte type = tlvs[i];
			i += 1;
			if (i >= tlvs.length)
				throw new IllegalArgumentException("Invalid TLV length.");
			int length = ByteUtils.byteToInt(tlvs[i]);
			i += 1;
			byte[] value = null;
			if (length == 0xFF) {
				int extraLength = ByteUtils.byteArrayToInt(new byte[]{tlvs[i], tlvs[i + 1]});
				i += 2;
				value = Arrays.copyOfRange(tlvs, i, i + extraLength);
				i += extraLength;
			} else {
				value = Arrays.copyOfRange(tlvs, i, i + length);
				i += length;
			}
			list.add(new TLV(type, value));
		}
		return list;
	}
}

