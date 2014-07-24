package com.digi.xbee.api.sendserialdata;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.utils.HexUtils;

/**
 * XBee Java Library Send Data sample application.
 * 
 * <p>This example sends data to a remote device with the provided 64-bit or 
 * 16-bit address.</p>
 * 
 * <p>For a complete description on the example, refer to the 'ReadMe.txt' file
 * included in the root directory.</p>
 */
public class MainApp {
	
	/* Constants */
	
	// TODO Replace with the serial port where your sender module is connected to.
	private static final String PORT = "COM1";
	// TODO Replace with the baud rate of your sender module.
	private static final int BAUD_RATE = 9600;
	
	// TODO Replace with the 64-bit address of your receiver module.
	private static final String DESTINATION_64_BIT_ADDRESS = "000000000000FFFE";
	// TODO Replace with the 16-bit address of your receiver module.
	//private static final String DESTINATION_16_BIT_ADDRESS = "FFFE";
	
	// TODO Replace with the data to send.
	private static final String DATA_TO_SEND = "Hello XBee!";
	
	/**
	 * Pattern for the 64-bit address string: {@value}.
	 */
	private static final String ADDRESS_64_PATTERN = "(0x)?[0-9A-Fa-f]{1,16}";
	/**
	 * Pattern for the 16-bit address string: {@value}.
	 */
	private static final String ADDRESS_16_PATTERN = "(0x)?[0-9A-Fa-f]{1,4}";
	
	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
		
		// Use an XBee64BitAddress object when using a 64-bit destination address.
		XBee64BitAddress destinationAddress = get64BitAddress(DESTINATION_64_BIT_ADDRESS);
		// Use an XBee16BitAddress object when using a 16-bit destination address.
		//XBee16BitAddress destinationAddress = get16BitAddress(DESTINATION_16_BIT_ADDRESS);
		
		byte[] dataToSend = DATA_TO_SEND.getBytes();
		
		boolean success = false;
		try {
			// Open the local device.
			myDevice.open();
			
			System.out.format("Sending data to %s >> %s | %s... ", destinationAddress, 
					HexUtils.prettyHexString(HexUtils.byteArrayToHexString(dataToSend)), 
					new String(dataToSend));
			
			// Send the data to the destination address.
			success = myDevice.sendSerialData(destinationAddress, dataToSend);
		} catch (XBeeException e) {
			e.printStackTrace();
		} catch (InvalidOperatingModeException e) {
			e.printStackTrace();
		} finally {
			// Close the local device before exiting.
			myDevice.close();
			if (success) {
				System.out.println("Success");
				System.exit(0);
			}
			System.out.println("Error");
			System.exit(1);
		}
	}
	
	/**
	 * Returns a {@code XBee64BitAddress} for the given address.
	 * 
	 * <p>The string must be the hexadecimal representation of a 64-bit address.
	 * If the specified {@code address} contains non-hexadecimal characters 
	 * {@code null} is returned.</p>
	 * 
	 * @param address The 64-bit address.
	 * @return The {@code XBee64BitAddress} object that represents the given 
	 *         address or {@code null} if it is invalid.
	 */
	private static XBee64BitAddress get64BitAddress(String address) {
		if (address == null || !address.matches(ADDRESS_64_PATTERN)) {
			System.out.println("*** ERROR: Invalid address, it should be an hexadecimal value with 1-16 digits.\n");
			return null;
		}
		
		return new XBee64BitAddress(address);
	}
	
	/**
	 * Returns a {@code XBee16BitAddress} for the given address.
	 * 
	 * <p>The string must be the hexadecimal representation of a 16-bit address.
	 * If the specified {@code address} contains non-hexadecimal characters 
	 * {@code null} is returned.</p>
	 * 
	 * @param address The 16-bit address.
	 * @return The {@code XBee16BitAddress} object that represents the given 
	 *         address or {@code null} if it is invalid.
	 */
	private static XBee16BitAddress get16BitAddress(String address) {
		if (address == null || !address.matches(ADDRESS_16_PATTERN)) {
			System.out.println("*** ERROR: Invalid address, it should be an hexadecimal value with 1-4 digits.\n");
			return null;
		}
		
		return new XBee16BitAddress(address);
	}
}
