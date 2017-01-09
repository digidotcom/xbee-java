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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.exceptions.InterfaceInUseException;
import com.digi.xbee.api.exceptions.InvalidConfigurationException;
import com.digi.xbee.api.exceptions.InvalidInterfaceException;
import com.digi.xbee.api.exceptions.PermissionDeniedException;

/**
 * @since 1.2.0
 */
public class AndroidXBeeInterface implements IConnectionInterface {

	// Constants.
	private static final int VID = 0x0403;
	private static final int PID = 0x6001;
	private static final int BASE_CLOCK = 48000000;

	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

	// Variables.
	private UsbDevice usbDevice;

	private UsbDeviceConnection usbConnection;

	private UsbInterface usbInterface;

	private UsbEndpoint receiveEndPoint;
	private UsbEndpoint sendEndPoint;

	private PendingIntent mPermissionIntent;

	private UsbManager usbManager;

	private AndroidUSBInputStream inputStream;

	private AndroidUSBOutputStream outputStream;

	private boolean isConnected = false;
	private boolean permissionsReceived = false;
	private boolean permissionsGranted = false;

	private Context context;

	private int baudRate;
	
	private AndroidUSBPermissionListener permissionListener;
	
	private Logger logger;

	/**
	 * Class constructor. Instantiates a new {@code AndroidXBeeInterface} object
	 * with the given parameters.
	 * 
	 * <p>This constructor requires that methods calling {@link #open()}
	 * method to be executed in a thread different than the UI to avoid hangs
	 * while waiting for USB device permissions.</p>
	 * 
	 * @param context The Android context.
	 * @param baudRate Device baud rate to use.
	 * @param usbDevice USB device to use, may be {@code null}
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null}.
	 * 
	 * @see #AndroidXBeeInterface(Context, int)
	 * @see #AndroidXBeeInterface(Context, int, AndroidUSBPermissionListener)
	 * @see #AndroidXBeeInterface(Context, int, UsbDevice, AndroidUSBPermissionListener)
	 * @see android.hardware.usb.UsbDevice
	 */
	public AndroidXBeeInterface(Context context, int baudRate, UsbDevice usbDevice) {
		this(context, baudRate, usbDevice, null);
	}

	/**
	 * Class constructor. Instantiates a new {@code AndroidXBeeInterface} object
	 * with the given parameters.
	 * 
	 * <p>This constructor requires all methods calling {@link #open()} 
	 * method to be executed in a thread different than the UI to avoid hangs
	 * while waiting for USB device permissions.</p>
	 * 
	 * @param context The Android context.
	 * @param baudRate Device baud rate to use.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null}.
	 * 
	 * @see #AndroidXBeeInterface(Context, int, AndroidUSBPermissionListener)
	 * @see #AndroidXBeeInterface(Context, int, UsbDevice)
	 * @see #AndroidXBeeInterface(Context, int, UsbDevice, AndroidUSBPermissionListener)
	 */
	public AndroidXBeeInterface(Context context, int baudRate) {
		this(context, baudRate, null, null);
	}

