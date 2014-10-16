package com.digi.xbee.api.connection.android;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.utils.HexUtils;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;

/**
 * This class acts as a wrapper to read data from the USB Interface in Android and
 * behaves like an {@code InputputStream} class.
 */
public class AndroidUSBInputStream extends InputStream {

	// Constants.
	private static final int READ_BUFFER_SIZE = 1024;
	private static final int OFFSET = 2;
	private static final int READ_TIMEOUT = 100;

	// Variables.
	private UsbDeviceConnection usbConnection;

	private UsbEndpoint receiveEndPoint;

	private boolean working = false;

	private Thread receiveThread;

	private CircularByteBuffer readBuffer;

	private AndroidXBeeInterface androidInterface;

	private Logger logger;
	
	/**
	 * Class constructor. Instances a new {@code AndroidUSBInputStream} object with the given
	 * parameters.
	 * 
	 * @param androidInterface The XBee Android interface to which the stream belongs to.
	 * @param readEndpoint The USB end point to use to read data from.
	 * @param connection The USB connection to use to read data from.
	 */
	public AndroidUSBInputStream(AndroidXBeeInterface androidInterface, UsbEndpoint readEndpoint, UsbDeviceConnection connection) {
		this.usbConnection = connection;
		this.receiveEndPoint = readEndpoint;
		this.androidInterface = androidInterface;
		this.logger = LoggerFactory.getLogger(AndroidUSBInputStream.class);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		byte[] buffer = new byte[1];
		read(buffer);
		return buffer[0] & 0xFF;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] buffer, int offset, int length) throws IOException {
		long deadLine = new Date().getTime() + READ_TIMEOUT;
		int readBytes = 0;
		while (new Date().getTime() < deadLine && readBytes <= 0)
			readBytes = readBuffer.read(buffer, offset, length);
		if (readBytes <= 0)
			return -1;
		byte[] readData = new byte[readBytes];
		System.arraycopy(buffer, offset, readData, 0, readBytes);
		logger.debug("Received a read request of " + length + " bytes, returning " + readData.length + ": " + HexUtils.byteArrayToHexString(readData));
		return readBytes;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		return readBuffer.availableToRead();
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(long byteCount) throws IOException {
		return readBuffer.skip((int)byteCount);
	}

	/**
	 * Starts the USB input stream read thread to start reading from the USB connection.
	 */
	public void startReadThread() {
		readBuffer = new CircularByteBuffer(READ_BUFFER_SIZE);
		receiveThread = new Thread() {
			public void run() {
				working = true;
				while (working) {
					byte[] buffer = new byte[1024];
					int receivedBytes = usbConnection.bulkTransfer(receiveEndPoint, buffer, buffer.length, READ_TIMEOUT) - OFFSET;
					if (receivedBytes > 0) {
						byte[] data = new byte[receivedBytes];
						System.arraycopy(buffer, OFFSET, data, 0, receivedBytes);
						logger.debug("Message received: " + HexUtils.byteArrayToHexString(data));
						readBuffer.write(buffer, OFFSET, receivedBytes);
						// Notify interface so that XBee Reader is notified about data available.
						synchronized (androidInterface) {
							androidInterface.notify();
						}
					}
				}
			};
		};
		receiveThread.start();
	}

	/**
	 * Stops the USB input stream read thread.
	 */
	public void stopReadThread() {
		working = false;
		if (receiveThread != null)
			receiveThread.interrupt();
	}
}
