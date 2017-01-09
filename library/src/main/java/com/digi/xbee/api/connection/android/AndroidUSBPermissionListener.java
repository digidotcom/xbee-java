/**
 * Copyright 2017, Digi International Inc.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES 
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR 
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES 
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN 
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF 
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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
