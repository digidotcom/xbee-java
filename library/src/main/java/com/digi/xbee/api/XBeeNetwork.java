/**
 * Copyright (c) 2014-2015 Digi International Inc.,
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.models.DiscoveryOptions;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.utils.ByteUtils;

/**
 * This class represents an XBee Network.
 *  
 * <p>The network allows the discovery of remote devices in the same network 
 * as the local one and stores them.</p>
 */
public class XBeeNetwork {
	
	// Variables.

	private XBeeDevice localDevice;
	
	private Map<XBee64BitAddress, RemoteXBeeDevice> remotesBy64BitAddr;
	private Map<XBee16BitAddress, RemoteXBeeDevice> remotesBy16BitAddr;
	
	private List<IDiscoveryListener> discoveryListeners = new ArrayList<IDiscoveryListener>();
	
	private NodeDiscovery nodeDiscovery;
	
	protected Logger logger;
	
	/**
	 * Instantiates a new {@code XBeeNetwork} object.
	 * 
	 * @param device Local XBee device to get the network from.
	 * 
	 * @throws NullPointerException if {@code device == null}.
	 * 
	 * @see XBeeDevice
	 */
	XBeeNetwork(XBeeDevice device) {
		if (device == null)
			throw new NullPointerException("Local XBee device cannot be null.");
		
		localDevice = device;
		remotesBy64BitAddr = new ConcurrentHashMap<XBee64BitAddress, RemoteXBeeDevice>();
		remotesBy16BitAddr = new ConcurrentHashMap<XBee16BitAddress, RemoteXBeeDevice>();
		nodeDiscovery = new NodeDiscovery(localDevice);
		
		logger = LoggerFactory.getLogger(this.getClass());
	}
	
	/**
	 * Discovers and reports the first remote XBee device that matches the 
	 * supplied identifier.
	 * 
	 * <p>This method blocks until the device is discovered or the configured 
	 * timeout expires. To configure the discovery timeout, use the method
	 * {@link #setDiscoveryTimeout(long)}.</p>
	 * 
	 * <p>To configure the discovery options, use the 
	 * {@link #setDiscoveryOptions(Set)} method.</p> 
	 * 
	 * @param id The identifier of the device to be discovered.
	 * 
	 * @return The discovered remote XBee device with the given identifier, 
	 *         {@code null} if the timeout expires and the device was not found.
	 * 
	 * @throws IllegalArgumentException if {@code id.length() == 0}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code id == null}.
	 * @throws XBeeException if there is an error discovering the device.
	 * 
	 * @see #discoverDevices(List)
	 * @see #getDevice(String)
	 * @see RemoteXBeeDevice
	 */
	public RemoteXBeeDevice discoverDevice(String id) throws XBeeException {
		if (id == null)
			throw new NullPointerException("Device identifier cannot be null.");
		if (id.length() == 0)
			throw new IllegalArgumentException("Device identifier cannot be an empty string.");
		
		logger.debug("{}Discovering '{}' device.", localDevice.toString(), id);
		
		return nodeDiscovery.discoverDevice(id);
	}
	
	/**
	 * Discovers and reports all remote XBee devices that match the supplied 
	 * identifiers.
	 * 
	 * <p>This method blocks until the configured timeout expires. To configure 
	 * the discovery timeout, use the method {@link #setDiscoveryTimeout(long)}.
	 * </p>
	 * 
	 * <p>To configure the discovery options, use the 
	 * {@link #setDiscoveryOptions(Set)} method.</p> 
	 * 
	 * @param ids List which contains the identifiers of the devices to be 
	 *            discovered.
	 * 
	 * @return A list of the discovered remote XBee devices with the given 
	 *         identifiers.
	 * 
	 * @throws IllegalArgumentException if {@code ids.size() == 0}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code ids == null}.
	 * @throws XBeeException if there is an error discovering the devices.
	 * 
	 * @see #discoverDevice(String)
	 * @see RemoteXBeeDevice
	 */
	public List<RemoteXBeeDevice> discoverDevices(List<String> ids) throws XBeeException {
		if (ids == null)
			throw new NullPointerException("List of device identifiers cannot be null.");
		if (ids.size() == 0)
			throw new IllegalArgumentException("List of device identifiers cannot be empty.");
		
		logger.debug("{}Discovering all '{}' devices.", localDevice.toString(), ids.toString());
		
		return nodeDiscovery.discoverDevices(ids);
	}
	
