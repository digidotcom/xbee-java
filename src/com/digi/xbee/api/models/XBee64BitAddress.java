package com.digi.xbee.api.models;

import java.util.Arrays;

import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a 64 bit address (also known as MAC address). Each
 * XBee device has its own 64 bit address which is unique.
 */
public class XBee64BitAddress {

	// Constants
	private static final String COORDINATOR_ADDRESS = "0x0000";
	private static final String BROADCAST_ADDRESS = "0xFFFF";
	private static final String DEVICE_ID_SEPARATOR = "-";
	private static final String DEVICE_ID_MAC_SEPARATOR = "FF";
	
	// Variables
	private byte[] address;
	
	/**
	 * Class constructor. Instances a new object of type XBee64BitAddress
	 * with the given parameters-
	 * 
	 * @param address The XBee 64 bit address as byte array.
	 */
	public XBee64BitAddress(byte[] address) {
		this.address = address;
	}
	
	/**
	 * Class constructor. Instances a new object of type XBee64BitAddress
	 * with the given parameters-
	 * 
	 * @param address The XBee 64 bit address as integer array.
	 */
	public XBee64BitAddress(int[] address) {
		this.address = new byte[address.length];
		for (int i = 0; i < address.length; i++)
			this.address[i] = (byte)address[i];
	}
	
	/**
	 * Class constructor. Instances a new object of type XBee64BitAddress
	 * with the given parameters-
	 * 
	 * @param address The XBee 64 bit address as string.
	 */
	public XBee64BitAddress(String address) {
		byte[] byteAddress = HexUtils.hexStringToByteArray(address);
		this.address = new byte[8];
		int diff = 8 - byteAddress.length;
		for (int i = 0; i < diff; i++)
			this.address[i] = 0;
		for (int i = diff; i < 8; i++)
			this.address[i] = byteAddress[i - diff];
	}
	
	/**
	 * Class constructor. Instances a new object of type XBee64BitAddress
	 * with the given bytes being b0 the more significant byte and b7 the 
	 * less significant byte.
	 * 
	 * @param b0 XBee 64 bit address b0.
	 * @param b1 XBee 64 bit address b1.
	 * @param b2 XBee 64 bit address b2.
	 * @param b3 XBee 64 bit address b3.
	 * @param b4 XBee 64 bit address b4.
	 * @param b5 XBee 64 bit address b5.
	 * @param b6 XBee 64 bit address b6.
	 * @param b7 XBee 64 bit address b7.
	 */
	public XBee64BitAddress(int b0, int b1, int b2, int b3, int b4, int b5, int b6, int b7) {
		address = new byte[8];
		address[0] = (byte) b0;
		address[1] = (byte) b1;
		address[2] = (byte) b2;
		address[3] = (byte) b3;
		address[4] = (byte) b4;
		address[5] = (byte) b5;
		address[6] = (byte) b6;
		address[7] = (byte) b7;
	}
	
	/**
	 * Instances a new broadcast XBee64BitAddress.
	 * 
	 * @return Broadcast XBee 64 bit address.
	 */
	public static XBee64BitAddress BroadcastAddress() {
		return new XBee64BitAddress(BROADCAST_ADDRESS);
	}
	
	/**
	 * Instances a new coordinator XBee64BitAddress.
	 * 
	 * @return Coordinator XBee 64 bit address.
	 */
	public static XBee64BitAddress CoordinatorAddress() {
		return new XBee64BitAddress(COORDINATOR_ADDRESS);
	}
	
	/**
	 * Sets the XBee 64 bit address value.
	 * 
	 * @param address The XBee 64 bit address value.
	 */
	public void setValue(byte[] address) {
		this.address = address;
	}
	
	/**
	 * Retrieves the XBee 64 bit address value as byte array.
	 * 
	 * @return XBee 64 bit address value as byte array.
	 */
	public byte[] getValue() {
		return address;
	}
	
	/**
	 * Generates a Device ID to be used in Device Cloud from this XBee 64-bit Address.
	 * 
	 * @return Device ID corresponding to this address.
	 */
	public String generateDeviceID() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++)
				sb.append(HexUtils.byteArrayToHexString(new byte[]{0}));
			sb.append(DEVICE_ID_SEPARATOR);
		}
		// Here we should have "00000000-00000000-"
		// Append first three bytes of the MAC Address, discard first 2.
		sb.append(HexUtils.byteArrayToHexString(new byte[]{address[2], address[3], address[4]}));
		sb.append(DEVICE_ID_MAC_SEPARATOR);
		sb.append(DEVICE_ID_SEPARATOR);
		sb.append(DEVICE_ID_MAC_SEPARATOR);
		// Here we should have "00000000-00000000-XXXXXXFF-FF"
		// Append second three bytes of the MAC Address.
		sb.append(HexUtils.byteArrayToHexString(new byte[]{address[5], address[6], address[7]}));
		return sb.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof XBee64BitAddress))
			return false;
		XBee64BitAddress addr = (XBee64BitAddress)obj;
		return Arrays.equals(addr.getValue(), getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return HexUtils.byteArrayToHexString(address);
	}
}
