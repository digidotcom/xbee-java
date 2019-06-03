/**
 * Copyright 2017-2019, Digi International Inc.
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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.XBeeDeviceException;
import com.digi.xbee.api.models.XBeeProtocol;

@RunWith(PowerMockRunner.class)
@PrepareForTest({NBIoTDevice.class})
@Deprecated
@Ignore("Ignoring deprecated test.")
public class NBIoTDeviceTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private NBIoTDevice nbiotDevice;
	
	@Before
	public void setup() throws Exception {
		// Spy the NBIoTDevice class.
		SerialPortRxTx mockPort = Mockito.mock(SerialPortRxTx.class);
		nbiotDevice = PowerMockito.spy(new NBIoTDevice(mockPort));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NBIoTDevice#open()}.
	 * 
	 * <p>Verify that the {@code open()} method throws an exception when the
	 * protocol is not NB-IoT.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testNotNBIoTDevice() throws Exception {
		// Suppress the 'readDeviceInfo' method of the parent class so that it is not
		// called from the child (NBIoTDevice) class.
		PowerMockito.suppress(PowerMockito.method(CellularDevice.class, "open"));
		
		exception.expect(XBeeDeviceException.class);
		exception.expectMessage(is(equalTo("XBee device is not a Cellular NB-IoT device, it is a Unknown device.")));
		
		// Call the method that should throw the exception.
		nbiotDevice.open();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NBIoTDevice#getXBeeProtocol()}.
	 */
	@Test
	public void testGetXBeeProtocol() {
		assertEquals(nbiotDevice.getXBeeProtocol(), XBeeProtocol.CELLULAR_NBIOT);
	}
}
