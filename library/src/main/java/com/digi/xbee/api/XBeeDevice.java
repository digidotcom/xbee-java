/*
 * Copyright 2017-2019, Digi International Inc.
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
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import com.digi.xbee.api.listeners.IModemStatusReceiveListener;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.listeners.IUserDataRelayReceiveListener;
import com.digi.xbee.api.listeners.relay.IBluetoothDataReceiveListener;
import com.digi.xbee.api.listeners.relay.IMicroPythonDataReceiveListener;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBeeLocalInterface;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.packet.XBeePacket;

/**
 * This class represents a local XBee device.
 * 
 * @see CellularDevice
 * @see DigiPointDevice
 * @see DigiMeshDevice
 * @see Raw802Device
 * @see ThreadDevice
 * @see WiFiDevice
 * @see ZigBeeDevice
 */
public class XBeeDevice extends AbstractXBeeDevice {
	
	/**
	 * Class constructor. Instantiates a new {@code XBeeDevice} object 
	 * physically connected to the given port name and configured at the 
	 * provided baud rate.
	 * 
	 * @param port Serial port name where XBee device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. 
	 *                 Other connection parameters will be set as default (8 
	 *                 data bits, 1 stop bit, no parity, no flow control).
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #XBeeDevice(IConnectionInterface)
	 * @see #XBeeDevice(String, SerialPortParameters)
	 * @see #XBeeDevice(String, int, int, int, int, int)
	 */
	public XBeeDevice(String port, int baudRate) {
		super(port, baudRate);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code XBeeDevice} object 
	 * physically connected to the given port name and configured to communicate 
	 * with the provided serial settings.
	 * 
	 * @param port Serial port name where XBee device is attached to.
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
	 * @see #XBeeDevice(IConnectionInterface)
	 * @see #XBeeDevice(String, int)
	 * @see #XBeeDevice(String, SerialPortParameters)
	 */
	public XBeeDevice(String port, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
		super(port, baudRate, dataBits, stopBits, parity, flowControl);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code XBeeDevice} object 
	 * physically connected to the given port name and configured to communicate 
	 * with the provided serial settings.
	 * 
	 * @param port Serial port name where XBee device is attached to.
	 * @param serialPortParameters Object containing the serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see #XBeeDevice(IConnectionInterface)
	 * @see #XBeeDevice(String, int)
	 * @see #XBeeDevice(String, int, int, int, int, int)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public XBeeDevice(String port, SerialPortParameters serialPortParameters) {
		super(port, serialPortParameters);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code XBeeDevice} object with the 
	 * given connection interface.
	 * 
	 * @param connectionInterface The connection interface with the physical 
	 *                            XBee device.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null}.
	 * 
	 * @see #XBeeDevice(String, int)
	 * @see #XBeeDevice(String, SerialPortParameters)
	 * @see #XBeeDevice(String, int, int, int, int, int)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	public XBeeDevice(IConnectionInterface connectionInterface) {
		super(connectionInterface);
	}
	
	@Override
	public void open() throws XBeeException {
		super.open();
	}
	
	@Override
	public void close() {
		super.close();
	}
	
	@Override
	public boolean isOpen() {
		return super.isOpen();
	}
	
	@Override
	public boolean isRemote() {
		return false;
	}
	
	@Override
	public XBeeNetwork getNetwork() {
		return super.getNetwork();
	}
	
	@Override
	public OperatingMode getOperatingMode() {
		return super.getOperatingMode();
	}
	
	@Override
	public int getNextFrameID() {
		return super.getNextFrameID();
	}
	
	@Override
	public int getReceiveTimeout() {
		return super.getReceiveTimeout();
	}
	
	@Override
	public void setReceiveTimeout(int receiveTimeout) {
		super.setReceiveTimeout(receiveTimeout);
	}
	
	@Override
	public void addPacketListener(IPacketReceiveListener listener) {
		super.addPacketListener(listener);
	}
	
	@Override
	public void removePacketListener(IPacketReceiveListener listener) {
		super.removePacketListener(listener);
	}
	
	@Override
	public void addDataListener(IDataReceiveListener listener) {
		super.addDataListener(listener);
	}
	
	@Override
	public void removeDataListener(IDataReceiveListener listener) {
		super.removeDataListener(listener);
	}
	
	@Override
	public void addIOSampleListener(IIOSampleReceiveListener listener) {
		super.addIOSampleListener(listener);
	}
	
	@Override
	public void removeIOSampleListener(IIOSampleReceiveListener listener) {
		super.removeIOSampleListener(listener);
	}
	
	@Override
	public void addModemStatusListener(IModemStatusReceiveListener listener) {
		super.addModemStatusListener(listener);
	}
	
	@Override
	public void removeModemStatusListener(IModemStatusReceiveListener listener) {
		super.removeModemStatusListener(listener);
	}
	
	@Override
	public void addUserDataRelayListener(IUserDataRelayReceiveListener listener) {
		super.addUserDataRelayListener(listener);
	}
	
	@Override
	public void removeUserDataRelayListener(IUserDataRelayReceiveListener listener) {
		super.removeUserDataRelayListener(listener);
	}
	
	@Override
	public void addBluetoothDataListener(IBluetoothDataReceiveListener listener) {
		super.addBluetoothDataListener(listener);
	}
	
	@Override
	public void removeBluetoothDataListener(IBluetoothDataReceiveListener listener) {
		super.removeBluetoothDataListener(listener);
	}
	
	@Override
	public void addMicroPythonDataListener(IMicroPythonDataReceiveListener listener) {
		super.addMicroPythonDataListener(listener);
	}
	
	@Override
	public void removeMicroPythonDataListener(IMicroPythonDataReceiveListener listener) {
		super.removeMicroPythonDataListener(listener);
	}
	
	@Override
	public void sendDataAsync(RemoteXBeeDevice remoteXBeeDevice, byte[] data) throws XBeeException {
		super.sendDataAsync(remoteXBeeDevice, data);
	}
	
	@Override
	public void sendData(RemoteXBeeDevice remoteXBeeDevice, byte[] data) throws TimeoutException, XBeeException {
		super.sendData(remoteXBeeDevice, data);
	}
	
	@Override
	public void sendBroadcastData(byte[] data) throws TimeoutException, XBeeException {
		super.sendBroadcastData(data);
	}
	
	@Override
	public void sendUserDataRelay(XBeeLocalInterface destInterface, byte[] data) throws XBeeException {
		super.sendUserDataRelay(destInterface, data);
	}
	
	@Override
	public void sendBluetoothData(byte[] data) throws XBeeException {
		super.sendBluetoothData(data);
	}
	
	@Override
	public void sendMicroPythonData(byte[] data) throws XBeeException {
		super.sendMicroPythonData(data);
	}
	
	@Override
	public void sendPacket(XBeePacket packet, IPacketReceiveListener packetReceiveListener) throws XBeeException {
		super.sendPacket(packet, packetReceiveListener);
	}
	
	@Override
	public void sendPacketAsync(XBeePacket packet) throws XBeeException {
		super.sendPacketAsync(packet);
	}
	
	@Override
	public XBeePacket sendPacket(XBeePacket packet) throws TimeoutException, XBeeException {
		return super.sendPacket(packet);
	}
	
	@Override
	public void reset() throws TimeoutException, XBeeException {
		softwareReset();
	}
	
	@Override
	public XBeeMessage readData() {
		return super.readData();
	}
	
	@Override
	public XBeeMessage readData(int timeout) {
		return super.readData(timeout);
	}
	
	@Override
	public XBeeMessage readDataFrom(RemoteXBeeDevice remoteXBeeDevice) {
		return super.readDataFrom(remoteXBeeDevice);
	}
	
	@Override
	public XBeeMessage readDataFrom(RemoteXBeeDevice remoteXBeeDevice, int timeout) {
		return super.readDataFrom(remoteXBeeDevice, timeout);
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}
