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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.RemoteRaw802Device;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.InvalidPacketException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import com.digi.xbee.api.listeners.IModemStatusReceiveListener;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.ModemStatusEvent;
import com.digi.xbee.api.models.SpecialByte;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.models.XBeePacketsQueue;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.XBeePacketParser;
import com.digi.xbee.api.packet.common.IODataSampleRxIndicatorPacket;
import com.digi.xbee.api.packet.common.ModemStatusPacket;
import com.digi.xbee.api.packet.common.ReceivePacket;
import com.digi.xbee.api.packet.raw.RX16IOPacket;
import com.digi.xbee.api.packet.raw.RX16Packet;
import com.digi.xbee.api.packet.raw.RX64IOPacket;
import com.digi.xbee.api.packet.raw.RX64Packet;
import com.digi.xbee.api.utils.HexUtils;

/**
 * Thread that constantly reads data from an input stream.
 * 
 * <p>Depending on the XBee operating mode, read data is notified as is to the 
 * subscribed listeners or is parsed to a packet using the packet parser and 
 * then notified to subscribed listeners.</p> 
 */
public class DataReader extends Thread {
	
	// Constants.
	private final static int ALL_FRAME_IDS = 99999;
	private final static int MAXIMUM_PARALLEL_LISTENER_THREADS = 20;
	
	// Variables.
	private boolean running = false;
	
	private IConnectionInterface connectionInterface;
	
	private volatile OperatingMode mode;
	
	private ArrayList<IDataReceiveListener> dataReceiveListeners = new ArrayList<IDataReceiveListener>();
	// The packetReceiveListeners requires to be a HashMap with an associated integer. The integer is used to determine 
	// the frame ID of the packet that should be received. When it is 99999 (ALL_FRAME_IDS), all the packets will be handled.
	private HashMap<IPacketReceiveListener, Integer> packetReceiveListeners = new HashMap<IPacketReceiveListener, Integer>();
	private ArrayList<IIOSampleReceiveListener> ioSampleReceiveListeners = new ArrayList<IIOSampleReceiveListener>();
	private ArrayList<IModemStatusReceiveListener> modemStatusListeners = new ArrayList<IModemStatusReceiveListener>();
	
	private Logger logger;
	
	private XBeePacketParser parser;
	
	private XBeePacketsQueue xbeePacketsQueue;
	
	private XBeeDevice xbeeDevice;
	
	/**
	 * Class constructor. Instantiates a new {@code DataReader} object for the 
	 * given connection interface using the given XBee operating mode and XBee
	 * device.
	 * 
	 * @param connectionInterface Connection interface to read data from.
	 * @param mode XBee operating mode.
	 * @param xbeeDevice Reference to the XBee device containing this 
	 *                   {@code DataReader} object.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null} or
	 *                                 {@code mode == null}.
	 * 
	 * @see IConnectionInterface
	 * @see com.digi.xbee.api.XBeeDevice
	 * @see com.digi.xbee.api.models.OperatingMode
	 */
	public DataReader(IConnectionInterface connectionInterface, OperatingMode mode, XBeeDevice xbeeDevice) {
		if (connectionInterface == null)
			throw new NullPointerException("Connection interface cannot be null.");
		if (mode == null)
			throw new NullPointerException("Operating mode cannot be null.");
		
		this.connectionInterface = connectionInterface;
		this.mode = mode;
		this.xbeeDevice = xbeeDevice;
		this.logger = LoggerFactory.getLogger(DataReader.class);
		parser = new XBeePacketParser();
		xbeePacketsQueue = new XBeePacketsQueue();
	}
	
	/**
	 * Sets the XBee operating mode of this data reader.
	 * 
	 * @param mode New XBee operating mode.
	 * 
	 * @throws NullPointerException if {@code mode == null}.
	 * 
	 * @see com.digi.xbee.api.models.OperatingMode
	 */
	public void setXBeeReaderMode(OperatingMode mode) {
		if (mode == null)
			throw new NullPointerException("Operating mode cannot be null.");
		
		this.mode = mode;
	}
	
