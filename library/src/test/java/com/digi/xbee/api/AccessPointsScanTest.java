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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.AccessPoint;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.WiFiEncryptionType;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.common.ATCommandPacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.common.ReceivePacket;

@PrepareForTest({WiFiDevice.class, XBee64BitAddress.class, XBee16BitAddress.class})
@RunWith(PowerMockRunner.class)
public class AccessPointsScanTest {
	
	// Constants.
	private static final String METHOD_SCAN_ACCESS_POINTS = "scanAccessPoints";
	private static final String METHOD_PARSE_DISCOVERED_ACCESS_POINT = "parseDiscoveredAccessPoint";
	private static final String METHOD_GET_SIGNAL_QUALITY = "getSignalQuality";
	private static final String METHOD_PARSE_DISCOVERED_AP = "parseDiscoveredAccessPoint";
	private static final String METHOD_SLEEP = "sleep";
		
	// Variables.
	private WiFiDevice wifiDevice;
	
	private IConnectionInterface mockedInterface;
	
	private IPacketReceiveListener packetListener;
	
	private List<XBeeAPIPacket> asAnswers = new ArrayList<XBeeAPIPacket>();
	
	private long currentMillis = 0;
	
	@Before
	public void setUp() throws Exception {
		asAnswers.clear();
		packetListener = null;
		
		mockedInterface = PowerMockito.mock(IConnectionInterface.class);
		wifiDevice = PowerMockito.spy(new WiFiDevice(mockedInterface));
		
		PowerMockito.when(wifiDevice.isOpen()).thenReturn(true);
		PowerMockito.when(wifiDevice.getOperatingMode()).thenReturn(OperatingMode.API);
		PowerMockito.when(wifiDevice.getNextFrameID()).thenReturn(1);
		PowerMockito.when(wifiDevice.getConnectionInterface()).thenReturn(mockedInterface);
		PowerMockito.when(mockedInterface.toString()).thenReturn("Mocked IConnectionInterface for NodeDiscovery test.");
		
		PowerMockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				packetListener = ((IPacketReceiveListener) invocation.getArguments()[0]);
				return null;
			}
		}).when(wifiDevice).addPacketListener(Mockito.any(IPacketReceiveListener.class));
		
		PowerMockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable {
				if (packetListener == null)
					return null;
				Thread t = new Thread() {
					@Override
					public void run() {
						if (invocation == null || invocation.getArguments() == null
								|| invocation.getArguments().length == 0)
							return;
						
						for (int i = 0; i < asAnswers.size(); i++)
							packetListener.packetReceived(asAnswers.get(i));
					}
				};
				t.start();
				
				return null;
			}
		}).when(wifiDevice).sendPacketAsync(Mockito.any(ATCommandPacket.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#scanAccessPoints()}.
	 * 
	 * <p>An {@code InterfaceNotOpenException} exception must be thrown when 
	 * the local device connection is not open.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public final void testScanAccessPointsDeviceNotOpen() throws XBeeException {
		// Setup the resources for the test.
		PowerMockito.when(wifiDevice.isOpen()).thenReturn(false);
		
		// Call the method under test.
		wifiDevice.scanAccessPoints();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#scanAccessPoints()}.
	 * 
	 * <p>An {@code InvalidOperatingModeException} exception must be thrown when 
	 * the local device operating mode is different than API or API Escaped.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public final void testScanAccessPointsInvalidOperatingMode() throws XBeeException {
		// Setup the resources for the test.
		PowerMockito.when(wifiDevice.getOperatingMode()).thenReturn(OperatingMode.AT);
		
		// Call the method under test.
		wifiDevice.scanAccessPoints();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#getAccessPoint(String)}.
	 * 
	 * <p>Verify that when asked for an access point with a {@code null} SSID, 
	 * the method throws a {@code NullPointerException}.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=NullPointerException.class)
	public final void testGetAccessPointNotNull() throws Exception {
		// Call the method under test.
		wifiDevice.getAccessPoint(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#getAccessPoint(String)}.
	 * 
	 * <p>Verify that when asked for a specific access point and it is not in the list of 
	 * discovered access points, the returned access point is null.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testGetAccessPointNotExists() throws Exception {
		// Setup the resources for the test.
		ArrayList<AccessPoint> accessPointsList = new ArrayList<AccessPoint>();
		
		// Mock a dummy access point.
		AccessPoint mockedAccessPoint = PowerMockito.mock(AccessPoint.class);
		PowerMockito.when(mockedAccessPoint.getSSID()).thenReturn("Dummy SSID");
		accessPointsList.add(mockedAccessPoint);
		
		PowerMockito.doReturn(accessPointsList).when(wifiDevice, METHOD_SCAN_ACCESS_POINTS);
		
		// Call the method under test.
		AccessPoint readAccessPoint = wifiDevice.getAccessPoint("Test SSID");
		
		// Verify the result.
		assertThat("The discovered access point should be null", readAccessPoint, is(equalTo(null)));
		
		Mockito.verify(wifiDevice, Mockito.times(1)).scanAccessPoints();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#getAccessPoint(String)}.
	 * 
	 * <p>Verify that when asked for a specific access point and it is not in the list of 
	 * discovered access points, the returned access point is null.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testGetAccessPointExists() throws Exception {
		// Setup the resources for the test.
		String testSSID = "Test SSID";
		ArrayList<AccessPoint> accessPointsList = new ArrayList<AccessPoint>();
		
		// Mock a dummy access point.
		AccessPoint mockedAccessPoint = PowerMockito.mock(AccessPoint.class);
		PowerMockito.when(mockedAccessPoint.getSSID()).thenReturn("Dummy SSID");
		accessPointsList.add(mockedAccessPoint);
		
		// Mock the access point to get when asked.
		AccessPoint mockedAccessPoint2 = PowerMockito.mock(AccessPoint.class);
		PowerMockito.when(mockedAccessPoint2.getSSID()).thenReturn(testSSID);
		accessPointsList.add(mockedAccessPoint2);
		
		PowerMockito.doReturn(accessPointsList).when(wifiDevice, METHOD_SCAN_ACCESS_POINTS);
		
		// Call the method under test.
		AccessPoint readAccessPoint = wifiDevice.getAccessPoint(testSSID);
		
		// Verify the result.
		assertThat("The discovered access point shouldn't be null", readAccessPoint, is(IsNull.notNullValue()));
		assertThat("The discovered access point's SSID should match the provided one", readAccessPoint.getSSID(), is(equalTo(testSSID)));
		
		Mockito.verify(wifiDevice, Mockito.times(1)).scanAccessPoints();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#scanAccessPoints()}.
	 * 
	 * <p>Verify that {@code scanAccessPoints()} method throws an {@code XBeeException} 
	 * if the device is already connected (AS response status is ERROR).</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=XBeeException.class)
	public final void testScanAccessPointsErrorConnected() throws Exception {
		// Setup the resources for the test.
		asAnswers.clear();
		asAnswers.add(new ATCommandResponsePacket(1, ATCommandStatus.ERROR, "AS", null));
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
		wifiDevice.scanAccessPoints();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#scanAccessPoints()}.
	 * 
	 * <p>Verify that {@code scanAccessPoints()} method throws a {@code TimeoutException} 
	 * if it does not receive the end of discovery command after the configured timeout expires.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=TimeoutException.class)
	public final void testScanAccessPointsErrorTimeout() throws Exception {
		// Setup the resources for the test.
		asAnswers.clear();
		
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
		wifiDevice.scanAccessPoints();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#scanAccessPoints()}.
	 * 
	 * <p>Verify that {@code scanAccessPoints()} method returns an empty list of 
	 * access points if only the end answer to AS command is received.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testScanAccessPointsSuccessEmpty() throws Exception {
		// Setup the resources for the test.
		asAnswers.clear();
		
		// Add the end of AS command response.
		asAnswers.add(new ATCommandResponsePacket(1, ATCommandStatus.OK, "AS", null));
		
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
		List<AccessPoint> accessPointsList = wifiDevice.scanAccessPoints();
		
		// Verify the result.
		assertThat("List of access points should be empty", accessPointsList.size(), is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#scanAccessPoints()}.
	 * 
	 * <p>Verify that {@code scanAccessPoints()} method returns an empty list of 
	 * access points if only the end answer to AS command is received. In this case the 
	 * end answer has an empty (not null) parameter value.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testScanAccessPointsSuccessEmptyValueEmpty() throws Exception {
		// Setup the resources for the test.
		asAnswers.clear();
		
		// Add the end of AS command response (this time the parameter is an empty byte array).
		asAnswers.add(new ATCommandResponsePacket(1, ATCommandStatus.OK, "AS", new byte[0]));
		
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
		List<AccessPoint> accessPointsList = wifiDevice.scanAccessPoints();
		
		// Verify the result.
		assertThat("List of access points should be empty", accessPointsList.size(), is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#scanAccessPoints()}.
	 * 
	 * <p>Verify that {@code scanAccessPoints()} method returns a list with
	 * discovered access points.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testScanAccessPointsSuccess() throws Exception {
		// Setup the resources for the test.
		asAnswers.clear();
		
		// Create 2 valid access points (to be verified later with the list of discovered ones)
		AccessPoint accessPoint1 = new AccessPoint("AP - 1", WiFiEncryptionType.NONE, 1, 60);
		AccessPoint accessPoint2 = new AccessPoint("AP - 2", WiFiEncryptionType.WEP, 2, 40);
		
		// Prepare the objects to return when the 
		PowerMockito.doReturn(accessPoint1, null, accessPoint2).when(wifiDevice, METHOD_PARSE_DISCOVERED_AP, Mockito.any(byte[].class));
		
		// Add some invalid AS response packets (they should be skipped) to the list of AS answers.
		asAnswers.add(new ReceivePacket(PowerMockito.mock(XBee64BitAddress.class), PowerMockito.mock(XBee16BitAddress.class), 0, null));
		asAnswers.add(new ATCommandResponsePacket(1, ATCommandStatus.OK, "NI", null));
		
		// Add a some valid AS answers to the list of AS answers.
		asAnswers.add(createASCmdPacket(1, ATCommandStatus.OK, 2, 1, WiFiEncryptionType.NONE, 30, "AP - 1"));
		asAnswers.add(createASCmdPacket(1, ATCommandStatus.OK, 2, 10, WiFiEncryptionType.NONE, 50, "AP - DUMMY"));
		asAnswers.add(createASCmdPacket(1, ATCommandStatus.OK, 2, 2, WiFiEncryptionType.WEP, 20, "AP - 2"));
		
		// Add the end of AS command response to the list of AS answers.
		asAnswers.add(new ATCommandResponsePacket(1, ATCommandStatus.OK, "AS", null));
		
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
		List<AccessPoint> accessPointsList = wifiDevice.scanAccessPoints();
		
		// Verify the result.
		assertThat("List of access points should contain 2 access points", accessPointsList.size(), is(equalTo(2)));
		assertThat("First access point from the list should equal 'accessPpoint1'", accessPointsList.get(0), is(equalTo(accessPoint1)));
		assertThat("Second access point from the list should equal 'accessPpoint2'", accessPointsList.get(1), is(equalTo(accessPoint2)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#parseDiscoveredAccessPoint(byte[])}.
	 * 
	 * <p>Verify that when the access point data to be parsed does not contain a Wi-Fi version, the 
	 * generated access point is {@code null}.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testParseDiscoveredAccessPointNoVersion() throws Exception {
		// Setup the resources for the test.
		byte[] data = new byte[0];
		
		// Call the method under test.
		AccessPoint parsedAccessPoint = Whitebox.invokeMethod(wifiDevice, METHOD_PARSE_DISCOVERED_ACCESS_POINT, data);
		
		// Verify the result.
		assertThat("Parsed access point should be null", parsedAccessPoint, is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#parseDiscoveredAccessPoint(byte[])}.
	 * 
	 * <p>Verify that when the access point data to be parsed does not contain channel, the 
	 * generated access point is {@code null}.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testParseDiscoveredAccessPointNoChannel() throws Exception {
		// Setup the resources for the test.
		byte[] data = new byte[1];
		data[0] = 2;                   /* version */
		
		// Call the method under test.
		AccessPoint parsedAccessPoint = Whitebox.invokeMethod(wifiDevice, METHOD_PARSE_DISCOVERED_ACCESS_POINT, data);
		
		// Verify the result.
		assertThat("Parsed access point should be null", parsedAccessPoint, is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#parseDiscoveredAccessPoint(byte[])}.
	 * 
	 * <p>Verify that when the access point data to be parsed does not contain encryption type, the 
	 * generated access point is {@code null}.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testParseDiscoveredAccessPointNoEncType() throws Exception {
		// Setup the resources for the test.
		byte[] data = new byte[2];
		data[0] = 2;                   /* version */
		data[1] = 12;                  /* channel */
		
		// Call the method under test.
		AccessPoint parsedAccessPoint = Whitebox.invokeMethod(wifiDevice, METHOD_PARSE_DISCOVERED_ACCESS_POINT, data);
		
		// Verify the result.
		assertThat("Parsed access point should be null", parsedAccessPoint, is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#parseDiscoveredAccessPoint(byte[])}.
	 * 
	 * <p>Verify that when the access point data to be parsed does not contain signal strength, the 
	 * generated access point is {@code null}.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testParseDiscoveredAccessPointNoSignal() throws Exception {
		// Setup the resources for the test.
		byte[] data = new byte[3];
		data[0] = 2;                   /* version */
		data[1] = 12;                  /* channel */
		data[2] = 0;                   /* encryption type */
		
		// Call the method under test.
		AccessPoint parsedAccessPoint = Whitebox.invokeMethod(wifiDevice, METHOD_PARSE_DISCOVERED_ACCESS_POINT, data);
		
		// Verify the result.
		assertThat("Parsed access point should be null", parsedAccessPoint, is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#parseDiscoveredAccessPoint(byte[])}.
	 * 
	 * <p>Verify that when the access point data to be parsed does not contain SSID, the 
	 * generated access point is {@code null}.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testParseDiscoveredAccessPointNoSSID() throws Exception {
		// Setup the resources for the test.
		byte[] data = new byte[4];
		data[0] = 2;                   /* version */
		data[1] = 12;                  /* channel */
		data[2] = 0;                   /* encryption type */
		data[3] = (byte)(42 & 0xFF);  /* signal strength */
		PowerMockito.doReturn(50).when(wifiDevice, METHOD_GET_SIGNAL_QUALITY, Mockito.anyInt(), Mockito.anyInt());
		
		// Call the method under test.
		AccessPoint parsedAccessPoint = Whitebox.invokeMethod(wifiDevice, METHOD_PARSE_DISCOVERED_ACCESS_POINT, data);
		
		// Verify the result.
		assertThat("Parsed access point should be null", parsedAccessPoint, is(equalTo(null)));
		PowerMockito.verifyPrivate(wifiDevice, Mockito.times(1)).invoke(METHOD_GET_SIGNAL_QUALITY, 2, 42 & 0xFF);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#parseDiscoveredAccessPoint(byte[])}.
	 * 
	 * <p>Verify that when the access point data to be parsed is valid, the access 
	 * point is generated successfully.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testParseDiscoveredAccessPointSuccess() throws Exception {
		// Setup the resources for the test.
		byte[] ssid = "AP SSID".getBytes();
		AccessPoint mockedAccessPoint = PowerMockito.mock(AccessPoint.class);
		PowerMockito.whenNew(AccessPoint.class).withAnyArguments().thenReturn(mockedAccessPoint);
		
		byte[] data = new byte[4 + ssid.length];
		data[0] = 2;                   /* version */
		data[1] = 12;                  /* channel */
		data[2] = 0;                   /* encryption type */
		data[3] = (byte)(42 & 0xFF);  /* signal strength */
		System.arraycopy(ssid, 0, data, 4, ssid.length);
		PowerMockito.doReturn(50).when(wifiDevice, METHOD_GET_SIGNAL_QUALITY, Mockito.anyInt(), Mockito.anyInt());
		
		// Call the method under test.
		AccessPoint parsedAccessPoint = Whitebox.invokeMethod(wifiDevice, METHOD_PARSE_DISCOVERED_ACCESS_POINT, data);
		
		// Verify the result.
		assertThat("Parsed access point should equal the mocked one", parsedAccessPoint, is(equalTo(mockedAccessPoint)));
		PowerMockito.verifyPrivate(wifiDevice, Mockito.times(1)).invoke(METHOD_GET_SIGNAL_QUALITY, 2, 42 & 0xFF);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#getSignalQuality(int, int)}.
	 * 
	 * <p>Verify that when Wi-Fi version is 1, the signal quality is generated correctly.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testGetSignalQualityVersion1() throws Exception {
		// Setup the resources for the test.
		int signal1 = -150;
		int signal2 = -80;
		int signal3 = -60;
		int signal4 = 40;
		
		// Call the method under test.
		int quality1 = Whitebox.invokeMethod(wifiDevice, METHOD_GET_SIGNAL_QUALITY, 1, signal1);
		int quality2 = Whitebox.invokeMethod(wifiDevice, METHOD_GET_SIGNAL_QUALITY, 1, signal2);
		int quality3 = Whitebox.invokeMethod(wifiDevice, METHOD_GET_SIGNAL_QUALITY, 1, signal3);
		int quality4 = Whitebox.invokeMethod(wifiDevice, METHOD_GET_SIGNAL_QUALITY, 1, signal4);
		
		// Verify the result.
		assertThat("quality1 should be 0", quality1, is(equalTo(0)));
		assertThat("quality2 should be 40", quality2, is(equalTo(40)));
		assertThat("quality3 should be 80", quality3, is(equalTo(80)));
		assertThat("quality4 should be 100", quality4, is(equalTo(100)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.WiFiDevice#getSignalQuality(int, int)}.
	 * 
	 * <p>Verify that when Wi-Fi version is 2, the signal quality is generated correctly.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testGetSignalQualityVersion2() throws Exception {
		// Setup the resources for the test.
		int signal1 = -20;
		int signal2 = 20;
		int signal3 = 40;
		int signal4 = 60;
		
		// Call the method under test.
		int quality1 = Whitebox.invokeMethod(wifiDevice, METHOD_GET_SIGNAL_QUALITY, 2, signal1);
		int quality2 = Whitebox.invokeMethod(wifiDevice, METHOD_GET_SIGNAL_QUALITY, 2, signal2);
		int quality3 = Whitebox.invokeMethod(wifiDevice, METHOD_GET_SIGNAL_QUALITY, 2, signal3);
		int quality4 = Whitebox.invokeMethod(wifiDevice, METHOD_GET_SIGNAL_QUALITY, 2, signal4);
		
		// Verify the result.
		assertThat("quality1 should be 0", quality1, is(equalTo(0)));
		assertThat("quality2 should be 40", quality2, is(equalTo(40)));
		assertThat("quality3 should be 80", quality3, is(equalTo(80)));
		assertThat("quality4 should be 100", quality4, is(equalTo(100)));
	}
	
	/**
	 * Helper method to create AS responses.
	 * 
	 * @param frameId Frame ID for the generated packet.
	 * @param status Status code of the packet.
	 * @param version Wi-Fi protocol version of the module.
	 * @param channel Operating channel.
	 * @param encryptionType Wi-Fi encryption type.
	 * @param signalStrength Signal strength value.
	 * @param ssid SSID name of the access point.
	 * 
	 * @return The AS AT command response packet with the access point information.
	 */
	private ATCommandResponsePacket createASCmdPacket(int frameId, ATCommandStatus status, 
			int version, int channel, WiFiEncryptionType encryptionType, int signalStrength, 
			String ssid) {
		byte[] value = new byte[1 /* version */ + 1 /* channel */ + 1 /* encryption type */ 
		                        + 1 /* signal strength */ + ssid.length()];
		
		byte[] ssidByteArray = ssid.getBytes();
		
		value[0] = (byte)(version & 0xFF);
		value[1] = (byte)(channel & 0xFF);
		value[2] = (byte)(encryptionType.getID() & 0xFF);
		value[3] = (byte)(signalStrength & 0xFF);
		System.arraycopy(ssidByteArray, 0, value, 4, ssidByteArray.length);
		
		return new ATCommandResponsePacket(frameId, status, "AS", value);
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