	/**
	 * Adds the given discovery listener to the list of listeners to be notified 
	 * when the discovery process is running.
	 * 
	 * <p>If the listener has already been included, this method does nothing.
	 * </p>
	 * 
	 * @param listener Listener to be notified when the discovery process is
	 *                 running.
	 * 
	 * @throws NullPointerException if {@code listener == null}.
	 * 
	 * @see com.digi.xbee.api.listeners.IDiscoveryListener
	 * @see #removeDiscoveryListener(IDiscoveryListener)
	 */
	public void addDiscoveryListener(IDiscoveryListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		synchronized (discoveryListeners) {
			if (!discoveryListeners.contains(listener))
				discoveryListeners.add(listener);
		}
	}
	
	/**
	 * Removes the given discovery listener from the list of discovery 
	 * listeners.
	 * 
	 * <p>If the listener is not included in the list, this method does nothing.
	 * </p>
	 * 
	 * @param listener Discovery listener to remove.
	 * 
	 * @throws NullPointerException if {@code listener == null}.
	 * 
	 * @see com.digi.xbee.api.listeners.IDiscoveryListener
	 * @see #addDiscoveryListener(IDiscoveryListener)
	 */
	public void removeDiscoveryListener(IDiscoveryListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		synchronized (discoveryListeners) {
			if (discoveryListeners.contains(listener))
				discoveryListeners.remove(listener);
		}
	}
	
	/**
	 * Starts the discovery process with the configured timeout and options.
	 * 
	 * <p>To be notified every time an XBee device is discovered, add a
	 * {@code IDiscoveryListener} using the 
	 * {@link #addDiscoveryListener(IDiscoveryListener)} method before starting
	 * the discovery process.</p>
	 * 
	 * <p>To configure the discovery timeout, use the 
	 * {@link #setDiscoveryTimeout(long)} method.</p>
	 * 
	 * <p>To configure the discovery options, use the 
	 * {@link #setDiscoveryOptions(Set)} method.</p> 
	 * 
	 * @throws IllegalStateException if the discovery process is already running.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * 
	 * @see #addDiscoveryListener(IDiscoveryListener)
	 * @see #stopDiscoveryProcess()
	 */
	public void startDiscoveryProcess() {
		if (isDiscoveryRunning())
			throw new IllegalStateException("The discovery process is already running.");
		
		synchronized (discoveryListeners) {
			nodeDiscovery.startDiscoveryProcess(discoveryListeners);
		}
	}
	
	/**
	 * Stops the discovery process if it is running.
	 * 
	 * <p>Note that DigiMesh/DigiPoint devices are blocked until the discovery
	 * time configured (NT parameter) has elapsed, so if you try to get/set
	 * any parameter during the discovery process you will receive a timeout 
	 * exception.</p>
	 * 
	 * @see #isDiscoveryRunning()
	 * @see #removeDiscoveryListener(IDiscoveryListener)
	 * @see #startDiscoveryProcess()
	 */
	public void stopDiscoveryProcess() {
		nodeDiscovery.stopDiscoveryProcess();
	}
	
	/**
	 * Retrieves whether the discovery process is running or not.
	 * 
	 * @return {@code true} if the discovery process is running, {@code false} 
	 *         otherwise.
	 * 
	 * @see #startDiscoveryProcess()
	 * @see #stopDiscoveryProcess()
	 */
	public boolean isDiscoveryRunning() {
		return nodeDiscovery.isRunning();
	}
	
