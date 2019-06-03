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

import java.net.Inet4Address;

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
import com.digi.xbee.api.listeners.ISMSReceiveListener;
import com.digi.xbee.api.models.IPProtocol;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IPDevice.class})
@Deprecated
@Ignore("Ignoring deprecated test.")
public class LPWANDeviceTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private LPWANDevice lpWanDevice;
	
	@Before
	public void setup() throws Exception {
		// Spy the LPWANDevice class.
		SerialPortRxTx mockPort = Mockito.mock(SerialPortRxTx.class);
		lpWanDevice = PowerMockito.spy(new LPWANDevice(mockPort));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.LPWANDevice#addSMSListener(com.digi.xbee.api.listeners.ISMSReceiveListener)}.
	 * 
	 * <p>Verify that the not supported methods of the LPWAN device throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationAddSMSListener() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		lpWanDevice.addSMSListener(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.LPWANDevice#removeSMSListener(ISMSReceiveListener)}.
	 * 
	 * <p>Verify that the not supported methods of the LPWAN device throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationRemoveSMSListener() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		lpWanDevice.removeSMSListener(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.LPWANDevice#sendSMS(String, String)}.
	 * 
	 * <p>Verify that the not supported methods of the LPWAN device throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSendSMS() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		lpWanDevice.sendSMS(null, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.LPWANDevice#sendSMSAsync(String, String)}.
	 * 
	 * <p>Verify that the not supported methods of the LPWAN device throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSendSMSAsync() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		lpWanDevice.sendSMSAsync(null, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.LPWANDevice#sendIPData(java.net.Inet4Address, int, com.digi.xbee.api.models.IPProtocol, byte[])}.
	 * 
	 * <p>Verify that the method throws an {@code IllegalArgumentException} if
	 * the protocol is not UDP.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendIPDataNotUDP() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("This protocol only supports UDP transmissions.")));
		
		Inet4Address destAddr = (Inet4Address)Inet4Address.getByName("192.168.1.55");
		int port = 3000;
		IPProtocol protocol = IPProtocol.TCP;
		byte[] data = "Test".getBytes();
		
		// Call the method that should throw the exception.
		lpWanDevice.sendIPData(destAddr, port, protocol, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.LPWANDevice#sendIPData(java.net.Inet4Address, int, com.digi.xbee.api.models.IPProtocol, byte[])}.
	 * 
	 * <p>Verify that the method calls the super implementation.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendIPData() throws Exception {
		// Do nothing when the sendIPData of IPDevice is called.
		Mockito.doNothing().when((IPDevice)lpWanDevice).sendIPData(Mockito.any(Inet4Address.class), 
				Mockito.anyInt(), Mockito.any(IPProtocol.class), Mockito.any(byte[].class));
		
		Inet4Address destAddr = (Inet4Address)Inet4Address.getByName("192.168.1.55");
		int port = 3000;
		IPProtocol protocol = IPProtocol.UDP;
		byte[] data = "Test".getBytes();
		
		// Call the method under test.
		lpWanDevice.sendIPData(destAddr, port, protocol, data);
		
		// Verify that the super method was called.
		Mockito.verify((IPDevice)lpWanDevice, Mockito.times(1)).sendIPData(Mockito.eq(destAddr), 
				Mockito.eq(port), Mockito.eq(protocol), Mockito.eq(data));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.LPWANDevice#sendIPData(Inet4Address, int, IPProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that this method calls the non-deprecated method with the same
	 * parameters.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testSendIPDataDeprecated() throws Exception {
		// Do nothing when the sendIPData of NBIoTDevice is called.
		Mockito.doNothing().when(lpWanDevice).sendIPData(Mockito.any(Inet4Address.class), 
				Mockito.anyInt(), Mockito.any(IPProtocol.class), Mockito.any(byte[].class));
		
		Inet4Address destAddr = (Inet4Address)Inet4Address.getByName("192.168.1.55");
		int port = 3000;
		IPProtocol protocol = IPProtocol.UDP;
		byte[] data = "Test".getBytes();
		
		// Call the method that should throw the exception.
		lpWanDevice.sendIPData(destAddr, port, protocol, false, data);
		
		// Verify that the super method was called.
		Mockito.verify(lpWanDevice, Mockito.times(1)).sendIPData(Mockito.eq(destAddr), 
				Mockito.eq(port), Mockito.eq(protocol), Mockito.eq(data));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.LPWANDevice#sendIPDataAsync(java.net.Inet4Address, int, com.digi.xbee.api.models.IPProtocol, byte[])}.
	 * 
	 * <p>Verify that the method throws an {@code IllegalArgumentException} if
	 * the protocol is not UDP.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendIPDataAsyncNotUDP() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("This protocol only supports UDP transmissions.")));
		
		Inet4Address destAddr = (Inet4Address)Inet4Address.getByName("192.168.1.55");
		int port = 3000;
		IPProtocol protocol = IPProtocol.TCP;
		byte[] data = "Test".getBytes();
		
		// Call the method that should throw the exception.
		lpWanDevice.sendIPDataAsync(destAddr, port, protocol, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.LPWANDevice#sendIPDataAsync(java.net.Inet4Address, int, com.digi.xbee.api.models.IPProtocol, byte[])}.
	 * 
	 * <p>Verify that this method calls the non-deprecated method with the same
	 * parameters.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendIPDataAsync() throws Exception {
		// Do nothing when the sendIPDataAsync of IPDevice is called.
		Mockito.doNothing().when((IPDevice)lpWanDevice).sendIPDataAsync(Mockito.any(Inet4Address.class), 
				Mockito.anyInt(), Mockito.any(IPProtocol.class), Mockito.any(byte[].class));
		
		Inet4Address destAddr = (Inet4Address)Inet4Address.getByName("192.168.1.55");
		int port = 3000;
		IPProtocol protocol = IPProtocol.UDP;
		byte[] data = "Test".getBytes();
		
		// Call the method under test.
		lpWanDevice.sendIPDataAsync(destAddr, port, protocol, data);
		
		// Verify that the super method was called.
		Mockito.verify((IPDevice)lpWanDevice, Mockito.times(1)).sendIPDataAsync(Mockito.eq(destAddr), 
				Mockito.eq(port), Mockito.eq(protocol), Mockito.eq(data));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.LPWANDevice#sendIPData(Inet4Address, int, IPProtocol, boolean, byte[])}.
	 * 
	 * <p>Verify that the method calls the super implementation.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testSendIPDataAsyncDeprecated() throws Exception {
		// Do nothing when the sendIPDataAsync of NBIoTDevice is called.
		Mockito.doNothing().when(lpWanDevice).sendIPDataAsync(Mockito.any(Inet4Address.class), 
				Mockito.anyInt(), Mockito.any(IPProtocol.class), Mockito.any(byte[].class));
		
		Inet4Address destAddr = (Inet4Address)Inet4Address.getByName("192.168.1.55");
		int port = 3000;
		IPProtocol protocol = IPProtocol.UDP;
		byte[] data = "Test".getBytes();
		
		// Call the method that should throw the exception.
		lpWanDevice.sendIPDataAsync(destAddr, port, protocol, false, data);
		
		// Verify that the super method was called.
		Mockito.verify(lpWanDevice, Mockito.times(1)).sendIPDataAsync(Mockito.eq(destAddr), 
				Mockito.eq(port), Mockito.eq(protocol), Mockito.eq(data));
	}
}
