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
package com.digi.xbee.api.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.digi.xbee.api.exceptions.PacketParsingException;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.listeners.ISerialDataReceiveListener;
import com.digi.xbee.api.models.SpecialByte;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.XBeePacketParser;
import com.digi.xbee.api.packet.common.ReceivePacket;
import com.digi.xbee.api.packet.raw.RX16Packet;
import com.digi.xbee.api.packet.raw.RX64Packet;

/**
 * Thread that constantly reads data from the input stream.
 * 
 * Depending on the working mode, read data is notified as is
 * to the subscribed listeners or is parsed to a packet using the
 * packet parser and then notified to subscribed listeners. 
 */
public class DataReader extends Thread {
	
	// Constants
	private final static int ALL_FRAME_IDS = 99999;
	private final static int MAXIMUM_PARALLEL_LISTENER_THREADS = 20;
	
	// Variables
	private boolean running = false;
	
	private IConnectionInterface connectionInterface;
	
	private volatile OperatingMode mode;
	
	private ArrayList<ISerialDataReceiveListener> serialDataReceiveListeners = new ArrayList<ISerialDataReceiveListener>();
	// The packetReceiveListeners requires to be a HashMap with an associated integer. The integer is used to determine 
	// the frame ID of the packet that should be received. When it is 99999 (ALL_FRAME_IDS), all the packets will be handled.
	private HashMap<IPacketReceiveListener, Integer> packetReceiveListeners = new HashMap<IPacketReceiveListener, Integer>();
	
	/**
	 * Class constructor. Instances a new DataReader object for the given interface.
	 * 
	 * @param connectionInterface Connection interface to read from.
	 * @param mode XBee operating mode.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null} or
	 *                                 {@code mode == null}.
	 */
	public DataReader(IConnectionInterface connectionInterface, OperatingMode mode) {
		if (connectionInterface == null)
			throw new NullPointerException("Connection interface cannot be null.");
		if (mode == null)
			throw new NullPointerException("Operating mode cannot be null.");
		
		this.connectionInterface = connectionInterface;
		this.mode = mode;
	}
	
	/**
	 * Sets the mode of the reader.
	 * 
	 * @param mode XBee mode
	 * 
	 * @throws NullPointerException if {@code mode == null}.
	 */
	public void setXBeeReaderMode(OperatingMode mode) {
		if (mode == null)
			throw new NullPointerException("Operating mode cannot be null.");
		
		this.mode = mode;
	}
	
	/**
	 * Adds the given serial data receive listener to the list of listeners to be notified when 
	 * serial data is received.
	 * 
	 * @param listener Listener to be notified when new serial data is received.
	 */
	public void addSerialDatatReceiveListener(ISerialDataReceiveListener listener) {
		synchronized (serialDataReceiveListeners) {
			if (!serialDataReceiveListeners.contains(listener))
				serialDataReceiveListeners.add(listener);
		}
	}
	
	/**
	 * Removes the given serial data receive listener from the list of serial data 
	 * receive listeners.
	 * 
	 * @param listener Serial data receive listener to remove.
	 */
	public void removeSerialDataReceiveListener(ISerialDataReceiveListener listener) {
		synchronized (serialDataReceiveListeners) {
			if (serialDataReceiveListeners.contains(listener))
				serialDataReceiveListeners.remove(listener);
		}
	}
	
	/**
	 * Adds the given packet receive listener to the list of listeners to be notified when a 
	 * packet is received.
	 * 
	 * @param listener Listener to be notified when a packet is received.
	 */
	public void addPacketReceiveListener(IPacketReceiveListener listener) {
		addPacketReceiveListener(listener, ALL_FRAME_IDS);
	}
	
