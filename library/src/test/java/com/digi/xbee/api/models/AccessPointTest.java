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
package com.digi.xbee.api.models;

import static org.junit.Assert.*;

import org.junit.Test;

public class AccessPointTest {

	// Constants.
	private static final String SSID = "My Access Point";
	private static final WiFiEncryptionType ENCRYPTION_TYPE = WiFiEncryptionType.WEP;
	private static final int CHANNEL = 12;
	private static final int SIGNAL_QUALITY = 75;
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.AccessPoint#AccessPoint(String, WiFiEncryptionType)}.
	 * 
	 * <p>Verify that the {@code AccessPoint} cannot be created if the SSID is null.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testCreateNullSSID() {
		new AccessPoint(null, ENCRYPTION_TYPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.AccessPoint#AccessPoint(String, WiFiEncryptionType)}.
	 * 
	 * <p>Verify that the {@code AccessPoint} cannot be created if the encryption type is null.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testCreateNullEncryptionType() {
		new AccessPoint(SSID, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.AccessPoint#AccessPoint(String, WiFiEncryptionType)}.
	 * 
	 * <p>Verify that the {@code AccessPoint} cannot be created if the length of the SSID is 0.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateIllegalSSIDEmpty() {
		new AccessPoint("", ENCRYPTION_TYPE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.AccessPoint#AccessPoint(String, WiFiEncryptionType, int, int)}.
	 * 
	 * <p>Verify that the {@code AccessPoint} cannot be created if the length of the SSID is 0.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateIllegalChannelNegative() {
		new AccessPoint(SSID, ENCRYPTION_TYPE, -5, 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.AccessPoint#AccessPoint(String, WiFiEncryptionType, int, int)}.
	 * 
	 * <p>Verify that the {@code AccessPoint} cannot be created if the signal quality is negative.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateIllegalSignalQualityNegative() {
		new AccessPoint(SSID, ENCRYPTION_TYPE, CHANNEL, -10);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.AccessPoint#AccessPoint(String, WiFiEncryptionType, int, int)}.
	 * 
	 * <p>Verify that the {@code AccessPoint} cannot be created if the signal quality is 
	 * greater than 100.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateIllegalSignalQualityBIG() {
		new AccessPoint(SSID, ENCRYPTION_TYPE, CHANNEL, 150);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.AccessPoint#AccessPoint(String, WiFiEncryptionType)}, 
	 * {@link com.digi.xbee.api.models.AccessPoint#getSSID()},
	 * {@link com.digi.xbee.api.models.AccessPoint#getEncryptionKey()}.
	 * 
	 * <p>Verify that the {@code AccessPoint} can be created successfully and the getters work 
	 * properly when the channel signal quality are not provided.</p>
	 */
	@Test
	public void testCreateSuccessNotChannelSignalQuality() {
		AccessPoint accessPoint = new AccessPoint(SSID, ENCRYPTION_TYPE);
		
		String accessPointString = SSID + " (" + ENCRYPTION_TYPE.name() + ") - CH: 0 - Signal: 0%";
		
		assertEquals(SSID, accessPoint.getSSID());
		assertEquals(ENCRYPTION_TYPE, accessPoint.getEncryptionType());
		assertEquals(0, accessPoint.getSignalQuality());
		assertEquals(accessPointString, accessPoint.toString());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.AccessPoint#AccessPoint(String, WiFiEncryptionType, int, int)}, 
	 * {@link com.digi.xbee.api.models.AccessPoint#getSSID()},
	 * {@link com.digi.xbee.api.models.AccessPoint#getEncryptionKey()},
	 * {@link com.digi.xbee.api.models.AccessPoint#getChannel()}, 
	 * {@link com.digi.xbee.api.models.AccessPoint#getSignalQuality()}.
	 * 
	 * <p>Verify that the {@code AccessPoint} can be created successfully and the getters work 
	 * properly when the channel and signal quality are provided.</p>
	 */
	@Test
	public void testCreateSuccess() {
		AccessPoint accessPoint = new AccessPoint(SSID, ENCRYPTION_TYPE, CHANNEL, SIGNAL_QUALITY);
		
		String accessPointString = SSID + " (" + ENCRYPTION_TYPE.name() + ") - CH: " + CHANNEL + " - Signal: " + SIGNAL_QUALITY + "%";
		
		assertEquals(SSID, accessPoint.getSSID());
		assertEquals(ENCRYPTION_TYPE, accessPoint.getEncryptionType());
		assertEquals(CHANNEL, accessPoint.getChannel());
		assertEquals(SIGNAL_QUALITY, accessPoint.getSignalQuality());
		assertEquals(accessPointString, accessPoint.toString());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.AccessPoint#AccessPoint(String, WiFiEncryptionType, int)}, 
	 * {@link com.digi.xbee.api.models.AccessPoint#getSignalQuality()},
	 * {@link com.digi.xbee.api.models.AccessPoint#setSignalQuality(int)}.
	 * 
	 * <p>Verify that the signal quality cannot be set if it is negative.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetSignalQualityNegative() {
		AccessPoint accessPoint = new AccessPoint(SSID, ENCRYPTION_TYPE);
		
		accessPoint.setSignalQuality(-10);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.AccessPoint#AccessPoint(String, WiFiEncryptionType, int)}, 
	 * {@link com.digi.xbee.api.models.AccessPoint#getSignalQuality()},
	 * {@link com.digi.xbee.api.models.AccessPoint#setSignalQuality(int)}.
	 * 
	 * <p>Verify that the signal quality cannot be set if it is greater than 100.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetSignalQualityBIG() {
		AccessPoint accessPoint = new AccessPoint(SSID, ENCRYPTION_TYPE);
		
		accessPoint.setSignalQuality(150);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.AccessPoint#AccessPoint(String, WiFiEncryptionType, int)}, 
	 * {@link com.digi.xbee.api.models.AccessPoint#setSignalQuality(int)},
	 * {@link com.digi.xbee.api.models.AccessPoint#getSignalQuality()}.
	 * 
	 * <p>Verify that the signal quality can be set and get successfully.</p>
	 */
	@Test
	public void testSetSignalQualitySuccess() {
		AccessPoint accessPoint = new AccessPoint(SSID, ENCRYPTION_TYPE);
		
		assertEquals(0, accessPoint.getSignalQuality());
		accessPoint.setSignalQuality(SIGNAL_QUALITY);
		assertEquals(SIGNAL_QUALITY, accessPoint.getSignalQuality());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.AccessPoint#AccessPoint(String, WiFiEncryptionType)}, 
	 * {@link com.digi.xbee.api.models.AccessPoint#getChannel()},
	 * {@link com.digi.xbee.api.models.AccessPoint#setChannel(int)}.
	 * 
	 * <p>Verify that the channel cannot be set if it is negative.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetChannelNegative() {
		AccessPoint accessPoint = new AccessPoint(SSID, ENCRYPTION_TYPE);
		
		accessPoint.setChannel(-10);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.AccessPoint#AccessPoint(String, WiFiEncryptionType)}, 
	 * {@link com.digi.xbee.api.models.AccessPoint#setChannel(int)},
	 * {@link com.digi.xbee.api.models.AccessPoint#getChannel()}.
	 * 
	 * <p>Verify that the channel can be set and get successfully.</p>
	 */
	@Test
	public void testSetChannelSuccess() {
		AccessPoint accessPoint = new AccessPoint(SSID, ENCRYPTION_TYPE);
		
		assertEquals(0, accessPoint.getChannel());
		accessPoint.setChannel(CHANNEL);
		assertEquals(CHANNEL, accessPoint.getChannel());
	}
}
