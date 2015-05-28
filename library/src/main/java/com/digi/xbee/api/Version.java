/**
 * Copyright (c) 2014-2015 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api;

/**
 * Class used to retrieve the current version of the XBee Java Library.
 */
public class Version {
	
	// Constants.
	public final static String CURRENT_VERSION = Version.class.getPackage().getImplementationVersion();
	
	/**
	 * Returns the current version of the XBee Java Library.
	 * 
	 * @return The current version of the XBee Java Library.
	 */
	public static String getCurrentVersion() {
		return CURRENT_VERSION;
	}
}
