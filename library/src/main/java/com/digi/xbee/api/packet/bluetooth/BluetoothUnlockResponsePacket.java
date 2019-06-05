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
package com.digi.xbee.api.packet.bluetooth;

import com.digi.xbee.api.models.SrpError;
import com.digi.xbee.api.models.SrpStep;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * This class represents a Bluetooth Unlock Respone packet. Packet is built
 * using the parameters of the constructor.
 *
 * <p>The Bluetooth Unlock Response packet is used to authenticate a connection
 * on the Bluetooth interface and unlock the processing of AT command frames.
 * </p>
 *
 * <p>The unlock process is an implementation of the SRP (Secure Remote
 * Password) algorithm using the RFC5054 1024-bit group and the SHA-256 hash
 * algorithm. The value of I is fixed to the username apiservice.</p>
 *
 * <p>Upon completion, each side will have derived a shared session key which is
 * used to communicate in an encrypted fashion with the peer. Additionally, a
 * Modem Status frame - 0x8A with the status code 0x32 (Bluetooth Connected) is
 * sent through the UART (if AP = 1 or 2). When an unlocked connection is
 * terminated, a Modem Status frame with the status code 0x33 (Bluetooth
 * Disconnected) is sent through the UART.</p>
 *
 * @see BluetoothUnlockResponsePacket
 * @see XBeeAPIPacket
 *
 * @since 1.3.0
 */
public class BluetoothUnlockResponsePacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 2; /* 1 (Frame type) + 1 (Step) */

	private static final String ERROR_PAYLOAD_NULL = "Bluetooth Unlock Response packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete Bluetooth Unlock Response packet.";
	private static final String ERROR_NOT_BLE_UNLOCK_RESPONSE = "Payload is not a Bluetooth Unlock Response packet.";
	private static final String ERROR_STEP_NULL = "SRP step cannot be null.";
	private static final String ERROR_DATA_NULL = "Data cannot be null.";

	// Variables.
	private SrpStep srpStep = null;
	private SrpError srpError = null;

	private byte[] data;

	private Logger logger;

	/**
	 * Creates a new {@code BluetoothUnlockResponsePacket} object from the given
	 * payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a Bluetooth Unlock packet ({@code 0xAC}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 *
	 * @return Parsed Bluetooth Unlock packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.BLE_UNLOCK_RESPONSE.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static BluetoothUnlockResponsePacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.BLE_UNLOCK_RESPONSE.getValue())
			throw new IllegalArgumentException(ERROR_NOT_BLE_UNLOCK_RESPONSE);

		// payload[0] is the frame type.
		int index = 1;

		// SRP step byte.
		SrpStep srpStep = SrpStep.get(payload[index] & 0xFF);

		// If the step is unknown, the packet contains an error.
		if (srpStep == null || srpStep == SrpStep.UNKNOWN)
			return new BluetoothUnlockResponsePacket(SrpError.get(payload[index] & 0xFF));

		index = index + 1;

		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);

		return new BluetoothUnlockResponsePacket(srpStep, data);
	}

	/**
	 * Class constructor. Instantiates a new
	 * {@code BluetoothUnlockResponsePacket} object with the given parameters.
	 *
	 * @param srpStep The SRP step.
	 * @param data Data contained in the packet.
	 *
	 * @throws NullPointerException if {@code srpStep == null} or
	 *                              if {@code data == null}.
	 *
	 * @see SrpStep
	 */
	public BluetoothUnlockResponsePacket(SrpStep srpStep, byte[] data) {
		super(APIFrameType.BLE_UNLOCK_RESPONSE);

		if (srpStep == null)
			throw new NullPointerException(ERROR_STEP_NULL);
		if (data == null)
			throw new NullPointerException(ERROR_DATA_NULL);

		this.srpStep = srpStep;
		this.data = data;
		this.logger = LoggerFactory.getLogger(BluetoothUnlockResponsePacket.class);
	}

	/**
	 * Class constructor. Instantiates a new
	 * {@code BluetoothUnlockResponsePacket} object with the given error.
	 *
	 * @param srpError The SRP error.
	 *
	 * @throws NullPointerException if {@code srpError == null}.
	 *
	 * @see SrpError
	 */
	public BluetoothUnlockResponsePacket(SrpError srpError) {
		super(APIFrameType.BLE_UNLOCK_RESPONSE);

		if (srpError == null)
			throw new NullPointerException(ERROR_STEP_NULL);

		this.srpError = srpError;
		this.logger = LoggerFactory.getLogger(BluetoothUnlockResponsePacket.class);
	}

	@Override
	public byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			if (srpStep != null && srpStep != SrpStep.UNKNOWN) {
				os.write(srpStep.getID());
				os.write(data);
			} else {
				os.write(srpError.getID());
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return os.toByteArray();
	}

	@Override
	public boolean needsAPIFrameID() {
		return false;
	}

	@Override
	public boolean isBroadcast() {
		return false;
	}
	
	/**
	 * Sets the SRP step.
	 *
	 * @param srpStep The new SRP step.
	 *
	 * @throws NullPointerException if {@code srpStep == null}.
	 *
	 * @see #getSrpStep()
	 * @see SrpStep
	 */
	public void setSrpStep(SrpStep srpStep) {
		if (srpStep == null)
			throw new NullPointerException(ERROR_STEP_NULL);
		this.srpStep = srpStep;
	}
	
	/**
	 * Retrieves the SRP step.
	 *
	 * @return The SRP step.
	 *
	 * @see #setSrpStep(SrpStep)
	 * @see SrpStep
	 */
	public SrpStep getSrpStep() {
		return srpStep;
	}

	/**
	 * Sets the SRP error.
	 *
	 * @param srpError The new SRP error.
	 *
	 * @throws NullPointerException if {@code srpError == null}.
	 *
	 * @see #getSrpError()
	 * @see SrpError
	 */
	public void setSrpError(SrpError srpError) {
		if (srpError == null)
			throw new NullPointerException(ERROR_STEP_NULL);
		this.srpError = srpError;
	}

	/**
	 * Retrieves the SRP error.
	 *
	 * @return The SRP error.
	 *
	 * @see #setSrpError(SrpError)
	 * @see SrpError
	 */
	public SrpError getSrpError() {
		return srpError;
	}
	
	/**
	 * Sets the SRP data.
	 *
	 * @param data The new SRP data.
	 *
	 * @see #getData()
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
	
	/**
	 * Retrieves the SRP data.
	 *
	 * @return The SRP data.
	 *
	 * @see #setData(byte[])
	 */
	public byte[] getData() {
		return data;
	}

	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
		if (srpStep != null && srpStep != SrpStep.UNKNOWN) {
			parameters.put("SRP step", String.format("%s (%s)", HexUtils.prettyHexString(HexUtils.integerToHexString(
					srpStep.getID(), 1)), srpStep.getDescription()));
			parameters.put("Data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)));
		} else {
			parameters.put("SRP error", String.format("%s (%s)", HexUtils.prettyHexString(HexUtils.integerToHexString(
					srpError.getID(), 1)), srpError.getDescription()));
		}
		return parameters;
	}
}