	/**
	 * Adds the given packet receive listener to the list of listeners to be notified when a 
	 * packet is received.
	 * 
	 * @param listener Listener to be notified when a packet is received.
	 * @param frameID Frame ID for which this listener should be notified and removed after.
	 * 					Using {@link #ALL_FRAME_IDS} this listener will be notified always and
	 * 					will be removed only by user request.
	 */
	public void addPacketReceiveListener(IPacketReceiveListener listener, int frameID) {
		synchronized (packetReceiveListeners) {
			if (!packetReceiveListeners.containsKey(listener))
				packetReceiveListeners.put(listener, frameID);
		}
	}
	
	
	/**
	 * Removes the given packet receive listener from the list of XBee packet 
	 * receive listeners.
	 * 
	 * @param listener Packet receive listener to remove.
	 */
	public void removePacketReceiveListener(IPacketReceiveListener listener) {
		synchronized (packetReceiveListeners) {
			if (packetReceiveListeners.containsKey(listener))
				packetReceiveListeners.remove(listener);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		running = true;
		try {
			synchronized (connectionInterface) {
				connectionInterface.wait();
			}
			while (running) {
				if (!running)
					break;
				if (connectionInterface.getInputStream() != null) {
					switch (mode) {
					case AT:
						break;
					case API:
					case API_ESCAPE:
						int headerByte = connectionInterface.getInputStream().read();
						// If it is packet header parse the packet, if not discard this byte and continue.
						if (headerByte == SpecialByte.HEADER_BYTE.getValue()) {
							XBeePacketParser parser = new XBeePacketParser(connectionInterface.getInputStream(), mode);
							try {
								XBeePacket packet = parser.parsePacket();
								packetReceived(packet);
							} catch (PacketParsingException e) {
								e.printStackTrace();
							}
						}
						break;
					default:
						break;
					}
				} else if (connectionInterface.getInputStream() == null)
					break;
				if (connectionInterface.getInputStream() == null)
					break;
				else if (connectionInterface.getInputStream().available() > 0)
					continue;
				synchronized (connectionInterface) {
					connectionInterface.wait();
				}
			}
		} catch (IOException e) {
			// TODO: We may receive error while reading from input stream and port was closed.
			//e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Always exception when close the port
			//e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Always exception when close the port
			//e.printStackTrace();
		} finally {
			if (running) {
				running = false;
				if (connectionInterface != null && connectionInterface.isOpen())
					connectionInterface.close();
			}
		}
	}
	
	/**
	 * A packet was received, dispatch it to the corresponding listener(s).
	 * 
	 * @param packet The received packet.
	 */
	private void packetReceived(XBeePacket packet) {
		// Notify that a packet has been received to the corresponding listeners.
		notifyPacketReceived(packet);
		
		// Check if the packet is an API packet.
		if (!(packet instanceof XBeeAPIPacket))
			return;
		
		// Get the API packet type.
		XBeeAPIPacket apiPacket = (XBeeAPIPacket)packet;
		APIFrameType apiType = apiPacket.getFrameType();
		if (apiType == null)
			return;
		
		String address = null;
		byte[] data = null;
		
		switch(apiType) {
		case RECEIVE_PACKET:
			address = ((ReceivePacket)apiPacket).get64bitAddress().toString();
			data = ((ReceivePacket)apiPacket).getReceivedData();
			break;
		case RX_64:
			address = ((RX64Packet)apiPacket).getSourceAddress().toString();
			data = ((RX64Packet)apiPacket).getReceivedData();
			break;
		case RX_16:
			address = ((RX16Packet)apiPacket).getSourceAddress().toString();
			data = ((RX16Packet)apiPacket).getReceivedData();
			break;
		default:
			break;
		}
		// Notify that serial data was received to the corresponding listeners.
		if (address != null && data != null)
			notifySerialDataReceived(address, data);
	}
	
	/**
	 * Notifies subscribed serial data receive listeners that serial data has been received.
	 *
	 * @param address The address of the node that sent the data.
	 * @param data The received data.
	 */
	private void notifySerialDataReceived(final String address, final byte[] data) {
		try {
			synchronized (serialDataReceiveListeners) {
				ScheduledExecutorService executor = Executors.newScheduledThreadPool(Math.min(MAXIMUM_PARALLEL_LISTENER_THREADS, packetReceiveListeners.size()));
				for (final ISerialDataReceiveListener listener:serialDataReceiveListeners) {
					executor.execute(new Runnable() {
						public void run() {
							listener.serialDataReceived(address, data);
						}
					});
				}
				executor.shutdown();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Notifies subscribed packet listeners that a packet has been received.
	 *
	 * @param packet The received packet.
	 */
	private void notifyPacketReceived(final XBeePacket packet) {
		try {
			synchronized (packetReceiveListeners) {
				final ArrayList<IPacketReceiveListener> removeListeners = new ArrayList<IPacketReceiveListener>();
				ScheduledExecutorService executor = Executors.newScheduledThreadPool(Math.min(MAXIMUM_PARALLEL_LISTENER_THREADS, packetReceiveListeners.size()));
				for (final IPacketReceiveListener listener:packetReceiveListeners.keySet()) {
					executor.execute(new Runnable() {
						public void run() {
							if (packetReceiveListeners.get(listener) == ALL_FRAME_IDS)
								listener.packetReceived(packet);
							else if (((XBeeAPIPacket)packet).needsAPIFrameID() && ((XBeeAPIPacket)packet).getFrameID() == packetReceiveListeners.get(listener)) {
								listener.packetReceived(packet);
								removeListeners.add(listener);
							}
						}
					});
				}
				executor.shutdown();
				// Remove required listeners.
				for (IPacketReceiveListener listener:removeListeners)
					packetReceiveListeners.remove(listener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns whether the Data reader is running or not.
	 * 
	 * @return True if the Data reader is running, false otherwise.
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Stops the Data reader thread.
	 */
	public void stopReader() {
		running = false;
		synchronized (connectionInterface) {
			connectionInterface.notify();
		}
	}
}
