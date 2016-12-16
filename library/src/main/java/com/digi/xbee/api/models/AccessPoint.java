/**
 * Copyright (c) 2016 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.models;

/**
 * This class represents an Access Point for the Wi-Fi protocol. It contains 
 * SSID, the encryption type and the link quality between the Wi-Fi module and 
 * the access point.
 * 
 * <p>This class is used within the XBee Java Library to list the access points 
 * and connect to a specific one in the Wi-Fi protocol.</p>
 */
public class AccessPoint {

	// Constants.
	private static final String ERROR_CHANNEL = "Channel cannot be negative.";
	private static final String ERROR_SIGNAL_QUALITY = "Signal quality must be between 0 and 100.";
	
	// Variables.
	private final String ssid;
	
	private final WiFiEncryptionType encryptionType;
	
	private int channel;
	private int signalQuality;
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code AccessPoint} with the given parameters.
	 * 
	 * @param ssid The SSID of the access point.
	 * @param encryptionType The encryption type configured in the access 
	 *                       point.
	 * 
	 * @throws IllegalArgumentException if {@code ssid.length < 0}.
	 * @throws NullPointerException if {@code ssid == null} or 
	 *                              if {@code encryptionType == null}.
	 * 
	 * @see #AccessPoint(String, WiFiEncryptionType, int, int)
	 * @see com.digi.xbee.api.models.WiFiEncryptionType
	 */
	public AccessPoint(String ssid, WiFiEncryptionType encryptionType) {
		this(ssid, encryptionType, 0, 0);
	}
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code AccessPoint} with the given parameters.
	 * 
	 * @param ssid The SSID of the access point.
	 * @param encryptionType The encryption type configured in the access 
	 *                       point.
	 * @param channgel Operating channel of the access point.
	 * @param quality Signal quality with the access point in %.
	 * 
	 * @throws IllegalArgumentException if {@code ssid.length < 0} or 
	 *                                  if {@code channel < 0} or 
	 *                                  if (@code signalQuality < 0} or 
	 *                                  if {@code signalQuality > 100}.
	 * @throws NullPointerException if {@code ssid == null} or
	 *                              if {@code encryptionType == null}.
	 * 
	 * @see #AccessPoint(String, WiFiEncryptionType)
	 * @see com.digi.xbee.api.models.WiFiEncryptionType
	 */
	public AccessPoint(String ssid, WiFiEncryptionType encryptionType, int channel,
			int signalQuality) {
		if (ssid == null)
			throw new NullPointerException("SSID cannot be null.");
		if (encryptionType == null)
			throw new NullPointerException("Encryption type cannot be null.");
		
		if (ssid.length() == 0)
			throw new IllegalArgumentException("SSID cannot be empty.");
		if (channel < 0)
			throw new IllegalArgumentException(ERROR_CHANNEL);
		if (signalQuality < 0 || signalQuality > 100)
			throw new IllegalArgumentException(ERROR_SIGNAL_QUALITY);
		
		this.ssid = ssid;
		this.encryptionType = encryptionType;
		this.channel = channel;
		this.signalQuality = signalQuality;
	}
	
	/**
	 * Returns the SSID of the access point.
	 * 
	 * @return The SSID of the access point.
	 */
	public String getSSID() {
		return ssid;
	}
	
	/**
	 * Returns the encryption type of the access point.
	 * 
	 * @return The encryption type of the access point.
	 * 
	 * @see com.digi.xbee.api.models.WiFiEncryptionType
	 */
	public WiFiEncryptionType getEncryptionType() {
		return encryptionType;
	}
	
	/**
	 * Returns the operating channel of the access point.
	 * 
	 * @return The operating channel of the access point.
	 * 
	 * @see #setChannel(int)
	 */
	public int getChannel() {
		return channel;
	}
	
	/**
	 * Sets the new channel of the access point.
	 * 
	 * @param channel The new channel of the access point.
	 * 
	 * @see #getChannel()
	 */
	public void setChannel(int channel) {
		if (channel < 0)
			throw new IllegalArgumentException(ERROR_CHANNEL);
		
		this.channel = channel;
	}
	
	/**
	 * Returns the signal quality with the access point in %.
	 * 
	 * @return The signal quality with the access point in %.
	 * 
	 * @see #setSignalQuality(int)
	 */
	public int getSignalQuality() {
		return signalQuality;
	}
	
	/**
	 * Sets the new signal quality with the access point.
	 * 
	 * @param signalQuality The new signal quality with the access point.
	 * 
	 * @see #getSignalQuality()
	 */
	public void setSignalQuality(int signalQuality) {
		if (signalQuality < 0 || signalQuality > 100)
			throw new IllegalArgumentException(ERROR_SIGNAL_QUALITY);
		
		this.signalQuality = signalQuality;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder message = new StringBuilder();
		message.append(ssid);
		message.append(" (");
		message.append(encryptionType.name());
		message.append(") - CH: ");
		message.append(channel);
		message.append(" - Signal: ");
		message.append(signalQuality);
		message.append("%");
		
		return message.toString();
	}
}
