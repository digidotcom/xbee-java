package com.digi.xbee.api.receiveserialdata;

import com.digi.xbee.api.listeners.ISerialDataReceiveListener;

/**
 * Class to manage the received packets.
 * 
 * <p>Acts as a packet listener by implementing the 
 * {@code ISerialDataReceiveListener} interface, and is notified when a new XBee
 * API packet is received.</p>
 * 
 * @see ISerialDataReceiveListener
 *
 */
public class MySerialDataReceiveListener implements ISerialDataReceiveListener {
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.listeners.ISerialDataReceiveListener#serialDataReceived(java.lang.String, byte[])
	 */
	public void serialDataReceived(String address, byte[] data) {
		System.out.println("Data received from " + address + ": " + new String(data));
	}

}
