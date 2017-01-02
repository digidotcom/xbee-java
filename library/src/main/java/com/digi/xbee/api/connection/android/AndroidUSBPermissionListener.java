/**
* Copyright (c) 2014-2017 Digi International Inc.,
* All rights not expressly granted are reserved.
*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/.
*
* Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
* =======================================================================
*/
package com.digi.xbee.api.connection.android;

/**
 * This interface is used as a listener to wait for user permissions after a 
 * USB access request is sent in Android.
 * 
 * <p>Permissions can be granted or denied, the result is passed within the 
 * {{@link #permissionReceived(boolean)} method to the class implementing it.
 * </p>
 * 
 * @since 1.2.0
 */
public interface AndroidUSBPermissionListener {

	/**
	 * This method is called whenever the USB Permissions request answer is 
	 * received from the user.
	 * 
	 * @param permissionGranted {@code true} if user granted USB permissions
	 *                          {@code false} otherwise.
	 */
	public void permissionReceived(boolean permissionGranted);
}
