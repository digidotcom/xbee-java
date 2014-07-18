package com.digi.xbee.api.packet.raw;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.XBeeAPIType;
import com.digi.xbee.api.utils.HexUtils;

public class RX64Packet extends XBeeAPIPacket {

	// Variables
	private XBee64BitAddress sourceAddress;
	
	private int rssi;
	private int receiveOptions;
	
	private byte[] receivedData;
	
	/**
	 * Class constructor. Instances a new object of type Receive64Packet with
	 * the given parameters.
	 * 
	 * @param sourceAddress 64-bit address of the sender.
	 * @param rssi Received signal strength indicator.
	 * @param receiveOptions Bitfield indicating the receive options. See {@link com.digi.xbee.models.XBeeReceiveOptions}.
	 * @param receivedData Received RF data.
	 */
	public RX64Packet(XBee64BitAddress sourceAddress, int rssi, int receiveOptions, byte[] receivedData) {
		super(XBeeAPIType.RX_64);
		this.sourceAddress = sourceAddress;
		this.rssi = rssi;
		this.receiveOptions = receiveOptions;
		this.receivedData = receivedData;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIData()
	 */
	public byte[] getAPIData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(sourceAddress.getValue());
			os.write(rssi);
			os.write(receiveOptions);
			if (receivedData != null)
				os.write(receivedData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return os.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#hasAPIFrameID()
	 */
	public boolean hasAPIFrameID() {
		return false;
	}
	
	/**
	 * Sets the 64 bit sender/source address.
	 * 
	 * @param sourceAddress The 64 bit sender/source address.
	 */
	public void setSourceAddress(XBee64BitAddress sourceAddress) {
		this.sourceAddress = sourceAddress;
	}
	
	/**
	 * Retrieves the 64 bit sender/source address. 
	 * 
	 * @return The 64 bit sender/source address.
	 */
	public XBee64BitAddress getSourceAddress() {
		return sourceAddress;
	}
	
	/**
	 * Sets the receive options bitfield.
	 * See {@link com.digi.xbee.models.XBeeReceiveOptions}.
	 * 
	 * @param options Receive options bitfield.
	 */
	public void setReceiveOptions(int options) {
		this.receiveOptions = options;
	}
	
	/**
	 * Retrieves the receive options bitfield.
	 * See {@link com.digi.xbee.models.XBeeReceiveOptions}.
	 * 
	 * @return Receive options bitfield.
	 */
	public int getReceiveOptions() {
		return receiveOptions;
	}
	
	/**
	 * Sets the received RF data.
	 * 
	 * @param receivedData Received RF data.
	 */
	public void setReceivedData (byte[] receivedData){
		this.receivedData = receivedData;
	}
	
	/**
	 * Retrieves the received RF data.
	 * 
	 * @return Received RF data.
	 */
	public byte[] getReceivedData (){
		return receivedData;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeePacket#getPacketParameters()
	 */
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("64-bit source address", HexUtils.prettyHexString(sourceAddress.toString()));
		parameters.put("RSSI", HexUtils.prettyHexString(HexUtils.integerToHexString(rssi, 1)));
		parameters.put("Options", HexUtils.prettyHexString(HexUtils.integerToHexString(receiveOptions, 1)));
		if (receivedData != null)
			parameters.put("RF data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(receivedData)));
		return parameters;
	}
}
