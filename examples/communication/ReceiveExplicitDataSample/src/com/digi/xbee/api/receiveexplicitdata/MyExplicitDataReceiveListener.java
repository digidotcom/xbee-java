package com.digi.xbee.api.receiveexplicitdata;

import com.digi.xbee.api.listeners.IExplicitDataReceiveListener;
import com.digi.xbee.api.models.ExplicitXBeeMessage;
import com.digi.xbee.api.utils.HexUtils;

/**
 * Class to manage the XBee received data in explicit format that was sent by 
 * other modules in the same network.
 * 
 * <p>Acts as an explicit data listener by implementing the 
 * {@link IExplicitDataReceiveListener} interface, and is notified when new 
 * data in explicit format for the module is received.</p>
 * 
 * @see IExolicitDataReceiveListener
 *
 */
public class MyExplicitDataReceiveListener implements IExplicitDataReceiveListener {
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.listeners.IExplicitDataReceiveListener#explicitDataReceived(com.digi.xbee.api.models.ExplicitXBeeMessage)
	 */
	@Override
	public void explicitDataReceived(ExplicitXBeeMessage explicitXBeeMessage) {
		System.out.format("From %s >> %s | %s%n", explicitXBeeMessage.getDevice().get64BitAddress(), 
				HexUtils.prettyHexString(HexUtils.byteArrayToHexString(explicitXBeeMessage.getData())), 
				new String(explicitXBeeMessage.getData()));
		System.out.println(" - Source endpoint: 0x" + HexUtils.integerToHexString(explicitXBeeMessage.getSourceEndpoint(), 1));
		System.out.println(" - Destination endpoint: 0x" + HexUtils.integerToHexString(explicitXBeeMessage.getDestinationEndpoint(), 1));
		System.out.println(" - Cluster ID: 0x" + HexUtils.byteArrayToHexString(explicitXBeeMessage.getClusterID()));
		System.out.println(" - Profile ID: 0x" + HexUtils.byteArrayToHexString(explicitXBeeMessage.getProfileID()));
		System.out.println("");
	}
}
