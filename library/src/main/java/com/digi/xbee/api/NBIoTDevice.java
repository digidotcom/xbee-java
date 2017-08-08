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
package com.digi.xbee.api;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.android.AndroidUSBPermissionListener;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.exceptions.XBeeDeviceException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBeeProtocol;

import android.content.Context;

/**
 * This class represents a local NB-IoT device.
 * 
 * @see XBeeDevice
 * @see CellularDevice
 * 
 * @since 1.2.1
 */
public class NBIoTDevice extends LPWANDevice {
	
	/**
	 * Class constructor. Instantiates a new {@code NBIoTDevice} object in 
	 * the given port name and baud rate.
	 * 
	 * @param port Serial port name where NB-IoT device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. 
	 *                 Other connection parameters will be set as default (8 
	 *                 data bits, 1 stop bit, no parity, no flow control).
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #NBIoTDevice(IConnectionInterface)
	 * @see #NBIoTDevice(String, SerialPortParameters)
	 * @see #NBIoTDevice(String, int, int, int, int, int)
	 * @see #NBIoTDevice(Context, int)
	 * @see #NBIoTDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #NBIoTDevice(Context, String, int)
	 * @see #NBIoTDevice(Context, String, SerialPortParameters)
	 */
	public NBIoTDevice(String port, int baudRate) {
		this(XBee.createConnectiontionInterface(port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code NBIoTDevice} object in 
	 * the given serial port name and settings.
	 * 
	 * @param port Serial port name where NB-IoT device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device.
	 * @param dataBits Serial port data bits.
	 * @param stopBits Serial port data bits.
	 * @param parity Serial port data bits.
	 * @param flowControl Serial port data bits.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0} or
	 *                                  if {@code dataBits < 0} or
	 *                                  if {@code stopBits < 0} or
	 *                                  if {@code parity < 0} or
	 *                                  if {@code flowControl < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #NBIoTDevice(IConnectionInterface)
	 * @see #NBIoTDevice(String, int)
	 * @see #NBIoTDevice(String, SerialPortParameters)
	 * @see #NBIoTDevice(Context, int)
	 * @see #NBIoTDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #NBIoTDevice(Context, String, int)
	 * @see #NBIoTDevice(Context, String, SerialPortParameters)
	 */
	public NBIoTDevice(String port, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
		this(port, new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code NBIoTDevice} object in 
	 * the given serial port name and parameters.
	 * 
	 * @param port Serial port name where NB-IoT device is attached to.
	 * @param serialPortParameters Object containing the serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see #NBIoTDevice(IConnectionInterface)
	 * @see #NBIoTDevice(String, int)
	 * @see #NBIoTDevice(String, int, int, int, int, int)
	 * @see #NBIoTDevice(Context, int)
	 * @see #NBIoTDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #NBIoTDevice(Context, String, int)
	 * @see #NBIoTDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public NBIoTDevice(String port, SerialPortParameters serialPortParameters) {
		this(XBee.createConnectiontionInterface(port, serialPortParameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code NBIoTDevice} object for
	 * Android with the given parameters.
	 * 
	 * @param context The Android context.
	 * @param baudRate The USB connection baud rate.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null}.
	 * 
	 * @see #NBIoTDevice(IConnectionInterface)
	 * @see #NBIoTDevice(String, int)
	 * @see #NBIoTDevice(String, int, int, int, int, int)
	 * @see #NBIoTDevice(String, SerialPortParameters)
	 * @see #NBIoTDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #NBIoTDevice(Context, String, int)
	 * @see #NBIoTDevice(Context, String, SerialPortParameters)
	 */
	public NBIoTDevice(Context context, int baudRate) {
		super(XBee.createConnectiontionInterface(context, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code NBIoTDevice} object for
	 * Android with the given parameters.
	 * 
	 * @param context The Android context.
	 * @param baudRate The USB connection baud rate.
	 * @param permissionListener The USB permission listener that will be 
	 *                           notified when user grants USB permissions.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null}.
	 * 
	 * @see #NBIoTDevice(IConnectionInterface)
	 * @see #NBIoTDevice(String, int)
	 * @see #NBIoTDevice(String, int, int, int, int, int)
	 * @see #NBIoTDevice(String, SerialPortParameters)
	 * @see #NBIoTDevice(Context, int)
	 * @see #NBIoTDevice(Context, String, int)
	 * @see #NBIoTDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.android.AndroidUSBPermissionListener
	 */
	public NBIoTDevice(Context context, int baudRate, AndroidUSBPermissionListener permissionListener) {
		super(XBee.createConnectiontionInterface(context, baudRate, permissionListener));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code NBIoTDevice} object for
	 * Android with the given parameters.
	 * 
	 * <p>This constructor uses the Digi Android Serial Port API based on the
	 * RxTx library to communicate with the devices.</p>
	 * 
	 * @param context The Android application context.
	 * @param port Serial port name where XBee device is attached to.
	 * @param baudRate The serial port connection baud rate.
	 * 
	 * @throws NullPointerException If {@code context == null} or
	 *                              if {@code port == null}.
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * 
	 * @see #NBIoTDevice(IConnectionInterface)
	 * @see #NBIoTDevice(String, int)
	 * @see #NBIoTDevice(String, int, int, int, int, int)
	 * @see #NBIoTDevice(String, SerialPortParameters)
	 * @see #NBIoTDevice(Context, int)
	 * @see #NBIoTDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #NBIoTDevice(Context, String, SerialPortParameters)
	 */
	public NBIoTDevice(Context context, String port, int baudRate) {
		super(XBee.createConnectiontionInterface(context, port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code NBIoTDevice} object for
	 * Android with the given parameters.
	 * 
	 * <p>This constructor uses the Digi Android Serial Port API based on the
	 * RxTx library to communicate with the devices.</p>
	 * 
	 * @param context The Android application context.
	 * @param port Serial port name where XBee device is attached to.
	 * @param parameters The serial port parameters.
	 * 
	 * @throws NullPointerException If {@code context == null} or
	 *                              if {@code port == null} or
	 *                              if {@code parameters == null}.
	 * 
	 * @see #NBIoTDevice(IConnectionInterface)
	 * @see #NBIoTDevice(String, int)
	 * @see #NBIoTDevice(String, int, int, int, int, int)
	 * @see #NBIoTDevice(String, SerialPortParameters)
	 * @see #NBIoTDevice(Context, int)
	 * @see #NBIoTDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #NBIoTDevice(Context, String, int)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public NBIoTDevice(Context context, String port, SerialPortParameters parameters) {
		super(XBee.createConnectiontionInterface(context, port, parameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code NBIoTDevice} object with 
	 * the given connection interface.
	 * 
	 * @param connectionInterface The connection interface with the physical 
	 *                            IP device.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null}
	 * 
	 * @see #NBIoTDevice(String, int)
	 * @see #NBIoTDevice(String, SerialPortParameters)
	 * @see #NBIoTDevice(String, int, int, int, int, int)
	 * @see #NBIoTDevice(Context, int)
	 * @see #NBIoTDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #NBIoTDevice(Context, String, int)
	 * @see #NBIoTDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	public NBIoTDevice(IConnectionInterface connectionInterface) {
		super(connectionInterface);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#open()
	 */
	@Override
	public void open() throws XBeeException {
		super.open();
		if (xbeeProtocol != XBeeProtocol.CELLULAR_NBIOT)
			throw new XBeeDeviceException("XBee device is not a " + getXBeeProtocol().getDescription() + 
					" device, it is a " + xbeeProtocol.getDescription() + " device.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#getXBeeProtocol()
	 */
	@Override
	public XBeeProtocol getXBeeProtocol() {
		return XBeeProtocol.CELLULAR_NBIOT;
	}
}
