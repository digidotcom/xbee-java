/**
 * Copyright (c) 2014 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.DiscoveryOptions;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ATCommandPacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * Helper class used to perform a node discovery ({@code ND}) in the provided 
 * local XBee device. 
 * 
 * <p>This action requires an XBee connection and optionally a discover timeout.
 * The node discovery works on all protocols and working modes returning as 
 * result a list of discovered XBee Devices.</p> 
 * 
 * <p>The discovery process updates the network of the local device with the new
 * discovered modules and refresh the already existing references.</p>
 */
class NodeDiscovery {

	// Constants.
	/**
	 * Default timeout to finish the discovery process: {@value} ms.
	 */
	public final static long DEFAULT_TIMEOUT = 20000; // 20 seconds.
	
	/**
	 * Use device configured timeout ({@code NT}).
	 */
	public final static long USE_DEVICE_TIMEOUT = -1;
	
	/**
	 * Discovery timeout to wait until the process finishes.
	 */
	public final static long WAIT_FOREVER = -2; // Wait forever.
	
	private static final String ND_COMMAND = "ND";
	private static final String NO_COMMAND = "NO";
	private static final String NT_COMMAND = "NT";
	
	// Variables.
	private static int globalFrameID = 1;
	
	private XBeeDevice xbeeDevice;
	
	private List<RemoteXBeeDevice> deviceList;
	
	private boolean running = false;
	private boolean finished = false;
	
	private int frameID;
	
	private byte[] oldNTValue;
	private byte[] oldNOValue;
	
	protected Logger logger;
	
	/**
	 * Instantiates a Node Discovery object.
	 * 
	 * @param xbeeDevice XBee Device to perform the discovery operation.
	 * 
	 * @throws NullPointerException If {@code xbeeDevice == null}.
	 * 
	 * @see XBeeDevice
	 */
	public NodeDiscovery(XBeeDevice xbeeDevice) {
		if (xbeeDevice == null)
			throw new NullPointerException("Local XBee device cannot be null.");
		
		this.xbeeDevice = xbeeDevice;
		
		frameID = globalFrameID;
		globalFrameID = globalFrameID + 1;
		if (globalFrameID == 0xFF)
			globalFrameID = 1;
		
		logger = LoggerFactory.getLogger(this.getClass());
	}
	
	/**
	 * Discovers and reports all XBee devices found.
	 * 
	 * <p>This method blocks until the provided timeout expires. If 
	 * {@code timeout == USE_DEVICE_TIMEOUT}, the time configured in the device
	 * will be used ({@code NT}).</p>
	 * 
	 * @param options Collection of discovery options to use for the operation.
	 * @param timeout Time to wait for the discovery process to complete in 
	 *                milliseconds.
	 * 
	 * @return A list with the discovered XBee devices.
	 * 
	 * @throws IllegalArgumentException If {@code timeout == WAIT_FOREVER} or
	 *                                  if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws XBeeException If there is an error sending the discovery command.
	 * 
	 * @see #USE_DEVICE_TIMEOUT
	 * @see #discoverDevices()
	 * @see #discoverDevices(long)
	 * @see DiscoveryOptions
	 * @see RemoteXBeeDevice
	 */
	public List<RemoteXBeeDevice> discoverDevices(Set<DiscoveryOptions> options, long timeout) throws XBeeException {
		if (timeout == WAIT_FOREVER)
			throw new IllegalArgumentException("The discovery process cannot block forever.");
		if (timeout < USE_DEVICE_TIMEOUT)
			throw new IllegalArgumentException("The timeout must be bigger than 0.");
		
		logger.debug("{}ND blocking '{}' ms.", toString(), timeout == USE_DEVICE_TIMEOUT ? "configured NT" : timeout);
		
		startDiscoveryProcess(xbeeDevice, null, null, options, timeout);
		
		if (deviceList == null || deviceList.size() == 0)
			return new ArrayList<RemoteXBeeDevice>(0);
		
		XBeeNetwork network = xbeeDevice.getNetwork();
		
		return network.addRemoteDevices(deviceList);
	}
	
