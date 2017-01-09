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

import java.net.Inet4Address;

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
@PrepareForTest({IPDevice.class})
public class IPDeviceTest {
	
	// Constants.
	private static final String PARAMETER_MY = "MY";
	private static final String PARAMETER_C0 = "C0";
	private static final byte[] RESPONSE_MY = new byte[]{0x00, 0x00, 0x00, 0x00};
	private static final byte[] RESPONSE_C0 = new byte[]{0x12, 0x34};
	
	private static short EXPECTED_SOURCE_PORT = ByteUtils.byteArrayToShort(RESPONSE_C0);
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private Inet4Address ipAddress;
	
	private IPDevice ipDevice;
	
	@Before
	public void setup() throws Exception {
		// Suppress the 'readDeviceInfo' method of the parent class so that it is not
		// called from the child (IPDevice) class.
		PowerMockito.suppress(PowerMockito.method(AbstractXBeeDevice.class, "readDeviceInfo"));
		
		// Spy the IPDevice class.
		SerialPortRxTx mockPort = Mockito.mock(SerialPortRxTx.class);
		ipDevice = PowerMockito.spy(new IPDevice(mockPort));
		
		// Mock an IP address object.
		ipAddress = PowerMockito.mock(Inet4Address.class);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#getIPAddress()} and
	 * {@link com.digi.xbee.api.IPDevice#readDeviceInfo()}.
	 * 
	 * <p>Verify that the {@code readDeviceInfo()} method of the IP device generates 
	 * the IP address correctly.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadDeviceInfoIP() throws Exception {
		// Whenever an Inet4Address object is instantiated, the mocked IP address should be returned.
		PowerMockito.whenNew(Inet4Address.class).withAnyArguments().thenReturn(ipAddress);
		
		// Return a valid response when requesting the MY parameter value.
		Mockito.doReturn(RESPONSE_MY).when(ipDevice).getParameter(PARAMETER_MY);
		// Return a valid response when requesting the C0 parameter value.
		Mockito.doReturn(RESPONSE_C0).when(ipDevice).getParameter(PARAMETER_C0);
		
		// Fist, check that the IP is null (it has not been created yet)
		assertNull(ipDevice.getIPAddress());
		// Check that the source port is the default one.
		assertEquals(IPDevice.DEFAULT_SOURCE_PORT, ipDevice.sourcePort);
		
		// Call the readDeviceInfo method.
		ipDevice.readDeviceInfo();
		Mockito.verify((AbstractXBeeDevice)ipDevice, Mockito.times(1)).readDeviceInfo();
		
		// Verify that the IP address was generated.
		assertEquals(ipAddress, ipDevice.getIPAddress());
		// Verify that the source port is the expected one.
		assertEquals(EXPECTED_SOURCE_PORT, ipDevice.sourcePort);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#startListening(int)}.
	 * 
	 * <p>Verify that device cannot start listening for IP data if the 
	 * source port is bigger than 65535.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testStartListeningInvalidPortBig() throws TimeoutException, XBeeException {
		ipDevice.startListening(100000);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#startListening(int)}.
	 * 
	 * <p>Verify that device cannot start listening for IP data if the 
	 * source port is negative.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testStartListeningInvalidPortNegative() throws TimeoutException, XBeeException {
		ipDevice.startListening(-10);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#startListening(int)}.
	 * 
	 * <p>Verify that device cannot start listening for IP data if the 
	 * device is not open.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testStartListeningConnectionClosed() throws TimeoutException, XBeeException {
		// Throw an Interface not open exception when setting any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(ipDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		ipDevice.startListening(123);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#startListening(int)}.
	 * 
	 * <p>Verify that device cannot start listening for IP data if device 
	 * has an invalid operating mode.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testStartListeningInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Throw an Invalid operating mode exception when setting any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(ipDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		ipDevice.startListening(123);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#startListening(int)}.
	 * 
	 * <p>Verify that device cannot start listening for IP data if there 
	 * is a timeout setting the 'C0' parameter.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TimeoutException.class)
	public void testStartListeningTimeout() throws TimeoutException, XBeeException {
		// Throw an timeout exception when setting any parameter.
		Mockito.doThrow(new TimeoutException()).when(ipDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		ipDevice.startListening(123);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#startListening(int)}.
	 * 
	 * <p>Verify that device starts listening for IP data successfully.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testStartListeningSuccess() throws TimeoutException, XBeeException {
		int newSourcePort = 123;
		// Do nothing when setting any parameter.
		Mockito.doNothing().when(ipDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		ipDevice.startListening(newSourcePort);
		
		// Verify that source port has changed to the provided one.
		assertEquals(newSourcePort, ipDevice.sourcePort);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#stopListening()}.
	 * 
	 * <p>Verify that device cannot stop listening for IP data if the 
	 * device is not open.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testStopListeningConnectionClosed() throws TimeoutException, XBeeException {
		// Throw an Interface not open exception when setting any parameter.
		Mockito.doThrow(new InterfaceNotOpenException()).when(ipDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		ipDevice.stopListening();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#stopListening()}.
	 * 
	 * <p>Verify that device cannot stop listening for IP data if device 
	 * has an invalid operating mode.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testStopListeningInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Throw an Invalid operating mode exception when setting any parameter.
		Mockito.doThrow(new InvalidOperatingModeException()).when(ipDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		ipDevice.stopListening();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#stopListening()}.
	 * 
	 * <p>Verify that device cannot stop listening for IP data if there 
	 * is a timeout setting the 'C0' parameter.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TimeoutException.class)
	public void testStoptListeningTimeout() throws TimeoutException, XBeeException {
		// Throw an timeout exception when setting any parameter.
		Mockito.doThrow(new TimeoutException()).when(ipDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		ipDevice.stopListening();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#stopListening()}.
	 * 
	 * <p>Verify that device stops listening for IP data successfully.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testStopListeningSuccess() throws TimeoutException, XBeeException {
		// Do nothing when setting any parameter.
		Mockito.doNothing().when(ipDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		ipDevice.stopListening();
		
		// Verify that source port has changed to 0.
		assertEquals(0, ipDevice.sourcePort);
		
		// Verify the setParameter(String, byte[]) method was called once.
		Mockito.verify(ipDevice, Mockito.times(1)).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#getDestinationAddress()}.
	 * 
	 * <p>Verify that the not supported methods of the IP device throw an
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
		ipDevice.getDestinationAddress();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#setDestinationAddress(com.digi.xbee.api.models.XBee64BitAddress)}.
	 * 
	 * <p>Verify that the not supported methods of the IP device throw an
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
		ipDevice.setDestinationAddress(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#getPANID()}.
	 * 
	 * <p>Verify that the not supported methods of the IP device throw an
	 * {@code UnsupportedOperationException}</p>.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationGetPANID() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		ipDevice.getPANID();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#setPANID(byte[])}.
	 * 
	 * <p>Verify that the not supported methods of the IP device throw an
	 * {@code UnsupportedOperationException}</p>.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSetPANID() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		ipDevice.setPANID(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#addDataListener(com.digi.xbee.api.listeners.IDataReceiveListener)}.
	 * 
	 * <p>Verify that the not supported methods of the IP device throw an
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
		ipDevice.addDataListener(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#removeDataListener(com.digi.xbee.api.listeners.IDataReceiveListener)}.
	 * 
	 * <p>Verify that the not supported methods of the IP device throw an
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
		ipDevice.removeDataListener(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#addIOSampleListener(com.digi.xbee.api.listeners.IIOSampleReceiveListener)}.
	 * 
	 * <p>Verify that the not supported methods of the IP device throw an
	 * {@code UnsupportedOperationException}</p>.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationsAddIOSampleListener() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		ipDevice.addIOSampleListener(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#removeIOSampleListener(com.digi.xbee.api.listeners.IIOSampleReceiveListener)}.
	 * 
	 * <p>Verify that the not supported methods of the IP device throw an
	 * {@code UnsupportedOperationException}</p>.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationRemoveIOSampleListener() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		ipDevice.removeIOSampleListener(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readData()}.
	 * 
	 * <p>Verify that the not supported methods of the IP device throw an
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
		ipDevice.readData();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readData(int)}.
	 * 
	 * <p>Verify that the not supported methods of the IP device throw an
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
		ipDevice.readData(-1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readDataFrom(RemoteXBeeDevice)}.
	 * 
	 * <p>Verify that the not supported methods of the IP device throw an
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
		ipDevice.readDataFrom(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readDataFrom(RemoteXBeeDevice, int)}.
	 * 
	 * <p>Verify that the not supported methods of the IP device throw an
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
		ipDevice.readDataFrom(null, -1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendBroadcastData(byte[])}.
	 * 
	 * <p>Verify that the not supported methods of the IP device throw an
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
		ipDevice.sendBroadcastData(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendData(RemoteXBeeDevice, byte[])}.
	 * 
	 * <p>Verify that the not supported methods of the IP device throw an
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
		ipDevice.sendData((RemoteXBeeDevice)null, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#sendDataAsync(RemoteXBeeDevice, byte[])}.
	 * 
	 * <p>Verify that the not supported methods of the IP device throw an
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
		ipDevice.sendDataAsync((RemoteXBeeDevice)null, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#get16BitAddress()} and
	 * {@link com.digi.xbee.api.IPDevice#getNetwork()}.
	 * 
	 * <p>Verify that parameters not supported by the IP device are returned 
	 * as {@code null}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedParameters() throws Exception {
		// IP devices do not have 16-bit address and do not support the 
		// network feature.
		assertNull(ipDevice.get16BitAddress());
		assertNull(ipDevice.getNetwork());
	}
}
