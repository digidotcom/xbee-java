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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ATCommandPacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
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
	private static final String ND_COMMAND = "ND";
	
	// Variables.
	private static int globalFrameID = 1;
	
	private XBeeDevice xbeeDevice;
	
	private List<RemoteXBeeDevice> deviceList;
	
	private boolean discovering = false;
	private boolean running = false;
	
	private int frameID;
	
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
	 * <p>This method blocks until the configured timeout expires.</p>
	 * 
	 * @return A list with the discovered XBee devices.
	 * 
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws XBeeException If there is an error sending the discovery command.
	 * 
	 * @see RemoteXBeeDevice
	 */
	public List<RemoteXBeeDevice> discoverDevices() throws XBeeException {
		logger.debug("{}ND for all devices.", toString());
		
		performNodeDiscovery(xbeeDevice, null, null);
		
		if (deviceList == null || deviceList.size() == 0)
			return new ArrayList<RemoteXBeeDevice>(0);
		
		XBeeNetwork network = xbeeDevice.getNetwork();
		
		return network.addRemoteDevices(deviceList);
	}
	
	/**
	 * Discovers and reports the first remote XBee device that matches the 
	 * supplied identifier.
	 * 
	 * <p>This method blocks until the device is discovered or the configured 
	 * timeout expires.</p>
	 * 
	 * @param id The identifier of the device to be discovered.
	 * 
	 * @return The discovered remote XBee device with the given identifier, 
	 *         {@code null} if the timeout expires and the device was not found.
	 * 
	 * @throws NullPointerException If {@code id == null}.
	 * @throws IllegalArgumentException If {@code id.length() == 0}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws XBeeException If there is an error sending the discovery command.
	 */
	public RemoteXBeeDevice discoverDeviceByNodeID(String id) throws XBeeException {
		if (id == null)
			throw new NullPointerException("Device identifier cannot be null.");
		if (id.length() == 0)
			throw new IllegalArgumentException("Device identifier cannot be an empty string.");
		
		logger.debug("{}ND for {} device.", toString(), id);
		
		performNodeDiscovery(xbeeDevice, null, id);
		
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
	 * Discovers and reports all remote XBee devices that match the supplied 
	 * identifier.
	 * 
	 * <p>This method blocks until the configured timeout expires.</p>
	 * 
	 * @param id The identifier of the devices to be discovered.
	 * 
	 * @return A list of the discover remote XBee devices with the given 
	 *         identifier.
	 * 
	 * @throws NullPointerException If {@code id == null}.
	 * @throws IllegalArgumentException If {@code id.length() == 0}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws XBeeException If there is an error sending the discovery command.
	 */
	public List<RemoteXBeeDevice> discoverDevicesByNodeID(String id) throws XBeeException {
		if (id == null)
			throw new NullPointerException("Device identifier cannot be null.");
		if (id.length() == 0)
			throw new IllegalArgumentException("Device identifier cannot be an empty string.");
		
		logger.debug("{}ND for all {} devices.", toString(), id);
		
		performNodeDiscovery(xbeeDevice, null, null);
		
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
	 * Performs a node discover to search for XBee devices in the same network. 
	 * 
	 * @param listeners Discovery listeners to be notified about process events.
	 * 
	 * @throws NullPointerException If {@code listeners == null}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 */
	public void startDiscoveryProcess(final ArrayList<IDiscoveryListener> listeners) {
		if (listeners == null)
			throw new NullPointerException("Listeners list cannot be null.");
		if (!xbeeDevice.isOpen())
			throw new InterfaceNotOpenException();
		
		running = true;
		
		Thread discoveryThread = new Thread() {
			@Override
			public void run() {
				try {
					performNodeDiscovery(xbeeDevice, listeners, null);
				} catch (XBeeException e) {
					// Notify the listeners about the error and finish.
					notifyDiscoveryFinished(listeners, e.getMessage());
				}
			}
		};
		discoveryThread.start();
	}
	
	/**
	 * Stops the discovery process if it is running.
	 * 
	 * @see #isRunning()
	 */
	public void stopDiscoveryProcess() {
		discovering = false;
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
	 * Performs a node discover to search for XBee devices in the same network. 
	 * 
	 * <p>This method blocks until the configured timeout expires.</p>
	 * 
	 * @param device XBee Device to perform the discovery operation.
	 * @param listeners Discovery listeners to be notified about process events.
	 * @param id The identifier of the device to be discovered, or {@code null}
	 *           to discover all devices in the network.
	 * 
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws XBeeException If there is an error sending the discovery command.
	 */
	private void performNodeDiscovery(XBeeDevice device, ArrayList<IDiscoveryListener> listeners, String id) 
			throws XBeeException {
		// Check if it is open.
		if (!device.isOpen())
			throw new InterfaceNotOpenException();
		
		running = true;
		discovering = true;
		
		try {
			long timeout = ByteUtils.byteArrayToLong(device.getParameter("NT")) * 100;
			
			discoverDevicesAPI(device, listeners, id, timeout);
			
			// In DigiMesh/DigiPoint and 802.15.4 the network discovery timeout 
			// is NT + the network propagation time. It means that if the user 
			// sends an AT command just after NT ms, s/he will receive a timeout
			// exception. Sleep 3 seconds in DigiMesh/DigiPoint and 1 second in 
			// 802.15.4 to avoid this issue.
			if (device.getXBeeProtocol() == XBeeProtocol.DIGI_MESH || 
					device.getXBeeProtocol() == XBeeProtocol.DIGI_POINT) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {}
			} else if (device.getXBeeProtocol() == XBeeProtocol.RAW_802_15_4) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
			}
			
			// Notify that the discovery finished without errors.
			notifyDiscoveryFinished(listeners, null);
			
		} finally {
			running = false;
			discovering = false;
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
	private void discoverDevicesAPI(final XBeeDevice device, final ArrayList<IDiscoveryListener> listeners, 
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
				if (!discovering)
					return;
				RemoteXBeeDevice rdevice = null;
				
				byte[] commandValue = getRemoteDeviceData((XBeeAPIPacket)receivedPacket);
				
				rdevice = parseDiscoveryAPIData(commandValue, device);
				
				// If a device with a specific id is being search and it is 
				// already found, return it.
				if (id != null) {
					if (rdevice != null 
							&& id.equals(rdevice.getNodeID())) {
						discovering = false;
						synchronized (deviceList) {
							deviceList.add(rdevice);
						}
					}
				} else if (rdevice != null)
					notifyDeviceDiscovered(listeners, rdevice);
			}
		};
		
		logger.debug("{}Start listening.", toString());
		device.addPacketListener(packetReceiveListener);
		
		try {
			sendNodeDiscoverCommand(device, id);
			
			// Wait for scan timeout.
			long deadLine = System.currentTimeMillis() + timeout;
			while (discovering) {
				if (System.currentTimeMillis() < deadLine)
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) { }
				else
					discovering = false;
			}
		} finally {
			device.removePacketListener(packetReceiveListener);
			logger.debug("{}Stop listening.", toString());
		}
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
					discovering = atResponse.getStatus() != ATCommandStatus.OK;
				return null;
			}
			
			logger.debug("{}Received self reponse: {}.", toString(), packet);
			
			data = atResponse.getCommandValue();
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
	 * Notifies the given discovery listener that a device was discovered.
	 * 
	 * @param listener The discovery listener to be notified.
	 * @param device The remote device discovered.
	 */
	private void notifyDeviceDiscovered(ArrayList<IDiscoveryListener> listeners, RemoteXBeeDevice device) {
		if (listeners == null) {
			synchronized (deviceList) {
				deviceList.add(device);
			}
			return;
		}
		
		XBeeNetwork network = xbeeDevice.getNetwork();
		
		RemoteXBeeDevice addedDev = network.addRemoteDevice(device);
		if (addedDev != null) {
			for (IDiscoveryListener listener : listeners)
				listener.deviceDiscovered(addedDev);
		} else {
			String error = "Error adding device '" + device + "' to the network.";
			notifyDiscoveryError(listeners, error);
		}
	}
	
	/**
	 * Notifies the given discovery listener about the provided error.
	 * 
	 * @param listener The discovery listener to be notified.
	 * @param error The error to notify.
	 */
	private void notifyDiscoveryError(ArrayList<IDiscoveryListener> listeners, String error) {
		logger.error("{}Error discovering devices: {}", toString(), error);
		
		if (listeners == null)
			return;
		
		for (IDiscoveryListener listener : listeners)
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
	private void notifyDiscoveryFinished(ArrayList<IDiscoveryListener> listeners, String error) {
		if (error != null && error.length() > 0)
			logger.error("{}Finished discovery: {}", toString(), error);
		else
			logger.debug("{}Finished discovery.", toString());
		
		if (listeners == null)
			return;
		
		for (IDiscoveryListener listener : listeners)
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