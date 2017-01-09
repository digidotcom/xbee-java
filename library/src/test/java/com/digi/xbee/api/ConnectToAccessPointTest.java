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

import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.AccessPoint;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.WiFiEncryptionType;

@PrepareForTest({WiFiDevice.class, System.class})
@RunWith(PowerMockRunner.class)
public class ConnectToAccessPointTest {
	
	// Constants.
	private static final String METHOD_SLEEP = "sleep";
		
	// Variables.
	private WiFiDevice wifiDevice;
	
	private IConnectionInterface cInterfaceMock;
	
	private long currentMillis = 0;
	
	private AccessPoint mockedAccessPoint;
	
	@Before
	public void setUp() throws Exception {
		cInterfaceMock = PowerMockito.mock(IConnectionInterface.class);
		wifiDevice = PowerMockito.spy(new WiFiDevice(cInterfaceMock));
		mockedAccessPoint = PowerMockito.mock(AccessPoint.class);
		PowerMockito.doReturn("").when(mockedAccessPoint).getSSID();
		PowerMockito.doReturn(WiFiEncryptionType.NONE).when(mockedAccessPoint).getEncryptionType();
		
		PowerMockito.when(wifiDevice.isOpen()).thenReturn(true);
		PowerMockito.when(wifiDevice.getOperatingMode()).thenReturn(OperatingMode.API);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#disconnect()}.
	 * 
	 * <p>Verify that the Wi-Fi module cannot disconnect from the access point 
	 * if the device is not open throwing an {@code InterfaceNotOpenException}.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public final void testDisconnectDeviceNotOpen() throws XBeeException {
		// Setup the resources for the test.
		PowerMockito.doThrow(new InterfaceNotOpenException()).when(wifiDevice).executeParameter(Mockito.anyString());
		
		// Call the method under test.
		wifiDevice.disconnect();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#disconnect()}.
	 * 
	 * <p>Verify that the Wi-Fi module cannot disconnect from the access point 
	 * if the device is in AT mode throwing an {@code InvalidOperatingModeException}.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public final void testDisconnectInvalidOperatingMode() throws XBeeException {
		// Setup the resources for the test.
		PowerMockito.doThrow(new InvalidOperatingModeException()).when(wifiDevice).executeParameter(Mockito.anyString());
		
		// Call the method under test.
		wifiDevice.disconnect();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#disconnect()}.
	 * 
	 * <p>Verify that the Wi-Fi module cannot disconnect from the access point 
	 * if there is a timeout sending the NR command throwing a {@code TimeoutException}.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=TimeoutException.class)
	public final void testDisconnectTimeout() throws XBeeException {
		// Setup the resources for the test.
		PowerMockito.doThrow(new TimeoutException()).when(wifiDevice).executeParameter(Mockito.anyString());
		
		// Call the method under test.
		wifiDevice.disconnect();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#disconnect()}.
	 * 
	 * <p>Verify that the Wi-Fi module cannot disconnect from the access point 
	 * if the AI status is {@code null}.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDisconnectNullAIStatus() throws Exception {
		// Setup the resources for the test.
		PowerMockito.doNothing().when(wifiDevice).executeParameter("NR");
		PowerMockito.doReturn(null).when(wifiDevice).getParameter("AI");
		
		// Get the current time.
		currentMillis = System.currentTimeMillis();
		
		// Configure the number of ticks (times) the sleep method should be called.
		int ticks = wifiDevice.getAccessPointTimeout()/100;
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
		
		// When the sleep method is called, add 100ms to the currentMillis variable.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Object[] args = invocation.getArguments();
				int sleepTime = (Integer)args[0];
				changeMillisToReturn(sleepTime);
				return null;
			}
		}).when(wifiDevice, METHOD_SLEEP, Mockito.anyInt());
		
		// Call the method under test.
		boolean disconnected = wifiDevice.disconnect();
		
		// Verify the result.
		assertThat("Module should not have disconnected", disconnected, is(equalTo(false)));
		// Verify that the sleep method was called 'ticks' times.
		PowerMockito.verifyPrivate(wifiDevice, Mockito.times(ticks)).invoke(METHOD_SLEEP, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#disconnect()}.
	 * 
	 * <p>Verify that the Wi-Fi module cannot disconnect from the access point 
	 * if the AI status is empty.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDisconnectEmptyAIStatus() throws Exception {
		// Setup the resources for the test.
		PowerMockito.doNothing().when(wifiDevice).executeParameter("NR");
		PowerMockito.doReturn(new byte[0]).when(wifiDevice).getParameter("AI");
		
		// Get the current time.
		currentMillis = System.currentTimeMillis();
		
		// Configure the number of ticks (times) the sleep method should be called.
		int ticks = wifiDevice.getAccessPointTimeout()/100;
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
		
		// When the sleep method is called, add 100ms to the currentMillis variable.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Object[] args = invocation.getArguments();
				int sleepTime = (Integer)args[0];
				changeMillisToReturn(sleepTime);
				return null;
			}
		}).when(wifiDevice, METHOD_SLEEP, Mockito.anyInt());
		