	/**
	 * Performs a discovery to search for XBee devices in the same network.
	 * 
	 * <p>The provided listener will be notified every time a new remote device 
	 * is discovered, when an error occurs, or when the operation finishes.</p>
	 * 
	 * <p>The operation finishes:</p>
	 * <ul>
	 * <li>When the provided timeout expires.</li>
	 * <li>If {@code timeout == USE_DEVICE_TIMEOUT}, the time configured in the 
	 * device will be used ({@code NT}).</li>
	 * <li>If {@code timeout == WAIT_FOREVER} the process will never finish 
	 * unless the {@link #stop()} method is called.</li>
	 * </ul>
	 * 
	 * <p>The operation can be stopped at any time using the method 
	 * {@link #stop()}.</p>
	 * 
	 * @param listener Discovery listener to be notified about process events.
	 * @param options Collection of discovery options to use for the operation.
	 * @param timeout Time to wait for the discovery process to complete in 
	 *                milliseconds.
	 * 
	 * @throws IllegalArgumentException If {@code timeout < 0}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws NullPointerException If {@code listener == null}.
	 * 
	 * @see #USE_DEVICE_TIMEOUT
	 * @see #WAIT_FOREVER
	 * @see IDiscoveryListener
	 * @see DiscoveryOptions
	 * @see #stop()
	 */
	public void discoverDevices(final IDiscoveryListener listener, 
			final Set<DiscoveryOptions> options, final long timeout) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		if (timeout < WAIT_FOREVER)
			throw new IllegalArgumentException("The timeout must be bigger than 0.");
		if (!xbeeDevice.isOpen())
			throw new InterfaceNotOpenException();
		
		if (logger.isDebugEnabled()) {
			String timeoutString = ""+timeout;
			if (timeout == NodeDiscovery.USE_DEVICE_TIMEOUT)
				timeoutString = "configured NT";
			else if (timeout == NodeDiscovery.WAIT_FOREVER)
				timeoutString = "forever";
			logger.debug("{}ND ('{}' ms).", toString(), timeoutString);
		}
		
