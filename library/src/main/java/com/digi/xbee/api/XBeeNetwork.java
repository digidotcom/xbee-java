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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.models.DiscoveryOptions;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
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
	
	private ArrayList<IDiscoveryListener> discoveryListeners = new ArrayList<IDiscoveryListener>();
	
	private NodeDiscovery nodeDiscovery;
	
	protected Logger logger;
	
	/**
	 * Instantiates a new XBee Network object.
	 * 
	 * @param device Local XBee device to get the network from.
	 * 
	 * @throws NullPointerException If {@code device == null}.
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
	 * timeout expires.</p>
	 * 
	 * <p>To look for all the devices with an specific identifier use 
	 * {@link #discoverDevicesByNodeID(String)}.</p>
	 * 
	 * @param id The identifier of the device to be discovered.
	 * 
	 * @return The discovered remote XBee device with the given identifier, 
	 *         {@code null} if the timeout expires and the device was not found.
	 * 
	 * @throws NullPointerException If {@code id == null}.
	 * @throws IllegalArgumentException If {@code id.length() == 0}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws XBeeException If there is an error discovering the device.
	 * 
	 * @see #getDeviceByNodeID(String)
	 * @see #discoverDevicesByNodeID(String)
	 * @see RemoteXBeeDevice
	 */
	public RemoteXBeeDevice discoverDeviceByNodeID(String id) throws XBeeException {
		if (id == null)
			throw new NullPointerException("Device identifier cannot be null.");
		if (id.length() == 0)
			throw new IllegalArgumentException("Device identifier cannot be an empty string.");
		
		logger.debug("{}Discovering '{}' device.", toString(), id);
		
		return nodeDiscovery.discoverDeviceByNodeID(id);
	}
	
	/**
	 * Discovers and reports all remote XBee devices that match the supplied 
	 * identifier.
	 * 
	 * <p>This method blocks until the configured timeout expires.</p>
	 * 
	 * <p>To look for the first device with an specific identifier use 
	 * {@link #discoverDeviceByNodeID(String)}.</p>
	 * 
	 * @param id The identifier of the devices to be discovered.
	 * 
	 * @return A list of the discovered remote XBee devices with the given 
	 *         identifier.
	 * 
	 * @throws NullPointerException If {@code id == null}.
	 * @throws IllegalArgumentException If {@code id.length() == 0}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws XBeeException If there is an error discovering the devices.
	 * 
	 * @see #getDevicesByNodeID(String)
	 * @see #discoverDeviceByNodeID(String)
	 * @see RemoteXBeeDevice
	 */
	public List<RemoteXBeeDevice> discoverDevicesByNodeID(String id) throws XBeeException {
		if (id == null)
			throw new NullPointerException("Device identifier cannot be null.");
		if (id.length() == 0)
			throw new IllegalArgumentException("Device identifier cannot be an empty string.");
		
		logger.debug("{}Discovering all '{}' devices.", toString(), id);
		
		return nodeDiscovery.discoverDevicesByNodeID(id);
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
	 * @see #getDevices()
	 * @see RemoteXBeeDevice
	 */
	public List<RemoteXBeeDevice> discoverDevices() throws XBeeException {
		logger.debug("{}Discovering devices from the network.", toString());
		return nodeDiscovery.discoverDevices();
	}
	
	/**
	 * Adds the given discovery listener to the list of listeners to be notified 
	 * when the discovery process is running.
	 * 
	 * <p>If the listener has been already included this method does nothing.
	 * </p>
	 * 
	 * @param listener Listener to be notified when the discovery process is
	 *                 running.
	 * 
	 * @throws NullPointerException If {@code listener == null}.
	 * 
	 * @see IDiscoveryListener
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
	 * @throws NullPointerException If {@code listener == null}.
	 * 
	 * @see IDiscoveryListener
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
	 * @throws InterfaceNotOpenException If the device is not open.
	 */
	public void startDiscoveryProcess() {
		nodeDiscovery.startDiscoveryProcess(discoveryListeners);
	}
	
	/**
	 * Stops the discovery process if it is running.
	 * 
	 * <p>Note that DigiMesh/DigiPoint devices are blocked until the discovery
	 * time configured (NT parameter) has elapsed, so if you try to get/set
	 * any parameter during the discovery process you will receive a timeout 
	 * exception.</p>
	 */
	public void stopDiscoveryProcess() {
		nodeDiscovery.stopDiscoveryProcess();
	}
	
	/**
	 * Retrieves whether or not the discovery process is running.
	 * 
	 * @return {@code true} if the discovery process is running, {@code false} 
	 *         otherwise.
	 */
	public boolean isDiscoveryRunning() {
		return nodeDiscovery.isRunning();
	}
	
	/**
	 * Configures the discovery timeout (NT parameter) with the given value.
	 * 
	 * @param timeout New discovery timeout in milliseconds.
	 * 
	 * @throws TimeoutException if there is a timeout setting the discovery
	 *                          timeout.
	 * @throws XBeeException if there is any other XBee related exception.
	 */
	public void setDiscoveryTimeout(long timeout) throws TimeoutException, XBeeException {
		if (timeout <= 0)
			throw new IllegalArgumentException("Timeout must be bigger than 0.");
		
		localDevice.setParameter("NT", ByteUtils.longToByteArray(timeout / 100));
	}
	
	/**
	 * Configures the discovery options (NO parameter) with the given value.
	 * 
	 * @param options New discovery options.
	 * 
	 * @throws TimeoutException if there is a timeout setting the discovery
	 *                          options.
	 * @throws XBeeException if there is any other XBee related exception.
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
	 * @see #discoverDevices()
	 * @see RemoteXBeeDevice
	 */
	public ArrayList<RemoteXBeeDevice> getDevices() {
		ArrayList<RemoteXBeeDevice> nodes = new ArrayList<RemoteXBeeDevice>();
		nodes.addAll(remotesBy64BitAddr.values());
		nodes.addAll(remotesBy16BitAddr.values());
		return nodes;
	}
	
	/**
	 * Returns the first remote device that matches the supplied identifier.
	 * 
	 * <p>Note that this method <b>does not perform a discovery</b>, only 
	 * returns the device that has been previously discovered.</p>
	 * 
	 * <p>To look for all the devices with an specific identifier use 
	 * {@link #getDevicesByNodeID(String)}.</p>
	 * 
	 * @param id The identifier of the device to be retrieved.
	 * 
	 * @return The remote XBee device contained in the network with the given 
	 *         identifier, {@code null} if the network does not contain any 
	 *         device with that Node ID.
	 * 
	 * @throws NullPointerException If {@code id == null}.
	 * @throws IllegalArgumentException If {@code id.length() == 0}.
	 * 
	 * @see #getDevicesByNodeID(String)
	 * @see #discoverDeviceByNodeID(String)
	 * @see #discoverDevicesByNodeID(String)
	 * @see RemoteXBeeDevice
	 */
	public RemoteXBeeDevice getDeviceByNodeID(String id) {
		if (id == null)
			throw new NullPointerException("Device identifier cannot be null.");
		if (id.length() == 0)
			throw new IllegalArgumentException("Device identifier cannot be an empty string.");
		
		// Look in the 64-bit map.
		for (RemoteXBeeDevice remote : remotesBy64BitAddr.values()) {
			if (remote.getNodeID().equals(id))
				return remote;
		}
		// Look in the 16-bit map.
		for (RemoteXBeeDevice remote : remotesBy16BitAddr.values()) {
			if (remote.getNodeID().equals(id))
				return remote;
		}
		// The given ID is not in the network.
		return null;
	}
	
	/**
	 * Returns all remote devices that match the supplied identifier.
	 * 
	 * <p>Note that this method <b>does not perform a  discovery</b>, only 
	 * returns the devices that have been previously discovered.</p>
	 * 
	 * <p>To look for the first device with an specific identifier use 
	 * {@link #getDeviceByNodeID(String)}.</p>
	 * 
	 * @param id The identifier of the devices to be retrieved.
	 * 
	 * @return A list of the remote XBee devices contained in the network with 
	 *         the given identifier.
	 * 
	 * @throws NullPointerException If {@code id == null}.
	 * @throws IllegalArgumentException If {@code id.length() == 0}.
	 * 
	 * @see #getDeviceByNodeID(String)
	 * @see #discoverDevicesByNodeID(String)
	 * @see RemoteXBeeDevice
	 */
	public ArrayList<RemoteXBeeDevice> getDevicesByNodeID(String id) {
		if (id == null)
			throw new NullPointerException("Device identifier cannot be null.");
		if (id.length() == 0)
			throw new IllegalArgumentException("Device identifier cannot be an empty string.");
		
		ArrayList<RemoteXBeeDevice> devices = new ArrayList<RemoteXBeeDevice>();
		
		// Look in the 64-bit map.
		for (RemoteXBeeDevice remote : remotesBy64BitAddr.values()) {
			if (remote.getNodeID().equals(id))
				devices.add(remote);
		}
		// Look in the 16-bit map.
		for (RemoteXBeeDevice remote : remotesBy16BitAddr.values()) {
			if (remote.getNodeID().equals(id))
				devices.add(remote);
		}
		// Return the list.
		return devices;
	}
	
	/**
	 * Returns the remote device already contained in the network whose 64-bit 
	 * address matches the given one.
	 * 
	 * @param address The 64-bit address of the device to be retrieved.
	 * 
	 * @return The remote device in the network or {@code null} if it is not 
	 *         found.
	 * 
	 * @throws IllegalArgumentException If {@code address.equals(XBee64BitAddress.UNKNOWN_ADDRESS)}.
	 * @throws NullPointerException If {@code address == null}.
	 */
	public RemoteXBeeDevice getDeviceBy64BitAddress(XBee64BitAddress address) {
		if (address == null)
			throw new NullPointerException("64-bit address cannot be null.");
		if (address.equals(XBee64BitAddress.UNKNOWN_ADDRESS))
			throw new NullPointerException("64-bit address cannot be unknown.");
		
		logger.debug("{}Getting device '{}' from network.", toString(), address);
		
		return remotesBy64BitAddr.get(address);
	}
	
	/**
	 * Returns the remote device already contained in the network whose 16-bit 
	 * address matches the given one.
	 * 
	 * @param address The 16-bit address of the device to be retrieved.
	 * 
	 * @return The remote device in the network or {@code null} if it is not 
	 *         found.
	 * 
	 * @throws IllegalArgumentException If {@code address.equals(XBee16BitAddress.UNKNOWN_ADDRESS)}.
	 * @throws NullPointerException If {@code address == null}.
	 */
	public RemoteXBeeDevice getDeviceBy16BitAddress(XBee16BitAddress address) {
		if (address == null)
			throw new NullPointerException("16-bit address cannot be null.");
		if (address.equals(XBee16BitAddress.UNKNOWN_ADDRESS))
			throw new NullPointerException("16-bit address cannot be unknown.");
		
		logger.debug("{}Getting device '{}' from network.", toString(), address);
		
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
	 * Adds a remote device to the network.
	 * 
	 * <p>This method will look for the 64-bit address. If it is not configured:
	 * </p>
	 * 
	 * <ul>
	 * <li>For 802.15.4 devices, it will look for the 16-bit address.</li>
	 * <li>For the rest will return {@code false} as the result of the addition.
	 * </li>
	 * </ul>
	 * 
	 * @param remoteDevice The remote device to be added to the network.
	 * 
	 * @return The remote XBee Device instance in the network, {@code null} if
	 *         the device could not be successfully added.
	 * 
	 * @throws NullPointerException If {@code RemoteDevice == null}.
	 */
	public RemoteXBeeDevice addRemoteDevice(RemoteXBeeDevice remoteDevice) {
		if (remoteDevice == null)
			throw new NullPointerException("Remote device cannot be null.");
		
		logger.debug("{}Adding device '{}' to network.", toString(), remoteDevice.toString());
		
		RemoteXBeeDevice devInNetwork = null;
		XBee64BitAddress addr64 = remoteDevice.get64BitAddress();
		XBee16BitAddress addr16 = get16BitAddress(remoteDevice);
		
		// Check if the device has 64-bit address.
		if (addr64 != null && !addr64.equals(XBee64BitAddress.UNKNOWN_ADDRESS)) {
			// The device has 64-bit address, so look in the 64-bit map.
			devInNetwork = remotesBy64BitAddr.get(addr64);
			if (devInNetwork != null) {
				// The device exists in the 64-bit map, so update the reference and return it.
				logger.debug("{}Existing device '{}' in network.", toString(), devInNetwork.toString());
				devInNetwork.updateDeviceDataFrom(remoteDevice);
				return devInNetwork;
			} else {
				// The device does not exist in the 64-bit map, so check its 16-bit address.
				if (addr16 != null && !addr16.equals(XBee16BitAddress.UNKNOWN_ADDRESS)) {
					// The device has 16-bit address, so look in the 16-bit map.
					devInNetwork = remotesBy16BitAddr.get(addr16);
					if (devInNetwork != null) {
						// The device exists in the 16-bit map, so remove it and add it to the 64-bit map.
						logger.debug("{}Existing device '{}' in network.", toString(), devInNetwork.toString());
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
				logger.debug("{}Existing device '{}' in network.", toString(), devInNetwork.toString());
				devInNetwork.updateDeviceDataFrom(remoteDevice);
				return devInNetwork;
			} else {
				// The device does not exist in the 64-bit map, so look in the 16-bit map.
				devInNetwork = remotesBy16BitAddr.get(addr16);
				if (devInNetwork != null) {
					// The device exists in the 16-bit map, so update the reference and return it.
					logger.debug("{}Existing device '{}' in network.", toString(), devInNetwork.toString());
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
		logger.error("{}Remote device '{}' cannot be added: 64-bit and 16-bit addresses must be specified.", toString(), remoteDevice.toString());
		return null;
	}
	
	/**
	 * Adds the given list of remote devices to the network.
	 * 
	 * <p>The way of adding a device to the network is based on the 64-bit 
	 * address. If it is not configured:</p>
	 * 
	 * <ul>
	 * <li>For 802.15.4 devices, the 16-bit address will be used instead.</li>
	 * <li>For the rest will return {@code false} as the result of the addition.
	 * </li>
	 * </ul>
	 * 
	 * @param list The list of remote devices to be added to the network.
	 * 
	 * @return A list with the successfully added devices to the network.
	 * 
	 * @throws NullPointerException If {@code list == null}.
	 */
	public List<RemoteXBeeDevice> addRemoteDevices(List<RemoteXBeeDevice> list) {
		if (list == null)
			throw new NullPointerException("The list of remote devices cannot be null.");
		
		List<RemoteXBeeDevice> addedList = new ArrayList<RemoteXBeeDevice>(list.size());
		
		if (list.size() == 0)
			return addedList;
		
		logger.debug("{}Adding '{}' devices to network.", toString(), list.size());
		
		for (int i = 0; i < list.size(); i++) {
			RemoteXBeeDevice d = addRemoteDevice(list.get(i));
			if (d != null)
				addedList.add(d);
		}
		
		return addedList;
	}
	
	/**
	 * Removes the given remote XBee device from the network. This method will check 
	 * for a device that matches the 64-bit address of the provided one, if found, that 
	 * device will be removed from the corresponding list. In case the 64-bit address is 
	 * null, it will look for the device in the list of 16-bit addresses.
	 * 
	 * @param remoteDevice The remote device to be removed from the network.
	 * 
	 * @throws NullPointerException If {@code RemoteDevice == null}.
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
			logger.error("{}Remote device '{}' cannot be removed: 64-bit and 16-bit addresses must be specified.", toString(), remoteDevice.toString());
	}
	
	/**
	 * Removes all the devices from this network. The network will be empty 
	 * after this call returns.
	 */
	public void clear() {
		logger.debug("{}Clearing network.", toString());
		remotesBy64BitAddr.clear();
		remotesBy64BitAddr.clear();
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
		return localDevice.toString();
	}
}
