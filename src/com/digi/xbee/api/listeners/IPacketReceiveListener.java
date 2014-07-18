package com.digi.xbee.api.listeners;

import com.digi.xbee.api.packet.XBeePacket;

/**
 * This interface defines the required methods that should be
 * implemented to behave as a packet listener and be notified
 * when new packets are received from an XBee device of the network.
 */
public interface IPacketReceiveListener {

	/**
	 * Called when a packet received through the connection interface.
	 * 
	 * @param receivedPacket The received packet.
	 */
	public void packetReceived(XBeePacket receivedPacket);
}
