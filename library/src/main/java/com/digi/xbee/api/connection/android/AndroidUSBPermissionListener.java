package com.digi.xbee.api.connection.android;

/**
 * This interface is used as a listener to wait from user permissions on USB access
 * request in Android. Permissions can be granted or denied, the result is passed 
 * within the {{@link #permissionReceived(boolean)} method to the class implementing it.
 */
public interface AndroidUSBPermissionListener {

	/**
	 * This method is called whenever the USB Permissions request answer 
	 * is received from the user.
	 * 
	 * @param permissionGranted {@code true} if user granted USB permissions
	 *                          {@code false} otherwise.
	 */
	public void permissionReceived(boolean permissionGranted);
}
