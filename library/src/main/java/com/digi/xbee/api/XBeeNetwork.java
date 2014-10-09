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
	 * @see #discoverDeviceByID(String, long)
	 * @see #discoverAllDevicesByID(String, long)
	 * @see RemoteXBeeDevice
	 */
	public RemoteXBeeDevice discoverDeviceByID(String id) throws XBeeException {
		return discoverDeviceByID(id, NodeDiscovery.USE_DEVICE_TIMEOUT);
	}
	
	/**
	 * Discovers and reports the first remote XBee device that matches the 
	 * supplied identifier.
	 * 
	 * <p>This method blocks until the device is discovered or the provided 
	 * timeout expires.</p>
	 * 
	 * <p>If {@code timeout == NodeDiscovery.USE_DEVICE_TIMEOUT}, the time 
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
	 *                                  if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws NullPointerException If {@code id == null}.
	 * @throws XBeeException If there is an error discovering the device.
	 * 
	 * @see #discoverDeviceByID(String)
	 * @see #discoverAllDevicesByID(String, long)
	 * @see NodeDiscovery#USE_DEVICE_TIMEOUT
	 * @see RemoteXBeeDevice
	 */
	public RemoteXBeeDevice discoverDeviceByID(String id, long timeout) throws XBeeException {
		if (id == null)
			throw new NullPointerException("Device identifier cannot be null.");
		if (id.length() == 0)
			throw new IllegalArgumentException("Device identifier cannot be an empty string.");
		if (timeout == NodeDiscovery.WAIT_FOREVER)
			throw new IllegalArgumentException("The discovery devices process cannot block forever.");
		if (timeout < NodeDiscovery.USE_DEVICE_TIMEOUT)
			throw new IllegalArgumentException("The timeout must be bigger than 0.");
		
		logger.debug("{}Discovering '{}' device ('{}' ms).", toString(), id, 
				timeout == NodeDiscovery.USE_DEVICE_TIMEOUT ? "configured NT" : timeout);
		
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
	 *                                  if {@code timeout == NodeDiscovery.WAIT_FOREVER} or
	 *                                  if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws NullPointerException If {@code id == null}.
	 * @throws XBeeException If there is an error discovering the devices.
	 * 
	 * @see #discoverDeviceByID(String)
	 * @see #discoverDeviceByID(String, long)
	 * @see RemoteXBeeDevice
	 */
	public List<RemoteXBeeDevice> discoverAllDevicesByID(String id, long timeout) throws XBeeException {
		if (id == null)
			throw new NullPointerException("Device identifier cannot be null.");
		if (id.length() == 0)
			throw new IllegalArgumentException("Device identifier cannot be an empty string.");
		if (timeout == NodeDiscovery.WAIT_FOREVER)
			throw new IllegalArgumentException("The discovery devices process cannot block forever.");
		if (timeout < 0)
			throw new IllegalArgumentException("The timeout must be bigger than 0.");
		
		logger.debug("{}Discovering all '{}' devices ('{}' ms).", toString(), id, 
				timeout == NodeDiscovery.USE_DEVICE_TIMEOUT ? "configured NT" : timeout);
		
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
	 * @see #discoverDevices(long)
	 * @see #discoverDevices(Set, long)
	 * @see RemoteXBeeDevice
	 */
	public List<RemoteXBeeDevice> discoverDevices() throws XBeeException {
		return discoverDevices((Set<DiscoveryOptions>)null, NodeDiscovery.USE_DEVICE_TIMEOUT);
	}
	
	/**
	 * Discovers and reports all XBee devices found.
	 * 
	 * <p>This method blocks until the provided timeout expires. If 
	 * {@code timeout == NodeDiscovery.USE_DEVICE_TIMEOUT}, the time configured 
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
	 * @throws IllegalArgumentException If {@code timeout == NodeDiscovery.WAIT_FOREVER} or
	 *                                  if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws XBeeException If there is an error sending the discovery command.
	 * 
	 * @see #discoverDevices()
	 * @see #discoverDevices(Set, long)
	 * @see NodeDiscovery#USE_DEVICE_TIMEOUT
	 * @see RemoteXBeeDevice
	 */
	public List<RemoteXBeeDevice> discoverDevices(long timeout) throws XBeeException {
		return discoverDevices((Set<DiscoveryOptions>)null, timeout);
	}
	
	/**
	 * Discovers and reports all XBee devices found.
	 * 
	 * <p>This method blocks until the provided timeout expires. If 
	 * {@code timeout == NodeDiscovery.USE_DEVICE_TIMEOUT}, the time configured 
	 * in the device will be used ({@code NT}).</p>
	 * 
	 * @param options Collection of discovery options to use for the operation.
	 * @param timeout Time to wait for the discovery process to complete in 
	 *                milliseconds.
	 * 
	 * @return A list with the discovered XBee devices.
	 * 
	 * @throws IllegalArgumentException If {@code timeout == NodeDiscovery.WAIT_FOREVER} or
	 *                                  if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException If the device is not open.
	 * @throws XBeeException If there is an error sending the discovery command.
	 * 
	 * @see #discoverDevices()
	 * @see #discoverDevices(long)
	 * @see NodeDiscovery#USE_DEVICE_TIMEOUT
	 * @see DiscoveryOptions
	 * @see RemoteXBeeDevice
	 */
	public List<RemoteXBeeDevice> discoverDevices(Set<DiscoveryOptions> options, long timeout) throws XBeeException {
		if (timeout == NodeDiscovery.WAIT_FOREVER)
			throw new IllegalArgumentException("The discovery process cannot block forever.");
		if (timeout < NodeDiscovery.USE_DEVICE_TIMEOUT)
			throw new IllegalArgumentException("The timeout must be bigger than 0.");
		
		logger.debug("{}Discovering devices from network (blocking for '{}' ms).", toString(), 
				timeout == NodeDiscovery.USE_DEVICE_TIMEOUT ? "configured NT" : timeout);
		
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
	 * {@link #stop()}.</p>
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
	 * @see #stop()
	 */
	public void discoverDevices(IDiscoveryListener listener) {
		discoverDevices(listener, null, NodeDiscovery.USE_DEVICE_TIMEOUT);
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
	 * <li>If {@code timeout == NodeDiscovery.USE_DEVICE_TIMEOUT}, the time 
	 * configured in the device will be used ({@code NT}).</li>
	 * <li>If {@code timeout == NodeDiscovery.WAIT_FOREVER} the process will 
	 * never finish unless the {@link #stop()} method is called.</li>
	 * </ul>
	 * 
	 * <p>The operation can be stopped at any time using the method 
	 * {@link #stop()}.</p>
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
	 * @see NodeDiscovery#USE_DEVICE_TIMEOUT
	 * @see NodeDiscovery#WAIT_FOREVER
	 * @see #stop()
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
	 * <li>If {@code timeout == NodeDiscovery.USE_DEVICE_TIMEOUT}, the time 
	 * configured in the device will be used ({@code NT}).</li>
	 * <li>If {@code timeout == NodeDiscovery.WAIT_FOREVER} the process will 
	 * never finish unless the {@link #stop()} method is called.</li>
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
	 * @see IDiscoveryListener
	 * @see DiscoveryOptions
	 * @see NodeDiscovery#USE_DEVICE_TIMEOUT
	 * @see NodeDiscovery#WAIT_FOREVER
	 * @see #stop()
	 */
	public void discoverDevices(final IDiscoveryListener listener, Set<DiscoveryOptions> options, long timeout) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		if (timeout < NodeDiscovery.WAIT_FOREVER)
			throw new IllegalArgumentException("The timeout must be bigger than 0.");
		
		if (logger.isDebugEnabled()) {
			String timeoutString = timeout + "";
			if (timeout == NodeDiscovery.USE_DEVICE_TIMEOUT)
				timeoutString = "configured NT";
			else if (timeout == NodeDiscovery.WAIT_FOREVER)
				timeoutString = "forever";
			logger.debug("{}Discovering devices from network ('{}' ms).", toString(), timeoutString);
		}
		
		nodeDiscovery.discoverDevices(listener, options, timeout);
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
		
		// Look in the 64-bit map.
		XBee64BitAddress addr64 = remoteDevice.get64BitAddress();
		if (addr64 != null && !addr64.equals(XBee64BitAddress.UNKNOWN_ADDRESS)) {
			devInNetwork = remotesBy64BitAddr.get(addr64);
			
			// Update the reference.
			if (devInNetwork != null) {
				logger.debug("{}Existing device '{}' in network.", toString(), devInNetwork.toString());
				devInNetwork.updateDeviceDataFrom(remoteDevice);
				return devInNetwork;
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
					devInNetwork = d;
					break;
				}
			}
			
			// If not found, look for the 16-bit address in the 16-bit map. 
			if (devInNetwork == null) {
				devInNetwork = remotesBy16BitAddr.get(addr16);
				
				// If the device to be added has a 64-bit address, remove it
				// from the 16-bit map and add it to the 64-bit map.
				if (devInNetwork != null 
						&& addr64 != null && !addr64.equals(XBee64BitAddress.UNKNOWN_ADDRESS)) {
					devInNetwork = remotesBy16BitAddr.remove(addr16);
					
					RemoteXBeeDevice devIn64bitMap = remotesBy64BitAddr.get(addr64);
					if (devIn64bitMap != null)
						devInNetwork = devIn64bitMap;
					else
						remotesBy64BitAddr.put(addr64, devInNetwork);
				}
			}
			
			// If found, update the reference.
			if (devInNetwork != null) {
				logger.debug("{}Existing device '{}' in network.", toString(), devInNetwork.toString());
				
				devInNetwork.updateDeviceDataFrom(remoteDevice);
				return devInNetwork;
			}
		}
		
		// If the device does not contain a valid address return null.
		if ((addr64 == null || addr64.equals(XBee64BitAddress.UNKNOWN_ADDRESS)) 
				&& (addr16 == null || addr16.equals(XBee16BitAddress.UNKNOWN_ADDRESS))) {
			logger.error("{}Remote device '{}' cannot be added: 64-bit and 16-bit addresses must be specified.", toString(), remoteDevice.toString());
			return null;
		}
		
		// Add it to the right map.
		if (addr64 != null && !addr64.equals(XBee64BitAddress.UNKNOWN_ADDRESS))
			remotesBy64BitAddr.put(addr64, remoteDevice);
		else
			remotesBy16BitAddr.put(addr16, remoteDevice);
		
		return remoteDevice;
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
