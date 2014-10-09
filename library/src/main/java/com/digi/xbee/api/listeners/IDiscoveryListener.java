package com.digi.xbee.api.listeners;

import com.digi.xbee.api.RemoteXBeeDevice;

/**
 * Listener interface used to be notified about device discovery events. 
 */
public interface IDiscoveryListener {

	/**
	 * Notifies that a remote device was discovered.
	 * 
	 * @param discoveredDevice The discovered remote device.
	 */
	public void deviceDiscovered(RemoteXBeeDevice discoveredDevice);
	
	/**
	 * Notifies that an error occurred during the discovery process.
	 * 
	 * <p>This method is only called when an error occurs but does not cause 
	 * the process to finish.</p>
	 * 
	 * @param error The error message.
	 */
	public void discoveryError(String error);
	
	/**
	 * Notifies that the discovery process has finished.
	 * 
	 * @param error The error message, or {@code null} if the process finished 
	 *              successfully.
	 */
	public void discoveryFinished(String error);
}
