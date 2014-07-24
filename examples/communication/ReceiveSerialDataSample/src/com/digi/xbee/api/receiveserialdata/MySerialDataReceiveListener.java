package com.digi.xbee.api.receiveserialdata;

import com.digi.xbee.api.listeners.ISerialDataReceiveListener;
import com.digi.xbee.api.utils.HexUtils;

/**
 * Class to manage the XBee received data that was sent by other modules in the 
 * same network.
 * 
 * <p>Acts as a data listener by implementing the 
 * {@code ISerialDataReceiveListener} interface, and is notified when new 
 * data for the module is received.</p>
 * 
 * @see ISerialDataReceiveListener
 *
 */
public class MySerialDataReceiveListener implements ISerialDataReceiveListener {
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.listeners.ISerialDataReceiveListener#serialDataReceived(java.lang.String, byte[])
	 */
	@Override
	public void serialDataReceived(String address, byte[] data) {
		System.out.format("Data received from %s >> %s | %s%n", address, 
				HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)), 
				new String(data));
	}
}
