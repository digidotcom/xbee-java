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

public class CircularByteBufferWriteTest {
	
	// Constants.
	private final static int CIRCULAR_BYTE_BUFFER_SIZE = 100;
	
	private final static String VARIABLE_READ_INDEX = "readIndex";
	private final static String VARIABLE_WRITE_INDEX = "writeIndex";
	
	// Variables.
	private CircularByteBuffer circularByteBuffer;
	
	@Before
	public void setup() {
		// Spy the circular byte buffer.
		circularByteBuffer = PowerMockito.spy(new CircularByteBuffer(CIRCULAR_BYTE_BUFFER_SIZE));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#write(byte[], int, int)}.
	 * 
	 * <p>Verify that when writing null data, a {@code NullPointerException} is thrown.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testWriteNullData() {
		circularByteBuffer.write(null, 0, 1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#write(byte[], int, int)}.
	 * 
	 * <p>Verify that when writing data with invalid offset, an {@code IllegalArgumentException} is thrown.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testWriteInvalidOffset() {
		circularByteBuffer.write(new byte[]{0x00}, -1, 1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#write(byte[], int, int)}.
	 * 
	 * <p>Verify that when writing data with invalid number of bytes, an {@code IllegalArgumentException} is thrown.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testWriteInvalidNumBytes() {
		circularByteBuffer.write(new byte[]{0x00}, 0, 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#write(byte[], int, int)}.
	 * 
	 * <p>Verify that it is possible to write data with valid parameters without overflow.</p>
	 */
	@Test
	public void testWriteValidDataNoOverflow() {
		// Prepare the variables.
		byte[] data = new byte[] {0x00, 0x01, 0x02, 0x03, 0x04};
		
		// Write the data.
		int writtenBytes = circularByteBuffer.write(data, 0, data.length);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(data.length, writtenBytes);
		assertEquals(data.length, circularByteBuffer.availableToRead());
		assertEquals(data.length, writeIndex);
		assertEquals(0, readIndex);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#write(byte[], int, int)}.
	 * 
	 * <p>Verify that it is possible to write data with the size of the circular buffer.</p>
	 */
	@Test
	public void testWriteValidDataTotalSize() {
		// Prepare the variables.
		byte[] data = new byte[CIRCULAR_BYTE_BUFFER_SIZE];
		for (int i = 0; i < CIRCULAR_BYTE_BUFFER_SIZE; i++)
			data[i] = (byte)i;
		
		// Write the data.
		int writtenBytes = circularByteBuffer.write(data, 0, data.length);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(data.length, writtenBytes);
		assertEquals(data.length, circularByteBuffer.availableToRead());
		assertEquals(0, writeIndex);
		assertEquals(0, readIndex);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#write(byte[], int, int)}.
	 * 
	 * <p>Verify that it is possible to write data with more size than the circular buffer.</p>
	 */
	@Test
	public void testWriteValidDataWithOverflow() {
		// Prepare the variables.
		int overflowSize = 5;
		byte[] data = new byte[CIRCULAR_BYTE_BUFFER_SIZE + overflowSize];
		for (int i = 0; i < CIRCULAR_BYTE_BUFFER_SIZE + overflowSize; i++)
			data[i] = (byte)i;
		// We have an overflow of 5 bytes, so we will be able to write only 100 bytes (the last 100 bytes).
		
		// Write the data.
		int writtenBytes = circularByteBuffer.write(data, 0, data.length);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(circularByteBuffer.getCapacity(), writtenBytes);
		assertEquals(circularByteBuffer.getCapacity(), circularByteBuffer.availableToRead());
		assertEquals(overflowSize, writeIndex);
		assertEquals(overflowSize, readIndex);
		
		byte[] readData = new byte[writtenBytes];
		int readBytes = circularByteBuffer.read(readData, 0, writtenBytes);
		assertEquals(writtenBytes, readBytes);
		for (int i = 0; i < writtenBytes; i++)
			assertEquals(data[data.length - circularByteBuffer.getCapacity() + i], readData[i]);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#write(byte[], int, int)}.
	 * 
	 * <p>Verify that it is possible to write a fragment of data.</p>
	 */
	@Test
	public void testWriteValidFragmentData() {
		// Prepare the variables.
		int numBytes = 50;
		byte[] data = new byte[CIRCULAR_BYTE_BUFFER_SIZE];
		for (int i = 0; i < CIRCULAR_BYTE_BUFFER_SIZE; i++)
			data[i] = (byte)i;
		
		// Write the data.
		int writtenBytes = circularByteBuffer.write(data, 0, numBytes);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(numBytes, writtenBytes);
		assertEquals(numBytes, circularByteBuffer.availableToRead());
		assertEquals(numBytes, writeIndex);
		assertEquals(0, readIndex);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#write(byte[], int, int)}.
	 * 
	 * <p>Verify that it is possible to write a fragment of data with offset.</p>
	 */
	@Test
	public void testWriteValidFragmentDataAndOffset() {
		// Prepare the variables.
		int numBytes = 50;
		int offset = 25;
		byte[] data = new byte[CIRCULAR_BYTE_BUFFER_SIZE];
		for (int i = 0; i < CIRCULAR_BYTE_BUFFER_SIZE; i++)
			data[i] = (byte)i;
		
		// Write the data.
		int writtenBytes = circularByteBuffer.write(data, offset, numBytes);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(numBytes, writtenBytes);
		assertEquals(numBytes, circularByteBuffer.availableToRead());
		assertEquals(numBytes, writeIndex);
		assertEquals(0, readIndex);
		
		byte[] readData = new byte[writtenBytes];
		int readBytes = circularByteBuffer.read(readData, 0, writtenBytes);
		assertEquals(writtenBytes, readBytes);
		for (int i = 0; i < writtenBytes; i++)
			assertEquals(data[offset + i], readData[i]);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#write(byte[], int, int)}.
	 * 
	 * <p>Verify that it is possible to write a fragment of data with offset if we are trying to write
	 * more bytes that available in the array.</p>
	 */
	@Test
	public void testWriteValidFragmentDataAndOffsetMoreThanAvailable() {
		// Prepare the variables.
		int numBytes = 80;
		int offset = 30;
		byte[] data = new byte[CIRCULAR_BYTE_BUFFER_SIZE];
		for (int i = 0; i < CIRCULAR_BYTE_BUFFER_SIZE; i++)
			data[i] = (byte)i;
		// The data size is 100 bytes and we are trying to write 80 bytes starting at offset 30.
		// This means that we should be able to write only 70 bytes.
		
		// Write the data.
		int writtenBytes = circularByteBuffer.write(data, offset, numBytes);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(data.length - offset, writtenBytes);
		assertEquals(data.length - offset, circularByteBuffer.availableToRead());
		assertEquals(data.length - offset, writeIndex);
		assertEquals(0, readIndex);
		
		byte[] readData = new byte[writtenBytes];
		int readBytes = circularByteBuffer.read(readData, 0, writtenBytes);
		assertEquals(writtenBytes, readBytes);
		for (int i = 0; i < writtenBytes; i++)
			assertEquals(data[offset + i], readData[i]);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#write(byte[], int, int)}.
	 * 
	 * <p>Verify that it is possible to write a fragment of data with offset if we are trying to write
	 * more bytes that available in the array with overflow in the buffer.</p>
	 */
	@Test
	public void testWriteValidFragmentDataAndOffsetMoreThanAvailableWithOverflow() {
		// Prepare the variables.
		int numBytes = 160;
		int offset = 30;
		int overflow = 80;
		byte[] data = new byte[CIRCULAR_BYTE_BUFFER_SIZE + overflow];
		for (int i = 0; i < CIRCULAR_BYTE_BUFFER_SIZE + overflow; i++)
			data[i] = (byte)i;
		// The data size is 180 bytes and we are trying to write 160 bytes starting at offset 30.
		// This means that we should be able to write only 150 bytes. Since we have an overflow, we will
		// be able to write only the circular byte array capacity number of bytes, 100 (the last 100 bytes).
		
		// Write the data.
		int writtenBytes = circularByteBuffer.write(data, offset, numBytes);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(circularByteBuffer.getCapacity(), writtenBytes);
		assertEquals(circularByteBuffer.getCapacity(), circularByteBuffer.availableToRead());
		assertEquals(overflow - offset, writeIndex);
		assertEquals(overflow - offset, readIndex);
		
		byte[] readData = new byte[writtenBytes];
		int readBytes = circularByteBuffer.read(readData, 0, writtenBytes);
		assertEquals(writtenBytes, readBytes);
		for (int i = 0; i < writtenBytes; i++)
			assertEquals(data[data.length - circularByteBuffer.getCapacity() + i], readData[i]);
	}
}
