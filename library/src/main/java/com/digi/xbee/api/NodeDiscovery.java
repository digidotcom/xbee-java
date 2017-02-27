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
 * discovered modules and refreshes the already existing references.</p>
 */
class NodeDiscovery {
	
	// Constants.
	private static final String ND_COMMAND = "ND";
	
	public static final long DEFAULT_TIMEOUT = 20000; // 20 seconds.
	
	// Variables.
	private static int globalFrameID = 1;
	
	private XBeeDevice xbeeDevice;
	
	private List<RemoteXBeeDevice> deviceList;
	
	private boolean discovering = false;
	private boolean running = false;
	
	private int frameID;
	
	protected Logger logger;
	
	/**
	 * Instantiates a new {@code NodeDiscovery} object.
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
	 * Discovers and reports the first remote XBee device that matches the 
	 * supplied identifier.
	 * 
	 * <p>This method blocks until the device is discovered or the configured 
	 * timeout in the device (NT) expires.</p>
	 * 
	 * @param id The identifier of the device to be discovered.
	 * 
	 * @return The discovered remote XBee device with the given identifier, 
	 *         {@code null} if the timeout expires and the device was not found.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws XBeeException if there is an error sending the discovery command.
	 * 
	 * @see #discoverDevices(List)
	 */
	public RemoteXBeeDevice discoverDevice(String id) throws XBeeException {
		// Check if the connection is open.
		if (!xbeeDevice.isOpen())
			throw new InterfaceNotOpenException();
		
		logger.debug("{}ND for {} device.", xbeeDevice.toString(), id);
		
		running = true;
		discovering = true;
		
		performNodeDiscovery(null, id);
		
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
	 * identifiers.
	 * 
	 * <p>This method blocks until the configured timeout in the device (NT) 
	 * expires.</p>
	 * 
	 * @param ids List which contains the identifiers of the devices to be 
	 *            discovered.
	 * 
	 * @return A list of the discovered remote XBee devices with the given 
	 *         identifiers.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws XBeeException if there is an error discovering the devices.
	 * 
	 * @see #discoverDevice(String)
	 */
	public List<RemoteXBeeDevice> discoverDevices(List<String> ids) throws XBeeException {
		// Check if the connection is open.
		if (!xbeeDevice.isOpen())
			throw new InterfaceNotOpenException();
		
		logger.debug("{}ND for all {} devices.", xbeeDevice.toString(), ids.toString());
		
		running = true;
		discovering = true;
		
		performNodeDiscovery(null, null);
		
		List<RemoteXBeeDevice> foundDevices = new ArrayList<RemoteXBeeDevice>(0);
		if (deviceList == null)
			return foundDevices;
		
		XBeeNetwork network = xbeeDevice.getNetwork();
		
		for (RemoteXBeeDevice d: deviceList) {
			String nID = d.getNodeID();
			if (nID == null)
				continue;
			for (String id : ids) {
				if (nID.equals(id)) {
					RemoteXBeeDevice rDevice = network.addRemoteDevice(d);
					if (rDevice != null && !foundDevices.contains(rDevice))
						foundDevices.add(rDevice);
				}
			}
		}
		
		return foundDevices;
	}
	
	/**
	 * Performs a node discover to search for XBee devices in the same network. 
	 * 
	 * @param listeners Discovery listeners to be notified about process events.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code listeners == null}.
	 * 
	 * @see #isRunning()
	 * @see #stopDiscoveryProcess()
	 */
	public void startDiscoveryProcess(final List<IDiscoveryListener> listeners) {
		// Check if the connection is open.
		if (!xbeeDevice.isOpen())
			throw new InterfaceNotOpenException();
		if (listeners == null)
			throw new NullPointerException("Listeners list cannot be null.");
		
		running = true;
		discovering = true;
		
		Thread discoveryThread = new Thread() {
			@Override
			public void run() {
				try {
					performNodeDiscovery(listeners, null);
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
	 * @see #startDiscoveryProcess(List)
	 */
	public void stopDiscoveryProcess() {
		discovering = false;
	}
	
	/**
	 * Retrieves whether the discovery process is running.
	 * 
	 * @return {@code true} if the discovery process is running, {@code false} 
	 *         otherwise.
	 * 
	 * @see #startDiscoveryProcess(List)
	 * @see #stopDiscoveryProcess()
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Performs a node discover to search for XBee devices in the same network. 
	 * 
	 * <p>This method blocks until the configured timeout expires.</p>
	 * 
	 * @param listeners Discovery listeners to be notified about process events.
	 * @param id The identifier of the device to be discovered, or {@code null}
	 *           to discover all devices in the network.
	 * 
	 * @throws XBeeException if there is an error sending the discovery command.
	 */
	private void performNodeDiscovery(List<IDiscoveryListener> listeners, String id) throws XBeeException {
		try {
			discoverDevicesAPI(listeners, id);
			
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
	 * @param listeners Discovery listeners to be notified about process events.
	 * @param id The identifier of the device to be discovered, or {@code null}
	 *           to discover all devices in the network.
	 * 
	 * @throws XBeeException if there is an error sending the discovery command.
	 */
	private void discoverDevicesAPI(final List<IDiscoveryListener> listeners, final String id) throws XBeeException {
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
				
				rdevice = parseDiscoveryAPIData(commandValue, xbeeDevice);
				
				// If a device with a specific id is being search and it is 
				// already found, return it.
				if (id != null) {
					if (rdevice != null && id.equals(rdevice.getNodeID())) {
						synchronized (deviceList) {
							deviceList.add(rdevice);
						}
						// If the local device is 802.15.4 wait until the 'end' command is received.
						if (xbeeDevice.getXBeeProtocol() != XBeeProtocol.RAW_802_15_4)
							discovering = false;
					}
				} else if (rdevice != null)
					notifyDeviceDiscovered(listeners, rdevice);
			}
		};
		
		logger.debug("{}Start listening.", xbeeDevice.toString());
		xbeeDevice.addPacketListener(packetReceiveListener);
		
		try {
			long deadLine = System.currentTimeMillis();
			
			// In 802.15.4 devices, the discovery finishes when the 'end' command 
			// is received, so it's not necessary to calculate the timeout.
			// This also applies to S1B devices working in compatibility mode.
			boolean is802Compatible = is802Compatible(); 
			if (!is802Compatible)
				deadLine += calculateTimeout(listeners);
			
			sendNodeDiscoverCommand(id);
			
			if (!is802Compatible) {
				// Wait for scan timeout.
				while (discovering) {
					if (System.currentTimeMillis() < deadLine)
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) { }
					else
						discovering = false;
				}
			} else {
				// Wait until the 'end' command is received.
				while (discovering) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) { }
				}
			}
		} finally {
			xbeeDevice.removePacketListener(packetReceiveListener);
			logger.debug("{}Stop listening.", xbeeDevice.toString());
		}
	}
	
	/**
	 * Calculates the maximum response time, in milliseconds, for network
	 * discovery responses.
	 * 
	 * @param listeners Discovery listeners to be notified about process events.
	 * 
	 * @return Maximum network discovery timeout.
	 */
	private long calculateTimeout(List<IDiscoveryListener> listeners) {
		long timeout = -1;
		
		// Read the maximum discovery timeout (N?).
		try {
			timeout = ByteUtils.byteArrayToLong(xbeeDevice.getParameter("N?"));
		} catch (XBeeException e) {
			logger.debug("{}Could not read the N? value.", xbeeDevice.toString());
		}
		
		// If N? does not exist, read the NT parameter.
		if (timeout == -1) {
			// Read the device timeout (NT).
			try {
				timeout = ByteUtils.byteArrayToLong(xbeeDevice.getParameter("NT")) * 100;
			} catch (XBeeException e) {
				timeout = DEFAULT_TIMEOUT;
				String error = "Could not read the discovery timeout from the device (NT). "
						+ "The default timeout (" + DEFAULT_TIMEOUT + " ms.) will be used.";
				notifyDiscoveryError(listeners, error);
			}
			
			// In DigiMesh/DigiPoint the network discovery timeout is NT + the 
			// network propagation time. It means that if the user sends an AT 
			// command just after NT ms, s/he will receive a timeout exception. 
			if (xbeeDevice.getXBeeProtocol() == XBeeProtocol.DIGI_MESH) {
				timeout += 3000;
			} else if (xbeeDevice.getXBeeProtocol() == XBeeProtocol.DIGI_POINT) {
				timeout += 8000;
			}
		}
		
		if (xbeeDevice.getXBeeProtocol() == XBeeProtocol.DIGI_MESH) {
			try {
				// If the module is 'Sleep support', wait another discovery cycle.
				boolean isSleepSupport = ByteUtils.byteArrayToInt(xbeeDevice.getParameter("SM")) == 7;
				if (isSleepSupport)
					timeout += timeout + (timeout * 0.1);
			} catch (XBeeException e) {
				logger.debug("{}Could not determine if the module is 'Sleep Support'.", xbeeDevice.toString());
			}
		}
		
		return timeout;
	}
	
	/**
	 * Returns a byte array with the remote device data to be parsed.
	 * 
	 * @param packet The API packet that contains the data.
	 * 
	 * @return A byte array with the data to be parsed.
	 */
	private byte[] getRemoteDeviceData(XBeeAPIPacket packet) {
		byte[] data = null;
		
		logger.trace("{}Received packet: {}.", xbeeDevice.toString(), packet);
		
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
			// Check if the 'end' command is received (empty response with OK status).
			if (atResponse.getCommandValue() == null || atResponse.getCommandValue().length == 0) {
				discovering = atResponse.getStatus() != ATCommandStatus.OK;
				return null;
			}
			
			logger.debug("{}Received self response: {}.", xbeeDevice.toString(), packet);
			
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
		if (data == null)
			return null;
		
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
		case SX:
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
					xbeeDevice.toString(), localDevice.getXBeeProtocol().getDescription(), addr16, 
					addr64, id, parentAddress, HexUtils.byteArrayToHexString(profileID), 
					HexUtils.byteArrayToHexString(manufacturerID));
			
			break;
		case RAW_802_15_4:
			// Read strength signal byte.
			signalStrength = inputStream.read();
			// Read node identifier.
			id = ByteUtils.readString(inputStream);
			
			logger.debug("{}Discovered {} device: 16-bit[{}], 64-bit[{}], id[{}], rssi[{}].",
					xbeeDevice.toString(), localDevice.getXBeeProtocol().getDescription(), addr16, addr64, id, signalStrength);
			
			break;
		case UNKNOWN:
		default:
			logger.debug("{}Discovered {} device: 16-bit[{}], 64-bit[{}].",
					xbeeDevice.toString(), localDevice.getXBeeProtocol().getDescription(), addr16, addr64);
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
	 * @param id The identifier of the device to be discovered, or {@code null}
	 *           to discover all devices in the network.
	 * 
	 * @throws XBeeException if there is an error writing in the communication interface.
	 */
	private void sendNodeDiscoverCommand(String id) throws XBeeException {
		if (id == null)
			xbeeDevice.sendPacketAsync(new ATCommandPacket(frameID, ND_COMMAND, ""));
		else
			xbeeDevice.sendPacketAsync(new ATCommandPacket(frameID, ND_COMMAND, id));
	}
	
	/**
	 * Notifies the given discovery listeners that a device was discovered.
	 * 
	 * @param listeners The discovery listeners to be notified.
	 * @param device The remote device discovered.
	 */
	private void notifyDeviceDiscovered(List<IDiscoveryListener> listeners, RemoteXBeeDevice device) {
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
	 * Notifies the given discovery listeners about the provided error.
	 * 
	 * @param listeners The discovery listeners to be notified.
	 * @param error The error to notify.
	 */
	private void notifyDiscoveryError(List<IDiscoveryListener> listeners, String error) {
		logger.error("{}Error discovering devices: {}", xbeeDevice.toString(), error);
		
		if (listeners == null)
			return;
		
		for (IDiscoveryListener listener : listeners)
			listener.discoveryError(error);
	}
	
	/**
	 * Notifies the given discovery listeners that the discovery process has 
	 * finished.
	 * 
	 * @param listeners The discovery listeners to be notified.
	 * @param error The error message, or {@code null} if the process finished 
	 *              successfully.
	 */
	private void notifyDiscoveryFinished(List<IDiscoveryListener> listeners, String error) {
		if (error != null && error.length() > 0)
			logger.error("{}Finished discovery: {}", xbeeDevice.toString(), error);
		else
			logger.debug("{}Finished discovery.", xbeeDevice.toString());
		
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
		return getClass().getName() + " [" + xbeeDevice.toString() + "] @" + 
				Integer.toHexString(hashCode());
	}
	
	/**
	 * Checks whether the device performing the node discovery is a legacy 
	 * 802.15.4 device or a S1B device working compatibility mode.
	 * 
	 * @return {@code true} if the device performing the node discovery is a
	 *         legacy 802.15.4 device or S1B in compatibility mode, {@code false}
	 *         otherwise.
	 */
	private boolean is802Compatible() {
		if (xbeeDevice.getXBeeProtocol() != XBeeProtocol.RAW_802_15_4)
			return false;
		byte[] param = null;
		try {
			param = xbeeDevice.getParameter("C8");
		} catch (Exception e) { }
		if (param == null || ((param[0] & 0x2) == 2 ))
			return true;
		return false;
	}
}