	/**
	 * Configures the discovery timeout ({@code NT} parameter) with the given 
	 * value.
	 * 
	 * <p>Note that in some protocols, the discovery process may take longer
	 * than the value set in this method due to the network propagation time.
	 * </p>
	 * 
	 * @param timeout New discovery timeout in milliseconds.
	 * 
	 * @throws TimeoutException if there is a timeout setting the discovery
	 *                          timeout.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setDiscoveryOptions(Set)
	 */
	public void setDiscoveryTimeout(long timeout) throws TimeoutException, XBeeException {
		if (timeout <= 0)
			throw new IllegalArgumentException("Timeout must be bigger than 0.");
		
		localDevice.setParameter("NT", ByteUtils.longToByteArray(timeout / 100));
	}
	
	/**
	 * Configures the discovery options ({@code NO} parameter) with the given 
	 * value.
	 * 
	 * @param options New discovery options.
	 * 
	 * @throws TimeoutException if there is a timeout setting the discovery
	 *                          options.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setDiscoveryTimeout(long)
	 * @see DiscoveryOptions
	 */
	public void setDiscoveryOptions(Set<DiscoveryOptions> options) throws TimeoutException, XBeeException {
		if (options == null)
			throw new NullPointerException("Options cannot be null.");
		
		int value = DiscoveryOptions.calculateDiscoveryValue(localDevice.getXBeeProtocol(), options);
		localDevice.setParameter("NO", ByteUtils.intToByteArray(value));
	}
	
	/**
	 * Returns all remote devices already contained in the network.
	 * 
	 * <p>Note that this method <b>does not perform a discovery</b>, only 
	 * returns the devices that have been previously discovered.</p>
	 * 
	 * @return A list with all XBee devices in the network.
	 * 
	 * @see #getDevices(String)
	 * @see RemoteXBeeDevice
	 */
	public List<RemoteXBeeDevice> getDevices() {
		List<RemoteXBeeDevice> nodes = new ArrayList<RemoteXBeeDevice>();
		nodes.addAll(remotesBy64BitAddr.values());
		nodes.addAll(remotesBy16BitAddr.values());
		return nodes;
	}
	
	/**
	 * Returns all remote devices that match the supplied identifier.
	 * 
	 * <p>Note that this method <b>does not perform a discovery</b>, only 
	 * returns the devices that have been previously discovered.</p>
	 * 
	 * @param id The identifier of the devices to be retrieved.
	 * 
	 * @return A list of the remote XBee devices contained in the network with 
	 *         the given identifier.
	 * 
	 * @throws IllegalArgumentException if {@code id.length() == 0}.
	 * @throws NullPointerException if {@code id == null}.
	 * 
	 * @see #getDevice(String)
	 * @see RemoteXBeeDevice
	 */
	public List<RemoteXBeeDevice> getDevices(String id) {
		if (id == null)
			throw new NullPointerException("Device identifier cannot be null.");
		if (id.length() == 0)
			throw new IllegalArgumentException("Device identifier cannot be an empty string.");
		
		List<RemoteXBeeDevice> devices = new ArrayList<RemoteXBeeDevice>();
		
		// Look in the 64-bit map.
		for (RemoteXBeeDevice remote : remotesBy64BitAddr.values()) {
			if (id.equals(remote.getNodeID()))
				devices.add(remote);
		}
		// Look in the 16-bit map.
		for (RemoteXBeeDevice remote : remotesBy16BitAddr.values()) {
			if (id.equals(remote.getNodeID()))
				devices.add(remote);
		}
		// Return the list.
		return devices;
	}
	
