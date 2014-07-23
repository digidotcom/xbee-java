/**
* Copyright (c) 2014 Digi International Inc.,
* All rights not expressly granted are reserved.
*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/.
*
* Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
* =======================================================================
*/
package com.digi.xbee.api.packet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import com.digi.xbee.api.exceptions.PacketParsingException;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.SpecialByte;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.packet.common.ATCommandPacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.common.ReceivePacket;
import com.digi.xbee.api.packet.raw.RX16Packet;
import com.digi.xbee.api.packet.raw.RX64Packet;

/**
 * This class reads and parses XBee packets from the input stream returning
 * a generic {@link com.digi.xbee.api.packet.XBeeAPIPacket} which can be casted later
 * to the corresponding high level specific API packet.
 * 
 * All the API and API2 logic is already included so all packet reads from the input 
 * stream are independent of the XBee working mode.
 */
public class XBeePacketParser {
	
	private static int DEFAULT_TIMEOUT = 2000;

	// Variables
	private InputStream inputStream;
	
	private OperatingMode mode;
	
	private boolean lengthRead;
	
	private int readBytes = 0;
	private int length = 0;
	
	private XBeeChecksum checksum;
	
	/**
	 * Class constructor. Instances a new object of type XBeePacketParser with
	 * the given parameters.
	 * 
	 * @param inputStream Input stream to read bytes from.
	 * @param mode XBee device working mode.
	 * 
	 * @throws NullPointerException if {@code inputStream == null} or 
	 *                              if {@code mode == null}.
	 * @throws NullPointerException if {@code mode != OperatingMode.API} and
	 *                              if {@code mode != OperatingMode.API_ESCAPE}.
	 */
	public XBeePacketParser(InputStream inputStream, OperatingMode mode) {
		if (inputStream == null)
			throw new NullPointerException("Input stream cannot be null.");
		if (mode == null)
			throw new NullPointerException("Operating mode cannot be null.");
		if (mode != OperatingMode.API || mode != OperatingMode.API_ESCAPE)
			throw new IllegalArgumentException("Operating mode must be API or API Escaped.");
		
		this.inputStream = inputStream;
		this.mode = mode;
	}
	
	/**
	 * Parses a packet from the input stream depending on the working mode and
	 * returns it.
	 * 
	 * @return Parsed packet.
	 * @throws PacketParsingException 
	 */
	public XBeePacket parsePacket() throws PacketParsingException {
		try {
			// Reset variables.
			readBytes = 0;
			lengthRead = false;
			
			// Initialize checksum.
			checksum = new XBeeChecksum();
			// Read packet size.
			int hSize = readByte();
			int lSize = readByte();
			length = hSize << 8 | lSize;
			lengthRead = true;
			
			// Read API ID
			int apiID = readByte();
			APIFrameType apiType = APIFrameType.get(apiID);
			
			// Parse API payload depending on API ID.
			XBeePacket packet = null;
			if (apiType == null) {
				// Parse unknown packet.
				// Read payload.
				byte[] payload = readBytes(length - 1);
				// Create packet.
				packet = new UnknownXBeePacket(apiID, payload);
			} else {
				switch (apiType) {
				case AT_COMMAND:
					packet = parseATCommandPacket();
					break;
				case AT_COMMAND_RESPONSE:
					packet = parseATCommandResponsePacket();
					break;
				case RX_64:
					packet = parseRX64Packet();
					break;
				case RX_16:
					packet = parseRX16Packet();
					break;
				case RECEIVE_PACKET:
					packet = parseZigBeeReceivePacket();
					break;
				case GENERIC:
				default:
					packet = parseGenericPacket();
				}
			}
			// Read single byte (checksum) which is automatically verified.
			readByte();
			return packet;
		} catch (IOException e) {
			throw new PacketParsingException("Error parsing packet: " + e.getMessage());
		}
	}
	
	/**
	 * Reads one byte from the input stream. This operation checks several things like the 
	 * working mode in order to consider escaped bytes.
	 * 
	 * @return The read byte.
	 * @throws IOException
	 * @throws PacketParsingException
	 */
	private int readByte() throws IOException, PacketParsingException {
		// Give a bit of time to fill stream if read byte is -1.
		long deadline = new Date().getTime() + 200;
		int b = inputStream.read();
		while (b == -1 && new Date().getTime() < deadline) {
			b = inputStream.read();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
		if (b == -1)
			throw new PacketParsingException("Read -1 from stream.");
		if (mode == OperatingMode.API_ESCAPE) {
			// Check if the byte is special.
			if (SpecialByte.isSpecialByte(b)) {
				// Check if the byte is ESCAPE
				if (b == SpecialByte.ESCAPE_BYTE.getValue()) {
					// Read next byte and unescape it.
					// Give a bit of time to fill stream if read byte is -1.
					deadline = new Date().getTime() + 200;
					b = inputStream.read();
					while (b == -1 && new Date().getTime() < deadline) {
						b = inputStream.read();
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {}
					}
					if (b == -1)
						throw new PacketParsingException("Read -1 from stream.");
					b ^= 0x20;
				} else {
					// TODO: Log some kind of information here when logging is implemented.
					// This should NEVER not occur!
					// rebootTheMatrix();
				}
			}
		}
		// If length was already read add byte to read bytes and checksum computing.
		if (lengthRead) {
			checksum.add(b);
			readBytes += 1;
			// Check if packet is fully read.
			if (readBytes >= length + 1) {
				// Was checksum byte, verify it!
				if (!checksum.validate())
					throw new PacketParsingException("Error verifying packet checksum.");
			}
		}
		return b;
	}
	
	/**
	 * Reads the given amount of bytes from the input stream. This operation checks several 
	 * things like the working mode in order to consider escaped bytes.
	 * 
	 * @param numBytes Number of bytes to read.
	 * @return The read byte array.
	 * @throws PacketParsingException 
	 * @throws IOException 
	 */
	private byte[] readBytes(int numBytes) throws IOException, PacketParsingException {
		byte[] data = new byte[numBytes];
		switch (mode) {
		case API:
			int numBytesRead = 0;
			int currentRead = 0;
			long deadline = new Date().getTime() + DEFAULT_TIMEOUT;
			while (new Date().getTime() < deadline) {
				currentRead =  inputStream.read(data, numBytesRead, numBytes - numBytesRead);
				if (currentRead == -1)
					throw new PacketParsingException("Read -1 from stream.");
				numBytesRead = numBytesRead + currentRead;
				if (numBytesRead < numBytes) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {}
				} else
					break;
			}
			if (numBytesRead < numBytes)
				throw new PacketParsingException("Not enough data in the stream.");
			if (lengthRead) {
				checksum.add(data);
				readBytes += numBytes;
				// Check if packet is fully read.
				if (readBytes >= length + 1) {
					// Was checksum byte, verify it!
					if (!checksum.validate())
						throw new PacketParsingException("Error verifying packet checksum.");
				}
			}
			break;
		case API_ESCAPE:
			for (int i = 0; i < numBytes; i++)
				data[i] = (byte)readByte();
			break;
		default:
			break;
		}
		return data;
	}
	
