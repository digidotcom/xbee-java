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
package com.digi.xbee.api.models;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.LinkedList;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket;
import com.digi.xbee.api.packet.common.ReceivePacket;
import com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket;
import com.digi.xbee.api.packet.ip.RXIPv4Packet;
import com.digi.xbee.api.packet.raw.RX16IOPacket;
import com.digi.xbee.api.packet.raw.RX16Packet;
import com.digi.xbee.api.packet.raw.RX64IOPacket;
import com.digi.xbee.api.packet.raw.RX64Packet;
import com.digi.xbee.api.packet.thread.RXIPv6Packet;

/**
 * This class represents a queue of XBee packets used for sequential packets 
 * reading within the XBee Java API. 
 * 
 * <p>The class provides some methods to get specific packet types from 
 * different source nodes.</p>
 * 
 * @see com.digi.xbee.api.packet.XBeePacket
 */
public class XBeePacketsQueue {

	// Constants.
	/**
	 * Default maximum number of packets to store in the queue 
	 * (value: {@value}).
	 */
	public static final int DEFAULT_MAX_LENGTH = 50;
	
	// Variables.
	private int maxLength = DEFAULT_MAX_LENGTH;
	
	private LinkedList<XBeePacket> packetsList;
	
	private Object lock = new Object();
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code XBeePacketsQueue}.
	 */
	public XBeePacketsQueue() {
		this(DEFAULT_MAX_LENGTH);
	}
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code XBeePacketsQueue} with the given maximum length.
	 * 
	 * @param maxLength Maximum length of the queue.
	 * 
	 * @throws IllegalArgumentException if {@code maxLength < 1}.
	 */
	public XBeePacketsQueue(int maxLength) {
		if (maxLength < 1)
			throw new IllegalArgumentException("Queue length must be greater than 0.");
		
		this.maxLength = maxLength;
		packetsList = new LinkedList<XBeePacket>();
	}
	
	/**
	 * Adds the provided packet to the list of packets. If the queue is full 
	 * the first packet will be discarded to add the given one.
	 * 
	 * @param xbeePacket The XBee packet to be added to the list.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	public void addPacket(XBeePacket xbeePacket) {
		synchronized (lock) {
			if (packetsList.size() == maxLength)
				packetsList.removeFirst();
			packetsList.add(xbeePacket);
		}
	}
	
	/**
	 * Clears the list of packets.
	 */
	public void clearQueue() {
		synchronized (lock) {
			packetsList.clear();
		}
	}
	
	/**
	 * Returns the first packet from the queue waiting up to the specified 
	 * timeout if  necessary for an XBee packet to become available. 
	 * {@code null }if the queue is empty.
	 * 
	 * @param timeout The time in milliseconds to wait for an XBee packet to 
	 *                become available. 0 to return immediately.
	 * @return The first packet from the queue, {@code null} if it is empty.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	public XBeePacket getFirstPacket(int timeout) {
		if (timeout > 0) {
			XBeePacket xbeePacket = getFirstPacket(0);
			// Wait for a timeout or until an XBee packet is read.
			Long deadLine = System.currentTimeMillis() + timeout;
			while (xbeePacket == null && deadLine > System.currentTimeMillis()) {
				sleep(100);
				xbeePacket = getFirstPacket(0);
			}
			return xbeePacket;
		} 
		synchronized (lock) {
			if (!packetsList.isEmpty())
				return packetsList.pop();
		}
		return null;
	}
	
	/**
	 * Returns the first packet from the queue whose 64-bit source address 
	 * matches the address of the provided remote XBee device.
	 * 
	 * <p>The methods waits up to the specified timeout if necessary for an 
	 * XBee packet to become available. Null if the queue is empty or there is 
	 * not any XBee packet sent by the provided remote XBee device.</p>
	 * 
	 * @param remoteXBeeDevice The remote XBee device containing the 64-bit 
	 *                         address to look for in the list of packets.
	 * @param timeout The time in milliseconds to wait for an XBee packet from 
	 *                the specified remote XBee device to become available. 
	 *                0 to return immediately.
	 * 
	 * @return The first XBee packet whose 64-bit address matches the address 
	 *         of the provided remote XBee device. {@code null} if no packets 
	 *         from the specified XBee device are found in the queue.
	 * 
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	public XBeePacket getFirstPacketFrom(RemoteXBeeDevice remoteXBeeDevice, int timeout) {
		if (timeout > 0) {
			XBeePacket xbeePacket = getFirstPacketFrom(remoteXBeeDevice, 0);
			// Wait for a timeout or until an XBee packet from remoteXBeeDevice is read.
			Long deadLine = System.currentTimeMillis() + timeout;
			while (xbeePacket == null && deadLine > System.currentTimeMillis()) {
				sleep(100);
				xbeePacket = getFirstPacketFrom(remoteXBeeDevice, 0);
			}
			return xbeePacket;
		} else {
			synchronized (lock) {
				for (int i = 0; i < packetsList.size(); i++) {
					XBeePacket xbeePacket = packetsList.get(i);
					if (addressesMatch(xbeePacket, remoteXBeeDevice))
						return packetsList.remove(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the first data packet from the queue waiting up to the 
	 * specified timeout if necessary for an XBee data packet to become 
	 * available. {@code null} if the queue is empty or there is not any data 
	 * packet inside.
	 * 
	 * @param timeout The time in milliseconds to wait for an XBee data packet 
	 *                to become available. 0 to return immediately.
	 * 
	 * @return The first data packet from the queue, {@code null} if it is 
	 *         empty or no data packets are contained in the queue.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	public XBeePacket getFirstDataPacket(int timeout) {
		if (timeout > 0) {
			XBeePacket xbeePacket = getFirstDataPacket(0);
			// Wait for a timeout or until a data XBee packet is read.
			Long deadLine = System.currentTimeMillis() + timeout;
			while (xbeePacket == null && deadLine > System.currentTimeMillis()) {
				sleep(100);
				xbeePacket = getFirstDataPacket(0);
			}
			return xbeePacket;
		} else {
			synchronized (lock) {
				for (int i = 0; i < packetsList.size(); i++) {
					XBeePacket xbeePacket = packetsList.get(i);
					if (isDataPacket(xbeePacket))
						return packetsList.remove(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the first data packet from the queue whose 64-bit source 
	 * address matches the address of the provided remote XBee device.
	 * 
	 * <p>The methods waits up to the specified timeout if necessary for an 
	 * XBee data packet to become available. {@code null} if the queue is 
	 * empty or there is not any XBee data packet sent by the provided remote 
	 * XBee device.</p>
	 * 
	 * @param remoteXBeeDevice The XBee device containing the 64-bit address 
	 *                         to look for in the list of packets.
	 * @param timeout The time in milliseconds to wait for an XBee data packet 
	 *                from the specified remote XBee device to become 
	 *                available. 0 to return immediately.
	 * 
	 * @return The first XBee data packet whose its 64-bit address matches the 
	 *         address of the provided remote XBee device. {@code null} if no 
	 *         data packets from the specified XBee device are found in the 
	 *         queue.
	 * 
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	public XBeePacket getFirstDataPacketFrom(RemoteXBeeDevice remoteXBeeDevice, int timeout) {
		if (timeout > 0) {
			XBeePacket xbeePacket = getFirstDataPacketFrom(remoteXBeeDevice, 0);
			// Wait for a timeout or until an XBee packet from remoteXBeeDevice is read.
			Long deadLine = System.currentTimeMillis() + timeout;
			while (xbeePacket == null && deadLine > System.currentTimeMillis()) {
				sleep(100);
				xbeePacket = getFirstDataPacketFrom(remoteXBeeDevice, 0);
			}
			return xbeePacket;
		} else {
			synchronized (lock) {
				for (int i = 0; i < packetsList.size(); i++) {
					XBeePacket xbeePacket = packetsList.get(i);
					if (isDataPacket(xbeePacket) && addressesMatch(xbeePacket, remoteXBeeDevice))
						return packetsList.remove(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the first explicit data packet from the queue waiting up to the 
	 * specified timeout if necessary for an XBee explicit data packet to 
	 * become available. {@code null} if the queue is empty or there is not 
	 * any explicit data packet inside.
	 * 
	 * @param timeout The time in milliseconds to wait for an XBee explicit 
	 *                data packet to become available. 0 to return immediately.
	 * 
	 * @return The first explicit data packet from the queue, {@code null} if 
	 *         it is empty or no data packets are contained in the queue.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 * @see com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket
	 */
	public XBeePacket getFirstExplicitDataPacket(int timeout) {
		if (timeout > 0) {
			XBeePacket xbeePacket = getFirstExplicitDataPacket(0);
			// Wait for a timeout or until an explicit data XBee packet is read.
			Long deadLine = System.currentTimeMillis() + timeout;
			while (xbeePacket == null && deadLine > System.currentTimeMillis()) {
				sleep(100);
				xbeePacket = getFirstExplicitDataPacket(0);
			}
			return xbeePacket;
		} else {
			synchronized (lock) {
				for (int i = 0; i < packetsList.size(); i++) {
					XBeePacket xbeePacket = packetsList.get(i);
					if (isExplicitDataPacket(xbeePacket))
						return packetsList.remove(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the first explicit data packet from the queue whose 64-bit 
	 * source address matches the address of the provided remote XBee device.
	 * 
	 * <p>The methods waits up to the specified timeout if necessary for an 
	 * XBee explicit data packet to become available. {@code null} if the 
	 * queue is empty or there is not any XBee explicit data packet sent by 
	 * the provided remote XBee device.</p>
	 * 
	 * @param remoteXBeeDevice The XBee device containing the 64-bit address 
	 *                         to look for in the list of packets.
	 * @param timeout The time in milliseconds to wait for an XBee explicit 
	 *                data packet from the specified remote XBee device to 
	 *                become available. 0 to return immediately.
	 * 
	 * @return The first XBee explicit data packet whose its 64-bit address 
	 *         matches the address of the provided remote XBee device. 
	 *         {@code null} if no explicit data packets from the specified 
	 *         XBee device are found in the queue.
	 * 
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 * @see com.digi.xbee.api.packet.XBeePacket
	 * @see com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket
	 */
	public XBeePacket getFirstExplicitDataPacketFrom(RemoteXBeeDevice remoteXBeeDevice, int timeout) {
		if (timeout > 0) {
			XBeePacket xbeePacket = getFirstExplicitDataPacketFrom(remoteXBeeDevice, 0);
			// Wait for a timeout or until an XBee explicit data packet from remoteXBeeDevice is read.
			Long deadLine = System.currentTimeMillis() + timeout;
			while (xbeePacket == null && deadLine > System.currentTimeMillis()) {
				sleep(100);
				xbeePacket = getFirstExplicitDataPacketFrom(remoteXBeeDevice, 0);
			}
			return xbeePacket;
		} else {
			synchronized (lock) {
				for (int i = 0; i < packetsList.size(); i++) {
					XBeePacket xbeePacket = packetsList.get(i);
					if (isExplicitDataPacket(xbeePacket) && addressesMatch(xbeePacket, remoteXBeeDevice))
						return packetsList.remove(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the first IP data packet from the queue waiting up to the 
	 * specified timeout if necessary for a IP data packet to 
	 * become available. {@code null} if the queue is empty or there is not 
	 * any IP data packet inside.
	 * 
	 * @param timeout The time in milliseconds to wait for a IP data 
	 *                packet to become available. 0 to return immediately.
	 * 
	 * @return The first IP data packet from the queue, {@code null} if 
	 *         it is empty or no IP packets are contained in the queue.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 * @see com.digi.xbee.api.packet.ip.RXIPv4Packet
	 * 
	 * @since 1.2.0
	 */
	public XBeePacket getFirstIPDataPacket(int timeout) {
		if (timeout > 0) {
			XBeePacket xbeePacket = getFirstIPDataPacket(0);
			// Wait for a timeout or until a IP data packet is read.
			Long deadLine = System.currentTimeMillis() + timeout;
			while (xbeePacket == null && deadLine > System.currentTimeMillis()) {
				sleep(100);
				xbeePacket = getFirstIPDataPacket(0);
			}
			return xbeePacket;
		} else {
			synchronized (lock) {
				for (int i = 0; i < packetsList.size(); i++) {
					XBeePacket xbeePacket = packetsList.get(i);
					if (isIPDataPacket(xbeePacket))
						return packetsList.remove(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the first IPv6 data packet from the queue waiting up to the 
	 * specified timeout if necessary for a IPv6 data packet to 
	 * become available. {@code null} if the queue is empty or there is not 
	 * any IPv6 data packet inside.
	 * 
	 * @param timeout The time in milliseconds to wait for a IPv6 data 
	 *                packet to become available. 0 to return immediately.
	 * 
	 * @return The first IPv6 data packet from the queue, {@code null} if 
	 *         it is empty or no IPv6 packets are contained in the queue.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 * @see com.digi.xbee.api.packet.thread.RXIPv6Packet
	 * 
	 * @since 1.2.1
	 */
	public XBeePacket getFirstIPv6DataPacket(int timeout) {
		if (timeout > 0) {
			XBeePacket xbeePacket = getFirstIPv6DataPacket(0);
			// Wait for a timeout or until a IPv6 data packet is read.
			Long deadLine = System.currentTimeMillis() + timeout;
			while (xbeePacket == null && deadLine > System.currentTimeMillis()) {
				sleep(100);
				xbeePacket = getFirstIPv6DataPacket(0);
			}
			return xbeePacket;
		} else {
			synchronized (lock) {
				for (int i = 0; i < packetsList.size(); i++) {
					XBeePacket xbeePacket = packetsList.get(i);
					if (isIPv6DataPacket(xbeePacket))
						return packetsList.remove(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the first IP data packet from the queue whose IP address 
	 * matches the provided address.
	 * 
	 * <p>The methods waits up to the specified timeout if necessary for a 
	 * IP data packet to become available. {@code null} if the 
	 * queue is empty or there is not any IP data packet sent by 
	 * the provided IP address.</p>
	 * 
	 * @param ipAddress The IP address to look for in the list of packets.
	 * @param timeout The time in milliseconds to wait for a IP data 
	 *                packet from the specified IP address to become available.
	 *                0 to return immediately.
	 * 
	 * @return The first IP packet whose IP address matches the provided 
	 *         IP address. {@code null} if no IP data packets from the 
	 *         specified IP address are found in the queue.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 * @see com.digi.xbee.api.packet.ip.RXIPv4Packet
	 * @see java.net.Inet4Address
	 * 
	 * @since 1.2.0
	 */
	public XBeePacket getFirstIPDataPacketFrom(Inet4Address ipAddress, int timeout) {
		if (timeout > 0) {
			XBeePacket xbeePacket = getFirstIPDataPacketFrom(ipAddress, 0);
			// Wait for a timeout or until a IP data packet with the provided IP address is read.
			Long deadLine = System.currentTimeMillis() + timeout;
			while (xbeePacket == null && deadLine > System.currentTimeMillis()) {
				sleep(100);
				xbeePacket = getFirstIPDataPacketFrom(ipAddress, 0);
			}
			return xbeePacket;
		} else {
			synchronized (lock) {
				for (int i = 0; i < packetsList.size(); i++) {
					XBeePacket xbeePacket = packetsList.get(i);
					if (isIPDataPacket(xbeePacket) && ipAddressesMatch(xbeePacket, ipAddress))
						return packetsList.remove(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the first IPv6 data packet from the queue whose IPv6 address 
	 * matches the provided address.
	 * 
	 * <p>The methods waits up to the specified timeout if necessary for a 
	 * IPv6 data packet to become available. {@code null} if the 
	 * queue is empty or there is not any IPv6 data packet sent by 
	 * the provided IPv6 address.</p>
	 * 
	 * @param ipv6Address The IPv6 address to look for in the list of packets.
	 * @param timeout The time in milliseconds to wait for a IPv6 data 
	 *                packet from the specified IPv6 address to become available.
	 *                0 to return immediately.
	 * 
	 * @return The first IPv6 packet whose IPv6 address matches the provided 
	 *         IPv6 address. {@code null} if no IPv6 data packets from the 
	 *         specified IPv6 address are found in the queue.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 * @see com.digi.xbee.api.packet.thread.RXIPv6Packet
	 * @see java.net.Inet6Address
	 * 
	 * @since 1.2.1
	 */
	public XBeePacket getFirstIPv6DataPacketFrom(Inet6Address ipv6Address, int timeout) {
		if (timeout > 0) {
			XBeePacket xbeePacket = getFirstIPv6DataPacketFrom(ipv6Address, 0);
			// Wait for a timeout or until a IPv6 data packet with the provided IPv6 address is read.
			Long deadLine = System.currentTimeMillis() + timeout;
			while (xbeePacket == null && deadLine > System.currentTimeMillis()) {
				sleep(100);
				xbeePacket = getFirstIPv6DataPacketFrom(ipv6Address, 0);
			}
			return xbeePacket;
		} else {
			synchronized (lock) {
				for (int i = 0; i < packetsList.size(); i++) {
					XBeePacket xbeePacket = packetsList.get(i);
					if (isIPv6DataPacket(xbeePacket) && ipv6AddressesMatch(xbeePacket, ipv6Address))
						return packetsList.remove(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns whether or not the source address of the provided XBee packet 
	 * matches the address of the given remote XBee device.
	 * 
	 * @param xbeePacket The XBee packet to compare its address with the 
	 *                   remote XBee device.
	 * @param remoteXBeeDevice The remote XBee device to compare its address 
	 *                         with the XBee packet.
	 * 
	 * @return {@code true} if the source address of the provided packet (if 
	 *         it has) matches the address of the remote XBee device.
	 * 
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	private boolean addressesMatch(XBeePacket xbeePacket, RemoteXBeeDevice remoteXBeeDevice) {
		if (!(xbeePacket instanceof XBeeAPIPacket))
			return false;
		APIFrameType packetType = ((XBeeAPIPacket)xbeePacket).getFrameType();
		switch (packetType) {
		case RECEIVE_PACKET:
			if (remoteXBeeDevice.get64BitAddress() != null && ((ReceivePacket)xbeePacket).get64bitSourceAddress().equals(remoteXBeeDevice.get64BitAddress()))
				return true;
			if (remoteXBeeDevice.get16BitAddress() != null && ((ReceivePacket)xbeePacket).get16bitSourceAddress().equals(remoteXBeeDevice.get16BitAddress()))
				return true;
			break;
		case REMOTE_AT_COMMAND_RESPONSE:
			if (remoteXBeeDevice.get64BitAddress() != null && ((RemoteATCommandResponsePacket)xbeePacket).get64bitSourceAddress().equals(remoteXBeeDevice.get64BitAddress()))
				return true;
			if (remoteXBeeDevice.get16BitAddress() != null && ((RemoteATCommandResponsePacket)xbeePacket).get16bitSourceAddress().equals(remoteXBeeDevice.get16BitAddress()))
				return true;
			break;
		case RX_16:
			if (((RX16Packet)xbeePacket).get16bitSourceAddress().equals(remoteXBeeDevice.get16BitAddress()))
				return true;
			break;
		case RX_64:
			if (((RX64Packet)xbeePacket).get64bitSourceAddress().equals(remoteXBeeDevice.get64BitAddress()))
				return true;
			break;
		case RX_IO_16:
			if (((RX16IOPacket)xbeePacket).get16bitSourceAddress().equals(remoteXBeeDevice.get16BitAddress()))
				return true;
			break;
		case RX_IO_64:
			if (((RX64IOPacket)xbeePacket).get64bitSourceAddress().equals(remoteXBeeDevice.get64BitAddress()))
				return true;
			break;
		case EXPLICIT_RX_INDICATOR:
			if (((ExplicitRxIndicatorPacket)xbeePacket).get64BitSourceAddress().equals(remoteXBeeDevice.get64BitAddress()))
				return true;
			break;
		default:
			return false;
		}
		return false;
	}
	
	/**
	 * Returns whether or not the IP address of the XBee packet matches the 
	 * provided one. 
	 * 
	 * @param xbeePacket The XBee packet to compare its IP address with the 
	 *                   provided one.
	 * @param ipAddress The IP address to be compared with the XBee packet's one.
	 * 
	 * @return {@code true} if the IP address of the XBee packet (if it has) 
	 *         matches the provided one. {@code false} otherwise.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 * @see java.net.Inet4Address
	 * 
	 * @since 1.2.0
	 */
	private boolean ipAddressesMatch(XBeePacket xbeePacket, Inet4Address ipAddress) {
		if (xbeePacket == null || ipAddress == null 
				|| !(xbeePacket instanceof XBeeAPIPacket))
			return false;
		APIFrameType packetType = ((XBeeAPIPacket)xbeePacket).getFrameType();
		switch (packetType) {
		case RX_IPV4:
			if (((RXIPv4Packet)xbeePacket).getSourceAddress().equals(ipAddress))
				return true;
			break;
		default:
			return false;
		}
		return false;
	}
	
	/**
	 * Returns whether the IPv6 address of the XBee packet matches the 
	 * provided one or not. 
	 * 
	 * @param xbeePacket The XBee packet to compare its IPv6 address with the 
	 *                   provided one.
	 * @param ipv6Address The IPv6 address to be compared with the XBee 
	 *                    packet's one.
	 * 
	 * @return {@code true} if the Iv6P address of the XBee packet (if it has) 
	 *         matches the provided one. {@code false} otherwise.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 * @see java.net.Inet6Address
	 * 
	 * @since 1.2.1
	 */
	private boolean ipv6AddressesMatch(XBeePacket xbeePacket, Inet6Address ipv6Address) {
		if (xbeePacket == null || ipv6Address == null 
				|| !(xbeePacket instanceof XBeeAPIPacket))
			return false;
		APIFrameType packetType = ((XBeeAPIPacket)xbeePacket).getFrameType();
		switch (packetType) {
		case RX_IPV6:
			if (((RXIPv6Packet)xbeePacket).getSourceAddress().equals(ipv6Address))
				return true;
			break;
		default:
			return false;
		}
		return false;
	}
	
	/**
	 * Returns whether or not the given XBee packet is a data packet.
	 * 
	 * @param xbeePacket The XBee packet to check if is data packet.
	 * 
	 * @return {@code true} if the XBee packet is a data packet, {@code false} 
	 *         otherwise.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 */
	private boolean isDataPacket(XBeePacket xbeePacket) {
		if (!(xbeePacket instanceof XBeeAPIPacket))
			return false;
		APIFrameType packetType = ((XBeeAPIPacket)xbeePacket).getFrameType();
		switch (packetType) {
			case RECEIVE_PACKET:
			case RX_16:
			case RX_64:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Returns whether or not the given XBee packet is an explicit data packet.
	 * 
	 * @param xbeePacket The XBee packet to check if is an explicit data packet.
	 * 
	 * @return {@code true} if the XBee packet is an explicit data packet, 
	 *         {@code false} otherwise.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 * @see com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket
	 */
	private boolean isExplicitDataPacket(XBeePacket xbeePacket) {
		if (!(xbeePacket instanceof XBeeAPIPacket))
			return false;
		APIFrameType packetType = ((XBeeAPIPacket)xbeePacket).getFrameType();
		return packetType == APIFrameType.EXPLICIT_RX_INDICATOR;
	}
	
	/**
	 * Returns whether or not the given XBee packet is a IP data packet.
	 * 
	 * @param xbeePacket The XBee packet to check if is a IP data packet.
	 * 
	 * @return {@code true} if the XBee packet is a IP data packet, 
	 *         {@code false} otherwise.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 * @see com.digi.xbee.api.packet.ip.RXIPv4Packet
	 * 
	 * @since 1.2.0
	 */
	private boolean isIPDataPacket(XBeePacket xbeePacket) {
		if (!(xbeePacket instanceof XBeeAPIPacket))
			return false;
		APIFrameType packetType = ((XBeeAPIPacket)xbeePacket).getFrameType();
		return packetType == APIFrameType.RX_IPV4;
	}
	
	/**
	 * Returns whether the given XBee packet is an IPv6 data packet or not.
	 * 
	 * @param xbeePacket The XBee packet to check if is an IPv6 data packet.
	 * 
	 * @return {@code true} if the XBee packet is an IPv6 data packet, 
	 *         {@code false} otherwise.
	 * 
	 * @see com.digi.xbee.api.packet.XBeePacket
	 * @see com.digi.xbee.api.packet.thread.RXIPv6Packet
	 * 
	 * @since 1.2.1
	 */
	private boolean isIPv6DataPacket(XBeePacket xbeePacket) {
		if (!(xbeePacket instanceof XBeeAPIPacket))
			return false;
		APIFrameType packetType = ((XBeeAPIPacket)xbeePacket).getFrameType();
		return packetType == APIFrameType.RX_IPV6;
	}
	
	/**
	 * Sleeps the thread for the given number of milliseconds.
	 * 
	 * @param milliseconds The number of milliseconds that the thread should 
	 *        be sleeping.
	 */
	private void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) { }
	}
	
	/**
	 * Returns the maximum size of the XBee packets queue.
	 * 
	 * @return The maximum size of the XBee packets queue.
	 */
	public int getMaxSize() {
		return maxLength;
	}
	
	/**
	 * Returns the current size of the XBee packets queue.
	 * 
	 * @return The current size of the XBee packets queue.
	 */
	public int getCurrentSize() {
		synchronized (lock) {
			return packetsList.size();
		}
	}
}