		// Call the method under test.
		boolean disconnected = wifiDevice.disconnect();
		
		// Verify the result.
		assertThat("Module should not have disconnected", disconnected, is(equalTo(false)));
		// Verify that the sleep method was called 'ticks' times.
		PowerMockito.verifyPrivate(wifiDevice, Mockito.times(ticks)).invoke(METHOD_SLEEP, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#disconnect()}.
	 * 
	 * <p>Verify that the Wi-Fi module cannot disconnect if the AI status is not 
	 * 0x23.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDisconnectWrongAIStatus() throws Exception {
		// Setup the resources for the test.
		// Prepare the answers to the AT commands.
		PowerMockito.doNothing().when(wifiDevice).executeParameter("NR");
		PowerMockito.doReturn(new byte[0x13]).when(wifiDevice).getParameter("AI");
		
		// Get the current time.
		currentMillis = System.currentTimeMillis();
		
		// Configure the number of ticks (times) the sleep method should be called.
		int ticks = wifiDevice.getAccessPointTimeout()/100;
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
		
		// When the sleep method is called, add 100ms to the currentMillis variable.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Object[] args = invocation.getArguments();
				int sleepTime = (Integer)args[0];
				changeMillisToReturn(sleepTime);
				return null;
			}
		}).when(wifiDevice, METHOD_SLEEP, Mockito.anyInt());
		
		// Call the method under test.
		boolean disconnected = wifiDevice.disconnect();
		
		// Verify the result.
		assertThat("Module should have not disconnected", disconnected, is(equalTo(false)));
		// Verify that the sleep method was called 'ticks' times.
		PowerMockito.verifyPrivate(wifiDevice, Mockito.times(ticks)).invoke(METHOD_SLEEP, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#disconnect()}.
	 * 
	 * <p>Verify that the Wi-Fi module can disconnect from the access point 
	 * successfully.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDisconnectSuccess() throws Exception {
		// Setup the resources for the test.
		PowerMockito.doNothing().when(wifiDevice).executeParameter("NR");
		PowerMockito.doReturn(new byte[]{0x23}).when(wifiDevice).getParameter("AI");
		
		// Get the current time.
		currentMillis = System.currentTimeMillis();
		
		// Configure the number of ticks (times) the sleep method should be called.
		int ticks = 1;
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
		
		// When the sleep method is called, add 100ms to the currentMillis variable.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Object[] args = invocation.getArguments();
				int sleepTime = (Integer)args[0];
				changeMillisToReturn(sleepTime);
				return null;
			}
		}).when(wifiDevice, METHOD_SLEEP, Mockito.anyInt());
		
		// Call the method under test.
		boolean disconnected = wifiDevice.disconnect();
		
		// Verify the result.
		assertThat("Module should have disconnected", disconnected, is(equalTo(true)));
		// Verify that the sleep method was called 'ticks' times.
		PowerMockito.verifyPrivate(wifiDevice, Mockito.times(ticks)).invoke(METHOD_SLEEP, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#connect(AccessPoint, String)}.
	 * 
	 * <p>Verify that the Wi-Fi module cannot connect to an access point if the 
	 * access point provided is {@code null} throwing a {@code NullPointerException}.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=NullPointerException.class)
	public final void testConnectAccessPointAPNull() throws XBeeException {
		// Call the method under test.
		wifiDevice.connect((AccessPoint)null, "");
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#connect(String, String, int)}.
	 * 
	 * <p>Verify that the Wi-Fi module cannot connect to an access point if the 
	 * SSID provided is {@code null} throwing a {@code NullPointerException}.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=NullPointerException.class)
	public final void testConnectAccessPointSSIDNull() throws XBeeException {
		// Call the method under test.
		wifiDevice.connect((String)null, "");
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#connect(String, String)}.
	 * 
	 * <p>Verify that the Wi-Fi module cannot connect to an access point if 
	 * there is not any access point with the provided SSID throwing an 
	 * {@code XBeeException}.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=XBeeException.class)
	public final void testConnectAccessPointSSIDNotFound() throws XBeeException {
		// Setup the resources for the test.
		String ssid = "AP SSID";
		
		// Return a null access point when asked for one.
		PowerMockito.doReturn(null).when(wifiDevice).getAccessPoint(ssid);
		
		// Call the method under test.
		wifiDevice.connect(ssid, "");
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#connect(String, String)}.
	 * 
	 * <p>Verify that the Wi-Fi module can connect to to an access point successfully 
	 * providing just the SSID of the access point.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testConnectSuccessSSID() throws XBeeException {
		// Setup the resources for the test.
		String ssid = "AP SSID";
		String password = "password";
		
		// Return the mocked access point when asked for one.
		PowerMockito.doReturn(mockedAccessPoint).when(wifiDevice).getAccessPoint(ssid);
		
		// Return true when asked to connect to an access point.
		PowerMockito.doReturn(true).when(wifiDevice).connect(Mockito.eq(mockedAccessPoint), Mockito.anyString());
		
		
		// Call the method under test.
		boolean connected = wifiDevice.connect(ssid, password);
		
		// Verify the result.
		assertThat("Module should have connected", connected, is(equalTo(true)));
		// Verify that the connect method was called one time.
		Mockito.verify(wifiDevice, Mockito.times(1)).connect(Mockito.eq(mockedAccessPoint), Mockito.eq(password));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#connect(AccessPoint, String)}.
	 * 
	 * <p>Verify that the Wi-Fi module cannot connect to an access point if 
	 * device is not open throwing an {@code InterfaceNotOpenException}.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public final void testConnectDeviceNotOpen() throws XBeeException {
		// Setup the resources for the test.
		// Prepare the answers to the AT commands.
		PowerMockito.doThrow(new InterfaceNotOpenException()).when(wifiDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		// Call the method under test.
		wifiDevice.connect(mockedAccessPoint, "");
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#connect(AccessPoint, String)}.
	 * 
	 * <p>Verify that the Wi-Fi module cannot connect to an access point if the 
	 * device is in AT mode throwing an {@code InvalidOperatingModeException}.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public final void testConnectInvalidOperatingMode() throws XBeeException {
		// Setup the resources for the test.
		// Prepare the answers to the AT commands.
		PowerMockito.doThrow(new InvalidOperatingModeException()).when(wifiDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		// Call the method under test.
		wifiDevice.connect(mockedAccessPoint, "");
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#connect(AccessPoint, String)}.
	 * 
	 * <p>Verify that the Wi-Fi module cannot connect to an access point if there 
	 * is a timeout setting any connection parameter throwing a {@code TimeoutException}.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=TimeoutException.class)
	public final void testConnectTimeout() throws XBeeException {
		// Setup the resources for the test.
		// Prepare the answers to the AT commands.
		PowerMockito.doThrow(new TimeoutException()).when(wifiDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		
		// Call the method under test.
		wifiDevice.connect(mockedAccessPoint, "");
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#connect(AccessPoint, String)}.
	 * 
	 * <p>Verify that the Wi-Fi module cannot connect to an access point if the AI 
	 * status is {@code null} and the password is not configured.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testConnectNullAIStatusNullPassword() throws Exception {
		// Setup the resources for the test.
		// Prepare the answers to the AT commands.
		PowerMockito.doNothing().when(wifiDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		PowerMockito.doReturn(null).when(wifiDevice).getParameter("AI");
		
		// Get the current time.
		currentMillis = System.currentTimeMillis();
		
		// Configure the number of ticks (times) the sleep method should be called.
		int ticks = wifiDevice.getAccessPointTimeout()/100;
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
		
		// When the sleep method is called, add 100ms to the currentMillis variable.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Object[] args = invocation.getArguments();
				int sleepTime = (Integer)args[0];
				changeMillisToReturn(sleepTime);
				return null;
			}
		}).when(wifiDevice, METHOD_SLEEP, Mockito.anyInt());
		
		// Call the method under test.
		boolean connected = wifiDevice.connect(mockedAccessPoint, null);
		
		// Verify the result.
		assertThat("Module should have not connected", connected, is(equalTo(false)));
		// Verify that the sleep method was called 'ticks' times.
		PowerMockito.verifyPrivate(wifiDevice, Mockito.times(ticks)).invoke(METHOD_SLEEP, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#connect(AccessPoint, String)}.
	 * 
	 * <p>Verify that the Wi-Fi module cannot connect to an access point if the AI 
	 * status is empty and the password is not configured.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testConnectEmptyAIStatus() throws Exception {
		// Setup the resources for the test.
		// Prepare the answers to the AT commands.
		PowerMockito.doNothing().when(wifiDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		PowerMockito.doReturn(new byte[0]).when(wifiDevice).getParameter("AI");
		
		// Get the current time.
		currentMillis = System.currentTimeMillis();
		
		// Configure the number of ticks (times) the sleep method should be called.
		int ticks = wifiDevice.getAccessPointTimeout()/100;
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
		
		// When the sleep method is called, add 100ms to the currentMillis variable.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Object[] args = invocation.getArguments();
				int sleepTime = (Integer)args[0];
				changeMillisToReturn(sleepTime);
				return null;
			}
		}).when(wifiDevice, METHOD_SLEEP, Mockito.anyInt());
		
		// Call the method under test.
		boolean connected = wifiDevice.connect(mockedAccessPoint, null);
		
		// Verify the result.
		assertThat("Module should have not connected", connected, is(equalTo(false)));
		// Verify that the sleep method was called 'ticks' times.
		PowerMockito.verifyPrivate(wifiDevice, Mockito.times(ticks)).invoke(METHOD_SLEEP, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#connect(AccessPoint, String)}.
	 * 
	 * <p>Verify that the Wi-Fi module cannot connect to an access point if the AI 
	 * status is not 0.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testConnectWrongAIStatus() throws Exception {
		// Setup the resources for the test.
		// Prepare the answers to the AT commands.
		PowerMockito.doNothing().when(wifiDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		PowerMockito.doReturn(new byte[]{0x23}).when(wifiDevice).getParameter("AI");
		
		Mockito.doReturn(WiFiEncryptionType.NONE).when(mockedAccessPoint).getEncryptionType();
		
		// Get the current time.
		currentMillis = System.currentTimeMillis();
		
		// Configure the number of ticks (times) the sleep method should be called.
		int ticks = wifiDevice.getAccessPointTimeout()/100;
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
		
		// When the sleep method is called, add 100ms to the currentMillis variable.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Object[] args = invocation.getArguments();
				int sleepTime = (Integer)args[0];
				changeMillisToReturn(sleepTime);
				return null;
			}
		}).when(wifiDevice, METHOD_SLEEP, Mockito.anyInt());
		
		// Call the method under test.
		boolean connected = wifiDevice.connect(mockedAccessPoint, "password");
		
		// Verify the result.
		assertThat("Module should have not connected", connected, is(equalTo(false)));
		// Verify that the sleep method was called 'ticks' times.
		PowerMockito.verifyPrivate(wifiDevice, Mockito.times(ticks)).invoke(METHOD_SLEEP, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#connect(AccessPoint, String)}.
	 * 
	 * <p>Verify that the Wi-Fi module can connect to to an access point successfully when 
	 * the password is null.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testConnectSuccessPasswordNull() throws Exception {
		// Setup the resources for the test.
		// Prepare the answers to the AT commands.
		PowerMockito.doNothing().when(wifiDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		PowerMockito.doReturn(new byte[]{0}).when(wifiDevice).getParameter("AI");
		
		// Get the current time.
		currentMillis = System.currentTimeMillis();
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
		
		// When the sleep method is called, add 100ms to the currentMillis variable.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Object[] args = invocation.getArguments();
				int sleepTime = (Integer)args[0];
				changeMillisToReturn(sleepTime);
				return null;
			}
		}).when(wifiDevice, METHOD_SLEEP, Mockito.anyInt());
		
		// Call the method under test.
		boolean connected = wifiDevice.connect(mockedAccessPoint, null);
		
		// Verify the result.
		assertThat("Module should have connected", connected, is(equalTo(true)));
		// Verify that the sleep method was called one time.
		PowerMockito.verifyPrivate(wifiDevice, Mockito.times(1)).invoke(METHOD_SLEEP, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#connect(AccessPoint, String)}.
	 * 
	 * <p>Verify that the Wi-Fi module can connect to to an access point successfully when 
	 * the password is not null.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testConnectSuccessPasswordNotNull() throws Exception {
		// Setup the resources for the test.
		// Prepare the answers to the AT commands.
		PowerMockito.doNothing().when(wifiDevice).setParameter(Mockito.anyString(), Mockito.any(byte[].class));
		PowerMockito.doReturn(new byte[]{0x00}).when(wifiDevice).getParameter("AI");
		
		Mockito.doReturn(WiFiEncryptionType.WEP).when(mockedAccessPoint).getEncryptionType();
		
		// Get the current time.
		currentMillis = System.currentTimeMillis();
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
		
		// When the sleep method is called, add 100ms to the currentMillis variable.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Object[] args = invocation.getArguments();
				int sleepTime = (Integer)args[0];
				changeMillisToReturn(sleepTime);
				return null;
			}
		}).when(wifiDevice, METHOD_SLEEP, Mockito.anyInt());
		
		// Call the method under test.
		boolean connected = wifiDevice.connect(mockedAccessPoint, "password");
		
		// Verify the result.
		assertThat("Module should have connected", connected, is(equalTo(true)));
		// Verify that the sleep method was called one time.
		PowerMockito.verifyPrivate(wifiDevice, Mockito.times(1)).invoke(METHOD_SLEEP, 100);
	}
	
	/**
	 * Helper method that changes the milliseconds to return when the System.currentMillis() 
	 * method is invoked.
	 * 
	 * @param time The time to all to the milliseconds to return.
	 */
	public void changeMillisToReturn(int time) {
		currentMillis += time;
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
	}
}