	/**
	 * Adds the given data receive listener to the list of listeners that will 
	 * be notified when XBee data packets are received.
	 * 
	 * <p>If the listener has been already added, this method does nothing.</p>
	 * 
	 * @param listener Listener to be notified when new XBee data packets are 
	 *                 received.
	 * 
	 * @see #removeDataReceiveListener(IDataReceiveListener)
	 * @see com.digi.xbee.api.listeners.IDataReceiveListener
	 */
	public void addDataReceiveListener(IDataReceiveListener listener) {
		synchronized (dataReceiveListeners) {
			if (!dataReceiveListeners.contains(listener))
				dataReceiveListeners.add(listener);
		}
	}
	
	/**
	 * Removes the given data receive listener from the list of data receive 
	 * listeners.
	 * 
	 * <p>If the listener is not included in the list, this method does nothing.
	 * </p>
	 * 
	 * @param listener Data receive listener to be remove from the list.
	 * 
	 * @see #addDataReceiveListener(IDataReceiveListener)
	 * @see com.digi.xbee.api.listeners.IDataReceiveListener
	 */
	public void removeDataReceiveListener(IDataReceiveListener listener) {
		synchronized (dataReceiveListeners) {
			if (dataReceiveListeners.contains(listener))
				dataReceiveListeners.remove(listener);
		}
	}
	
	/**
	 * Adds the given packet receive listener to the list of listeners that will
	 * be notified when any XBee packet is received.
	 * 
	 * <p>If the listener has been already added, this method does nothing.</p>
	 * 
	 * @param listener Listener to be notified when any XBee packet is received.
	 * 
	 * @see #addPacketReceiveListener(IPacketReceiveListener, int)
	 * @see #removePacketReceiveListener(IPacketReceiveListener)
	 * @see com.digi.xbee.api.listeners.IPacketReceiveListener
	 */
	public void addPacketReceiveListener(IPacketReceiveListener listener) {
		addPacketReceiveListener(listener, ALL_FRAME_IDS);
	}
	
	/**
	 * Adds the given packet receive listener to the list of listeners that will
	 * be notified when an XBee packet with the given frame ID is received.
	 * 
	 * <p>If the listener has been already added, this method does nothing.</p>
	 * 
	 * @param listener Listener to be notified when an XBee packet with the
	 *                 provided frame ID is received.
	 * @param frameID Frame ID for which this listener should be notified and 
	 *                removed after.
	 *                Using {@link #ALL_FRAME_IDS} this listener will be 
	 *                notified always and will be removed only by user request.
	 * 
	 * @see #addPacketReceiveListener(IPacketReceiveListener)
	 * @see #removePacketReceiveListener(IPacketReceiveListener)
	 * @see com.digi.xbee.api.listeners.IPacketReceiveListener
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
	 * <p>If the listener is not included in the list, this method does nothing.
	 * </p>
	 * 
	 * @param listener Packet receive listener to remove from the list.
	 * 
	 * @see #addPacketReceiveListener(IPacketReceiveListener)
	 * @see #addPacketReceiveListener(IPacketReceiveListener, int)
	 * @see com.digi.xbee.api.listeners.IPacketReceiveListener
	 */
	public void removePacketReceiveListener(IPacketReceiveListener listener) {
		synchronized (packetReceiveListeners) {
			if (packetReceiveListeners.containsKey(listener))
				packetReceiveListeners.remove(listener);
		}
	}
	
	/**
	 * Adds the given IO sample receive listener to the list of listeners that 
	 * will be notified when an IO sample packet is received.
	 * 
	 * <p>If the listener has been already added, this method does nothing.</p>
	 * 
	 * @param listener Listener to be notified when new IO sample packets are 
	 *                 received.
	 * 
	 * @see #removeIOSampleReceiveListener(IIOSampleReceiveListener)
	 * @see com.digi.xbee.api.listeners.IIOSampleReceiveListener
	 */
	public void addIOSampleReceiveListener(IIOSampleReceiveListener listener) {
		synchronized (ioSampleReceiveListeners) {
			if (!ioSampleReceiveListeners.contains(listener))
				ioSampleReceiveListeners.add(listener);
		}
	}
	
