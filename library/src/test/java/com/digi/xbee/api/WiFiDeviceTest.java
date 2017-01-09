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

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

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
import com.digi.xbee.api.exceptions.ATCommandException;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.IPAddressingMode;
import com.digi.xbee.api.models.WiFiAssociationIndicationStatus;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WiFiDevice.class, WiFiAssociationIndicationStatus.class})
public class WiFiDeviceTest {

	// Constants.
	private static final String PARAMETER_AI = "AI";
	private static final String PARAMETER_MA = "MA";
	private static final String PARAMETER_MY = "MY";
	private static final String PARAMETER_MK = "MK";
	private static final String PARAMETER_GW = "GW";
	private static final String PARAMETER_NS = "NS";

	private static final byte[] RESPONSE_AI = new byte[]{0x00};
	private static final byte[] RESPONSE_MA = new byte[]{(byte)IPAddressingMode.STATIC.getID()};

	private static final byte[] IP_ADDRESS = new byte[]{0x0A, 0x0B, 0x0C, 0x0D};

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private WiFiDevice wifiDevice;

	private WiFiAssociationIndicationStatus validWiFiAIStatus = WiFiAssociationIndicationStatus.SUCCESSFULLY_JOINED;

	@Before
	public void setup() throws Exception {
		// Spy the WiFiDevice class.
		SerialPortRxTx mockPort = Mockito.mock(SerialPortRxTx.class);
		wifiDevice = PowerMockito.spy(new WiFiDevice(mockPort));

		// Always return a valid Wi-Fi association indication when requested.
		PowerMockito.mockStatic(WiFiAssociationIndicationStatus.class);
		PowerMockito.when(WiFiAssociationIndicationStatus.get(Mockito.anyInt())).thenReturn(validWiFiAIStatus);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#getWiFiAssociationIndicationStatus()}.
	 *
	 * <p>Check that the Wi-Fi association indication method returns values
	 * successfully.</p>
	 *
	 * @throws Exception
	 */
	@Test
	public void testAssociationIndicationStatus() throws Exception {
		// Return a valid response when requesting the AI parameter value.
		Mockito.doReturn(RESPONSE_AI).when(wifiDevice).getParameter(PARAMETER_AI);

		assertEquals(validWiFiAIStatus, wifiDevice.getWiFiAssociationIndicationStatus());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#isConnected()}.
	 * 
	 * <p>Verify that it is not possible to determine if the device is connected if 
	 * the connection of the device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testIsConnectedErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when getting the WiFiAssociationIndicationStatus.
		Mockito.doThrow(new InterfaceNotOpenException()).when(wifiDevice).getWiFiAssociationIndicationStatus();
		
		// Check the connection.
		wifiDevice.isConnected();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#isConnected()}.
	 * 
	 * <p>Verify that it is not possible to determine if the device is connected if 
	 * the operating mode of the device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testIsConnectedErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when getting the WiFiAssociationIndicationStatus.
		Mockito.doThrow(new InvalidOperatingModeException()).when(wifiDevice).getWiFiAssociationIndicationStatus();
		
		// Check the connection.
		wifiDevice.isConnected();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#isConnected()}.
	 * 
	 * <p>Verify that it is not possible to determine if the device is connected 
	 * when there is an Timeout exception getting the WiFiAssociationIndicationStatus.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testIsConnectedErrorTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when getting the WiFiAssociationIndicationStatus.
		Mockito.doThrow(new TimeoutException()).when(wifiDevice).getWiFiAssociationIndicationStatus();
		
		// Check the connection.
		wifiDevice.isConnected();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#isConnected()}.
	 * 
	 * <p>Verify that it is not possible to determine if the device is connected 
	 * when there is an AT command exception getting the WiFiAssociationIndicationStatus.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testIsConnectedErrorInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when when getting the WiFiAssociationIndicationStatus.
		Mockito.doThrow(new ATCommandException(null)).when(wifiDevice).getWiFiAssociationIndicationStatus();
		
		// Check the connection.
		wifiDevice.isConnected();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#isConnected()}.
	 * 
	 * <p>Verify that it is possible to determine if the device is connected or not 
	 * when it is connected.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testIsConnectedSuccessConnected() throws XBeeException, IOException {
		// Return the connected value when when getting the WiFiAssociationIndicationStatus.
		Mockito.doReturn(WiFiAssociationIndicationStatus.SUCCESSFULLY_JOINED).when(wifiDevice).getWiFiAssociationIndicationStatus();
		
		// Check the connection.
		assertTrue(wifiDevice.isConnected());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#isConnected()}.
	 * 
	 * <p>Verify that it is possible to determine if the device is connected or not 
	 * when it is disconnected.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testIsConnectedSuccessDisconnected() throws XBeeException, IOException {
		// Return a valid disconnected value when when getting the WiFiAssociationIndicationStatus.
		Mockito.doReturn(WiFiAssociationIndicationStatus.WAITING_FOR_IP).when(wifiDevice).getWiFiAssociationIndicationStatus();
		
		// Check the connection.
		assertFalse(wifiDevice.isConnected());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#setAccessPointTimeout(int)}.
	 * 
	 * <p>Check that the access point timeout cannot be set if it is negative throwing an 
	 * {@code IllegalArgumentException}.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSetAccessPointTimeoutNegative() {
		// Call the method under test.
		wifiDevice.setAccessPointTimeout(-50);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#setAccessPointTimeout(int)} and 
	 * {@link com.digi.xbee.api.WiFiDevice#getAccessPointTimeout()}.
	 * 
	 * <p>Check that the access point timeout can be set and get successfully.</p>
	 */
	@Test
	public void testSetAccessPointTimeoutSuccess() {
		// First, verify that the timeout has the default value before changing it. 
		assertEquals(15000, wifiDevice.getAccessPointTimeout());
		
		// Call the method under test (change the timeout).
		wifiDevice.setAccessPointTimeout(5000);
		
		// First, verify that the new valu was set. 
		assertEquals(5000, wifiDevice.getAccessPointTimeout());
	}
	
	 /**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#setIPAddressingMode(IPAddressingMode)}.
	 *
	 * <p>Check that the method throws a {@code NullPointerException} when
	 * passing a null argument.</p>
	 *
	 * @throws Exception
	 */
	@Test
	public void testSetIPAddressingModeNull() throws Exception {
		// Set up the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("IP addressing mode cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		wifiDevice.setIPAddressingMode(null);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#setIPAddressingMode(IPAddressingMode)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testSetIPAddressingMode() throws Exception {
		// Do nothing when trying to set the MA parameter.
		Mockito.doNothing().when(wifiDevice).setParameter(Mockito.eq(PARAMETER_MA), Mockito.any(byte[].class));

		// Set the addressing mode.
		wifiDevice.setIPAddressingMode(IPAddressingMode.DHCP);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#getIPAddressingMode()}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetIPAddressingMode() throws Exception {
		// Return a valid response when requesting the MA parameter value.
		Mockito.doReturn(RESPONSE_MA).when(wifiDevice).getParameter(PARAMETER_MA);

		assertEquals(IPAddressingMode.STATIC, wifiDevice.getIPAddressingMode());
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#setIPAddress(java.net.Inet4Address)}.
	 *
	 * <p>Check that the method throws a {@code NullPointerException} when
	 * passing a null argument.</p>
	 *
	 * @throws Exception
	 */
	@Test
	public void testSetIPAddressNull() throws Exception {
		// Set up the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("IP address cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		wifiDevice.setIPAddress(null);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#setIPAddress(java.net.Inet4Address)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testSetIPAddress() throws Exception {
		// Do nothing when trying to set the MY parameter.
		Mockito.doNothing().when(wifiDevice).setParameter(Mockito.eq(PARAMETER_MY), Mockito.any(byte[].class));

		// Set the IP address.
		wifiDevice.setIPAddress((Inet4Address) Inet4Address.getByAddress(IP_ADDRESS));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#setIPAddressMask(java.net.Inet4Address)}.
	 *
	 * <p>Check that the method throws a {@code NullPointerException} when
	 * passing a null argument.</p>
	 *
	 * @throws Exception
	 */
	@Test
	public void testSetIPAddressMaskNull() throws Exception {
		// Set up the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Address mask cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		wifiDevice.setIPAddressMask(null);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#setIPAddressMask(Inet4Address)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testSetIPAddressMask() throws Exception {
		// Do nothing when trying to set the MK parameter.
		Mockito.doNothing().when(wifiDevice).setParameter(Mockito.eq(PARAMETER_MK), Mockito.any(byte[].class));

		// Set the IP address mask.
		wifiDevice.setIPAddressMask((Inet4Address) Inet4Address.getByAddress(IP_ADDRESS));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#getIPAddressMask()}.
	 *
	 * <p>Verify that the IP address mask of an XBee device cannot be gotten if
	 * the returned IP is illegal. It is, there is an XBee exception getting the
	 * MK parameter.</p>
	 *
	 * @throws XBeeException
	 */
	@Test
	public void testGetIPAddressMaskIllegalIP() throws XBeeException {
		// Return an illegal IP address when getting the MK parameter.
		Mockito.doReturn(new byte[]{0x00}).when(wifiDevice).getParameter(PARAMETER_MK);

		// Get the IP address mask of the device.
		try {
			wifiDevice.getIPAddressMask();
			fail();
		} catch (XBeeException e) {
			assertEquals("Exception is not caused by an UnknownHostException", e.getCause().getClass(), UnknownHostException.class);
		}
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#getIPAddressMask()}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetIPAddressMask() throws Exception {
		// Return a valid response when requesting the MK parameter value.
		Mockito.doReturn(IP_ADDRESS).when(wifiDevice).getParameter(PARAMETER_MK);

		assertEquals(Inet4Address.getByAddress(IP_ADDRESS), wifiDevice.getIPAddressMask());
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#setGatewayIPAddress(java.net.Inet4Address)}.
	 *
	 * <p>Check that the method throws a {@code NullPointerException} when
	 * passing a null argument.</p>
	 *
	 * @throws Exception
	 */
	@Test
	public void testSetGatewayIPAddressNull() throws Exception {
		// Set up the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Gateway address cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		wifiDevice.setGatewayIPAddress(null);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#setGatewayIPAddress(Inet4Address)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testSetGatewayIPAddress() throws Exception {
		// Do nothing when trying to set the GW parameter.
		Mockito.doNothing().when(wifiDevice).setParameter(Mockito.eq(PARAMETER_GW), Mockito.any(byte[].class));

		// Set the IP address.
		wifiDevice.setGatewayIPAddress((Inet4Address) Inet4Address.getByAddress(IP_ADDRESS));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#getGatewayIPAddress()}.
	 *
	 * <p>Verify that the gateway IP address of an XBee device cannot be gotten
	 * if the returned IP is illegal. It is, there is an XBee exception getting
	 * the GW parameter.</p>
	 *
	 * @throws XBeeException
	 */
	@Test
	public void testGetGatewayIPAddressIllegalIP() throws XBeeException {
		// Return an illegal IP address when getting the GW parameter.
		Mockito.doReturn(new byte[]{0x00}).when(wifiDevice).getParameter(PARAMETER_GW);

		// Get the gateway IP address of the device.
		try {
			wifiDevice.getGatewayIPAddress();
			fail();
		} catch (XBeeException e) {
			assertEquals("Exception is not caused by an UnknownHostException", e.getCause().getClass(), UnknownHostException.class);
		}
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#getGatewayIPAddress()}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetGatewayAddress() throws Exception {
		// Return a valid response when requesting the GW parameter value.
		Mockito.doReturn(IP_ADDRESS).when(wifiDevice).getParameter(PARAMETER_GW);

		assertEquals(Inet4Address.getByAddress(IP_ADDRESS), wifiDevice.getGatewayIPAddress());
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#setDNSAddress(java.net.Inet4Address)}.
	 *
	 * <p>Check that the method throws a {@code NullPointerException} when
	 * passing a null argument.</p>
	 *
	 * @throws Exception
	 */
	@Test
	public void testSetDNSAddressNull() throws Exception {
		// Set up the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("DNS address cannot be null.")));

		// Call the method under test that should throw a NullPointerException.
		wifiDevice.setDNSAddress(null);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#setDNSAddress(Inet4Address)}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testSetDNSAddress() throws Exception {
		// Do nothing when trying to set the NS parameter.
		Mockito.doNothing().when(wifiDevice).setParameter(Mockito.eq(PARAMETER_NS), Mockito.any(byte[].class));

		// Set the DNS address.
		wifiDevice.setDNSAddress((Inet4Address) Inet4Address.getByAddress(IP_ADDRESS));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#getDNSAddress()}.
	 *
	 * <p>Verify that the DNS address of an XBee device cannot be gotten if the
	 * returned IP is illegal. It is, there is an XBee exception getting the NS
	 * parameter.</p>
	 *
	 * @throws XBeeException
	 */
	@Test
	public void testGetDNSAddressIllegalIP() throws XBeeException {
		// Return an illegal IP address when getting the NS parameter.
		Mockito.doReturn(new byte[]{0x00}).when(wifiDevice).getParameter(PARAMETER_NS);

		// Get the DNS address of the device.
		try {
			wifiDevice.getDNSAddress();
			fail();
		} catch (XBeeException e) {
			assertEquals("Exception is not caused by an UnknownHostException", e.getCause().getClass(), UnknownHostException.class);
		}
	}

	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#getDNSAddress()}.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetDNSAddress() throws Exception {
		// Return a valid response when requesting the NS parameter value.
		Mockito.doReturn(IP_ADDRESS).when(wifiDevice).getParameter(PARAMETER_NS);

		assertEquals(Inet4Address.getByAddress(IP_ADDRESS), wifiDevice.getDNSAddress());
	}

}