	/**
	 * Returns the first remote device that matches the supplied identifier.
	 * 
	 * <p>Note that this method <b>does not perform a discovery</b>, only 
	 * returns the device that has been previously discovered.</p>
	 * 
	 * @param id The identifier of the device to be retrieved.
	 * 
	 * @return The remote XBee device contained in the network with the given 
	 *         identifier, {@code null} if the network does not contain any 
	 *         device with that Node ID.
	 * 
	 * @throws IllegalArgumentException if {@code id.length() == 0}.
	 * @throws NullPointerException if {@code id == null}.
	 * 
	 * @see #discoverDevice(String)
	 * @see #getDevices(String)
	 * @see RemoteXBeeDevice
	 */
	public RemoteXBeeDevice getDevice(String id) {
		if (id == null)
			throw new NullPointerException("Device identifier cannot be null.");
		if (id.length() == 0)
			throw new IllegalArgumentException("Device identifier cannot be an empty string.");
		
		// Look in the 64-bit map.
		for (RemoteXBeeDevice remote : remotesBy64BitAddr.values()) {
			if (id.equals(remote.getNodeID()))
				return remote;
		}
		// Look in the 16-bit map.
		for (RemoteXBeeDevice remote : remotesBy16BitAddr.values()) {
			if (id.equals(remote.getNodeID()))
				return remote;
		}
		// The given ID is not in the network.
		return null;
	}
	
	/**
	 * Returns the remote device already contained in the network whose 64-bit 
	 * address matches the given one.
	 * 
	 * <p>Note that this method <b>does not perform a discovery</b>, only 
	 * returns the device that has been previously discovered.</p>
	 * 
	 * @param address The 64-bit address of the device to be retrieved.
	 * 
	 * @return The remote device in the network or {@code null} if it is not 
	 *         found.
	 * 
	 * @throws IllegalArgumentException if {@code address.equals(XBee64BitAddress.UNKNOWN_ADDRESS)}.
	 * @throws NullPointerException if {@code address == null}.
	 */
	public RemoteXBeeDevice getDevice(XBee64BitAddress address) {
		if (address == null)
			throw new NullPointerException("64-bit address cannot be null.");
		if (address.equals(XBee64BitAddress.UNKNOWN_ADDRESS))
			throw new IllegalArgumentException("64-bit address cannot be unknown.");
		
		logger.debug("{}Getting device '{}' from network.", localDevice.toString(), address);
		
		return remotesBy64BitAddr.get(address);
	}
	
	/**
	 * Returns the remote device already contained in the network whose 16-bit 
	 * address matches the given one.
	 * 
	 * <p>Note that this method <b>does not perform a discovery</b>, only 
	 * returns the device that has been previously discovered.</p>
	 * 
	 * @param address The 16-bit address of the device to be retrieved.
	 * 
	 * @return The remote device in the network or {@code null} if it is not 
	 *         found.
	 * 
	 * @throws IllegalArgumentException if {@code address.equals(XBee16BitAddress.UNKNOWN_ADDRESS)}.
	 * @throws NullPointerException if {@code address == null}.
	 * @throws OperationNotSupportedException if the protocol of the local XBee device is DigiMesh or Point-to-Multipoint.
	 */
	public RemoteXBeeDevice getDevice(XBee16BitAddress address) throws OperationNotSupportedException {
		if (localDevice.getXBeeProtocol() == XBeeProtocol.DIGI_MESH)
			throw new OperationNotSupportedException("DigiMesh protocol does not support 16-bit addressing.");
		if (localDevice.getXBeeProtocol() == XBeeProtocol.DIGI_POINT)
			throw new OperationNotSupportedException("Point-to-Multipoint protocol does not support 16-bit addressing.");
		if (address == null)
			throw new NullPointerException("16-bit address cannot be null.");
		if (address.equals(XBee16BitAddress.UNKNOWN_ADDRESS))
			throw new IllegalArgumentException("16-bit address cannot be unknown.");
		
		logger.debug("{}Getting device '{}' from network.", localDevice.toString(), address);
		
		// The preference order is: 
		//    1.- Look in the 64-bit map 
		//    2.- Then in the 16-bit map.
		// This should be maintained in the 'addRemoteDevice' method.
		
		RemoteXBeeDevice devInNetwork = null;
		
		// Look in the 64-bit map.
		Collection<RemoteXBeeDevice> devices = remotesBy64BitAddr.values();
		for (RemoteXBeeDevice d: devices) {
			XBee16BitAddress a = get16BitAddress(d);
			if (a != null && a.equals(address)) {
				devInNetwork = d;
				break;
			}
		}
		
		// Look in the 16-bit map.
		if (devInNetwork == null)
			devInNetwork = remotesBy16BitAddr.get(address);
		
		return devInNetwork;
	}
	