		Thread discoveryThread = new Thread() {
			/*
			 * (non-Javadoc)
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				try {
					startDiscoveryProcess(xbeeDevice, listener, null, options, timeout);
				} catch (XBeeException e) {
					// Notify listener about the error and finish.
					notifyDiscoveryFinished(listener, e.getMessage());
				}
			}
		};
		discoveryThread.start();
	}
	
	/**
	 * Discovers and reports the first remote XBee device that matches the 
	 * supplied identifier.
	 * 
	 * <p>This method blocks until the device is discovered or the provided 
	 * timeout expires.</p>
	 * 
	 * <p>If {@code timeout == USE_DEVICE_TIMEOUT}, the time configured in the 
	 * device will be used ({@code NT}).</p>
	 * 
	 * @param id The identifier of the device to be discovered.
	 * @param timeout The timeout in milliseconds to wait for the device to be 
	 *                discovered.
	 * 
	 * @return The discovered remote XBee device with the given identifier, 
	 *         {@code null} if the timeout expires and the device was not found.
	 * 
	 * @throws IllegalArgumentException If {@code id.length() == 0} or
	 *                                  if {@code timeout == NodeDiscovery.WAIT_FOREVER} or
	 *                                  if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws NullPointerException If {@code id == null}.
	 * @throws XBeeException If there is an error sending the discovery command.
	 * 
	 * @see #USE_DEVICE_TIMEOUT
	 */
	public RemoteXBeeDevice discoverDeviceByID(String id, long timeout) throws XBeeException {
		if (id == null)
			throw new NullPointerException("Device identifier cannot be null.");
		if (id.length() == 0)
			throw new IllegalArgumentException("Device identifier cannot be an empty string.");
		if (timeout == WAIT_FOREVER)
			throw new IllegalArgumentException("The discovery devices process cannot block forever.");
		if (timeout < USE_DEVICE_TIMEOUT)
			throw new IllegalArgumentException("The timeout must be bigger than 0.");
		
		logger.debug("{}ND for {} device bloking '{}' ms.", toString(), id, timeout == USE_DEVICE_TIMEOUT ? "configured NT" : timeout);
		
		startDiscoveryProcess(xbeeDevice, null, id, null, timeout);
		
		XBeeNetwork network = xbeeDevice.getNetwork();
		RemoteXBeeDevice rDevice = null;
		
		if (deviceList != null && deviceList.size() > 0) {
			rDevice = deviceList.get(0);
			if (rDevice != null)
				rDevice = network.addRemoteDevice(rDevice);
		}
		
		return rDevice;
	}
	
	/**
	 * Discovers and reports all RF modules that matches the supplied 
	 * identifier.
	 * 
	 * <p>This method blocks until the provided timeout expires.</p>
	 * 
	 * @param id The identifier of the devices to be discovered.
	 * @param timeout The timeout in milliseconds to wait for the devices to be 
	 *                discovered.
	 * 
	 * @return A list of the discover remote XBee devices with the given 
	 *         identifier.
	 * 
	 * @throws IllegalArgumentException If {@code id.length() == 0} or
	 *                                  if {@code timeout == NodeDiscovery.WAIT_FOREVER} or
	 *                                  if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws NullPointerException If {@code id == null}.
	 * @throws XBeeException If there is an error sending the discovery command.
	 * 
	 * @see #USE_DEVICE_TIMEOUT
	 */
	public List<RemoteXBeeDevice> discoverAllDevicesByID(String id, long timeout) throws XBeeException {
		if (id == null)
			throw new NullPointerException("Device identifier cannot be null.");
		if (id.length() == 0)
			throw new IllegalArgumentException("Device identifier cannot be an empty string.");
		if (timeout == WAIT_FOREVER)
			throw new IllegalArgumentException("The discovery devices process cannot block forever.");
		if (timeout < USE_DEVICE_TIMEOUT)
			throw new IllegalArgumentException("The timeout must be bigger than 0.");
		
		logger.debug("{}ND for all {} devices bloking '{}' ms.", toString(), id, timeout == USE_DEVICE_TIMEOUT ? "configured NT" : timeout);
		
		startDiscoveryProcess(xbeeDevice, null, null, null, timeout);
		
		List<RemoteXBeeDevice> foundDevices = new ArrayList<RemoteXBeeDevice>(0);
		if (deviceList == null)
			return foundDevices;
		
		XBeeNetwork network = xbeeDevice.getNetwork();
		
		for (RemoteXBeeDevice d: deviceList) {
			String nID = d.getNodeID();
			if (nID != null && nID.equals(id)) {
				RemoteXBeeDevice rDevice = network.addRemoteDevice(d);
				if (rDevice != null)
					foundDevices.add(rDevice);
			}
		}
		
		return foundDevices;
	}
	
	/**
	 * Retrieves whether or not the discovery process is running.
	 * 
	 * @return {@code true} if the discovery process is running, {@code false} 
	 *         otherwise.
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Stops the discovery process if it is running.
	 */
	public void stop() {
		running = false;
	}
	
	/**
	 * Retrieves whether or not the discovery process has fully finished.
	 * 
	 * @return {@code true} if the process has fully finished, {@code false} 
	 *         otherwise.
	 */
	public boolean hasFinished() {
		return finished;
	}
	
	/**
	 * Performs a node discover to search for XBee devices in the same network. 
	 * 
	 * <p>This method blocks until the device is discovered or the provided 
	 * timeout expires.</p>
	 * 
	 * @param device XBee Device to perform the discovery operation.
	 * @param listener Discovery listener to be notified about process events. 
	 *                 It may be {@code null}.
	 * @param id The identifier of the device to be discovered, or {@code null}
	 *           to discover all devices in the network.
	 * @param options Collection of discovery options to use for the operation.
	 * @param timeout Time to wait for the discovery process to complete in 
	 *                milliseconds. If {@code timeout == USE_DEVICE_TIMEOUT} 
	 *                the {@code NT} value of the device will be read.
	 * 
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws XBeeException If there is an error sending the discovery command.
	 */
	private void startDiscoveryProcess(XBeeDevice device, IDiscoveryListener listener, 
			String id, Set<DiscoveryOptions> options, long timeout) throws XBeeException {
		// TODO Check if the local device supports ND?
		
		// Check if it is open.
		if (!device.isOpen())
			throw new InterfaceNotOpenException();
		
		finished = false;
		running = true;
		
		try {
			// TODO Continue although the configuration fails?
			configureDiscoveryOptions(device, options, timeout, listener);
			
			// Read the timeout of the device.
			if (timeout == USE_DEVICE_TIMEOUT) {
				timeout = DEFAULT_TIMEOUT;
				byte[] value = getParameter(device, NT_COMMAND, DEFAULT_TIMEOUT, "network timeout", listener);
				if (value != null) {
					timeout = ByteUtils.byteArrayToLong(value) * 100; // The NT timeout is in 100ms
					logger.debug("{}Using NT value: {} ms.", toString(), timeout);
				}
			}
			
			discoverDevicesAPI(device, listener, id, timeout);
			
			// Restore old values.
			logger.debug("{}Restoring discovery options.", toString());
			setDiscoveryOptions(device, oldNOValue, oldNTValue, listener);
			
		} finally {
			finished = true;
			running = false;
			
			notifyDiscoveryFinished(listener, null);
		}
	}
	
	/**
	 * Performs the device discovery in API1 or API2 (API Escaped) mode.
	 * 
	 * @param device XBee Device to perform the discovery operation.
	 * @param listener Discovery listener to be notified about process events. 
	 *                 It may be {@code null}.
	 * @param id The identifier of the device to be discovered, or {@code null}
	 *           to discover all devices in the network.
	 * @param timeout Time to wait for the discovery process to complete in 
	 *                milliseconds.
	 * 
	 * @throws XBeeException If there is an error sending the discovery command.
	 */
	private void discoverDevicesAPI(final XBeeDevice device, final IDiscoveryListener listener, 
			final String id, long timeout) throws XBeeException {
		if (deviceList == null)
			deviceList = new ArrayList<RemoteXBeeDevice>();
		deviceList.clear();
		
		IPacketReceiveListener packetReceiveListener = new IPacketReceiveListener() {
			/*
			 * (non-Javadoc)
			 * @see com.digi.xbee.api.listeners.IPacketReceiveListener#packetReceived(com.digi.xbee.api.packet.XBeePacket)
			 */
			@Override
			public void packetReceived(XBeePacket receivedPacket) {
				if (!running)
					return;
				RemoteXBeeDevice rdevice = null;
				
				byte[] commandValue = getRemoteDeviceData((XBeeAPIPacket)receivedPacket);
				
				rdevice = parseDiscoveryAPIData(commandValue, device);
				
				// If a device with a specific id is being search and it is 
				// already found, return it.
				if (id != null) {
					if (rdevice != null 
							&& id.equals(rdevice.getNodeID())) {
						running = false;
						synchronized (deviceList) {
							deviceList.add(rdevice);
						}
					}
				} else if (rdevice != null)
					notifyDeviceDiscovered(listener, rdevice);
			}
		};
		
		logger.debug("{}Start listening.", toString());
		device.startListeningForPackets(packetReceiveListener);
		
		sendNodeDiscoverCommand(device, id);
		
		// Wait for scan timeout.
		long deadLine = System.currentTimeMillis() + timeout;
		while (running) {
			if (timeout == WAIT_FOREVER || System.currentTimeMillis() < deadLine)
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) { }
			else
				running = false;
		}
		
		device.stopListeningForPackets(packetReceiveListener);
		logger.debug("{}Stop listening.", toString());
	}
	
