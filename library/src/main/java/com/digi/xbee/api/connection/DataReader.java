/**
 * Copyright (c) 2014-2016 Digi International Inc.,
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

import com.digi.xbee.api.RemoteDigiMeshDevice;
import com.digi.xbee.api.RemoteDigiPointDevice;
import com.digi.xbee.api.RemoteRaw802Device;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.RemoteZigBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.InvalidPacketException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.listeners.IExplicitDataReceiveListener;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import com.digi.xbee.api.listeners.IModemStatusReceiveListener;
import com.digi.xbee.api.listeners.INetworkDataReceiveListener;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.listeners.ISMSReceiveListener;
import com.digi.xbee.api.models.ExplicitXBeeMessage;
import com.digi.xbee.api.models.ModemStatusEvent;
import com.digi.xbee.api.models.NetworkMessage;
import com.digi.xbee.api.models.SMSMessage;
import com.digi.xbee.api.models.SpecialByte;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.models.XBeePacketsQueue;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.XBeePacketParser;
import com.digi.xbee.api.packet.cellular.RXSMSPacket;
import com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket;
import com.digi.xbee.api.packet.common.IODataSampleRxIndicatorPacket;
import com.digi.xbee.api.packet.common.ModemStatusPacket;
import com.digi.xbee.api.packet.common.ReceivePacket;
import com.digi.xbee.api.packet.network.RXIPv4Packet;
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
	private ArrayList<IExplicitDataReceiveListener> explicitDataReceiveListeners = new ArrayList<IExplicitDataReceiveListener>();
	private ArrayList<INetworkDataReceiveListener> networkDataReceiveListeners = new ArrayList<INetworkDataReceiveListener>();
	private ArrayList<ISMSReceiveListener> smsReceiveListeners = new ArrayList<ISMSReceiveListener>();
	
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
	 * @throws NullPointerException if {@code listener == null}.
	 * 
	 * @see #removeDataReceiveListener(IDataReceiveListener)
	 * @see com.digi.xbee.api.listeners.IDataReceiveListener
	 */
	public void addDataReceiveListener(IDataReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
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
	 * @throws NullPointerException if {@code listener == null}.
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
	 * @throws NullPointerException if {@code listener == null}.
	 * 
	 * @see #addPacketReceiveListener(IPacketReceiveListener)
	 * @see #removePacketReceiveListener(IPacketReceiveListener)
	 * @see com.digi.xbee.api.listeners.IPacketReceiveListener
	 */
	public void addPacketReceiveListener(IPacketReceiveListener listener, int frameID) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
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
	 * @throws NullPointerException if {@code listener == null}.
	 * 
	 * @see #removeIOSampleReceiveListener(IIOSampleReceiveListener)
	 * @see com.digi.xbee.api.listeners.IIOSampleReceiveListener
	 */
	public void addIOSampleReceiveListener(IIOSampleReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
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
	 * @throws NullPointerException if {@code listener == null}.
	 * 
	 * @see #removeModemStatusReceiveListener(IModemStatusReceiveListener)
	 * @see com.digi.xbee.api.listeners.IModemStatusReceiveListener
	 */
	public void addModemStatusReceiveListener(IModemStatusReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
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
	
	/**
	 * Adds the given explicit data receive listener to the list of listeners 
	 * that will be notified when an explicit data packet is received.
	 * 
	 * <p>If the listener has been already added, this method does nothing.</p>
	 * 
	 * @param listener Listener to be notified when new explicit data packets 
	 *                 are received.
	 * 
	 * @throws NullPointerException if {@code listener == null}.
	 * 
	 * @see #removeExplicitDataReceiveListener(IExplicitDataReceiveListener)
	 * @see com.digi.xbee.api.listeners.IExplicitDataReceiveListener
	 */
	public void addExplicitDataReceiveListener(IExplicitDataReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		synchronized (explicitDataReceiveListeners) {
			if (!explicitDataReceiveListeners.contains(listener))
				explicitDataReceiveListeners.add(listener);
		}
	}
	
	/**
	 * Removes the given explicit data receive listener from the list of 
	 * explicit data receive listeners.
	 * 
	 * <p>If the listener is not included in the list, this method does nothing.
	 * </p>
	 * 
	 * @param listener Explicit data receive listener to remove from the list.
	 * 
	 * @see #addExplicitDataReceiveListener(IExplicitDataReceiveListener)
	 * @see com.digi.xbee.api.listeners.IExplicitDataReceiveListener
	 */
	public void removeExplicitDataReceiveListener(IExplicitDataReceiveListener listener) {
		synchronized (explicitDataReceiveListeners) {
			if (explicitDataReceiveListeners.contains(listener))
				explicitDataReceiveListeners.remove(listener);
		}
	}
	
	/**
	 * Adds the given network data receive listener to the list of listeners 
	 * that will be notified when a network data packet is received.
	 * 
	 * <p>If the listener has been already added, this method does nothing.</p>
	 * 
	 * @param listener Listener to be notified when new network data packets 
	 *                 are received.
	 * 
	 * @throws NullPointerException if {@code listener == null}.
	 * 
	 * @see #removeNetworkDataReceiveListener(INetworkDataReceiveListener)
	 * @see com.digi.xbee.api.listeners.INetworkDataReceiveListener
	 */
	public void addNetworkDataReceiveListener(INetworkDataReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		synchronized (networkDataReceiveListeners) {
			if (!networkDataReceiveListeners.contains(listener))
				networkDataReceiveListeners.add(listener);
		}
	}
	
	/**
	 * Removes the given network data receive listener from the list of 
	 * network data receive listeners.
	 * 
	 * <p>If the listener is not included in the list, this method does nothing.
	 * </p>
	 * 
	 * @param listener Network data receive listener to remove from the list.
	 * 
	 * @see #addNetworkDataReceiveListener(INetworkDataReceiveListener)
	 * @see com.digi.xbee.api.listeners.INetworkDataReceiveListener
	 */
	public void removeNetworkDataReceiveListener(INetworkDataReceiveListener listener) {
		synchronized (networkDataReceiveListeners) {
			if (networkDataReceiveListeners.contains(listener))
				networkDataReceiveListeners.remove(listener);
		}
	}
	
	/**
	 * Adds the given SMS receive listener to the list of listeners that will 
	 * be notified when an SMS packet is received.
	 * 
	 * <p>If the listener has been already added, this method does nothing.</p>
	 * 
	 * @param listener Listener to be notified when new SMS packet is received.
	 * 
	 * @throws NullPointerException if {@code listener == null}.
	 * 
	 * @see #removeSMSReceiveListener(ISMSReceiveListener)
	 * @see com.digi.xbee.api.listeners.ISMSReceiveListener
	 */
	public void addSMSReceiveListener(ISMSReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		
		synchronized (smsReceiveListeners) {
			if (!smsReceiveListeners.contains(listener))
				smsReceiveListeners.add(listener);
		}
	}
	
	/**
	 * Removes the given SMS receive listener from the list of SMS receive 
	 * listeners.
	 * 
	 * <p>If the listener is not included in the list, this method does nothing.
	 * </p>
	 * 
	 * @param listener SMS receive listener to remove from the list.
	 * 
	 * @see #addSMSReceiveListener(ISMSReceiveListener)
	 * @see com.digi.xbee.api.listeners.ISMSReceiveListener
	 */
	public void removeSMSReceiveListener(ISMSReceiveListener listener) {
		synchronized (smsReceiveListeners) {
			if (smsReceiveListeners.contains(listener))
				smsReceiveListeners.remove(listener);
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
		
		try {
			// Obtain the remote device from the packet.
			RemoteXBeeDevice remoteDevice = getRemoteXBeeDeviceFromPacket(apiPacket);
			byte[] data = null;
			
			switch(apiType) {
			case RECEIVE_PACKET:
				ReceivePacket receivePacket = (ReceivePacket)apiPacket;
				data = receivePacket.getRFData();
				notifyDataReceived(new XBeeMessage(remoteDevice, data, apiPacket.isBroadcast()));
				break;
			case RX_64:
				RX64Packet rx64Packet = (RX64Packet)apiPacket;
				data = rx64Packet.getRFData();
				notifyDataReceived(new XBeeMessage(remoteDevice, data, apiPacket.isBroadcast()));
				break;
			case RX_16:
				RX16Packet rx16Packet = (RX16Packet)apiPacket;
				data = rx16Packet.getRFData();
				notifyDataReceived(new XBeeMessage(remoteDevice, data, apiPacket.isBroadcast()));
				break;
			case IO_DATA_SAMPLE_RX_INDICATOR:
				IODataSampleRxIndicatorPacket ioSamplePacket = (IODataSampleRxIndicatorPacket)apiPacket;
				notifyIOSampleReceived(remoteDevice, ioSamplePacket.getIOSample());
				break;
			case RX_IO_64:
				RX64IOPacket rx64IOPacket = (RX64IOPacket)apiPacket;
				notifyIOSampleReceived(remoteDevice, rx64IOPacket.getIOSample());
				break;
			case RX_IO_16:
				RX16IOPacket rx16IOPacket = (RX16IOPacket)apiPacket;
				notifyIOSampleReceived(remoteDevice, rx16IOPacket.getIOSample());
				break;
			case MODEM_STATUS:
				ModemStatusPacket modemStatusPacket = (ModemStatusPacket)apiPacket;
				notifyModemStatusReceived(modemStatusPacket.getStatus());
				break;
			case EXPLICIT_RX_INDICATOR:
				ExplicitRxIndicatorPacket explicitDataPacket = (ExplicitRxIndicatorPacket)apiPacket;
				int sourceEndpoint = explicitDataPacket.getSourceEndpoint();
				int destEndpoint = explicitDataPacket.getDestinationEndpoint();
				int clusterID = explicitDataPacket.getClusterID();
				int profileID = explicitDataPacket.getProfileID();
				data = explicitDataPacket.getRFData();
				// If this is an explicit packet for data transmissions in the Digi profile, 
				// notify also the data listener and add a Receive packet to the queue.
				if (sourceEndpoint == ExplicitRxIndicatorPacket.DATA_ENDPOINT && 
						destEndpoint == ExplicitRxIndicatorPacket.DATA_ENDPOINT &&
						clusterID == ExplicitRxIndicatorPacket.DATA_CLUSTER && 
						profileID == ExplicitRxIndicatorPacket.DIGI_PROFILE) {
					notifyDataReceived(new XBeeMessage(remoteDevice, data, apiPacket.isBroadcast()));
					xbeePacketsQueue.addPacket(new ReceivePacket(explicitDataPacket.get64BitSourceAddress(), 
							explicitDataPacket.get16BitSourceAddress(), 
							explicitDataPacket.getReceiveOptions(), 
							explicitDataPacket.getRFData()));
				}
				notifyExplicitDataReceived(new ExplicitXBeeMessage(remoteDevice, sourceEndpoint, destEndpoint, clusterID, profileID, data, explicitDataPacket.isBroadcast()));
				break;
			case RX_IPV4:
				RXIPv4Packet rxIPv4Packet = (RXIPv4Packet)apiPacket;
				notifyNetworkDataReceived(new NetworkMessage(
						rxIPv4Packet.getDestAddress(), 
						rxIPv4Packet.getSourcePort(), 
						rxIPv4Packet.getDestPort(),
						rxIPv4Packet.getProtocol(),
						rxIPv4Packet.getData(),
						apiPacket.isBroadcast()));
				break;
			case RX_SMS:
				RXSMSPacket rxSMSPacket = (RXSMSPacket)apiPacket;
				notifySMSReceived(new SMSMessage(rxSMSPacket.getPhoneNumber(), rxSMSPacket.getData()));
				break;
			default:
				break;
			}
			
		} catch (XBeeException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Returns the remote XBee device from where the given package was sent 
	 * from.
	 * 
	 * <p><b>This is for internal use only.</b></p>
	 * 
	 * <p>If the package does not contain information about the source, this 
	 * method returns {@code null} (for example, {@code ModemStatusPacket}).</p>
	 * 
	 * <p>First the device that sent the provided package is looked in the 
	 * network of the local XBee device. If the remote device is not in the 
	 * network, it is automatically added only if the packet contains 
	 * information about the origin of the package.</p>
	 * 
	 * @param packet The packet sent from the remote device.
	 * 
	 * @return The remote XBee device that sends the given packet. It may be 
	 *         {@code null} if the packet is not a known frame (see 
	 *         {@link APIFrameType}) or if it does not contain information of 
	 *         the source device.
	 * 
	 * @throws NullPointerException if {@code packet == null}
	 * @throws XBeeException if any error occur while adding the device to the 
	 *                       network.
	 */
	public RemoteXBeeDevice getRemoteXBeeDeviceFromPacket(XBeeAPIPacket packet) throws XBeeException {
		if (packet == null)
			throw new NullPointerException("XBee API packet cannot be null.");
			
		XBeeAPIPacket apiPacket = (XBeeAPIPacket)packet;
		APIFrameType apiType = apiPacket.getFrameType();
		if (apiType == null || apiType == APIFrameType.UNKNOWN)
			return null;
		
		RemoteXBeeDevice remoteDevice = null;
		XBee64BitAddress addr64 = null;
		XBee16BitAddress addr16 = null;
		
		XBeeNetwork network = xbeeDevice.getNetwork();
		// There are protocols that do not support the network feature.
		if (network == null)
			return null;
		
		switch(apiType) {
		case RECEIVE_PACKET:
			ReceivePacket receivePacket = (ReceivePacket)apiPacket;
			addr64 = receivePacket.get64bitSourceAddress();
			addr16 = receivePacket.get16bitSourceAddress();
			remoteDevice = network.getDevice(addr64);
			break;
		case RX_64:
			RX64Packet rx64Packet = (RX64Packet)apiPacket;
			addr64 = rx64Packet.get64bitSourceAddress();
			remoteDevice = network.getDevice(addr64);
			break;
		case RX_16:
			RX16Packet rx16Packet = (RX16Packet)apiPacket;
			addr64 = XBee64BitAddress.UNKNOWN_ADDRESS;
			addr16 = rx16Packet.get16bitSourceAddress();
			remoteDevice = network.getDevice(addr16);
			break;
		case IO_DATA_SAMPLE_RX_INDICATOR:
			IODataSampleRxIndicatorPacket ioSamplePacket = (IODataSampleRxIndicatorPacket)apiPacket;
			addr64 = ioSamplePacket.get64bitSourceAddress();
			addr16 = ioSamplePacket.get16bitSourceAddress();
			remoteDevice = network.getDevice(addr64);
			break;
		case RX_IO_64:
			RX64IOPacket rx64IOPacket = (RX64IOPacket)apiPacket;
			addr64 = rx64IOPacket.get64bitSourceAddress();
			remoteDevice = network.getDevice(addr64);
			break;
		case RX_IO_16:
			RX16IOPacket rx16IOPacket = (RX16IOPacket)apiPacket;
			addr64 = XBee64BitAddress.UNKNOWN_ADDRESS;
			addr16 = rx16IOPacket.get16bitSourceAddress();
			remoteDevice = network.getDevice(addr16);
			break;
		case EXPLICIT_RX_INDICATOR:
			ExplicitRxIndicatorPacket explicitDataPacket = (ExplicitRxIndicatorPacket)apiPacket;
			addr64 = explicitDataPacket.get64BitSourceAddress();
			addr16 = explicitDataPacket.get16BitSourceAddress();
			remoteDevice = network.getDevice(addr64);
			break;
		default:
			// Rest of the types are considered not to contain information 
			// about the origin of the packet.
			return remoteDevice;
		}
		
		// If the origin is not in the network, add it.
		if (remoteDevice == null) {
			remoteDevice = createRemoteXBeeDevice(addr64, addr16, null);
			network.addRemoteDevice(remoteDevice);
		}
		
		return remoteDevice;
	}
	
	/**
	 * Creates a new remote XBee device with the provided 64-bit address, 
	 * 16-bit address, node identifier and the XBee device that is using this 
	 * data reader as the connection interface for the remote device.
	 * 
	 * The new XBee device will be a {@code RemoteDigiMeshDevice}, 
	 * a {@code RemoteDigiPointDevice}, a {@code RemoteRaw802Device} or a 
	 * {@code RemoteZigBeeDevice} depending on the protocol of the local XBee 
	 * device. If the protocol cannot be determined or is unknown a 
	 * {@code RemoteXBeeDevice} will be created instead.
	 * 
	 * @param addr64 The 64-bit address of the new remote device. It cannot be 
	 *               {@code null}.
	 * @param addr16 The 16-bit address of the new remote device. It may be 
	 *               {@code null}.
	 * @param ni The node identifier of the new remote device. It may be 
	 *           {@code null}.
	 * 
	 * @return a new remote XBee device with the given parameters.
	 */
	private RemoteXBeeDevice createRemoteXBeeDevice(XBee64BitAddress addr64, 
			XBee16BitAddress addr16, String ni) {
		RemoteXBeeDevice device = null;
		
		switch (xbeeDevice.getXBeeProtocol()) {
		case ZIGBEE:
			device = new RemoteZigBeeDevice(xbeeDevice, addr64, addr16, ni);
			break;
		case DIGI_MESH:
			device = new RemoteDigiMeshDevice(xbeeDevice, addr64, ni);
			break;
		case DIGI_POINT:
			device = new RemoteDigiPointDevice(xbeeDevice, addr64, ni);
			break;
		case RAW_802_15_4:
			device = new RemoteRaw802Device(xbeeDevice, addr64, addr16, ni);
			break;
		default:
			device = new RemoteXBeeDevice(xbeeDevice, addr64, addr16, ni);
			break;
		}
		
		return device;
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
	 * Notifies subscribed explicit data receive listeners that a new XBee 
	 * explicit data packet has been received in form of an 
	 * {@code ExplicitXBeeMessage}.
	 *
	 * @param explicitXBeeMessage The XBee message to be sent to subscribed 
	 *                            XBee data listeners.
	 * 
	 * @see com.digi.xbee.api.models.ExplicitXBeeMessage
	 */
	private void notifyExplicitDataReceived(final ExplicitXBeeMessage explicitXBeeMessage) {
		if (explicitXBeeMessage.isBroadcast())
			logger.info(connectionInterface.toString() + 
					"Broadcast explicit data received from {} >> {}.", explicitXBeeMessage.getDevice().get64BitAddress(), HexUtils.prettyHexString(explicitXBeeMessage.getData()));
		else
			logger.info(connectionInterface.toString() + 
					"Explicit data received from {} >> {}.", explicitXBeeMessage.getDevice().get64BitAddress(), HexUtils.prettyHexString(explicitXBeeMessage.getData()));
		
		try {
			synchronized (explicitDataReceiveListeners) {
				ScheduledExecutorService executor = Executors.newScheduledThreadPool(Math.min(MAXIMUM_PARALLEL_LISTENER_THREADS, 
						explicitDataReceiveListeners.size()));
				for (final IExplicitDataReceiveListener listener:explicitDataReceiveListeners) {
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
								listener.explicitDataReceived(explicitXBeeMessage);
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
	 * Notifies subscribed network data receive listeners that a new network 
	 * data packet has been received in form of a {@code NetworkMessage}.
	 *
	 * @param networkMessage The network message to be sent to subscribed 
	 *                       network data listeners.
	 * 
	 * @see com.digi.xbee.api.models.NetworkMessage
	 */
	private void notifyNetworkDataReceived(final NetworkMessage networkMessage) {
		if (networkMessage.isBroadcast())
			logger.info(connectionInterface.toString() + 
					"Broadcast network data received from {} >> {}.", networkMessage.getIPAddress(), HexUtils.prettyHexString(networkMessage.getData()));
		else
			logger.info(connectionInterface.toString() + 
					"Network data received from {} >> {}.", networkMessage.getIPAddress(), HexUtils.prettyHexString(networkMessage.getData()));
		
		try {
			synchronized (networkDataReceiveListeners) {
				ScheduledExecutorService executor = Executors.newScheduledThreadPool(Math.min(MAXIMUM_PARALLEL_LISTENER_THREADS, 
						networkDataReceiveListeners.size()));
				for (final INetworkDataReceiveListener listener:networkDataReceiveListeners) {
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
								listener.networkDataReceived(networkMessage);
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
	 * Notifies subscribed SMS receive listeners that a new SMS packet has 
	 * been received in form of an {@code SMSMessage}.
	 *
	 * @param smsMessage The SMS message to be sent to subscribed SMS listeners.
	 * 
	 * @see com.digi.xbee.api.models.SMSMessage
	 */
	private void notifySMSReceived(final SMSMessage smsMessage) {
		logger.info(connectionInterface.toString() + 
				"SMS received from {} >> {}.", smsMessage.getPhoneNumber(), smsMessage.getData());
		
		try {
			synchronized (smsReceiveListeners) {
				ScheduledExecutorService executor = Executors.newScheduledThreadPool(Math.min(MAXIMUM_PARALLEL_LISTENER_THREADS, 
						smsReceiveListeners.size()));
				for (final ISMSReceiveListener listener:smsReceiveListeners) {
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
								listener.smsReceived(smsMessage);
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
