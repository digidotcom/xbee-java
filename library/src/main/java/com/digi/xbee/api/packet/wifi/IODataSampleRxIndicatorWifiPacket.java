package com.digi.xbee.api.packet.wifi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.models.IP32BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a IO Data Sample Rx Indicator (Wi-Fi) packet. Packet is
 * built using the parameters of the constructor or providing a valid API
 * payload.
 *
 * <p>When the module receives an IO sample frame from a remote device, it sends
 * the sample out the UART or SPI using this frame type. Only modules running
 * API mode will be able to receive IO samples.</p>
 *
 * <p>Among received data, some options can also be received indicating
 * transmission parameters.</p>
 *
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class IODataSampleRxIndicatorWifiPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 11; /* 1 (Frame type) + 8 (32-bit IP address) + 1 (RSSI) + 1 (receive options)  */

	private static final String ERROR_PAYLOAD_NULL = "IO Data Sample Rx Indicator (Wi-Fi) packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete IO Data Sample Rx Indicator (Wi-Fi) packet.";
	private static final String ERROR_NOT_VALID = "Payload is not a IO Data Sample Rx Indicator (Wi-Fi) packet.";
	private static final String ERROR_SOURCE_ADDRESS_NULL = "Source address cannot be null.";
	private static final String ERROR_RSSI_ILLEGAL = "RSSI must be between 0 and 255.";
	private static final String ERROR_OPTIONS_ILLEGAL = "Receive options must be between 0 and 255.";

	// Variables.
	private IP32BitAddress sourceAddress;

	private final IOSample ioSample;

	private int rssi;
	private int receiveOptions;

	private byte[] rfData;

	private Logger logger;

	/**
	 * Creates a new {@code IODataSampleRxIndicatorWifiPacket} object from the
	 * given payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a IO Data Sample RX Indicator (Wi-Fi)
	 *                packet ({@code 0x8F}). The byte array must be in
	 *                {@code OperatingMode.API} mode.
	 *
	 * @return Parsed IO Data Sample Rx Indicator (Wi-Fi) packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.IO_DATA_SAMPLE_RX_INDICATOR_WIFI.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static IODataSampleRxIndicatorWifiPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.IO_DATA_SAMPLE_RX_INDICATOR_WIFI.getValue())
			throw new IllegalArgumentException(ERROR_NOT_VALID);

		// payload[0] is the frame type.
		int index = 1;

		// 8 bytes of 32-bit IP address.
		IP32BitAddress sourceAddress = new IP32BitAddress(Arrays.copyOfRange(payload, index + 4, index + 8));
		index = index + 8;

		// RSSI options byte.
		int rssi = payload[index] & 0xFF;
		index = index + 1;

		// Receive options byte.
		int receiveOptions = payload[index] & 0xFF;
		index = index + 1;

		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);

		return new IODataSampleRxIndicatorWifiPacket(sourceAddress, rssi, receiveOptions, data);
	}

	/**
	 * Class constructor. Instantiates a new
	 * {@code IODataSampleRxIndicatorWifiPacket} object with the given
	 * parameters.
	 *
	 * @param sourceAddress 32-bit IP address of the sender.
	 * @param rssi RSSI in terms of link margin.
	 * @param receiveOptions Receive options.
	 * @param data Received RF data.
	 *
	 * @throws IllegalArgumentException if {@code rssi < 0} or
	 *                                  if {@code rssi > 255} or
	 *                                  if {@code receiveOptions < 0} or
	 *                                  if {@code receiveOptions > 255}.
	 * @throws NullPointerException if {@code sourceAddress == null}.
	 *
	 * @see com.digi.xbee.api.models.XBeeReceiveOptions
	 */
	public IODataSampleRxIndicatorWifiPacket(IP32BitAddress sourceAddress, int rssi,
			int receiveOptions, byte[] rfData) {
		super(APIFrameType.IO_DATA_SAMPLE_RX_INDICATOR_WIFI);

		if (sourceAddress == null)
			throw new NullPointerException(ERROR_SOURCE_ADDRESS_NULL);
		if (rssi < 0 || rssi > 255)
			throw new IllegalArgumentException(ERROR_RSSI_ILLEGAL);
		if (receiveOptions < 0 || receiveOptions > 255)
			throw new IllegalArgumentException(ERROR_OPTIONS_ILLEGAL);

		this.sourceAddress = sourceAddress;
		this.rssi = rssi;
		this.receiveOptions = receiveOptions;
		this.rfData = rfData;
		if (rfData != null)
			ioSample = new IOSample(rfData);
		else
			ioSample = null;
		this.logger = LoggerFactory.getLogger(IODataSampleRxIndicatorWifiPacket.class);
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
			os.write(sourceAddress.getValue());
			os.write(rssi);
			os.write(receiveOptions);
			if (rfData != null)
				os.write(rfData);
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
		return false;
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
	 * Sets the 32-bit sender/source IP address.
	 *
	 * @param sourceAddress The 32-bit sender/source IP address.
	 *
	 * @throws NullPointerException if {@code sourceAddress == null}.
	 *
	 * @see #getSourceAddress()
	 * @see IP32BitAddress
	 */
	public void setSourceAddress(IP32BitAddress sourceAddress) {
		if (sourceAddress == null)
			throw new NullPointerException(ERROR_SOURCE_ADDRESS_NULL);

		this.sourceAddress = sourceAddress;
	}

	/**
	 * Retrieves the 32-bit sender/source IP address.
	 *
	 * @return The 32-bit sender/source IP address.
	 *
	 * @see #setSourceAddress(IP32BitAddress)
	 * @see IP32BitAddress
	 */
	public IP32BitAddress getSourceAddress() {
		return sourceAddress;
	}

	/**
	 * Sets the RSSI value in terms of link margin.
	 *
	 * @param receiveOptions The RSSI value in terms of link margin.
	 *
	 * @throws IllegalArgumentException if {@code rssi < 0} or
	 *                                  if {@code rssi > 255}.
	 *
	 * @see #getRSSI()
	 */
	public void setRSSI(int rssi) {
		if (rssi < 0 || rssi > 255)
			throw new IllegalArgumentException(ERROR_RSSI_ILLEGAL);

		this.rssi = rssi;
	}

	/**
	 * Retrieves the RSSI value in terms of link margin.
	 *
	 * @return The RSSI value in terms of link margin.
	 *
	 * @see #setRSSI(int)
	 */
	public int getRSSI() {
		return rssi;
	}

	/**
	 * Sets the receive options.
	 *
	 * @param receiveOptions Receive options.
	 *
	 * @throws IllegalArgumentException if {@code receiveOptions < 0} or
	 *                                  if {@code receiveOptions > 255}.
	 *
	 * @see #getReceiveOptions()
	 */
	public void setReceiveOptions(int receiveOptions) {
		if (receiveOptions < 0 || receiveOptions > 255)
			throw new IllegalArgumentException(ERROR_OPTIONS_ILLEGAL);

		this.receiveOptions = receiveOptions;
	}

	/**
	 * Retrieves the receive options.
	 *
	 * @return Receive options.
	 *
	 * @see #setReceiveOptions(int)
	 */
	public int getReceiveOptions() {
		return receiveOptions;
	}

	/**
	 * Returns the IO sample corresponding to the data contained in the packet.
	 *
	 * @return The IO sample of the packet, {@code null} if the packet has not
	 *         any data or if the sample could not be generated correctly.
	 *
	 * @see IOSample
	 */
	public IOSample getIOSample() {
		return ioSample;
	}

	/**
	 * Returns the received RF data.
	 *
	 * @return Received RF data.
	 */
	public byte[] getRFData() {
		return rfData;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Source address", "00 00 00 00 " + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(sourceAddress.getValue())) + " (" + sourceAddress.toString() + ")");
		parameters.put("RSSI", HexUtils.prettyHexString(HexUtils.integerToHexString(rssi, 1)));
		parameters.put("Receive options", HexUtils.prettyHexString(HexUtils.integerToHexString(receiveOptions, 1)));
		if (ioSample != null) {
			parameters.put("Number of samples", HexUtils.prettyHexString(HexUtils.integerToHexString(1, 1))); // There is always 1 sample.
			parameters.put("Digital channel mask", HexUtils.prettyHexString(HexUtils.integerToHexString(ioSample.getDigitalMask(), 2)));
			parameters.put("Analog channel mask", HexUtils.prettyHexString(HexUtils.integerToHexString(ioSample.getAnalogMask(), 2)));
			for (int i = 0; i < 16; i++) {
				if (ioSample.hasDigitalValue(IOLine.getDIO(i)))
					parameters.put(IOLine.getDIO(i).getName() + " digital value", ioSample.getDigitalValue(IOLine.getDIO(i)).getName());
			}
			for (int i = 0; i < 6; i++) {
				if (ioSample.hasAnalogValue(IOLine.getDIO(i)))
					parameters.put(IOLine.getDIO(i).getName() + " analog value", HexUtils.prettyHexString(HexUtils.integerToHexString(ioSample.getAnalogValue(IOLine.getDIO(i)), 2)));
			}
		}
		return parameters;
	}
}