	/**
	 * Returns a byte array with the remote device data to be parsed.
	 * 
	 * @param packet The API packet which contains the data.
	 * 
	 * @return A byte array with the data to be parsed.
	 */
	private byte[] getRemoteDeviceData(XBeeAPIPacket packet) {
		byte[] data = null;
		
		logger.trace("{}Received packet: {}.", toString(), packet);
		
		APIFrameType frameType = packet.getFrameType();
		switch (frameType) {
		case AT_COMMAND_RESPONSE:
			ATCommandResponsePacket atResponse = (ATCommandResponsePacket)packet;
			// Check the frame ID.
			if (atResponse.getFrameID() != frameID)
				return null;
			// Check the command.
			if (!atResponse.getCommand().equals(ND_COMMAND))
				return null;
			// Check if the command end is received: Empty response with OK status.
			if (atResponse.getCommandValue() == null 
					|| atResponse.getCommandValue().length == 0) {
					running = atResponse.getStatus() != ATCommandStatus.OK;
				return null;
			}
			
			logger.debug("{}Received self reponse: {}.", toString(), packet);
			
			data = atResponse.getCommandValue();
			break;
		case REMOTE_AT_COMMAND_RESPONSE:
			RemoteATCommandResponsePacket remoteAtResponse = (RemoteATCommandResponsePacket)packet;
			// Check the frame ID.
			if (remoteAtResponse.getFrameID() != frameID)
				return null;
			// Check the command.
			if (!remoteAtResponse.getCommand().equals(ND_COMMAND))
				return null;
			// Check if the command end is received: Empty response with OK status.
			if (remoteAtResponse.getCommandValue() == null 
					|| remoteAtResponse.getCommandValue().length == 0) {
				running = remoteAtResponse.getStatus() != ATCommandStatus.OK;
				return null;
			}
			
			logger.debug("{}Received remote reponse: {}.", toString(), packet);
			
			data = remoteAtResponse.getCommandValue();
			break;
		default:
			break;
		}
		
		return data;
	}
	
