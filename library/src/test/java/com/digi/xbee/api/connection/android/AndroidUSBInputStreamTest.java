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
package com.digi.xbee.api.connection.android;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;

@RunWith(PowerMockRunner.class)
@PrepareForTest({System.class, AndroidUSBInputStream.class})
public class AndroidUSBInputStreamTest {

	// Constants.
	private static final String VARIABLE_READ_BUFFER = "readBuffer";
	private static final String VARIABLE_WORKING = "working";

	private static final byte[] DATA = "Test data".getBytes();

	// Variables.
	private UsbDeviceConnection usbConnection;
	private UsbEndpoint receiveEndPoint;
	private AndroidXBeeInterface androidInterface;

	private CircularByteBuffer circularBuffer;

	private AndroidUSBInputStream is;

	@Before
	public void setup() throws Exception {
		usbConnection = Mockito.mock(UsbDeviceConnection.class);
		Mockito.when(usbConnection.bulkTransfer(Mockito.any(UsbEndpoint.class), Mockito.any(byte[].class),
				Mockito.anyInt(), Mockito.anyInt())).thenReturn(DATA.length);
		receiveEndPoint = Mockito.mock(UsbEndpoint.class);
		androidInterface = Mockito.mock(AndroidXBeeInterface.class);

		is = PowerMockito.spy(new AndroidUSBInputStream(androidInterface, receiveEndPoint, usbConnection));

		circularBuffer = Mockito.mock(CircularByteBuffer.class);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBInputStream#read()}
	 *
	 * <p>Verify that an {@code IOException} is thrown when reading data without
	 * having started the read thread first.</p>
	 */
	@Test(expected=IOException.class)
	public void testReadByteThreadNull() throws IOException {
		// Call the method that should throw the exception.
		is.read();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBInputStream#read()}
	 *
	 * <p>Verify that one byte is read successfully.</p>
	 */
	@Test
	public void testReadByte() throws IOException {
		// Set up the resources for the test.
		final byte[] data = new byte[]{0x60};

		Mockito.doAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				byte[] buffer = (byte[])invocation.getArguments()[0];
				int l = buffer.length > data.length ? data.length : buffer.length;
				System.arraycopy(data, 0, buffer, 0, l);
				return l;
			}
		}).when(circularBuffer).read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());
		Whitebox.setInternalState(is, VARIABLE_READ_BUFFER, circularBuffer);

		// Perform the verifications.
		assertThat(is.read(), is(equalTo(data[0] & 0xFF)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBInputStream#read(byte[])}
	 *
	 * <p>Verify that an {@code IOException} is thrown when reading data without
	 * having started the read thread first.</p>
	 */
	@Test(expected=IOException.class)
	public void testReadByteArrayThreadNull() throws IOException {
		// Set up the resources for the test.
		byte[] buffer = new byte[1];

		// Call the method that should throw the exception.
		is.read(buffer);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBInputStream#read(byte[])}
	 *
	 * <p>Verify that a byte array is read successfully.</p>
	 */
	@Test
	public void testReadByteArray() throws IOException {
		// Set up the resources for the test.
		final byte[] data = new byte[]{0x56, 0x10};
		byte[] buffer = new byte[data.length];

		Mockito.doAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				byte[] buffer = (byte[])invocation.getArguments()[0];
				int l = buffer.length > data.length ? data.length : buffer.length;
				System.arraycopy(data, 0, buffer, 0, l);
				return l;
			}
		}).when(circularBuffer).read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());
		Whitebox.setInternalState(is, VARIABLE_READ_BUFFER, circularBuffer);

		// Perform the verifications.
		assertThat(is.read(buffer), is(equalTo(data.length)));
		assertThat(buffer, is(equalTo(data)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBInputStream#read(byte[], int, int)}
	 *
	 * <p>Verify that an {@code IOException} is thrown when reading data without
	 * having started the read thread first.</p>
	 */
	@Test(expected=IOException.class)
	public void testReadByteArrayWithOffsetThreadNull() throws IOException {
		// Set up the resources for the test.
		byte[] buffer = new byte[1];

		// Call the method that should throw the exception.
		is.read(buffer, 0, buffer.length);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBInputStream#read(byte[], int, int)}
	 *
	 * <p>Verify that when the deadline for reading data is elapsed, {@code -1}
	 * is returned.</p>
	 */
	@Test
	public void testReadByteArrayWithOffsetDeadline() throws IOException {
		// Set up the resources for the test.
		byte[] buffer = new byte[2];

		Mockito.when(circularBuffer.read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt())).thenReturn(1);
		Whitebox.setInternalState(is, VARIABLE_READ_BUFFER, circularBuffer);

		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(0L).thenReturn(1000L);

		// Perform the verifications.
		assertThat(is.read(buffer, 0, buffer.length), is(equalTo(-1)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBInputStream#read(byte[], int, int)}
	 *
	 * <p>Verify that when none byte can be read, {@code -1} is returned.</p>
	 */
	@Test
	public void testReadByteArrayWithOffsetZeroReadBytes() throws IOException {
		// Set up the resources for the test.
		byte[] buffer = new byte[2];

		Mockito.when(circularBuffer.read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt())).thenReturn(0);
		Whitebox.setInternalState(is, VARIABLE_READ_BUFFER, circularBuffer);

		// Perform the verifications.
		assertThat(is.read(buffer, 0, buffer.length), is(equalTo(-1)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBInputStream#read(byte[], int, int)}
	 *
	 * <p>Verify that a byte array with offset is read successfully.</p>
	 */
	@Test
	public void testReadByteArrayWithOffset() throws IOException {
		// Set up the resources for the test.
		byte firstByte = 0x7A;
		final int offset = 1;
		final byte[] data = new byte[]{0x56, 0x10};
		byte[] buffer = new byte[data.length + offset];
		byte[] result = new byte[buffer.length];
		System.arraycopy(new byte[]{firstByte}, 0, buffer, 0, 1);
		System.arraycopy(new byte[]{firstByte}, 0, result, 0, 1);
		System.arraycopy(data, 0, result, offset, data.length);

		Mockito.doAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				byte[] buffer = (byte[])invocation.getArguments()[0];
				int l = buffer.length > data.length ? data.length : buffer.length;
				System.arraycopy(data, 0, buffer, offset, l);
				return l;
			}
		}).when(circularBuffer).read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());
		Whitebox.setInternalState(is, VARIABLE_READ_BUFFER, circularBuffer);

		// Perform the verifications.
		assertThat(is.read(buffer, offset, buffer.length), is(equalTo(data.length)));
		assertThat(buffer, is(equalTo(result)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBInputStream#available()}
	 *
	 * <p>Verify that an {@code IOException} is thrown when reading the
	 * available bytes without having started the read thread first.</p>
	 */
	@Test(expected=IOException.class)
	public void testAvailableThreadNull() throws IOException {
		// Call the method that should throw the exception.
		is.available();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBInputStream#available()}
	 */
	@Test
	public void testAvailable() throws IOException {
		// Set up the resources for the test.
		Mockito.when(circularBuffer.availableToRead()).thenReturn(DATA.length);
		Whitebox.setInternalState(is, VARIABLE_READ_BUFFER, circularBuffer);

		// Perform the verifications.
		assertThat(is.available(), is(equalTo(DATA.length)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBInputStream#skip(long)}
	 *
	 * <p>Verify that an {@code IOException} is thrown when skipping some bytes
	 * without having started the read thread first.</p>
	 */
	@Test(expected=IOException.class)
	public void testSkipThreadNull() throws IOException {
		// Call the method that should throw the exception.
		is.skip(1);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBInputStream#skip(long)}
	 */
	@Test
	public void testSkip() throws IOException {
		// Set up the resources for the test.
		int skipped = 1;
		Mockito.when(circularBuffer.skip(Mockito.anyInt())).thenReturn(skipped);
		Whitebox.setInternalState(is, VARIABLE_READ_BUFFER, circularBuffer);

		// Perform the verifications.
		assertThat(is.skip(Mockito.anyLong()), is(equalTo((long) skipped)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBInputStream#startReadThread()}
	 *
	 * <p>Verify that the {@code startReadThread()} method does nothing if the
	 * thread is already started.</p>
	 */
	@Test
	public void testStartReadThreadAlreadyWorking() throws Exception {
		// Set up the resources for the test.
		Whitebox.setInternalState(is, VARIABLE_WORKING, true);
		Whitebox.setInternalState(is, VARIABLE_READ_BUFFER, circularBuffer);

		// Call the method under test.
		is.startReadThread();

		CircularByteBuffer cbb = (CircularByteBuffer) Whitebox.getInternalState(is, VARIABLE_READ_BUFFER);

		// Perform the verifications.
		assertThat(cbb, is(equalTo(circularBuffer)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBInputStream#startReadThread()}
	 */
	@Test
	public void testStartReadThread() throws Exception {
		// Prepare the resources for the test.
		Whitebox.setInternalState(is, VARIABLE_READ_BUFFER, circularBuffer);

		// Call the method under test.
		is.startReadThread();

		Thread.sleep(10);

		boolean working = (Boolean) Whitebox.getInternalState(is, VARIABLE_WORKING);

		// Perform the verifications.
		assertThat(working, is(equalTo(true)));
		Mockito.verify(usbConnection, Mockito.atLeast(1)).bulkTransfer(Mockito.any(UsbEndpoint.class),
				Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());

		is.stopReadThread();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBInputStream#stopReadThread()}
	 *
	 * <p>Verify the method to stop the read thread when it is null.</p>
	 */
	@Test
	public void testStopReadThreadReceiveThreadNull() {
		// Call the method under test.
		is.stopReadThread();

		boolean working = (Boolean) Whitebox.getInternalState(is, VARIABLE_WORKING);

		// Perform the verifications.
		assertThat(working, is(equalTo(false)));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.AndroidUSBInputStream#stopReadThread()}
	 *
	 * <p>Verify the method to stop the read thread when it is not null.</p>
	 */
	@Test
	public void testStopReadThread() throws Exception {
		// Set up the resources for the test.
		is.startReadThread();

		Thread.sleep(10);

		// Call the method under test.
		is.stopReadThread();

		boolean working = (Boolean) Whitebox.getInternalState(is, VARIABLE_WORKING);

		// Perform the verifications.
		assertThat(working, is(equalTo(false)));
	}

}
