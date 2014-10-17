package com.digi.xbee.api.connection.android;

import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import static org.junit.Assert.*;

public class CircularByteBufferCreateTest {
	
	// Constants.
	private final static String VARIABLE_READ_INDEX = "readIndex";
	private final static String VARIABLE_WRITE_INDEX = "writeIndex";
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#CircularByteBuffer(int)}.
	 * 
	 * <p>Verify that if the size is negative, a {@code IllegalArgumentException} is thrown when
	 * creating the object.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateWithNegativeSize() {
		new CircularByteBuffer(-1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#CircularByteBuffer(int)}.
	 * 
	 * <p>Verify that if the size is zero, a {@code IllegalArgumentException} is thrown when
	 * creating the object.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateWithZeroSize() {
		new CircularByteBuffer(0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#CircularByteBuffer(int)}.
	 * 
	 * <p>Verify that the object can be created with positive size.</p>
	 */
	@Test
	public void testCreateWithValidSize() {
		CircularByteBuffer circularByteBuffer = new CircularByteBuffer(10);
		int readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		int writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertNotNull(circularByteBuffer);
		assertEquals(10, circularByteBuffer.getCapacity());
		assertEquals(0, circularByteBuffer.availableToRead());
		assertEquals(0, readIndex);
		assertEquals(0, writeIndex);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.android.CircularByteBuffer#clearBuffer()}.
	 * 
	 * <p>Verify that the buffer is cleared successfully.</p>
	 */
	@Test
	public void testClearBuffer() {
		// Prepare the variables.
		CircularByteBuffer circularByteBuffer = new CircularByteBuffer(10);
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
		
		// Clear the buffer.
		circularByteBuffer.clearBuffer();
		readIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_READ_INDEX);
		writeIndex = (Integer)Whitebox.getInternalState(circularByteBuffer, VARIABLE_WRITE_INDEX);
		
		// Perform verifications.
		assertEquals(0, circularByteBuffer.availableToRead());
		assertEquals(0, writeIndex);
		assertEquals(0, readIndex);
	}
}