	/**
	 * Parses the given node discovery API data to create and return a remote 
	 * XBee Device.
	 * 
	 * @param data Byte array with the data to parse.
	 * @param localDevice The local device that received the remote XBee data.
	 * 
	 * @return Discovered XBee device.
	 */
	private RemoteXBeeDevice parseDiscoveryAPIData(byte[] data, XBeeDevice localDevice) {
		RemoteXBeeDevice device = null;
		XBee16BitAddress addr16 = null;
		XBee64BitAddress addr64 = null;
		String id = null;
		// TODO role of the device: coordinator, router, end device or unknown.
		//XBeeDeviceType role = XBeeDeviceType.UNKNOWN;
		int signalStrength = 0;
		byte[] profileID = null;
		byte[] manufacturerID = null;
		
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
		// Read 16 bit address.
		addr16 = new XBee16BitAddress(ByteUtils.readBytes(2, inputStream));
		// Read 64 bit address.
		addr64 = new XBee64BitAddress(ByteUtils.readBytes(8, inputStream));
		
		switch (localDevice.getXBeeProtocol()) {
		case ZIGBEE:
		case DIGI_MESH:
		case ZNET:
		case DIGI_POINT:
		case XLR:
		// TODO [XLR_DM] The next version of the XLR will add DigiMesh support.
		// For the moment only point-to-multipoint is supported in this kind of devices.
		case XLR_DM:
			// Read node identifier.
			id = ByteUtils.readString(inputStream);
			// Read parent address.
			XBee16BitAddress parentAddress = new XBee16BitAddress(ByteUtils.readBytes(2, inputStream));
			// TODO Read device type.
			//role = XBeeDeviceType.get(inputStream.read());
			// Consume status byte, it is not used yet.
			ByteUtils.readBytes(1, inputStream);
			// Read profile ID.
			profileID = ByteUtils.readBytes(2, inputStream);
			// Read manufacturer ID.
			manufacturerID = ByteUtils.readBytes(2, inputStream);
			
			logger.debug("{}Discovered {} device: 16-bit[{}], 64-bit[{}], id[{}], parent[{}], profile[{}], manufacturer[{}].", 
					toString(), localDevice.getXBeeProtocol().getDescription(), addr16, 
					addr64, id, parentAddress, HexUtils.byteArrayToHexString(profileID), 
					HexUtils.byteArrayToHexString(manufacturerID));
			
			break;
		case RAW_802_15_4:
			// Read strength signal byte.
			signalStrength = inputStream.read();
			// Read node identifier.
			id = ByteUtils.readString(inputStream);
			
			logger.debug("{}Discovered {} device: 16-bit[{}], 64-bit[{}], id[{}], rssi[{}].",
					toString(), localDevice.getXBeeProtocol().getDescription(), addr16, addr64, id, signalStrength);
			
			break;
		case UNKNOWN:
		default:
			logger.debug("{}Discovered {} device: 16-bit[{}], 64-bit[{}].",
					toString(), localDevice.getXBeeProtocol().getDescription(), addr16, addr64);
			break;
		}
		
		// Create device and fill with parameters.
		switch (localDevice.getXBeeProtocol()) {
		case ZIGBEE:
			device = new RemoteZigBeeDevice(localDevice, addr64, addr16, id/*, role*/);
			// TODO profileID and manufacturerID
			break;
		case DIGI_MESH:
			device = new RemoteDigiMeshDevice(localDevice, addr64, id/*, role*/);
			// TODO profileID and manufacturerID
			break;
		case DIGI_POINT:
			device = new RemoteDigiPointDevice(localDevice, addr64, id/*, role*/);
			// TODO profileID and manufacturerID
			break;
		case RAW_802_15_4:
			device = new RemoteRaw802Device(localDevice, addr64, addr16, id/*, role*/);
			// TODO signalStrength
			break;
		default:
			device = new RemoteXBeeDevice(localDevice, addr64, addr16, id/*, role*/);
			break;
		}
		
		return device;
	}
	
