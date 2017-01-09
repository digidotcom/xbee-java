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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.DataReader;
import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.OperatingMode;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBeeDevice.class, SerialPortRxTx.class, DataReader.class, Thread.class})
public class XBeeDeviceDetermineOperatingModeTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private SerialPortRxTx connectionInterface;
	private XBeeDevice xbeeDevice;
	private DataReader dataReader;
	private OperatingMode dataReaderOperatingMode;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dataReaderOperatingMode = null;
		
		// Mock the connection interface to be returned by the XBee class.
		connectionInterface = Mockito.mock(SerialPortRxTx.class);
		// Stub the 'open' method of the connectionInterface mock so when checking if the 
		// interface is open next time it returns true.
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Mockito.when(connectionInterface.isOpen()).thenReturn(true);
				return null;
			}
		}).when(connectionInterface).open();
		// Stub the 'close' method of the connectionInterface mock so when checking if the 
		// interface is open next time it returns false.
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Mockito.when(connectionInterface.isOpen()).thenReturn(false);
				return null;
			}
		}).when(connectionInterface).close();
		
		// Mock a DataReader object (used in the XBeeDevice connect process).
		dataReader = Mockito.mock(DataReader.class);
		// Stub the 'start' method of the dataReader mock so when checking if the 
		// dataReader is running next time it returns true.
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Mockito.when(dataReader.isRunning()).thenReturn(true);
				return null;
			}
		}).when(dataReader).start();
		// Stub the 'stopReader' method of the dataReader mock so when checking if the 
		// dataReader is running next time it returns false.
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Mockito.when(dataReader.isRunning()).thenReturn(false);
				return null;
			}
		}).when(dataReader).stopReader();
		// Stub the 'setXBeeReaderMode' method of the dataReader mock so we can 
		// get the configured operating mode for the reader.
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				dataReaderOperatingMode = (OperatingMode)invocation.getArguments()[0];
				return null;
			}
		}).when(dataReader).setXBeeReaderMode(Mockito.any(OperatingMode.class));
		
		// Whenever a DataReader class is instantiated, the mocked dataReader should be returned.
		PowerMockito.whenNew(DataReader.class).withAnyArguments().thenReturn(dataReader);
		
		// Instantiate an XBeeDevice object with basic parameters.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(connectionInterface));
		// Stub the readDeviceInfo method to do nothing (it has its own test file).
		Mockito.doNothing().when(xbeeDevice).readDeviceInfo();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#determineOperatingMode())}.
	 * 
	 * <p>An {@code InterfaceNotOpenException} exception must be thrown when 
	 * trying to determine the operating mode of the device.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public void testDetermineOperatingModeDeviceNotOpened() throws XBeeException {
		// Setup the resources for the test.
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Mockito.when(connectionInterface.isOpen()).thenReturn(false);
				return null;
			}
		}).when(connectionInterface).open();
		
		exception.expect(InterfaceNotOpenException.class);
		exception.expectMessage(is(equalTo("The connection interface is not open.")));
		
		// Call the method 'open' that calls the 'determineOperatingMode' 
		// that should throw an InterfaceNotOpenException.
		xbeeDevice.open();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#determineOperatingMode())}.
	 * 
	 * <p>A {@code TimeoutException} exception must be thrown when trying to 
	 * determine the operating mode of the device.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testDetermineOperatingModeTimeout() throws Exception {
		// Setup the resources for the test.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));
		PowerMockito.mockStatic(Thread.class);
		Thread.sleep(Mockito.anyLong());
		
		exception.expect(InvalidOperatingModeException.class);
		exception.expectMessage(is(equalTo("Could not determine operating mode.")));
		
		// Call the method 'open' that calls the 'determineOperatingMode' 
		// that should throw a TimeoutException.
		xbeeDevice.open();
		
		// Verify the result.
		PowerMockito.verifyStatic(Mockito.times(1));
		Thread.sleep(Mockito.anyLong());
		
		Mockito.verify(xbeeDevice).close();
		
		Mockito.verify(dataReader).setXBeeReaderMode(OperatingMode.API);
		Mockito.verify(dataReader).setXBeeReaderMode(OperatingMode.AT);
		Mockito.verify(dataReader, Mockito.times(2)).setXBeeReaderMode(Mockito.any(OperatingMode.class));
		
		assertThat("XBee operating mode must be unknown", xbeeDevice.getOperatingMode(), is(equalTo(OperatingMode.UNKNOWN)));
		assertThat("Data Reader operating mode must be unknown", dataReaderOperatingMode, is(equalTo(OperatingMode.UNKNOWN)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#determineOperatingMode())}.
	 * 
	 * <p>A {@code TimeoutException} exception must be thrown when trying to 
	 * determine the operating mode of the device.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testDetermineOperatingModeDeviceInATMode() throws Exception {
		// Setup the resources for the test.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));
		PowerMockito.doReturn(true).when(xbeeDevice, "enterATCommandMode");
		
		PowerMockito.mockStatic(Thread.class);
		Thread.sleep(Mockito.anyLong());
		
		exception.expect(InvalidOperatingModeException.class);
		exception.expectMessage(is(equalTo("Unsupported operating mode: AT mode")));
		
		// Call the method 'open' that calls the 'determineOperatingMode' 
		// that should return AT mode.
		xbeeDevice.open();
		
		// Verify the result.
		PowerMockito.verifyStatic(Mockito.times(1));
		Thread.sleep(Mockito.anyLong());
		
		Mockito.verify(xbeeDevice).close();
		
		Mockito.verify(dataReader).setXBeeReaderMode(OperatingMode.API);
		Mockito.verify(dataReader).setXBeeReaderMode(OperatingMode.AT);
		Mockito.verify(dataReader, Mockito.times(2)).setXBeeReaderMode(Mockito.any(OperatingMode.class));
		
		assertThat("XBee operating mode must be AT", xbeeDevice.getOperatingMode(), is(equalTo(OperatingMode.AT)));
		assertThat("Data Reader operating mode must be AT", dataReaderOperatingMode, is(equalTo(OperatingMode.AT)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#determineOperatingMode())}.
	 * 
	 * <p>A {@code TimeoutException} exception must be thrown when trying to 
	 * determine the operating mode of the device.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testDetermineOperatingModeDeviceInAPIMode() throws Exception {
		byte[] api1Mode = new byte[]{1};
		Mockito.doReturn(new ATCommandResponse(new ATCommand("AP"), api1Mode)).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));;
		
		// Call the method 'open' that calls the 'determineOperatingMode' 
		// that should return API 1 mode.
		xbeeDevice.open();
		
		// Verify the result.
		Mockito.verify(dataReader).setXBeeReaderMode(OperatingMode.API);
		Mockito.verify(dataReader, Mockito.times(1)).setXBeeReaderMode(Mockito.any(OperatingMode.class));
		
		assertThat("XBee operating mode must be API 1", xbeeDevice.getOperatingMode(), is(equalTo(OperatingMode.API)));
		assertThat("Data Reader operating mode must be API 1", dataReaderOperatingMode, is(equalTo(OperatingMode.API)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#determineOperatingMode())}.
	 * 
	 * <p>The device is in API 2 (Escaped) and that is the value that be configured 
	 * for the data reader.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testDetermineOperatingModeDeviceInAPI2Mode() throws Exception {
		byte[] api2Mode = new byte[]{2};
		Mockito.doReturn(new ATCommandResponse(new ATCommand("AP"), api2Mode)).when(xbeeDevice).sendATCommand(Mockito.any(ATCommand.class));;
		
		// Call the method 'open' that calls the 'determineOperatingMode' 
		// that should return API 2 mode.
		xbeeDevice.open();
		
		// Verify the result.
		Mockito.verify(dataReader).setXBeeReaderMode(OperatingMode.API);
		Mockito.verify(dataReader).setXBeeReaderMode(OperatingMode.API_ESCAPE);
		Mockito.verify(dataReader, Mockito.times(2)).setXBeeReaderMode(Mockito.any(OperatingMode.class));
		
		assertThat("XBee operating mode must be API 2", xbeeDevice.getOperatingMode(), is(equalTo(OperatingMode.API_ESCAPE)));
		assertThat("Data Reader operating mode must be API 2", dataReaderOperatingMode, is(equalTo(OperatingMode.API_ESCAPE)));
	}
}