	/**
	 * Reads an XBee 64 bit address from the input stream.
	 * 
	 * @return The read XBee 64 bit address, null if error.
	 * @throws PacketParsingException 
	 * @throws IOException 
	 */
	private XBee64BitAddress readXBee64BitAddress() throws IOException, PacketParsingException {
		int[] address = new int[8];
		for (int i = 0; i < 8; i++)
			address[i] = readByte();
		return new XBee64BitAddress(address);
	}
	
	/**
	 * Reads an XBee 16 bit address from the input stream.
	 * 
	 * @return The read XBee 16 bit address, null if error.
	 * @throws PacketParsingException 
	 * @throws IOException 
	 */
	private XBee16BitAddress readXBee16BitAddress() throws IOException, PacketParsingException {
		int hsb = readByte();
		int lsb = readByte();
		return new XBee16BitAddress(hsb, lsb);
	}
	
	/**
	 * Parses the input stream and returns a packet of type Generic.
	 * 
	 * @return Parsed Generic packet.
	 * @throws IOException
	 * @throws PacketParsingException
	 */
	private XBeePacket parseGenericPacket() throws IOException, PacketParsingException {
		byte[] commandData = null;
		if (readBytes < length)
			commandData = readBytes(length - readBytes);
		return new GenericXBeePacket(commandData);
	}
	
	/**
	 * Parses the input stream and returns a packet of type AT Command.
	 * 
	 * @return Parsed AT Command packet.
	 * @throws IOException
	 * @throws ParsingException
	 */
	private XBeePacket parseATCommandPacket() throws IOException, PacketParsingException {
		int frameID = readByte();
		String command = new String(readBytes(2));
		byte[] parameterData = null;
		if (readBytes < length)
			parameterData = readBytes(length - readBytes);
		return new ATCommandPacket(frameID, command, parameterData);
	}
	
	/**
	 * Parses the input stream and returns a packet of type AT Command Response.
	 * 
	 * @return Parsed AT Command Response packet.
	 * @throws IOException
	 * @throws ParsingException
	 */
	private XBeePacket parseATCommandResponsePacket() throws IOException, PacketParsingException {
		int frameID = readByte();
		String command = new String(readBytes(2));
		int status = readByte();
		byte[] commandData = null;
		if (readBytes < length)
			commandData = readBytes(length - readBytes);
		return new ATCommandResponsePacket(frameID, ATCommandStatus.get(status), command, commandData);
	}
	
	/**
	 * Parses the input stream and returns a packet of type ZigBee Receive.
	 * 
	 * @return Parsed ZigBee Receive packet.
	 * @throws IOException
	 * @throws PacketParsingException
	 */
	private XBeePacket parseZigBeeReceivePacket() throws IOException, PacketParsingException {
		XBee64BitAddress sourceAddress64 = readXBee64BitAddress();
		XBee16BitAddress sourceAddress16 = readXBee16BitAddress();
		int receiveOptions = readByte();
		byte[] data = null;
		if (readBytes < length)
			data = readBytes(length - readBytes);
		return new ReceivePacket(sourceAddress64, sourceAddress16, receiveOptions, data);
	}
	
	/**
	 * Parses the input stream and returns a packet of type RX 16.
	 * 
	 * @return Parsed RX 16 packet.
	 * @throws IOException
	 * @throws PacketParsingException
	 */
	private XBeePacket parseRX16Packet() throws IOException, PacketParsingException {
		XBee16BitAddress sourceAddress16 = readXBee16BitAddress();
		int signalStrength = readByte();
		int receiveOptions = readByte();
		byte[] data = null;
		if (readBytes < length)
			data = readBytes(length - readBytes);
		return new RX16Packet(sourceAddress16, signalStrength, receiveOptions, data);
	}
	
	/**
	 * Parses the input stream and returns a packet of type RX 64.
	 * 
	 * @return Parsed RX 64 packet.
	 * @throws IOException
	 * @throws PacketParsingException
	 */
	private XBeePacket parseRX64Packet() throws IOException, PacketParsingException {
		XBee64BitAddress sourceAddress64 = readXBee64BitAddress();
		int signalStrength = readByte();
		int receiveOptions = readByte();
		byte[] data = null;
		if (readBytes < length)
			data = readBytes(length - readBytes);
		return new RX64Packet(sourceAddress64, signalStrength, receiveOptions, data);
	}
}
