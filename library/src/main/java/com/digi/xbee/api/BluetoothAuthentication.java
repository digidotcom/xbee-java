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
package com.digi.xbee.api;

import com.digi.xbee.api.exceptions.BluetoothAuthenticationException;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.models.SrpStep;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.bluetooth.BluetoothUnlockPacket;
import com.digi.xbee.api.packet.bluetooth.BluetoothUnlockResponsePacket;
import com.digi.xbee.api.utils.HexUtils;
import com.digi.xbee.api.utils.srp.SrpConstants;
import com.digi.xbee.api.utils.srp.SrpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Helper class to perform the Bluetooth authentication.
 *
 * <p>The Bluetooth authentication process is an implementation of the SRP
 * (Secure Remote Password) algorithm using the RFC5054 1024-bit group and the
 * SHA-256 hash algorithm. The value of I is fixed to the username
 * {@code apiservice}.</p>
 *
 * @since 1.3.0
 */
class BluetoothAuthentication {

	// Constants.
	private static final int TIMEOUT_AUTH = 4000;

	private static final String ERROR_AUTH = "Error performing authentication";
	private static final String ERROR_AUTH_EXTENDED = ERROR_AUTH + " > %s";
	private static final String ERROR_RESPONSE_NOT_RECEIVED = "Server response not received.";
	private static final String ERROR_BAD_PROOF = "Bad proof of key.";
	private static final String ERROR_CHALLENGE = "Could not process challenge.";

	private static final int LENGTH_SALT = 4;
	private static final int LENGTH_EPHEMERAL = 128;
	private static final int LENGTH_SESSION_PROOF = 32;
	private static final int LENGTH_NONCE = 12;

	// Variables.
	private final AbstractXBeeDevice device;
	private final String password;

	private final Object unlockLock = new Object();

	private SrpStep expectedStep;

	private BluetoothUnlockListener bluetoothUnlockListener;

	private BluetoothUnlockResponsePacket unlockResponse;

	private byte[] key = null;
	private byte[] txNonce = null;
	private byte[] rxNonce = null;

	private Logger logger;

	/**
	 * Class constructor. Instantiates a new {@code BluetoothAuthentication}
	 * object with the given parameters.
	 *
	 * @throws IllegalArgumentException If {@code device.isRemote() == true}.
	 *
	 * @param device XBee device.
	 * @param password Bluetooth password.
	 */
	BluetoothAuthentication(AbstractXBeeDevice device, String password) {
		if (device.isRemote())
			throw new IllegalArgumentException("The given local XBee device is remote.");
		
		this.device = device;
		this.password = password;

		bluetoothUnlockListener = new BluetoothUnlockListener(this);
		logger = LoggerFactory.getLogger(BluetoothAuthentication.class);
	}

