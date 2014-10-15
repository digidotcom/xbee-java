package com.digi.xbee.api.connection.android;

/**
 * Helper class used to store data bytes as a circular buffer.
 */
public class CircularByteBuffer {

	// Variables.
	private byte[] buffer;

	private int readIndex;
	private int writeIndex;

	/**
	 * Creates a circular buffer with the indicated capacity in bytes.
	 * 
	 * @param size Circular buffer size in bytes.
	 * 
	 * @throws IllegalArgumentException if {@code size < 1}.
	 */
	public CircularByteBuffer(int size) {
		if (size < 1)
			throw new IllegalArgumentException("Buffer size must be greater than 0.");
		buffer = new byte[size];
		readIndex = 0;
		writeIndex = 0;
	}

	/**
	 * Writes the given amount of bytes to the circular buffer.
	 * 
	 * @param data Bytes to write.
	 * @param offset Offset inside data to start writing bytes in.
	 * @param numBytes Number of bytes to write.
	 * @return the number of bytes actually written.
	 * 
	 * @throws NullPointerException if {@code data == null}.
	 * @throws IllegalArgumentException if {@code offset < 0} or
	 *                                  if {@code numBytes < 1}.
	 */
	public synchronized int write(byte[] data, int offset, int numBytes) {
		if (data == null)
			throw new NullPointerException("Data cannot be null.");
		if (offset < 0)
			throw new IllegalArgumentException("Offset cannot be negative.");
		if (numBytes < 1)
			throw new IllegalArgumentException("Number of bytes to write must be greater than 0.");
		
		if (numBytes < buffer.length - getWriteIndex()) {
			System.arraycopy(data, offset, buffer, getWriteIndex(), numBytes);
			writeIndex = getWriteIndex() + numBytes;
		} else {
			System.arraycopy(data, offset, buffer, getWriteIndex(), buffer.length - getWriteIndex());
			System.arraycopy(data, offset + buffer.length-getWriteIndex(), buffer, 0, numBytes - (buffer.length - getWriteIndex()));
			writeIndex = numBytes - (buffer.length-getWriteIndex());
			if (getReadIndex() < getWriteIndex())
				readIndex = getWriteIndex();
		}
		return numBytes;
	}

	/**
	 * Reads the given amount of bytes to the given array from the circular buffer.
	 * 
	 * @param data Byte buffer to place read bytes in.
	 * @param offset Offset inside data to start placing read bytes in.
	 * @param numBytes Number of bytes to read.
	 * @return The number of bytes actually read.
	 * 
	 * @throws NullPointerException if {@code data == null}.
	 * @throws IllegalArgumentException if {@code offset < 0} or
	 *                                  if {@code numBytes < 1}.
	 */
	public synchronized int read(byte[] data, int offset, int numBytes) {
		if (data == null)
			throw new NullPointerException("Data cannot be null.");
		if (offset < 0)
			throw new IllegalArgumentException("Offset cannot be negative.");
		if (numBytes < 1)
			throw new IllegalArgumentException("Number of bytes to read must be greater than 0.");
		
		if (data.length - offset < numBytes)
			return read(data, offset, data.length - offset);
		if (availableToRead() < numBytes)
			return read(data, offset, availableToRead());
		if (numBytes < buffer.length - getReadIndex()){
			System.arraycopy(buffer, getReadIndex(), data, offset, numBytes);
			readIndex = getReadIndex() + numBytes;
		} else {
			System.arraycopy(buffer, getReadIndex(), data, offset, buffer.length - getReadIndex());
			System.arraycopy(buffer, 0, data, offset + buffer.length - getReadIndex(), numBytes - (buffer.length - getReadIndex()));
			readIndex = numBytes-(buffer.length - getReadIndex());
		}
		return numBytes;
	}

	/**
	 * Skips the given number of bytes from the circular buffer.
	 * 
	 * @param numBytes Number of bytes to skip.
	 * @return the number of bytes actually skipped.
	 * 
	 * @throws IllegalArgumentException if {@code numBytes < 1}.
	 */
	public synchronized int skip(int numBytes) {
		if (numBytes < 1)
			throw new IllegalArgumentException("Number of bytes to skip must be greater than 0.");
		
		if (availableToRead() < numBytes)
			return skip(availableToRead());
		if (numBytes < buffer.length - getReadIndex())
			readIndex = getReadIndex() + numBytes;
		else
			readIndex = numBytes - (buffer.length - getReadIndex());
		return numBytes;
	}

	/**
	 * Retrieves the available number of bytes to read.
	 * 
	 * @return The number of bytes in the buffer available for reading.
	 */
	public int availableToRead() {
		if (getReadIndex() <= getWriteIndex())
			return (getWriteIndex() - getReadIndex());
		else
			return (buffer.length - getReadIndex() + getWriteIndex());
	}

	/**
	 * Retrieves the current read index.
	 * 
	 * @return readIndex The current read index.
	 */
	private int getReadIndex() {
		return readIndex;
	}

	/**
	 * Retrieves the current write index.
	 * 
	 * @return writeIndex The current write index.
	 */
	private int getWriteIndex() {
		return writeIndex;
	}
}