/*
 * Copyright 2019, Digi International Inc.
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
package com.digi.xbee.api.connection.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.exceptions.InvalidInterfaceException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * This class represents a communication interface with XBee devices over
 * Bluetooth Low Energy.
 *
 * @since 1.3.0
 */
public class BluetoothInterface extends BluetoothGattCallback implements IConnectionInterface {

	// Constants.
	private static final String SERVICE_GUID = "53DA53B9-0447-425A-B9EA-9837505EB59A";
	private static final String TX_CHAR_GUID = "7DDDCA00-3E05-4651-9254-44074792C590";
	private static final String RX_CHAR_GUID = "F9279EE9-2CD0-410C-81CC-ADF11E4E5AEA";

	private static final String AES_CIPHER = "AES/CTR/NoPadding";

	private static final int CHAR_PROP_INDICATE = 32;

	private static final int CONNECTION_TIMEOUT = 20000;
	private static final int DISCONNECTION_TIMEOUT = 10000;
	private static final int SERVICES_TIMEOUT = 10000;
	private static final int WRITE_TIMEOUT = 2000;

	private static final int RETRIES_CONNECT = 3;

	private static final int LENGTH_COUNTER = 16;

	// Variables.
	private Context context;

	private BluetoothDevice device;
	private BluetoothGatt bluetoothGatt;
	private BluetoothGattCharacteristic txCharacteristic;
	private BluetoothGattCharacteristic rxCharacteristic;

	private BlCircularByteBuffer inputByteBuffer;
	private BlCircularByteBuffer outputByteBuffer;

	private boolean isOpen = false;
	private boolean encrypt = false;
	private boolean writeTaskRunning = false;
	private boolean dataWritten = false;

	private Cipher cipherEnc;
	private Cipher cipherDec;

	private WriteTask writeTask;

	private final Object connectionLock = new Object();
	private final Object disconnectionLock = new Object();
	private final Object servicesLock = new Object();
	private final Object descriptorLock = new Object();
	private final Object writeCharLock = new Object();

	private Logger logger;

	/**
	 * Class constructor. Instantiates a new {@code BluetoothInterface}
	 * with the given parameters.
	 *
	 * @param context The Android application context.
	 * @param device The Bluetooth device to connect to.
	 *
	 * @throws NullPointerException if {@code context == null} or
	 *                              if {@code device == null}.
	 */
	public BluetoothInterface(Context context, BluetoothDevice device) {
		if (context == null)
			throw new NullPointerException("Android context cannot be null.");
		if (device == null)
			throw new NullPointerException("Bluetooth device cannot be null.");

		this.context = context;
		this.device = device;

		logger = LoggerFactory.getLogger(BluetoothInterface.class);
	}

	/**
	 * Class constructor. Instantiates a new {@code BluetoothInterface}
	 * with the given parameters.
	 *
	 * @param context The Android application context.
	 * @param deviceAddress The address of the Bluetooth device to connect to.
	 *
	 * @throws IllegalArgumentException if the device address does not follow
	 *                                  the format "00:11:22:33:AA:BB".
	 * @throws NullPointerException if {@code context == null} or
	 *                              if {@code deviceAddress == null}.
	 */
	public BluetoothInterface(Context context, String deviceAddress) {
		if (context == null)
			throw new NullPointerException("Android context cannot be null.");
		if (deviceAddress == null)
			throw new NullPointerException("Bluetooth address cannot be null.");
		if (!BluetoothAdapter.checkBluetoothAddress(deviceAddress.toUpperCase()))
			throw new IllegalArgumentException("Invalid Bluetooth address.");

		BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

		this.context = context;
		this.device = bluetoothAdapter.getRemoteDevice(deviceAddress.toUpperCase());

		logger = LoggerFactory.getLogger(BluetoothInterface.class);
	}