	/**
	 * Configures the given discovery options and timeout in the device.
	 * 
	 * <p>If provided options are {@code null}, the options are not configured.
	 * </p>
	 * 
	 * <p>If provided timeout is negative, it is not configured.</p>
	 * 
	 * @param device The local device to set the discovery options.
	 * @param options Collection of discovery options to be configured in the 
	 *                {@code NO} parameter.
	 * @param timeout Time to be configured in the {@code NT} parameter of the
	 *                device.
	 * @param listener Discovery listeners to be notified about errors in the 
	 *                 configuration. It may be {@code null}.
	 * 
	 * @return {@code true} if the configuration operation finishes successfully,
	 *         {@code false} otherwise.
	 */
	private boolean configureDiscoveryOptions(XBeeDevice device, Set<DiscoveryOptions> options, 
			long timeout, IDiscoveryListener listener) {
		oldNOValue = null;
		oldNTValue = null;
		
		logger.debug("{}Configuring discovery options.", toString());
		
		// If options are null, do not configure the radio module.
		if (options == null && timeout < 0)
			return true;
		
		byte[] optionsValue = null;
		byte[] timeoutValue = null;
		
		if (options != null) {
			oldNOValue = getParameter(device, NO_COMMAND, null, "network options", listener);
			
			if (oldNOValue != null)
				logger.debug("{}Previous NO: {}.", toString(), HexUtils.byteArrayToHexString(oldNOValue));
			
			int value = DiscoveryOptions.calculateDiscoveryValue(device.getXBeeProtocol(), options);
			optionsValue = ByteUtils.intToByteArray(value);
		}
		
		if (timeout > USE_DEVICE_TIMEOUT) {
			oldNTValue = getParameter(device, NT_COMMAND, null, "network timeout", listener);
			
			if (oldNTValue != null)
				logger.debug("{}Previous NT: {} ms.", toString(), ByteUtils.byteArrayToInt(oldNTValue)*100);
			
			timeoutValue = ByteUtils.intToByteArray((int)timeout / 100);
		}
		
		return setDiscoveryOptions(device, optionsValue, timeoutValue, listener);
	}
	
	/**
	 * Configures the given {@code NO} and {@code NT} values the device.
	 * 
	 * <p>If any of the provided values is {@code null}, that value will not 
	 * be configured in the device.</p>
	 * 
	 * @param device The local device to set the provided values.
	 * @param noValue An hexadecimal string representing the value for the 
	 *                {@code N0} parameter.
	 * @param ntValue An hexadecimal string representing the value for the 
	 *                {@code NT} parameter.
	 * @param listener Discovery listener to be notified about errors in the 
	 *                 configuration. It may be {@code null}.
	 * 
	 * @return {@code true} if the configuration operation finishes successfully,
	 *         {@code false} otherwise.
	 */
	private boolean setDiscoveryOptions(XBeeDevice device, byte[] noValue, 
			byte[] ntValue, IDiscoveryListener listener) {
		if (noValue == null && ntValue == null)
			return true;
		
		boolean configured = true;
		
		if (noValue != null) {
			boolean success = setParameter(device, NO_COMMAND, noValue, "network options", listener);
			configured = configured & success;
			if (success)
				logger.debug("{}Configured NO to {}.", toString(), HexUtils.byteArrayToHexString(noValue));
			else
				logger.error("{}Could not configure NO to {}.", toString(), HexUtils.byteArrayToHexString(noValue));
		}
		
		if (ntValue != null) {
			boolean success = setParameter(device, NT_COMMAND, ntValue, "network timeout", listener);
			configured = configured & success;
			if (success)
				logger.debug("{}Configured NT to {} ms.", toString(), ByteUtils.byteArrayToInt(ntValue)*100);
			else
				logger.error("{}Could not configure NT to {} ms.", toString(), HexUtils.byteArrayToHexString(ntValue));
		}
		
		return configured;
	}
	
