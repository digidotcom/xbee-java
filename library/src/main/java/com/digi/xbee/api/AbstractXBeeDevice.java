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

import java.io.IOException;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.bluetooth.AbstractBluetoothInterface;
import com.digi.xbee.api.connection.ConnectionType;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.exceptions.ATCommandException;
import com.digi.xbee.api.exceptions.BluetoothAuthenticationException;
import com.digi.xbee.api.exceptions.InterfaceAlreadyOpenException;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.TransmitException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOMode;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.listeners.IExplicitDataReceiveListener;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import com.digi.xbee.api.listeners.IModemStatusReceiveListener;
import com.digi.xbee.api.listeners.IIPDataReceiveListener;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.listeners.ISMSReceiveListener;
import com.digi.xbee.api.listeners.IUserDataRelayReceiveListener;
import com.digi.xbee.api.listeners.relay.IBluetoothDataReceiveListener;
import com.digi.xbee.api.listeners.relay.IMicroPythonDataReceiveListener;
import com.digi.xbee.api.listeners.relay.ISerialDataReceiveListener;
import com.digi.xbee.api.models.APIOutputMode;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.AssociationIndicationStatus;
import com.digi.xbee.api.models.ExplicitXBeeMessage;
import com.digi.xbee.api.models.HardwareVersion;
import com.digi.xbee.api.models.ModemStatusEvent;
import com.digi.xbee.api.models.PowerLevel;
import com.digi.xbee.api.models.RemoteATCommandOptions;
import com.digi.xbee.api.models.RestFulStatusEnum;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeLocalInterface;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.models.XBeePacketsQueue;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.models.XBeeTransmitOptions;
import com.digi.xbee.api.models.XBeeTransmitStatus;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ATCommandPacket;
import com.digi.xbee.api.packet.common.ATCommandQueuePacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.common.ExplicitAddressingPacket;
import com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket;
import com.digi.xbee.api.packet.common.IODataSampleRxIndicatorPacket;
import com.digi.xbee.api.packet.common.ReceivePacket;
import com.digi.xbee.api.packet.common.RemoteATCommandPacket;
import com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket;
import com.digi.xbee.api.packet.common.TransmitPacket;
import com.digi.xbee.api.packet.common.TransmitStatusPacket;
import com.digi.xbee.api.packet.raw.RX16IOPacket;
import com.digi.xbee.api.packet.raw.RX16Packet;
import com.digi.xbee.api.packet.raw.RX64IOPacket;
import com.digi.xbee.api.packet.raw.RX64Packet;
import com.digi.xbee.api.packet.raw.TX64Packet;
import com.digi.xbee.api.packet.raw.TXStatusPacket;
import com.digi.xbee.api.packet.relay.UserDataRelayPacket;
import com.digi.xbee.api.packet.thread.CoAPRxResponsePacket;
import com.digi.xbee.api.packet.thread.CoAPTxRequestPacket;
import com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket;
import com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;
import com.digi.xbee.api.utils.srp.SrpUtils;

/**
 * This class provides common functionality for all XBee devices.
 * 
 * @see XBeeDevice
 * @see RemoteXBeeDevice
 */
public abstract class AbstractXBeeDevice {
	
	// Constants.
	private static String COMMAND_MODE_CHAR = "+";
	private static String COMMAND_MODE_OK = "OK\r";
	
	private static int TIMEOUT_RESET = 5000;
	protected static int TIMEOUT_READ_PACKET = 3000;
	
	// Variables.
	protected XBeeNetwork network;
	
	private Object resetLock = new Object();
	
	private boolean modemStatusReceived = false;
	
	protected String bluetoothPassword;
	
	/**
	 * Default receive timeout used to wait for a response in synchronous 
	 * operations: {@value} ms.
	 * 
	 * @see XBeeDevice#getReceiveTimeout()
	 * @see XBeeDevice#setReceiveTimeout(int)
	 */
	protected final static int DEFAULT_RECEIVE_TIMETOUT = 2000; // 2.0 seconds of timeout to receive packet and command responses.
	
	/**
	 * Timeout to wait before entering in command mode: {@value} ms.
	 * 
	 * <p>It is used to determine the operating mode of the module (this 
	 * library only supports API modes, not transparent mode).</p>
	 * 
	 * <p>This value depends on the {@code GT}, {@code AT} and/or {@code BT} 
	 * parameters.</p>
	 * 
	 * @see XBeeDevice#determineOperatingMode()
	 */
	protected final static int TIMEOUT_BEFORE_COMMAND_MODE = 1200;
	
	/**
	 * Timeout to wait after entering in command mode: {@value} ms.
	 * 
	 * <p>It is used to determine the operating mode of the module (this 
	 * library only supports API modes, not transparent mode).</p>
	 * 
	 * <p>This value depends on the {@code GT}, {@code AT} and/or {@code BT} 
	 * parameters.</p>
	 * 
	 * @see XBeeDevice#determineOperatingMode()
	 */
	protected final static int TIMEOUT_ENTER_COMMAND_MODE = 1500;
	
	// Variables.
	protected IConnectionInterface connectionInterface;
	
	protected DataReader dataReader = null;
	
	protected XBeeProtocol xbeeProtocol = XBeeProtocol.UNKNOWN;
	
	protected OperatingMode operatingMode = OperatingMode.UNKNOWN;
	
	protected XBee16BitAddress xbee16BitAddress = XBee16BitAddress.UNKNOWN_ADDRESS;
	protected XBee64BitAddress xbee64BitAddress = XBee64BitAddress.UNKNOWN_ADDRESS;
	
	protected Inet6Address ipv6Address = null;
	
	protected int currentFrameID = 0xFF;
	protected int receiveTimeout = DEFAULT_RECEIVE_TIMETOUT;
	
	protected AbstractXBeeDevice localXBeeDevice;
	
	protected Logger logger;
	
	private String nodeID;
	private String firmwareVersion;
	
	private HardwareVersion hardwareVersion;
	
	private Object ioLock = new Object();
	
	private boolean ioPacketReceived = false;
	private boolean applyConfigurationChanges = true;
	
	private byte[] ioPacketPayload;
	