	@Override
	public void open() throws InvalidInterfaceException {
		// Do nothing if the device is already open.
		if (isOpen)
			return;

		// Connect the device. Try up to 3 times.
		int retries = RETRIES_CONNECT;
		while (!isOpen && retries > 0) {
			bluetoothGatt = device.connectGatt(context, false, this);
			// Wait until the device is connected.
			synchronized (connectionLock) {
				try {
					connectionLock.wait(CONNECTION_TIMEOUT);
				} catch (InterruptedException ignore) {
				}
			}
			retries -= 1;
		}

		// Check if the device is connected.
		if (!isOpen)
			throw new InvalidInterfaceException();

		// Discover the services.
		bluetoothGatt.discoverServices();
		// Wait until the services are discovered.
		synchronized (servicesLock) {
			try {
				servicesLock.wait(SERVICES_TIMEOUT);
			} catch (InterruptedException ignore) {}
		}

		// Get the TX and RX characteristics.
		BluetoothGattService service = bluetoothGatt.getService(UUID.fromString(SERVICE_GUID));
		if (service == null)
			throw new InvalidInterfaceException("Could not get the communication service");
		txCharacteristic = service.getCharacteristic(UUID.fromString(TX_CHAR_GUID));
		rxCharacteristic = service.getCharacteristic(UUID.fromString(RX_CHAR_GUID));
		if (txCharacteristic == null || rxCharacteristic == null)
			throw new InvalidInterfaceException("Could not get the communication characteristics");

		// Subscribe to the RX characteristic.
		bluetoothGatt.setCharacteristicNotification(rxCharacteristic, true);
		byte[] descValue = (rxCharacteristic.getProperties() & CHAR_PROP_INDICATE) == CHAR_PROP_INDICATE ?
				BluetoothGattDescriptor.ENABLE_INDICATION_VALUE : BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
		for (BluetoothGattDescriptor descriptor : rxCharacteristic.getDescriptors()) {
			descriptor.setValue(descValue);
			bluetoothGatt.writeDescriptor(descriptor);
			// Wait until the descriptor is written.
			synchronized (descriptorLock) {
				try {
					descriptorLock.wait(WRITE_TIMEOUT);
				} catch (InterruptedException ignore) {}
			}
		}

		// Initialize the input and output streams.
		inputByteBuffer = new BlCircularByteBuffer();
		outputByteBuffer = new BlCircularByteBuffer();

		writeTask = new WriteTask(this, outputByteBuffer.getInputStream());
		writeTaskRunning = true;
		writeTask.start();

		encrypt = false;
	}

	@Override
	public void close() {
		if (!isOpen() || bluetoothGatt == null)
			return;

		// Unsubscribe from the RX characteristic.
		bluetoothGatt.setCharacteristicNotification(rxCharacteristic, false);
		for (BluetoothGattDescriptor descriptor : rxCharacteristic.getDescriptors()) {
			descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
			bluetoothGatt.writeDescriptor(descriptor);
			// Wait until the descriptor is written.
			synchronized (descriptorLock) {
				try {
					descriptorLock.wait(WRITE_TIMEOUT);
				} catch (InterruptedException ignore) {}
			}
		}

		// Close the streams.
		if (writeTask != null) {
			writeTaskRunning = false;
			writeTask = null;
		}
		if (inputByteBuffer != null) {
			try {
				inputByteBuffer.getOutputStream().close();
			} catch (IOException ignore) {}
			try {
				inputByteBuffer.getInputStream().close();
			} catch (IOException ignore) {}
			inputByteBuffer = null;
		}
		if (outputByteBuffer != null) {
			try {
				outputByteBuffer.getOutputStream().close();
			} catch (IOException ignore) {}
			try {
				outputByteBuffer.getInputStream().close();
			} catch (IOException ignore) {}
			outputByteBuffer = null;
		}

		// Disconnect the device.
		bluetoothGatt.disconnect();

		// Wait until the device is disconnected.
		synchronized (disconnectionLock) {
			try {
				disconnectionLock.wait(DISCONNECTION_TIMEOUT);
			} catch (InterruptedException ignore) {}
		}
	}

	@Override
	public boolean isOpen() {
		return isOpen;
	}

	@Override
	public InputStream getInputStream() {
		if (inputByteBuffer != null)
			return inputByteBuffer.getInputStream();
		return null;
	}

	@Override
	public OutputStream getOutputStream() {
		if (outputByteBuffer != null)
			return outputByteBuffer.getOutputStream();
		return null;
	}

	@Override
	public void writeData(byte[] data) {
		writeData(data, 0, data.length);
	}

	@Override
	public synchronized void writeData(byte[] data, int offset, int length) {
		byte[] dataToWrite = new byte[length];

		// Write the data in the TX characteristic.
		dataWritten = false;
		try {
			if (encrypt)
				cipherEnc.update(data, offset, length, dataToWrite, 0);
			else
				System.arraycopy(data, offset, dataToWrite, 0, length);

			txCharacteristic.setValue(dataToWrite);
			bluetoothGatt.writeCharacteristic(txCharacteristic);

			if (!dataWritten) {
				// Wait until the data is written.
				synchronized (writeCharLock) {
					writeCharLock.wait(WRITE_TIMEOUT);
				}
			}
		} catch (InterruptedException | ShortBufferException e) {
			logger.error(e.getMessage(), e);
		}

		if (!dataWritten)
			logger.error("Could not write data in the TX characteristic");
	}

