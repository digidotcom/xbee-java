package com.digi.xbee.api.packet.devicecloud;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;

import com.digi.xbee.api.models.DeviceCloudStatus;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a Device Response Status packet. Packet is built
 * using the parameters of the constructor or providing a valid API payload.
 *
 * <p>This frame type is sent to the serial port after the serial port sends a
 * {@link DeviceResponsePacket}.</p>
 *
 * @see DeviceResponsePacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class DeviceResponseStatusPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 3; /* 1 (Frame type) + 1 (Frame ID) + 1 (Status) */

	private static final String ERROR_PAYLOAD_NULL = "Device Response Status packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete Device Response Status packet.";
	private static final String ERROR_NOT_VALID = "Payload is not a Device Response Status packet.";
	private static final String ERROR_FRAME_ID_ILLEGAL = "Frame ID must be between 0 and 255.";
	private static final String ERROR_STATUS_NULL = "Status cannot be null.";

	// Variables.
	private DeviceCloudStatus status;

	/**
	 * Creates a new {@code DeviceResponseStatusPacket} object from the given
	 * payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a Device Response Status packet
	 *                ({@code 0xBA}). The byte array must be in
	 *                {@code OperatingMode.API} mode.
	 *
	 * @return Parsed Device Response Status packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.DEVICE_RESPONSE_STATUS.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static DeviceResponseStatusPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.DEVICE_RESPONSE_STATUS.getValue())
			throw new IllegalArgumentException(ERROR_NOT_VALID);

		// payload[0] is the frame type.
		int index = 1;

		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;

		// Status byte.
		DeviceCloudStatus status = DeviceCloudStatus.get(payload[index] & 0xFF);

		return new DeviceResponseStatusPacket(frameID, status);
	}

	/**
	 * Class constructor. Instantiates a new {@code DeviceResponseStatusPacket}
	 * object with the given parameters.
	 *
	 * @param frameID Frame ID.
	 * @param status Device response status.
	 *
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * @throws NullPointerException if {@code status == null}.
	 *
	 * @see DeviceCloudStatus
	 */
	public DeviceResponseStatusPacket(int frameID, DeviceCloudStatus status) {
		super(APIFrameType.DEVICE_RESPONSE_STATUS);

		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);
		if (status == null)
			throw new NullPointerException(ERROR_STATUS_NULL);

		this.frameID = frameID;
		this.status = status;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(status.getID());
		return os.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#needsAPIFrameID()
	 */
	@Override
	public boolean needsAPIFrameID() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#isBroadcast()
	 */
	@Override
	public boolean isBroadcast() {
		return false;
	}

	/**
	 * Sets the status of the device response.
	 *
	 * @param status The status of the device response.
	 *
	 * @throws NullPointerException if {@code status == null}.
	 *
	 * @see #getStatus()
	 * @see DeviceCloudStatus
	 */
	public void setStatus(DeviceCloudStatus status) {
		if (status == null)
			throw new NullPointerException(ERROR_STATUS_NULL);

		this.status = status;
	}

	/**
	 * Retrieves the status of the device response.
	 *
	 * @return The status of the device response.
	 *
	 * @see #setStatus(DeviceCloudStatus)
	 * @see DeviceCloudStatus
	 */
	public DeviceCloudStatus getStatus() {
		return status;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Status", HexUtils.prettyHexString(HexUtils.integerToHexString(status.getID(), 1)) + " (" + status.getName() + ")");
		return parameters;
	}
}
