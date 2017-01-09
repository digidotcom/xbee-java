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
package com.digi.xbee.api.packet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import com.digi.xbee.api.exceptions.InvalidPacketException;
import com.digi.xbee.api.models.SpecialByte;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.packet.cellular.RXSMSPacket;
import com.digi.xbee.api.packet.cellular.TXSMSPacket;
import com.digi.xbee.api.packet.common.ATCommandPacket;
import com.digi.xbee.api.packet.common.ATCommandQueuePacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.common.ExplicitAddressingPacket;
import com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket;
import com.digi.xbee.api.packet.common.IODataSampleRxIndicatorPacket;
import com.digi.xbee.api.packet.common.ModemStatusPacket;
import com.digi.xbee.api.packet.common.ReceivePacket;
import com.digi.xbee.api.packet.common.RemoteATCommandPacket;
import com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket;
import com.digi.xbee.api.packet.common.TransmitPacket;
import com.digi.xbee.api.packet.common.TransmitStatusPacket;
import com.digi.xbee.api.packet.devicecloud.DeviceRequestPacket;
import com.digi.xbee.api.packet.devicecloud.DeviceResponsePacket;
import com.digi.xbee.api.packet.devicecloud.DeviceResponseStatusPacket;
import com.digi.xbee.api.packet.devicecloud.FrameErrorPacket;
import com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket;
import com.digi.xbee.api.packet.devicecloud.SendDataResponsePacket;
import com.digi.xbee.api.packet.ip.RXIPv4Packet;
import com.digi.xbee.api.packet.ip.TXIPv4Packet;
import com.digi.xbee.api.packet.raw.RX16IOPacket;
import com.digi.xbee.api.packet.raw.RX16Packet;
import com.digi.xbee.api.packet.raw.RX64IOPacket;
import com.digi.xbee.api.packet.raw.RX64Packet;
import com.digi.xbee.api.packet.raw.TX16Packet;
import com.digi.xbee.api.packet.raw.TX64Packet;
import com.digi.xbee.api.packet.raw.TXStatusPacket;
import com.digi.xbee.api.packet.wifi.IODataSampleRxIndicatorWifiPacket;
import com.digi.xbee.api.packet.wifi.RemoteATCommandResponseWifiPacket;
import com.digi.xbee.api.packet.wifi.RemoteATCommandWifiPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class reads and parses XBee packets from the input stream returning
 * a generic {@code XBeePacket} which can be casted later to the corresponding 
 * high level specific API packet.
 * 
 * <p>All the API and API2 logic is already included so all packet reads are 
 * independent of the XBee operating mode.</p>
 * 
 * <p>Two API modes are supported and both can be enabled using the {@code AP} 
 * (API Enable) command:
 * 
 * <ul>
 * <li><b>API1 - API Without Escapes</b>
 * <p>The data frame structure is defined as follows:</p>
 * 
 * <pre>
 * {@code 
 *   Start Delimiter          Length                   Frame Data                   Checksum
 *       (Byte 1)            (Bytes 2-3)               (Bytes 4-n)                (Byte n + 1)
 * +----------------+  +-------------------+  +--------------------------- +  +----------------+
 * |      0x7E      |  |   MSB   |   LSB   |  |   API-specific Structure   |  |     1 Byte     |
 * +----------------+  +-------------------+  +----------------------------+  +----------------+
 *                MSB = Most Significant Byte, LSB = Least Significant Byte
 * }
 * </pre>
 * </li>
 * 
 * <li><b>API2 - API With Escapes</b>
 * <p>The data frame structure is defined as follows:</p>
 * 
 * <pre>
 * {@code 
 *   Start Delimiter          Length                   Frame Data                   Checksum
 *       (Byte 1)            (Bytes 2-3)               (Bytes 4-n)                (Byte n + 1)
 * +----------------+  +-------------------+  +--------------------------- +  +----------------+
 * |      0x7E      |  |   MSB   |   LSB   |  |   API-specific Structure   |  |     1 Byte     |
 * +----------------+  +-------------------+  +----------------------------+  +----------------+
 *                     \___________________________________  _________________________________/
 *                                                         \/
 *                                             Characters Escaped If Needed
 *                                             
 *                MSB = Most Significant Byte, LSB = Least Significant Byte
 * }
 * </pre>
 * 
 * <p>When sending or receiving an API2 frame, specific data values must be 
 * escaped (flagged) so they do not interfere with the data frame sequencing. 
 * To escape an interfering data byte, the byte {@code 0x7D} is inserted before 
 * the byte to be escaped XOR'd with {@code 0x20}.</p>
 * 
 * <p>The data bytes that need to be escaped:</p>
 * <ul>
 * <li>{@code 0x7E} - Frame Delimiter ({@link SpecialByte#HEADER_BYTE})</li>
 * <li>{@code 0x7D} - Escape ({@link SpecialByte#ESCAPE_BYTE})</li>
 * <li>{@code 0x11} - XON ({@link SpecialByte#XON_BYTE})</li>
 * <li>{@code 0x13} - XOFF ({@link SpecialByte#XOFF_BYTE})</li>
 * </ul>
 * 
 * </li>
 * </ul>
 * 
 * <p>The <b>length</b> field has a two-byte value that specifies the number of 
 * bytes that will be contained in the frame data field. It does not include the 
 * checksum field.</p>
 * 
 * <p>The <b>frame data</b>  forms an API-specific structure as follows:</p>
 * 
 * <pre>
 * {@code 
 *   Start Delimiter          Length                   Frame Data                   Checksum
 *       (Byte 1)            (Bytes 2-3)               (Bytes 4-n)                (Byte n + 1)
 * +----------------+  +-------------------+  +--------------------------- +  +----------------+
 * |      0x7E      |  |   MSB   |   LSB   |  |   API-specific Structure   |  |     1 Byte     |
 * +----------------+  +-------------------+  +----------------------------+  +----------------+
 *                                            /                                                 \
 *                                           /  API Identifier        Identifier specific data   \
 *                                           +------------------+  +------------------------------+
 *                                           |       cmdID      |  |           cmdData            |
 *                                           +------------------+  +------------------------------+
 * }
 * </pre>
 * 
 * <p>The {@code cmdID} frame (API-identifier) indicates which API messages 
 * will be contained in the {@code cmdData} frame (Identifier-specific data).
 * </p>
 * 
 * <p>To test data integrity, a <b>checksum</b> is calculated and verified on 
 * non-escaped data.</p>
 * 
 * @see APIFrameType
 * @see XBeePacket
 * @see com.digi.xbee.api.models.OperatingMode
 */
public class XBeePacketParser {
	
	/**
	 * Parses the bytes from the given input stream depending on the provided 
	 * operating mode and returns the API packet.
	 * 
	 * <p>The operating mode must be {@link OperatingMode#API} or 
	 * {@link OperatingMode#API_ESCAPE}.</p>
	 * 
	 * @param inputStream Input stream to read bytes from.
	 * @param mode XBee device operating mode.
	 * 
	 * @return Parsed packet from the input stream.
	 * 
	 * @throws IllegalArgumentException if {@code mode != OperatingMode.API } and
	 *                              if {@code mode != OperatingMode.API_ESCAPE}.
	 * @throws InvalidPacketException if there is not enough data in the stream or 
	 *                                if there is an error verifying the checksum or
	 *                                if the payload is invalid for the specified frame type.
	 * @throws NullPointerException if {@code inputStream == null} or 
	 *                              if {@code mode == null}.
	 * 
	 * @see XBeePacket
	 * @see com.digi.xbee.api.models.OperatingMode#API
	 * @see com.digi.xbee.api.models.OperatingMode#API_ESCAPE
	 */
	public XBeePacket parsePacket(InputStream inputStream, OperatingMode mode) throws InvalidPacketException {
		if (inputStream == null)
			throw new NullPointerException("Input stream cannot be null.");
		
		if (mode == null)
			throw new NullPointerException("Operating mode cannot be null.");
		
		if (mode != OperatingMode.API && mode != OperatingMode.API_ESCAPE)
			throw new IllegalArgumentException("Operating mode must be API or API Escaped.");
		
		try {
			// Read packet size.
			int hSize = readByte(inputStream, mode);
			int lSize = readByte(inputStream, mode);
			int length = hSize << 8 | lSize;
			
			// Read the payload.
			byte[] payload = readBytes(inputStream, mode, length);
			
			// Calculate the expected checksum.
			XBeeChecksum checksum = new XBeeChecksum();
			checksum.add(payload);
			byte expectedChecksum = (byte)(checksum.generate() & 0xFF);
			
			// Read checksum from the input stream.
			byte readChecksum = (byte)(readByte(inputStream, mode) & 0xFF);
			
			// Verify the checksum of the read bytes.
			if (readChecksum != expectedChecksum)
				throw new InvalidPacketException("Invalid checksum (expected 0x" 
							+ HexUtils.byteToHexString(expectedChecksum) + ").");
			
			return parsePayload(payload);
			
		} catch (IOException e) {
			throw new InvalidPacketException("Error parsing packet: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Parses the bytes from the given array depending on the provided operating
	 * mode and returns the API packet.
	 * 
	 * <p>The operating mode must be {@link OperatingMode#API} or 
	 * {@link OperatingMode#API_ESCAPE}.</p>
	 * 
	 * @param packetByteArray Byte array with the complete frame, starting from 
	 *                        the header and ending in the checksum.
	 * @param mode XBee device operating mode.
	 * 
	 * @return Parsed packet from the given byte array.
	 * 
	 * @throws InvalidPacketException if there is not enough data in the array or 
	 *                                if there is an error verifying the checksum or
	 *                                if the payload is invalid for the specified frame type.
	 * @throws IllegalArgumentException if {@code mode != OperatingMode.API } and
	 *                              if {@code mode != OperatingMode.API_ESCAPE}.
	 * @throws NullPointerException if {@code packetByteArray == null} or 
	 *                              if {@code mode == null}.
	 * 
	 * @see XBeePacket
	 * @see com.digi.xbee.api.models.OperatingMode#API
	 * @see com.digi.xbee.api.models.OperatingMode#API_ESCAPE
	 */
	public XBeePacket parsePacket(byte[] packetByteArray, OperatingMode mode) throws InvalidPacketException {
		if (packetByteArray == null)
			throw new NullPointerException("Packet byte array cannot be null.");
		
		if (mode == null)
			throw new NullPointerException("Operating mode cannot be null.");
		
		if (mode != OperatingMode.API && mode != OperatingMode.API_ESCAPE)
			throw new IllegalArgumentException("Operating mode must be API or API Escaped.");
		
		// Check the byte array has at least 4 bytes.
		if (packetByteArray.length < 4)
			throw new InvalidPacketException("Error parsing packet: Incomplete packet.");
		
		// Check the header of the frame.
		if ((packetByteArray[0] & 0xFF) != SpecialByte.HEADER_BYTE.getValue())
			throw new InvalidPacketException("Invalid start delimiter (expected 0x" 
						+ HexUtils.byteToHexString((byte)SpecialByte.HEADER_BYTE.getValue()) + ").");
		
		return parsePacket(new ByteArrayInputStream(packetByteArray, 1, packetByteArray.length - 1), mode);
	}
	
	/**
	 * Parses the given API payload to get the right API packet, depending 
	 * on its API type ({@code payload[0]}).
	 * 
	 * @param payload The payload of the API frame.
	 * 
	 * @return The corresponding API packet or {@code UnknownXBeePacket} if 
	 *         the frame API type is unknown.
	 *         
	 * @throws InvalidPacketException if the payload is invalid for the 
	 *                                specified frame type.
	 * 
	 * @see APIFrameType
	 * @see XBeePacket
	 */
	private XBeePacket parsePayload(byte[] payload) throws InvalidPacketException {
		// Get the API frame type.
		APIFrameType apiType = APIFrameType.get(payload[0] & 0xFF);
		
		if (apiType == null)
			// Create unknown packet.
			return UnknownXBeePacket.createPacket(payload);
		
		// Parse API payload depending on API ID.
		XBeePacket packet = null;
		switch (apiType) {
		case TX_64:
			packet = TX64Packet.createPacket(payload);
			break;
		case TX_16:
			packet = TX16Packet.createPacket(payload);
			break;
		case REMOTE_AT_COMMAND_REQUEST_WIFI:
			packet = RemoteATCommandWifiPacket.createPacket(payload);
			break;
		case AT_COMMAND:
			packet = ATCommandPacket.createPacket(payload);
			break;
		case AT_COMMAND_QUEUE:
			packet = ATCommandQueuePacket.createPacket(payload);
			break;
		case TRANSMIT_REQUEST:
			packet = TransmitPacket.createPacket(payload);
			break;
		case EXPLICIT_ADDRESSING_COMMAND_FRAME:
			packet = ExplicitAddressingPacket.createPacket(payload);
			break;
		case REMOTE_AT_COMMAND_REQUEST:
			packet = RemoteATCommandPacket.createPacket(payload);
			break;
		case TX_SMS:
			packet = TXSMSPacket.createPacket(payload);
			break;
		case TX_IPV4:
			packet = TXIPv4Packet.createPacket(payload);
			break;
		case SEND_DATA_REQUEST:
			packet = SendDataRequestPacket.createPacket(payload);
			break;
		case DEVICE_RESPONSE:
			packet = DeviceResponsePacket.createPacket(payload);
			break;
		case RX_64:
			packet = RX64Packet.createPacket(payload);
			break;
		case RX_16:
			packet = RX16Packet.createPacket(payload);
			break;
		case RX_IO_64:
			packet = RX64IOPacket.createPacket(payload);
			break;
		case RX_IO_16:
			packet = RX16IOPacket.createPacket(payload);
			break;
		case REMOTE_AT_COMMAND_RESPONSE_WIFI:
			packet = RemoteATCommandResponseWifiPacket.createPacket(payload);
			break;
		case AT_COMMAND_RESPONSE:
			packet = ATCommandResponsePacket.createPacket(payload);
			break;
		case TX_STATUS:
			packet = TXStatusPacket.createPacket(payload);
			break;
		case MODEM_STATUS:
			packet = ModemStatusPacket.createPacket(payload);
			break;
		case TRANSMIT_STATUS:
			packet = TransmitStatusPacket.createPacket(payload);
			break;
		case IO_DATA_SAMPLE_RX_INDICATOR_WIFI:
			packet = IODataSampleRxIndicatorWifiPacket.createPacket(payload);
			break;
		case RECEIVE_PACKET:
			packet = ReceivePacket.createPacket(payload);
			break;
		case EXPLICIT_RX_INDICATOR:
			packet = ExplicitRxIndicatorPacket.createPacket(payload);
			break;
		case IO_DATA_SAMPLE_RX_INDICATOR:
			packet = IODataSampleRxIndicatorPacket.createPacket(payload);
			break;
		case REMOTE_AT_COMMAND_RESPONSE:
			packet = RemoteATCommandResponsePacket.createPacket(payload);
			break;
		case RX_SMS:
			packet = RXSMSPacket.createPacket(payload);
			break;
		case RX_IPV4:
			packet = RXIPv4Packet.createPacket(payload);
			break;
		case SEND_DATA_RESPONSE:
			packet = SendDataResponsePacket.createPacket(payload);
			break;
		case DEVICE_REQUEST:
			packet = DeviceRequestPacket.createPacket(payload);
			break;
		case DEVICE_RESPONSE_STATUS:
			packet = DeviceResponseStatusPacket.createPacket(payload);
			break;
		case FRAME_ERROR:
			packet = FrameErrorPacket.createPacket(payload);
			break;
		case GENERIC:
			packet = GenericXBeePacket.createPacket(payload);
			break;
		case UNKNOWN:
		default:
			packet = UnknownXBeePacket.createPacket(payload);
		}
		return packet;
	}
	
	/**
	 * Reads one byte from the input stream.
	 * 
	 * <p>This operation checks several things like the working mode in order 
	 * to consider escaped bytes.</p>
	 * 
	 * @param inputStream Input stream to read bytes from.
	 * @param mode XBee device working mode.
	 * 
	 * @return The read byte.
	 * 
	 * @throws InvalidPacketException if there is not enough data in the stream or 
	 *                                if there is an error verifying the checksum.
	 * @throws IOException if the first byte cannot be read for any reason other than end of file, or 
	 *                     if the input stream has been closed, or 
	 *                     if some other I/O error occurs.
	 */
	private int readByte(InputStream inputStream, OperatingMode mode) throws InvalidPacketException, IOException {
		int timeout = 300;
		
		int b = readByteFrom(inputStream, timeout);
		
		if (b == -1)
			throw new InvalidPacketException("Error parsing packet: Incomplete packet.");
		
		/* Process the byte for API1. */
		
		if (mode == OperatingMode.API)
			return b;
		
		/* Process the byte for API2. */
		
		// Check if the byte is special.
		if (!SpecialByte.isSpecialByte(b))
			return b;
		
		// Check if the byte is ESCAPE.
		if (b == SpecialByte.ESCAPE_BYTE.getValue()) {
			// Read next byte and escape it.
			b = readByteFrom(inputStream, timeout);
			
			if (b == -1)
				throw new InvalidPacketException("Error parsing packet: Incomplete packet.");
			
			b ^= 0x20;
		} else
			// If the byte is not a escape there is a special byte not escaped.
			throw new InvalidPacketException("Special byte not escaped: 0x" + HexUtils.byteToHexString((byte)(b & 0xFF)) + ".");
		
		return b;
	}
	
	/**
	 * Reads the given amount of bytes from the input stream.
	 * 
	 * <p>This operation checks several things like the working mode in order 
	 * to consider escaped bytes.</p>
	 * 
	 * @param inputStream Input stream to read bytes from.
	 * @param mode XBee device working mode.
	 * @param numBytes Number of bytes to read.
	 * 
	 * @return The read byte array.
	 * 
	 * @throws IOException if the first byte cannot be read for any reason other than end of file, or 
	 *                     if the input stream has been closed, or 
	 *                     if some other I/O error occurs.
	 * @throws InvalidPacketException if there is not enough data in the stream or 
	 *                                if there is an error verifying the checksum.
	 */
	private byte[] readBytes(InputStream inputStream, OperatingMode mode, int numBytes) throws IOException, InvalidPacketException {
		byte[] data = new byte[numBytes];
		
		for (int i = 0; i < numBytes; i++)
			data[i] = (byte)readByte(inputStream, mode);
		
		return data;
	}
	
	/**
	 * Reads a byte from the given input stream.
	 * 
	 * @param inputStream The input stream to read the byte.
	 * @param timeout Timeout to wait for a byte in the input stream 
	 *                in milliseconds.
	 * 
	 * @return The read byte or {@code -1} if the timeout expires or the end
	 *         of the stream is reached.
	 * 
	 * @throws IOException if an I/O errors occurs while reading the byte.
	 */
	private int readByteFrom(InputStream inputStream, int timeout) throws IOException {
		long deadline = new Date().getTime() + timeout;
		
		int b = inputStream.read();
		// Let's try again if the byte is -1.
		while (b == -1 && new Date().getTime() < deadline) {
			b = inputStream.read();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
		
		return b;
	}
}