	@Override
	public int readData(byte[] data) throws IOException {
		return readData(data, 0, data.length);
	}

	@Override
	public int readData(byte[] data, int offset, int length) throws IOException {
		int readBytes = 0;
		if (getInputStream() != null)
			readBytes = getInputStream().read(data, offset, length);
		return readBytes;
	}

	@Override
	public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
		if (newState == BluetoothProfile.STATE_CONNECTED) {
			isOpen = true;
			// Notify the connection lock.
			synchronized (connectionLock) {
				connectionLock.notify();
			}
		} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
			isOpen = false;
			bluetoothGatt.close();
			bluetoothGatt = null;
			// Notify the disconnection lock.
			synchronized (disconnectionLock) {
				disconnectionLock.notify();
			}
		}
	}

	@Override
	public void onServicesDiscovered(BluetoothGatt gatt, int status) {
		// Notify that the services has been discovered.
		synchronized (servicesLock) {
			servicesLock.notify();
		}
	}

	@Override
	public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
		// Notify that the descriptor has been written.
		synchronized (descriptorLock) {
			descriptorLock.notify();
		}
	}

	@Override
	public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
		byte[] value = characteristic.getValue();

		// If the communication is encrypted, decrypt the received data.
		if (encrypt) {
			try {
				cipherDec.update(value, 0, value.length, value, 0);
			} catch (ShortBufferException e) {
				logger.error(e.getMessage(), e);
			}
		}

		if (inputByteBuffer == null)
			return;

		try {
			inputByteBuffer.getOutputStream().write(value);
			inputByteBuffer.getOutputStream().flush();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		// Notify that data has been received.
		synchronized (this) {
			this.notify();
		}
	}

	@Override
	public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		dataWritten = status == BluetoothGatt.GATT_SUCCESS;

		// Notify that the write operation has finished.
		synchronized (writeCharLock) {
			writeCharLock.notify();
		}
	}

	/**
	 * Sets the encryption keys and starts to encrypt the communication with the
	 * module.
	 *
	 * @param key Session key.
	 * @param txNonce TX nonce used as prefix of the counter block.
	 * @param rxNonce RX nonce used as prefix of the counter block.
	 *
	 * @throws XBeeException if there is any error setting up the ciphers.
	 */
	public void setEncryptionKeys(byte[] key, byte[] txNonce, byte[] rxNonce) throws XBeeException {
		byte[] txCounter = getCounter(txNonce, 1);
		byte[] rxCounter = getCounter(rxNonce, 1);

		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

		try {
			cipherEnc = Cipher.getInstance(AES_CIPHER);
			cipherEnc.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(txCounter));

			cipherDec = Cipher.getInstance(AES_CIPHER);
			cipherDec.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(rxCounter));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
			throw new XBeeException("Could not set the encryption keys", e);
		}

		encrypt = true;
	}

	/**
	 * Generates and returns the encryption counter with the given nonce and
	 * count value.
	 *
	 * @param nonce Nonce used as prefix of the counter block.
	 * @param count Count value.
	 *
	 * @return The encryption counter.
	 */
	private byte[] getCounter(byte[] nonce, int count) {
		byte[] counter = new byte[LENGTH_COUNTER];
		System.arraycopy(nonce, 0, counter, 0, nonce.length);
		byte[] countBytes = ByteUtils.intToByteArray(count);
		System.arraycopy(countBytes, 0, counter, nonce.length, countBytes.length);
		return counter;
	}

	@Override
	public String toString() {
		return String.format("[%s] ", device.getAddress());
	}

	/**
	 * Class used to write user's data into the Bluetooth characteristic.
	 */
	class WriteTask extends Thread {
		private final BluetoothInterface iface;
		private final InputStream input;

		WriteTask(BluetoothInterface iface, InputStream input) {
			this.iface = iface;
			this.input = input;
		}

		@Override
		public void run() {
			try {
				while (writeTaskRunning) {
					int available = input.available();
					if (available > 0) {
						byte[] data = new byte[available];
						if (input.read(data) > 0)
							iface.writeData(data);
					} else {
						Thread.sleep(50);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}
