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
package com.digi.xbee.api.connection.serial;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.digi.xbee.api.exceptions.ConnectionException;
import com.digi.xbee.api.exceptions.InterfaceInUseException;
import com.digi.xbee.api.exceptions.InvalidConfigurationException;
import com.digi.xbee.api.exceptions.InvalidInterfaceException;
import com.digi.xbee.api.exceptions.PermissionDeniedException;

public class AbstractSerialPortTest {
	
	private static final byte[] RECEIVED_BUFFER = "Hello, this is the received data".getBytes();
	private static final int OUTPUT_BUFFER_SIZE = 100;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private InputStream mockInputStream;
	private OutputStream mockOutputStream;
	
	String writtenData;
	
	public class TestAbstractSerialPort extends AbstractSerialPort {

		boolean breakOn = false;
		int readTimeout = 0;
		
		protected TestAbstractSerialPort(String port, SerialPortParameters parameters) {
			super(port, parameters);
		}

		protected TestAbstractSerialPort(String port, int baudRate) {
			super(port, baudRate);
		}

		protected TestAbstractSerialPort(String port, int baudRate, int receiveTimeout) {
			super(port, baudRate, receiveTimeout);
		}

		protected TestAbstractSerialPort(String port, SerialPortParameters parameters, int receiveTimeout) {
			super(port, parameters, receiveTimeout);
		}

		@Override
		public void open() throws InterfaceInUseException,
				InvalidInterfaceException, InvalidConfigurationException,
				PermissionDeniedException {
			connectionOpen = true;
		}

		@Override
		public void close() {
			connectionOpen = false;
		}

		@Override
		public InputStream getInputStream() {
			return mockInputStream;
		}

		@Override
		public OutputStream getOutputStream() {
			return mockOutputStream;
		}

		@Override
		public void setDTR(boolean state) {
		}

		@Override
		public void setRTS(boolean state) {
		}

		@Override
		public boolean isCTS() {
			return false;
		}

		@Override
		public boolean isDSR() {
			return false;
		}

		@Override
		public boolean isCD() {
			return false;
		}

		@Override
		public void setBreak(boolean enabled) {
			breakOn = enabled;
		}

		@Override
		public void sendBreak(final int duration) {
			breakOn = true;
			Thread t = new Thread() {
				@Override
				public void run() {
					long startMillis = System.currentTimeMillis();
					while (System.currentTimeMillis() - startMillis < duration){
						try {
							wait(10);
						} catch (InterruptedException e) { }
					}
					breakOn = false;
				}
			};
			t.run();
		}

		@Override
		public void setReadTimeout(int timeout) {
			readTimeout = timeout;
		}

		@Override
		public int getReadTimeout() {
			return readTimeout;
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
		writtenData = "";
		
		mockInputStream = Mockito.mock(InputStream.class);
		
		Mockito.when(mockInputStream.available()).thenReturn(RECEIVED_BUFFER.length);
		
		Mockito.doAnswer(new Answer<Integer>() {
			public Integer answer(InvocationOnMock invocation) throws Exception {
				byte[] buffer = (byte[])invocation.getArguments()[0];
				
				int l = buffer.length > RECEIVED_BUFFER.length ? RECEIVED_BUFFER.length: buffer.length;
				
				System.arraycopy(RECEIVED_BUFFER, 0, buffer, 0, l);
				
				return l;
			}
		}).when(mockInputStream).read(Mockito.any(byte[].class));
		
		Mockito.doAnswer(new Answer<Integer>() {
			public Integer answer(InvocationOnMock invocation) throws Exception {
				byte[] buffer = (byte[])invocation.getArguments()[0];
				int offset = (Integer) invocation.getArguments()[1];
				int length = (Integer) invocation.getArguments()[2];
				
				int l = length > RECEIVED_BUFFER.length ? RECEIVED_BUFFER.length: length;
				
				if (buffer != null)
					System.arraycopy(RECEIVED_BUFFER, 0, buffer, offset, l);
				
				return l;
			}
		}).when(mockInputStream).read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());
		
		
		mockOutputStream = Mockito.mock(OutputStream.class);
		
		Mockito.doAnswer(new Answer<Integer>() {
			public Integer answer(InvocationOnMock invocation) throws Exception {
				byte[] data = (byte[])invocation.getArguments()[0];
				
				int l = data.length > OUTPUT_BUFFER_SIZE ? OUTPUT_BUFFER_SIZE: data.length;
				
				for (int i = 0; i < l; i++)
					writtenData = writtenData + (char)data[i];
				
				//System.arraycopy(data, 0, writtenData, offset, l);
				
				return null;
			}
		}).when(mockOutputStream).write(Mockito.any(byte[].class));
		
