package com.digi.xbee.api.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;

import com.digi.xbee.api.exceptions.PacketParsingException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.SpecialByte;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;



/**
 * This abstract class represents the basic structure of an XBee packet. Derived classes
 * should implement their own payload generation depending on their type. Generic
 * actions like checksum compute or packet length calculation is performed here.
 */
public abstract class XBeePacket {

	// Variables
	private XBeeChecksum checksum;
	
	/**
	 * Class constructor. Instances a new XBeePacket.
	 */
	protected XBeePacket() {
		checksum = new XBeeChecksum();
	}

	/**
	 * Generates the XBee packet byte array. Use only while working in 
	 * API mode 1. If API mode is 2, use {@link #generateByteArrayEscaped()}.
	 * 
	 * @return The XBee packet byte array.
	 */
	public byte[] generateByteArray() {
		checksum.reset();
		byte[] packetData = getPacketData();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(SpecialByte.HEADER_BYTE.getValue());
		if (packetData != null) {
			byte[] length = ByteUtils.shortToByteArray((short)packetData.length);
			int msb = length[0];
			int lsb = length[1];
			os.write(msb);
			os.write(lsb);
			for (int i = 0; i < packetData.length; i++) {
				checksum.add(packetData[i]);
				os.write(packetData[i]);
			}
		} else {
			os.write(0);
			os.write(0);
		}
		os.write((byte)checksum.generate() & 0xFF);
		return os.toByteArray();
	}

	/**
	 * Generates the XBee packet byte array escaping the special
	 * bytes. Use only while working in API mode 2. If API mode is 1
	 * use {@link #generateByteArray()}.
	 * 
	 * @return The XBee packet byte array with escaped characters.
	 */
	public byte[] generateByteArrayEscaped() {
		byte[] unescapedArray = generateByteArray();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// Write header byte and do not escape it.
		os.write(SpecialByte.HEADER_BYTE.getValue());
		for (int i = 1; i < unescapedArray.length; i++) {
			// Start at 1 to avoid escaping header byte.
			if (SpecialByte.isSpecialByte(unescapedArray[i])) {
				os.write(SpecialByte.ESCAPE_BYTE.getValue());
				SpecialByte specialByte = SpecialByte.get(unescapedArray[i]);
				os.write(specialByte.escapeByte());
			} else
				os.write(unescapedArray[i]);
		}
		return os.toByteArray();
	}

	/**
	 * Retrieves the packet data.
	 * 
	 * @return The packet data.
	 */
	public abstract byte[] getPacketData();

	/**
	 * Retrieves the packet length.
	 * 
	 * @return The packet length.
	 */
	public int getPacketLength() {
		byte[] packetData = getPacketData();
		if (packetData == null)
			return 0;
		return packetData.length;
	}
	
	/**
	 * retrieves a map with the XBee packet parameters and their values.
	 * 
	 * @return A sorted map containing the XBee packet parameters with their values.
	 */
	public LinkedHashMap<String, String> getParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Start delimiter", HexUtils.integerToHexString(SpecialByte.HEADER_BYTE.getValue(), 1));
		parameters.put("Length", HexUtils.prettyHexString(HexUtils.integerToHexString(getPacketLength(), 2)) + " (" + getPacketLength() + ")");
		parameters.putAll(getPacketParameters());
		parameters.put("Checksum", toString().substring(toString().length() - 2));
		return parameters;
	}
	
	/**
	 * retrieves a map with the XBee packet parameters and their values.
	 * 
	 * @return A sorted map containing the XBee packet parameters with their values.
	 */
	protected abstract LinkedHashMap<String, String> getPacketParameters();
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return HexUtils.byteArrayToHexString(generateByteArray());
	}
	
	/**
	 * Retrieves a pretty string representing the packet.
	 * 
	 * @return Pretty String representing the packet.
	 */
	public String toPrettyString() {
		String value = "Packet: " + toString() + "\n";
		LinkedHashMap<String, String> parameters = getParameters();
		for (String parameter:parameters.keySet())
			value = value + parameter + ": " + parameters.get(parameter) + "\n";
		return value;
	}
	
	/**
	 * Parses the given hexadecimal string and returns a Generic XBee packet. The 
	 * string can contain white spaces.
	 * 
	 * @param packet The hexadecimal string to parse.
	 * @return The generated Generic XBee Packet.
	 * @throws PacketParsingException 
	 * 
	 * @throws NullPointerException if {@code packet == null}.
	 */
	public static XBeePacket parsePacket(String packet) throws PacketParsingException {
		if (packet == null)
			throw new NullPointerException("Packet cannot be null.");
			
		return parsePacket(HexUtils.hexStringToByteArray(packet.trim().replace(" ",  "")));
	}
	
	/**
	 * Parses the given byte array and returns a Generic XBee packet.
	 * 
	 * @param packet The byte array to parse.
	 * @return The generated Generic XBee Packet.
	 * @throws PacketParsingException 
	 * 
	 * @throws NullPointerException if {@code packet == null}.
	 */
	public static XBeePacket parsePacket(byte[] packet) throws PacketParsingException {
		if (packet == null)
			throw new NullPointerException("Packet byte array cannot be null.");
		
		if (packet.length > 1 && ((packet[0] &0xFF) != SpecialByte.HEADER_BYTE.getValue()))
			throw new PacketParsingException("Invalid start delimiter.");
		
		if (packet.length < 4)
			throw new PacketParsingException("Packet length is too short.");
		
		int length = ByteUtils.byteArrayToInt(new byte[] {packet[1], packet[2]});
		if (length != packet.length - 4)
			throw new PacketParsingException("Invalid packet length.");
		
		byte[] data = new byte[length];
		System.arraycopy(packet, 3, data, 0, length);
		byte packetChecksum = packet[packet.length - 1];
		XBeeChecksum checksum = new XBeeChecksum();
		checksum.add(data);
		
		byte generatedChecksum = (byte)(checksum.generate() & 0xFF);
		if (packetChecksum != generatedChecksum)
			throw new PacketParsingException("Invalid '" + packetChecksum + "' checksum. It should be " + generatedChecksum);
		
		XBeePacketParser parser = new XBeePacketParser(new ByteArrayInputStream(packet, 1, packet.length - 1), OperatingMode.API);
		XBeePacket xbeePacket = parser.parsePacket();
		return xbeePacket;
	}
}