	/**
	 * Class constructor. Instantiates a new {@code XBeeDevice} object in the 
	 * given port name and baud rate.
	 * 
	 * @param port Serial port name where XBee device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. 
	 *                 Other connection parameters will be set as default (8 
	 *                 data bits, 1 stop bit, no parity, no flow control).
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #AbstractXBeeDevice(IConnectionInterface)
	 * @see #AbstractXBeeDevice(String, SerialPortParameters)
	 * @see #AbstractXBeeDevice(AbstractXBeeDevice, XBee64BitAddress)
	 * @see #AbstractXBeeDevice(AbstractXBeeDevice, XBee64BitAddress, XBee16BitAddress, String)
	 * @see #AbstractXBeeDevice(String, int, int, int, int, int)
	 */
	public AbstractXBeeDevice(String port, int baudRate) {
		this(XBee.createConnectiontionInterface(port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code XBeeDevice} object in the 
	 * given serial port name and settings.
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
	 * @see #AbstractXBeeDevice(IConnectionInterface)
	 * @see #AbstractXBeeDevice(String, int)
	 * @see #AbstractXBeeDevice(String, SerialPortParameters)
	 * @see #AbstractXBeeDevice(AbstractXBeeDevice, XBee64BitAddress)
	 * @see #AbstractXBeeDevice(AbstractXBeeDevice, XBee64BitAddress, XBee16BitAddress, String)
	 */
	public AbstractXBeeDevice(String port, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
		this(port, new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code XBeeDevice} object in the 
	 * given serial port name and parameters.
	 * 
	 * @param port Serial port name where XBee device is attached to.
	 * @param serialPortParameters Object containing the serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see #AbstractXBeeDevice(IConnectionInterface)
	 * @see #AbstractXBeeDevice(String, int)
	 * @see #AbstractXBeeDevice(AbstractXBeeDevice, XBee64BitAddress)
	 * @see #AbstractXBeeDevice(AbstractXBeeDevice, XBee64BitAddress, XBee16BitAddress, String)
	 * @see #AbstractXBeeDevice(String, int, int, int, int, int)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public AbstractXBeeDevice(String port, SerialPortParameters serialPortParameters) {
		this(XBee.createConnectiontionInterface(port, serialPortParameters));
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
	 * @see #AbstractXBeeDevice(String, int)
	 * @see #AbstractXBeeDevice(String, SerialPortParameters)
	 * @see #AbstractXBeeDevice(AbstractXBeeDevice, XBee64BitAddress)
	 * @see #AbstractXBeeDevice(AbstractXBeeDevice, XBee64BitAddress, XBee16BitAddress, String)
	 * @see #AbstractXBeeDevice(String, int, int, int, int, int)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	public AbstractXBeeDevice(IConnectionInterface connectionInterface) {
		if (connectionInterface == null)
			throw new NullPointerException("ConnectionInterface cannot be null.");
		
		this.connectionInterface = connectionInterface;
		this.logger = LoggerFactory.getLogger(this.getClass());
		logger.debug(toString() + "Using the connection interface {}.", 
				connectionInterface.getClass().getSimpleName());
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteXBeeDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote XBee device.
	 * @param addr64 The 64-bit address to identify this XBee device.
	 * 
	 * @throws IllegalArgumentException If {@code localXBeeDevice.isRemote() == true}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code addr64 == null}.
	 * 
	 * @see #AbstractXBeeDevice(IConnectionInterface)
	 * @see #AbstractXBeeDevice(String, int)
	 * @see #AbstractXBeeDevice(String, SerialPortParameters)
	 * @see #AbstractXBeeDevice(AbstractXBeeDevice, XBee64BitAddress, XBee16BitAddress, String)
	 * @see #AbstractXBeeDevice(String, int, int, int, int, int)
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 */
	public AbstractXBeeDevice(AbstractXBeeDevice localXBeeDevice, XBee64BitAddress addr64) {
		this(localXBeeDevice, addr64, null, null);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteXBeeDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote XBee device.
	 * @param addr64 The 64-bit address to identify this XBee device.
	 * @param addr16 The 16-bit address to identify this XBee device. It might 
	 *               be {@code null}.
	 * @param id The node identifier of this XBee device. It might be 
	 *           {@code null}.
	 * 
	 * @throws IllegalArgumentException If {@code localXBeeDevice.isRemote() == true}.
	 * @throws NullPointerException If {@code localXBeeDevice == null} or
	 *                              if {@code addr64 == null}.
	 * 
	 * @see #AbstractXBeeDevice(IConnectionInterface)
	 * @see #AbstractXBeeDevice(String, int)
	 * @see #AbstractXBeeDevice(String, SerialPortParameters)
	 * @see #AbstractXBeeDevice(AbstractXBeeDevice, XBee64BitAddress)
	 * @see #AbstractXBeeDevice(String, int, int, int, int, int)
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public AbstractXBeeDevice(AbstractXBeeDevice localXBeeDevice, XBee64BitAddress addr64, 
			XBee16BitAddress addr16, String id) {
		if (localXBeeDevice == null)
			throw new NullPointerException("Local XBee device cannot be null.");
		if (addr64 == null)
			throw new NullPointerException("XBee 64-bit address of the device cannot be null.");
		if (localXBeeDevice.isRemote())
			throw new IllegalArgumentException("The given local XBee device is remote.");
		
		this.localXBeeDevice = localXBeeDevice;
		this.connectionInterface = localXBeeDevice.getConnectionInterface();
		this.xbee64BitAddress = addr64;
		this.xbee16BitAddress = addr16;
		if (addr16 == null)
			xbee16BitAddress = XBee16BitAddress.UNKNOWN_ADDRESS;
		this.nodeID = id;
		this.logger = LoggerFactory.getLogger(this.getClass());
		logger.debug(toString() + "Using the connection interface {}.", 
				connectionInterface.getClass().getSimpleName());
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteXBeeDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote XBee device.
	 * @param ipv6Addr The IPv6 address to identify this XBee device.
	 * 
	 * @throws IllegalArgumentException If {@code localXBeeDevice.isRemote() == true}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code ipv6Addr == null}.
	 * 
	 * @see #AbstractXBeeDevice(IConnectionInterface)
	 * @see #AbstractXBeeDevice(String, int)
	 * @see #AbstractXBeeDevice(String, SerialPortParameters)
	 * @see #AbstractXBeeDevice(String, int, int, int, int, int)
	 * @see java.net.Inet6Address
	 * 
	 * @since 1.2.1
	 */
	public AbstractXBeeDevice(AbstractXBeeDevice localXBeeDevice, Inet6Address ipv6Addr) {
		this(localXBeeDevice, ipv6Addr, null);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteXBeeDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote XBee device.
	 * @param ipv6Addr The IPv6 address to identify this XBee device.
	 * @param id The node identifier of this XBee device. It might be 
	 *           {@code null}.
	 * 
	 * @throws IllegalArgumentException If {@code localXBeeDevice.isRemote() == true}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code ipv6Addr == null}.
	 * 
	 * @see #AbstractXBeeDevice(IConnectionInterface)
	 * @see #AbstractXBeeDevice(String, int)
	 * @see #AbstractXBeeDevice(String, SerialPortParameters)
	 * @see #AbstractXBeeDevice(String, int, int, int, int, int)
	 * @see java.net.Inet6Address
	 * 
	 * @since 1.2.1
	 */
	public AbstractXBeeDevice(AbstractXBeeDevice localXBeeDevice, Inet6Address ipv6Addr, String id) {
		if (localXBeeDevice == null)
			throw new NullPointerException("Local XBee device cannot be null.");
		if (ipv6Addr == null)
			throw new NullPointerException("XBee IPv6 address of the device cannot be null.");
		if (localXBeeDevice.isRemote())
			throw new IllegalArgumentException("The given local XBee device is remote.");
		
		this.localXBeeDevice = localXBeeDevice;
		this.connectionInterface = localXBeeDevice.getConnectionInterface();
		this.ipv6Address = ipv6Addr;
		this.nodeID = id;
		this.logger = LoggerFactory.getLogger(this.getClass());
		logger.debug(toString() + "Using the connection interface {}.", 
				connectionInterface.getClass().getSimpleName());
	}
	
	/**
	 * Returns the connection interface associated to this XBee device.
	 * 
	 * @return XBee device's connection interface.
	 * 
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	public IConnectionInterface getConnectionInterface() {
		return connectionInterface;
	}
	
	/**
	 * Returns whether this XBee device is a remote device.
	 * 
	 * @return {@code true} if this XBee device is a remote device, 
	 *         {@code false} otherwise.
	 */
	abstract public boolean isRemote();
	
	/**
	 * Reads some parameters from this device and obtains its protocol.
	 * 
	 * <p>This method refresh the values of:</p>
	 * <ul>
	 * <li>64-bit address only if it is not initialized.</li>
	 * <li>Node Identifier.</li>
	 * <li>Hardware version if it is not initialized.</li>
	 * <li>Firmware version.</li>
	 * <li>XBee device protocol.</li>
	 * <li>16-bit address (not for DigiMesh/DigiPoint modules).</li>
	 * </ul>
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout reading the parameters.
	 * @throws XBeeException if there is any error trying to read the device information or
	 *                       if there is any other XBee related exception.
	 * 
	 * @see #get16BitAddress()
	 * @see #get64BitAddress()
	 * @see #getHardwareVersion()
	 * @see #getNodeID()
	 * @see #getFirmwareVersion()
	 * @see #getXBeeProtocol()
	 * @see #setNodeID(String)
	 */
	public void readDeviceInfo() throws TimeoutException, XBeeException {
		byte[] response = null;
		// Get the 64-bit address.
		if (xbee64BitAddress == null || xbee64BitAddress.equals(XBee64BitAddress.UNKNOWN_ADDRESS)) {
			String addressHigh;
			String addressLow;
			
			response = getParameter("SH");
			addressHigh = HexUtils.byteArrayToHexString(response);
			
			response = getParameter("SL");
			addressLow = HexUtils.byteArrayToHexString(response);
			
			while(addressLow.length() < 8)
				addressLow = "0" + addressLow;
			
			xbee64BitAddress = new XBee64BitAddress(addressHigh + addressLow);
		}
		// Get the Node ID.
		response = getParameter("NI");
		nodeID = new String(response);
		
		// Get the hardware version.
		if (hardwareVersion == null) {
			response = getParameter("HV");
			hardwareVersion = HardwareVersion.get(response[0]);
		}
		// Get the firmware version.
		response = getParameter("VR");
		firmwareVersion = HexUtils.byteArrayToHexString(response);
		
		// Original value of the protocol.
		XBeeProtocol origProtocol = getXBeeProtocol();
		
		// Obtain the device protocol.
		xbeeProtocol = XBeeProtocol.determineProtocol(hardwareVersion, firmwareVersion);
		
		if (origProtocol != XBeeProtocol.UNKNOWN
				&& origProtocol != xbeeProtocol) {
			throw new XBeeException("Error reading device information: "
					+ "Your module seems to be " + xbeeProtocol 
					+ " and NOT " + origProtocol + ". Check if you are using" 
					+ " the appropriate device class.");
		}
		
		// Get the 16-bit address. This must be done after obtaining the protocol because 
		// DigiMesh and Point-to-Multipoint protocols don't have 16-bit addresses.
		XBeeProtocol protocol = getXBeeProtocol();
		if (protocol == XBeeProtocol.ZIGBEE
				|| protocol == XBeeProtocol.RAW_802_15_4
				|| protocol == XBeeProtocol.XTEND
				|| protocol == XBeeProtocol.SMART_ENERGY
				|| protocol == XBeeProtocol.ZNET) {
			response = getParameter("MY");
			xbee16BitAddress = new XBee16BitAddress(response);
		}
	}
	
	/**
	 * Returns the 16-bit address of this XBee device.
	 * 
	 * <p>To refresh this value use the {@link #readDeviceInfo()} method.</p>
	 * 
	 * @return The 16-bit address of this XBee device.
	 * 
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 */
	public XBee16BitAddress get16BitAddress() {
		return xbee16BitAddress;
	}
	
	/**
	 * Returns the 64-bit address of this XBee device.
	 * 
	 * <p>If this value is {@code null} or 
	 * {@code XBee64BitAddress.UNKNOWN_ADDRESS}, use the 
	 * {@link #readDeviceInfo()} method to get its value.</p>
	 * 
	 * @return The 64-bit address of this XBee device.
	 * 
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public XBee64BitAddress get64BitAddress() {
		return xbee64BitAddress;
	}
	
	/**
	 * Returns the IPv6 address of this IPv6 device.
	 * 
	 * <p>To refresh this value use the {@link #readDeviceInfo()} method.</p>
	 * 
	 * @return The IPv6 address of this IPv6 device.
	 * 
	 * @see java.net.Inet6Address
	 * 
	 * @since 1.2.1
	 */
	public Inet6Address getIPv6Address() {
		return ipv6Address;
	}
	
	/**
	 * Returns the Operating mode (AT, API or API escaped) of this XBee device 
	 * for a local device, and the operating mode of the local device used as 
	 * communication interface for a remote device.
	 * 
	 * @return The operating mode of the local XBee device.
	 * 
	 * @see #isRemote()
	 * @see com.digi.xbee.api.models.OperatingMode
	 */
	protected OperatingMode getOperatingMode() {
		if (isRemote())
			return localXBeeDevice.getOperatingMode();
		return operatingMode;
	}
	
	/**
	 * Returns the XBee Protocol of this XBee device.
	 * 
	 * <p>To refresh this value use the {@link #readDeviceInfo()} method.</p>
	 * 
	 * @return The XBee device protocol.
	 * 
	 * @see com.digi.xbee.api.models.XBeeProtocol
	 */
	public XBeeProtocol getXBeeProtocol() {
		return xbeeProtocol;
	}
	
	/**
	 * Returns the node identifier of this XBee device.
	 * 
	 * <p>To refresh this value use the {@link #readDeviceInfo()} method.</p>
	 * 
	 * @return The node identifier of this device.
	 * 
	 * @see #setNodeID(String)
	 */
	public String getNodeID() {
		return nodeID;
	}
	
	/**
	 * Sets the node identifier of this XBee device.
	 * 
	 * @param nodeID The new node id of the device.
	 * 
	 * @throws IllegalArgumentException if {@code nodeID.length > 20}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code nodeID == null}.
	 * @throws TimeoutException if there is a timeout setting the node ID value.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getNodeID()
	 */
	public void setNodeID(String nodeID) throws TimeoutException, XBeeException {
		if (nodeID == null)
			throw new NullPointerException("Node ID cannot be null.");
		if (nodeID.length() > 20)
			throw new IllegalArgumentException("Node ID length must be less than 21.");
		
		setParameter("NI", nodeID.getBytes());
		
		this.nodeID = nodeID;
	}
	
	/**
	 * Returns the firmware version (hexadecimal string value) of this XBee 
	 * device.
	 * 
	 * <p>To refresh this value use the {@link #readDeviceInfo()} method.</p>
	 * 
	 * @return The firmware version of the XBee device.
	 */
	public String getFirmwareVersion() {
		return firmwareVersion;
	}
	
	/**
	 * Returns the hardware version of this XBee device.
	 * 
	 * <p>If this value is {@code null}, use the {@link #readDeviceInfo()} 
	 * method to get its value.</p>
	 * 
	 * @return The hardware version of the XBee device.
	 * 
	 * @see com.digi.xbee.api.models.HardwareVersion
	 * @see com.digi.xbee.api.models.HardwareVersionEnum
	 */
	public HardwareVersion getHardwareVersion() {
		return hardwareVersion;
	}
	
	/**
	 * Updates the current device reference with the data provided for the 
	 * given device.
	 * 
	 * <p><b>This is only for internal use.</b></p>
	 * 
	 * @param device The XBee Device to get the data from.
	 */
	public void updateDeviceDataFrom(AbstractXBeeDevice device) {
		// TODO Should the devices have the same protocol??
		// TODO Should be allow to update a local from a remote or viceversa?? Maybe 
		// this must be in the Local/Remote device class(es) and not here... 
		
		// Only update the Node Identifier if the provided is not null.
		if (device.getNodeID() != null)
			this.nodeID = device.getNodeID();
		
		// Only update the 64-bit address if the original is null or unknown.
		XBee64BitAddress addr64 = device.get64BitAddress();
		if (addr64 != null && !addr64.equals(XBee64BitAddress.UNKNOWN_ADDRESS)
				&& !addr64.equals(xbee64BitAddress) 
				&& (xbee64BitAddress == null 
					|| xbee64BitAddress.equals(XBee64BitAddress.UNKNOWN_ADDRESS))) {
			xbee64BitAddress = addr64;
		}
		
		// TODO Change here the 16-bit address or maybe in ZigBee and 802.15.4?
		// TODO Should the 16-bit address be always updated? Or following the same rule as the 64-bit address.
		XBee16BitAddress addr16 = device.get16BitAddress();
		if (addr16 != null && !addr16.equals(xbee16BitAddress)) {
			xbee16BitAddress = addr16;
		}
		
		//this.deviceType = device.deviceType; // This is not yet done.
		
		// The operating mode: only API/API2. Do we need this for a remote device?
		// The protocol of the device should be the same.
		// The hardware version should be the same.
		// The firmware version can change...
	}
	
	/**
	 * Adds the provided listener to the list of listeners to be notified
	 * when new packets are received. 
	 * 
	 * <p>If the listener has been already included, this method does nothing.
	 * </p>
	 * 
	 * @param listener Listener to be notified when new packets are received.
	 * 
	 * @throws NullPointerException if {@code listener == null}
	 * 
	 * @see #removePacketListener(IPacketReceiveListener)
	 * @see com.digi.xbee.api.listeners.IPacketReceiveListener
	 */
	protected void addPacketListener(IPacketReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		if (dataReader == null)
			return;
		dataReader.addPacketReceiveListener(listener);
	}
	
	/**
	 * Removes the provided listener from the list of packets listeners. 
	 * 
	 * <p>If the listener was not in the list this method does nothing.</p>
	 * 
	 * @param listener Listener to be removed from the list of listeners.
	 * 
	 * @throws NullPointerException if {@code listener == null}
	 * 
	 * @see #addPacketListener(IPacketReceiveListener)
	 * @see com.digi.xbee.api.listeners.IPacketReceiveListener
	 */
	protected void removePacketListener(IPacketReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		if (dataReader == null)
			return;
		dataReader.removePacketReceiveListener(listener);
	}
	
	/**
	 * Adds the provided listener to the list of listeners to be notified
	 * when new data from a remote XBee device is received.
	 * 
	 * <p>If the listener has been already included this method does nothing.
	 * </p>
	 * 
	 * @param listener Listener to be notified when new data from a remote XBee
	 *                 device is received.
	 * 
	 * @throws NullPointerException if {@code listener == null}
	 * 
	 * @see #removeDataListener(IDataReceiveListener)
	 * @see com.digi.xbee.api.listeners.IDataReceiveListener
	 */
	protected void addDataListener(IDataReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		if (dataReader == null)
			return;
		dataReader.addDataReceiveListener(listener);
	}
	
	/**
	 * Removes the provided listener from the list of data listeners for remote
	 * XBee devices.
	 * 
	 * <p>If the listener was not in the list this method does nothing.</p>
	 * 
	 * @param listener Listener to be removed from the list of listeners.
	 * 
	 * @throws NullPointerException if {@code listener == null}
	 * 
	 * @see #addDataListener(IDataReceiveListener)
	 * @see com.digi.xbee.api.listeners.IDataReceiveListener
	 */
	protected void removeDataListener(IDataReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		if (dataReader == null)
			return;
		dataReader.removeDataReceiveListener(listener);
	}
	
	/**
	 * Adds the provided listener to the list of listeners to be notified
	 * when new IO samples are received. 
	 * 
	 * <p>If the listener has been already included this method does nothing.
	 * </p>
	 * 
	 * @param listener Listener to be notified when new IO samples are received.
	 * 
	 * @throws NullPointerException if {@code listener == null}
	 * 
	 * @see #removeIOSampleListener(IIOSampleReceiveListener)
	 * @see com.digi.xbee.api.listeners.IIOSampleReceiveListener
	 */
	protected void addIOSampleListener(IIOSampleReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		if (dataReader == null)
			return;
		dataReader.addIOSampleReceiveListener(listener);
	}
	
	/**
	 * Removes the provided listener from the list of IO samples listeners. 
	 * 
	 * <p>If the listener was not in the list this method does nothing.</p>
	 * 
	 * @param listener Listener to be removed from the list of listeners.
	 * 
	 * @throws NullPointerException if {@code listener == null}
	 * 
	 * @see #addIOSampleListener(IIOSampleReceiveListener)
	 * @see com.digi.xbee.api.listeners.IIOSampleReceiveListener
	 */
	protected void removeIOSampleListener(IIOSampleReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		if (dataReader == null)
			return;
		dataReader.removeIOSampleReceiveListener(listener);
	}
	
	/**
	 * Adds the provided listener to the list of listeners to be notified
	 * when new Modem Status events are received.
	 * 
	 * <p>If the listener has been already included this method does nothing.
	 * </p>
	 * 
	 * @param listener Listener to be notified when new Modem Status events are 
	 *                 received.
	 * 
	 * @throws NullPointerException if {@code listener == null}
	 * 
	 * @see #removeModemStatusListener(IModemStatusReceiveListener)
	 * @see com.digi.xbee.api.listeners.IModemStatusReceiveListener
	 */
	protected void addModemStatusListener(IModemStatusReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		if (dataReader == null)
			return;
		dataReader.addModemStatusReceiveListener(listener);
	}
	
	/**
	 * Removes the provided listener from the list of Modem Status listeners.
	 * 
	 * <p>If the listener was not in the list this method does nothing.</p>
	 * 
	 * @param listener Listener to be removed from the list of listeners.
	 * 
	 * @throws NullPointerException if {@code listener == null}
	 * 
	 * @see #addModemStatusListener(IModemStatusReceiveListener)
	 * @see com.digi.xbee.api.listeners.IModemStatusReceiveListener
	 */
	protected void removeModemStatusListener(IModemStatusReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		if (dataReader == null)
			return;
		dataReader.removeModemStatusReceiveListener(listener);
	}
	
	/**
	 * Adds the provided listener to the list of listeners to be notified
	 * when new explicit data packets are received.
	 * 
	 * <p>If the listener has been already included this method does nothing.
	 * </p>
	 * 
	 * @param listener Listener to be notified when new explicit data packets 
	 *                 are received.
	 * 
	 * @throws NullPointerException if {@code listener == null}
	 * 
	 * @see #removeExplicitDataListener(IExplicitDataReceiveListener)
	 * @see com.digi.xbee.api.listeners.IExplicitDataReceiveListener
	 */
	protected void addExplicitDataListener(IExplicitDataReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		if (dataReader == null)
			return;
		dataReader.addExplicitDataReceiveListener(listener);
	}
	
	/**
	 * Removes the provided listener from the list of explicit data receive 
	 * listeners.
	 * 
	 * <p>If the listener was not in the list this method does nothing.</p>
	 * 
	 * @param listener Listener to be removed from the list of listeners.
	 * 
	 * @throws NullPointerException if {@code listener == null}
	 * 
	 * @see #addExplicitDataListener(IExplicitDataReceiveListener)
	 * @see com.digi.xbee.api.listeners.IExplicitDataReceiveListener
	 */
	protected void removeExplicitDataListener(IExplicitDataReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		if (dataReader == null)
			return;
		dataReader.removeExplicitDataReceiveListener(listener);
	}
	
	
	/**
	 * Adds the provided listener to the list of listeners to be notified
	 * when new IP data is received. 
	 * 
	 * <p>If the listener has been already included this method does nothing.
	 * </p>
	 * 
	 * @param listener Listener to be notified when new IP data is 
	 *                 received.
	 * 
	 * @throws NullPointerException if {@code listener == null}
	 * 
	 * @see #removeIPDataListener(IIPDataReceiveListener)
	 * @see com.digi.xbee.api.listeners.IIPDataReceiveListener
	 * 
	 * @since 1.2.0
	 */
	protected void addIPDataListener(IIPDataReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		if (dataReader == null)
			return;
		dataReader.addIPDataReceiveListener(listener);
	}
	
	/**
	 * Removes the provided listener from the list of IP data listeners. 
	 * 
	 * <p>If the listener was not in the list this method does nothing.</p>
	 * 
	 * @param listener Listener to be removed from the list of listeners.
	 * 
	 * @throws NullPointerException if {@code listener == null}
	 * 
	 * @see #addIPDataListener(IIPDataReceiveListener)
	 * @see com.digi.xbee.api.listeners.IIPDataReceiveListener
	 * 
	 * @since 1.2.0
	 */
	protected void removeIPDataListener(IIPDataReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		if (dataReader == null)
			return;
		dataReader.removeIPDataReceiveListener(listener);
	}
	
	/**
	 * Adds the provided listener to the list of listeners to be notified
	 * when new SMS is received. 
	 * 
	 * <p>If the listener has been already included this method does nothing.
	 * </p>
	 * 
	 * @param listener Listener to be notified when new SMS is received.
	 * 
	 * @throws NullPointerException if {@code listener == null}
	 * 
	 * @see #removeSMSListener(ISMSReceiveListener)
	 * @see com.digi.xbee.api.listeners.ISMSReceiveListener
	 * 
	 * @since 1.2.0
	 */
	protected void addSMSListener(ISMSReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		if (dataReader == null)
			return;
		dataReader.addSMSReceiveListener(listener);
	}
	
	/**
	 * Removes the provided listener from the list of SMS listeners. 
	 * 
	 * <p>If the listener was not in the list this method does nothing.</p>
	 * 
	 * @param listener Listener to be removed from the list of listeners.
	 * 
	 * @throws NullPointerException if {@code listener == null}
	 * 
	 * @see #addSMSListener(ISMSReceiveListener)
	 * @see com.digi.xbee.api.listeners.ISMSReceiveListener
	 * 
	 * @since 1.2.0
	 */
	protected void removeSMSListener(ISMSReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		if (dataReader == null)
			return;
		dataReader.removeSMSReceiveListener(listener);
	}

	/**
	 * Adds the provided listener to the list of listeners to be notified
	 * when new User Data Relay message is received.
	 *
	 * <p>If the listener has been already included this method does nothing.
	 * </p>
	 *
	 * @param listener Listener to be notified when new User Data Relay message
	 *                 is received.
	 *
	 * @throws NullPointerException if {@code listener == null}
	 *
	 * @see #removeUserDataRelayListener(IUserDataRelayReceiveListener)
	 * @see IUserDataRelayReceiveListener
	 *
	 * @since 1.3.0
	 */
	protected void addUserDataRelayListener(IUserDataRelayReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");

		if (dataReader == null)
			return;
		dataReader.addUserDataRelayReceiveListener(listener);
	}

	/**
	 * Removes the provided listener from the list of User Data Relay listeners.
	 *
	 * <p>If the listener was not in the list this method does nothing.</p>
	 *
	 * @param listener Listener to be removed from the list of listeners.
	 *
	 * @throws NullPointerException if {@code listener == null}
	 *
	 * @see #addUserDataRelayListener(IUserDataRelayReceiveListener)
	 * @see IUserDataRelayReceiveListener
	 *
	 * @since 1.3.0
	 */
	protected void removeUserDataRelayListener(IUserDataRelayReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");

		if (dataReader == null)
			return;
		dataReader.removeUserDataRelayReceiveListener(listener);
	}

	/**
	 * Adds the provided listener to the list of listeners to be notified
	 * when new data from the Bluetooth interface is received in a User Data
	 * Relay frame.
	 *
	 * <p>If the listener has been already included this method does nothing.
	 * </p>
	 *
	 * @param listener Listener to be notified when new data from the Bluetooth
	 *                 interface is received.
	 *
	 * @throws NullPointerException if {@code listener == null}
	 *
	 * @see #removeBluetoothDataListener(IBluetoothDataReceiveListener)
	 * @see IBluetoothDataReceiveListener
	 *
	 * @since 1.3.0
	 */
	protected void addBluetoothDataListener(IBluetoothDataReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");

		if (dataReader == null)
			return;
		dataReader.addBluetoothDataReceiveListener(listener);
	}

	/**
	 * Removes the provided listener from the list of data receive listeners
	 * for the Bluetooth interface.
	 *
	 * <p>If the listener was not in the list this method does nothing.</p>
	 *
	 * @param listener Listener to be removed from the list of listeners.
	 *
	 * @throws NullPointerException if {@code listener == null}
	 *
	 * @see #addBluetoothDataListener(IBluetoothDataReceiveListener)
	 * @see IBluetoothDataReceiveListener
	 *
	 * @since 1.3.0
	 */
	protected void removeBluetoothDataListener(IBluetoothDataReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");

		if (dataReader == null)
			return;
		dataReader.removeBluetoothDataReceiveListener(listener);
	}

	/**
	 * Adds the provided listener to the list of listeners to be notified
	 * when new data from the MicroPython interface is received in a User
	 * Data Relay frame.
	 *
	 * <p>If the listener has been already included this method does nothing.
	 * </p>
	 *
	 * @param listener Listener to be notified when new data from the
	 *                 MicroPython interface is received.
	 *
	 * @throws NullPointerException if {@code listener == null}
	 *
	 * @see #removeMicroPythonDataListener(IMicroPythonDataReceiveListener)
	 * @see IMicroPythonDataReceiveListener
	 *
	 * @since 1.3.0
	 */
	protected void addMicroPythonDataListener(IMicroPythonDataReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");

		if (dataReader == null)
			return;
		dataReader.addMicroPythonDataReceiveListener(listener);
	}

	/**
	 * Removes the provided listener from the list of data receive listeners
	 * for the MicroPython interface.
	 *
	 * <p>If the listener was not in the list this method does nothing.</p>
	 *
	 * @param listener Listener to be removed from the list of listeners.
	 *
	 * @throws NullPointerException if {@code listener == null}
	 *
	 * @see #addMicroPythonDataListener(IMicroPythonDataReceiveListener)
	 * @see IMicroPythonDataReceiveListener
	 *
	 * @since 1.3.0
	 */
	protected void removeMicroPythonDataListener(IMicroPythonDataReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");

		if (dataReader == null)
			return;
		dataReader.removeMicroPythonDataReceiveListener(listener);
	}

	/**
	 * Adds the provided listener to the list of listeners to be notified
	 * when new data from the serial interface is received in a User Data
	 * Relay frame.
	 *
	 * <p>If the listener has been already included this method does nothing.
	 * </p>
	 *
	 * @param listener Listener to be notified when new data from the serial
	 *                 interface is received.
	 *
	 * @throws NullPointerException if {@code listener == null}
	 *
	 * @see #removeSerialDataListener(ISerialDataReceiveListener)
	 * @see ISerialDataReceiveListener
	 *
	 * @since 1.3.0
	 */
	protected void addSerialDataListener(ISerialDataReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");

		if (dataReader == null)
			return;
		dataReader.addSerialDataReceiveListener(listener);
	}

	/**
	 * Removes the provided listener from the list of data receive listeners
	 * for the serial interface.
	 *
	 * <p>If the listener was not in the list this method does nothing.</p>
	 *
	 * @param listener Listener to be removed from the list of listeners.
	 *
	 * @throws NullPointerException if {@code listener == null}
	 *
	 * @see #addSerialDataListener(ISerialDataReceiveListener)
	 * @see ISerialDataReceiveListener
	 *
	 * @since 1.3.0
	 */
	protected void removeSerialDataListener(ISerialDataReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");

		if (dataReader == null)
			return;
		dataReader.removeSerialDataReceiveListener(listener);
	}

	/**
	 * Sends the given AT command and waits for answer or until the configured 
	 * receive timeout expires.
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * @param command AT command to be sent.
	 * @return An {@code ATCommandResponse} object containing the response of 
	 *         the command or {@code null} if there is no response.
	 *         
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws InvalidOperatingModeException if the operating mode is different 
	 *                                       than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws IOException if an I/O error occurs while sending the AT command.
	 * @throws NullPointerException if {@code command == null}.
	 * @throws TimeoutException if the configured time expires while waiting 
	 *                          for the command reply.
	 * 
	 * @see XBeeDevice#getReceiveTimeout()
	 * @see XBeeDevice#setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.ATCommand
	 * @see com.digi.xbee.api.models.ATCommandResponse
	 */
	protected ATCommandResponse sendATCommand(ATCommand command) 
			throws InvalidOperatingModeException, TimeoutException, IOException {
		// Check if command is null.
		if (command == null)
			throw new NullPointerException("AT command cannot be null.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		ATCommandResponse response = null;
		OperatingMode operatingMode = getOperatingMode();
		switch (operatingMode) {
		case AT:
		case UNKNOWN:
		default:
			throw new InvalidOperatingModeException(operatingMode);
		case API:
		case API_ESCAPE:
			// Create the corresponding AT command packet depending on if the device is local or remote.
			XBeePacket packet;
			if (isRemote()) {
				int remoteATCommandOptions = RemoteATCommandOptions.OPTION_NONE;
				if (isApplyConfigurationChangesEnabled())
					remoteATCommandOptions |= RemoteATCommandOptions.OPTION_APPLY_CHANGES;
				
				if (getXBeeProtocol() == XBeeProtocol.THREAD) {
					packet = new IPv6RemoteATCommandRequestPacket(getNextFrameID(), ipv6Address,
							remoteATCommandOptions, command.getCommand(), command.getParameter());
				} else{
					XBee16BitAddress remote16BitAddress = get16BitAddress();
					if (remote16BitAddress == null)
						remote16BitAddress = XBee16BitAddress.UNKNOWN_ADDRESS;
					
					packet = new RemoteATCommandPacket(getNextFrameID(), get64BitAddress(), 
							remote16BitAddress, remoteATCommandOptions, command.getCommand(), command.getParameter());
				}
			} else {
				if (isApplyConfigurationChangesEnabled())
					packet = new ATCommandPacket(getNextFrameID(), command.getCommand(), command.getParameter());
				else
					packet = new ATCommandQueuePacket(getNextFrameID(), command.getCommand(), command.getParameter());
			}
			if (command.getParameter() == null)
				logger.debug(toString() + "Sending AT command '{}'.", command.getCommand());
			else
				logger.debug(toString() + "Sending AT command '{} {}'.", command.getCommand(), 
						HexUtils.prettyHexString(command.getParameter()));
			try {
				// Send the packet and build the corresponding response depending on if the device is local or remote.
				XBeePacket answerPacket;
				if (isRemote())
					answerPacket = localXBeeDevice.sendXBeePacket(packet);
				else
					answerPacket = sendXBeePacket(packet);
				
				if (answerPacket instanceof ATCommandResponsePacket) {
					ATCommandResponsePacket r = (ATCommandResponsePacket)answerPacket;
					response = new ATCommandResponse(command, r.getCommandValue(), r.getStatus());
				} else if (answerPacket instanceof RemoteATCommandResponsePacket) {
					RemoteATCommandResponsePacket r = (RemoteATCommandResponsePacket)answerPacket;
					response = new ATCommandResponse(command, r.getCommandValue(), r.getStatus());
				} else if (answerPacket instanceof IPv6RemoteATCommandResponsePacket) {
					IPv6RemoteATCommandResponsePacket r = (IPv6RemoteATCommandResponsePacket)answerPacket;
					response = new ATCommandResponse(command, r.getCommandValue(), r.getStatus());
				}
				
				if (response != null && response.getResponse() != null)
					logger.debug(toString() + "AT command response: {}.", HexUtils.prettyHexString(response.getResponse()));
				else
					logger.debug(toString() + "AT command response: null.");
			} catch (ClassCastException e) {
				logger.error("Received an invalid packet type after sending an AT command packet." + e);
			}
		}
		return response;
	}
	
	/**
	 * Sends the given XBee packet asynchronously.
	 * 
	 * <p>The method will not wait for an answer for the packet.</p>
	 * 
	 * <p>To be notified when the answer is received, use 
	 * {@link #sendXBeePacket(XBeePacket, IPacketReceiveListener)}.</p>
	 * 
	 * @param packet XBee packet to be sent asynchronously.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws InvalidOperatingModeException if the operating mode is different 
	 *                                       than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws IOException if an I/O error occurs while sending the XBee packet.
	 * @throws NullPointerException if {@code packet == null}.
	 * 
	 * @see #sendXBeePacket(XBeePacket)
	 * @see #sendXBeePacket(XBeePacket, IPacketReceiveListener)
	 * @see #sendXBeePacketAsync(XBeePacket)
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	protected void sendXBeePacketAsync(XBeePacket packet) 
			throws InvalidOperatingModeException, IOException {
		sendXBeePacket(packet, null);
	}
	
	/**
	 * Sends the given XBee packet asynchronously and registers the given 
	 * packet listener (if not {@code null}) to wait for an answer.
	 * 
	 * <p>The method will not wait for an answer for the packet, but the given 
	 * listener will be notified when the answer arrives.</p>
	 * 
	 * @param packet XBee packet to be sent.
	 * @param packetReceiveListener Listener for the operation, {@code null} 
	 *                              not to be notified when the answer arrives.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws InvalidOperatingModeException if the operating mode is different 
	 *                                       than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws IOException if an I/O error occurs while sending the XBee packet.
	 * @throws NullPointerException if {@code packet == null}.
	 * 
	 * @see #sendXBeePacket(XBeePacket)
	 * @see #sendXBeePacket(XBeePacket, IPacketReceiveListener)
	 * @see #sendXBeePacketAsync(XBeePacket)
	 * @see com.digi.xbee.api.listeners.IPacketReceiveListener
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	protected void sendXBeePacket(XBeePacket packet, IPacketReceiveListener packetReceiveListener)
			throws InvalidOperatingModeException, IOException {
		// Check if the packet to send is null.
		if (packet == null)
			throw new NullPointerException("XBee packet cannot be null.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		OperatingMode operatingMode = getOperatingMode();
		switch (operatingMode) {
		case AT:
		case UNKNOWN:
		default:
			throw new InvalidOperatingModeException(operatingMode);
		case API:
		case API_ESCAPE:
			// Add the required frame ID and subscribe listener if given.
			if (packet instanceof XBeeAPIPacket) {
					
					insertFrameID(packet);
					
					XBeeAPIPacket apiPacket = (XBeeAPIPacket)packet;
					
					if (packetReceiveListener != null 
							&& apiPacket.needsAPIFrameID())
						dataReader.addPacketReceiveListener(packetReceiveListener, apiPacket.getFrameID());
					else if (packetReceiveListener != null)
						dataReader.addPacketReceiveListener(packetReceiveListener);
			}
			
			// Write packet data.
			writePacket(packet);
			break;
		}
	}
	
	/**
	 * Sends the given XBee packet synchronously and blocks until response is 
	 * received or receive timeout is reached.
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>Use {@link #sendXBeePacketAsync(XBeePacket)} for non-blocking 
	 * operations.</p>
	 * 
	 * @param packet XBee packet to be sent.
	 * @return An {@code XBeePacket} containing the response of the sent packet 
	 *         or {@code null} if there is no response.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws InvalidOperatingModeException if the operating mode is different 
	 *                                       than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws IOException if an I/O error occurs while sending the XBee packet.
	 * @throws NullPointerException if {@code packet == null}.
	 * @throws TimeoutException if the configured time expires while waiting for
	 *                          the packet reply.
	 * 
	 * @see #sendXBeePacket(XBeePacket)
	 * @see #sendXBeePacket(XBeePacket, IPacketReceiveListener)
	 * @see #sendXBeePacketAsync(XBeePacket)
	 * @see XBeeDevice#setReceiveTimeout(int)
	 * @see XBeeDevice#getReceiveTimeout()
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	protected XBeePacket sendXBeePacket(final XBeePacket packet) 
			throws InvalidOperatingModeException, TimeoutException, IOException {
		// Check if the packet to send is null.
		if (packet == null)
			throw new NullPointerException("XBee packet cannot be null.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		OperatingMode operatingMode = getOperatingMode();
		switch (operatingMode) {
		case AT:
		case UNKNOWN:
		default:
			throw new InvalidOperatingModeException(operatingMode);
		case API:
		case API_ESCAPE:
			// Build response container.
			ArrayList<XBeePacket> responseList = new ArrayList<XBeePacket>();
			
			// If the packet does not need frame ID, send it async. and return null.
			if (packet instanceof XBeeAPIPacket) {
				if (!((XBeeAPIPacket)packet).needsAPIFrameID()) {
					sendXBeePacketAsync(packet);
					return null;
				}
			} else {
				sendXBeePacketAsync(packet);
				return null;
			}
			
			// Add the required frame ID to the packet if necessary.
			insertFrameID(packet);
			
			// Generate a packet received listener for the packet to be sent.
			IPacketReceiveListener packetReceiveListener = createPacketReceivedListener(packet, responseList);
			
			// Add the packet listener to the data reader.
			addPacketListener(packetReceiveListener);
			
			// Write the packet data.
			writePacket(packet);
			try {
				// Wait for response or timeout.
				synchronized (responseList) {
					try {
						responseList.wait(receiveTimeout);
					} catch (InterruptedException e) {}
				}
				// After the wait check if we received any response, if not throw timeout exception.
				if (responseList.size() < 1)
					throw new TimeoutException();
				// Return the received packet.
				return responseList.get(0);
			} finally {
				// Always remove the packet listener from the list.
				removePacketListener(packetReceiveListener);
			}
		}
	}
	
	/**
	 * Insert (if possible) the next frame ID stored in the device to the 
	 * provided packet.
	 * 
	 * @param xbeePacket The packet to add the frame ID.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	private void insertFrameID(XBeePacket xbeePacket) {
		if (xbeePacket instanceof XBeeAPIPacket)
			return;
		
		XBeeAPIPacket apiPacket = (XBeeAPIPacket)xbeePacket;
		if (apiPacket.needsAPIFrameID() 
				&& apiPacket.getFrameID() == XBeeAPIPacket.NO_FRAME_ID)
			apiPacket.setFrameID(getNextFrameID());
	}
	
	/**
	 * Returns the packet listener corresponding to the provided sent packet. 
	 * 
	 * <p>The listener will filter those packets  matching with the Frame ID of 
	 * the sent packet storing them in the provided responseList array.</p>
	 * 
	 * @param sentPacket The packet sent.
	 * @param responseList List of packets received that correspond to the 
	 *                     frame ID of the packet sent.
	 * 
	 * @return A packet receive listener that will filter the packets received 
	 *         corresponding to the sent one.
	 * 
	 * @see com.digi.xbee.api.listeners.IPacketReceiveListener
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	private IPacketReceiveListener createPacketReceivedListener(final XBeePacket sentPacket, final ArrayList<XBeePacket> responseList) {
		IPacketReceiveListener packetReceiveListener = new IPacketReceiveListener() {
			/*
			 * (non-Javadoc)
			 * @see com.digi.xbee.api.listeners.IPacketReceiveListener#packetReceived(com.digi.xbee.api.packet.XBeePacket)
			 */
			@Override
			public void packetReceived(XBeePacket receivedPacket) {
				// Check if it is the packet we are waiting for.
				if (((XBeeAPIPacket)receivedPacket).checkFrameID((((XBeeAPIPacket)sentPacket).getFrameID()))) {
					// Security check to avoid class cast exceptions. It has been observed that parallel processes 
					// using the same connection but with different frame index may collide and cause this exception at some point.
					if (sentPacket instanceof XBeeAPIPacket
							&& receivedPacket instanceof XBeeAPIPacket) {
						XBeeAPIPacket sentAPIPacket = (XBeeAPIPacket)sentPacket;
						XBeeAPIPacket receivedAPIPacket = (XBeeAPIPacket)receivedPacket;
						
						// If the packet sent is an AT command, verify that the received one is an AT command response and 
						// the command matches in both packets.
						if (sentAPIPacket.getFrameType() == APIFrameType.AT_COMMAND) {
							if (receivedAPIPacket.getFrameType() != APIFrameType.AT_COMMAND_RESPONSE)
								return;
							if (!((ATCommandPacket)sentAPIPacket).getCommand().equalsIgnoreCase(((ATCommandResponsePacket)receivedPacket).getCommand()))
								return;
						}
						// If the packet sent is a remote AT command, verify that the received one is a remote AT command response and 
						// the command matches in both packets.
						if (sentAPIPacket.getFrameType() == APIFrameType.REMOTE_AT_COMMAND_REQUEST) {
							if (receivedAPIPacket.getFrameType() != APIFrameType.REMOTE_AT_COMMAND_RESPONSE)
								return;
							if (!((RemoteATCommandPacket)sentAPIPacket).getCommand().equalsIgnoreCase(((RemoteATCommandResponsePacket)receivedPacket).getCommand()))
								return;
						}
					}
					
					// Verify that the sent packet is not the received one! This can happen when the echo mode is enabled in the 
					// serial port.
					if (!sentPacket.equals(receivedPacket)) {
						responseList.add(receivedPacket);
						synchronized (responseList) {
							responseList.notify();
						}
					}
				}
			}
		};
		
		return packetReceiveListener;
	}
	
	/**
	 * Writes the given XBee packet in the connection interface of this device.
	 * 
	 * @param packet XBee packet to be written.
	 * 
	 * @throws IOException if an I/O error occurs while writing the XBee packet 
	 *                     in the connection interface.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	private void writePacket(XBeePacket packet) throws IOException {
		logger.debug(toString() + "Sending XBee packet: \n{}", packet.toPrettyString());
		// Write bytes with the required escaping mode.
		switch (operatingMode) {
		case API:
		default:
			connectionInterface.writeData(packet.generateByteArray());
			break;
		case API_ESCAPE:
			connectionInterface.writeData(packet.generateByteArrayEscaped());
			break;
		}
	}
	
	/**
	 * Returns the next Frame ID of this XBee device.
	 * 
	 * @return The next Frame ID.
	 */
	protected synchronized int getNextFrameID() {
		if (isRemote())
			return localXBeeDevice.getNextFrameID();
		if (currentFrameID == 0xff) {
			// Reset counter.
			currentFrameID = 1;
		} else
			currentFrameID ++;
		return currentFrameID;
	}
	
	/**
	 * Sends the provided {@code XBeePacket} and determines if the transmission 
	 * status is success for synchronous transmissions.
	 * 
	 * <p>If the status is not success, an {@code TransmitException} is thrown.</p>
	 * 
	 * @param packet The {@code XBeePacket} to be sent.
	 * @param asyncTransmission Determines whether the transmission must be 
	 *                          asynchronous.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code packet == null}.
	 * @throws TransmitException if {@code packet} is not an instance of 
	 *                           {@code TransmitStatusPacket} or 
	 *                           if {@code packet} is not an instance of 
	 *                           {@code TXStatusPacket} or 
	 *                           if its transmit status is different than 
	 *                           {@code XBeeTransmitStatus.SUCCESS}.
	 * @throws XBeeException if there is any other XBee related error.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	protected void sendAndCheckXBeePacket(XBeePacket packet, boolean asyncTransmission) throws TransmitException, XBeeException {
		XBeePacket receivedPacket = null;
		
		// Send the XBee packet.
		try {
			if (asyncTransmission)
				sendXBeePacketAsync(packet);
			else
				receivedPacket = sendXBeePacket(packet);
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
		
		// If the transmission is async. we are done.
		if (asyncTransmission)
			return;
		
		// Check if the packet received is a valid transmit status packet.
		if (receivedPacket == null)
			throw new TransmitException(null);
		
		XBeeTransmitStatus status = null;
		if (receivedPacket instanceof TransmitStatusPacket)
			status = ((TransmitStatusPacket)receivedPacket).getTransmitStatus();
		else if (receivedPacket instanceof TXStatusPacket)
			status = ((TXStatusPacket)receivedPacket).getTransmitStatus();
		
		if (status != XBeeTransmitStatus.SUCCESS
				&& status != XBeeTransmitStatus.SELF_ADDRESSED)
				throw new TransmitException(status);
	}
	
	/**
	 * Sends the provided {@code CoAPTxRequestPacket} and determines if the 
	 * transmission status and CoAP RX Response are success for synchronous 
	 * transmissions.
	 * 
	 * <p>If the status or the CoAP response is not successful, an 
	 * {@code TransmitException} is thrown.</p>
	 * 
	 * @param packet The {@code CoAPTxRequestPacket} to be sent.
	 * @param asyncTransmission Determines whether the transmission must be 
	 *                          asynchronous.
	 * 
	 * @return A byte array containing the RfData of the CoAP RX Response 
	 *         packet, if that field is not empty. In other cases, it will 
	 *         return {@code null}.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code packet == null}.
	 * @throws TransmitException if {@code packet} is not an instance of 
	 *                           {@code TransmitStatusPacket} or 
	 *                           if {@code packet} is not an instance of 
	 *                           {@code TXStatusPacket} or 
	 *                           if its transmit status is different than 
	 *                           {@code XBeeTransmitStatus.SUCCESS}.
	 * @throws XBeeException if CoAP response packet is not received or it is 
	 *                       not successful or if there is any other XBee 
	 *                       related error.
	 * 
	 * @see com.digi.xbee.api.packet.thread.CoAPTxRequestPacket
	 * 
	 * @since 1.2.1
	 */
	protected byte[] sendAndCheckCoAPPacket(CoAPTxRequestPacket packet,
			boolean asyncTransmission) throws TransmitException, XBeeException {
		// Add listener to read CoAP response (when sending a CoAP transmission, 
		// a transmit status and a CoAP response are received by the sender)
		ArrayList<CoAPRxResponsePacket> coapResponsePackets = new ArrayList<CoAPRxResponsePacket>();
		IPacketReceiveListener listener = createCoAPResponseListener(coapResponsePackets);
		addPacketListener(listener);
		
		// Send the CoAP Tx Request packet.
		XBeePacket receivedPacket = null;
		try {
			if (asyncTransmission)
				sendXBeePacketAsync(packet);
			else
				receivedPacket = sendXBeePacket(packet);
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
		
		// If the transmission is async. we are done.
		if (asyncTransmission)
			return null;
		
		if (receivedPacket == null)
			throw new TransmitException(null);
		
		// Verify Transmit status.
		XBeeTransmitStatus status = null;
		if (receivedPacket instanceof TXStatusPacket)
			status = ((TXStatusPacket)receivedPacket).getTransmitStatus();
		
		if (status == null || 
				(status != XBeeTransmitStatus.SUCCESS && status != XBeeTransmitStatus.SELF_ADDRESSED)) 
			throw new TransmitException(status);
		
		// Verify CoAP response (only expect one CoAP RX Response packet).
		CoAPRxResponsePacket coapRxPacket = null;
		try {
			coapRxPacket = waitForCoAPRxResponsePacket(coapResponsePackets);
		} finally {
			// Always remove the packet listener from the list.
			removePacketListener(listener);
		}
		if (coapRxPacket == null)
			throw new XBeeException("CoAP response was null.");
		return coapRxPacket.getData();
	}
	
	/**
	 * Returns the CoAP packet listener corresponding to the provided sent CoAP 
	 * packet. 
	 * 
	 * <p>The listener will filter CoAP Rx Response packets (0x9C) and storing 
	 * them in the provided responseList array.</p>
	 * 
	 * @param coapResponsePackets List of CoAP Rx Response packets received.
	 * 
	 * @return A CoAP packet receive listener.
	 * 
	 * @see com.digi.xbee.api.listeners.IPacketReceiveListener
	 * @see com.digi.xbee.api.packet.thread.CoAPRxResponsePacket
	 * 
	 * @since 1.2.1
	 */
	private IPacketReceiveListener createCoAPResponseListener(final ArrayList<CoAPRxResponsePacket> coapResponsePackets) {
		IPacketReceiveListener listener = new IPacketReceiveListener() {
			
			@Override
			public void packetReceived(XBeePacket receivedPacket) {
				if (receivedPacket instanceof CoAPRxResponsePacket) {
					coapResponsePackets.add((CoAPRxResponsePacket)receivedPacket);
					coapResponsePackets.notifyAll();
				}
			}
		};
		return listener;
	}
	
	/**
	 * Returns the CoAP Rx Response packet that comes in through the serial 
	 * port in the given timeout.
	 * 
	 * @param coapResponsePackets List of packets where the received packet will be stored.
	 * 
	 * @return CoAP Rx Response packet received.
	 * 
	 * @throws XBeeException if CoAP response packet is not received or it is 
	 *                       not successful or if there is any other XBee 
	 *                       related error.
	 * 
	 * @since 1.2.1
	 */
	private CoAPRxResponsePacket waitForCoAPRxResponsePacket(ArrayList<CoAPRxResponsePacket> coapResponsePackets) throws XBeeException {
		synchronized (coapResponsePackets) {
			try {
				coapResponsePackets.wait(receiveTimeout);
			} catch (InterruptedException e) {}
		}
		
		if (!coapResponsePackets.isEmpty()) {
			RestFulStatusEnum responseStatus = coapResponsePackets.get(0).getStatus();
			if (responseStatus != RestFulStatusEnum.SUCCESS
					&& responseStatus != RestFulStatusEnum.CREATED
					&& responseStatus != RestFulStatusEnum.ACCEPTED
					&& responseStatus != RestFulStatusEnum.NON_AUTHORITATIVE
					&& responseStatus != RestFulStatusEnum.NO_CONTENT
					&& responseStatus != RestFulStatusEnum.RESET_CONTENT)
				throw new XBeeException("CoAP response had an unexpected status: " + responseStatus.toString());
		} else {
			throw new XBeeException("CoAP response was not received.");
		}
		return coapResponsePackets.get(0);
	}
	
	/**
	 * Sets the configuration of the given IO line of this XBee device.
	 * 
	 * @param ioLine The IO line to configure.
	 * @param ioMode The IO mode to set to the IO line.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ioLine == null} or
	 *                              if {@code ioMode == null}.
	 * @throws TimeoutException if there is a timeout sending the set 
	 *                          configuration command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getIOConfiguration(IOLine)
	 * @see com.digi.xbee.api.io.IOLine
	 * @see com.digi.xbee.api.io.IOMode
	 */
	public void setIOConfiguration(IOLine ioLine, IOMode ioMode) throws TimeoutException, XBeeException {
		// Check IO line.
		if (ioLine == null)
			throw new NullPointerException("IO line cannot be null.");
		if (ioMode == null)
			throw new NullPointerException("IO mode cannot be null.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		setParameter(ioLine.getConfigurationATCommand(), new byte[]{(byte)ioMode.getID()});
	}
	
	/**
	 * Returns the configuration mode of the provided IO line of this XBee 
	 * device.
	 * 
	 * @param ioLine The IO line to get its configuration.
	 * 
	 * @return The IO mode (configuration) of the provided IO line.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ioLine == null}.
	 * @throws TimeoutException if there is a timeout sending the get 
	 *                          configuration command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setIOConfiguration(IOLine, IOMode)
	 * @see com.digi.xbee.api.io.IOLine
	 * @see com.digi.xbee.api.io.IOMode
	 */
	public IOMode getIOConfiguration(IOLine ioLine) throws TimeoutException, XBeeException {
		// Check IO line.
		if (ioLine == null)
			throw new NullPointerException("DIO pin cannot be null.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		// Check if the received configuration mode is valid.
		int ioModeValue = getParameter(ioLine.getConfigurationATCommand())[0];
		IOMode dioMode = IOMode.getIOMode(ioModeValue, ioLine);
		if (dioMode == null)
			throw new OperationNotSupportedException("Received configuration mode '" + HexUtils.integerToHexString(ioModeValue, 1) + "' is not valid.");
		
		// Return the configuration mode.
		return dioMode;
	}
	
	/**
	 * Sets the digital value (high or low) to the provided IO line of this 
	 * XBee device.
	 * 
	 * @param ioLine The IO line to set its value.
	 * @param ioValue The IOValue to set to the IO line ({@code HIGH} or 
	 *              {@code LOW}).
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ioLine == null} or 
	 *                              if {@code ioValue == null}.
	 * @throws TimeoutException if there is a timeout sending the set DIO 
	 *                          command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getIOConfiguration(IOLine)
	 * @see #setIOConfiguration(IOLine, IOMode)
	 * @see com.digi.xbee.api.io.IOLine
	 * @see com.digi.xbee.api.io.IOValue
	 * @see com.digi.xbee.api.io.IOMode#DIGITAL_OUT_HIGH
	 * @see com.digi.xbee.api.io.IOMode#DIGITAL_OUT_LOW
	 */
	public void setDIOValue(IOLine ioLine, IOValue ioValue) throws TimeoutException, XBeeException {
		// Check IO line.
		if (ioLine == null)
			throw new NullPointerException("IO line cannot be null.");
		// Check IO value.
		if (ioValue == null)
			throw new NullPointerException("IO value cannot be null.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		setParameter(ioLine.getConfigurationATCommand(), new byte[]{(byte)ioValue.getID()});
	}
	
	/**
	 * Returns the digital value of the provided IO line of this XBee device.
	 * 
	 * <p>The provided <b>IO line must be previously configured as digital I/O
	 * </b>. To do so, use {@code setIOConfiguration} and the following 
	 * {@code IOMode}:</p>
	 * 
	 * <ul>
	 * <li>{@code IOMode.DIGITAL_IN} to configure as digital input.</li>
	 * <li>{@code IOMode.DIGITAL_OUT_HIGH} to configure as digital output, high.
	 * </li>
	 * <li>{@code IOMode.DIGITAL_OUT_LOW} to configure as digital output, low.
	 * </li>
	 * </ul>
	 * 
	 * @param ioLine The IO line to get its digital value.
	 * 
	 * @return The digital value corresponding to the provided IO line.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ioLine == null}.
	 * @throws TimeoutException if there is a timeout sending the get IO values 
	 *                          command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getIOConfiguration(IOLine)
	 * @see #setIOConfiguration(IOLine, IOMode)
	 * @see com.digi.xbee.api.io.IOLine
	 * @see com.digi.xbee.api.io.IOValue
	 * @see com.digi.xbee.api.io.IOMode#DIGITAL_IN
	 * @see com.digi.xbee.api.io.IOMode#DIGITAL_OUT_HIGH
	 * @see com.digi.xbee.api.io.IOMode#DIGITAL_OUT_LOW
	 */
	public IOValue getDIOValue(IOLine ioLine) throws TimeoutException, XBeeException {
		// Check IO line.
		if (ioLine == null)
			throw new NullPointerException("IO line cannot be null.");
		
		// Obtain an IO Sample from the XBee device.
		IOSample ioSample = readIOSample();
		
		// Check if the IO sample contains the expected IO line and value.
		if (!ioSample.hasDigitalValues() || !ioSample.getDigitalValues().containsKey(ioLine))
			throw new OperationNotSupportedException("Answer does not contain digital data for " + ioLine.getName() + ".");
		
		// Return the digital value. 
		return ioSample.getDigitalValues().get(ioLine);
	}
	
	/**
	 * Sets the duty cycle (in %) of the provided IO line of this XBee device. 
	 * 
	 * <p>The provided <b>IO line must be</b>:</p>
	 * 
	 * <ul>
	 * <li><b>PWM capable</b> ({@link IOLine#hasPWMCapability()}).</li>
	 * <li>Previously <b>configured as PWM Output</b> (use 
	 * {@code setIOConfiguration} and {@code IOMode.PWM}).</li>
	 * </ul>
	 * 
	 * @param ioLine The IO line to set its duty cycle value.
	 * @param dutyCycle The duty cycle of the PWM.
	 * 
	 * @throws IllegalArgumentException if {@code ioLine.hasPWMCapability() == false} or 
	 *                                  if {@code value < 0} or
	 *                                  if {@code value > 1023}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ioLine == null}.
	 * @throws TimeoutException if there is a timeout sending the set PWM duty 
	 *                          cycle command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getIOConfiguration(IOLine)
	 * @see #getIOConfiguration(IOLine)
	 * @see #setIOConfiguration(IOLine, IOMode)
	 * @see #getPWMDutyCycle(IOLine)
	 * @see com.digi.xbee.api.io.IOLine
	 * @see com.digi.xbee.api.io.IOMode#PWM
	 */
	public void setPWMDutyCycle(IOLine ioLine, double dutyCycle) throws TimeoutException, XBeeException {
		// Check IO line.
		if (ioLine == null)
			throw new NullPointerException("IO line cannot be null.");
		// Check if the IO line has PWM capability.
		if (!ioLine.hasPWMCapability())
			throw new IllegalArgumentException("Provided IO line does not have PWM capability.");
		// Check duty cycle limits.
		if (dutyCycle < 0 || dutyCycle > 100)
			throw new IllegalArgumentException("Duty Cycle must be between 0% and 100%.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		// Convert the value.
		int finaldutyCycle = (int)(dutyCycle * 1023.0/100.0);
		
		setParameter(ioLine.getPWMDutyCycleATCommand(), ByteUtils.intToByteArray(finaldutyCycle));
	}
	
	/**
	 * Gets the PWM duty cycle (in %) corresponding to the provided IO line of 
	 * this XBee device.
	 * 
	 * <p>The provided <b>IO line must be</b>:</p>
	 * 
	 * <ul>
	 * <li><b>PWM capable</b> ({@link IOLine#hasPWMCapability()}).</li>
	 * <li>Previously <b>configured as PWM Output</b> (use 
	 * {@code setIOConfiguration} and {@code IOMode.PWM}).</li>
	 * </ul>
	 * 
	 * @param ioLine The IO line to get its PWM duty cycle.
	 * 
	 * @return The PWM duty cycle value corresponding to the provided IO line 
	 *         (0% - 100%).
	 * 
	 * @throws IllegalArgumentException if {@code ioLine.hasPWMCapability() == false}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ioLine == null}.
	 * @throws TimeoutException if there is a timeout sending the get PWM duty 
	 *                          cycle command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getIOConfiguration(IOLine)
	 * @see #setIOConfiguration(IOLine, IOMode)
	 * @see #setPWMDutyCycle(IOLine, double)
	 * @see com.digi.xbee.api.io.IOLine
	 * @see com.digi.xbee.api.io.IOMode#PWM
	 */
	public double getPWMDutyCycle(IOLine ioLine) throws TimeoutException, XBeeException {
		// Check IO line.
		if (ioLine == null)
			throw new NullPointerException("IO line cannot be null.");
		// Check if the IO line has PWM capability.
		if (!ioLine.hasPWMCapability())
			throw new IllegalArgumentException("Provided IO line does not have PWM capability.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		byte[] value = getParameter(ioLine.getPWMDutyCycleATCommand());
		
		// Return the PWM duty cycle value.
		int readValue = ByteUtils.byteArrayToInt(value);
		return Math.round((readValue * 100.0/1023.0) * 100.0) / 100.0;
	}
	
	/**
	 * Returns the analog value of the provided IO line of this XBee device.
	 * 
	 * <p>The provided <b>IO line must be previously configured as ADC</b>. To 
	 * do so, use {@code setIOConfiguration} and {@code IOMode.ADC}.</p>
	 * 
	 * @param ioLine The IO line to get its analog value.
	 * 
	 * @return The analog value corresponding to the provided IO line.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ioLine == null}.
	 * @throws TimeoutException if there is a timeout sending the get IO values
	 *                          command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getIOConfiguration(IOLine)
	 * @see #setIOConfiguration(IOLine, IOMode)
	 * @see com.digi.xbee.api.io.IOLine
	 * @see com.digi.xbee.api.io.IOMode#ADC
	 */
	public int getADCValue(IOLine ioLine) throws TimeoutException, XBeeException {
		// Check IO line.
		if (ioLine == null)
			throw new NullPointerException("IO line cannot be null.");
		
		// Obtain an IO Sample from the XBee device.
		IOSample ioSample = readIOSample();
		
		// Check if the IO sample contains the expected IO line and value.
		if (!ioSample.hasAnalogValues() || !ioSample.getAnalogValues().containsKey(ioLine))
			throw new OperationNotSupportedException("Answer does not contain analog data for " + ioLine.getName() + ".");
		
		// Return the analog value.
		return ioSample.getAnalogValues().get(ioLine);
	}
	
	/**
	 * Sets the 64-bit destination extended address of this XBee device.
	 * 
	 * <p>{@link XBee64BitAddress#BROADCAST_ADDRESS} is the broadcast address 
	 * for the PAN. {@link XBee64BitAddress#COORDINATOR_ADDRESS} can be used to 
	 * address the Pan Coordinator.</p>
	 * 
	 * @param xbee64BitAddress 64-bit destination address to be configured.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code xbee64BitAddress == null}.
	 * @throws TimeoutException if there is a timeout sending the set 
	 *                          destination address command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getDestinationAddress()
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public void setDestinationAddress(XBee64BitAddress xbee64BitAddress) throws TimeoutException, XBeeException {
		if (xbee64BitAddress == null)
			throw new NullPointerException("Address cannot be null.");
		
		// This method needs to apply changes after modifying the destination 
		// address, but only if the destination address could be set successfully.
		boolean applyChanges = isApplyConfigurationChangesEnabled();
		if (applyChanges)
			enableApplyConfigurationChanges(false);
		
		byte[] address = xbee64BitAddress.getValue();
		try {
			setParameter("DH", Arrays.copyOfRange(address, 0, 4));
			setParameter("DL", Arrays.copyOfRange(address, 4, 8));
			applyChanges();
		} finally {
			// Always restore the old value of the AC.
			enableApplyConfigurationChanges(applyChanges);
		}
	}
	
	/**
	 * Returns the 64-bit destination extended address of this XBee device.
	 * 
	 * <p>{@link XBee64BitAddress#BROADCAST_ADDRESS} is the broadcast address 
	 * for the PAN. {@link XBee64BitAddress#COORDINATOR_ADDRESS} can be used to 
	 * address the Pan Coordinator.</p>
	 * 
	 * @return 64-bit destination address.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout sending the get
	 *                          destination address command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setDestinationAddress(XBee64BitAddress)
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public XBee64BitAddress getDestinationAddress() throws TimeoutException, XBeeException {
		byte[] dh = getParameter("DH");
		byte[] dl = getParameter("DL");
		byte[] address = new byte[dh.length + dl.length];
		
		System.arraycopy(dh, 0, address, 0, dh.length);
		System.arraycopy(dl, 0, address, dh.length, dl.length);
		
		return new XBee64BitAddress(address);
	}
	
	/**
	 * Sets the destination IPv6 address.
	 * 
	 * @param ipv6Address Destination IPv6 address.
	 * 
	 * @throws NullPointerException if {@code ipv6Address == null}.
	 * @throws TimeoutException if there is a timeout setting the IPv6 
	 *                          destination address.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getIPv6DestinationAddress()
	 * @see java.net.Inet6Address
	 * 
	 * @since 1.2.1
	 */
	public void setIPv6DestinationAddress(Inet6Address ipv6Address) throws TimeoutException, XBeeException {
		if (ipv6Address == null)
			throw new NullPointerException("Destination IPv6 address cannot be null.");
		
		setParameter("DL", ipv6Address.getAddress());
	}
	
	/**
	 * Returns the destination IPv6 address.
	 * 
	 * @return The configured destination IPv6 address.
	 * 
	 * @throws TimeoutException if there is a timeout reading the IPv6 
	 *                          destination address.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setIPv6DestinationAddress(Inet6Address)
	 * @see java.net.Inet6Address
	 * 
	 * @since 1.2.1
	 */
	public Inet6Address getIPv6DestinationAddress() throws TimeoutException, XBeeException {
		try {
			return (Inet6Address) Inet6Address.getByAddress(getParameter("DL"));
		} catch (UnknownHostException e) {
			throw new XBeeException(e);
		}
	}
	
	/**
	 * Sets the IO sampling rate to enable periodic sampling in this XBee 
	 * device.
	 * 
	 * <p>A sample rate of {@code 0} ms. disables this feature.</p>
	 * 
	 * <p>All enabled digital IO and analog inputs will be sampled and
	 * transmitted every {@code rate} milliseconds to the configured destination
	 * address.</p>
	 * 
	 * <p>The destination address can be configured using the 
	 * {@code setDestinationAddress(XBee64BitAddress)} method and retrieved by 
	 * {@code getDestinationAddress()}.</p>
	 * 
	 * @param rate IO sampling rate in milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code rate < 0} or {@code rate >
	 *                                  0xFFFF}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout sending the set IO
	 *                          sampling rate command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getDestinationAddress()
	 * @see #setDestinationAddress(XBee64BitAddress)
	 * @see #getIOSamplingRate()
	 */
	public void setIOSamplingRate(int rate) throws TimeoutException, XBeeException {
		// Check range.
		if (rate < 0 || rate > 0xFFFF)
			throw new IllegalArgumentException("Rate must be between 0 and 0xFFFF.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		setParameter("IR", ByteUtils.intToByteArray(rate));
	}
	
	/**
	 * Returns the IO sampling rate of this XBee device.
	 * 
	 * <p>A sample rate of {@code 0} means the IO sampling feature is disabled.
	 * </p>
	 * 
	 * <p>Periodic sampling allows this XBee module to take an IO sample and 
	 * transmit it to a remote device (configured in the destination address) 
	 * at the configured periodic rate (ms).</p>
	 * 
	 * @return IO sampling rate in milliseconds.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout sending the get IO
	 *                          sampling rate command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getDestinationAddress()
	 * @see #setDestinationAddress(XBee64BitAddress)
	 * @see #setIOSamplingRate(int)
	 */
	public int getIOSamplingRate() throws TimeoutException, XBeeException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		byte[] rate = getParameter("IR");
		return ByteUtils.byteArrayToInt(rate);
	}
	
	/**
	 * Sets the digital IO lines of this XBee device to be monitored and 
	 * sampled whenever their status changes.
	 * 
	 * <p>A {@code null} set of lines disables this feature.</p>
	 * 
	 * <p>If a change is detected on an enabled digital IO pin, a digital IO
	 * sample is immediately transmitted to the configured destination address.
	 * </p>
	 * 
	 * <p>The destination address can be configured using the 
	 * {@code setDestinationAddress(XBee64BitAddress)} method and retrieved by 
	 * {@code getDestinationAddress()}.</p>
	 * 
	 * @param lines Set of IO lines to be monitored, {@code null} to disable 
	 *              this feature.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout sending the set DIO
	 *                          change detection command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getDestinationAddress()
	 * @see #getDIOChangeDetection()
	 * @see #setDestinationAddress(XBee64BitAddress)
	 */
	public void setDIOChangeDetection(Set<IOLine> lines) throws TimeoutException, XBeeException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		byte[] bitfield = new byte[2];
		
		if (lines != null) {
			for (IOLine line : lines) {
				int i = line.getIndex();
				if (i < 8)
					bitfield[1] = (byte) (bitfield[1] | (1 << i));
				else
					bitfield[0] = (byte) (bitfield[0] | (1 << i - 8));
			}
		}
		
		setParameter("IC", bitfield);
	}
	
	/**
	 * Returns the set of IO lines of this device that are monitored for 
	 * change detection.
	 * 
	 * <p>A {@code null} set means the DIO change detection feature is disabled.
	 * </p>
	 * 
	 * <p>Modules can be configured to transmit to the configured destination 
	 * address a data sample immediately whenever a monitored digital IO line 
	 * changes state.</p> 
	 * 
	 * @return Set of digital IO lines that are monitored for change detection,
	 *         {@code null} if there are no monitored lines.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout sending the get DIO
	 *                          change detection command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getDestinationAddress()
	 * @see #setDestinationAddress(XBee64BitAddress)
	 * @see #setDIOChangeDetection(Set)
	 */
	public Set<IOLine> getDIOChangeDetection() throws TimeoutException, XBeeException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		byte[] bitfield = getParameter("IC");
		TreeSet<IOLine> lines = new TreeSet<IOLine>();
		int mask = (bitfield[0] << 8) + (bitfield[1] & 0xFF);
		
		for (int i = 0; i < 16; i++) {
			if (ByteUtils.isBitEnabled(mask, i))
				lines.add(IOLine.getDIO(i));
		}
		
		if (lines.size() > 0)
			return lines;
		return null;
	}
	
	/**
	 * Applies changes to all command registers causing queued command register
	 * values to be applied.
	 * 
	 * <p>This method must be invoked if the 'apply configuration changes' 
	 * option is disabled and the changes to this XBee device parameters must 
	 * be applied.</p>
	 * 
	 * <p>To know if the 'apply configuration changes' option is enabled, use 
	 * the {@code isApplyConfigurationChangesEnabled()} method. And to 
	 * enable/disable this feature, the method 
	 * {@code enableApplyConfigurationChanges(boolean)}.</p>
	 * 
	 * <p>Applying changes does not imply the modifications will persist 
	 * through subsequent resets. To do so, use the {@code writeChanges()} 
	 * method.</p>
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout sending the get Apply
	 *                          Changes command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #enableApplyConfigurationChanges(boolean)
	 * @see #isApplyConfigurationChangesEnabled()
	 * @see #setParameter(String, byte[])
	 * @see #writeChanges()
	 */
	public void applyChanges() throws TimeoutException, XBeeException {
		executeParameter("AC");
	}
	
	/**
	 * Checks if the provided {@code ATCommandResponse} is valid throwing an 
	 * {@code ATCommandException} in case it is not.
	 * 
	 * @param response The {@code ATCommandResponse} to check.
	 * 
	 * @throws ATCommandException if {@code response == null} or 
	 *                            if {@code response.getResponseStatus() != ATCommandStatus.OK}.
	 * 
	 * @see com.digi.xbee.api.models.ATCommandResponse
	 */
	protected void checkATCommandResponseIsValid(ATCommandResponse response) throws ATCommandException {
		if (response == null || response.getResponseStatus() == null)
			throw new ATCommandException(null);
		else if (response.getResponseStatus() != ATCommandStatus.OK)
			throw new ATCommandException(response.getResponseStatus());
	}
	
	/**
	 * Returns an IO sample from this XBee device containing the value of all
	 * enabled digital IO and analog input channels.
	 * 
	 * @return An IO sample containing the value of all enabled digital IO and
	 *         analog input channels.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout getting the IO sample.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see com.digi.xbee.api.io.IOSample
	 */
	public IOSample readIOSample() throws TimeoutException, XBeeException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		// Try to build an IO Sample from the sample payload.
		byte[] samplePayload = null;
		IOSample ioSample;
		
		// The response to the IS command in local 802.15.4 devices is empty, 
		// so we have to create a packet listener to receive the IO sample.
		if (!isRemote() && getXBeeProtocol() == XBeeProtocol.RAW_802_15_4) {
			executeParameter("IS");
			samplePayload = receiveRaw802IOPacket();
			if (samplePayload == null)
				throw new TimeoutException("Timeout waiting for the IO response packet.");
		} else
			samplePayload = getParameter("IS");
		
		try {
			ioSample = new IOSample(samplePayload);
		} catch (IllegalArgumentException e) {
			throw new XBeeException("Couldn't create the IO sample.", e);
		} catch (NullPointerException e) {
			throw new XBeeException("Couldn't create the IO sample.", e);
		}
		return ioSample;
	}
	
	/**
	 * Returns the latest 802.15.4 IO packet and returns its value.
	 * 
	 * @return The value of the latest received 802.15.4 IO packet.
	 */
	private byte[] receiveRaw802IOPacket() {
		ioPacketReceived = false;
		ioPacketPayload = null;
		addPacketListener(IOPacketReceiveListener);
		synchronized (ioLock) {
			try {
				ioLock.wait(receiveTimeout);
			} catch (InterruptedException e) { }
		}
		removePacketListener(IOPacketReceiveListener);
		if (ioPacketReceived)
			return ioPacketPayload;
		return null;
	}
	
	/**
	 * Custom listener for 802.15.4 IO packets. It will try to receive an 
	 * 802.15.4 IO sample packet.
	 * 
	 * <p>When an IO sample packet is received, it saves its payload and 
	 * notifies the object that was waiting for the reception.</p>
	 */
	private IPacketReceiveListener IOPacketReceiveListener = new IPacketReceiveListener() {
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.IPacketReceiveListener#packetReceived(com.digi.xbee.api.packet.XBeePacket)
		 */
		@Override
		public void packetReceived(XBeePacket receivedPacket) {
			// Discard non API packets.
			if (!(receivedPacket instanceof XBeeAPIPacket))
				return;
			// If we already have received an IO packet, ignore this packet.
			if (ioPacketReceived)
				return;
			
			// Save the packet value (IO sample payload)
			switch (((XBeeAPIPacket)receivedPacket).getFrameType()) {
			case IO_DATA_SAMPLE_RX_INDICATOR:
				ioPacketPayload = ((IODataSampleRxIndicatorPacket)receivedPacket).getRFData();
				break;
			case RX_IO_16:
				ioPacketPayload = ((RX16IOPacket)receivedPacket).getRFData();
				break;
			case RX_IO_64:
				ioPacketPayload = ((RX64IOPacket)receivedPacket).getRFData();
				break;
			default:
				return;
			}
			// Set the IO packet received flag.
			ioPacketReceived = true;
			
			// Continue execution by notifying the lock object.
			synchronized (ioLock) {
				ioLock.notify();
			}
		}
	};
	
	/**
	 * Performs a software reset on this XBee device and blocks until the 
	 * process is completed.
	 * 
	 * @throws TimeoutException if the configured time expires while waiting 
	 *                          for the command reply.
	 * @throws XBeeException if there is any other XBee related exception.
	 */
	abstract public void reset() throws TimeoutException, XBeeException;
	
	/**
	 * Sets the given parameter with the provided value in this XBee device.
	 * 
	 * <p>If the 'apply configuration changes' option is enabled in this device,
	 * the configured value for the given parameter will be immediately applied, 
	 * if not the method {@code applyChanges()} must be invoked to apply it.</p>
	 * 
	 * <p>Use:</p>
	 * <ul>
	 * <li>Method {@code isApplyConfigurationChangesEnabled()} to know 
	 * if the 'apply configuration changes' option is enabled.</li>
	 * <li>Method {@code enableApplyConfigurationChanges(boolean)} to enable or
	 * disable this option.</li>
	 * </ul>
	 * 
	 * <p>To make parameter modifications persist through subsequent resets use 
	 * the {@code writeChanges()} method.</p>
	 * 
	 * @param parameter The name of the parameter to be set.
	 * @param parameterValue The value of the parameter to set.
	 * 
	 * @throws IllegalArgumentException if {@code parameter.length() != 2}.
	 * @throws NullPointerException if {@code parameter == null} or 
	 *                              if {@code parameterValue == null}.
	 * @throws TimeoutException if there is a timeout setting the parameter.
	 * @throws XBeeException if {@code parameter} is not supported by the module or
	 *                       if {@code parameterValue} is not supported or
	 *                       if there is any other XBee related exception.
	 * 
	 * @see #applyChanges()
	 * @see #enableApplyConfigurationChanges(boolean)
	 * @see #executeParameter(String) 
	 * @see #getParameter(String)
	 * @see #isApplyConfigurationChangesEnabled()
	 * @see #writeChanges()
	 */
	public void setParameter(String parameter, byte[] parameterValue) throws TimeoutException, XBeeException {
		if (parameterValue == null)
			throw new NullPointerException("Value of the parameter cannot be null.");
		
		sendParameter(parameter, parameterValue);
	}
	
	/**
	 * Gets the value of the given parameter from this XBee device.
	 * 
	 * @param parameter The name of the parameter to retrieve its value.
	 * 
	 * @return A byte array containing the value of the parameter.
	 * 
	 * @throws IllegalArgumentException if {@code parameter.length() != 2}.
	 * @throws NullPointerException if {@code parameter == null}.
	 * @throws TimeoutException if there is a timeout getting the parameter value.
	 * @throws XBeeException if {@code parameter} is not supported by the module or
	 *                       if there is any other XBee related exception.
	 * 
	 * @see #executeParameter(String)
	 * @see #setParameter(String, byte[])
	 */
	public byte[] getParameter(String parameter) throws TimeoutException, XBeeException {
		byte[] parameterValue = sendParameter(parameter, null);
		
		// Check if the response is null, if so throw an exception (maybe it was a write-only parameter).
		if (parameterValue == null)
			throw new OperationNotSupportedException("Couldn't get the '" + parameter + "' value.");
		return parameterValue;
	}
	
	/**
	 * Executes the given command in this XBee device.
	 * 
	 * <p>This method is intended to be used for those AT parameters that cannot 
	 * be read or written, they just execute some action in the XBee module.</p>
	 * 
	 * @param parameter The AT command to be executed.
	 * 
	 * @throws IllegalArgumentException if {@code parameter.length() != 2}.
	 * @throws NullPointerException if {@code parameter == null}.
	 * @throws TimeoutException if there is a timeout executing the parameter.
	 * @throws XBeeException if {@code parameter} is not supported by the module or
	 *                       if there is any other XBee related exception.
	 * 
	 * @see #getParameter(String)
	 * @see #setParameter(String, byte[])
	 */
	public void executeParameter(String parameter) throws TimeoutException, XBeeException {
		sendParameter(parameter, null);
	}
	
	/**
	 * Sends the given AT parameter to this XBee device with an optional 
	 * argument or value and returns the response (likely the value) of that 
	 * parameter in a byte array format.
	 * 
	 * @param parameter The name of the AT command to be executed.
	 * @param parameterValue The value of the parameter to set (if any).
	 * 
	 * @return A byte array containing the value of the parameter.
	 * 
	 * @throws IllegalArgumentException if {@code parameter.length() != 2}.
	 * @throws NullPointerException if {@code parameter == null}.
	 * @throws TimeoutException if there is a timeout executing the parameter.
	 * @throws XBeeException if {@code parameter} is not supported by the module or
	 *                       if {@code parameterValue} is not supported or
	 *                       if there is any other XBee related exception.
	 * 
	 * @see #getParameter(String)
	 * @see #executeParameter(String)
	 * @see #setParameter(String, byte[])
	 */
	private byte[] sendParameter(String parameter, byte[] parameterValue) throws TimeoutException, XBeeException {
		if (parameter == null)
			throw new NullPointerException("Parameter cannot be null.");
		if (parameter.length() != 2)
			throw new IllegalArgumentException("Parameter must contain exactly 2 characters.");
		
		ATCommand atCommand = new ATCommand(parameter, parameterValue);
		
		// Create and send the AT Command.
		ATCommandResponse response = null;
		try {
			response = sendATCommand(atCommand);
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
		
		// Check if AT Command response is valid.
		checkATCommandResponseIsValid(response);
		
		// Return the response value.
		return response.getResponse();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String id = getNodeID() == null ? "" : getNodeID();
		String addr64 = get64BitAddress() == null || get64BitAddress().equals(XBee64BitAddress.UNKNOWN_ADDRESS) ? 
				"" : get64BitAddress().toString();
		
		if (id.length() == 0 && addr64.length() == 0)
			return connectionInterface.toString();
		
		StringBuilder message = new StringBuilder(connectionInterface.toString());
		message.append(addr64);
		if (id.length() > 0) {
			message.append(" (");
			message.append(id);
			message.append(")");
		}
		message.append(" - ");
		
		return message.toString();
	}
	
	/**
	 * Enables or disables the 'apply configuration changes' option for this 
	 * device.
	 * 
	 * <p>Enabling this option means that when any parameter of this XBee 
	 * device is set, it will be also applied.</p>
	 * 
	 * <p>If this option is disabled, the method {@code applyChanges()} must be 
	 * used in order to apply the changes in all the parameters that were 
	 * previously set.</p>
	 * 
	 * @param enabled {@code true} to apply configuration changes when an XBee 
	 *                parameter is set, {@code false} otherwise.
	 * 
	 * @see #applyChanges()
	 * @see #isApplyConfigurationChangesEnabled()
	 */
	public void enableApplyConfigurationChanges(boolean enabled) {
		applyConfigurationChanges = enabled;
	}
	
	/**
	 * Returns whether the 'apply configuration changes' option is enabled in 
	 * this device.
	 * 
	 * <p>If this option is enabled, when any parameter of this XBee device is 
	 * set, it will be also applied.</p>
	 * 
	 * <p>If this option is disabled, the method {@code applyChanges()} must be 
	 * used in order to apply the changes in all the parameters that were 
	 * previously set.</p>
	 * 
	 * @return {@code true} if the option is enabled, {@code false} otherwise.
	 * 
	 * @see #applyChanges()
	 * @see #enableApplyConfigurationChanges(boolean)
	 */
	public boolean isApplyConfigurationChangesEnabled() {
		return applyConfigurationChanges;
	}
	
	/**
	 * Configures the 16-bit address (network address) of this XBee device with 
	 * the provided one.
	 * 
	 * @param xbee16BitAddress The new 16-bit address.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code xbee16BitAddress == null}.
	 * @throws TimeoutException if there is a timeout setting the address.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #get16BitAddress()
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 */
	protected void set16BitAddress(XBee16BitAddress xbee16BitAddress) throws TimeoutException, XBeeException {
		if (xbee16BitAddress == null)
			throw new NullPointerException("16-bit address canot be null.");
		
		setParameter("MY", xbee16BitAddress.getValue());
		
		this.xbee16BitAddress = xbee16BitAddress;
	}
	
	/**
	 * Returns the operating PAN ID (Personal Area Network Identifier) of 
	 * this XBee device.
	 * 
	 * <p>For modules to communicate they must be configured with the same 
	 * identifier. Only modules with matching IDs can communicate with each 
	 * other.This parameter allows multiple networks to co-exist on the same 
	 * physical channel.</p>
	 * 
	 * @return The operating PAN ID of this XBee device.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout getting the PAN ID.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setPANID(byte[])
	 */
	public byte[] getPANID() throws TimeoutException, XBeeException {
		switch (getXBeeProtocol()) {
		case ZIGBEE:
			return getParameter("OP");
		default:
			return getParameter("ID");
		}
	}
	
	/**
	 * Sets the PAN ID (Personal Area Network Identifier) of this XBee device.
	 * 
	 * <p>For modules to communicate they must be configured with the same 
	 * identifier. Only modules with matching IDs can communicate with each 
	 * other.This parameter allows multiple networks to co-exist on the same 
	 * physical channel.</p>
	 * 
	 * @param panID The new PAN ID of this XBee device.
	 * 
	 * @throws IllegalArgumentException if {@code panID.length == 0} or 
	 *                                  if {@code panID.length > 8}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code panID == null}.
	 * @throws TimeoutException if there is a timeout setting the PAN ID.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getPANID()
	 */
	public void setPANID(byte[] panID) throws TimeoutException, XBeeException {
		if (panID == null)
			throw new NullPointerException("PAN ID cannot be null.");
		if (panID.length == 0)
			throw new IllegalArgumentException("Length of the PAN ID cannot be 0.");
		if (panID.length > 8)
			throw new IllegalArgumentException("Length of the PAN ID cannot be longer than 8 bytes.");
		
		setParameter("ID", panID);
	}
	
	/**
	 * Returns the output power level at which this XBee device transmits 
	 * conducted power.
	 * 
	 * @return The output power level of this XBee device.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout getting the power level.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setPowerLevel(PowerLevel)
	 * @see com.digi.xbee.api.models.PowerLevel
	 */
	public PowerLevel getPowerLevel() throws TimeoutException, XBeeException {
		byte[] powerLevelValue = getParameter("PL");
		return PowerLevel.get(ByteUtils.byteArrayToInt(powerLevelValue));
	}
	
	/**
	 * Sets the output power level at which this XBee device transmits 
	 * conducted power.
	 * 
	 * @param powerLevel The new output power level to be set in this XBee 
	 *                   device.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code powerLevel == null}.
	 * @throws TimeoutException if there is a timeout setting the power level.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getPowerLevel()
	 * @see com.digi.xbee.api.models.PowerLevel
	 */
	public void setPowerLevel(PowerLevel powerLevel) throws TimeoutException, XBeeException {
		if (powerLevel == null)
			throw new NullPointerException("Power level cannot be null.");
		
		setParameter("PL", ByteUtils.intToByteArray(powerLevel.getValue()));
	}
	
	/**
	 * Returns the current association status of this XBee device.
	 * 
	 * <p>It indicates occurrences of errors during the last association 
	 * request.</p>
	 * 
	 * @return The association indication status of the XBee device.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout getting the association 
	 *                          indication status.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #forceDisassociate()
	 * @see com.digi.xbee.api.models.AssociationIndicationStatus
	 */
	protected AssociationIndicationStatus getAssociationIndicationStatus() throws TimeoutException, XBeeException {
		byte[] associationIndicationValue = getParameter("AI");
		return AssociationIndicationStatus.get(ByteUtils.byteArrayToInt(associationIndicationValue));
	}
	
	/**
	 * Forces this XBee device to immediately disassociate from the network and 
	 * re-attempt to associate.
	 * 
	 * <p>Only valid for End Devices.</p>
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout executing the 
	 *         disassociation command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getAssociationIndicationStatus()
	 */
	protected void forceDisassociate() throws TimeoutException, XBeeException {
		executeParameter("DA");
	}
	
	/**
	 * Writes configurable parameter values to the non-volatile memory of this 
	 * XBee device so that parameter modifications persist through subsequent 
	 * resets.
	 * 
	 * <p>Parameters values remain in this device's memory until overwritten by 
	 * subsequent use of this method.</p>
	 * 
	 * <p>If changes are made without writing them to non-volatile memory, the 
	 * module reverts back to previously saved parameters the next time the 
	 * module is powered-on.</p>
	 * 
	 * <p>Writing the parameter modifications does not mean those values are 
	 * immediately applied, this depends on the status of the 'apply 
	 * configuration changes' option. Use method 
	 * {@code isApplyConfigurationChangesEnabled()} to get its status and 
	 * {@code enableApplyConfigurationChanges(boolean)} to enable/disable the 
	 * option. If it is disable method {@code applyChanges()} can be used in 
	 * order to manually apply the changes.</p>
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout executing the write 
	 *                          changes command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #applyChanges()
	 * @see #enableApplyConfigurationChanges(boolean)
	 * @see #isApplyConfigurationChangesEnabled()
	 * @see #setParameter(String, byte[])
	 */
	public void writeChanges() throws TimeoutException, XBeeException {
		executeParameter("WR");
	}

	/**
	 * Enables the Bluetooth interface of this XBee device.
	 *
	 * <p>To work with this interface, you must also configure the Bluetooth
	 * password if not done previously. You can use the
	 * {@link #updateBluetoothPassword(String)} method for that purpose.</p>
	 *
	 * <p>Note that your device must have Bluetooth Low Energy support to use
	 * this method.</p>
	 *
	 * @throws TimeoutException if there is a timeout enabling the interface.
	 * @throws XBeeException if there is any other XBee related exception.
	 *
	 * @see #disableBluetooth()
	 * @see #updateBluetoothPassword(String)
	 *
	 * @since 1.3.0
	 */
	public void enableBluetooth() throws TimeoutException, XBeeException {
		enableBluetooth(true);
	}

	/**
	 * Disables the Bluetooth interface of this XBee device.
	 *
	 * <p>Note that your device must have Bluetooth Low Energy support to use
	 * this method.</p>
	 *
	 * @throws TimeoutException if there is a timeout disabling the interface.
	 * @throws XBeeException if there is any other XBee related exception.
	 *
	 * @see #enableBluetooth()
	 *
	 * @since 1.3.0
	 */
	public void disableBluetooth() throws TimeoutException, XBeeException {
		enableBluetooth(false);
	}

	/**
	 * Enables or disables the Bluetooth interface of this XBee device.
	 *
	 * @param enable {@code true} to enable the Bluetooth interface,
	 *               {@code false} to disable it.
	 *
	 * @throws TimeoutException if there is a timeout enabling or disabling the
	 *                          interface.
	 * @throws XBeeException if there is any other XBee related exception.
	 *
	 * @since 1.3.0
	 */
	private void enableBluetooth(boolean enable) throws TimeoutException, XBeeException {
		setParameter("BT", new byte[] {(byte) (enable ? 0x01 : 0x00)});
		writeChanges();
		applyChanges();
	}

	/**
	 * Reads and returns the EUI-48 Bluetooth MAC address of this XBee device
	 * in a format such as {@code 00112233AABB}.
	 *
	 * <p>Note that your device must have Bluetooth Low Energy support to use
	 * this method.</p>
	 *
	 * @return The Bluetooth MAC address.
	 *
	 * @throws TimeoutException if there is a timeout reading the MAC address.
	 * @throws XBeeException if there is any other XBee related exception.
	 *
	 * @since 1.3.0
	 */
	public String getBluetoothMacAddress() throws TimeoutException, XBeeException {
		return HexUtils.byteArrayToHexString(getParameter("BL"));
	}

	/**
	 * Changes the password of this Bluetooth device with the new one provided.
	 *
	 * <p>Note that your device must have Bluetooth Low Energy support to use
	 * this method.</p>
	 *
	 * @param newPassword New Bluetooth password.
	 *
	 * @throws TimeoutException if there is a timeout changing the Bluetooth
	 *                          password.
	 * @throws XBeeException if there is any other XBee related exception.
	 *
	 * @since 1.3.0
	 */
	public void updateBluetoothPassword(String newPassword) throws TimeoutException, XBeeException {
		// Generate a new salt and verifier.
		byte[] salt = SrpUtils.generateSalt();
		byte[] verifier;
		try {
			verifier = SrpUtils.generateVerifier(salt, newPassword);
		} catch (IOException | NoSuchAlgorithmException e) {
			throw new XBeeException(e);
		}

		// Set the salt.
		setParameter("$S", salt);

		// Set the verifier (split in 4 settings).
		int index = 0;
		int atLength = verifier.length / 4;

		setParameter("$V", Arrays.copyOfRange(verifier, index, index + atLength));
		index += atLength;
		setParameter("$W", Arrays.copyOfRange(verifier, index, index + atLength));
		index += atLength;
		setParameter("$X", Arrays.copyOfRange(verifier, index, index + atLength));
		index += atLength;
		setParameter("$Y", Arrays.copyOfRange(verifier, index, index + atLength));

		// Write and apply changes.
		writeChanges();
		applyChanges();
	}
	
	/**
	 * Opens the connection interface associated with this XBee device.
	 * 
	 * <p>When opening the device an information reading process is 
	 * automatically performed. This includes:</p>
	 * 
	 * <ul>
	 * <li>64-bit address.</li>
	 * <li>Node Identifier.</li>
	 * <li>Hardware version.</li>
	 * <li>Firmware version.</li>
	 * <li>XBee device protocol.</li>
	 * <li>16-bit address (not for DigiMesh modules).</li>
	 * </ul>
	 *
	 * @throws BluetoothAuthenticationException if there is any problem in the
	 *                                          Bluetooth authentication.
	 * @throws InterfaceAlreadyOpenException if this device connection is 
	 *                                       already open.
	 * @throws XBeeException if there is any problem opening this device 
	 *                       connection.
	 * 
	 * @see #close()
	 * @see #isOpen()
	 */
	protected void open() throws XBeeException {
		logger.info(toString() + "Opening the connection interface...");
		
		if (isRemote())
			throw new OperationNotSupportedException("Remote devices cannot be open.");
		
		// First, verify that the connection is not already open.
		if (connectionInterface.isOpen())
			throw new InterfaceAlreadyOpenException();
		
		// Connect the interface.
		connectionInterface.open();
		
		logger.info(toString() + "Connection interface open.");
		
		// Initialize the data reader.
		dataReader = new DataReader(connectionInterface, operatingMode, this);
		dataReader.start();
		
		// Wait 10 milliseconds until the dataReader thread is started.
		// This is because when the connection is opened immediately after 
		// closing it, there is sometimes a concurrency problem and the 
		// dataReader thread never dies.
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {}

		if (connectionInterface.getConnectionType() == ConnectionType.BLUETOOTH) {
			// The communication in Bluetooth is always done through API frames
			// regardless of the AP setting.
			operatingMode = OperatingMode.API;
			dataReader.setXBeeReaderMode(operatingMode);

			// Perform the Bluetooth authentication.
			try {
				logger.info(toString() + "Starting Bluetooth authentication...");
				BluetoothAuthentication auth = new BluetoothAuthentication(this, bluetoothPassword);
				auth.authenticate();
				((AbstractBluetoothInterface) connectionInterface).setEncryptionKeys(auth.getKey(), auth.getTxNonce(), auth.getRxNonce());
				logger.info(toString() + "Authentication finished successfully.");
			} catch (BluetoothAuthenticationException e) {
				close();
				throw e;
			}
		} else {
			// Determine the operating mode of the XBee device if it is unknown.
			if (operatingMode == OperatingMode.UNKNOWN)
				operatingMode = determineOperatingMode();

			// Check if the operating mode is a valid and supported one.
			if (operatingMode == OperatingMode.UNKNOWN) {
				close();
				throw new InvalidOperatingModeException("Could not determine operating mode.");
			} else if (operatingMode == OperatingMode.AT) {
				close();
				throw new InvalidOperatingModeException(operatingMode);
			}
		}
		
		// Read the device info (obtain its parameters and protocol).
		try {
			readDeviceInfo();
		} catch (ATCommandException e) {
			throw new XBeeException("Error reading device information.", e);
		}
	}
	
	/**
	 * Closes the connection interface associated with this XBee device.
	 * 
	 * @see #isOpen()
	 * @see #open()
	 */
	protected void close() {
		// Stop XBee reader.
		if (dataReader != null && dataReader.isRunning())
			dataReader.stopReader();
		// Close interface.
		connectionInterface.close();
		logger.info(toString() + "Connection interface closed.");
	}
	
	/**
	 * Determines the operating mode of this XBee device.
	 * 
	 * @return The operating mode of the XBee device.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws OperationNotSupportedException if the packet is being sent from 
	 *                                        a remote device.
	 * 
	 * @see com.digi.xbee.api.models.OperatingMode
	 */
	protected OperatingMode determineOperatingMode() throws OperationNotSupportedException {
		try {
			// Check if device is in API or API Escaped operating modes.
			operatingMode = OperatingMode.API;
			dataReader.setXBeeReaderMode(operatingMode);
			
			ATCommandResponse response = sendATCommand(new ATCommand("AP"));
			if (response.getResponse() != null && response.getResponse().length > 0) {
				if (response.getResponse()[0] != OperatingMode.API.getID()) {
					operatingMode = OperatingMode.API_ESCAPE;
					dataReader.setXBeeReaderMode(operatingMode);
				}
				logger.debug(toString() + "Using {}.", operatingMode.getName());
				return operatingMode;
			}
		} catch (TimeoutException e) {
			// Check if device is in AT operating mode.
			operatingMode = OperatingMode.AT;
			dataReader.setXBeeReaderMode(operatingMode);
			
			try {
				// It is necessary to wait at least 1 second to enter in 
				// command mode after sending any data to the device.
				Thread.sleep(TIMEOUT_BEFORE_COMMAND_MODE);
				// Try to enter in AT command mode, if so the module is in AT mode.
				boolean success = enterATCommandMode();
				if (success)
					return OperatingMode.AT;
			} catch (TimeoutException e1) {
				logger.error(e1.getMessage(), e1);
			} catch (InvalidOperatingModeException e1) {
				logger.error(e1.getMessage(), e1);
			} catch (InterruptedException e1) {
				logger.error(e1.getMessage(), e1);
			}
		} catch (InvalidOperatingModeException e) {
			logger.error("Invalid operating mode", e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return OperatingMode.UNKNOWN;
	}
	
	/**
	 * Attempts to put this device in AT Command mode. Only valid if device is 
	 * working in AT mode.
	 * 
	 * @return {@code true} if the device entered in AT command mode, 
	 *         {@code false} otherwise.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws InvalidOperatingModeException if the operating mode cannot be 
	 *                                       determined or is not supported.
	 * @throws TimeoutException if the configured time for this device expires.
	 */
	private boolean enterATCommandMode() throws InvalidOperatingModeException, TimeoutException {
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		if (operatingMode != OperatingMode.AT)
			throw new InvalidOperatingModeException("Invalid mode. Command mode can be only accessed while in AT mode.");
		
		// Enter in AT command mode (send '+++'). The process waits 1,5 seconds for the 'OK\n'.
		byte[] readData = new byte[256];
		try {
			// Send the command mode sequence.
			connectionInterface.writeData(COMMAND_MODE_CHAR.getBytes());
			connectionInterface.writeData(COMMAND_MODE_CHAR.getBytes());
			connectionInterface.writeData(COMMAND_MODE_CHAR.getBytes());
			
			// Wait some time to let the module generate a response.
			Thread.sleep(TIMEOUT_ENTER_COMMAND_MODE);
			
			// Read data from the device (it should answer with 'OK\r').
			int readBytes = connectionInterface.readData(readData);
			if (readBytes < COMMAND_MODE_OK.length())
				throw new TimeoutException();
			
			// Check if the read data is 'OK\r'.
			String readString = new String(readData, 0, readBytes);
			if (!readString.contains(COMMAND_MODE_OK))
				return false;
			
			// Read data was 'OK\r'.
			return true;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * Returns whether the connection interface associated to this device is 
	 * already open.
	 * 
	 * @return {@code true} if the interface is open, {@code false} otherwise.
	 * 
	 * @see #close()
	 * @see #open()
	 */
	protected boolean isOpen() {
		if (connectionInterface != null)
			return connectionInterface.isOpen();
		return false;
	}
	
	/**
	 * Returns the network associated with this XBee device.
	 * 
	 * @return The XBee network of the device.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * 
	 * @see XBeeNetwork
	 */
	protected XBeeNetwork getNetwork() {
		if (isRemote())
			throw new RuntimeException("Remote devices do not have network.");
		
		if (!isOpen())
			throw new InterfaceNotOpenException();
		
		if (network == null)
			network = new XBeeNetwork(this);
		return network;
	}
	
	/**
	 * Returns this XBee device configured timeout for receiving packets in 
	 * synchronous operations.
	 * 
	 * @return The current receive timeout in milliseconds.
	 * 
	 * @see #setReceiveTimeout(int)
	 */
	protected int getReceiveTimeout() {
		return receiveTimeout;
	}
	
	/**
	 * Configures this XBee device timeout in milliseconds for receiving 
	 * packets in synchronous operations.
	 *  
	 * @param receiveTimeout The new receive timeout in milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code receiveTimeout < 0}.
	 * 
	 * @see #getReceiveTimeout()
	 */
	protected void setReceiveTimeout(int receiveTimeout) {
		if (receiveTimeout < 0)
			throw new IllegalArgumentException("Receive timeout cannot be less than 0.");
		
		this.receiveTimeout = receiveTimeout;
	}
	
	/**
	 * Sends asynchronously the provided data to the XBee device of the network 
	 * corresponding to the given 64-bit address.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @param address The 64-bit address of the XBee that will receive the data.
	 * @param data Byte array containing the data to be sent.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code address == null} or 
	 *                              if {@code data == null}.
	 * @throws XBeeException if there is any XBee related exception.
	 * 
	 * @see #sendData(RemoteXBeeDevice, byte[])
	 * @see #sendData(XBee64BitAddress, byte[])
	 * @see #sendData(XBee64BitAddress, XBee16BitAddress, byte[])
	 * @see #sendDataAsync(RemoteXBeeDevice, byte[])
	 * @see #sendDataAsync(XBee64BitAddress, XBee16BitAddress, byte[])
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	protected void sendDataAsync(XBee64BitAddress address, byte[] data) throws XBeeException {
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address == null)
			throw new NullPointerException("Address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send data to a remote device from a remote device.");
		
		logger.debug(toString() + "Sending data asynchronously to {} >> {}.", address, HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket;
		switch (getXBeeProtocol()) {
		case RAW_802_15_4:
			xbeePacket = new TX64Packet(getNextFrameID(), address, XBeeTransmitOptions.NONE, data);
			break;
		default:
			xbeePacket = new TransmitPacket(getNextFrameID(), address, XBee16BitAddress.UNKNOWN_ADDRESS, 0, XBeeTransmitOptions.NONE, data);
		}
		sendAndCheckXBeePacket(xbeePacket, true);
	}
	
	/**
	 * Sends asynchronously the provided data to the XBee device of the network 
	 * corresponding to the given 64-bit/16-bit address.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @param address64Bit The 64-bit address of the XBee that will receive the 
	 *                     data.
	 * @param address16Bit The 16-bit address of the XBee that will receive the 
	 *                     data. If it is unknown the 
	 *                     {@code XBee16BitAddress.UNKNOWN_ADDRESS} must be 
	 *                     used.
	 * @param data Byte array containing the data to be sent.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code address64Bit == null} or 
	 *                              if {@code address16Bit == null} or
	 *                              if {@code data == null}.
	 * @throws XBeeException if a remote device is trying to send data or 
	 *                       if there is any other XBee related exception.
	 * 
	 * @see #sendData(RemoteXBeeDevice, byte[])
	 * @see #sendData(XBee64BitAddress, byte[])
	 * @see #sendData(XBee64BitAddress, XBee16BitAddress, byte[])
	 * @see #sendDataAsync(RemoteXBeeDevice, byte[])
	 * @see #sendDataAsync(XBee64BitAddress, byte[])
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	protected void sendDataAsync(XBee64BitAddress address64Bit, XBee16BitAddress address16Bit, byte[] data) throws XBeeException {
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address64Bit == null)
			throw new NullPointerException("64-bit address cannot be null");
		if (address16Bit == null)
			throw new NullPointerException("16-bit address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send data to a remote device from a remote device.");
		
		logger.debug(toString() + "Sending data asynchronously to {}[{}] >> {}.", 
				address64Bit, address16Bit, HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new TransmitPacket(getNextFrameID(), address64Bit, address16Bit, 0, XBeeTransmitOptions.NONE, data);
		sendAndCheckXBeePacket(xbeePacket, true);
	}
	
	/**
	 * Sends the provided data to the provided XBee device asynchronously 
	 * choosing the optimal send method depending on the protocol of the local 
	 * XBee device.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @param remoteXBeeDevice The XBee device of the network that will receive the 
	 *                   data.
	 * @param data Byte array containing the data to be sent.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code remoteXBeeDevice == null} or 
	 *                              if {@code data == null}.
	 * @throws XBeeException if there is any XBee related exception.
	 * 
	 * @see #sendData(RemoteXBeeDevice, byte[])
	 * @see #sendData(XBee64BitAddress, byte[])
	 * @see #sendData(XBee64BitAddress, XBee16BitAddress, byte[])
	 * @see #sendDataAsync(XBee64BitAddress, byte[])
	 * @see #sendDataAsync(XBee64BitAddress, XBee16BitAddress, byte[])
	 * @see RemoteXBeeDevice
	 */
	protected void sendDataAsync(RemoteXBeeDevice remoteXBeeDevice, byte[] data) throws XBeeException {
		if (remoteXBeeDevice == null)
			throw new NullPointerException("Remote XBee device cannot be null");
		
		switch (getXBeeProtocol()) {
		case ZIGBEE:
		case DIGI_POINT:
			if (remoteXBeeDevice.get64BitAddress() != null && remoteXBeeDevice.get16BitAddress() != null)
				sendDataAsync(remoteXBeeDevice.get64BitAddress(), remoteXBeeDevice.get16BitAddress(), data);
			else
				sendDataAsync(remoteXBeeDevice.get64BitAddress(), data);
			break;
		case RAW_802_15_4:
			if (this instanceof Raw802Device) {
				if (remoteXBeeDevice.get64BitAddress() != null)
					((Raw802Device)this).sendDataAsync(remoteXBeeDevice.get64BitAddress(), data);
				else
					((Raw802Device)this).sendDataAsync(remoteXBeeDevice.get16BitAddress(), data);
			} else
				sendDataAsync(remoteXBeeDevice.get64BitAddress(), data);
			break;
		case DIGI_MESH:
		default:
			sendDataAsync(remoteXBeeDevice.get64BitAddress(), data);
		}
	}
	
	/**
	 * Sends the provided data to the XBee device of the network corresponding 
	 * to the given 64-bit address.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations use the method 
	 * {@link #sendDataAsync(XBee64BitAddress, byte[])}.</p>
	 * 
	 * @param address The 64-bit address of the XBee that will receive the data.
	 * @param data Byte array containing the data to be sent.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code address == null} or 
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 * @see #sendData(RemoteXBeeDevice, byte[])
	 * @see #sendData(XBee64BitAddress, XBee16BitAddress, byte[])
	 * @see #sendDataAsync(RemoteXBeeDevice, byte[])
	 * @see #sendDataAsync(XBee64BitAddress, byte[])
	 * @see #sendDataAsync(XBee64BitAddress, XBee16BitAddress, byte[])
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	protected void sendData(XBee64BitAddress address, byte[] data) throws TimeoutException, XBeeException {
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address == null)
			throw new NullPointerException("Address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send data to a remote device from a remote device.");
		
		logger.debug(toString() + "Sending data to {} >> {}.", address, HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket;
		switch (getXBeeProtocol()) {
		case RAW_802_15_4:
			xbeePacket = new TX64Packet(getNextFrameID(), address, XBeeTransmitOptions.NONE, data);
			break;
		default:
			xbeePacket = new TransmitPacket(getNextFrameID(), address, XBee16BitAddress.UNKNOWN_ADDRESS, 0, XBeeTransmitOptions.NONE, data);
		}
		sendAndCheckXBeePacket(xbeePacket, false);
	}
	
	/**
	 * Sends the provided data to the XBee device of the network corresponding 
	 * to the given 64-bit/16-bit address.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations use the method 
	 * {@link #sendDataAsync(XBee64BitAddress, XBee16BitAddress, byte[])}.</p>
	 * 
	 * @param address64Bit The 64-bit address of the XBee that will receive the 
	 *                     data.
	 * @param address16Bit The 16-bit address of the XBee that will receive the 
	 *                     data. If it is unknown the 
	 *                     {@code XBee16BitAddress.UNKNOWN_ADDRESS} must be 
	 *                     used.
	 * @param data Byte array containing the data to be sent.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code address64Bit == null} or 
	 *                              if {@code address16Bit == null} or
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if a remote device is trying to send data or 
	 *                       if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 * @see #sendData(RemoteXBeeDevice, byte[])
	 * @see #sendData(XBee64BitAddress, byte[])
	 * @see #sendDataAsync(RemoteXBeeDevice, byte[])
	 * @see #sendDataAsync(XBee64BitAddress, byte[])
	 * @see #sendDataAsync(XBee64BitAddress, XBee16BitAddress, byte[])
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	protected void sendData(XBee64BitAddress address64Bit, XBee16BitAddress address16Bit, byte[] data) throws TimeoutException, XBeeException {
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address64Bit == null)
			throw new NullPointerException("64-bit address cannot be null");
		if (address16Bit == null)
			throw new NullPointerException("16-bit address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send data to a remote device from a remote device.");
		
		logger.debug(toString() + "Sending data to {}[{}] >> {}.", 
				address64Bit, address16Bit, HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new TransmitPacket(getNextFrameID(), address64Bit, address16Bit, 0, XBeeTransmitOptions.NONE, data);
		sendAndCheckXBeePacket(xbeePacket, false);
	}
	
	/**
	 * Sends the provided data to the given XBee device choosing the optimal 
	 * send method depending on the protocol of the local XBee device.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations use the method 
	 * {@link #sendDataAsync(RemoteXBeeDevice, byte[])}.</p>
	 * 
	 * @param remoteXBeeDevice The XBee device of the network that will receive 
	 *                         the data.
	 * @param data Byte array containing the data to be sent.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code xbeeDevice == null} or 
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 * @see #sendData(XBee64BitAddress, byte[])
	 * @see #sendData(XBee64BitAddress, XBee16BitAddress, byte[])
	 * @see #sendDataAsync(RemoteXBeeDevice, byte[])
	 * @see #sendDataAsync(XBee64BitAddress, byte[])
	 * @see #sendDataAsync(XBee64BitAddress, XBee16BitAddress, byte[])
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 */
	protected void sendData(RemoteXBeeDevice remoteXBeeDevice, byte[] data) throws TimeoutException, XBeeException {
		if (remoteXBeeDevice == null)
			throw new NullPointerException("Remote XBee device cannot be null");
		
		switch (getXBeeProtocol()) {
		case ZIGBEE:
		case DIGI_POINT:
			if (remoteXBeeDevice.get64BitAddress() != null && remoteXBeeDevice.get16BitAddress() != null)
				sendData(remoteXBeeDevice.get64BitAddress(), remoteXBeeDevice.get16BitAddress(), data);
			else
				sendData(remoteXBeeDevice.get64BitAddress(), data);
			break;
		case RAW_802_15_4:
			if (this instanceof Raw802Device) {
				if (remoteXBeeDevice.get64BitAddress() != null)
					((Raw802Device)this).sendData(remoteXBeeDevice.get64BitAddress(), data);
				else
					((Raw802Device)this).sendData(remoteXBeeDevice.get16BitAddress(), data);
			} else
				sendData(remoteXBeeDevice.get64BitAddress(), data);
			break;
		case DIGI_MESH:
		default:
			sendData(remoteXBeeDevice.get64BitAddress(), data);
		}
	}
	
	/**
	 * Sends the provided data to all the XBee nodes of the network (broadcast).
	 * 
	 * <p>This method blocks till a success or error transmit status arrives or 
	 * the configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * @param data Byte array containing the data to be sent.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 */
	protected void sendBroadcastData(byte[] data) throws TimeoutException, XBeeException {
		sendData(XBee64BitAddress.BROADCAST_ADDRESS, data);
	}
	
	/**
	 * Sends asynchronously the provided data in application layer mode to the 
	 * XBee device of the network corresponding to the given 64-bit address. 
	 * Application layer mode means that you need to specify the application 
	 * layer fields to be sent with the data.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @param address The 64-bit address of the XBee that will receive the data.
	 * @param sourceEndpoint Source endpoint for the transmission.
	 * @param destEndpoint Destination endpoint for the transmission.
	 * @param clusterID Cluster ID used in the transmission.
	 * @param profileID Profile ID used in the transmission.
	 * @param data Byte array containing the data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code sourceEndpoint < 0} or 
	 *                                  if {@code sourceEndpoint > 0xFF} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 0xFF} or 
	 *                                  if {@code clusterID < 0} or 
	 *                                  if {@code clusterID > 0xFFFF} or 
	 *                                  if {@code profileID < 0} or 
	 *                                  if {@code profileID > 0xFFFF}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code address == null} or 
	 *                              if {@code data == null}.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #sendExplicitData(RemoteXBeeDevice, int, int, int, int, byte[])
	 * @see #sendExplicitData(XBee64BitAddress, int, int, int, int, byte[])
	 * @see #sendExplicitData(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[])
	 * @see #sendExplicitDataAsync(RemoteXBeeDevice, int, int, int, int, byte[])
	 * @see #sendExplicitDataAsync(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[])
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	protected void sendExplicitDataAsync(XBee64BitAddress address, int sourceEndpoint, int destEndpoint, int clusterID, 
			int profileID, byte[] data) throws XBeeException {
		if (address == null)
			throw new NullPointerException("Address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null.");
		if (sourceEndpoint < 0 || sourceEndpoint > 0xFF)
			throw new IllegalArgumentException("Source endpoint must be between 0 and 0xFF.");
		if (destEndpoint < 0 || destEndpoint > 0xFF)
			throw new IllegalArgumentException("Destination endpoint must be between 0 and 0xFF.");
		if (clusterID < 0 || clusterID > 0xFFFF)
			throw new IllegalArgumentException("Cluster ID must be between 0 and 0xFFFF.");
		if (profileID < 0 || profileID > 0xFFFF)
			throw new IllegalArgumentException("Profile ID must be between 0 and 0xFFFF.");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send explicit data to a remote device from a remote device.");
		
		logger.debug(toString() + "Sending explicit data asynchronously to {} [{} - {} - {} - {}] >> {}.", address, 
				String.format("%02X", sourceEndpoint), String.format("%02X", destEndpoint), 
				String.format("%04X", clusterID), String.format("%04X", profileID), 
				HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new ExplicitAddressingPacket(getNextFrameID(), address, XBee16BitAddress.UNKNOWN_ADDRESS, sourceEndpoint, destEndpoint, clusterID, profileID, 0, XBeeTransmitOptions.NONE, data);
		sendAndCheckXBeePacket(xbeePacket, true);
	}
	
	/**
	 * Sends asynchronously the provided data in application layer mode to the 
	 * XBee device of the network corresponding to the given 64-bit/16-bit 
	 * address. Application layer mode means that you need to specify the 
	 * application layer fields to be sent with the data.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @param address64Bit The 64-bit address of the XBee that will receive the 
	 *                     data.
	 * @param address16Bit The 16-bit address of the XBee that will receive the 
	 *                     data. If it is unknown the 
	 *                     {@code XBee16BitAddress.UNKNOWN_ADDRESS} must be 
	 *                     used.
	 * @param sourceEndpoint Source endpoint for the transmission.
	 * @param destEndpoint Destination endpoint for the transmission.
	 * @param clusterID Cluster ID used in the transmission.
	 * @param profileID Profile ID used in the transmission.
	 * @param data Byte array containing the data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code sourceEndpoint < 0} or 
	 *                                  if {@code sourceEndpoint > 0xFF} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 0xFF} or 
	 *                                  if {@code clusterID < 0} or 
	 *                                  if {@code clusterID > 0xFFFF} or 
	 *                                  if {@code profileID < 0} or 
	 *                                  if {@code profileID > 0xFFFF}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code address64Bit == null} or 
	 *                              if {@code address16Bit == null} or 
	 *                              if {@code data == null}.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #sendExplicitData(RemoteXBeeDevice, int, int, int, int, byte[])
	 * @see #sendExplicitData(XBee64BitAddress, int, int, int, int, byte[])
	 * @see #sendExplicitData(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[])
	 * @see #sendExplicitDataAsync(RemoteXBeeDevice, int, int, int, int, byte[])
	 * @see #sendExplicitDataAsync(XBee64BitAddress, int, int, int, int, byte[])
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	protected void sendExplicitDataAsync(XBee64BitAddress address64Bit, XBee16BitAddress address16Bit, int sourceEndpoint, 
			int destEndpoint, int clusterID, int profileID, byte[] data) throws XBeeException {
		if (address64Bit == null)
			throw new NullPointerException("64-bit address cannot be null.");
		if (address16Bit == null)
			throw new NullPointerException("16-bit address cannot be null.");
		if (data == null)
			throw new NullPointerException("Data cannot be null.");
		if (sourceEndpoint < 0 || sourceEndpoint > 0xFF)
			throw new IllegalArgumentException("Source endpoint must be between 0 and 0xFF.");
		if (destEndpoint < 0 || destEndpoint > 0xFF)
			throw new IllegalArgumentException("Destination endpoint must be between 0 and 0xFF.");
		if (clusterID < 0 || clusterID > 0xFFFF)
			throw new IllegalArgumentException("Cluster ID must be between 0 and 0xFFFF.");
		if (profileID < 0 || profileID > 0xFFFF)
			throw new IllegalArgumentException("Profile ID must be between 0 and 0xFFFF.");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send explicit data to a remote device from a remote device.");
		
		logger.debug(toString() + "Sending explicit data asynchronously to {}[{}] [{} - {} - {} - {}] >> {}.", address64Bit, address16Bit, 
				String.format("%02X", sourceEndpoint), String.format("%02X", destEndpoint), 
				String.format("%04X", clusterID), String.format("%04X", profileID), 
				HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new ExplicitAddressingPacket(getNextFrameID(), address64Bit, address16Bit, sourceEndpoint, destEndpoint, clusterID, profileID, 0, XBeeTransmitOptions.NONE, data);
		sendAndCheckXBeePacket(xbeePacket, true);
	}
	
	/**
	 * Sends asynchronously the provided data in application layer mode to the 
	 * provided XBee device choosing the optimal send method depending on the 
	 * protocol of the local XBee device. Application layer mode means that you 
	 * need to specify the application layer fields to be sent with the data.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @param remoteXBeeDevice The XBee device of the network that will receive 
	 *                         the data.
	 * @param sourceEndpoint Source endpoint for the transmission.
	 * @param destEndpoint Destination endpoint for the transmission.
	 * @param clusterID Cluster ID used in the transmission.
	 * @param profileID Profile ID used in the transmission.
	 * @param data Byte array containing the data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code sourceEndpoint < 0} or 
	 *                                  if {@code sourceEndpoint > 0xFF} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 0xFF} or 
	 *                                  if {@code clusterID < 0} or 
	 *                                  if {@code clusterID > 0xFFFF} or 
	 *                                  if {@code profileID < 0} or 
	 *                                  if {@code profileID > 0xFFFF}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code remoteXBeeDevice == null} or 
	 *                              if {@code data == null}.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #sendExplicitData(RemoteXBeeDevice, int, int, int, int, byte[])
	 * @see #sendExplicitData(XBee64BitAddress, int, int, int, int, byte[])
	 * @see #sendExplicitData(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[])
	 * @see #sendExplicitDataAsync(XBee64BitAddress, int, int, int, int, byte[])
	 * @see #sendExplicitDataAsync(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[])
	 * @see RemoteXBeeDevice
	 */
	protected void sendExplicitDataAsync(RemoteXBeeDevice remoteXBeeDevice, int sourceEndpoint, int destEndpoint, 
			int clusterID, int profileID, byte[] data) throws XBeeException {
		if (remoteXBeeDevice == null)
			throw new NullPointerException("Remote XBee device cannot be null");
		
		switch (getXBeeProtocol()) {
		case ZIGBEE:
		case DIGI_POINT:
			if (remoteXBeeDevice.get64BitAddress() != null && remoteXBeeDevice.get16BitAddress() != null)
				sendExplicitDataAsync(remoteXBeeDevice.get64BitAddress(), remoteXBeeDevice.get16BitAddress(), sourceEndpoint, destEndpoint, clusterID, profileID, data);
			else
				sendExplicitDataAsync(remoteXBeeDevice.get64BitAddress(), sourceEndpoint, destEndpoint, clusterID, profileID, data);
			break;
		case RAW_802_15_4:
			throw new OperationNotSupportedException("802.15.4. protocol does not support explicit data transmissions.");
		case DIGI_MESH:
		default:
			sendExplicitDataAsync(remoteXBeeDevice.get64BitAddress(), sourceEndpoint, destEndpoint, clusterID, profileID, data);
		}
	}
	
	/**
	 * Sends the provided data in application layer mode to the XBee device of 
	 * the network corresponding to the given 64-bit address. Application layer 
	 * mode means that you need to specify the application layer fields to be 
	 * sent with the data.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * @param address The 64-bit address of the XBee that will receive the data.
	 * @param sourceEndpoint Source endpoint for the transmission.
	 * @param destEndpoint Destination endpoint for the transmission.
	 * @param clusterID Cluster ID used in the transmission.
	 * @param profileID Profile ID used in the transmission.
	 * @param data Byte array containing the data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code sourceEndpoint < 0} or 
	 *                                  if {@code sourceEndpoint > 0xFF} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 0xFF} or 
	 *                                  if {@code clusterID < 0} or 
	 *                                  if {@code clusterID > 0xFFFF} or 
	 *                                  if {@code profileID < 0} or 
	 *                                  if {@code profileID > 0xFFFF}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code address == null} or 
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #sendExplicitData(RemoteXBeeDevice, int, int, int, int, byte[])
	 * @see #sendExplicitData(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[])
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	protected void sendExplicitData(XBee64BitAddress address, int sourceEndpoint, int destEndpoint, int clusterID, 
			int profileID, byte[] data) throws TimeoutException, XBeeException {
		if (address == null)
			throw new NullPointerException("Address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null.");
		if (sourceEndpoint < 0 || sourceEndpoint > 0xFF)
			throw new IllegalArgumentException("Source endpoint must be between 0 and 0xFF.");
		if (destEndpoint < 0 || destEndpoint > 0xFF)
			throw new IllegalArgumentException("Destination endpoint must be between 0 and 0xFF.");
		if (clusterID < 0 || clusterID > 0xFFFF)
			throw new IllegalArgumentException("Cluster ID must be between 0 and 0xFFFF.");
		if (profileID < 0 || profileID > 0xFFFF)
			throw new IllegalArgumentException("Profile ID must be between 0 and 0xFFFF.");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send explicit data to a remote device from a remote device.");
		
		logger.debug(toString() + "Sending explicit data to {} [{} - {} - {} - {}] >> {}.", address, 
				String.format("%02X", sourceEndpoint), String.format("%02X", destEndpoint), 
				String.format("%04X", clusterID), String.format("%04X", profileID), 
				HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new ExplicitAddressingPacket(getNextFrameID(), address, XBee16BitAddress.UNKNOWN_ADDRESS, sourceEndpoint, destEndpoint, clusterID, profileID, 0, XBeeTransmitOptions.NONE, data);
		sendAndCheckXBeePacket(xbeePacket, false);
	}
	
	/**
	 * Sends the provided data in application layer mode to the XBee device of 
	 * the network corresponding to the given 64-bit/16-bit address. 
	 * Application layer mode means that you need to specify the application 
	 * layer fields to be sent with the data.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * @param address64Bit The 64-bit address of the XBee that will receive the 
	 *                     data.
	 * @param address16Bit The 16-bit address of the XBee that will receive the 
	 *                     data. If it is unknown the 
	 *                     {@code XBee16BitAddress.UNKNOWN_ADDRESS} must be 
	 *                     used.
	 * @param sourceEndpoint Source endpoint for the transmission.
	 * @param destEndpoint Destination endpoint for the transmission.
	 * @param clusterID Cluster ID used in the transmission.
	 * @param profileID Profile ID used in the transmission.
	 * @param data Byte array containing the data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code sourceEndpoint < 0} or 
	 *                                  if {@code sourceEndpoint > 0xFF} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 0xFF} or 
	 *                                  if {@code clusterID < 0} or 
	 *                                  if {@code clusterID > 0xFFFF} or 
	 *                                  if {@code profileID < 0} or 
	 *                                  if {@code profileID > 0xFFFF}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code address == null} or 
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #sendExplicitData(RemoteXBeeDevice, int, int, int, int, byte[])
	 * @see #sendExplicitData(XBee64BitAddress, int, int, int, int, byte[])
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	protected void sendExplicitData(XBee64BitAddress address64Bit, XBee16BitAddress address16Bit, int sourceEndpoint, int destEndpoint, 
			int clusterID, int profileID, byte[] data) throws TimeoutException, XBeeException {
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address64Bit == null)
			throw new NullPointerException("64-bit address cannot be null");
		if (address16Bit == null)
			throw new NullPointerException("16-bit address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null.");
		if (sourceEndpoint < 0 || sourceEndpoint > 0xFF)
			throw new IllegalArgumentException("Source endpoint must be between 0 and 0xFF.");
		if (destEndpoint < 0 || destEndpoint > 0xFF)
			throw new IllegalArgumentException("Destination endpoint must be between 0 and 0xFF.");
		if (clusterID < 0 || clusterID > 0xFFFF)
			throw new IllegalArgumentException("Cluster ID must be between 0 and 0xFFFF.");
		if (profileID < 0 || profileID > 0xFFFF)
			throw new IllegalArgumentException("Profile ID must be between 0 and 0xFFFF.");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send explicit data to a remote device from a remote device.");
		
		logger.debug(toString() + "Sending explicit data to {}[{}] [{} - {} - {} - {}] >> {}.", address64Bit, address16Bit, 
				String.format("%02X", sourceEndpoint), String.format("%02X", destEndpoint), 
				String.format("%04X", clusterID), String.format("%04X", profileID), 
				HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new ExplicitAddressingPacket(getNextFrameID(), address64Bit, address16Bit, sourceEndpoint, destEndpoint, clusterID, profileID, 0, XBeeTransmitOptions.NONE, data);
		sendAndCheckXBeePacket(xbeePacket, false);
	}
	
	/**
	 * Sends the provided data to the given XBee device in application layer 
	 * mode choosing the optimal send method depending on the protocol of the 
	 * local XBee device. Application layer mode means that you need to specify 
	 * the application layer fields to be sent with the data.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * @param remoteXBeeDevice The XBee device of the network that will receive 
	 *                         the explicit data.
	 * @param sourceEndpoint Source endpoint for the transmission.
	 * @param destEndpoint Destination endpoint for the transmission.
	 * @param clusterID Cluster ID used in the transmission.
	 * @param profileID Profile ID used in the transmission.
	 * @param data Byte array containing the data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code sourceEndpoint < 0} or 
	 *                                  if {@code sourceEndpoint > 0xFF} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 0xFF} or 
	 *                                  if {@code clusterID < 0} or 
	 *                                  if {@code clusterID > 0xFFFF} or 
	 *                                  if {@code profileID < 0} or 
	 *                                  if {@code profileID > 0xFFFF}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code remoteXBeeDevice == null} or
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #sendExplicitData(XBee64BitAddress, int, int, int, int, byte[])
	 * @see #sendExplicitData(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[])
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 */
	protected void sendExplicitData(RemoteXBeeDevice remoteXBeeDevice, int sourceEndpoint, int destEndpoint, int clusterID, 
			int profileID, byte[] data) throws TimeoutException, XBeeException {
		if (remoteXBeeDevice == null)
			throw new NullPointerException("Remote XBee device cannot be null.");
		
		switch (getXBeeProtocol()) {
		case ZIGBEE:
		case DIGI_POINT:
			if (remoteXBeeDevice.get64BitAddress() != null && remoteXBeeDevice.get16BitAddress() != null)
				sendExplicitData(remoteXBeeDevice.get64BitAddress(), remoteXBeeDevice.get16BitAddress(), sourceEndpoint, destEndpoint, clusterID, profileID, data);
			else
				sendExplicitData(remoteXBeeDevice.get64BitAddress(), sourceEndpoint, destEndpoint, clusterID, profileID, data);
			break;
		case RAW_802_15_4:
			throw new OperationNotSupportedException("802.15.4. protocol does not support explicit data transmissions.");
		case DIGI_MESH:
		default:
			sendExplicitData(remoteXBeeDevice.get64BitAddress(), sourceEndpoint, destEndpoint, clusterID, profileID, data);
		}
	}
	
	/**
	 * Sends the provided data to all the XBee nodes of the network (broadcast) 
	 * in application layer mode. Application layer mode means that you need to 
	 * specify the application layer fields to be sent with the data.
	 * 
	 * <p>This method blocks till a success or error transmit status arrives or 
	 * the configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * @param sourceEndpoint Source endpoint for the transmission.
	 * @param destEndpoint Destination endpoint for the transmission.
	 * @param clusterID Cluster ID used in the transmission.
	 * @param profileID Profile ID used in the transmission.
	 * @param data Byte array containing the data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code sourceEndpoint < 0} or 
	 *                                  if {@code sourceEndpoint > 0xFF} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 0xFF} or 
	 *                                  if {@code clusterID < 0} or 
	 *                                  if {@code clusterID > 0xFFFF} or 
	 *                                  if {@code profileID < 0} or 
	 *                                  if {@code profileID > 0xFFFF}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code remoteXBeeDevice == null} or
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 */
	protected void sendBroadcastExplicitData(int sourceEndpoint, int destEndpoint, int clusterID, int profileID, 
			byte[] data) throws TimeoutException, XBeeException {
		if (getXBeeProtocol() == XBeeProtocol.RAW_802_15_4)
			throw new OperationNotSupportedException("802.15.4. protocol does not support explicit data transmissions.");
		
		sendExplicitData(XBee64BitAddress.BROADCAST_ADDRESS, sourceEndpoint, destEndpoint, clusterID, profileID, data);
	}
	
	/**
	 * Sends the given data to the given XBee local interface.
	 *
	 * @param destInterface Destination XBee local interface.
	 * @param data Data to send.
	 *
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code destInterface == null}.
	 * @throws XBeeException if there is any XBee related exception sending the
	 *                       User Data Relay message.
	 *
	 * @see XBeeLocalInterface
	 *
	 * @since 1.3.0
	 */
	protected void sendUserDataRelay(XBeeLocalInterface destInterface, byte[] data) throws XBeeException {
		if (destInterface == null)
			throw new NullPointerException("Destination interface cannot be null.");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send User Data Relay messages from a remote device.");
		
		logger.debug(toString() + "Sending User Data Relay to {} >> {}.", destInterface.getDescription(),
				data != null ? HexUtils.prettyHexString(data) : "");
		
		XBeePacket xbeePacket = new UserDataRelayPacket(getNextFrameID(), destInterface, data);
		// Send the packet asynchronously since User Data Relay frames do not receive any transmit status.
		sendAndCheckXBeePacket(xbeePacket, true);
	}
	
	/**
	 * Sends the given data to the XBee Bluetooth interface in a User Data Relay
	 * frame.
	 *
	 * @param data Data to send.
	 *
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws XBeeException if there is any XBee related exception sending the
	 *                       data.
	 *
	 * @see #sendMicroPythonData(byte[])
	 * @see #sendUserDataRelay(XBeeLocalInterface, byte[])
	 *
	 * @since 1.3.0
	 */
	protected void sendBluetoothData(byte[] data) throws XBeeException {
		sendUserDataRelay(XBeeLocalInterface.BLUETOOTH, data);
	}
	
	/**
	 * Sends the given data to the XBee MicroPython interface in a User Data
	 * Relay frame.
	 *
	 * @param data Data to send.
	 *
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws XBeeException if there is any XBee related exception sending the
	 *                       data.
	 *
	 * @see #sendBluetoothData(byte[])
	 * @see #sendUserDataRelay(XBeeLocalInterface, byte[])
	 *
	 * @since 1.3.0
	 */
	protected void sendMicroPythonData(byte[] data) throws XBeeException {
		sendUserDataRelay(XBeeLocalInterface.MICROPYTHON, data);
	}
	
	/**
	 * Sends the given data to the XBee serial interface in a User Data Relay
	 * frame.
	 *
	 * @param data Data to send.
	 *
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws XBeeException if there is any XBee related exception sending the
	 *                       data.
	 *
	 * @see #sendBluetoothData(byte[])
	 * @see #sendMicroPythonData(byte[])
	 * @see #sendUserDataRelay(XBeeLocalInterface, byte[])
	 *
	 * @since 1.3.0
	 */
	protected void sendSerialData(byte[] data) throws XBeeException {
		sendUserDataRelay(XBeeLocalInterface.SERIAL, data);
	}
	
	/**
	 * Sends the given XBee packet and registers the given packet listener 
	 * (if not {@code null}) to be notified when the answers is received.
	 * 
	 * <p>This is a non-blocking operation. To wait for the answer use 
	 * {@code sendPacket(XBeePacket)}.</p>
	 * 
	 * @param packet XBee packet to be sent.
	 * @param packetReceiveListener Listener for the operation, {@code null} 
	 *                              not to be notified when the answer arrives.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code packet == null}.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #sendPacket(XBeePacket)
	 * @see #sendPacketAsync(XBeePacket)
	 * @see com.digi.xbee.api.listeners.IPacketReceiveListener
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	protected void sendPacket(XBeePacket packet, IPacketReceiveListener packetReceiveListener) throws XBeeException {
		try {
			sendXBeePacket(packet, packetReceiveListener);
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
	}
	
	/**
	 * Sends the given XBee packet asynchronously.
	 * 
	 * <p>This is a non-blocking operation that do not wait for the answer and 
	 * is never notified when it arrives.</p>
	 * 
	 * <p>To be notified when the answer is received, use 
	 * {@link #sendXBeePacket(XBeePacket, IPacketReceiveListener)}.</p>
	 * 
	 * @param packet XBee packet to be sent asynchronously.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code packet == null}.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #sendXBeePacket(XBeePacket)
	 * @see #sendXBeePacket(XBeePacket, IPacketReceiveListener)
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	protected void sendPacketAsync(XBeePacket packet) throws XBeeException {
		try {
			sendXBeePacket(packet, null);
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
	}
	
	/**
	 * Sends the given XBee packet synchronously and blocks until the response 
	 * is received or the configured receive timeout expires.
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>Use {@code sendXBeePacketAsync(XBeePacket)} or 
	 * {@code #sendXBeePacket(XBeePacket, IPacketReceiveListener)} for 
	 * non-blocking operations.</p>
	 * 
	 * @param packet XBee packet to be sent.
	 * 
	 * @return An {@code XBeePacket} object containing the response of the sent
	 *         packet or {@code null} if there is no response.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code packet == null}.
	 * @throws TimeoutException if there is a timeout sending the XBee packet.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #sendXBeePacket(XBeePacket, IPacketReceiveListener)
	 * @see #sendXBeePacketAsync(XBeePacket)
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	protected XBeePacket sendPacket(XBeePacket packet) throws TimeoutException, XBeeException {
		try {
			return sendXBeePacket(packet);
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
	}
	
	/**
	 * Waits until a Modem Status packet with a reset status, 
	 * {@code ModemStatusEvent.STATUS_HARDWARE_RESET} (0x00), or a watchdog 
	 * timer reset, {@code ModemStatusEvent.STATUS_WATCHDOG_TIMER_RESET} (0x01),
	 * is received or the timeout expires.
	 * 
	 * @return {@code true} if the Modem Status packet is received, 
	 *                      {@code false} otherwise.
	 * 
	 * @see com.digi.xbee.api.models.ModemStatusEvent#STATUS_HARDWARE_RESET
	 * @see com.digi.xbee.api.models.ModemStatusEvent#STATUS_WATCHDOG_TIMER_RESET
	 */
	private boolean waitForModemResetStatusPacket() {
		modemStatusReceived = false;
		addModemStatusListener(resetStatusListener);
		synchronized (resetLock) {
			try {
				resetLock.wait(TIMEOUT_RESET);
			} catch (InterruptedException e) { }
		}
		removeModemStatusListener(resetStatusListener);
		return modemStatusReceived;
	}
	
	/**
	 * Custom listener for modem reset packets.
	 * 
	 * <p>When a Modem Status packet is received with status 
	 * {@code ModemStatusEvent.STATUS_HARDWARE_RESET} or 
	 * {@code ModemStatusEvent.STATUS_WATCHDOG_TIMER_RESET}, it 
	 * notifies the object that was waiting for the reception.</p>
	 * 
	 * @see com.digi.xbee.api.listeners.IModemStatusReceiveListener
	 * @see com.digi.xbee.api.models.ModemStatusEvent#STATUS_HARDWARE_RESET
	 * @see com.digi.xbee.api.models.ModemStatusEvent#STATUS_WATCHDOG_TIMER_RESET
	 */
	private IModemStatusReceiveListener resetStatusListener = new IModemStatusReceiveListener() {
		
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.IModemStatusReceiveListener#modemStatusEventReceived(com.digi.xbee.api.models.ModemStatusEvent)
		 */
		@Override
		public void modemStatusEventReceived(ModemStatusEvent modemStatusEvent) {
			if (modemStatusEvent == ModemStatusEvent.STATUS_HARDWARE_RESET
					|| modemStatusEvent == ModemStatusEvent.STATUS_WATCHDOG_TIMER_RESET){
				modemStatusReceived = true;
				// Continue execution by notifying the lock object.
				synchronized (resetLock) {
					resetLock.notify();
				}
			}
		}
	};
	
	/**
	 * Performs a software reset on this XBee device and blocks until the 
	 * process is completed.
	 * 
	 * @throws TimeoutException if the configured time expires while waiting 
	 *                          for the command reply.
	 * @throws XBeeException if there is any other XBee related exception.
	 */
	protected void softwareReset() throws TimeoutException, XBeeException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		logger.info(toString() + "Resetting the local module...");
		
		ATCommandResponse response = null;
		try {
			response = sendATCommand(new ATCommand("FR"));
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
		
		// Check if AT Command response is valid.
		checkATCommandResponseIsValid(response);
		
		// Wait for a Modem Status packet.
		if (!waitForModemResetStatusPacket())
			throw new TimeoutException("Timeout waiting for the Modem Status packet.");
		
		logger.info(toString() + "Module reset successfully.");
	}
	
	/**
	 * Reads new data received by this XBee device during the configured 
	 * receive timeout.
	 * 
	 * <p>This method blocks until new data is received or the configured 
	 * receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations, register a {@code IDataReceiveListener} 
	 * using the method {@link #addDataListener(IDataReceiveListener)}.</p>
	 * 
	 * @return An {@code XBeeMessage} object containing the data and the source 
	 *         address of the remote node that sent the data. {@code null} if 
	 *         this did not receive new data during the configured receive 
	 *         timeout.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * 
	 * @see #readData(int)
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 * @see #readDataFrom(RemoteXBeeDevice)
	 * @see #readDataFrom(RemoteXBeeDevice, int)
	 * @see com.digi.xbee.api.models.XBeeMessage
	 */
	protected XBeeMessage readData() {
		return readDataPacket(null, TIMEOUT_READ_PACKET);
	}
	
	/**
	 * Reads new data received by this XBee device during the provided timeout.
	 * 
	 * <p>This method blocks until new data is received or the provided timeout 
	 * expires.</p>
	 * 
	 * <p>For non-blocking operations, register a {@code IDataReceiveListener} 
	 * using the method {@link #addDataListener(IDataReceiveListener)}.</p>
	 * 
	 * @param timeout The time to wait for new data in milliseconds.
	 * 
	 * @return An {@code XBeeMessage} object containing the data and the source 
	 *         address of the remote node that sent the data. {@code null} if 
	 *         this device did not receive new data during {@code timeout} 
	 *         milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * 
	 * @see #readData()
	 * @see #readDataFrom(RemoteXBeeDevice)
	 * @see #readDataFrom(RemoteXBeeDevice, int)
	 * @see com.digi.xbee.api.models.XBeeMessage
	 */
	protected XBeeMessage readData(int timeout) {
		if (timeout < 0)
			throw new IllegalArgumentException("Read timeout must be 0 or greater.");
		
		return readDataPacket(null, timeout);
	}
	
	/**
	 * Reads new data received from the given remote XBee device during the 
	 * configured receive timeout.
	 * 
	 * <p>This method blocks until new data from the provided remote XBee 
	 * device is received or the configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations, register a {@code IDataReceiveListener} 
	 * using the method {@link #addDataListener(IDataReceiveListener)}.</p>
	 * 
	 * @param remoteXBeeDevice The remote device to read data from.
	 * 
	 * @return An {@code XBeeMessage} object containing the data and the source
	 *         address of the remote node that sent the data. {@code null} if 
	 *         this device did not receive new data from the provided remote 
	 *         XBee device during the configured receive timeout.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code remoteXBeeDevice == null}.
	 * 
	 * @see #readDataFrom(RemoteXBeeDevice, int)
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 * @see #readData()
	 * @see #readData(int)
	 * @see RemoteXBeeDevice
	 * @see com.digi.xbee.api.models.XBeeMessage
	 */
	protected XBeeMessage readDataFrom(RemoteXBeeDevice remoteXBeeDevice) {
		if (remoteXBeeDevice == null)
			throw new NullPointerException("Remote XBee device cannot be null.");
		
		return readDataPacket(remoteXBeeDevice, TIMEOUT_READ_PACKET);
	}
	
	/**
	 * Reads new data received from the given remote XBee device during the 
	 * provided timeout.
	 * 
	 * <p>This method blocks until new data from the provided remote XBee 
	 * device is received or the given timeout expires.</p>
	 * 
	 * <p>For non-blocking operations, register a {@code IDataReceiveListener} 
	 * using the method {@link #addDataListener(IDataReceiveListener)}.</p>
	 * 
	 * @param remoteXBeeDevice The remote device to read data from.
	 * @param timeout The time to wait for new data in milliseconds.
	 * 
	 * @return An {@code XBeeMessage} object containing the data and the source
	 *         address of the remote node that sent the data. {@code null} if 
	 *         this device did not receive new data from the provided remote 
	 *         XBee device during {@code timeout} milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code remoteXBeeDevice == null}.
	 * 
	 * @see #readDataFrom(RemoteXBeeDevice)
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 * @see #readData()
	 * @see #readData(int)
	 * @see RemoteXBeeDevice
	 * @see com.digi.xbee.api.models.XBeeMessage
	 */
	protected XBeeMessage readDataFrom(RemoteXBeeDevice remoteXBeeDevice, int timeout) {
		if (remoteXBeeDevice == null)
			throw new NullPointerException("Remote XBee device cannot be null.");
		if (timeout < 0)
			throw new IllegalArgumentException("Read timeout must be 0 or greater.");
		
		return readDataPacket(remoteXBeeDevice, timeout);
	}
	
	/**
	 * Reads a new data packet received by this XBee device during the provided 
	 * timeout.
	 * 
	 * <p>This method blocks until new data is received or the given timeout 
	 * expires.</p>
	 * 
	 * <p>If the provided remote XBee device is {@code null} the method returns 
	 * the first data packet read from any remote device.
	 * <br>
	 * If the remote device is not {@code null} the method returns the first 
	 * data package read from the provided device.
	 * </p>
	 * 
	 * @param remoteXBeeDevice The remote device to get a data packet from. 
	 *                         {@code null} to read a data packet sent by any 
	 *                         remote XBee device.
	 * @param timeout The time to wait for a data packet in milliseconds.
	 * 
	 * @return An {@code XBeeMessage} received by this device, containing the 
	 *         data and the source address of the remote node that sent the 
	 *         data. {@code null} if this device did not receive new data 
	 *         during {@code timeout} milliseconds, or if any error occurs while
	 *         trying to get the source of the message.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * 
	 * @see RemoteXBeeDevice
	 * @see com.digi.xbee.api.models.XBeeMessage
	 */
	private XBeeMessage readDataPacket(RemoteXBeeDevice remoteXBeeDevice, int timeout) {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		XBeePacketsQueue xbeePacketsQueue = dataReader.getXBeePacketsQueue();
		XBeePacket xbeePacket = null;
		
		if (remoteXBeeDevice != null)
			xbeePacket = xbeePacketsQueue.getFirstDataPacketFrom(remoteXBeeDevice, timeout);
		else
			xbeePacket = xbeePacketsQueue.getFirstDataPacket(timeout);
		
		if (xbeePacket == null)
			return null;
		
		// Obtain the remote device from the packet.
		RemoteXBeeDevice remoteDevice = null;
		try {
			remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket((XBeeAPIPacket)xbeePacket);
			// If the provided device is not null, add it to the network, so the 
			// device provided is the one that will remain in the network.
			if (remoteXBeeDevice != null)
				remoteDevice = getNetwork().addRemoteDevice(remoteXBeeDevice);
			
			// The packet always contains information of the source so the 
			// remote device should never be null.
			if (remoteDevice == null)
				return null;
			
		} catch (XBeeException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		
		// Obtain the data from the packet.
		byte[] data = null;
		
		switch (((XBeeAPIPacket)xbeePacket).getFrameType()) {
			case RECEIVE_PACKET:
				ReceivePacket receivePacket = (ReceivePacket)xbeePacket;
				data = receivePacket.getRFData();
				break;
			case RX_16:
				RX16Packet rx16Packet = (RX16Packet)xbeePacket;
				data = rx16Packet.getRFData();
				break;
			case RX_64:
				RX64Packet rx64Packet = (RX64Packet)xbeePacket;
				data = rx64Packet.getRFData();
				break;
			default:
				return null;
			}
		
		// Create and return the XBee message.
		return new XBeeMessage(remoteDevice, data, ((XBeeAPIPacket)xbeePacket).isBroadcast());
	}
	
	/**
	 * Reads new explicit data received by this XBee device during the 
	 * configured receive timeout.
	 * 
	 * <p>This method blocks until new explicit data is received or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations, register a 
	 * {@code IExplicitDataReceiveListener} using the method 
	 * {@link #addExplicitDataListener(IExplicitDataReceiveListener)}.</p>
	 * 
	 * @return An {@code ExplicitXBeeMessage} object containing the explicit 
	 *         data, the source address of the remote node that sent the data 
	 *         and other values related to the transmission. {@code null} if 
	 *         this did not receive new explicit data during the configured 
	 *         receive timeout.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #readExplicitData(int)
	 * @see #readExplicitDataFrom(RemoteXBeeDevice)
	 * @see #readExplicitDataFrom(RemoteXBeeDevice, int)
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.ExplicitXBeeMessage
	 */
	protected ExplicitXBeeMessage readExplicitData() {
		return readExplicitDataPacket(null, TIMEOUT_READ_PACKET);
	}
	
	/**
	 * Reads new explicit data received by this XBee device during the provided 
	 * timeout.
	 * 
	 * <p>This method blocks until new explicit data is received or the 
	 * provided timeout expires.</p>
	 * 
	 * <p>For non-blocking operations, register a 
	 * {@code IExplicitDataReceiveListener} using the method 
	 * {@link #addExplicitDataListener(IExplicitDataReceiveListener)}.</p>
	 * 
	 * @param timeout The time to wait for new explicit data in milliseconds.
	 * 
	 * @return An {@code ExplicitXBeeMessage} object containing the explicit 
	 *         data, the source address of the remote node that sent the data 
	 *         and other values related to the transmission. {@code null} if 
	 *         this device did not receive new explicit data during 
	 *         {@code timeout} milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * 
	 * @see #readExplicitData()
	 * @see #readExplicitDataFrom(RemoteXBeeDevice)
	 * @see #readExplicitDataFrom(RemoteXBeeDevice, int)
	 * @see com.digi.xbee.api.models.ExplicitXBeeMessage
	 */
	protected ExplicitXBeeMessage readExplicitData(int timeout) {
		if (timeout < 0)
			throw new IllegalArgumentException("Read timeout must be 0 or greater.");
		
		return readExplicitDataPacket(null, timeout);
	}
	
	/**
	 * Reads new explicit data received from the given remote XBee device 
	 * during the configured receive timeout.
	 * 
	 * <p>This method blocks until new explicit data from the provided remote 
	 * XBee device is received or the configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations, register a 
	 * {@code IExplicitDataReceiveListener} using the method 
	 * {@link #addExplicitDataListener(IExplicitDataReceiveListener)}.</p>
	 * 
	 * @param remoteXBeeDevice The remote device to read explicit data from.
	 * 
	 * @return An {@code ExplicitXBeeMessage} object containing the explicit 
	 *         data, the source address of the remote node that sent the data 
	 *         and other values related to the transmission. {@code null} if 
	 *         this device did not receive new explicit data from the provided 
	 *         remote XBee device during the configured receive timeout.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code remoteXBeeDevice == null}.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #readExplicitData()
	 * @see #readExplicitData(int)
	 * @see #readExplicitDataFrom(RemoteXBeeDevice, int)
	 * @see #setReceiveTimeout(int)
	 * @see RemoteXBeeDevice
	 * @see com.digi.xbee.api.models.ExplicitXBeeMessage
	 */
	protected ExplicitXBeeMessage readExplicitDataFrom(RemoteXBeeDevice remoteXBeeDevice) {
		if (remoteXBeeDevice == null)
			throw new NullPointerException("Remote XBee device cannot be null.");
		
		return readExplicitDataPacket(remoteXBeeDevice, TIMEOUT_READ_PACKET);
	}
	
	/**
	 * Reads new explicit data received from the given remote XBee device 
	 * during the provided timeout.
	 * 
	 * <p>This method blocks until new explicit data from the provided remote 
	 * XBee device is received or the given timeout expires.</p>
	 * 
	 * <p>For non-blocking operations, register a 
	 * {@code IExplicitDataReceiveListener} using the method 
	 * {@link #addExplicitDataListener(IExplicitDataReceiveListener)}.</p>
	 * 
	 * @param remoteXBeeDevice The remote device to read explicit data from.
	 * @param timeout The time to wait for new explicit data in milliseconds.
	 * 
	 * @return An {@code ExplicitXBeeMessage} object containing the explicit 
	 *         data, the source address of the remote node that sent the data 
	 *         and other values related to the transmission. {@code null} if 
	 *         this device did not receive new data from the provided remote 
	 *         XBee device during {@code timeout} milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code remoteXBeeDevice == null}.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #readExplicitData()
	 * @see #readExplicitData(int)
	 * @see #readExplicitDataFrom(RemoteXBeeDevice)
	 * @see #setReceiveTimeout(int)
	 * @see RemoteXBeeDevice
	 * @see com.digi.xbee.api.models.ExplicitXBeeMessage
	 */
	protected ExplicitXBeeMessage readExplicitDataFrom(RemoteXBeeDevice remoteXBeeDevice, int timeout) {
		if (remoteXBeeDevice == null)
			throw new NullPointerException("Remote XBee device cannot be null.");
		if (timeout < 0)
			throw new IllegalArgumentException("Read timeout must be 0 or greater.");
		
		return readExplicitDataPacket(remoteXBeeDevice, timeout);
	}
	
	/**
	 * Reads a new explicit data packet received by this XBee device during 
	 * the provided timeout.
	 * 
	 * <p>This method blocks until new explicit data is received or the given 
	 * timeout expires.</p>
	 * 
	 * <p>If the provided remote XBee device is {@code null} the method returns 
	 * the first explicit data packet read from any remote device.
	 * <br>
	 * If the remote device is not {@code null} the method returns the first 
	 * explicit data package read from the provided device.
	 * </p>
	 * 
	 * @param remoteXBeeDevice The remote device to get an explicit data 
	 *                         packet from. {@code null} to read an explicit 
	 *                         data packet sent by any remote XBee device.
	 * @param timeout The time to wait for an explicit data packet in 
	 *                milliseconds.
	 * 
	 * @return An {@code XBeeMessage} received by this device, containing the 
	 *         explicit data and the source address of the remote node that 
	 *         sent the data. {@code null} if this device did not receive new 
	 *         explicit data during {@code timeout} milliseconds.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * 
	 * @see RemoteXBeeDevice
	 * @see com.digi.xbee.api.models.ExplicitXBeeMessage
	 */
	private ExplicitXBeeMessage readExplicitDataPacket(RemoteXBeeDevice remoteXBeeDevice, int timeout) {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		if (isRemote())
			throw new RuntimeException("Remote devices cannot read explicit data.");
		
		XBeePacketsQueue xbeePacketsQueue = dataReader.getXBeePacketsQueue();
		XBeePacket xbeePacket = null;
		
		if (remoteXBeeDevice != null)
			xbeePacket = xbeePacketsQueue.getFirstExplicitDataPacketFrom(remoteXBeeDevice, timeout);
		else
			xbeePacket = xbeePacketsQueue.getFirstExplicitDataPacket(timeout);
		
		if (xbeePacket == null)
			return null;
		
		// Verify the packet is an explicit data packet.
		APIFrameType packetType = ((XBeeAPIPacket)xbeePacket).getFrameType();
		if (packetType != APIFrameType.EXPLICIT_RX_INDICATOR)
			return null;
		
		// Obtain the necessary data from the packet.
		ExplicitRxIndicatorPacket explicitDataPacket = (ExplicitRxIndicatorPacket)xbeePacket;
		RemoteXBeeDevice remoteDevice = getNetwork().getDevice(explicitDataPacket.get64BitSourceAddress());
		if (remoteDevice == null) {
			if (remoteXBeeDevice != null)
				remoteDevice = remoteXBeeDevice;
			else
				remoteDevice = new RemoteXBeeDevice(this, explicitDataPacket.get64BitSourceAddress());
			getNetwork().addRemoteDevice(remoteDevice);
		}
		int sourceEndpoint = explicitDataPacket.getSourceEndpoint();
		int destEndpoint = explicitDataPacket.getDestinationEndpoint();
		int clusterID = explicitDataPacket.getClusterID();
		int profileID = explicitDataPacket.getProfileID();
		byte[] data = explicitDataPacket.getRFData();
		
		// Create and return the XBee message.
		return new ExplicitXBeeMessage(remoteDevice, sourceEndpoint, destEndpoint, clusterID, profileID, data, ((XBeeAPIPacket)xbeePacket).isBroadcast());
	}
	
	/**
	 * Configures the API output mode of the XBee device.
	 * 
	 * <p>The API output mode determines the format that the received data is 
	 * output through the serial interface of the XBee device.</p>
	 * 
	 * @param apiOutputMode The API output mode to be set to the XBee device.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code apiOutputMode == null}.
	 * @throws TimeoutException if there is a timeout configuring the API 
	 *                          output mode.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getAPIOutputMode()
	 * @see APIOutputMode
	 */
	protected void setAPIOutputMode(APIOutputMode apiOutputMode) throws TimeoutException, XBeeException {
		if (apiOutputMode == null)
			throw new NullPointerException("API output mode cannot be null.");
		
		setParameter("AO", new byte[]{(byte)apiOutputMode.getValue()});
	}
	
	/**
	 * Returns the API output mode of the XBee device.
	 * 
	 * <p>The API output mode determines the format that the received data is 
	 * output through the serial interface of the XBee device.</p>
	 * 
	 * @return The API output mode that the XBee device is configured with.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout getting the API output 
	 *                          mode from the device.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setAPIOutputMode(APIOutputMode)
	 * @see APIOutputMode
	 */
	protected APIOutputMode getAPIOutputMode() throws TimeoutException, XBeeException {
		byte[] apiOutputModeValue = getParameter("AO");
		
		return APIOutputMode.get(apiOutputModeValue[0]);
	}
	
	/**
	 * Sets the password of this Bluetooth device in order to connect to it.
	 *
	 * <p>The Bluetooth password must be provided before calling the
	 * {@link #open()} method.</p>
	 *
	 * @param password The password of this Bluetooth device.
	 *
	 * @since 1.3.0
	 */
	protected void setBluetoothPassword(String password) {
		this.bluetoothPassword = password;
	}
}
