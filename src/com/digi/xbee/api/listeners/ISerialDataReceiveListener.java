package com.digi.xbee.api.listeners;

/**
 * This interface defines the required methods that should be
 * implemented to behave as a serial data listener and be notified
 * when new serial data is received from an XBee device of the network.
 */
public interface ISerialDataReceiveListener {

	/**
	 * Called when serial data is received from a remote node.
	 * 
	 * @param address The address of the remote node that sent the data.
	 * @param data The received data.
	 */
	public void serialDataReceived(String address, byte[] data);
}
