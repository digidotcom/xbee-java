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
package com.digi.xbee.api.connection;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

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
import org.powermock.reflect.Whitebox;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.InterfaceInUseException;
import com.digi.xbee.api.exceptions.InvalidConfigurationException;
import com.digi.xbee.api.exceptions.InvalidInterfaceException;
import com.digi.xbee.api.exceptions.InvalidPacketException;
import com.digi.xbee.api.exceptions.PermissionDeniedException;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.listeners.IExplicitDataReceiveListener;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import com.digi.xbee.api.listeners.IModemStatusReceiveListener;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.models.ExplicitXBeeMessage;
import com.digi.xbee.api.models.ModemStatusEvent;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.SpecialByte;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.models.XBeePacketsQueue;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.XBeePacketParser;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataReader.class})
public class DataReaderTest {
	
	private XBeePacket NOT_SPECIFIC_PACKET;
	private XBeePacket AT_CMD_RESPONSE;
	private XBeePacket RMT_AT_CMD_RESPONSE;
	private XBeePacket RX_64_PACKET;
	private XBeePacket RX_16_PACKET;
	private XBeePacket RX_PACKET;
	private XBeePacket RX_IO_64_PACKET;
	private XBeePacket RX_IO_16_PACKET;
	private XBeePacket RX_IO_PACKET;
	private XBeePacket MODEM_STATUS_PACKET;
	private XBeePacket EXPLICIT_INDICATOR_DIGI_PACKET;
	private XBeePacket EXPLICIT_INDICATOR_PACKET;
	
	private XBeePacket PACKET_TO_BE_RECEIVED;
	
	private XBeeDevice mockDevice;
	private XBeeNetwork mockNetwork;
	private InputStream mockInput;
	
	private XBeePacketParser mockParser;
	private XBeePacketsQueue mockQueue;
	
	private TestConnectionInterface testCI;
	
