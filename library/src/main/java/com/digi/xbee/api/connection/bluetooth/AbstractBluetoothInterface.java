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
package com.digi.xbee.api.connection.bluetooth;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.digi.xbee.api.connection.ConnectionType;
import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.utils.ByteUtils;

/**
 * This class provides common functionality to Bluetooth Low Energy interfaces.
 * 
 * @since 1.3.0
 */
public abstract class AbstractBluetoothInterface implements IConnectionInterface {
	
	// Constants.
	protected static final String SERVICE_GUID = "53DA53B9-0447-425A-B9EA-9837505EB59A";
	protected static final String TX_CHAR_GUID = "7DDDCA00-3E05-4651-9254-44074792C590";
	protected static final String RX_CHAR_GUID = "F9279EE9-2CD0-410C-81CC-ADF11E4E5AEA";

	protected static final String AES_CIPHER = "AES/CTR/NoPadding";

	protected static final int LENGTH_COUNTER = 16;
	
	// Variables.
	protected Cipher cipherEnc;
	protected Cipher cipherDec;
	
	protected boolean encrypt = false;
	
	/**
	 * Sets the encryption keys and starts to encrypt the communication with the
	 * module.
	 *
	 * @param key Session key.
	 * @param txNonce TX nonce used as prefix of the counter block.
	 * @param rxNonce RX nonce used as prefix of the counter block.
	 *
	 * @throws XBeeException if there is any error setting up the ciphers.
	 */
	public void setEncryptionKeys(byte[] key, byte[] txNonce, byte[] rxNonce) throws XBeeException {
		byte[] txCounter = getCounter(txNonce, 1);
		byte[] rxCounter = getCounter(rxNonce, 1);

		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

		try {
			cipherEnc = Cipher.getInstance(AES_CIPHER);
			cipherEnc.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(txCounter));

			cipherDec = Cipher.getInstance(AES_CIPHER);
			cipherDec.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(rxCounter));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
			throw new XBeeException("Could not set the encryption keys", e);
		}

		encrypt = true;
	}
	
	/**
	 * Generates and returns the encryption counter with the given nonce and
	 * count value.
	 *
	 * @param nonce Nonce used as prefix of the counter block.
	 * @param count Count value.
	 *
	 * @return The encryption counter.
	 */
	private byte[] getCounter(byte[] nonce, int count) {
		byte[] counter = new byte[LENGTH_COUNTER];
		System.arraycopy(nonce, 0, counter, 0, nonce.length);
		byte[] countBytes = ByteUtils.intToByteArray(count);
		System.arraycopy(countBytes, 0, counter, nonce.length, countBytes.length);
		return counter;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#getConnectionType()
	 */
	@Override
	public ConnectionType getConnectionType() {
		return ConnectionType.BLUETOOTH;
	}
}