	/**
	 * Sends the node discover ({@code ND}) command.
	 * 
	 * @param device The local device to send the ({@code ND}) command.
	 * @param id The identifier of the device to be discovered, or {@code null}
	 *           to discover all devices in the network.
	 * 
	 * @throws XBeeException If there is an error writing in the communication interface.
	 */
	private void sendNodeDiscoverCommand(XBeeDevice device, String id) throws XBeeException {
		if (id == null)
			device.sendPacketAsync(new ATCommandPacket(frameID, ND_COMMAND, ""));
		else
			device.sendPacketAsync(new ATCommandPacket(frameID, ND_COMMAND, id));
	}
	
	/**
	 * Retrieves the value of the provided parameter in the given device.
	 * 
	 * <p>This method notifies the provided discovery listener if any error 
	 * occurs when getting the value.</p>
	 * 
	 * @param device The device to get the value from.
	 * @param parameter The parameter to get.
	 * @param defaultValue The default value to be used if any error occurs.
	 * @param desc The description of the parameter to be read.
	 * @param listener Discovery listener to be notified if any error occurs. 
	 *                 It may be {@code null}.
	 * 
	 * @return A byte array with the value of the parameter read, {@code null}
	 *         if any error occurs.
	 */
	private byte[] getParameter(XBeeDevice device, String parameter, Object defaultValue, 
			String desc, IDiscoveryListener listener) {
		byte[] value = null;
		try {
			value = device.getParameter(parameter);
		} catch (XBeeException e) {
			String error = "Could not read %s (%s)";
			if (defaultValue != null)
				error = error + ", using default value %d instead: %s";
			else
				error = error + ": %s";
			notifyDiscoveryError(listener, 
					String.format(error, desc, parameter, defaultValue, e.getMessage()));
		}
		return value;
	}
	
	/**
	 * Configures the value of the provided parameter in the given device.
	 * 
	 * <p>This method notifies the provided discovery listener if any error 
	 * occurs when setting the value.</p>
	 * 
	 * @param device The device to set the value to.
	 * @param parameter The parameter to be set.
	 * @param value A byte array with the new value of the parameter.
	 * @param desc The description of the parameter to be set.
	 * @param listener Discovery listener to be notified if any error occurs. 
	 *                 It may be {@code null}.
	 * 
	 * @return {@code true} if the operation finishes successfully, 
	 *         {@code false} otherwise.
	 */
	private boolean setParameter(XBeeDevice device, String parameter, byte[] value, 
			String desc, IDiscoveryListener listener) {
		try {
			device.setParameter(parameter, value);
			return true;
		} catch (XBeeException e) {
			notifyDiscoveryError(listener, 
					String.format("Could not set %s (%s) to %s: %s.", desc, parameter, 
							HexUtils.byteArrayToHexString(value), e.getMessage()));
			return false;
		}
	}
	
	/**
	 * Notifies the given discovery listener that a device was discovered.
	 * 
	 * @param listener The discovery listener to be notified.
	 * @param device The remote device discovered.
	 */
	private void notifyDeviceDiscovered(IDiscoveryListener listener, RemoteXBeeDevice device) {
		if (listener == null) {
			synchronized (deviceList) {
				deviceList.add(device);
			}
			return;
		}
		
		XBeeNetwork network = xbeeDevice.getNetwork();
		
		RemoteXBeeDevice addedDev = network.addRemoteDevice(device);
		if (addedDev != null)
			listener.deviceDiscovered(addedDev);
		else {
			String error = "Error adding device '" + device + "' to the network.";
			logger.error("{}{}", toString(), device);
			listener.discoveryError(error);
		}
	}
	
	/**
	 * Notifies the given discovery listener about the provided error.
	 * 
	 * @param listener The discovery listener to be notified.
	 * @param error The error to notify.
	 */
	private void notifyDiscoveryError(IDiscoveryListener listener, String error) {
		logger.error("{}Error discovering devices: {}", toString(), error);
		
		if (listener == null)
			return;
		
		listener.discoveryError(error);
	}
	
	/**
	 * Notifies the given discovery listener that a the discovery process has 
	 * finished.
	 * 
	 * @param listener The discovery listener to be notified.
	 * @param error The error message, or {@code null} if the process finished 
	 *              successfully.
	 */
	private void notifyDiscoveryFinished(IDiscoveryListener listener, String error) {
		if (error != null && error.length() > 0)
			logger.error("{}Finished discovery: {}", toString(), error);
		else
			logger.debug("{}Finished discovery.", toString());
		
		if (listener == null)
			return;
		
		listener.discoveryFinished(error);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return xbeeDevice.toString();
	}
}