	/**
	 * Adds the given remote device to the network. 
	 * 
	 * <p>Notice that this operation does not join the remote XBee device to the
	 * network; it just tells the network that it contains that device. However, 
	 * the device has only been added to the device list, and may not be 
	 * physically in the same network.</p>
	 * 
	 * <p>The way of adding a device to the network is based on the 64-bit 
	 * address. If it is not configured:</p>
	 * 
	 * <ul>
	 * <li>For 802.15.4 and ZigBee devices, it will use the 16-bit address.</li>
	 * <li>For the rest will return {@code false} as the result of the addition.</li>
	 * </ul>
	 * 
	 * @param remoteDevice The remote device to be added to the network.
	 * 
	 * @return The remote XBee Device instance in the network, {@code null} if
	 *         the device could not be successfully added.
	 * 
	 * @throws NullPointerException if {@code remoteDevice == null}.
	 * 
	 * @see #addRemoteDevices(List)
	 * @see #removeRemoteDevice(RemoteXBeeDevice)
	 * @see RemoteXBeeDevice
	 */
	public RemoteXBeeDevice addRemoteDevice(RemoteXBeeDevice remoteDevice) {
		if (remoteDevice == null)
			throw new NullPointerException("Remote device cannot be null.");
		
		logger.debug("{}Adding device '{}' to network.", localDevice.toString(), remoteDevice.toString());
		
		RemoteXBeeDevice devInNetwork = null;
		XBee64BitAddress addr64 = remoteDevice.get64BitAddress();
		XBee16BitAddress addr16 = get16BitAddress(remoteDevice);
		
		// Check if the device has 64-bit address.
		if (addr64 != null && !addr64.equals(XBee64BitAddress.UNKNOWN_ADDRESS)) {
			// The device has 64-bit address, so look in the 64-bit map.
			devInNetwork = remotesBy64BitAddr.get(addr64);
			if (devInNetwork != null) {
				// The device exists in the 64-bit map, so update the reference and return it.
				logger.debug("{}Existing device '{}' in network.", localDevice.toString(), devInNetwork.toString());
				devInNetwork.updateDeviceDataFrom(remoteDevice);
				return devInNetwork;
			} else {
				// The device does not exist in the 64-bit map, so check its 16-bit address.
				if (addr16 != null && !addr16.equals(XBee16BitAddress.UNKNOWN_ADDRESS)) {
					// The device has 16-bit address, so look in the 16-bit map.
					devInNetwork = remotesBy16BitAddr.get(addr16);
					if (devInNetwork != null) {
						// The device exists in the 16-bit map, so remove it and add it to the 64-bit map.
						logger.debug("{}Existing device '{}' in network.", localDevice.toString(), devInNetwork.toString());
						devInNetwork = remotesBy16BitAddr.remove(addr16);
						devInNetwork.updateDeviceDataFrom(remoteDevice);
						remotesBy64BitAddr.put(addr64, devInNetwork);
						return devInNetwork;
					} else {
						// The device does not exist in the 16-bit map, so add it to the 64-bit map.
						remotesBy64BitAddr.put(addr64, remoteDevice);
						return remoteDevice;
					}
				} else {
					// The device has not 16-bit address, so add it to the 64-bit map.
					remotesBy64BitAddr.put(addr64, remoteDevice);
					return remoteDevice;
				}
			}
		}
		
		// If the device has not 64-bit address, check if it has 16-bit address.
		if (addr16 != null && !addr16.equals(XBee16BitAddress.UNKNOWN_ADDRESS)) {
			// The device has 16-bit address, so look in the 64-bit map.
			Collection<RemoteXBeeDevice> devices = remotesBy64BitAddr.values();
			for (RemoteXBeeDevice d : devices) {
				XBee16BitAddress a = get16BitAddress(d);
				if (a != null && a.equals(addr16)) {
					devInNetwork = d;
					break;
				}
			}
			// Check if the device exists in the 64-bit map.
			if (devInNetwork != null) {
				// The device exists in the 64-bit map, so update the reference and return it.
				logger.debug("{}Existing device '{}' in network.", localDevice.toString(), devInNetwork.toString());
				devInNetwork.updateDeviceDataFrom(remoteDevice);
				return devInNetwork;
			} else {
				// The device does not exist in the 64-bit map, so look in the 16-bit map.
				devInNetwork = remotesBy16BitAddr.get(addr16);
				if (devInNetwork != null) {
					// The device exists in the 16-bit map, so update the reference and return it.
					logger.debug("{}Existing device '{}' in network.", localDevice.toString(), devInNetwork.toString());
					devInNetwork.updateDeviceDataFrom(remoteDevice);
					return devInNetwork;
				} else {
					// The device does not exist in the 16-bit map, so add it.
					remotesBy16BitAddr.put(addr16, remoteDevice);
					return remoteDevice;
				}
			}
		}
		
		// If the device does not contain a valid address, return null.
		logger.error("{}Remote device '{}' cannot be added: 64-bit and 16-bit addresses must be specified.", 
				localDevice.toString(), remoteDevice.toString());
		return null;
	}
	
