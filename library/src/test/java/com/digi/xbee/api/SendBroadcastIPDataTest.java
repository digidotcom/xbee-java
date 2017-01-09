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

import java.net.Inet4Address;

import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.models.IPProtocol;

public class SendBroadcastIPDataTest {
	
	// Constants.
	private static final String DATA = "data";
	
	private static final int PORT = 12345;
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendBroadcastIPData(int, IPProtocol, byte[])}.
	 * 
	 * <p>Verify that broadcast IP data can be sent successfully.</p>
	 */
	@Test
	public void testSendBroadcastIPDataSuccess() throws Exception {
		// Instantiate an IPDevice object with a mocked interface.
		IPDevice ipDevice = PowerMockito.spy(new IPDevice(Mockito.mock(SerialPortRxTx.class)));
		
		// Do nothing when the sendNetowrkData(Inet4Address, int, IPProtocol, boolean, byte[]) method is called.
		Mockito.doNothing().when(ipDevice).sendIPData(Mockito.any(Inet4Address.class), Mockito.any(int.class), Mockito.any(IPProtocol.class), Mockito.anyBoolean(), Mockito.any(byte[].class));
				
		ipDevice.sendBroadcastIPData(PORT, DATA.getBytes());
		
		// Verify that the method sendIPData(Inet4Address, int, IPProtocol, boolean, byte[]) was 
		// called with a BROADCAST_ADDRESS and UDP protocol.
		Mockito.verify(ipDevice, Mockito.times(1)).sendIPData(Mockito.eq((Inet4Address) Inet4Address.getByName(IPDevice.BROADCAST_IP)), 
				Mockito.eq(PORT), Mockito.eq(IPProtocol.UDP), Mockito.eq(false), Mockito.eq(DATA.getBytes()));
	}
}