	/**
	 * Starts the Bluetooth authentication with the XBee device.
	 *
	 * @throws InterfaceNotOpenException If the connection interface is not
	 *                                   open.
	 * @throws BluetoothAuthenticationException If there is any error performing
	 *                                          the Bluetooth authentication.
	 */
	void authenticate() throws BluetoothAuthenticationException {
		// Check connection.
		if (!device.isOpen())
			throw new InterfaceNotOpenException();

		device.addPacketListener(bluetoothUnlockListener);

		try {
			SrpUser user = new SrpUser(SrpConstants.API_USERNAME, password);

			// Step 1.
			byte[] clientEphemeral = user.startAuthentication();
			logger.debug(String.format("%sSRP step 1 - A = %s", device.toString(), HexUtils.byteArrayToHexString(clientEphemeral)));
			expectedStep = SrpStep.STEP_2;
			unlockResponse = null;
			device.sendPacketAsync(new BluetoothUnlockPacket(SrpStep.STEP_1, clientEphemeral));
			synchronized (unlockLock) {
				try {
					unlockLock.wait(TIMEOUT_AUTH);
				} catch (InterruptedException ignore) {
				}
			}
			checkResponsePacket();

			// Step 2.
			int index = 0;
			byte[] salt = new byte[LENGTH_SALT];
			System.arraycopy(unlockResponse.getData(), index, salt, 0, salt.length);
			index += LENGTH_SALT;
			byte[] serverEphemeral = new byte[LENGTH_EPHEMERAL];
			System.arraycopy(unlockResponse.getData(), index, serverEphemeral, 0, serverEphemeral.length);
			logger.debug(String.format("%sSRP step 2 - S = %s - B = %s", device.toString(),
					HexUtils.byteArrayToHexString(salt), HexUtils.byteArrayToHexString(serverEphemeral)));

			// Step 3.
			byte[] clientSessionProof = user.processChallenge(salt, serverEphemeral);
			if (clientSessionProof == null)
				throw new BluetoothAuthenticationException(String.format(ERROR_AUTH_EXTENDED, ERROR_CHALLENGE));
			logger.debug(String.format("%sSRP step 3 - M1 = %s", device.toString(), HexUtils.byteArrayToHexString(clientSessionProof)));
			expectedStep = SrpStep.STEP_4;
			unlockResponse = null;
			device.sendPacketAsync(new BluetoothUnlockPacket(SrpStep.STEP_3, clientSessionProof));
			synchronized (unlockLock) {
				try {
					unlockLock.wait(TIMEOUT_AUTH);
				} catch (InterruptedException ignore) {
				}
			}
			checkResponsePacket();

			// Step 4.
			index = 0;
			byte[] serverSessionProof = new byte[LENGTH_SESSION_PROOF];
			System.arraycopy(unlockResponse.getData(), index, serverSessionProof, 0, serverSessionProof.length);
			index += LENGTH_SESSION_PROOF;
			txNonce = new byte[LENGTH_NONCE];
			System.arraycopy(unlockResponse.getData(), index, txNonce, 0, txNonce.length);
			index += LENGTH_NONCE;
			rxNonce = new byte[LENGTH_NONCE];
			System.arraycopy(unlockResponse.getData(), index, rxNonce, 0, rxNonce.length);
			logger.debug(String.format("%sSRP step 4 - M2 = %s - TX nonce = %s - RX nonce = %s", device.toString(),
					HexUtils.byteArrayToHexString(serverSessionProof), HexUtils.byteArrayToHexString(txNonce),
					HexUtils.byteArrayToHexString(rxNonce)));

			user.verifySession(serverSessionProof);

			if (!user.isAuthenticated())
				throw new BluetoothAuthenticationException(String.format(ERROR_AUTH_EXTENDED, ERROR_BAD_PROOF));

			// Save the session key.
			key = user.getSessionKey();
		} catch (BluetoothAuthenticationException e) {
			throw e;
		} catch (XBeeException | NoSuchAlgorithmException | IOException e) {
			throw new BluetoothAuthenticationException(String.format(ERROR_AUTH_EXTENDED, e.getMessage()));
		} finally {
			device.removePacketListener(bluetoothUnlockListener);
		}
	}

	/**
	 * Returns the session key agreed in the authentication process.
	 *
	 * @return The session key, or {@code null} if the authentication process
	 *         failed.
	 */
	byte[] getKey() {
		return key;
	}

	/**
	 * Returns the TX nonce generated by the XBee device.
	 *
	 * @return The TX nonce, or {@code null} if the authentication process
	 *         failed.
	 */
	byte[] getTxNonce() {
		return txNonce;
	}

	/**
	 * Returns the RX nonce generated by the XBee device.
	 *
	 * @return The RX nonce, or {@code null} if the authentication process
	 *         failed.
	 */
	byte[] getRxNonce() {
		return rxNonce;
	}

	/**
	 * Checks the unlock response packet and throws the appropriate exception
	 * in case of error.
	 *
	 * @throws BluetoothAuthenticationException If the unlock response packet
	 *                                          was not received or contains an
	 *                                          error.
	 */
	private void checkResponsePacket() throws BluetoothAuthenticationException {
		if (unlockResponse == null)
			throw new BluetoothAuthenticationException(String.format(ERROR_AUTH_EXTENDED, ERROR_RESPONSE_NOT_RECEIVED));
		else if (unlockResponse.getSrpStep() == null || unlockResponse.getSrpStep() == SrpStep.UNKNOWN)
			throw new BluetoothAuthenticationException(String.format(ERROR_AUTH_EXTENDED, unlockResponse.getSrpError().getDescription()));
	}

	/**
	 * Internal class used to receive and process
	 * {@code BluetoothUnlockResponsePacket}.
	 */
	private class BluetoothUnlockListener implements IPacketReceiveListener {

		private BluetoothAuthentication auth;

		BluetoothUnlockListener(BluetoothAuthentication auth) {
			this.auth = auth;
		}

		@Override
		public void packetReceived(XBeePacket receivedPacket) {
			if (!(receivedPacket instanceof XBeeAPIPacket) || ((XBeeAPIPacket) receivedPacket).getFrameType() != APIFrameType.BLE_UNLOCK_RESPONSE)
				return;

			BluetoothUnlockResponsePacket response = (BluetoothUnlockResponsePacket) receivedPacket;

			// Check if the packet contains the expected phase or an error.
			if (response.getSrpStep() != null && response.getSrpStep() != auth.expectedStep)
				return;

			auth.unlockResponse = response;

			// Continue execution by notifying the lock object.
			synchronized (auth.unlockLock) {
				auth.unlockLock.notify();
			}
		}
	}
}
