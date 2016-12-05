package com.digi.xbee.api.packet.wifi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.ATStringCommands;
import com.digi.xbee.api.models.IP32BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a Remote AT Command Request (Wi-Fi) packet. Packet is
 * built using the parameters of the constructor or providing a valid API
 * payload.
 *
 * <p>Used to query or set module parameters on a remote device. For parameter
 * changes on the remote device to take effect, changes must be applied, either
 * by setting the apply changes options bit, or by sending an {@code AC} command
 * to the remote node.</p>
 *
 * <p>Remote Command options are set as a bitfield.</p>
 *
 * <p>If configured, command response is received as a
 * {@code RemoteATCommandResponseWifi packet}.</p>
 *
 * @see RemoteATCommandResponseWifiPacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class RemoteATCommandWifiPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 13; // 1 (Frame type) + 1 (frame ID) + 8 (64-bit address) + 1 (transmit options byte) + 2 (AT command)

	private static final String ERROR_PAYLOAD_NULL = "Remote AT Command Request (Wi-Fi) packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete Remote AT Command Request (Wi-Fi) packet.";
	private static final String ERROR_NOT_VALID_PACKET = "Payload is not a Remote AT Command Request (Wi-Fi) packet.";
	private static final String ERROR_FRAME_ID_ILLEGAL = "Frame ID must be between 0 and 255.";
	private static final String ERROR_DEST_ADDRESS_NULL = "Destination address cannot be null.";
	private static final String ERROR_OPTIONS_INVALID = "Receive options must be between 0 and 255.";
	private static final String ERROR_AT_COMMAND_NULL = "AT command cannot be null.";

	// Variables.
	private final IP32BitAddress destAddress;

	private final int transmitOptions;

	private final String command;

	private byte[] parameter;

	private Logger logger;

	/**
	 * Creates a new {@code RemoteATCommandWifiPacket} object from the given
	 * payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a Remote AT Command Requst (Wi-Fi) packet
	 *                ({@code 0x07}). The byte array must be in
	 *                {@code OperatingMode.API} mode.
	 *
	 * @return Parsed Remote AT Command Request (Wi-Fi) packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.REMOTE_AT_COMMAND_REQUEST_WIFI.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static RemoteATCommandWifiPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.REMOTE_AT_COMMAND_REQUEST_WIFI.getValue())
			throw new IllegalArgumentException(ERROR_NOT_VALID_PACKET);

		// payload[0] is the frame type.
		int index = 1;

		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;

		// 8 bytes of 32-bit destination address.
		IP32BitAddress destAddress = new IP32BitAddress(Arrays.copyOfRange(payload, index + 4, index + 8));
		index = index + 8;

		// Options byte.
		int transmitOptions = payload[index] & 0xFF;
		index = index + 1;

		// 2 bytes of AT command.
		String command = new String(new byte[]{payload[index], payload[index + 1]});
		index = index + 2;

		// Get data.
		byte[] parameterData = null;
		if (index < payload.length)
			parameterData = Arrays.copyOfRange(payload, index, payload.length);

		return new RemoteATCommandWifiPacket(frameID, destAddress, transmitOptions, command, parameterData);
	}

	/**
	 * Class constructor. Instantiates a new {@code RemoteATCommandWifiPacket}
	 * object with the given parameters.
	 *
	 * @param frameID The Frame ID.
	 * @param destAddress 32-bit IP address of the destination device.
	 * @param transmitOptions Bitfield of supported transmission options.
	 * @param command AT command.
	 * @param parameter AT command parameter as byte array.
	 *
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code transmitOptions < 0} or
	 *                                  if {@code transmitOptions > 255}.
	 * @throws NullPointerException if {@code destAddress == null} or
	 *                              if {@code command == null}.
	 *
	 * @see #RemoteATCommandWifiPacket(int, IP32BitAddress, int, String, String)
	 * @see com.digi.xbee.api.models.IP32BitAddress
	 * @see com.digi.xbee.api.models.RemoteATCommandOptions
	 */
	public RemoteATCommandWifiPacket(int frameID, IP32BitAddress destAddress, int transmitOptions,
			String command, byte[] parameter) {
		super(APIFrameType.REMOTE_AT_COMMAND_REQUEST_WIFI);

		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);
		if (destAddress == null)
			throw new NullPointerException(ERROR_DEST_ADDRESS_NULL);
		if (transmitOptions < 0 || transmitOptions > 255)
			throw new IllegalArgumentException(ERROR_OPTIONS_INVALID);
		if (command == null)
			throw new NullPointerException(ERROR_AT_COMMAND_NULL);

		this.frameID = frameID;
		this.destAddress = destAddress;
		this.transmitOptions = transmitOptions;
		this.command = command;
		this.parameter = parameter;
		this.logger = LoggerFactory.getLogger(RemoteATCommandWifiPacket.class);
	}

	/**
	 * Class constructor. Instantiates a new {@code RemoteATCommandWifiPacket}
	 * object with the given parameters.
	 *
	 * @param frameID The Frame ID.
	 * @param destAddress 32-bit IP address of the destination device.
	 * @param transmitOptions Bitfield of supported transmission options.
	 * @param command AT command.
	 * @param parameter AT command parameter as string.
	 *
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code transmitOptions < 0} or
	 *                                  if {@code transmitOptions > 255}.
	 * @throws NullPointerException if {@code destAddress == null} or
	 *                              if {@code command == null}.
	 *
	 * @see #RemoteATCommandWifiPacket(int, IP32BitAddress, int, String, byte[])
	 * @see com.digi.xbee.api.models.IP32BitAddress
	 * @see com.digi.xbee.api.models.RemoteATCommandOptions
	 */
	public RemoteATCommandWifiPacket(int frameID, IP32BitAddress destAddress, int transmitOptions,
			String command, String parameter) {
		this(frameID, destAddress, transmitOptions, command, parameter == null ? null : parameter.getBytes());
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(new byte[]{0x00, 0x00, 0x00, 0x00}); // First 4 bytes of the 64-bit source address.
			os.write(destAddress.getValue());
			os.write(transmitOptions);
			os.write(command.getBytes());
			if (parameter != null)
				os.write(parameter);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
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
		return destAddress.equals(IP32BitAddress.BROADCAST_ADDRESS);
	}

	/**
	 * Retrieves the 32-bit IP destination address.
	 *
	 * @return The 32-bit IP destination address.
	 *
	 * @see com.digi.xbee.api.models.IP32BitAddress
	 */
	public IP32BitAddress getDestinationAddress() {
		return destAddress;
	}

	/**
	 * Returns the transmit options bitfield.
	 *
	 * @return Transmit options bitfield.
	 *
	 * @see com.digi.xbee.api.models.RemoteATCommandOptions
	 */
	public int getTransmitOptions() {
		return transmitOptions;
	}

	/**
	 * Returns the AT Command.
	 *
	 * @return The AT Command.
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Sets the AT command parameter as String.
	 *
	 * @param parameter The AT command parameter as String.
	 *
	 * @see #getParameter()
	 * @see #getParameterAsString()
	 * @see #setParameter(byte[])
	 */
	public void setParameter(String parameter) {
		if (parameter == null)
			this.parameter = null;
		else
			this.parameter = parameter.getBytes();
	}

	/**
	 * Sets the AT command parameter.
	 *
	 * @param parameter The AT command parameter.
	 *
	 * @see #getParameter()
	 * @see #getParameterAsString()
	 * @see #setParameter(String)
	 */
	public void setParameter(byte[] parameter) {
		this.parameter = parameter;
	}

	/**
	 * Returns the AT command parameter.
	 *
	 * @return The AT command parameter.
	 *
	 * @see #getParameterAsString()
	 * @see #setParameter(byte[])
	 * @see #setParameter(String)
	 */
	public byte[] getParameter() {
		return parameter;
	}

	/**
	 * Returns the AT command parameter as String.
	 *
	 * @return The AT command parameter as String, {@code null} if it is not
	 *         set.
	 *
	 * @see #getParameter()
	 * @see #setParameter(byte[])
	 * @see #setParameter(String)
	 */
	public String getParameterAsString() {
		if (parameter == null)
			return null;
		return new String(parameter);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Destination address", "00 00 00 00 " + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(destAddress.getValue())) + " (" + destAddress.toString() + ")");
		parameters.put("Transmit options", HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1)));
		parameters.put("AT Command", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(command.getBytes())) + " (" + command + ")");
		if (parameter != null) {
			if (ATStringCommands.get(command) != null)
				parameters.put("Parameter", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(parameter)) + " (" + new String(parameter) + ")");
			else
				parameters.put("Parameter", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(parameter)));
		}
		return parameters;
	}
}
