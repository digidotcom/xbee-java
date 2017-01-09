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
package com.digi.xbee.api;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.models.XBee64BitAddress;

public class SendBroadcastDataTest {
	
	// Constants.
	private static final String DATA = "data";
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendBroadcastData(byte[])}.
	 * 
	 * <p>Verify that broadcast data can be sent successfully.</p>
	 */
	@Test
	public void testSendBroadcastDataSuccess() throws Exception {
		// Instantiate an XBeeDevice object with a mocked interface.
		XBeeDevice xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
		
		// Do nothing when the sendData(XBee64BitAddress, byte[]) method is called.
		Mockito.doNothing().when(xbeeDevice).sendData(Mockito.any(XBee64BitAddress.class), Mockito.any(byte[].class));
				
		xbeeDevice.sendBroadcastData(DATA.getBytes());
		
		// Verify that the method sendData(XBee64BitAddress, byte[]) was called with a BROADCAST_ADDRESS.
		Mockito.verify(xbeeDevice, Mockito.times(1)).sendData(Mockito.eq(XBee64BitAddress.BROADCAST_ADDRESS), Mockito.eq(DATA.getBytes()));
	}
}