	/**
	 * Class constructor. Instantiates a new {@code AndroidXBeeInterface} object
	 * with the given parameters.
	 * 
	 * @param context The Android context.
	 * @param baudRate Device baud rate to use.
	 * @param permissionListener Android USB permission listener to be notified 
	 *                           when access to USB device is granted, may be
	 *                           {@code null}.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null}.
	 * 
	 * @see #AndroidXBeeInterface(Context, int)
	 * @see #AndroidXBeeInterface(Context, int, UsbDevice)
	 * @see #AndroidXBeeInterface(Context, int, UsbDevice, AndroidUSBPermissionListener)
	 * @see AndroidUSBPermissionListener
	 */
	public AndroidXBeeInterface(Context context, int baudRate, AndroidUSBPermissionListener permissionListener) {
		this(context, baudRate, null, permissionListener);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code AndroidXBeeInterface} object
	 * with the given parameters.
	 * 
	 * @param context The Android context.
	 * @param baudRate Device baud rate to use.
	 * @param usbDevice USB device to use, may be {@code null}.
	 * @param permissionListener Android USB permission listener to be notified 
	 *                           when access to USB device is granted, may be 
	 *                           {@code null}.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null}.
	 * 
	 * @see #AndroidXBeeInterface(Context, int)
	 * @see #AndroidXBeeInterface(Context, int, AndroidUSBPermissionListener)
	 * @see #AndroidXBeeInterface(Context, int, UsbDevice)
	 * @see AndroidUSBPermissionListener
	 * @see android.hardware.usb.UsbDevice
	 */
	public AndroidXBeeInterface(Context context, int baudRate, UsbDevice usbDevice, AndroidUSBPermissionListener permissionListener) {
		if (context == null)
			throw new NullPointerException("Android contex cannot be null.");
		if (baudRate < 1)
			throw new IllegalArgumentException("Baud rate must be greater than 0.");
		
		this.context = context;
		this.baudRate = baudRate;
		this.permissionListener = permissionListener;
		this.usbDevice = usbDevice;
		this.usbManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
		this.logger = LoggerFactory.getLogger(AndroidXBeeInterface.class);
	}

	/**
	 * Calculates a valid divisor for the given baud rate and base clock for
	 * the FT232BM, FT2232C and FT232LR chips.
	 * 
	 * @param baud Desired baud rate. 
	 * @param base The base clock.
	 * 
	 * @return The calculated divisor.
	 */
	private int calculateBaudRate(int baud, int base) {
		int divisor;
		divisor = (base / 16 / baud) | (((base / 2 / baud) & 4) != 0 ? 0x4000 // 0.5
				: ((base / 2 / baud) & 2) != 0 ? 0x8000 // 0.25
						: ((base / 2 / baud) & 1) != 0 ? 0xc000 // 0.125
								: 0);
		return divisor;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#open()
	 */
	@Override
	public void open() throws InterfaceInUseException,
			InvalidInterfaceException, InvalidConfigurationException,
			PermissionDeniedException {
		// Reset variables.
		permissionsReceived = false;
		permissionsGranted = false;
		// Look for a compatible USB device.
		if (usbDevice == null)
			usbDevice = findDevice();
		if (usbDevice == null)
			throw new InvalidInterfaceException("XBee USB device not found");
		// Check USB device permissions.
		if (!usbManager.hasPermission(usbDevice)) {
			mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
			IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
			context.registerReceiver(mUsbReceiver, filter);
			usbManager.requestPermission(usbDevice, mPermissionIntent);
			if (permissionListener == null) {
				// This should be called when connecting from a thread different than the UI.
				while (!permissionsReceived) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				}
				if (!permissionsGranted)
					throw new PermissionDeniedException("User didn't grant permissions to access XBee device.");
			} else {
				// Main application is waiting now to the permission receiver listener. We have to exit here, main 
				// application should try to reconnect again when permissions are granted. At this point we do not have
				// permissions yet.
				throw new PermissionDeniedException("User didn't grant permissions to access XBee device.");
			}
		}
		// Start the USB connection.
		startUSBConnection();
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#close()
	 */
	@Override
	public void close() {
		// Not connected
		isConnected = false;
		// Stop the read thread.
		if (inputStream != null)
			inputStream.stopReadThread();
		// Release the streams.
		inputStream = null;
		outputStream = null;
		// Release end points.
		receiveEndPoint = null;
		sendEndPoint = null;
		// Disconnect USB connection
		if (usbConnection != null) {
			usbConnection.releaseInterface(usbInterface);
			usbConnection.close();
		}
		// Release USB connection and USB interface.
		usbConnection = null;
		usbInterface = null;
		usbDevice = null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#isOpen()
	 */
	@Override
	public boolean isOpen() {
		return isConnected;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#writeData(byte[])
	 */
	@Override
	public void writeData(byte[] data) throws IOException {
		if (data == null)
			throw new NullPointerException("Data to be sent cannot be null.");
		
		if (getOutputStream() != null) {
			getOutputStream().write(data);
			getOutputStream().flush();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#writeData(byte[], int, int)
	 */
	@Override
	public void writeData(byte[] data, int offset, int length)
			throws IOException {
		if (data == null)
			throw new NullPointerException("Data to be sent cannot be null.");
		if (offset < 0)
			throw new IllegalArgumentException("Offset cannot be less than 0.");
		if (length < 1)
			throw new IllegalArgumentException("Length cannot be less than 0.");
		if (offset >= data.length)
			throw new IllegalArgumentException("Offset must be less than the data length.");
		if (offset + length > data.length)
			throw new IllegalArgumentException("Offset + length cannot be great than the data length.");
		
		if (getOutputStream() != null) {
			getOutputStream().write(data, offset, length);
			getOutputStream().flush();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#readData(byte[])
	 */
	@Override
	public int readData(byte[] data) throws IOException {
		if (data == null)
			throw new NullPointerException("Buffer cannot be null.");
		
		int readBytes = 0;
		if (getInputStream() != null)
			readBytes = getInputStream().read(data);
		return readBytes;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#readData(byte[], int, int)
	 */
	@Override
	public int readData(byte[] data, int offset, int length) throws IOException {
		if (data == null)
			throw new NullPointerException("Buffer cannot be null.");
		if (offset < 0)
			throw new IllegalArgumentException("Offset cannot be less than 0.");
		if (length < 1)
			throw new IllegalArgumentException("Length cannot be less than 0.");
		if (offset >= data.length)
			throw new IllegalArgumentException("Offset must be less than the buffer length.");
		if (offset + length > data.length)
			throw new IllegalArgumentException("Offset + length cannot be great than the buffer length.");
		
		int readBytes = 0;
		if (getInputStream() != null)
			readBytes = getInputStream().read(data, offset, length);
		return readBytes;
	}
	
	/**
	 * Looks for a compatible USB device to use as XBee device.
	 * 
	 * @return The USB device found, {@code null} if no compatible device is
	 *         found.
	 */
	private UsbDevice findDevice() {
		UsbDevice usbDevice = null;
		HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
		for (UsbDevice device:deviceList.values()) {
			if ((device.getProductId() == PID) && (device.getVendorId() == VID)) {
				usbDevice = device;
				logger.info("USB XBee Android device found: " + usbDevice.getDeviceName());
				break;
			}
		}
		return usbDevice;
	}

	/**
	 * Starts the USB device connection.
	 * 
	 * @throws InterfaceInUseException if there is an error claiming the USB
	 *                                 interface.
	 */
	private void startUSBConnection() throws InterfaceInUseException {
		// Create the USB connection.
		if (usbConnection == null)
			usbConnection = usbManager.openDevice(usbDevice);
		// Create the USB interface.
		if (usbInterface == null)
			usbInterface = usbDevice.getInterface(0);
		// Claim USB interface.
		if (!usbConnection.claimInterface(usbInterface, true)) 
			throw new InterfaceInUseException("Could not get control of USB interface, it may be in use by other applications.");
		// Configure the USB end points.
		for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
			if (usbInterface.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
				if (usbInterface.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN)
					receiveEndPoint = usbInterface.getEndpoint(i);	
				else
					sendEndPoint = usbInterface.getEndpoint(i);
			} 
		}
		// Configure USB baud rate.
		usbConnection.controlTransfer(0x40, 0x03, calculateBaudRate(baudRate, BASE_CLOCK), 0, null, 0, 0);
		// Instantiate input stream and output stream.
		inputStream = new AndroidUSBInputStream(this, receiveEndPoint, usbConnection);
		outputStream = new AndroidUSBOutputStream(sendEndPoint, usbConnection);
		// Start the read thread.
		inputStream.startReadThread();
		// Connection finished.
		isConnected = true;
	}
	
	/**
	 * Helper class used to listen for USB permission intents in Android.
	 */
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		/*
		 * (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
				usbDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				context.unregisterReceiver(mUsbReceiver);
				if (permissionListener == null) {
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
						permissionsGranted = true;
					permissionsReceived = true;
				} else
					permissionListener.permissionReceived(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false));
			}
		}
	};
}
