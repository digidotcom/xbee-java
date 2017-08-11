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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

import java.net.Inet6Address;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.utils.ByteUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IPv6Device.class})
public class IPv6DeviceTest {
	
	// Constants.
	private static final String PARAMETER_MY = "MY";
	private static final String PARAMETER_C0 = "C0";
	private static final byte[] RESPONSE_MY = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	private static final byte[] RESPONSE_C0 = new byte[]{0x12, 0x34};
	
	private static short EXPECTED_SOURCE_PORT = ByteUtils.byteArrayToShort(RESPONSE_C0);
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private Inet6Address ipv6Address;
	
	private IPv6Device ipv6Device;
	
	@Before
	public void setup() throws Exception {
		// Suppress the 'readDeviceInfo' method of the parent class so that it is not
		// called from the child (IPv6Device) class.
		PowerMockito.suppress(PowerMockito.method(AbstractXBeeDevice.class, "readDeviceInfo"));
		
		// Spy the IPv6Device class.
		SerialPortRxTx mockPort = Mockito.mock(SerialPortRxTx.class);
		ipv6Device = PowerMockito.spy(new IPv6Device(mockPort));
		
		// Mock an IPv6 address object.
		ipv6Address = (Inet6Address) Inet6Address.getByAddress(RESPONSE_MY);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#getIPv6Address()} and
	 * {@link com.digi.xbee.api.IPv6Device#readDeviceInfo()}.
	 * 
	 * <p>Verify that the {@code readDeviceInfo()} method of the IPv6 device generates 
	 * the IPv6 address correctly.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadDeviceInfoIP() throws Exception {
		// Return a valid response when requesting the MY parameter value.
		Mockito.doReturn(RESPONSE_MY).when(ipv6Device).getParameter(PARAMETER_MY);
		// Return a valid response when requesting the C0 parameter value.
		Mockito.doReturn(RESPONSE_C0).when(ipv6Device).getParameter(PARAMETER_C0);
		
		// Fist, check that the IPv6 is null (it has not been created yet)
		assertNull(ipv6Device.getIPv6Address());
		// Check that the source port is the default one.
		assertEquals(IPv6Device.DEFAULT_SOURCE_PORT, ipv6Device.sourcePort);
		
		// Call the readDeviceInfo method.
		ipv6Device.readDeviceInfo();
		Mockito.verify((AbstractXBeeDevice)ipv6Device, Mockito.times(1)).readDeviceInfo();
		
		// Verify that the IPv6 address was generated.
		assertEquals(ipv6Address, ipv6Device.getIPv6Address());
		// Verify that the source port is the expected one.
		assertEquals(EXPECTED_SOURCE_PORT, ipv6Device.sourcePort);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#startListening(int)}.
	 * 
	 * <p>Verify that device cannot start listening for IPv6 data if the 
	 * source port is bigger than 65535.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testStartListeningInvalidPortBig() throws TimeoutException, XBeeException {
		ipv6Device.startListening(100000);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#startListening(int)}.
	 * 
	 * <p>Verify that device cannot start listening for IPv6 data if the 
	 * source port is negative.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testStartListeningInvalidPortNegative() throws TimeoutException, XBeeException {
		ipv6Device.startListening(-10);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#startListening(int)}.
	 * 
	 * <p>Verify that device cannot start listening for IPv6 data if the 
	 * device is not open.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testStartListeningConnectionClosed() throws TimeoutException, XBeeException {
		// Throw an Interface not open exception when setting any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(ipv6Device).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		ipv6Device.startListening(123);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#startListening(int)}.
	 * 
	 * <p>Verify that device cannot start listening for IPv6 data if device 
	 * has an invalid operating mode.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testStartListeningInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Throw an Invalid operating mode exception when setting any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(ipv6Device).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		ipv6Device.startListening(123);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#startListening(int)}.
	 * 
	 * <p>Verify that device cannot start listening for IPv6 data if there 
	 * is a timeout setting the 'C0' parameter.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TimeoutException.class)
	public void testStartListeningTimeout() throws TimeoutException, XBeeException {
		// Throw an timeout exception when setting any parameter.
		Mockito.doThrow(new TimeoutException()).when(ipv6Device).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		ipv6Device.startListening(123);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#startListening(int)}.
	 * 
	 * <p>Verify that device starts listening for IPv6 data successfully.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testStartListeningSuccess() throws TimeoutException, XBeeException {
		int newSourcePort = 123;
		// Do nothing when setting any parameter.
		Mockito.doNothing().when(ipv6Device).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		ipv6Device.startListening(newSourcePort);
		
		// Verify that source port has changed to the provided one.
		assertEquals(newSourcePort, ipv6Device.sourcePort);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#stopListening()}.
	 * 
	 * <p>Verify that device cannot stop listening for IPv6 data if the 
	 * device is not open.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testStopListeningConnectionClosed() throws TimeoutException, XBeeException {
		// Throw an Interface not open exception when setting any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(ipv6Device).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		ipv6Device.stopListening();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#stopListening()}.
	 * 
	 * <p>Verify that device cannot stop listening for IPv6 data if device 
	 * has an invalid operating mode.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testStopListeningInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Throw an Invalid operating mode exception when setting any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(ipv6Device).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		ipv6Device.stopListening();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#stopListening()}.
	 * 
	 * <p>Verify that device cannot stop listening for IPv6 data if there 
	 * is a timeout setting the 'C0' parameter.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TimeoutException.class)
	public void testStoptListeningTimeout() throws TimeoutException, XBeeException {
		// Throw an timeout exception when setting any parameter.
		Mockito.doThrow(new TimeoutException()).when(ipv6Device).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		ipv6Device.stopListening();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#stopListening()}.
	 * 
	 * <p>Verify that device stops listening for IPv6 data successfully.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testStopListeningSuccess() throws TimeoutException, XBeeException {
		// Do nothing when setting any parameter.
		Mockito.doNothing().when(ipv6Device).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		ipv6Device.stopListening();
		
		// Verify that source port has changed to 0.
		assertEquals(0, ipv6Device.sourcePort);
		
		// Verify the setParameter(String, byte[]) method was called once.
		Mockito.verify(ipv6Device, Mockito.times(1)).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#getDestinationAddress()}.
	 * 
	 * <p>Verify that the not supported methods of the IPv6 device throw an
	 * {@code UnsupportedOperationException}</p>.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationGetDestinationAddress() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		ipv6Device.getDestinationAddress();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#setDestinationAddress(com.digi.xbee.api.models.XBee64BitAddress)}.
	 * 
	 * <p>Verify that the not supported methods of the IPv6 device throw an
	 * {@code UnsupportedOperationException}</p>.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSetDestinationAddress() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		ipv6Device.setDestinationAddress(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#addDataListener(com.digi.xbee.api.listeners.IDataReceiveListener)}.
	 * 
	 * <p>Verify that the not supported methods of the IPv6 device throw an
	 * {@code UnsupportedOperationException}</p>.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationAddDataListener() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		ipv6Device.addDataListener(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#removeDataListener(com.digi.xbee.api.listeners.IDataReceiveListener)}.
	 * 
	 * <p>Verify that the not supported methods of the IPv6 device throw an
	 * {@code UnsupportedOperationException}</p>.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationRemoveDataListener() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		ipv6Device.removeDataListener(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readData()}.
	 * 
	 * <p>Verify that the not supported methods of the IPv6 device throw an
	 * {@code UnsupportedOperationException}</p>.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationReadData() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		ipv6Device.readData();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readData(int)}.
	 * 
	 * <p>Verify that the not supported methods of the IPv6 device throw an
	 * {@code UnsupportedOperationException}</p>.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationReadDataTimeout() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		ipv6Device.readData(-1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readDataFrom(RemoteXBeeDevice)}.
	 * 
	 * <p>Verify that the not supported methods of the IPv6 device throw an
	 * {@code UnsupportedOperationException}</p>.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationReadDataFrom() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		ipv6Device.readDataFrom(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readDataFrom(RemoteXBeeDevice, int)}.
	 * 
	 * <p>Verify that the not supported methods of the IPv6 device throw an
	 * {@code UnsupportedOperationException}</p>.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationReadDataFromTimeout() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		ipv6Device.readDataFrom(null, -1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendBroadcastData(byte[])}.
	 * 
	 * <p>Verify that the not supported methods of the IPv6 device throw an
	 * {@code UnsupportedOperationException}</p>.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSendBroadcastData() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		ipv6Device.sendBroadcastData(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendData(RemoteXBeeDevice, byte[])}.
	 * 
	 * <p>Verify that the not supported methods of the IPv6 device throw an
	 * {@code UnsupportedOperationException}</p>.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSendData() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// All the following operations should throw an 
		// {@code UnsupportedOperationException} exception.
		ipv6Device.sendData((RemoteXBeeDevice)null, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendDataAsync(RemoteXBeeDevice, byte[])}.
	 * 
	 * <p>Verify that the not supported methods of the IPv6 device throw an
	 * {@code UnsupportedOperationException}</p>.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSendDataAsync() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		ipv6Device.sendDataAsync((RemoteXBeeDevice)null, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#get16BitAddress()} and
	 * {@link com.digi.xbee.api.IPv6Device#getNetwork()}.
	 * 
	 * <p>Verify that parameters not supported by the IPv6 device are returned 
	 * as {@code null}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedParameters() throws Exception {
		// IPv6 devices do not have 16-bit address and do not support the 
		// network feature.
		assertNull(ipv6Device.get16BitAddress());
		assertNull(ipv6Device.getNetwork());
	}
}