	/**
	 * Removes the given IO sample receive listener from the list of IO sample 
	 * receive listeners.
	 * 
	 * <p>If the listener is not included in the list, this method does nothing.
	 * </p>
	 * 
	 * @param listener IO sample receive listener to remove from the list.
	 * 
	 * @see #addIOSampleReceiveListener(IIOSampleReceiveListener)
	 * @see com.digi.xbee.api.listeners.IIOSampleReceiveListener
	 */
	public void removeIOSampleReceiveListener(IIOSampleReceiveListener listener) {
		synchronized (ioSampleReceiveListeners) {
			if (ioSampleReceiveListeners.contains(listener))
				ioSampleReceiveListeners.remove(listener);
		}
	}
	
	/**
	 * Adds the given Modem Status receive listener to the list of listeners 
	 * that will be notified when a modem status packet is received.
	 * 
	 * <p>If the listener has been already added, this method does nothing.</p>
	 * 
	 * @param listener Listener to be notified when new modem status packets are
	 *                 received.
	 * 
	 * @see #removeModemStatusReceiveListener(IModemStatusReceiveListener)
	 * @see com.digi.xbee.api.listeners.IModemStatusReceiveListener
	 */
	public void addModemStatusReceiveListener(IModemStatusReceiveListener listener) {
		synchronized (modemStatusListeners) {
			if (!modemStatusListeners.contains(listener))
				modemStatusListeners.add(listener);
		}
	}
	