	/**
	 * Adds the given list of remote devices to the network.
	 * 
	 * <p>Notice that this operation does not join the remote XBee devices to 
	 * the network; it just tells the network that it contains those devices. 
	 * However, the devices have only been added to the device list, and may 
	 * not be physically in the same network.</p>
	 * 
	 * <p>The way of adding a device to the network is based on the 64-bit 
	 * address. If it is not configured:</p>
	 * 
	 * <ul>
	 * <li>For 802.15.4 and ZigBee devices, the 16-bit address will be used instead.</li>
	 * <li>For the rest will return {@code false} as the result of the addition.</li>
	 * </ul>
	 * 
	 * @param list The list of remote devices to be added to the network.
	 * 
	 * @return A list with the successfully added devices to the network.
	 * 
	 * @throws NullPointerException if {@code list == null}.
	 * 
	 * @see #addRemoteDevice(RemoteXBeeDevice)
	 * @see RemoteXBeeDevice
	 */
	public List<RemoteXBeeDevice> addRemoteDevices(List<RemoteXBeeDevice> list) {
		if (list == null)
			throw new NullPointerException("The list of remote devices cannot be null.");
		
		List<RemoteXBeeDevice> addedList = new ArrayList<RemoteXBeeDevice>(list.size());
		
		if (list.size() == 0)
			return addedList;
		
		logger.debug("{}Adding '{}' devices to network.", localDevice.toString(), list.size());
		
		for (int i = 0; i < list.size(); i++) {
			RemoteXBeeDevice toAdd = list.get(i);
			if (toAdd == null)
				continue;
			
			RemoteXBeeDevice d = addRemoteDevice(toAdd);
			if (d != null)
				addedList.add(d);
		}
		
		return addedList;
	}
	
