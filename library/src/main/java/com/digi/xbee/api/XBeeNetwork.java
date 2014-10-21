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
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.models.DiscoveryOptions;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;

/**
 * An object that represents an XBee Network.
 *  
 * <p>The network allows the discovery of remote devices in the same network 
 * as the local one and stores them.</p>
 */
public class XBeeNetwork {
	
	// Constants.
	
	/**
	 * Default timeout to finish the discovery process: {@value} ms.
	 */
	public final static long DISCOVERY_TIMEOUT_DEFAULT = NodeDiscovery.DEFAULT_TIMEOUT;
	
	/**
	 * Use device configured discovery timeout ({@code NT}).
	 */
	public final static long DISCOVERY_TIMEOUT_DEVICE = NodeDiscovery.USE_DEVICE_TIMEOUT;
	
	/**
	 * Discovery timeout to wait until the process finishes.
	 */
	public final static long DISCOVERY_TIMEOUT_WAIT_FOREVER = NodeDiscovery.WAIT_FOREVER;
	
	// Variables.

	private XBeeDevice localDevice;
	
	private Map<XBee64BitAddress, RemoteXBeeDevice> remotesBy64BitAddr;
	private Map<XBee16BitAddress, RemoteXBeeDevice> remotesBy16BitAddr;
	
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
	 * <p>This method blocks until the device is discovered or the discover 
	 * timeout configured in the device ({@code NT}) expires.</p>
	 * 
	 * <p>To look for all the devices with an specific identifier use 
	 * {@link XBeeNetwork#discoverAllDevicesByID(String, long)}.</p>
	 * 
	 * @param id The identifier of the device to be discovered.
	 * 
	 * @return The discovered remote XBee device with the given identifier, 
	 *         {@code null} if the timeout expires and the device was not found.
	 * 
	 * @throws IllegalArgumentException If {@code id.length() == 0}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws NullPointerException If {@code id == null}.
	 * @throws XBeeException If there is an error discovering the device.
	 * 
	 * @see #getDeviceByID(String)
	 * @see #discoverDeviceByID(String, long)
	 * @see #discoverAllDevicesByID(String, long)
	 * @see RemoteXBeeDevice
	 */
	public RemoteXBeeDevice discoverDeviceByID(String id) throws XBeeException {
		return discoverDeviceByID(id, DISCOVERY_TIMEOUT_DEVICE);
	}
	
	/**
	 * Discovers and reports the first remote XBee device that matches the 
	 * supplied identifier.
	 * 
	 * <p>This method blocks until the device is discovered or the provided 
	 * timeout expires.</p>
	 * 
	 * <p>If {@code timeout == DISCOVERY_TIMEOUT_DEVICE}, the time 
	 * configured in the device will be used ({@code NT}).</p>
	 * 
	 * <p>To look for all the devices with an specific identifier use 
	 * {@link XBeeNetwork#discoverAllDevicesByID(String, long)}.</p>
	 * 
	 * @param id The identifier of the device to be discovered.
	 * @param timeout The timeout in milliseconds to wait for the device to be 
	 *                discovered.
	 * 
	 * @return The discovered remote XBee device with the given identifier, 
	 *         {@code null} if the timeout expires and the device was not found.
	 * 
	 * @throws IllegalArgumentException If {@code id.length() == 0} or
	 *                                  if {@code timeout < 0} or
	 *                                  if {@code timeout == DISCOVERY_TIMEOUOT_WAIT_FOREVER}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws NullPointerException If {@code id == null}.
	 * @throws XBeeException If there is an error discovering the device.
	 * 
	 * @see #getDeviceByID(String)
	 * @see #discoverDeviceByID(String)
	 * @see #discoverAllDevicesByID(String, long)
	 * @see #DISCOVERY_TIMEOUT_DEVICE
	 * @see RemoteXBeeDevice
	 */
	public RemoteXBeeDevice discoverDeviceByID(String id, long timeout) throws XBeeException {
		if (id == null)
			throw new NullPointerException("Device identifier cannot be null.");
		if (id.length() == 0)
			throw new IllegalArgumentException("Device identifier cannot be an empty string.");
		if (timeout == DISCOVERY_TIMEOUT_WAIT_FOREVER)
			throw new IllegalArgumentException("The discovery devices process cannot block forever.");
		if (timeout < DISCOVERY_TIMEOUT_DEVICE)
			throw new IllegalArgumentException("The timeout must be bigger than 0.");
		
		logger.debug("{}Discovering '{}' device ('{}' ms).", toString(), id, 
				timeout == DISCOVERY_TIMEOUT_DEVICE ? "configured NT" : timeout);
		
		return nodeDiscovery.discoverDeviceByID(id, timeout);
	}
	
	/**
	 * Discovers and reports all RF modules that match the supplied identifier.
	 * 
	 * <p>This method blocks till the provided {@code timeout} expires.</p>
	 * 
	 * <p>To look for the first device with an specific identifier use 
	 * {@link XBeeNetwork#discoverDeviceByID(String)}.</p>
	 * 
	 * @param id The identifier of the devices to be discovered.
	 * @param timeout The timeout in milliseconds to wait for the devices to be 
	 *                discovered.
	 * 
	 * @return A list of the discovered remote XBee devices with the given 
	 *         identifier.
	 * 
	 * @throws IllegalArgumentException If {@code id.length() == 0} or
	 *                                  if {@code timeout == DISCOVERY_TIMEOUT_WAIT_FOREVER} or
	 *                                  if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws NullPointerException If {@code id == null}.
	 * @throws XBeeException If there is an error discovering the devices.
	 * 
	 * @see #getAllDevicesByID(String)
	 * @see #discoverDeviceByID(String)
	 * @see #discoverDeviceByID(String, long)
	 * @see RemoteXBeeDevice
	 */
	public List<RemoteXBeeDevice> discoverAllDevicesByID(String id, long timeout) throws XBeeException {
		if (id == null)
			throw new NullPointerException("Device identifier cannot be null.");
		if (id.length() == 0)
			throw new IllegalArgumentException("Device identifier cannot be an empty string.");
		if (timeout == DISCOVERY_TIMEOUT_WAIT_FOREVER)
			throw new IllegalArgumentException("The discovery devices process cannot block forever.");
		if (timeout < 0)
			throw new IllegalArgumentException("The timeout must be bigger than 0.");
		
		logger.debug("{}Discovering all '{}' devices ('{}' ms).", toString(), id, 
				timeout == DISCOVERY_TIMEOUT_DEVICE ? "configured NT" : timeout);
		
		return nodeDiscovery.discoverAllDevicesByID(id, timeout);
	}
	
	/**
	 * Discovers and reports all XBee devices found.
	 * 
	 * <p>This method blocks until the discover timeout configured in the 
	 * device ({@code NT}) expires.</p>
	 * 
	 * <p>The operation will use the network discovery options ({@code NO}) 
	 * configured in the XBee device.</p>
	 * 
	 * @return A list with the discovered XBee devices.
	 * 
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws XBeeException If there is an error sending the discovery command.
	 * 
	 * @see #getDevices()
	 * @see #discoverDevices(long)
	 * @see #discoverDevices(Set, long)
	 * @see RemoteXBeeDevice
	 */
	public List<RemoteXBeeDevice> discoverDevices() throws XBeeException {
		return discoverDevices((Set<DiscoveryOptions>)null, DISCOVERY_TIMEOUT_DEVICE);
	}
	
	/**
	 * Discovers and reports all XBee devices found.
	 * 
	 * <p>This method blocks until the provided timeout expires. If 
	 * {@code timeout == DISCOVERY_TIMEOUT_DEVICE}, the time configured 
	 * in the device will be used ({@code NT})</p>
	 * 
	 * <p>The operation will use the network discovery options ({@code NO}) 
	 * configured in the XBee device.</p>
	 * 
	 * @param timeout Time to wait for the discovery process to complete in 
	 *                milliseconds.
	 * 
	 * @return A list with the discovered XBee device.
	 * 
	 * @throws IllegalArgumentException If {@code timeout == DISCOVERY_TIMEOUT_WAIT_FOREVER} or
	 *                                  if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws XBeeException If there is an error sending the discovery command.
	 * 
	 * @see #getDevices()
	 * @see #discoverDevices()
	 * @see #discoverDevices(Set, long)
	 * @see #DISCOVERY_TIMEOUT_DEVICE
	 * @see RemoteXBeeDevice
	 */
	public List<RemoteXBeeDevice> discoverDevices(long timeout) throws XBeeException {
		return discoverDevices((Set<DiscoveryOptions>)null, timeout);
	}
	
	/**
	 * Discovers and reports all XBee devices found.
	 * 
	 * <p>This method blocks until the provided timeout expires. If 
	 * {@code timeout == DISCOVERY_TIMEOUT_DEVICE}, the time configured 
	 * in the device will be used ({@code NT}).</p>
	 * 
	 * @param options Collection of discovery options to use for the operation.
	 * @param timeout Time to wait for the discovery process to complete in 
	 *                milliseconds.
	 * 
	 * @return A list with the discovered XBee devices.
	 * 
	 * @throws IllegalArgumentException If {@code timeout == DISCOVERY_TIMEOUT_WAIT_FOREVER} or
	 *                                  if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws XBeeException If there is an error sending the discovery command.
	 * 
	 * @see #getDevices()
	 * @see #discoverDevices()
	 * @see #discoverDevices(long)
	 * @see #DISCOVERY_TIMEOUT_DEVICE
	 * @see DiscoveryOptions
	 * @see RemoteXBeeDevice
	 */
	public List<RemoteXBeeDevice> discoverDevices(Set<DiscoveryOptions> options, long timeout) throws XBeeException {
		if (timeout == DISCOVERY_TIMEOUT_WAIT_FOREVER)
			throw new IllegalArgumentException("The discovery process cannot block forever.");
		if (timeout < DISCOVERY_TIMEOUT_DEVICE)
			throw new IllegalArgumentException("The timeout must be bigger than 0.");
		
		logger.debug("{}Discovering devices from network (blocking for '{}' ms).", toString(), 
				timeout == DISCOVERY_TIMEOUT_DEVICE ? "configured NT" : timeout);
		
		return nodeDiscovery.discoverDevices(options, timeout);
	}
	
	/**
	 * Performs a discovery to search for XBee devices in the same network.
	 * 
	 * <p>The provided listener will be notified every time a new remote device 
	 * is discovered, when an error occurs, or when the operation finishes.</p>
	 * 
	 * <p>The operation finishes when the discover timeout configured in the 
	 * device ({@code NT}) expires.</p>
	 * 
	 * <p>The operation can be stopped at any time using the method 
	 * {@link #stopDiscoveryProcess()}.</p>
	 * 
	 * <p>The operation will use the network discovery options ({@code NO}) 
	 * configured in the XBee device.</p>
	 * 
	 * @param listener Discovery listener to be notified about process events.
	 * 
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws NullPointerException If {@code listener == null}.
	 * 
	 * @see IDiscoveryListener
	 * @see #stopDiscoveryProcess()
	 */
	public void discoverDevices(IDiscoveryListener listener) {
		discoverDevices(listener, null, DISCOVERY_TIMEOUT_DEVICE);
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
	 * <li>If {@code timeout == DISCOVERY_TIMEOUT_DEVICE}, the time 
	 * configured in the device will be used ({@code NT}).</li>
	 * <li>If {@code timeout == DISCOVERY_TIMEOUT_WAIT_FOREVER} the process will 
	 * never finish unless the {@link #stopDiscoveryProcess()} method is called.
	 * </li></ul>
	 * 
	 * <p>The operation can be stopped at any time using the method 
	 * {@link #stopDiscoveryProcess()}.</p>
	 * 
	 * <p>The operation will use the network discovery options ({@code NO}) 
	 * configured in the XBee device.</p>
	 * 
	 * @param listener Discovery listener to be notified about process events.
	 * @param timeout Time to wait for the discovery process to complete in 
	 *                milliseconds.
	 * 
	 * @throws IllegalArgumentException If {@code timeout < 0}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws NullPointerException If {@code listener == null}.
	 * 
	 * @see IDiscoveryListener
	 * @see #DISCOVERY_TIMEOUT_DEVICE
	 * @see #DISCOVERY_TIMEOUT_WAIT_FOREVER
	 * @see #stopDiscoveryProcess()
	 */
	public void discoverDevices(IDiscoveryListener listener, long timeout) {
		discoverDevices(listener, null, timeout);
	}
	
	/**
	 * Performs a discovery to search for XBee devices in the same network.
	 * 
	 * <p>The provided listener will be notified every time a new remote device 
	 * is discovered, when an error occur or when the operation finishes.</p>
	 * 
	 * <p>The operation finishes:</p>
	 * <ul>
	 * <li>When the provided timeout expires.</li>
	 * <li>If {@code timeout == DISCOVERY_TIMEOUT_DEVICE}, the time 
	 * configured in the device will be used ({@code NT}).</li>
	 * <li>If {@code timeout == DISCOVERY_TIMEOUT_WAIT_FOREVER} the process will 
	 * never finish unless the {@link #stopDiscoveryProcess()} method is called.
	 * </li></ul>
	 * 
	 * <p>The operation can be stopped at any time using the method 
	 * {@link #stopDiscoveryProcess()}.</p>
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
	 * @see IDiscoveryListener
	 * @see DiscoveryOptions
	 * @see #DISCOVERY_TIMEOUT_DEVICE
	 * @see #DISCOVERY_TIMEOUT_WAIT_FOREVER
	 * @see #stopDiscoveryProcess()
	 */
	public void discoverDevices(final IDiscoveryListener listener, Set<DiscoveryOptions> options, long timeout) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		if (timeout < DISCOVERY_TIMEOUT_WAIT_FOREVER)
			throw new IllegalArgumentException("The timeout must be bigger than 0.");
		
		if (logger.isDebugEnabled()) {
			String timeoutString = timeout + "";
			if (timeout == DISCOVERY_TIMEOUT_DEVICE)
				timeoutString = "configured NT";
			else if (timeout == DISCOVERY_TIMEOUT_WAIT_FOREVER)
				timeoutString = "forever";
			logger.debug("{}Discovering devices from network ('{}' ms).", toString(), timeoutString);
		}
		
		nodeDiscovery.discoverDevices(listener, options, timeout);
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
	 * {@link #getAllDevicesByID(String)}.</p>
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
	 * @see #getAllDevicesByID(String)
	 * @see #discoverDeviceByID(String)
	 * @see #discoverDeviceByID(String, long)
	 * @see #discoverAllDevicesByID(String, long)
	 * @see RemoteXBeeDevice
	 */
	public RemoteXBeeDevice getDeviceByID(String id) {
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
	 * {@link #getDeviceByID(String)}.</p>
	 * 
	 * @param id The identifier of the devices to be retrieved.
	 * 
	 * @return A list of the remote XBee devices contained in the network with 
	 *         the given identifier.
	 * 
	 * @throws NullPointerException If {@code id == null}.
	 * @throws IllegalArgumentException If {@code id.length() == 0}.
	 * 
	 * @see #getDeviceByID(String)
	 * @see #discoverAllDevicesByID(String, long)
	 * @see RemoteXBeeDevice
	 */
	public ArrayList<RemoteXBeeDevice> getAllDevicesByID(String id) {
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
	 * Retrieves whether or not the discovery process is running.
	 * 
	 * @return {@code true} if the discovery process is running, {@code false} 
	 *         otherwise.
	 */
	public boolean isDiscoveryRunning() {
		return nodeDiscovery.isRunning();
	}
	
	/**
	 * Stops the discovery process if it is running.
	 */
	public void stopDiscoveryProcess() {
		nodeDiscovery.stop();
	}
	
	/**
	 * Retrieves whether or not the discovery process has fully finished.
	 * 
	 * @return {@code true} if the process has fully finished, {@code false} 
	 *         otherwise.
	 */
	public boolean hasDiscoveryFinished() {
		return nodeDiscovery.hasFinished();
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
