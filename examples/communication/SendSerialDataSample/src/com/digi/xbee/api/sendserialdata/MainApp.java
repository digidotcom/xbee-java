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
	private static final XBee64BitAddress DESTINATION_64_BIT_ADDRESS = new XBee64BitAddress("0013A20040XXXXXX");
	// TODO Replace with the 16-bit address of your receiver module.
	//private static final XBee16BitAddress DESTINATION_16_BIT_ADDRESS = new XBee16BitAddress("XXXX");
	
	// TODO Replace with the data to send.
	private static final String DATA_TO_SEND = "Hello XBee!";
	
	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
		
		// Use an XBee64BitAddress object when using a 64-bit destination address.
		XBee64BitAddress destinationAddress = DESTINATION_64_BIT_ADDRESS;
		// Use an XBee16BitAddress object when using a 16-bit destination address.
		//XBee16BitAddress destinationAddress = DESTINATION_16_BIT_ADDRESS;
		
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
}
