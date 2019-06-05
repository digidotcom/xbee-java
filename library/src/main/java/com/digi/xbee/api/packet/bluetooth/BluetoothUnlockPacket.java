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
 * This class represents a Bluetooth Unlock packet. Packet is built using the
 * parameters of the constructor.
 *
 * <p>The Bluetooth Unlock packet is used to authenticate a connection on the
 * Bluetooth interface and unlock the processing of AT command frames.</p>
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
public class BluetoothUnlockPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 34; /* 1 (Frame type) + 1 (Step) + 32 (Hash length) */

	private static final String ERROR_PAYLOAD_NULL = "Bluetooth Unlock packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete Bluetooth Unlock packet.";
	private static final String ERROR_NOT_BLE_UNLOCK = "Payload is not a Bluetooth Unlock packet.";
	private static final String ERROR_STEP_NULL = "SRP step cannot be null.";
	private static final String ERROR_DATA_NULL = "Data cannot be null.";

	// Variables.
	private SrpStep srpStep;

	private byte[] data;

	private Logger logger;

	/**
	 * Creates a new {@code BluetoothUnlockPacket} object from the given
	 * payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a Bluetooth Unlock packet ({@code 0x2C}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 *
	 * @return Parsed Bluetooth Unlock packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.BLE_UNLOCK.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static BluetoothUnlockPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.BLE_UNLOCK.getValue())
			throw new IllegalArgumentException(ERROR_NOT_BLE_UNLOCK);

		// payload[0] is the frame type.
		int index = 1;

		// SRP step byte.
		SrpStep srpStep = SrpStep.get(payload[index] & 0xFF);
		index = index + 1;

		// Get data.
		byte[] data = Arrays.copyOfRange(payload, index, payload.length);

		return new BluetoothUnlockPacket(srpStep, data);
	}

	/**
	 * Class constructor. Instantiates a new {@code BluetoothUnlockPacket}
	 * object with the given parameters.
	 *
	 * @param srpStep The SRP step.
	 * @param data Data contained in the packet.
	 *
	 * @throws NullPointerException if {@code srpStep == null} or
	 *                              if {@code data == null}.
	 */
	public BluetoothUnlockPacket(SrpStep srpStep, byte[] data) {
		super(APIFrameType.BLE_UNLOCK);

		if (srpStep == null)
			throw new NullPointerException(ERROR_STEP_NULL);
		if (data == null)
			throw new NullPointerException(ERROR_DATA_NULL);

		this.srpStep = srpStep;
		this.data = data;
		this.logger = LoggerFactory.getLogger(BluetoothUnlockPacket.class);
	}

	@Override
	public byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(srpStep.getID());
			os.write(data);
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
	 * Sets the SRP data.
	 *
	 * @param data The new SRP data.
	 *
	 * @throws NullPointerException if {@code data == null}.
	 *
	 * @see #getData()
	 */
	public void setData(byte[] data) {
		if (data == null)
			throw new NullPointerException(ERROR_DATA_NULL);
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
		parameters.put("SRP step", String.format("%s (%s)", HexUtils.prettyHexString(HexUtils.integerToHexString(
				srpStep.getID(), 1)), srpStep.getDescription()));
		parameters.put("Data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)));
		return parameters;
	}
}