	private ScheduledThreadPoolExecutor mockExecutorService;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	class TestConnectionInterface implements IConnectionInterface {

		boolean isOpen = true;
		int counter = 4; // Because to read a packet the 
		                 // 'connectionInterface.getInputStream()' is called 
		                 // 3 times inside the 'while (running)' loop of the 
		                 // DataReader 'run' method + 1.
		boolean transmissionFinished = false;
		
		@Override
		public void open() throws InterfaceInUseException,
				InvalidInterfaceException, InvalidConfigurationException,
				PermissionDeniedException {
			isOpen = true;
		}

		@Override
		public void close() {
			isOpen = false;
		}

		@Override
		public boolean isOpen() {
			return isOpen;
		}

		@Override
		public InputStream getInputStream() {
			if (counter > 0) {
				counter--;
				return mockInput;
			}
			return null;
		}

		@Override
		public OutputStream getOutputStream() {
			// Do nothing.
			return null;
		}

		@Override
		public void writeData(byte[] data) throws IOException {
			// Do nothing.
		}

		@Override
		public void writeData(byte[] data, int offset, int length)
				throws IOException {
			// Do nothing.
		}

		@Override
		public int readData(byte[] data) throws IOException {
			// Do nothing.
			return 0;
		}

		@Override
		public int readData(byte[] data, int offset, int length)
				throws IOException {
			// Do nothing.
			return 0;
		}
		
		@Override
		public String toString() {
			return "[Test Connection Interface] ";
		}
		
		public void notifyData() {
			synchronized (this) {
				this.notify();
			}
		}
		
		public void setAlreadyRead() {
			counter = 0;
		}
		
		public boolean isAlreadyRead() {
			return counter > 0;
		}
	}
	
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
		XBeePacketParser parser = new XBeePacketParser();
		NOT_SPECIFIC_PACKET = parser.parsePacket(new byte[]{0x7E, 0x00, 0x04, 0x08, 0x01, 0x4E, 0x49, 0x5F}, OperatingMode.API);
		AT_CMD_RESPONSE = parser.parsePacket(new byte[]{0x7E, 0x00, 0x08, 0x08, 0x01, 0x4E, 0x49, 0x4E, 0x41, 0x4D, 0x45, 0x3E}, OperatingMode.API);
		RMT_AT_CMD_RESPONSE = parser.parsePacket(new byte[]{0x7E, 0x00, 0x13, (byte)0x97, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte)0xFF, (byte)0xFE, 0x4E, 0x49, 0x00, 0x4E, 0x41, 0x4D, 0x45, (byte)0xB2}, OperatingMode.API);
		RX_64_PACKET = parser.parsePacket(new byte[]{0x7E, 0x00, 0x0F, (byte)0x80, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, 0x00, 0x01, 0x64, 0x61, 0x74, 0x61, (byte)0xEC}, OperatingMode.API);
		RX_16_PACKET = parser.parsePacket(new byte[]{0x7E, 0x00, 0x09, (byte)0x81, (byte)0xFF, (byte)0xFE, 0x00, 0x01, 0x64, 0x61, 0x74, 0x61, (byte)0xE6}, OperatingMode.API);
		RX_PACKET = parser.parsePacket(new byte[]{0x7E, 0x00, 0x10, (byte)0x90, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFE, 0x01, 0x64, 0x61, 0x74, 0x61, (byte)0xDF}, OperatingMode.API);
		RX_IO_64_PACKET = parser.parsePacket(new byte[]{0x7E, 0x00, 0x10, (byte)0x82, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, 0x4B, 0x40, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF}, OperatingMode.API);
		RX_IO_16_PACKET = parser.parsePacket(new byte[]{0x7E, 0x00, 0x0A, (byte)0x83, (byte)0xFF, (byte)0xFE, 0x4B, 0x40, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xF9}, OperatingMode.API);
		RX_IO_PACKET = parser.parsePacket(new byte[]{0x7E, 0x00, 0x12, (byte)0x92, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFE, 0x40, 0x01, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, 0x3C}, OperatingMode.API);
		MODEM_STATUS_PACKET = parser.parsePacket(new byte[]{0x7E, 0x00, 0x02, (byte)0x8A, 0x11, 0x64}, OperatingMode.API);
		EXPLICIT_INDICATOR_DIGI_PACKET = parser.parsePacket(new byte[]{0x7E, 0x00, 0x16, (byte)0x91, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFE, (byte)0xE8, (byte)0xE8, 0x00, 0x11, (byte)0xC1, 0x05, 0x01, 0x44, 0x41, 0x54, 0x41, (byte)0xB7}, OperatingMode.API);
		EXPLICIT_INDICATOR_PACKET = parser.parsePacket(new byte[]{0x7E, 0x00, 0x16, (byte)0x91, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFE, (byte)0xC5, (byte)0xB7, 0x00, 0x55, 0x00, 0x01, 0x01, 0x44, 0x41, 0x54, 0x41, (byte)0x8C}, OperatingMode.API);
		
		PACKET_TO_BE_RECEIVED = NOT_SPECIFIC_PACKET;
		
		mockNetwork = Mockito.mock(XBeeNetwork.class);
		Mockito.when(mockNetwork.getDevice(Mockito.any(XBee64BitAddress.class))).thenReturn(null);
		Mockito.when(mockNetwork.getDevice(Mockito.any(XBee16BitAddress.class))).thenReturn(null);
		
		mockDevice = Mockito.mock(XBeeDevice.class);
		Mockito.when(mockDevice.getNetwork()).thenReturn(mockNetwork);
		Mockito.when(mockDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		
		PowerMockito.whenNew(RemoteXBeeDevice.class).withAnyArguments().thenReturn(Mockito.mock(RemoteXBeeDevice.class));
		
		mockInput = Mockito.mock(InputStream.class);
		Mockito.when(mockInput.available()).thenReturn(10);
		Mockito.when(mockInput.read()).thenReturn(SpecialByte.HEADER_BYTE.getValue());
		
		testCI = new TestConnectionInterface();
		
		mockParser = Mockito.mock(XBeePacketParser.class);
		PowerMockito.whenNew(XBeePacketParser.class).withNoArguments().thenReturn(mockParser);
		Mockito.doAnswer(new Answer<XBeePacket>() {
			@Override
			public XBeePacket answer(InvocationOnMock invocation) throws Throwable {
				return PACKET_TO_BE_RECEIVED;
			}
		}).when(mockParser).parsePacket(Mockito.eq(mockInput), Mockito.any(OperatingMode.class));
		
		mockQueue = Mockito.mock(XBeePacketsQueue.class);
		PowerMockito.whenNew(XBeePacketsQueue.class).withNoArguments().thenReturn(mockQueue);
		
		mockExecutorService = Mockito.mock(ScheduledThreadPoolExecutor.class);
		PowerMockito.mockStatic(Executors.class);
		Mockito.when(Executors.newScheduledThreadPool(Mockito.anyInt())).thenReturn(mockExecutorService);
		// Executors.newScheduledThreadPool(Math.min(MAXIMUM_PARALLEL_LISTENER_THREADS, dataReceiveListeners.size()));
		
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				((Runnable) invocation.getArguments()[0]).run();
				testCI.transmissionFinished = true;
				return null;
			}
		}).when(mockExecutorService).execute(Mockito.any(Runnable.class));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	private void waitForInitialization(long threadID) throws InterruptedException {
		ThreadMXBean mbean = ManagementFactory.getThreadMXBean();
		boolean isWaiting = true;
		
		while (isWaiting) {
			ThreadInfo threadInfo = mbean.getThreadInfo(threadID);
			if (threadInfo == null) {
				Thread.sleep(50);
				continue;
			}
			
			if (threadInfo.getThreadState() == State.WAITING)
				isWaiting = false;
			else
				Thread.sleep(50);
		}
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#DataReader(IConnectionInterface, com.digi.xbee.api.models.OperatingMode, com.digi.xbee.api.XBeeDevice)}.
	 */
	@Test
	public final void testCreateDataReaderNullConnectionInterface() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Connection interface cannot be null.")));
		
		// Call the method under test.
		new DataReader(null, OperatingMode.API, mockDevice);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#DataReader(IConnectionInterface, com.digi.xbee.api.models.OperatingMode, com.digi.xbee.api.XBeeDevice)}.
	 */
	@Test
	public final void testCreateDataReaderNullOperatingMode() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Operating mode cannot be null.")));
		
		// Call the method under test.
		new DataReader(testCI, null, mockDevice);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#DataReader(IConnectionInterface, com.digi.xbee.api.models.OperatingMode, com.digi.xbee.api.XBeeDevice)}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testCreateDataReader() throws Exception {
		// Setup the resources for the test.
		
		// Call the method under test.
		new DataReader(testCI, OperatingMode.API, mockDevice);
		
		// Verify the result.
		PowerMockito.verifyNew(XBeePacketParser.class).withNoArguments();
		PowerMockito.verifyNew(XBeePacketsQueue.class).withNoArguments();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#setXBeeReaderMode(OperatingMode)}. 
	 */
	@Test
	public final void testSetXbeeReaderModeNullMode() {
		// Setup the resources for the test.
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Operating mode cannot be null.")));
		
		// Call the method under test.
		reader.setXBeeReaderMode(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#setXBeeReaderMode(OperatingMode)}. 
	 */
	@Test
	public final void testSetXbeeReaderMode() {
		// Setup the resources for the test.
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		// Call the method under test.
		reader.setXBeeReaderMode(OperatingMode.AT);
		
		// Verify the result.
		OperatingMode mode = Whitebox.getInternalState(reader, "mode");
		assertThat(mode, is(equalTo(OperatingMode.AT)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addDataReceiveListener(com.digi.xbee.api.listeners.IDataReceiveListener)}. 
	 */
	@Test
	public final void testAddDataReceiveListenerNullListener() {
		// Setup the resources for the test.
		IDataReceiveListener l = null;
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Listener cannot be null.")));
		
		// Call the method under test.
		reader.addDataReceiveListener(l);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addDataReceiveListener(com.digi.xbee.api.listeners.IDataReceiveListener)}. 
	 */
	@Test
	public final void testAddDataReceiveListener() {
		// Setup the resources for the test.
		IDataReceiveListener l = Mockito.mock(IDataReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		// Call the method under test.
		reader.addDataReceiveListener(l);
		
		// Verify the result.
		ArrayList<IDataReceiveListener> list = Whitebox.getInternalState(reader, "dataReceiveListeners");
		assertThat(list.size(), is(equalTo(1)));
		assertThat(list.contains(l), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addDataReceiveListener(com.digi.xbee.api.listeners.IDataReceiveListener)}. 
	 */
	@Test
	public final void testAddDataReceiveListenerExistingListener() {
		// Setup the resources for the test.
		IDataReceiveListener l = Mockito.mock(IDataReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		reader.addDataReceiveListener(l);
		
		ArrayList<IDataReceiveListener> list = Whitebox.getInternalState(reader, "dataReceiveListeners");
		assertThat(list.size(), is(equalTo(1)));
		assertThat(list.contains(l), is(equalTo(true)));
		
		// Call the method under test.
		reader.addDataReceiveListener(l);
		
		// Verify the result.
		list = Whitebox.getInternalState(reader, "dataReceiveListeners");
		assertThat(list.size(), is(equalTo(1)));
		assertThat(list.contains(l), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#removeDataReceiveListener(com.digi.xbee.api.listeners.IDataReceiveListener)}. 
	 */
	@Test
	public final void testRemoveDataReceiveListenerNonAdded() {
		// Setup the resources for the test.
		IDataReceiveListener l = Mockito.mock(IDataReceiveListener.class);
		IDataReceiveListener l1 = Mockito.mock(IDataReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		reader.addDataReceiveListener(l);
		
		// Call the method under test.
		reader.removeDataReceiveListener(l1);
		
		// Verify the result.
		ArrayList<IDataReceiveListener> list = Whitebox.getInternalState(reader, "dataReceiveListeners");
		assertThat(list.size(), is(equalTo(1)));
		assertThat(list.contains(l), is(equalTo(true)));
		assertThat(list.contains(l1), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#removeDataReceiveListener(com.digi.xbee.api.listeners.IDataReceiveListener)}. 
	 */
	@Test
	public final void testRemoveDataReceiveListener() {
		// Setup the resources for the test.
		IDataReceiveListener l = Mockito.mock(IDataReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		reader.addDataReceiveListener(l);
		
		// Call the method under test.
		reader.removeDataReceiveListener(l);
		
		// Verify the result.
		ArrayList<IDataReceiveListener> list = Whitebox.getInternalState(reader, "dataReceiveListeners");
		assertThat(list.size(), is(equalTo(0)));
		assertThat(list.contains(l), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addPacketReceiveListener(com.digi.xbee.api.listeners.IPacketReceiveListener)}. 
	 */
	@Test
	public final void testAddPacketReceiveListenerNullListener() {
		// Setup the resources for the test.
		IPacketReceiveListener l = null;
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Listener cannot be null.")));
		
		// Call the method under test.
		reader.addPacketReceiveListener(l);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addPacketReceiveListener(com.digi.xbee.api.listeners.IPacketReceiveListener)}. 
	 */
	@Test
	public final void testAddPacketReceiveListener() {
		// Setup the resources for the test.
		IPacketReceiveListener l = Mockito.mock(IPacketReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		// Call the method under test.
		reader.addPacketReceiveListener(l);
		
		// Verify the result.
		HashMap<IPacketReceiveListener, Integer> map = Whitebox.getInternalState(reader, "packetReceiveListeners");
		assertThat(map.size(), is(equalTo(1)));
		assertThat(map.containsKey(l), is(equalTo(true)));
		assertThat(map.get(l), is(equalTo(99999)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addPacketReceiveListener(com.digi.xbee.api.listeners.IPacketReceiveListener)}. 
	 */
	@Test
	public final void testAddPacketReceiveListenerExistingListener() {
		// Setup the resources for the test.
		IPacketReceiveListener l = Mockito.mock(IPacketReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		reader.addPacketReceiveListener(l);
		
		HashMap<IPacketReceiveListener, Integer> map = Whitebox.getInternalState(reader, "packetReceiveListeners");
		assertThat(map.size(), is(equalTo(1)));
		assertThat(map.containsKey(l), is(equalTo(true)));
		
		// Call the method under test.
		reader.addPacketReceiveListener(l);
		
		// Verify the result.
		map = Whitebox.getInternalState(reader, "packetReceiveListeners");
		assertThat(map.size(), is(equalTo(1)));
		assertThat(map.containsKey(l), is(equalTo(true)));
		assertThat(map.get(l), is(equalTo(99999)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addPacketReceiveListener(IPacketReceiveListener, int))}. 
	 */
	@Test
	public final void testAddPacketReceiveListenerFrameIDNullListener() {
		// Setup the resources for the test.
		IPacketReceiveListener l = null;
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Listener cannot be null.")));
		
		// Call the method under test.
		reader.addPacketReceiveListener(l, 1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addPacketReceiveListener(IPacketReceiveListener, int))}. 
	 */
	@Test
	public final void testAddPacketReceiveListenerFrameID() {
		// Setup the resources for the test.
		IPacketReceiveListener l = Mockito.mock(IPacketReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		int frameID = 1;
		
		// Call the method under test.
		reader.addPacketReceiveListener(l, frameID);
		
		// Verify the result.
		HashMap<IPacketReceiveListener, Integer> map = Whitebox.getInternalState(reader, "packetReceiveListeners");
		assertThat(map.size(), is(equalTo(1)));
		assertThat(map.containsKey(l), is(equalTo(true)));
		assertThat(map.get(l), is(equalTo(frameID)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addPacketReceiveListener(IPacketReceiveListener, int))}. 
	 */
	@Test
	public final void testAddPacketReceiveListenerFrameIDExistingListener() {
		// Setup the resources for the test.
		IPacketReceiveListener l = Mockito.mock(IPacketReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		int frameID = 1;
		
		reader.addPacketReceiveListener(l, frameID);
		
		HashMap<IPacketReceiveListener, Integer> map = Whitebox.getInternalState(reader, "packetReceiveListeners");
		assertThat(map.size(), is(equalTo(1)));
		assertThat(map.containsKey(l), is(equalTo(true)));
		
		// Call the method under test.
		reader.addPacketReceiveListener(l, frameID);
		
		// Verify the result.
		map = Whitebox.getInternalState(reader, "packetReceiveListeners");
		assertThat(map.size(), is(equalTo(1)));
		assertThat(map.containsKey(l), is(equalTo(true)));
		assertThat(map.get(l), is(equalTo(frameID)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addPacketReceiveListener(IPacketReceiveListener, int))}. 
	 */
	@Test
	public final void testAddPacketReceiveListenerFrameIDExistingListenerDifferentID() {
		// Setup the resources for the test.
		IPacketReceiveListener l = Mockito.mock(IPacketReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		int frameID = 1;
		
		reader.addPacketReceiveListener(l, frameID);
		
		HashMap<IPacketReceiveListener, Integer> map = Whitebox.getInternalState(reader, "packetReceiveListeners");
		assertThat(map.size(), is(equalTo(1)));
		assertThat(map.containsKey(l), is(equalTo(true)));
		
		// Call the method under test.
		reader.addPacketReceiveListener(l, 5);
		
		// Verify the result.
		map = Whitebox.getInternalState(reader, "packetReceiveListeners");
		assertThat(map.size(), is(equalTo(1)));
		assertThat(map.containsKey(l), is(equalTo(true)));
		assertThat(map.get(l), is(equalTo(frameID)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#removePacketReceiveListener(com.digi.xbee.api.listeners.IPacketReceiveListener)}. 
	 */
	@Test
	public final void testRemovePacketReceiveListenerNonAdded() {
		// Setup the resources for the test.
		IPacketReceiveListener l = Mockito.mock(IPacketReceiveListener.class);
		IPacketReceiveListener l1 = Mockito.mock(IPacketReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		reader.addPacketReceiveListener(l);
		
		// Call the method under test.
		reader.removePacketReceiveListener(l1);
		
		// Verify the result.
		HashMap<IPacketReceiveListener, Integer> map = Whitebox.getInternalState(reader, "packetReceiveListeners");
		assertThat(map.size(), is(equalTo(1)));
		assertThat(map.containsKey(l), is(equalTo(true)));
		assertThat(map.get(l), is(equalTo(99999)));
		assertThat(map.containsKey(l1), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#removeDataReceiveListener(com.digi.xbee.api.listeners.IPacketReceiveListener)}. 
	 */
	@Test
	public final void testRemovePacketReceiveListener() {
		// Setup the resources for the test.
		IPacketReceiveListener l = Mockito.mock(IPacketReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		reader.addPacketReceiveListener(l);
		
		// Call the method under test.
		reader.removePacketReceiveListener(l);
		
		// Verify the result.
		HashMap<IPacketReceiveListener, Integer> map = Whitebox.getInternalState(reader, "packetReceiveListeners");
		assertThat(map.size(), is(equalTo(0)));
		assertThat(map.containsKey(l), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addIOSampleReceiveListener(com.digi.xbee.api.listeners.IIOSampleReceiveListener)}. 
	 */
	@Test
	public final void testAddIOSampleReceiveListenerNullListener() {
		// Setup the resources for the test.
		IIOSampleReceiveListener l = null;
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Listener cannot be null.")));
		
		// Call the method under test.
		reader.addIOSampleReceiveListener(l);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addIOSampleReceiveListener(com.digi.xbee.api.listeners.IIOSampleReceiveListener)}. 
	 */
	@Test
	public final void testAddIOSampleReceiveListener() {
		// Setup the resources for the test.
		IIOSampleReceiveListener l = Mockito.mock(IIOSampleReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		// Call the method under test.
		reader.addIOSampleReceiveListener(l);
		
		// Verify the result.
		ArrayList<IIOSampleReceiveListener> list = Whitebox.getInternalState(reader, "ioSampleReceiveListeners");
		assertThat(list.size(), is(equalTo(1)));
		assertThat(list.contains(l), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addIOSampleReceiveListener(com.digi.xbee.api.listeners.IIOSampleReceiveListener)}. 
	 */
	@Test
	public final void testAddIOSampleReceiveListenerExistingListener() {
		// Setup the resources for the test.
		IIOSampleReceiveListener l = Mockito.mock(IIOSampleReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		reader.addIOSampleReceiveListener(l);
		
		ArrayList<IIOSampleReceiveListener> list = Whitebox.getInternalState(reader, "ioSampleReceiveListeners");
		assertThat(list.size(), is(equalTo(1)));
		assertThat(list.contains(l), is(equalTo(true)));
		
		// Call the method under test.
		reader.addIOSampleReceiveListener(l);
		
		// Verify the result.
		list = Whitebox.getInternalState(reader, "ioSampleReceiveListeners");
		assertThat(list.size(), is(equalTo(1)));
		assertThat(list.contains(l), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#removeIOSampleReceiveListener(com.digi.xbee.api.listeners.IIOSampleReceiveListener)}. 
	 */
	@Test
	public final void testRemoveIOSampleReceiveListenerNonAdded() {
		// Setup the resources for the test.
		IIOSampleReceiveListener l = Mockito.mock(IIOSampleReceiveListener.class);
		IIOSampleReceiveListener l1 = Mockito.mock(IIOSampleReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		reader.addIOSampleReceiveListener(l);
		
		// Call the method under test.
		reader.removeIOSampleReceiveListener(l1);
		
		// Verify the result.
		ArrayList<IIOSampleReceiveListener> list = Whitebox.getInternalState(reader, "ioSampleReceiveListeners");
		assertThat(list.size(), is(equalTo(1)));
		assertThat(list.contains(l), is(equalTo(true)));
		assertThat(list.contains(l1), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#removeIOSampleReceiveListener(com.digi.xbee.api.listeners.IIOSampleReceiveListener)}. 
	 */
	@Test
	public final void testRemoveIOSampleReceiveListener() {
		// Setup the resources for the test.
		IIOSampleReceiveListener l = Mockito.mock(IIOSampleReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		reader.addIOSampleReceiveListener(l);
		
		// Call the method under test.
		reader.removeIOSampleReceiveListener(l);
		
		// Verify the result.
		ArrayList<IIOSampleReceiveListener> list = Whitebox.getInternalState(reader, "ioSampleReceiveListeners");
		assertThat(list.size(), is(equalTo(0)));
		assertThat(list.contains(l), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addModemStatusReceiveListener(com.digi.xbee.api.listeners.IModemStatusReceiveListener)}. 
	 */
	@Test
	public final void testAddModemStatusReceiveListenerNullListener() {
		// Setup the resources for the test.
		IModemStatusReceiveListener l = null;
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Listener cannot be null.")));
		
		// Call the method under test.
		reader.addModemStatusReceiveListener(l);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addModemStatusReceiveListener(com.digi.xbee.api.listeners.IModemStatusReceiveListener)}. 
	 */
	@Test
	public final void testAddModemStatusReceiveListener() {
		// Setup the resources for the test.
		IModemStatusReceiveListener l = Mockito.mock(IModemStatusReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		// Call the method under test.
		reader.addModemStatusReceiveListener(l);
		
		// Verify the result.
		ArrayList<IModemStatusReceiveListener> list = Whitebox.getInternalState(reader, "modemStatusListeners");
		assertThat(list.size(), is(equalTo(1)));
		assertThat(list.contains(l), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addModemStatusReceiveListener(com.digi.xbee.api.listeners.IModemStatusReceiveListener)}. 
	 */
	@Test
	public final void testAddModemStatuseReceiveListenerExistingListener() {
		// Setup the resources for the test.
		IModemStatusReceiveListener l = Mockito.mock(IModemStatusReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		reader.addModemStatusReceiveListener(l);
		
		ArrayList<IModemStatusReceiveListener> list = Whitebox.getInternalState(reader, "modemStatusListeners");
		assertThat(list.size(), is(equalTo(1)));
		assertThat(list.contains(l), is(equalTo(true)));
		
		// Call the method under test.
		reader.addModemStatusReceiveListener(l);
		
		// Verify the result.
		list = Whitebox.getInternalState(reader, "modemStatusListeners");
		assertThat(list.size(), is(equalTo(1)));
		assertThat(list.contains(l), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#removeModemStatusReceiveListener(com.digi.xbee.api.listeners.IModemStatusReceiveListener)}. 
	 */
	@Test
	public final void testRemoveModemStatusReceiveListenerNonAdded() {
		// Setup the resources for the test.
		IModemStatusReceiveListener l = Mockito.mock(IModemStatusReceiveListener.class);
		IModemStatusReceiveListener l1 = Mockito.mock(IModemStatusReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		reader.addModemStatusReceiveListener(l);
		
		// Call the method under test.
		reader.removeModemStatusReceiveListener(l1);
		
		// Verify the result.
		ArrayList<IModemStatusReceiveListener> list = Whitebox.getInternalState(reader, "modemStatusListeners");
		assertThat(list.size(), is(equalTo(1)));
		assertThat(list.contains(l), is(equalTo(true)));
		assertThat(list.contains(l1), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#removeModemStatusReceiveListener(com.digi.xbee.api.listeners.IModemStatusReceiveListener)}. 
	 */
	@Test
	public final void testRemoveModemStatusReceiveListener() {
		// Setup the resources for the test.
		IModemStatusReceiveListener l = Mockito.mock(IModemStatusReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		reader.addModemStatusReceiveListener(l);
		
		// Call the method under test.
		reader.removeModemStatusReceiveListener(l);
		
		// Verify the result.
		ArrayList<IModemStatusReceiveListener> list = Whitebox.getInternalState(reader, "modemStatusListeners");
		assertThat(list.size(), is(equalTo(0)));
		assertThat(list.contains(l), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addExplicitDataReceiveListener(com.digi.xbee.api.listeners.IExplicitDataReceiveListener)}. 
	 */
	@Test
	public final void testAddExplicitDataReceiveListenerNullListener() {
		// Setup the resources for the test.
		IExplicitDataReceiveListener l = null;
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Listener cannot be null.")));
		
		// Call the method under test.
		reader.addExplicitDataReceiveListener(l);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addExplicitDataReceiveListener(com.digi.xbee.api.listeners.IExplicitDataReceiveListener)}. 
	 */
	@Test
	public final void testAddExplicitDataReceiveListener() {
		// Setup the resources for the test.
		IExplicitDataReceiveListener l = Mockito.mock(IExplicitDataReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		// Call the method under test.
		reader.addExplicitDataReceiveListener(l);
		
		// Verify the result.
		ArrayList<IExplicitDataReceiveListener> list = Whitebox.getInternalState(reader, "explicitDataReceiveListeners");
		assertThat(list.size(), is(equalTo(1)));
		assertThat(list.contains(l), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#addExplicitDataReceiveListener(com.digi.xbee.api.listeners.IExplicitDataReceiveListener)}. 
	 */
	@Test
	public final void testAddExplicitDataeReceiveListenerExistingListener() {
		// Setup the resources for the test.
		IExplicitDataReceiveListener l = Mockito.mock(IExplicitDataReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		reader.addExplicitDataReceiveListener(l);
		
		ArrayList<IExplicitDataReceiveListener> list = Whitebox.getInternalState(reader, "explicitDataReceiveListeners");
		assertThat(list.size(), is(equalTo(1)));
		assertThat(list.contains(l), is(equalTo(true)));
		
		// Call the method under test.
		reader.addExplicitDataReceiveListener(l);
		
		// Verify the result.
		list = Whitebox.getInternalState(reader, "explicitDataReceiveListeners");
		assertThat(list.size(), is(equalTo(1)));
		assertThat(list.contains(l), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#removeExplicitDataReceiveListener(com.digi.xbee.api.listeners.IExplicitDataReceiveListener)}. 
	 */
	@Test
	public final void testRemoveExplicitDataReceiveListenerNonAdded() {
		// Setup the resources for the test.
		IExplicitDataReceiveListener l = Mockito.mock(IExplicitDataReceiveListener.class);
		IExplicitDataReceiveListener l1 = Mockito.mock(IExplicitDataReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		reader.addExplicitDataReceiveListener(l);
		
		// Call the method under test.
		reader.removeExplicitDataReceiveListener(l1);
		
		// Verify the result.
		ArrayList<IExplicitDataReceiveListener> list = Whitebox.getInternalState(reader, "explicitDataReceiveListeners");
		assertThat(list.size(), is(equalTo(1)));
		assertThat(list.contains(l), is(equalTo(true)));
		assertThat(list.contains(l1), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#removeExplicitDataReceiveListener(com.digi.xbee.api.listeners.IExplicitDataReceiveListener)}. 
	 */
	@Test
	public final void testRemoveExplicitDataReceiveListener() {
		// Setup the resources for the test.
		IExplicitDataReceiveListener l = Mockito.mock(IExplicitDataReceiveListener.class);
		DataReader reader = new DataReader(testCI, OperatingMode.API, mockDevice);
		
		reader.addExplicitDataReceiveListener(l);
		
		// Call the method under test.
		reader.removeExplicitDataReceiveListener(l);
		
		// Verify the result.
		ArrayList<IExplicitDataReceiveListener> list = Whitebox.getInternalState(reader, "explicitDataReceiveListeners");
		assertThat(list.size(), is(equalTo(0)));
		assertThat(list.contains(l), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketBadHeaderByte() throws Exception {
		// Setup the resources for the test.
		Mockito.doAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				if (testCI.isAlreadyRead())
					return null;
				
				testCI.setAlreadyRead();
				return 0x88;
			}
		}).when(mockInput).read();
		
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		PACKET_TO_BE_RECEIVED = NOT_SPECIFIC_PACKET;
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning())
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockParser, Mockito.times(0)).parsePacket(Mockito.any(InputStream.class), Mockito.any(OperatingMode.class));
		Mockito.verify(mockQueue, Mockito.times(0)).addPacket(PACKET_TO_BE_RECEIVED);
		Mockito.verify(packetListener, Mockito.times(0)).packetReceived(PACKET_TO_BE_RECEIVED);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketBadPacket() throws Exception {
		// Setup the resources for the test.
		Mockito.doAnswer(new Answer<XBeePacket>() {
			@Override
			public XBeePacket answer(InvocationOnMock invocation) throws Throwable {
				if (testCI.isAlreadyRead())
					return null;
				
				testCI.setAlreadyRead();
				throw new InvalidPacketException();
			}
		}).when(mockParser).parsePacket(Mockito.eq(mockInput), Mockito.any(OperatingMode.class));
		
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning())
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockParser, Mockito.times(1)).parsePacket(Mockito.any(InputStream.class), Mockito.any(OperatingMode.class));
		Mockito.verify(mockQueue, Mockito.times(0)).addPacket(PACKET_TO_BE_RECEIVED);
		Mockito.verify(packetListener, Mockito.times(0)).packetReceived(PACKET_TO_BE_RECEIVED);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketNotContemplatedPacket() throws Exception {
		// Setup the resources for the test.
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		IDataReceiveListener dataListener = Mockito.mock(IDataReceiveListener.class);
		dataReader.addDataReceiveListener(dataListener);
		
		IIOSampleReceiveListener ioListener = Mockito.mock(IIOSampleReceiveListener.class);
		dataReader.addIOSampleReceiveListener(ioListener);
		
		IModemStatusReceiveListener modemListener = Mockito.mock(IModemStatusReceiveListener.class);
		dataReader.addModemStatusReceiveListener(modemListener);
		
		IExplicitDataReceiveListener explicitListener = Mockito.mock(IExplicitDataReceiveListener.class);
		dataReader.addExplicitDataReceiveListener(explicitListener);
		
		PACKET_TO_BE_RECEIVED = NOT_SPECIFIC_PACKET;
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning())
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockQueue, Mockito.times(1)).addPacket(PACKET_TO_BE_RECEIVED);
		Mockito.verify(packetListener, Mockito.times(1)).packetReceived(PACKET_TO_BE_RECEIVED);
		
		Mockito.verify(dataListener, Mockito.times(0)).dataReceived(Mockito.any(XBeeMessage.class));
		Mockito.verify(ioListener, Mockito.times(0)).ioSampleReceived(Mockito.any(RemoteXBeeDevice.class), Mockito.any(IOSample.class));
		Mockito.verify(modemListener, Mockito.times(0)).modemStatusEventReceived(Mockito.any(ModemStatusEvent.class));
		Mockito.verify(explicitListener, Mockito.times(0)).explicitDataReceived(Mockito.any(ExplicitXBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketListentoFrameIDNotReceived() throws Exception {
		// Setup the resources for the test.
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener, 5);
		
		IDataReceiveListener dataListener = Mockito.mock(IDataReceiveListener.class);
		dataReader.addDataReceiveListener(dataListener);
		
		IIOSampleReceiveListener ioListener = Mockito.mock(IIOSampleReceiveListener.class);
		dataReader.addIOSampleReceiveListener(ioListener);
		
		IModemStatusReceiveListener modemListener = Mockito.mock(IModemStatusReceiveListener.class);
		dataReader.addModemStatusReceiveListener(modemListener);
		
		IExplicitDataReceiveListener explicitListener = Mockito.mock(IExplicitDataReceiveListener.class);
		dataReader.addExplicitDataReceiveListener(explicitListener);
		
		PACKET_TO_BE_RECEIVED = NOT_SPECIFIC_PACKET;
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning())
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockQueue, Mockito.times(1)).addPacket(PACKET_TO_BE_RECEIVED);
		Mockito.verify(packetListener, Mockito.times(0)).packetReceived(PACKET_TO_BE_RECEIVED);
		
		Mockito.verify(dataListener, Mockito.times(0)).dataReceived(Mockito.any(XBeeMessage.class));
		Mockito.verify(ioListener, Mockito.times(0)).ioSampleReceived(Mockito.any(RemoteXBeeDevice.class), Mockito.any(IOSample.class));
		Mockito.verify(modemListener, Mockito.times(0)).modemStatusEventReceived(Mockito.any(ModemStatusEvent.class));
		Mockito.verify(explicitListener, Mockito.times(0)).explicitDataReceived(Mockito.any(ExplicitXBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketListentoFrameIDReceived() throws Exception {
		// Setup the resources for the test.
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener, 1);
		
		IDataReceiveListener dataListener = Mockito.mock(IDataReceiveListener.class);
		dataReader.addDataReceiveListener(dataListener);
		
		IIOSampleReceiveListener ioListener = Mockito.mock(IIOSampleReceiveListener.class);
		dataReader.addIOSampleReceiveListener(ioListener);
		
		IModemStatusReceiveListener modemListener = Mockito.mock(IModemStatusReceiveListener.class);
		dataReader.addModemStatusReceiveListener(modemListener);
		
		IExplicitDataReceiveListener explicitListener = Mockito.mock(IExplicitDataReceiveListener.class);
		dataReader.addExplicitDataReceiveListener(explicitListener);
		
		PACKET_TO_BE_RECEIVED = NOT_SPECIFIC_PACKET;
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning())
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockQueue, Mockito.times(1)).addPacket(PACKET_TO_BE_RECEIVED);
		Mockito.verify(packetListener, Mockito.times(1)).packetReceived(PACKET_TO_BE_RECEIVED);
		
		Mockito.verify(dataListener, Mockito.times(0)).dataReceived(Mockito.any(XBeeMessage.class));
		Mockito.verify(ioListener, Mockito.times(0)).ioSampleReceived(Mockito.any(RemoteXBeeDevice.class), Mockito.any(IOSample.class));
		Mockito.verify(modemListener, Mockito.times(0)).modemStatusEventReceived(Mockito.any(ModemStatusEvent.class));
		Mockito.verify(explicitListener, Mockito.times(0)).explicitDataReceived(Mockito.any(ExplicitXBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketATResponse() throws Exception {
		// Setup the resources for the test.
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		IDataReceiveListener dataListener = Mockito.mock(IDataReceiveListener.class);
		dataReader.addDataReceiveListener(dataListener);
		
		IIOSampleReceiveListener ioListener = Mockito.mock(IIOSampleReceiveListener.class);
		dataReader.addIOSampleReceiveListener(ioListener);
		
		IModemStatusReceiveListener modemListener = Mockito.mock(IModemStatusReceiveListener.class);
		dataReader.addModemStatusReceiveListener(modemListener);
		
		IExplicitDataReceiveListener explicitListener = Mockito.mock(IExplicitDataReceiveListener.class);
		dataReader.addExplicitDataReceiveListener(explicitListener);
		
		PACKET_TO_BE_RECEIVED = AT_CMD_RESPONSE;
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning())
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockQueue, Mockito.times(1)).addPacket(PACKET_TO_BE_RECEIVED);
		Mockito.verify(packetListener, Mockito.times(1)).packetReceived(PACKET_TO_BE_RECEIVED);
		
		Mockito.verify(dataListener, Mockito.times(0)).dataReceived(Mockito.any(XBeeMessage.class));
		Mockito.verify(ioListener, Mockito.times(0)).ioSampleReceived(Mockito.any(RemoteXBeeDevice.class), Mockito.any(IOSample.class));
		Mockito.verify(modemListener, Mockito.times(0)).modemStatusEventReceived(Mockito.any(ModemStatusEvent.class));
		Mockito.verify(explicitListener, Mockito.times(0)).explicitDataReceived(Mockito.any(ExplicitXBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketRemoteATResponse() throws Exception {
		// Setup the resources for the test.
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		IDataReceiveListener dataListener = Mockito.mock(IDataReceiveListener.class);
		dataReader.addDataReceiveListener(dataListener);
		
		IIOSampleReceiveListener ioListener = Mockito.mock(IIOSampleReceiveListener.class);
		dataReader.addIOSampleReceiveListener(ioListener);
		
		IModemStatusReceiveListener modemListener = Mockito.mock(IModemStatusReceiveListener.class);
		dataReader.addModemStatusReceiveListener(modemListener);
		
		IExplicitDataReceiveListener explicitListener = Mockito.mock(IExplicitDataReceiveListener.class);
		dataReader.addExplicitDataReceiveListener(explicitListener);
		
		PACKET_TO_BE_RECEIVED = RMT_AT_CMD_RESPONSE;
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning())
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockQueue, Mockito.times(1)).addPacket(PACKET_TO_BE_RECEIVED);
		Mockito.verify(packetListener, Mockito.times(1)).packetReceived(PACKET_TO_BE_RECEIVED);
		
		Mockito.verify(dataListener, Mockito.times(0)).dataReceived(Mockito.any(XBeeMessage.class));
		Mockito.verify(ioListener, Mockito.times(0)).ioSampleReceived(Mockito.any(RemoteXBeeDevice.class), Mockito.any(IOSample.class));
		Mockito.verify(modemListener, Mockito.times(0)).modemStatusEventReceived(Mockito.any(ModemStatusEvent.class));
		Mockito.verify(explicitListener, Mockito.times(0)).explicitDataReceived(Mockito.any(ExplicitXBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketRX64DataPacket() throws Exception {
		// Setup the resources for the test.
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		IDataReceiveListener dataListener = Mockito.mock(IDataReceiveListener.class);
		dataReader.addDataReceiveListener(dataListener);
		
		IIOSampleReceiveListener ioListener = Mockito.mock(IIOSampleReceiveListener.class);
		dataReader.addIOSampleReceiveListener(ioListener);
		
		IModemStatusReceiveListener modemListener = Mockito.mock(IModemStatusReceiveListener.class);
		dataReader.addModemStatusReceiveListener(modemListener);
		
		IExplicitDataReceiveListener explicitListener = Mockito.mock(IExplicitDataReceiveListener.class);
		dataReader.addExplicitDataReceiveListener(explicitListener);
		
		PACKET_TO_BE_RECEIVED = RX_64_PACKET;
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning() || !testCI.transmissionFinished)
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockQueue, Mockito.times(1)).addPacket(PACKET_TO_BE_RECEIVED);
		Mockito.verify(packetListener, Mockito.times(1)).packetReceived(PACKET_TO_BE_RECEIVED);
		
		Mockito.verify(dataListener, Mockito.times(1)).dataReceived(Mockito.any(XBeeMessage.class));
		Mockito.verify(ioListener, Mockito.times(0)).ioSampleReceived(Mockito.any(RemoteXBeeDevice.class), Mockito.any(IOSample.class));
		Mockito.verify(modemListener, Mockito.times(0)).modemStatusEventReceived(Mockito.any(ModemStatusEvent.class));
		Mockito.verify(explicitListener, Mockito.times(0)).explicitDataReceived(Mockito.any(ExplicitXBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketRX16DataPacket() throws Exception {
		// Setup the resources for the test.
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		IDataReceiveListener dataListener = Mockito.mock(IDataReceiveListener.class);
		dataReader.addDataReceiveListener(dataListener);
		
		IIOSampleReceiveListener ioListener = Mockito.mock(IIOSampleReceiveListener.class);
		dataReader.addIOSampleReceiveListener(ioListener);
		
		IModemStatusReceiveListener modemListener = Mockito.mock(IModemStatusReceiveListener.class);
		dataReader.addModemStatusReceiveListener(modemListener);
		
		IExplicitDataReceiveListener explicitListener = Mockito.mock(IExplicitDataReceiveListener.class);
		dataReader.addExplicitDataReceiveListener(explicitListener);
		
		PACKET_TO_BE_RECEIVED = RX_16_PACKET;
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning() || !testCI.transmissionFinished)
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockQueue, Mockito.times(1)).addPacket(PACKET_TO_BE_RECEIVED);
		Mockito.verify(packetListener, Mockito.times(1)).packetReceived(PACKET_TO_BE_RECEIVED);
		
		Mockito.verify(dataListener, Mockito.times(1)).dataReceived(Mockito.any(XBeeMessage.class));
		Mockito.verify(ioListener, Mockito.times(0)).ioSampleReceived(Mockito.any(RemoteXBeeDevice.class), Mockito.any(IOSample.class));
		Mockito.verify(modemListener, Mockito.times(0)).modemStatusEventReceived(Mockito.any(ModemStatusEvent.class));
		Mockito.verify(explicitListener, Mockito.times(0)).explicitDataReceived(Mockito.any(ExplicitXBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketRXDataPacket() throws Exception {
		// Setup the resources for the test.
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		IDataReceiveListener dataListener = Mockito.mock(IDataReceiveListener.class);
		dataReader.addDataReceiveListener(dataListener);
		
		IIOSampleReceiveListener ioListener = Mockito.mock(IIOSampleReceiveListener.class);
		dataReader.addIOSampleReceiveListener(ioListener);
		
		IModemStatusReceiveListener modemListener = Mockito.mock(IModemStatusReceiveListener.class);
		dataReader.addModemStatusReceiveListener(modemListener);
		
		IExplicitDataReceiveListener explicitListener = Mockito.mock(IExplicitDataReceiveListener.class);
		dataReader.addExplicitDataReceiveListener(explicitListener);
		
		PACKET_TO_BE_RECEIVED = RX_PACKET;
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning() || !testCI.transmissionFinished)
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockQueue, Mockito.times(1)).addPacket(PACKET_TO_BE_RECEIVED);
		Mockito.verify(packetListener, Mockito.times(1)).packetReceived(PACKET_TO_BE_RECEIVED);
		
		Mockito.verify(dataListener, Mockito.times(1)).dataReceived(Mockito.any(XBeeMessage.class));
		Mockito.verify(ioListener, Mockito.times(0)).ioSampleReceived(Mockito.any(RemoteXBeeDevice.class), Mockito.any(IOSample.class));
		Mockito.verify(modemListener, Mockito.times(0)).modemStatusEventReceived(Mockito.any(ModemStatusEvent.class));
		Mockito.verify(explicitListener, Mockito.times(0)).explicitDataReceived(Mockito.any(ExplicitXBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketRXIO64DataPacket() throws Exception {
		// Setup the resources for the test.
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		IDataReceiveListener dataListener = Mockito.mock(IDataReceiveListener.class);
		dataReader.addDataReceiveListener(dataListener);
		
		IIOSampleReceiveListener ioListener = Mockito.mock(IIOSampleReceiveListener.class);
		dataReader.addIOSampleReceiveListener(ioListener);
		
		IModemStatusReceiveListener modemListener = Mockito.mock(IModemStatusReceiveListener.class);
		dataReader.addModemStatusReceiveListener(modemListener);
		
		IExplicitDataReceiveListener explicitListener = Mockito.mock(IExplicitDataReceiveListener.class);
		dataReader.addExplicitDataReceiveListener(explicitListener);
		
		PACKET_TO_BE_RECEIVED = RX_IO_64_PACKET;
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning() || !testCI.transmissionFinished)
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockQueue, Mockito.times(1)).addPacket(PACKET_TO_BE_RECEIVED);
		Mockito.verify(packetListener, Mockito.times(1)).packetReceived(PACKET_TO_BE_RECEIVED);
		
		Mockito.verify(dataListener, Mockito.times(0)).dataReceived(Mockito.any(XBeeMessage.class));
		Mockito.verify(ioListener, Mockito.times(1)).ioSampleReceived(Mockito.any(RemoteXBeeDevice.class), Mockito.any(IOSample.class));
		Mockito.verify(modemListener, Mockito.times(0)).modemStatusEventReceived(Mockito.any(ModemStatusEvent.class));
		Mockito.verify(explicitListener, Mockito.times(0)).explicitDataReceived(Mockito.any(ExplicitXBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketRXIO16DataPacket() throws Exception {
		// Setup the resources for the test.
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		IDataReceiveListener dataListener = Mockito.mock(IDataReceiveListener.class);
		dataReader.addDataReceiveListener(dataListener);
		
		IIOSampleReceiveListener ioListener = Mockito.mock(IIOSampleReceiveListener.class);
		dataReader.addIOSampleReceiveListener(ioListener);
		
		IModemStatusReceiveListener modemListener = Mockito.mock(IModemStatusReceiveListener.class);
		dataReader.addModemStatusReceiveListener(modemListener);
		
		IExplicitDataReceiveListener explicitListener = Mockito.mock(IExplicitDataReceiveListener.class);
		dataReader.addExplicitDataReceiveListener(explicitListener);
		
		PACKET_TO_BE_RECEIVED = RX_IO_16_PACKET;
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning() || !testCI.transmissionFinished)
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockQueue, Mockito.times(1)).addPacket(PACKET_TO_BE_RECEIVED);
		Mockito.verify(packetListener, Mockito.times(1)).packetReceived(PACKET_TO_BE_RECEIVED);
		
		Mockito.verify(dataListener, Mockito.times(0)).dataReceived(Mockito.any(XBeeMessage.class));
		Mockito.verify(ioListener, Mockito.times(1)).ioSampleReceived(Mockito.any(RemoteXBeeDevice.class), Mockito.any(IOSample.class));
		Mockito.verify(modemListener, Mockito.times(0)).modemStatusEventReceived(Mockito.any(ModemStatusEvent.class));
		Mockito.verify(explicitListener, Mockito.times(0)).explicitDataReceived(Mockito.any(ExplicitXBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketRXIOPacket() throws Exception {
		// Setup the resources for the test.
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		IDataReceiveListener dataListener = Mockito.mock(IDataReceiveListener.class);
		dataReader.addDataReceiveListener(dataListener);
		
		IIOSampleReceiveListener ioListener = Mockito.mock(IIOSampleReceiveListener.class);
		dataReader.addIOSampleReceiveListener(ioListener);
		
		IModemStatusReceiveListener modemListener = Mockito.mock(IModemStatusReceiveListener.class);
		dataReader.addModemStatusReceiveListener(modemListener);
		
		IExplicitDataReceiveListener explicitListener = Mockito.mock(IExplicitDataReceiveListener.class);
		dataReader.addExplicitDataReceiveListener(explicitListener);
		
		PACKET_TO_BE_RECEIVED = RX_IO_PACKET;
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning() || !testCI.transmissionFinished)
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockQueue, Mockito.times(1)).addPacket(PACKET_TO_BE_RECEIVED);
		Mockito.verify(packetListener, Mockito.times(1)).packetReceived(PACKET_TO_BE_RECEIVED);
		
		Mockito.verify(dataListener, Mockito.times(0)).dataReceived(Mockito.any(XBeeMessage.class));
		Mockito.verify(ioListener, Mockito.times(1)).ioSampleReceived(Mockito.any(RemoteXBeeDevice.class), Mockito.any(IOSample.class));
		Mockito.verify(modemListener, Mockito.times(0)).modemStatusEventReceived(Mockito.any(ModemStatusEvent.class));
		Mockito.verify(explicitListener, Mockito.times(0)).explicitDataReceived(Mockito.any(ExplicitXBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketModemStatusPacket() throws Exception {
		// Setup the resources for the test.
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		IDataReceiveListener dataListener = Mockito.mock(IDataReceiveListener.class);
		dataReader.addDataReceiveListener(dataListener);
		
		IIOSampleReceiveListener ioListener = Mockito.mock(IIOSampleReceiveListener.class);
		dataReader.addIOSampleReceiveListener(ioListener);
		
		IModemStatusReceiveListener modemListener = Mockito.mock(IModemStatusReceiveListener.class);
		dataReader.addModemStatusReceiveListener(modemListener);
		
		IExplicitDataReceiveListener explicitListener = Mockito.mock(IExplicitDataReceiveListener.class);
		dataReader.addExplicitDataReceiveListener(explicitListener);
		
		PACKET_TO_BE_RECEIVED = MODEM_STATUS_PACKET;
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning() || !testCI.transmissionFinished)
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockQueue, Mockito.times(1)).addPacket(PACKET_TO_BE_RECEIVED);
		Mockito.verify(packetListener, Mockito.times(1)).packetReceived(PACKET_TO_BE_RECEIVED);
		
		Mockito.verify(dataListener, Mockito.times(0)).dataReceived(Mockito.any(XBeeMessage.class));
		Mockito.verify(ioListener, Mockito.times(0)).ioSampleReceived(Mockito.any(RemoteXBeeDevice.class), Mockito.any(IOSample.class));
		Mockito.verify(modemListener, Mockito.times(1)).modemStatusEventReceived(Mockito.any(ModemStatusEvent.class));
		Mockito.verify(explicitListener, Mockito.times(0)).explicitDataReceived(Mockito.any(ExplicitXBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketExplicitIndicatorDigiPacket() throws Exception {
		// Setup the resources for the test.
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		IDataReceiveListener dataListener = Mockito.mock(IDataReceiveListener.class);
		dataReader.addDataReceiveListener(dataListener);
		
		IIOSampleReceiveListener ioListener = Mockito.mock(IIOSampleReceiveListener.class);
		dataReader.addIOSampleReceiveListener(ioListener);
		
		IModemStatusReceiveListener modemListener = Mockito.mock(IModemStatusReceiveListener.class);
		dataReader.addModemStatusReceiveListener(modemListener);
		
		IExplicitDataReceiveListener explicitListener = Mockito.mock(IExplicitDataReceiveListener.class);
		dataReader.addExplicitDataReceiveListener(explicitListener);
		
		PACKET_TO_BE_RECEIVED = EXPLICIT_INDICATOR_DIGI_PACKET;
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning() || !testCI.transmissionFinished)
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockQueue, Mockito.times(1)).addPacket(PACKET_TO_BE_RECEIVED);
		Mockito.verify(packetListener, Mockito.times(1)).packetReceived(PACKET_TO_BE_RECEIVED);
		
		Mockito.verify(dataListener, Mockito.times(1)).dataReceived(Mockito.any(XBeeMessage.class));
		Mockito.verify(ioListener, Mockito.times(0)).ioSampleReceived(Mockito.any(RemoteXBeeDevice.class), Mockito.any(IOSample.class));
		Mockito.verify(modemListener, Mockito.times(0)).modemStatusEventReceived(Mockito.any(ModemStatusEvent.class));
		Mockito.verify(explicitListener, Mockito.times(1)).explicitDataReceived(Mockito.any(ExplicitXBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketExplicitIndicatorPacket() throws Exception {
		// Setup the resources for the test.
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		IDataReceiveListener dataListener = Mockito.mock(IDataReceiveListener.class);
		dataReader.addDataReceiveListener(dataListener);
		
		IIOSampleReceiveListener ioListener = Mockito.mock(IIOSampleReceiveListener.class);
		dataReader.addIOSampleReceiveListener(ioListener);
		
		IModemStatusReceiveListener modemListener = Mockito.mock(IModemStatusReceiveListener.class);
		dataReader.addModemStatusReceiveListener(modemListener);
		
		IExplicitDataReceiveListener explicitListener = Mockito.mock(IExplicitDataReceiveListener.class);
		dataReader.addExplicitDataReceiveListener(explicitListener);
		
		PACKET_TO_BE_RECEIVED = EXPLICIT_INDICATOR_PACKET;
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning() || !testCI.transmissionFinished)
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockQueue, Mockito.times(1)).addPacket(PACKET_TO_BE_RECEIVED);
		Mockito.verify(packetListener, Mockito.times(1)).packetReceived(PACKET_TO_BE_RECEIVED);
		
		Mockito.verify(dataListener, Mockito.times(0)).dataReceived(Mockito.any(XBeeMessage.class));
		Mockito.verify(ioListener, Mockito.times(0)).ioSampleReceived(Mockito.any(RemoteXBeeDevice.class), Mockito.any(IOSample.class));
		Mockito.verify(modemListener, Mockito.times(0)).modemStatusEventReceived(Mockito.any(ModemStatusEvent.class));
		Mockito.verify(explicitListener, Mockito.times(1)).explicitDataReceived(Mockito.any(ExplicitXBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketIOExceptionWhenReading() throws Exception {
		// Setup the resources for the test.
		Mockito.doAnswer(new Answer<XBeePacket>() {
			@Override
			public XBeePacket answer(InvocationOnMock invocation) throws Throwable {
				if (testCI.isAlreadyRead())
					return null;
				
				testCI.setAlreadyRead();
				throw new IOException("Exception when reading");
			}
		}).when(mockInput).read();
		
		DataReader dataReader = new DataReader(testCI, OperatingMode.API, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		assertThat(testCI.isOpen(), is(equalTo(true)));
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning())
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockQueue, Mockito.times(0)).addPacket(Mockito.any(XBeePacket.class));
		Mockito.verify(packetListener, Mockito.times(0)).packetReceived(Mockito.any(XBeePacket.class));
		assertThat(testCI.isOpen(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketATmode() throws Exception {
		// Setup the resources for the test.
		TestConnectionInterface testCI = new TestConnectionInterface() {
			@Override
			public InputStream getInputStream() {
				if (isAlreadyRead())
					return null;
				
				setAlreadyRead();
				return mockInput;
			}
		};
		DataReader dataReader = new DataReader(testCI, OperatingMode.AT, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		assertThat(testCI.isOpen(), is(equalTo(true)));
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning())
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockInput, Mockito.times(0)).read();
		Mockito.verify(mockQueue, Mockito.times(0)).addPacket(Mockito.any(XBeePacket.class));
		Mockito.verify(packetListener, Mockito.times(0)).packetReceived(Mockito.any(XBeePacket.class));
		assertThat(testCI.isOpen(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#start()}. 
	 */
	@Test
	public final void testDataReaderReceivePacketNullInputStream() throws Exception {
		// Setup the resources for the test.
		TestConnectionInterface testCI = new TestConnectionInterface() {
			@Override
			public InputStream getInputStream() {
				setAlreadyRead();
				return null;
			}
		};
		DataReader dataReader = new DataReader(testCI, OperatingMode.AT, mockDevice);
		IPacketReceiveListener packetListener = Mockito.mock(IPacketReceiveListener.class);
		dataReader.addPacketReceiveListener(packetListener);
		
		assertThat(testCI.isOpen(), is(equalTo(true)));
		
		// Call the method under test.
		dataReader.start();
		
		waitForInitialization(dataReader.getId());
		testCI.notifyData();
		while (dataReader.isRunning())
			Thread.sleep(30);
		
		// Verify the result.
		Mockito.verify(mockInput, Mockito.times(0)).read();
		Mockito.verify(mockQueue, Mockito.times(0)).addPacket(Mockito.any(XBeePacket.class));
		Mockito.verify(packetListener, Mockito.times(0)).packetReceived(Mockito.any(XBeePacket.class));
		assertThat(testCI.isOpen(), is(equalTo(false)));
	}
}