		Mockito.doAnswer(new Answer<Integer>() {
			public Integer answer(InvocationOnMock invocation) throws Exception {
				byte[] data = (byte[])invocation.getArguments()[0];
				int offset = (Integer) invocation.getArguments()[1];
				int length = (Integer) invocation.getArguments()[2];
				
				int l = length > OUTPUT_BUFFER_SIZE ? OUTPUT_BUFFER_SIZE: length;
				
				for (int i = offset; i < l + offset; i++)
					writtenData = writtenData + (char)data[i];
				
				//System.arraycopy(data, 0, writtenData, offset, l);
				
				return null;
			}
		}).when(mockOutputStream).write(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, SerialPortParameters)}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortSerialPortParametersNullPort() {
		// Setup the resources for the test.
		String name = null;
		SerialPortParameters params = new SerialPortParameters(9600, 8, 1, 0, 0);
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Serial port cannot be null.")));
		
		// Call the method under test.
		new TestAbstractSerialPort(name, params);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, SerialPortParameters)}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortSerialPortParametersNullSerialPortParameters() {
		// Setup the resources for the test.
		String name = "COM1";
		SerialPortParameters params = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("SerialPortParameters cannot be null.")));
		
		// Call the method under test.
		new TestAbstractSerialPort(name, params);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, SerialPortParameters)}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortSerialPortParametersEmptyPort() {
		// Setup the resources for the test.
		String name = "";
		SerialPortParameters params = new SerialPortParameters(9600, 8, 1, 0, 0);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Serial port cannot be an empty string.")));
		
		// Call the method under test.
		new TestAbstractSerialPort(name, params);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, SerialPortParameters)}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortSerialPortParametersValid() {
		// Setup the resources for the test.
		String name = "COM1";
		SerialPortParameters params = new SerialPortParameters(57600, 8, 1, 0, 0);
		
		// Call the method under test.
		TestAbstractSerialPort port = new TestAbstractSerialPort(name, params);
		
		// Verify the result.
		assertThat(port.port, is(equalTo(name)));
		assertThat(port.parameters, is(equalTo(params)));
		assertThat(port.baudRate, is(equalTo(port.parameters.baudrate)));
		assertThat(port.receiveTimeout, is(equalTo(AbstractSerialPort.DEFAULT_PORT_TIMEOUT)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, int))}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortBaudrateNullPort() {
		// Setup the resources for the test.
		String name = null;
		int baudrate = 9600;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Serial port cannot be null.")));
		
		// Call the method under test.
		new TestAbstractSerialPort(name, baudrate);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, int)}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortBaudrateEmptyPort() {
		// Setup the resources for the test.
		String name = "";
		int baudrate = 9600;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Serial port cannot be an empty string.")));
		
		// Call the method under test.
		new TestAbstractSerialPort(name, baudrate);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, int)}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortBaudrateNegativeBaudrate() {
		// Setup the resources for the test.
		String name = "COM1";
		int baudrate = -9600;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Baudrate cannot be less than 0.")));
		
		// Call the method under test.
		new TestAbstractSerialPort(name, baudrate);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, int)}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortBaudrateValid() {
		// Setup the resources for the test.
		String name = "COM1";
		int baudrate = 1200;
		SerialPortParameters expectedParams = new SerialPortParameters(baudrate, 
				AbstractSerialPort.DEFAULT_DATA_BITS, AbstractSerialPort.DEFAULT_STOP_BITS, 
				AbstractSerialPort.DEFAULT_PARITY, AbstractSerialPort.DEFAULT_FLOW_CONTROL);
		
		// Call the method under test.
		TestAbstractSerialPort port = new TestAbstractSerialPort(name, baudrate);
		
		// Verify the result.
		assertThat(port.port, is(equalTo(name)));
		assertThat(port.baudRate, is(equalTo(baudrate)));
		assertThat(port.parameters, is(not(nullValue(SerialPortParameters.class))));
		assertThat(port.parameters.baudrate, is(equalTo(baudrate)));
		assertThat(port.parameters, is(equalTo(expectedParams)));
		assertThat(port.receiveTimeout, is(equalTo(AbstractSerialPort.DEFAULT_PORT_TIMEOUT)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, int, int)))}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortBaudrateReceiveTimeoutNullPort() {
		// Setup the resources for the test.
		String name = null;
		int baudrate = 9600;
		int receiveTimeout = 5000;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Serial port cannot be null.")));
		
		// Call the method under test.
		new TestAbstractSerialPort(name, baudrate, receiveTimeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, int, int)))}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortBaudrateReceiveTimeoutEmptyPort() {
		// Setup the resources for the test.
		String name = "";
		int baudrate = 9600;
		int receiveTimeout = 5000;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Serial port cannot be an empty string.")));
		
		// Call the method under test.
		new TestAbstractSerialPort(name, baudrate, receiveTimeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, int, int)))}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortBaudrateReceiveTimeoutNegativeBaudrate() {
		// Setup the resources for the test.
		String name = "COM1";
		int baudrate = -9600;
		int receiveTimeout = 5000;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Baudrate cannot be less than 0.")));
		
		// Call the method under test.
		new TestAbstractSerialPort(name, baudrate, receiveTimeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, int, int)))}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortBaudrateReceiveTimeoutNegativeReceiveTimeout() {
		// Setup the resources for the test.
		String name = "COM1";
		int baudrate = 9600;
		int receiveTimeout = -5000;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Receive timeout cannot be less than 0.")));
		
		// Call the method under test.
		new TestAbstractSerialPort(name, baudrate, receiveTimeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, int, int)))}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortBaudrateReceiveTimeoutValid() {
		// Setup the resources for the test.
		String name = "COM1";
		int baudrate = 2400;
		int receiveTimeout = 5000;
		SerialPortParameters expectedParams = new SerialPortParameters(baudrate, 
				AbstractSerialPort.DEFAULT_DATA_BITS, AbstractSerialPort.DEFAULT_STOP_BITS, 
				AbstractSerialPort.DEFAULT_PARITY, AbstractSerialPort.DEFAULT_FLOW_CONTROL);
		
		// Call the method under test.
		TestAbstractSerialPort port = new TestAbstractSerialPort(name, baudrate, receiveTimeout);
		
		// Verify the result.
		assertThat(port.port, is(equalTo(name)));
		assertThat(port.baudRate, is(equalTo(baudrate)));
		assertThat(port.parameters, is(not(nullValue(SerialPortParameters.class))));
		assertThat(port.parameters.baudrate, is(equalTo(baudrate)));
		assertThat(port.parameters, is(equalTo(expectedParams)));
		assertThat(port.receiveTimeout, is(equalTo(receiveTimeout)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, SerialPortParameters, int))}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortSerialPortParametersReceiveTimeoutNullPort() {
		// Setup the resources for the test.
		String name = null;
		SerialPortParameters params = new SerialPortParameters(9600, 8, 1, 0, 0);
		int receiveTimeout = 5000;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Serial port cannot be null.")));
		
		// Call the method under test.
		new TestAbstractSerialPort(name, params, receiveTimeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, SerialPortParameters, int))}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortSerialPortParametersReceiveTimeoutNullSerialPortParameters() {
		// Setup the resources for the test.
		String name = "COM1";
		SerialPortParameters params = null;
		int receiveTimeout = 5000;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("SerialPortParameters cannot be null.")));
		
		// Call the method under test.
		new TestAbstractSerialPort(name, params, receiveTimeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, SerialPortParameters, int))}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortSerialPortParametersReceiveTimeoutEmptyPort() {
		// Setup the resources for the test.
		String name = "";
		SerialPortParameters params = new SerialPortParameters(9600, 8, 1, 0, 0);
		int receiveTimeout = 5000;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Serial port cannot be an empty string.")));
		
		// Call the method under test.
		new TestAbstractSerialPort(name, params, receiveTimeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, SerialPortParameters, int))}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortSerialPortParametersReceiveTimeoutNegativeReceiveTimeout() {
		// Setup the resources for the test.
		String name = "COM1";
		SerialPortParameters params = new SerialPortParameters(9600, 8, 1, 0, 0);
		int receiveTimeout = -5000;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Receive timeout cannot be less than 0.")));
		
		// Call the method under test.
		new TestAbstractSerialPort(name, params, receiveTimeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#AbstractSerialPort(String, SerialPortParameters, int))}.
	 */
	@Test
	public final void testCreateAbstractSerialPortPortSerialPortParametersReceiveTimeoutValid() {
		// Setup the resources for the test.
		String name = "COM1";
		SerialPortParameters params = new SerialPortParameters(9600, 8, 1, 0, 0);
		int receiveTimeout = 5000;
		
		// Call the method under test.
		TestAbstractSerialPort port = new TestAbstractSerialPort(name, params, receiveTimeout);
		
		// Verify the result.
		assertThat(port.port, is(equalTo(name)));
		assertThat(port.baudRate, is(equalTo(params.baudrate)));
		assertThat(port.parameters, is(equalTo(params)));
		assertThat(port.receiveTimeout, is(equalTo(receiveTimeout)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#isOpen()}.
	 */
	@Test
	public final void testIsOpenNoOpened() {
		// Setup the resources for the test.
		TestAbstractSerialPort port = new TestAbstractSerialPort("COM1", 9600);
		
		// Call the method under test.
		boolean isOpen = port.isOpen();
		
		// Verify the result.
		assertThat(isOpen, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#isOpen()}.
	 */
	@Test
	public final void testIsOpenAfterOpening() {
		// Setup the resources for the test.
		TestAbstractSerialPort port = new TestAbstractSerialPort("COM1", 9600);
		port.connectionOpen = true;
		
		// Call the method under test.
		boolean isOpen = port.isOpen();
		
		// Verify the result.
		assertThat(isOpen, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#getPort()}.
	 */
	@Test
	public final void testGetPort() {
		// Setup the resources for the test.
		String name = "COM1";
		TestAbstractSerialPort port = new TestAbstractSerialPort(name, 9600);
		
		// Call the method under test.
		String result = port.getPort();
		
		// Verify the result.
		assertThat(result, is(equalTo(port.port)));
		assertThat(result, is(equalTo(name)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#isHardwareFlowControl()}.
	 */
	@Test
	public final void testIsHardwareFlowControlNoHwFlowControl() {
		// Setup the resources for the test.
		SerialPortParameters params = new SerialPortParameters(9600, 8, 1, 0, 0); // 0 - No Hardware flow control
		TestAbstractSerialPort port = new TestAbstractSerialPort("COM1", params);
		
		// Call the method under test.
		boolean result = port.isHardwareFlowControl();
		
		// Verify the result.
		assertThat(result, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#isHardwareFlowControl()}.
	 */
	@Test
	public final void testIsHardwareFlowControlWithHwFlowControl() {
		// Setup the resources for the test.
		SerialPortParameters params = new SerialPortParameters(9600, 8, 1, 0, 3); // 3 - Hardware flow control
		TestAbstractSerialPort port = new TestAbstractSerialPort("COM1", params);
		
		// Call the method under test.
		boolean result = port.isHardwareFlowControl();
		
		// Verify the result.
		assertThat(result, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#setPortParameters(int, int, int, int, int)}.
	 */
	@Test
	public final void testSetPortParametersNegativeBaudrate() throws InvalidConfigurationException, ConnectionException {
		// Setup the resources for the test.
		int baudrate = -115200;
		int dataBits = 7;
		int stopBits = 2;
		int parity = 1;
		int flowControl = 3;
		TestAbstractSerialPort port = new TestAbstractSerialPort("COM1", 9600);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Baudrate cannot be less than 0.")));
		
		// Call the method under test.
		port.setPortParameters(baudrate, dataBits, stopBits, parity, flowControl);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#setPortParameters(int, int, int, int, int)}.
	 */
	@Test
	public final void testSetPortParametersNegativeDataBits() throws InvalidConfigurationException, ConnectionException {
		// Setup the resources for the test.
		int baudrate = 115200;
		int dataBits = -7;
		int stopBits = 2;
		int parity = 1;
		int flowControl = 3;
		TestAbstractSerialPort port = new TestAbstractSerialPort("COM1", 9600);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Number of data bits cannot be less than 0.")));
		
		// Call the method under test.
		port.setPortParameters(baudrate, dataBits, stopBits, parity, flowControl);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#setPortParameters(int, int, int, int, int)}.
	 */
	@Test
	public final void testSetPortParametersNegativeStopBits() throws InvalidConfigurationException, ConnectionException {
		// Setup the resources for the test.
		int baudrate = 115200;
		int dataBits = 7;
		int stopBits = -2;
		int parity = 1;
		int flowControl = 3;
		TestAbstractSerialPort port = new TestAbstractSerialPort("COM1", 9600);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Number of stop bits cannot be less than 0.")));
		
		// Call the method under test.
		port.setPortParameters(baudrate, dataBits, stopBits, parity, flowControl);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#setPortParameters(int, int, int, int, int)}.
	 */
	@Test
	public final void testSetPortParametersNegativeParity() throws InvalidConfigurationException, ConnectionException {
		// Setup the resources for the test.
		int baudrate = 115200;
		int dataBits = 7;
		int stopBits = 2;
		int parity = -1;
		int flowControl = 3;
		TestAbstractSerialPort port = new TestAbstractSerialPort("COM1", 9600);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Illegal parity value.")));
		
		// Call the method under test.
		port.setPortParameters(baudrate, dataBits, stopBits, parity, flowControl);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#setPortParameters(int, int, int, int, int)}.
	 */
	@Test
	public final void testSetPortParametersNegativeFlowControl() throws InvalidConfigurationException, ConnectionException {
		// Setup the resources for the test.
		int baudrate = 115200;
		int dataBits = 7;
		int stopBits = 2;
		int parity = 1;
		int flowControl = -3;
		TestAbstractSerialPort port = new TestAbstractSerialPort("COM1", 9600);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Illegal flow control value.")));
		
		// Call the method under test.
		port.setPortParameters(baudrate, dataBits, stopBits, parity, flowControl);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#setPortParameters(int, int, int, int, int)}.
	 * 
	 * @throws ConnectionException 
	 * @throws InvalidConfigurationException 
	 */
	@Test
	public final void testSetPortParametersClosedPort() throws InvalidConfigurationException, ConnectionException {
		// Setup the resources for the test.
		SerialPortParameters params = new SerialPortParameters(115200, 7, 2, 1, 3);
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		
		// Call the method under test.
		port.setPortParameters(params.baudrate, params.dataBits, params.stopBits, params.parity, params.flowControl);
		
		// Verify the result.
		assertThat(port.baudRate, is(equalTo(params.baudrate)));
		assertThat(port.parameters, is(equalTo(params)));
		Mockito.verify(port).isOpen();
		Mockito.verify(port, Mockito.times(0)).close();
		Mockito.verify(port, Mockito.times(0)).open();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#setPortParameters(int, int, int, int, int)}.
	 * 
	 * @throws ConnectionException 
	 * @throws InvalidConfigurationException 
	 */
	@Test
	public final void testSetPortParametersOpenPort() throws InvalidConfigurationException, ConnectionException {
		// Setup the resources for the test.
		SerialPortParameters params = new SerialPortParameters(115200, 7, 2, 1, 3);
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		port.open();
		
		// Call the method under test.
		port.setPortParameters(params.baudrate, params.dataBits, params.stopBits, params.parity, params.flowControl);
		
		// Verify the result.
		assertThat(port.baudRate, is(equalTo(params.baudrate)));
		assertThat(port.parameters, is(equalTo(params)));
		Mockito.verify(port).isOpen();
		Mockito.verify(port, Mockito.times(1)).close();
		Mockito.verify(port, Mockito.times(2)).open();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#setPortParameters(SerialPortParameters))}.
	 * 
	 * @throws ConnectionException 
	 * @throws InvalidConfigurationException 
	 */
	@Test
	public final void testSetPortParametersWithParamsClosedPort() throws InvalidConfigurationException, ConnectionException {
		// Setup the resources for the test.
		SerialPortParameters params = new SerialPortParameters(115200, 7, 2, 1, 3);
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		
		// Call the method under test.
		port.setPortParameters(params);
		
		// Verify the result.
		assertThat(port.baudRate, is(equalTo(params.baudrate)));
		assertThat(port.parameters, is(equalTo(params)));
		Mockito.verify(port).isOpen();
		Mockito.verify(port, Mockito.times(0)).close();
		Mockito.verify(port, Mockito.times(0)).open();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#setPortParameters(SerialPortParameters)}.
	 * 
	 * @throws ConnectionException 
	 * @throws InvalidConfigurationException 
	 */
	@Test
	public final void testSetPortParametersWithParamsOpenPort() throws InvalidConfigurationException, ConnectionException {
		// Setup the resources for the test.
		SerialPortParameters params = new SerialPortParameters(115200, 7, 2, 1, 3);
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		port.open();
		
		// Call the method under test.
		port.setPortParameters(params);
		
		// Verify the result.
		assertThat(port.baudRate, is(equalTo(params.baudrate)));
		assertThat(port.parameters, is(equalTo(params)));
		Mockito.verify(port).isOpen();
		Mockito.verify(port, Mockito.times(1)).close();
		Mockito.verify(port, Mockito.times(2)).open();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#purge()}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testPurgeNullInputStream() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		mockInputStream = null;
		
		// Call the method under test.
		port.purge();
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(1)).getInputStream();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#purge()}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testPurgeExceptionWhenAvailable() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		Mockito.doThrow(new IOException("IO exception")).when(mockInputStream).available();
		
		// Call the method under test.
		port.purge();
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(2)).getInputStream();
		Mockito.verify(mockInputStream, Mockito.times(1)).available();
		Mockito.verify(mockInputStream, Mockito.times(0)).read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#purge()}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testPurgeExceptionAvailableBytesToRead() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		
		Mockito.when(mockInputStream.available()).thenReturn(10);
		Mockito.when(mockInputStream.read(Mockito.any(byte[].class), Mockito.eq(0), Mockito.eq(10))).thenReturn(10);
		
		// Call the method under test.
		port.purge();
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(5)).getInputStream();
		Mockito.verify(mockInputStream, Mockito.times(3)).available();
		Mockito.verify(mockInputStream, Mockito.times(1)).read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#purge()}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testPurge() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		
		// Call the method under test.
		port.purge();
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(5)).getInputStream();
		Mockito.verify(mockInputStream, Mockito.times(3)).available();
		Mockito.verify(mockInputStream, Mockito.times(1)).read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());
		Mockito.verify(mockInputStream, Mockito.times(1)).read(Mockito.any(byte[].class), Mockito.eq(0), Mockito.eq(RECEIVED_BUFFER.length));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#flush()}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testFlushNullOutputStream() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		mockOutputStream = null;
		
		// Call the method under test.
		port.flush();
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(1)).getOutputStream();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#flush()}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testFlushExceptionWhenFlushing() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		Mockito.doThrow(new IOException("IO exception")).when(mockOutputStream).flush();
		
		// Call the method under test.
		port.flush();
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(2)).getOutputStream();
		Mockito.verify(mockOutputStream, Mockito.times(1)).flush();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#flush()}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testFlush() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		
		// Call the method under test.
		port.flush();
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(2)).getOutputStream();
		Mockito.verify(mockOutputStream, Mockito.times(1)).flush();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[])}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataByteArrayDataNullData() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Data to be sent cannot be null.")));
		
		// Call the method under test.
		port.writeData(data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[])}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataByteArrayDataEmptyData() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[0];
		
		// Call the method under test.
		port.writeData(data);
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(3)).getOutputStream();
		Mockito.verify(port, Mockito.times(1)).isHardwareFlowControl();
		Mockito.verify(port, Mockito.times(0)).isCTS();
		Mockito.verify(mockOutputStream, Mockito.times(1)).write(data);
		Mockito.verify(mockOutputStream, Mockito.times(1)).flush();
		assertThat(writtenData.length(), is(equalTo(data.length)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[])}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataByteArrayDataOutputStreamNull() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		mockOutputStream = null;
		byte[] data = new byte[] {'h', 'e', 'l', 'l', 'o'};
		
		// Call the method under test.
		port.writeData(data);
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(1)).getOutputStream();
		Mockito.verify(port, Mockito.times(0)).isHardwareFlowControl();
		assertThat(writtenData.length(), is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[])}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataByteArrayDataWriteIOException() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'h', 'e', 'l', 'l', 'o'};
		Mockito.doThrow(new IOException("IO exception: Write")).when(mockOutputStream).write(data);
		
		exception.expect(IOException.class);
		exception.expectMessage(is(equalTo("IO exception: Write")));
		
		// Call the method under test.
		port.writeData(data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[])}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataByteArrayDataFlushIOException() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'h', 'e', 'l', 'l', 'o'};
		Mockito.doThrow(new IOException("IO exception: Flush")).when(mockOutputStream).flush();
		
		exception.expect(IOException.class);
		exception.expectMessage(is(equalTo("IO exception: Flush")));
		
		// Call the method under test.
		port.writeData(data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[])}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataByteArrayData() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'h', 'e', 'l', 'l', 'o'};
		
		// Call the method under test.
		port.writeData(data);
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(3)).getOutputStream();
		Mockito.verify(port, Mockito.times(1)).isHardwareFlowControl();
		Mockito.verify(port, Mockito.times(0)).isCTS();
		Mockito.verify(mockOutputStream, Mockito.times(1)).write(data);
		Mockito.verify(mockOutputStream, Mockito.times(1)).flush();
		assertThat(writtenData.length(), is(equalTo(data.length)));
		assertThat(writtenData.getBytes(), is(equalTo(data)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[])}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataByteArrayDataHardwareFlowCtrl() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'h', 'e', 'l', 'l', 'o', ' ', 'X', 'B', 'e', 'e'};
		
		Mockito.when(port.isHardwareFlowControl()).thenReturn(true);
		Mockito.when(port.isCTS()).thenReturn(false, true, true);
		
		// Call the method under test.
		port.writeData(data);
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(3)).getOutputStream();
		Mockito.verify(port, Mockito.times(1)).isHardwareFlowControl();
		Mockito.verify(port, Mockito.times(3)).isCTS();
		Mockito.verify(mockOutputStream, Mockito.times(1)).write(data);
		Mockito.verify(mockOutputStream, Mockito.times(1)).flush();
		assertThat(writtenData.length(), is(equalTo(data.length)));
		assertThat(writtenData.getBytes(), is(equalTo(data)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[])}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataByteArrayDataHardwareFlowCtrlCTSLow() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'h', 'e', 'l', 'l', 'o', ' ', 'X', 'B', 'e', 'e'};
		
		Mockito.when(port.isHardwareFlowControl()).thenReturn(true);
		Mockito.when(port.isCTS()).thenReturn(false, false, false);
		
		// Call the method under test.
		port.writeData(data);
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(1)).getOutputStream();
		Mockito.verify(port, Mockito.times(1)).isHardwareFlowControl();
		Mockito.verify(port, Mockito.times(4)).isCTS();
		Mockito.verify(mockOutputStream, Mockito.times(0)).write(data);
		Mockito.verify(mockOutputStream, Mockito.times(0)).flush();
		assertThat(writtenData.length(), is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataCompleteNullData() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = null;
		int offset = 0;
		int length = 0;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Data to be sent cannot be null.")));
		
		// Call the method under test.
		port.writeData(data, offset, length);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataCompleteNegativeOffset() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'h', 'e', 'l', 'l', 'o'};
		int offset = -5;
		int length = 0;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Offset cannot be less than 0.")));
		
		// Call the method under test.
		port.writeData(data, offset, length);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataCompleteNegativeLength() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'h', 'e', 'l', 'l', 'o'};
		int offset = 0;
		int length = -9;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Length cannot be less than 1.")));
		
		// Call the method under test.
		port.writeData(data, offset, length);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataCompleteOffsetBiggerThanDataSize() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'h', 'e', 'l', 'l', 'o'};
		int offset = data.length + 1;
		int length = data.length;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Offset must be less than the data length.")));
		
		// Call the method under test.
		port.writeData(data, offset, length);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataCompleteOffsetPlusLengthThanDataSize() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'h', 'e', 'l', 'l', 'o'};
		int offset = 1;
		int length = data.length;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Offset + length cannot be great than the data length.")));
		
		// Call the method under test.
		port.writeData(data, offset, length);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataCompleteEmptyData() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[0];
		int offset = 0;
		int length = data.length;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Length cannot be less than 1.")));
		
		// Call the method under test.
		port.writeData(data, offset, length);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataCompleteOutputStreamNull() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		mockOutputStream = null;
		byte[] data = new byte[] {'h', 'e', 'l', 'l', 'o'};
		int offset = 0;
		int length = data.length;
		
		// Call the method under test.
		port.writeData(data, offset, length);
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(1)).getOutputStream();
		Mockito.verify(port, Mockito.times(0)).isHardwareFlowControl();
		assertThat(writtenData.length(), is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataCompleteWriteIOException() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'h', 'e', 'l', 'l', 'o'};
		int offset = 0;
		int length = data.length;
		
		Mockito.doThrow(new IOException("IO exception: Write")).when(mockOutputStream).write(data, offset, length);
		
		exception.expect(IOException.class);
		exception.expectMessage(is(equalTo("IO exception: Write")));
		
		// Call the method under test.
		port.writeData(data, offset, length);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataCompleteFlushIOException() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'h', 'e', 'l', 'l', 'o'};
		int offset = 0;
		int length = data.length;
		
		Mockito.doThrow(new IOException("IO exception: Flush")).when(mockOutputStream).flush();
		
		exception.expect(IOException.class);
		exception.expectMessage(is(equalTo("IO exception: Flush")));
		
		// Call the method under test.
		port.writeData(data, offset, length);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataComplete() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'h', 'e', 'l', 'l', 'o'};
		int offset = 0;
		int length = data.length;
		
		// Call the method under test.
		port.writeData(data, offset, length);
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(3)).getOutputStream();
		Mockito.verify(port, Mockito.times(1)).isHardwareFlowControl();
		Mockito.verify(port, Mockito.times(0)).isCTS();
		Mockito.verify(mockOutputStream, Mockito.times(1)).write(data, offset, length);
		Mockito.verify(mockOutputStream, Mockito.times(1)).flush();
		assertThat(writtenData.length(), is(equalTo(data.length)));
		assertThat(writtenData.getBytes(), is(equalTo(data)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataCompleteOffsetAndLength() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'1', '2', 'h', 'e', 'l', 'l', 'o', 'y', '6', 'p', '8'};
		int offset = 2;
		int length = 5;
		
		// Call the method under test.
		port.writeData(data, offset, length);
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(3)).getOutputStream();
		Mockito.verify(port, Mockito.times(1)).isHardwareFlowControl();
		Mockito.verify(port, Mockito.times(0)).isCTS();
		Mockito.verify(mockOutputStream, Mockito.times(1)).write(data, offset, length);
		Mockito.verify(mockOutputStream, Mockito.times(1)).flush();
		assertThat(writtenData.length(), is(equalTo(length)));
		assertThat(writtenData.getBytes(), is(equalTo(new byte[] {'h', 'e', 'l', 'l', 'o'})));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataCompleteHardwareFlowCtrl() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'y', '6', 'p', 'h', 'e', 'l', 'l', 'o', ' ', 'X', 'B', 'e', 'e', '1', '2', '8', 'u'};
		int offset = 3;
		int length = 10;
		
		Mockito.when(port.isHardwareFlowControl()).thenReturn(true);
		Mockito.when(port.isCTS()).thenReturn(false, true, true);
		
		// Call the method under test.
		port.writeData(data, offset, length);
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(3)).getOutputStream();
		Mockito.verify(port, Mockito.times(1)).isHardwareFlowControl();
		Mockito.verify(port, Mockito.times(3)).isCTS();
		Mockito.verify(mockOutputStream, Mockito.times(1)).write(data, offset, length);
		Mockito.verify(mockOutputStream, Mockito.times(1)).flush();
		assertThat(writtenData.length(), is(equalTo(length)));
		assertThat(writtenData.getBytes(), is(equalTo(new byte[] {'h', 'e', 'l', 'l', 'o', ' ', 'X', 'B', 'e', 'e'})));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#writeData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testWriteDataCompleteHardwareFlowCtrlCTSLow() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'y', '6', 'p', 'h', 'e', 'l', 'l', 'o', ' ', 'X', 'B', 'e', 'e', '1', '2', '8', 'u'};
		int offset = 3;
		int length = 10;
		
		Mockito.when(port.isHardwareFlowControl()).thenReturn(true);
		Mockito.when(port.isCTS()).thenReturn(false, false, false);
		
		// Call the method under test.
		port.writeData(data, offset, length);
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(1)).getOutputStream();
		Mockito.verify(port, Mockito.times(1)).isHardwareFlowControl();
		Mockito.verify(port, Mockito.times(4)).isCTS();
		Mockito.verify(mockOutputStream, Mockito.times(0)).write(data, offset, length);
		Mockito.verify(mockOutputStream, Mockito.times(0)).flush();
		assertThat(writtenData.length(), is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#readData(byte[])}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testReadDataByteArrayNullData() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Buffer cannot be null.")));
		
		// Call the method under test.
		port.readData(data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#readData(byte[])}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testReadDataByteArrayEmptyData() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[0];
		
		// Call the method under test.
		int nBytes = port.readData(data);
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(2)).getInputStream();
		Mockito.verify(mockInputStream, Mockito.times(1)).read(data);
		assertThat(nBytes, is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#readData(byte[])}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testReadDataByteArrayInputStreamNull() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		mockInputStream = null;
		byte[] data = new byte[0];
		
		// Call the method under test.
		int nBytes = port.readData(data);
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(1)).getInputStream();
		assertThat(nBytes, is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#readData(byte[])}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testReadDataByteArrayReadIOException() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[20];
		Mockito.doThrow(new IOException("IO exception: Read")).when(mockInputStream).read(data);
		
		exception.expect(IOException.class);
		exception.expectMessage(is(equalTo("IO exception: Read")));
		
		// Call the method under test.
		port.readData(data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#readData(byte[])}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testReadDataByteArray() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[10];
		
		// Call the method under test.
		int nBytes = port.readData(data);
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(2)).getInputStream();
		Mockito.verify(mockInputStream, Mockito.times(1)).read(data);
		assertThat(nBytes, is(equalTo(data.length)));
		assertThat(new String(data), is(equalTo(new String(RECEIVED_BUFFER, 0, data.length))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#readData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testReadDataCompleteNullData() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = null;
		int offset = 0;
		int length = 0;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Buffer cannot be null.")));
		
		// Call the method under test.
		port.readData(data, offset, length);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#readData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testReadDataCompleteNegativeOffset() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[0];
		int offset = -5;
		int length = 0;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Offset cannot be less than 0.")));
		
		// Call the method under test.
		port.readData(data, offset, length);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#readData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testReadDataCompleteNegativeLength() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[0];
		int offset = 0;
		int length = -6;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Length cannot be less than 1.")));
		
		// Call the method under test.
		port.readData(data, offset, length);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#readData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testReadDataCompleteOffsetBiggerThanDataSize() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'h', 'e', 'l', 'l', 'o'};
		int offset = data.length + 1;
		int length = data.length;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Offset must be less than the buffer length.")));
		
		// Call the method under test.
		port.readData(data, offset, length);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#readData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testReadDataCompleteOffsetPlusLengthThanDataSize() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[] {'h', 'e', 'l', 'l', 'o'};
		int offset = 1;
		int length = data.length;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Offset + length cannot be great than the buffer length.")));
		
		// Call the method under test.
		port.readData(data, offset, length);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#readData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testReadDataCompleteEmptyData() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[0];
		int offset = 0;
		int length = data.length;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Length cannot be less than 1.")));
		
		// Call the method under test.
		port.readData(data, offset, length);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#readData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testReadDataCompleteInputStreamNull() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		mockInputStream = null;
		byte[] data = new byte[10];
		int offset = 0;
		int length = data.length;
		
		// Call the method under test.
		int nBytes = port.readData(data, offset, length);
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(1)).getInputStream();
		assertThat(nBytes, is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#readData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testReadDataCompleteReadIOException() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[20];
		int offset = 0;
		int length = data.length;
		Mockito.doThrow(new IOException("IO exception: Read")).when(mockInputStream).read(data, offset, length);
		
		exception.expect(IOException.class);
		exception.expectMessage(is(equalTo("IO exception: Read")));
		
		// Call the method under test.
		port.readData(data, offset, length);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#readData(byte[], int, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testReadDataComplete() throws IOException {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		byte[] data = new byte[20];
		int offset = 5;
		int length = 10;
		byte[] expected = new byte[] {0, 0, 0, 0, 0, 'H', 'e', 'l', 'l', 'o', ',', ' ', 't', 'h', 'i', 0, 0, 0, 0, 0};
		
		// Call the method under test.
		int nBytes = port.readData(data, offset, length);
		
		// Verify the result.
		Mockito.verify(port, Mockito.times(2)).getInputStream();
		Mockito.verify(mockInputStream, Mockito.times(1)).read(data, offset, length);
		assertThat(nBytes, is(equalTo(length)));
		assertThat(data, is(equalTo(expected)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#getPortParameters()}.
	 */
	@Test
	public final void testGetPortParametersDefault() {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		SerialPortParameters expected = new SerialPortParameters(9600, AbstractSerialPort.DEFAULT_DATA_BITS, 
				AbstractSerialPort.DEFAULT_STOP_BITS, AbstractSerialPort.DEFAULT_PARITY, AbstractSerialPort.DEFAULT_FLOW_CONTROL);
		
		// Call the method under test.
		SerialPortParameters result = port.getPortParameters();
		
		// Verify the result.
		assertThat(result, is(equalTo(expected)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#getPortParameters()}.
	 */
	@Test
	public final void testGetPortParameters() {
		// Setup the resources for the test.
		SerialPortParameters expected = new SerialPortParameters(9600, 7, 
				2, 1, 3);
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", expected));
		
		// Call the method under test.
		SerialPortParameters result = port.getPortParameters();
		
		// Verify the result.
		assertThat(result, is(equalTo(expected)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#getReadTimeout()}.
	 */
	@Test
	public void testGetReceiveTimeoutDefault() {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		
		// Call the method under test.
		int result = port.getReceiveTimeout();
		
		// Verify the result.
		assertThat(result, is(equalTo(AbstractSerialPort.DEFAULT_PORT_TIMEOUT)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#getReadTimeout()}.
	 */
	@Test
	public void testGetReceiveTimeout() {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 
				new SerialPortParameters(9600, AbstractSerialPort.DEFAULT_DATA_BITS, 
						AbstractSerialPort.DEFAULT_STOP_BITS, AbstractSerialPort.DEFAULT_PARITY, 
						AbstractSerialPort.DEFAULT_FLOW_CONTROL), 20));
		
		// Call the method under test.
		int result = port.getReceiveTimeout();
		
		// Verify the result.
		assertThat(result, is(equalTo(20)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#toString()}.
	 */
	@Test
	public void testToStringParityOdd() {
		// Setup the resources for the test.
		SerialPortParameters params = new SerialPortParameters(115200, 7, 2, 1, 3);
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", params));
		String expected = "[COM1 - 115200/7/O/2/H] ";
		
		// Call the method under test.
		String result = port.toString();
		
		// Verify the result.
		assertThat(result, is(equalTo(expected)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#toString()}.
	 */
	@Test
	public void testToStringParityEven() {
		// Setup the resources for the test.
		SerialPortParameters params = new SerialPortParameters(115200, 7, 2, 2, 3);
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", params));
		String expected = "[COM1 - 115200/7/E/2/H] ";
		
		// Call the method under test.
		String result = port.toString();
		
		// Verify the result.
		assertThat(result, is(equalTo(expected)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#toString()}.
	 */
	@Test
	public void testToStringParityMark() {
		// Setup the resources for the test.
		SerialPortParameters params = new SerialPortParameters(115200, 7, 2, 3, 3);
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", params));
		String expected = "[COM1 - 115200/7/M/2/H] ";
		
		// Call the method under test.
		String result = port.toString();
		
		// Verify the result.
		assertThat(result, is(equalTo(expected)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#toString()}.
	 */
	@Test
	public void testToStringParitySpace() {
		// Setup the resources for the test.
		SerialPortParameters params = new SerialPortParameters(115200, 7, 2, 4, 3);
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", params));
		String expected = "[COM1 - 115200/7/S/2/H] ";
		
		// Call the method under test.
		String result = port.toString();
		
		// Verify the result.
		assertThat(result, is(equalTo(expected)));
	}
	
	/**
	 * TTest method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#toString()}.
	 */
	@Test
	public void testToStringSoftwareFlowControl() {
		// Setup the resources for the test.
		SerialPortParameters params = new SerialPortParameters(115200, 7, 2, 4, 4);
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", params));
		String expected = "[COM1 - 115200/7/S/2/S] ";
		
		// Call the method under test.
		String result = port.toString();
		
		// Verify the result.
		assertThat(result, is(equalTo(expected)));
	}

	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.AbstractSerialPort#toString()}.
	 */
	@Test
	public void testToStringDefault() {
		// Setup the resources for the test.
		TestAbstractSerialPort port = Mockito.spy(new TestAbstractSerialPort("COM1", 9600));
		String expected = "[COM1 - 9600/8/N/1/N] ";
		
		// Call the method under test.
		String result = port.toString();
		
		// Verify the result.
		assertThat(result, is(equalTo(expected)));
	}
}