	/**
	 * Removes the given remote XBee device from the network.
	 * 
	 * <p>Notice that this operation does not remove the remote XBee device 
	 * from the actual XBee network; it just tells the network object that it 
	 * will no longer contain that device. However, next time a discovery is 
	 * performed, it could be added again automatically.</p>
	 * 
	 * <p>This method will check for a device that matches the 64-bit address 
	 * of the provided one, if found, that device will be removed from the 
	 * corresponding list. In case the 64-bit address is not defined, it will 
	 * use the 16-bit address for DigiMesh and ZigBee devices.</p>
	 * 
	 * @param remoteDevice The remote device to be removed from the network.
	 * 
	 * @throws NullPointerException if {@code RemoteDevice == null}.
	 * 
	 * @see #addRemoteDevice(RemoteXBeeDevice)
	 * @see #clearDeviceList()
	 * @see RemoteXBeeDevice
	 */
	public void removeRemoteDevice(RemoteXBeeDevice remoteDevice) {
		if (remoteDevice == null)
			throw new NullPointerException("Remote device cannot be null.");
		
		RemoteXBeeDevice devInNetwork = null;
		
		// Look in the 64-bit map.
		XBee64BitAddress addr64 = remoteDevice.get64BitAddress();
		if (addr64 != null && !addr64.equals(XBee64BitAddress.UNKNOWN_ADDRESS)) {
			devInNetwork = remotesBy64BitAddr.get(addr64);
			
			// Remove the device.
			if (devInNetwork != null) {
				remotesBy64BitAddr.remove(addr64);
				return;
			}
		}
		
		// If not found, look in the 16-bit map.
		XBee16BitAddress addr16 = get16BitAddress(remoteDevice);
		if (addr16 != null && !addr16.equals(XBee16BitAddress.UNKNOWN_ADDRESS)) {
			
			// The preference order is: 
			//    1.- Look in the 64-bit map 
			//    2.- Then in the 16-bit map.
			// This should be maintained in the 'getDeviceBy16BitAddress' method.
			
			// Look for the 16-bit address in the 64-bit map.
			Collection<RemoteXBeeDevice> devices = remotesBy64BitAddr.values();
			for (RemoteXBeeDevice d: devices) {
				XBee16BitAddress a = get16BitAddress(d);
				if (a != null && a.equals(addr16)) {
					remotesBy64BitAddr.remove(d.get64BitAddress());
					return;
				}
			}
			
			// If not found, look for the 16-bit address in the 16-bit map. 
			devInNetwork = remotesBy16BitAddr.get(addr16);
			
			// Remove the device.
			if (devInNetwork != null) {
				remotesBy16BitAddr.remove(addr16);
				return;
			}
		}
		
		// If the device does not contain a valid address log an error.
		if ((addr64 == null || addr64.equals(XBee64BitAddress.UNKNOWN_ADDRESS)) 
				&& (addr16 == null || addr16.equals(XBee16BitAddress.UNKNOWN_ADDRESS)))
			logger.error("{}Remote device '{}' cannot be removed: 64-bit and 16-bit addresses must be specified.", 
					localDevice.toString(), remoteDevice.toString());
	}
	
	/**
	 * Removes all the devices from this network. 
	 * 
	 * <p>The network will be empty after this call returns.</p>
	 * 
	 * <p>Notice that this does not imply removing the XBee devices from the 
	 * actual XBee network; it just tells the object that the list should be 
	 * empty now. Next time a discovery is performed, the list could be filled 
	 * with the remote XBee devices found.</p>
	 * 
	 * @see #removeRemoteDevice(RemoteXBeeDevice)
	 */
	public void clearDeviceList() {
		logger.debug("{}Clearing the network.", localDevice.toString());
		remotesBy64BitAddr.clear();
		remotesBy16BitAddr.clear();
	}
	
	/**
	 * Returns the number of devices already discovered in this network.
	 * 
	 * @return The number of devices already discovered in this network.
	 */
	public int getNumberOfDevices() {
		return remotesBy64BitAddr.size() + remotesBy16BitAddr.size();
	}
	
	/**
	 * Retrieves the 16-bit address of the given remote device.
	 * 
	 * @param device The remote device to get the 16-bit address.
	 * 
	 * @return The 16-bit address of the device, {@code null} if it does not
	 *         contain a valid one.
	 */
	private XBee16BitAddress get16BitAddress(RemoteXBeeDevice device) {
		if (device == null)
			return null;
		
		XBee16BitAddress address = null;
		
		switch (device.getXBeeProtocol()) {
		case RAW_802_15_4:
			address = ((RemoteRaw802Device)device).get16BitAddress();
			break;
		case ZIGBEE:
			address = ((RemoteZigBeeDevice)device).get16BitAddress();
			break;
		default:
			// TODO should we allow this operation for general remote devices?
			address = device.get16BitAddress();
			break;
		}
		
		return address;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return getClass().getName() + " [" + localDevice.toString() + "] @" + 
				Integer.toHexString(hashCode());
	}
}