	/**
	 * Removes the given Modem Status receive listener from the list of Modem 
	 * Status receive listeners.
	 * 
	 * <p>If the listener is not included in the list, this method does nothing.
	 * </p>
	 * 
	 * @param listener Modem Status receive listener to remove from the list.
	 * 
	 * @see #addModemStatusReceiveListener(IModemStatusReceiveListener)
	 * @see com.digi.xbee.api.listeners.IModemStatusReceiveListener
	 */
	public void removeModemStatusReceiveListener(IModemStatusReceiveListener listener) {
		synchronized (modemStatusListeners) {
			if (modemStatusListeners.contains(listener))
				modemStatusListeners.remove(listener);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		logger.debug(connectionInterface.toString() + "Data reader started.");
		running = true;
		// Clear the list of read packets.
		xbeePacketsQueue.clearQueue();
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
							try {
								XBeePacket packet = parser.parsePacket(connectionInterface.getInputStream(), mode);
								packetReceived(packet);
							} catch (InvalidPacketException e) {
								logger.error("Error parsing the API packet.", e);
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
			logger.error("Error reading from input stream.", e);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		} catch (IllegalStateException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (running) {
				running = false;
				if (connectionInterface.isOpen())
					connectionInterface.close();
			}
		}
	}
	
	/**
	 * Dispatches the received XBee packet to the corresponding listener(s).
	 * 
	 * @param packet The received XBee packet to be dispatched to the 
	 *               corresponding listeners.
	 * 
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	private void packetReceived(XBeePacket packet) {
		// Add the packet to the packets queue.
		xbeePacketsQueue.addPacket(packet);
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
		
		XBeeNetwork network = xbeeDevice.getNetwork();
		RemoteXBeeDevice remoteDevice = null;
		byte[] data = null;
		
		try {
			switch(apiType) {
			case RECEIVE_PACKET:
				ReceivePacket receivePacket = (ReceivePacket)apiPacket;
				remoteDevice = network.getDevice(receivePacket.get64bitSourceAddress());
				if (remoteDevice == null) {
					remoteDevice = new RemoteXBeeDevice(xbeeDevice, receivePacket.get64bitSourceAddress());
					network.addRemoteDevice(remoteDevice);
				}
				data = receivePacket.getRFData();
				notifyDataReceived(new XBeeMessage(remoteDevice, data, apiPacket.isBroadcast()));
				break;
			case RX_64:
				RX64Packet rx64Packet = (RX64Packet)apiPacket;
				remoteDevice = network.getDevice(rx64Packet.get64bitSourceAddress());
				if (remoteDevice == null) {
					remoteDevice = new RemoteXBeeDevice(xbeeDevice, rx64Packet.get64bitSourceAddress());
					network.addRemoteDevice(remoteDevice);
				}
				data = rx64Packet.getRFData();
				notifyDataReceived(new XBeeMessage(remoteDevice, data, apiPacket.isBroadcast()));
				break;
			case RX_16:
				RX16Packet rx16Packet = (RX16Packet)apiPacket;
				remoteDevice = network.getDevice(rx16Packet.get16bitSourceAddress());
				if (remoteDevice == null) {
					remoteDevice = new RemoteRaw802Device(xbeeDevice, rx16Packet.get16bitSourceAddress());
					network.addRemoteDevice(remoteDevice);
				}
				data = rx16Packet.getRFData();
				notifyDataReceived(new XBeeMessage(remoteDevice, data, apiPacket.isBroadcast()));
				break;
			case IO_DATA_SAMPLE_RX_INDICATOR:
				IODataSampleRxIndicatorPacket ioSamplePacket = (IODataSampleRxIndicatorPacket)apiPacket;
				remoteDevice = network.getDevice(ioSamplePacket.get64bitSourceAddress());
				if (remoteDevice == null) {
					remoteDevice = new RemoteXBeeDevice(xbeeDevice, ioSamplePacket.get64bitSourceAddress());
					network.addRemoteDevice(remoteDevice);
				}
				notifyIOSampleReceived(remoteDevice, ioSamplePacket.getIOSample());
				break;
			case RX_IO_64:
				RX64IOPacket rx64IOPacket = (RX64IOPacket)apiPacket;
				remoteDevice = network.getDevice(rx64IOPacket.get64bitSourceAddress());
				if (remoteDevice == null) {
					remoteDevice = new RemoteXBeeDevice(xbeeDevice, rx64IOPacket.get64bitSourceAddress());
					network.addRemoteDevice(remoteDevice);
				}
				notifyIOSampleReceived(remoteDevice, rx64IOPacket.getIOSample());
				break;
			case RX_IO_16:
				RX16IOPacket rx16IOPacket = (RX16IOPacket)apiPacket;
				remoteDevice = network.getDevice(rx16IOPacket.get16bitSourceAddress());
				if (remoteDevice == null) {
					remoteDevice = new RemoteRaw802Device(xbeeDevice, rx16IOPacket.get16bitSourceAddress());
					network.addRemoteDevice(remoteDevice);
				}
				notifyIOSampleReceived(remoteDevice, rx16IOPacket.getIOSample());
				break;
			case MODEM_STATUS:
				ModemStatusPacket modemStatusPacket = (ModemStatusPacket)apiPacket;
				notifyModemStatusReceived(modemStatusPacket.getStatus());
			default:
				break;
			}
		} catch (XBeeException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Notifies subscribed data receive listeners that a new XBee data packet 
	 * has been received in form of an {@code XBeeMessage}.
	 *
	 * @param xbeeMessage The XBee message to be sent to subscribed XBee data
	 *                    listeners.
	 * 
	 * @see com.digi.xbee.api.models.XBeeMessage
	 */
	private void notifyDataReceived(final XBeeMessage xbeeMessage) {
		if (xbeeMessage.isBroadcast())
			logger.info(connectionInterface.toString() + 
					"Broadcast data received from {} >> {}.", xbeeMessage.getDevice().get64BitAddress(), HexUtils.prettyHexString(xbeeMessage.getData()));
		else
			logger.info(connectionInterface.toString() + 
					"Data received from {} >> {}.", xbeeMessage.getDevice().get64BitAddress(), HexUtils.prettyHexString(xbeeMessage.getData()));
		
		try {
			synchronized (dataReceiveListeners) {
				ScheduledExecutorService executor = Executors.newScheduledThreadPool(Math.min(MAXIMUM_PARALLEL_LISTENER_THREADS, 
						dataReceiveListeners.size()));
				for (final IDataReceiveListener listener:dataReceiveListeners) {
					executor.execute(new Runnable() {
						/*
						 * (non-Javadoc)
						 * @see java.lang.Runnable#run()
						 */
						@Override
						public void run() {
							/* Synchronize the listener so it is not called 
							 twice. That is, let the listener to finish its job. */
							synchronized (listener) {
								listener.dataReceived(xbeeMessage);
							}
						}
					});
				}
				executor.shutdown();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Notifies subscribed XBee packet listeners that a new XBee packet has 
	 * been received.
	 *
	 * @param packet The received XBee packet.
	 * 
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	private void notifyPacketReceived(final XBeePacket packet) {
		logger.debug(connectionInterface.toString() + "Packet received: \n{}", packet.toPrettyString());
		
		try {
			synchronized (packetReceiveListeners) {
				final ArrayList<IPacketReceiveListener> removeListeners = new ArrayList<IPacketReceiveListener>();
				ScheduledExecutorService executor = Executors.newScheduledThreadPool(Math.min(MAXIMUM_PARALLEL_LISTENER_THREADS, 
						packetReceiveListeners.size()));
				for (final IPacketReceiveListener listener:packetReceiveListeners.keySet()) {
					executor.execute(new Runnable() {
						/*
						 * (non-Javadoc)
						 * @see java.lang.Runnable#run()
						 */
						@Override
						public void run() {
							// Synchronize the listener so it is not called 
							// twice. That is, let the listener to finish its job.
							synchronized (packetReceiveListeners) {
								synchronized (listener) {
									if (packetReceiveListeners.get(listener) == ALL_FRAME_IDS)
										listener.packetReceived(packet);
									else if (((XBeeAPIPacket)packet).needsAPIFrameID() && 
											((XBeeAPIPacket)packet).getFrameID() == packetReceiveListeners.get(listener)) {
										listener.packetReceived(packet);
										removeListeners.add(listener);
									}
								}
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
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Notifies subscribed IO sample listeners that a new IO sample packet has
	 * been received.
	 *
	 * @param ioSample The received IO sample.
	 * @param remoteDevice The remote XBee device that sent the sample.
	 * 
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 * @see com.digi.xbee.api.io.IOSample
	 */
	private void notifyIOSampleReceived(final RemoteXBeeDevice remoteDevice, final IOSample ioSample) {
		logger.debug(connectionInterface.toString() + "IO sample received.");
		
		try {
			synchronized (ioSampleReceiveListeners) {
				ScheduledExecutorService executor = Executors.newScheduledThreadPool(Math.min(MAXIMUM_PARALLEL_LISTENER_THREADS, 
						ioSampleReceiveListeners.size()));
				for (final IIOSampleReceiveListener listener:ioSampleReceiveListeners) {
					executor.execute(new Runnable() {
						/*
						 * (non-Javadoc)
						 * @see java.lang.Runnable#run()
						 */
						@Override
						public void run() {
							// Synchronize the listener so it is not called 
							// twice. That is, let the listener to finish its job.
							synchronized (listener) {
								listener.ioSampleReceived(remoteDevice, ioSample);
							}
						}
					});
				}
				executor.shutdown();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Notifies subscribed Modem Status listeners that a Modem Status event 
	 * packet has been received.
	 *
	 * @param modemStatusEvent The Modem Status event.
	 * 
	 * @see com.digi.xbee.api.models.ModemStatusEvent
	 */
	private void notifyModemStatusReceived(final ModemStatusEvent modemStatusEvent) {
		logger.debug(connectionInterface.toString() + "Modem Status event received.");
		
		try {
			synchronized (modemStatusListeners) {
				ScheduledExecutorService executor = Executors.newScheduledThreadPool(Math.min(MAXIMUM_PARALLEL_LISTENER_THREADS, 
						modemStatusListeners.size()));
				for (final IModemStatusReceiveListener listener:modemStatusListeners) {
					executor.execute(new Runnable() {
						/*
						 * (non-Javadoc)
						 * @see java.lang.Runnable#run()
						 */
						@Override
						public void run() {
							// Synchronize the listener so it is not called 
							// twice. That is, let the listener to finish its job.
							synchronized (listener) {
								listener.modemStatusEventReceived(modemStatusEvent);
							}
						}
					});
				}
				executor.shutdown();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Returns whether this Data reader is running or not.
	 * 
	 * @return {@code true} if the Data reader is running, {@code false} 
	 *         otherwise.
	 * 
	 * @see #stopReader()
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Stops the Data reader thread.
	 * 
	 * @see #isRunning()
	 */
	public void stopReader() {
		running = false;
		synchronized (connectionInterface) {
			connectionInterface.notify();
		}
		logger.debug(connectionInterface.toString() + "Data reader stopped.");
	}
	
	/**
	 * Returns the queue of read XBee packets.
	 * 
	 * @return The queue of read XBee packets.
	 * 
	 * @see com.digi.xbee.api.models.XBeePacketsQueue
	 */
	public XBeePacketsQueue getXBeePacketsQueue() {
		return xbeePacketsQueue;
	}
}
