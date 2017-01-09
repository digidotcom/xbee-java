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

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;

import static org.junit.Assert.*;

public class CircularByteBufferReadTest {
	
	// Constants.
	private final static int CIRCULAR_BYTE_BUFFER_SIZE = 100;
	private final static int TEST_DATA_SIZE = 75;
	
	private final static String VARIABLE_READ_INDEX = "readIndex";
	private final static String VARIABLE_WRITE_INDEX = "writeIndex";
	private final static String VARIABLE_EMPTY = "empty";
	
	// Variables.
	private CircularByteBuffer circularByteBuffer;
	
	private static byte[] dataToWrite;
	static {
		dataToWrite = new byte[TEST_DATA_SIZE];
		for (int i = 0; i < TEST_DATA_SIZE; i++)
			dataToWrite[i] = (byte)i;
	}
	
	@Before
	public void setup() {
		// Spy the circular byte buffer.
		circularByteBuffer = PowerMockito.spy(new CircularByteBuffer(CIRCULAR_BYTE_BUFFER_SIZE));
		// Write test data in the buffer.
		circularByteBuffer.write(dataToWrite, 0, TEST_DATA_SIZE);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#read(byte[], int, int)}.
	 * 
	 * <p>Verify that when reading using a null data buffer, a {@code NullPointerException} is thrown.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testReadWithNullData() {
		circularByteBuffer.read(null, 0, 1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#read(byte[], int, int)}.
	 * 
	 * <p>Verify that when reading using an invalid offset, an {@code IllegalArgumentException} is thrown.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testReadWithInvalidOffset() {
		circularByteBuffer.read(new byte[]{0x00}, -1, 1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#read(byte[], int, int)}.
	 * 
	 * <p>Verify that when reading data using an invalid number of bytes, an {@code IllegalArgumentException} is thrown.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testReadWithInvalidNumBytes() {
		circularByteBuffer.read(new byte[]{0x00}, 0, 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#read(byte[], int, int)}.
	 * 
	 * <p>Verify that when reading from an empty buffer, no data is received.</p>
	 */
	@Test
	public void testReadEmptyBuffer() {
		// Create variables for test.
		byte[] data = new byte[TEST_DATA_SIZE];
		
		// Clear the buffer.
		circularByteBuffer.clearBuffer();
		assertEquals(0, circularByteBuffer.availableToRead());
		// Read data.
		int readBytes = circularByteBuffer.read(data, 0, TEST_DATA_SIZE);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(0, readBytes);
		assertEquals(0, readIndex);
		assertEquals(0, writeIndex);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#read(byte[], int, int)}.
	 * 
	 * <p>Verify that we can read some bytes from the buffer.</p>
	 */
	@Test
	public void testReadSomeBytes() {
		// Create variables for test.
		int bytesToRead = 30;
		byte[] data = new byte[bytesToRead];

		// Read data.
		int readBytes = circularByteBuffer.read(data, 0, bytesToRead);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(bytesToRead, readBytes);
		assertEquals(TEST_DATA_SIZE - bytesToRead, circularByteBuffer.availableToRead());
		assertEquals(bytesToRead, readIndex);
		assertEquals(TEST_DATA_SIZE, writeIndex);
		for (int i = 0; i < readBytes; i++)
			assertEquals(dataToWrite[i], data[i]);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#read(byte[], int, int)}.
	 * 
	 * <p>Verify that we can read some bytes from the buffer.</p>
	 */
	@Test
	public void testReadAllBytes() {
		// Create variables for test.
		byte[] data = new byte[TEST_DATA_SIZE];

		// Read data.
		int readBytes = circularByteBuffer.read(data, 0, TEST_DATA_SIZE);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(TEST_DATA_SIZE, readBytes);
		assertEquals(0, circularByteBuffer.availableToRead());
		assertEquals(TEST_DATA_SIZE, readIndex);
		assertEquals(TEST_DATA_SIZE, writeIndex);
		for (int i = 0; i < readBytes; i++)
			assertEquals(dataToWrite[i], data[i]);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#read(byte[], int, int)}.
	 * 
	 * <p>Verify that we can read some bytes from the buffer.</p>
	 */
	@Test
	public void testReadMoreBytesThanAvailable() {
		// Create variables for test.
		int bytesToRead = TEST_DATA_SIZE + 1;
		byte[] data = new byte[bytesToRead];

		// Read data.
		int readBytes = circularByteBuffer.read(data, 0, bytesToRead);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(TEST_DATA_SIZE, readBytes);
		assertEquals(0, circularByteBuffer.availableToRead());
		assertEquals(TEST_DATA_SIZE, readIndex);
		assertEquals(TEST_DATA_SIZE, writeIndex);
		for (int i = 0; i < readBytes; i++)
			assertEquals(dataToWrite[i], data[i]);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#read(byte[], int, int)}.
	 * 
	 * <p>Verify that we cannot read more bytes than the size of the passed buffer.</p>
	 */
	@Test
	public void testReadMoreBytesThanPassedBufferSize() {
		// Create variables for test.
		int bytesToRead = 30;
		byte[] data = new byte[bytesToRead];

		// Read data.
		int readBytes = circularByteBuffer.read(data, 0, bytesToRead + 1);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(bytesToRead, readBytes);
		assertEquals(TEST_DATA_SIZE - bytesToRead, circularByteBuffer.availableToRead());
		assertEquals(bytesToRead, readIndex);
		assertEquals(TEST_DATA_SIZE, writeIndex);
		for (int i = 0; i < readBytes; i++)
			assertEquals(dataToWrite[i], data[i]);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#read(byte[], int, int)}.
	 * 
	 * <p>Verify that we can read some bytes from the buffer and store them with an offset.</p>
	 */
	@Test
	public void testReadSomeBytesWithOffset() {
		// Create variables for test.
		int offset = 10;
		int bytesToRead = 30;
		byte[] data = new byte[bytesToRead];

		// Read data.
		int readBytes = circularByteBuffer.read(data, offset, bytesToRead);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(bytesToRead - offset, readBytes);
		assertEquals(TEST_DATA_SIZE - bytesToRead + offset, circularByteBuffer.availableToRead());
		assertEquals(bytesToRead - offset, readIndex);
		assertEquals(TEST_DATA_SIZE, writeIndex);
		for (int i = 0; i < readBytes; i++)
			assertEquals(dataToWrite[i], data[i + offset]);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#read(byte[], int, int)}.
	 * 
	 * <p>Verify that we cannot read data when the offset to store them is bigger than the passed buffer size.</p>
	 */
	@Test
	public void testReadSomeBytesWithOffsetBiggerThanSize() {
		// Create variables for test.
		int offset = 40;
		int bytesToRead = 30;
		byte[] data = new byte[bytesToRead];

		// Read data.
		int readBytes = circularByteBuffer.read(data, offset, bytesToRead);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(0, readBytes);
		assertEquals(TEST_DATA_SIZE, circularByteBuffer.availableToRead());
		assertEquals(0, readIndex);
		assertEquals(TEST_DATA_SIZE, writeIndex);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#read(byte[], int, int)}.
	 * 
	 * <p>Verify that we can read data when there has been an overflow in the buffer.</p>
	 */
	@Test
	public void testReadWithOverflow() {
		// Create variables for test.
		int bytesToRead = 60;
		byte[] data = new byte[bytesToRead];
		byte[] datatoWrite = new byte[TEST_DATA_SIZE];
		for (int i = 0; i < TEST_DATA_SIZE; i++)
			datatoWrite[i] = (byte)i;
		int totalBytesWritten = TEST_DATA_SIZE + TEST_DATA_SIZE;

		// Write more data to force overflow.
		int writtenBytes = circularByteBuffer.write(datatoWrite, 0, TEST_DATA_SIZE);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		int expectedReadIndex = totalBytesWritten;
		while (expectedReadIndex > circularByteBuffer.getCapacity())
			expectedReadIndex = expectedReadIndex - circularByteBuffer.getCapacity();
		int expectedWriteIndex = totalBytesWritten;
		while (expectedWriteIndex > circularByteBuffer.getCapacity())
			expectedWriteIndex = expectedWriteIndex - circularByteBuffer.getCapacity();
		assertEquals(TEST_DATA_SIZE, writtenBytes);
		assertEquals(circularByteBuffer.getCapacity(), circularByteBuffer.availableToRead());
		assertEquals(expectedReadIndex, readIndex);
		assertEquals(expectedWriteIndex, writeIndex);
		
		// Read data.
		int readBytes = circularByteBuffer.read(data, 0, bytesToRead);
		readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		expectedReadIndex = totalBytesWritten + bytesToRead;
		while (expectedReadIndex > circularByteBuffer.getCapacity())
			expectedReadIndex = expectedReadIndex - circularByteBuffer.getCapacity();
		expectedWriteIndex = totalBytesWritten;
		while (expectedWriteIndex > circularByteBuffer.getCapacity())
			expectedWriteIndex = expectedWriteIndex - circularByteBuffer.getCapacity();
		assertEquals(bytesToRead, readBytes);
		assertEquals(circularByteBuffer.getCapacity() - bytesToRead, circularByteBuffer.availableToRead());
		assertEquals(expectedReadIndex, readIndex);
		assertEquals(expectedWriteIndex, writeIndex);
		
		int offset = totalBytesWritten;
		while (offset > circularByteBuffer.getCapacity())
			offset = offset - circularByteBuffer.getCapacity();
		for (int i = 0; i < readBytes; i++) {
			int dataIndex = offset + i;
			if (dataIndex >= TEST_DATA_SIZE)
				dataIndex = dataIndex - TEST_DATA_SIZE;
			assertEquals(dataToWrite[dataIndex], data[i]);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#read(byte[], int, int)}.
	 * 
	 * <p>Verify that we can read all data when there has been an overflow in the buffer.</p>
	 */
	@Test
	public void testReadAllDataWithOverflow() {
		// Create variables for test.
		byte[] data = new byte[CIRCULAR_BYTE_BUFFER_SIZE];
		byte[] datatoWrite = new byte[TEST_DATA_SIZE];
		for (int i = 0; i < TEST_DATA_SIZE; i++)
			datatoWrite[i] = (byte)i;
		int totalBytesWritten = TEST_DATA_SIZE + TEST_DATA_SIZE;

		// Write more data to force overflow.
		int writtenBytes = circularByteBuffer.write(datatoWrite, 0, TEST_DATA_SIZE);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		int expectedReadIndex = totalBytesWritten;
		while (expectedReadIndex > circularByteBuffer.getCapacity())
			expectedReadIndex = expectedReadIndex - circularByteBuffer.getCapacity();
		int expectedWriteIndex = totalBytesWritten;
		while (expectedWriteIndex > circularByteBuffer.getCapacity())
			expectedWriteIndex = expectedWriteIndex - circularByteBuffer.getCapacity();
		assertEquals(TEST_DATA_SIZE, writtenBytes);
		assertEquals(circularByteBuffer.getCapacity(), circularByteBuffer.availableToRead());
		assertEquals(expectedReadIndex, readIndex);
		assertEquals(expectedWriteIndex, writeIndex);
		
		// Read data.
		int readBytes = circularByteBuffer.read(data, 0, CIRCULAR_BYTE_BUFFER_SIZE);
		readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		expectedReadIndex = totalBytesWritten + CIRCULAR_BYTE_BUFFER_SIZE;
		while (expectedReadIndex > circularByteBuffer.getCapacity())
			expectedReadIndex = expectedReadIndex - circularByteBuffer.getCapacity();
		expectedWriteIndex = totalBytesWritten;
		while (expectedWriteIndex > circularByteBuffer.getCapacity())
			expectedWriteIndex = expectedWriteIndex - circularByteBuffer.getCapacity();
		assertEquals(CIRCULAR_BYTE_BUFFER_SIZE, readBytes);
		assertEquals(0, circularByteBuffer.availableToRead());
		assertEquals(expectedReadIndex, readIndex);
		assertEquals(expectedWriteIndex, writeIndex);
		
		int offset = totalBytesWritten;
		while (offset > circularByteBuffer.getCapacity())
			offset = offset - circularByteBuffer.getCapacity();
		for (int i = 0; i < readBytes; i++) {
			int dataIndex = offset + i;
			if (dataIndex >= TEST_DATA_SIZE)
				dataIndex = dataIndex - TEST_DATA_SIZE;
			assertEquals(dataToWrite[dataIndex], data[i]);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#skip(int)}.
	 * 
	 * <p>Verify that when skipping an invalid number of bytes, an {@code IllegalArgumentException} is thrown.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSkipInvalidNumBytes() {
		circularByteBuffer.skip(0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#skip(int)}.
	 * 
	 * <p>Verify that the method returns 0 when the buffer is empty.</p>
	 */
	@Test
	public void testSkipEmpty() {
		// Create variables for test.
		int bytesToSkip = 2;
		
		// Set internal state.
		Whitebox.setInternalState(circularByteBuffer, VARIABLE_EMPTY, true);
		
		// Perform verifications.
		assertEquals(circularByteBuffer.skip(bytesToSkip), 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#skip(int)}.
	 * 
	 * <p>Verify that we can skip some bytes.</p>
	 */
	@Test
	public void testSkipSomeBytes() {
		// Create variables for test.
		int bytesToSkip = 75;
		
		// Skip bytes.
		circularByteBuffer.skip(bytesToSkip);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(TEST_DATA_SIZE - bytesToSkip, circularByteBuffer.availableToRead());
		assertEquals(bytesToSkip, readIndex);
		assertEquals(TEST_DATA_SIZE, writeIndex);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#skip(int)}.
	 * 
	 * <p>Verify that we cannot skip more bytes that available in the buffer.</p>
	 */
	@Test
	public void testSkipMoreBytesThanAvailable() {
		// Create variables for test.
		int bytesToSkip = TEST_DATA_SIZE + 1;
		
		// Skip bytes.
		int skipedBytes = circularByteBuffer.skip(bytesToSkip);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(TEST_DATA_SIZE, skipedBytes);
		assertEquals(0, circularByteBuffer.availableToRead());
		assertEquals(TEST_DATA_SIZE, readIndex);
		assertEquals(TEST_DATA_SIZE, writeIndex);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#skip(int)}.
	 * 
	 * <p>Verify that we can skip more bytes than the buffer.</p>
	 */
	@Test
	public void testSkipMoreBytesThanBuffer() {
		// Create variables for test.
		int bytesToSkip = 20;
		int newReadIndex = 90;
		
		Whitebox.setInternalState(circularByteBuffer, VARIABLE_READ_INDEX, newReadIndex);
		
		// Skip bytes.
		int skipped = circularByteBuffer.skip(bytesToSkip);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		
		// Perform verifications.
		assertEquals(skipped, bytesToSkip);
		assertEquals(readIndex, CIRCULAR_BYTE_BUFFER_SIZE - newReadIndex);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#read(byte[], int, int)}.
	 * 
	 * <p>Verify that we can skip some bytes and continue reading.</p>
	 */
	@Test
	public void testSkipSomeBytesAndReadSomeData() {
		// Create variables for test.
		int bytesToRead = 30;
		byte[] data = new byte[bytesToRead];
		int bytesToSkip = 30;
		
		// Skip bytes.
		int skipedBytes = circularByteBuffer.skip(bytesToSkip);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(bytesToSkip, skipedBytes);
		assertEquals(TEST_DATA_SIZE - bytesToSkip, circularByteBuffer.availableToRead());
		assertEquals(bytesToSkip, readIndex);
		assertEquals(TEST_DATA_SIZE, writeIndex);
		
		// Read data.
		int readBytes = circularByteBuffer.read(data, 0, bytesToRead);
		readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(bytesToRead, readBytes);
		assertEquals(TEST_DATA_SIZE - bytesToSkip - bytesToRead, circularByteBuffer.availableToRead());
		assertEquals(bytesToSkip + bytesToRead, readIndex);
		assertEquals(TEST_DATA_SIZE, writeIndex);
		for (int i = 0; i < readBytes; i++)
			assertEquals(dataToWrite[i + bytesToSkip], data[i]);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#read(byte[], int, int)}.
	 * 
	 * <p>Verify that we can skip some bytes and read all the data.</p>
	 */
	@Test
	public void testSkipSomeBytesAndReadAllData() {
		// Create variables for test.
		byte[] data = new byte[TEST_DATA_SIZE];
		int bytesToSkip = 30;
		
		// Skip bytes.
		int skipedBytes = circularByteBuffer.skip(bytesToSkip);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(bytesToSkip, skipedBytes);
		assertEquals(TEST_DATA_SIZE - bytesToSkip, circularByteBuffer.availableToRead());
		assertEquals(bytesToSkip, readIndex);
		assertEquals(TEST_DATA_SIZE, writeIndex);
		
		// Read data.
		int readBytes = circularByteBuffer.read(data, 0, TEST_DATA_SIZE);
		readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(TEST_DATA_SIZE - bytesToSkip, readBytes);
		assertEquals(0, circularByteBuffer.availableToRead());
		assertEquals(TEST_DATA_SIZE, readIndex);
		assertEquals(TEST_DATA_SIZE, writeIndex);
		for (int i = 0; i < readBytes; i++)
			assertEquals(dataToWrite[i + bytesToSkip], data[i]);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#read(byte[], int, int)}.
	 * 
	 * <p>Verify that we can skip all bytes and after that we cannot read more bytes.</p>
	 */
	@Test
	public void testSkipAllBytesAndReadData() {
		// Create variables for test.
		byte[] data = new byte[TEST_DATA_SIZE];
		int bytesToSkip = TEST_DATA_SIZE;
		
		// Skip bytes.
		int skipedBytes = circularByteBuffer.skip(bytesToSkip);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(bytesToSkip, skipedBytes);
		assertEquals(TEST_DATA_SIZE - bytesToSkip, circularByteBuffer.availableToRead());
		assertEquals(bytesToSkip, readIndex);
		assertEquals(TEST_DATA_SIZE, writeIndex);
		
		// Read data.
		int readBytes = circularByteBuffer.read(data, 0, TEST_DATA_SIZE);
		readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(0, readBytes);
		assertEquals(0, circularByteBuffer.availableToRead());
		assertEquals(TEST_DATA_SIZE, readIndex);
		assertEquals(TEST_DATA_SIZE, writeIndex);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#skip(int)}.
	 * 
	 * <p>Verify that we can skip some bytes when there has been a buffer overflow.</p>
	 */
	@Test
	public void testSkipSomeBytesWithOverflow() {
		// Create variables for test.
		int bytesToSkip = 30;
		byte[] datatoWrite = new byte[TEST_DATA_SIZE];
		for (int i = 0; i < TEST_DATA_SIZE; i++)
			datatoWrite[i] = (byte)i;
		int totalBytesWritten = TEST_DATA_SIZE + TEST_DATA_SIZE;

		// Write more data to force overflow.
		int writtenBytes = circularByteBuffer.write(datatoWrite, 0, TEST_DATA_SIZE);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		int expectedReadIndex = totalBytesWritten;
		while (expectedReadIndex > circularByteBuffer.getCapacity())
			expectedReadIndex = expectedReadIndex - circularByteBuffer.getCapacity();
		int expectedWriteIndex = totalBytesWritten;
		while (expectedWriteIndex > circularByteBuffer.getCapacity())
			expectedWriteIndex = expectedWriteIndex - circularByteBuffer.getCapacity();
		assertEquals(TEST_DATA_SIZE, writtenBytes);
		assertEquals(circularByteBuffer.getCapacity(), circularByteBuffer.availableToRead());
		assertEquals(expectedReadIndex, readIndex);
		assertEquals(expectedWriteIndex, writeIndex);
		
		// Skip bytes.
		circularByteBuffer.skip(bytesToSkip);
		readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		int offset = totalBytesWritten;
		while (offset > circularByteBuffer.getCapacity())
			offset = offset - circularByteBuffer.getCapacity();
		assertEquals(circularByteBuffer.getCapacity() - bytesToSkip, circularByteBuffer.availableToRead());
		assertEquals(offset + bytesToSkip, readIndex);
		assertEquals(offset, writeIndex);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#skip(int)}.
	 * 
	 * <p>Verify that we can skip some bytes and then read when there has been a buffer overflow.</p>
	 */
	@Test
	public void testSkipSomeBytesAndReadWithOverflow() {
		// Create variables for test.
		int bytesToSkip = 30;
		int bytesToRead = 60;
		byte[] data = new byte[bytesToRead];
		byte[] datatoWrite = new byte[TEST_DATA_SIZE];
		for (int i = 0; i < TEST_DATA_SIZE; i++)
			datatoWrite[i] = (byte)i;
		int totalBytesWritten = TEST_DATA_SIZE + TEST_DATA_SIZE;

		// Write more data to force overflow.
		int writtenBytes = circularByteBuffer.write(datatoWrite, 0, TEST_DATA_SIZE);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		int expectedReadIndex = totalBytesWritten;
		while (expectedReadIndex > circularByteBuffer.getCapacity())
			expectedReadIndex = expectedReadIndex - circularByteBuffer.getCapacity();
		int expectedWriteIndex = totalBytesWritten;
		while (expectedWriteIndex > circularByteBuffer.getCapacity())
			expectedWriteIndex = expectedWriteIndex - circularByteBuffer.getCapacity();
		assertEquals(TEST_DATA_SIZE, writtenBytes);
		assertEquals(circularByteBuffer.getCapacity(), circularByteBuffer.availableToRead());
		assertEquals(expectedReadIndex, readIndex);
		assertEquals(expectedWriteIndex, writeIndex);
		
		// Skip bytes.
		circularByteBuffer.skip(bytesToSkip);
		readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		int writeOffset = totalBytesWritten;
		while (writeOffset > circularByteBuffer.getCapacity())
			writeOffset = writeOffset - circularByteBuffer.getCapacity();
		int readOffset = totalBytesWritten + bytesToSkip;
		while (readOffset > circularByteBuffer.getCapacity())
			readOffset = readOffset - circularByteBuffer.getCapacity();
		assertEquals(circularByteBuffer.getCapacity() - bytesToSkip, circularByteBuffer.availableToRead());
		assertEquals(readOffset, readIndex);
		assertEquals(writeOffset, writeIndex);
		
		// Read data.
		int readBytes = circularByteBuffer.read(data, 0, bytesToRead);
		readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		writeOffset = totalBytesWritten;
		while (writeOffset > circularByteBuffer.getCapacity())
			writeOffset = writeOffset - circularByteBuffer.getCapacity();
		readOffset = totalBytesWritten + bytesToSkip + bytesToRead;
		while (readOffset > circularByteBuffer.getCapacity())
			readOffset = readOffset - circularByteBuffer.getCapacity();
		assertEquals(bytesToRead, readBytes);
		assertEquals(circularByteBuffer.getCapacity() - bytesToSkip - bytesToRead, circularByteBuffer.availableToRead());
		assertEquals(readOffset, readIndex);
		assertEquals(writeOffset, writeIndex);
		int offset = totalBytesWritten + bytesToSkip;
		while (offset > circularByteBuffer.getCapacity())
			offset = offset - circularByteBuffer.getCapacity();
		for (int i = 0; i < readBytes; i++) {
			int dataIndex = offset + i;
			if (dataIndex >= TEST_DATA_SIZE)
				dataIndex = dataIndex - TEST_DATA_SIZE;
			assertEquals(dataToWrite[dataIndex], data[i]);
		}
	}
}
