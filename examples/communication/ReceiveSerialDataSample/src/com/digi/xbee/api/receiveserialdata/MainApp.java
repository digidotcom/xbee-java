package com.digi.xbee.api.receiveserialdata;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.XBeeException;

/**
 * XBee Java Library Receive Serial Data sample application.
 * 
 * <p>This example registers a listener to manage the received serial data.</p>
 * 
 * <p>For a complete description on the example, refer to the 'ReadMe.txt' file
 * included in the root directory.</p>
 */
public class MainApp {
	
	/* Constants */
	
	// ** REPLACE WITH THE SERIAL PORT FOR YOUR COORDINATOR XBEE **
	private static final String PORT = "COM5";
	// ** REPLACE WITH THE BAUD RATE FOR YOUR COORDINATOR XBEE **
	private static final int BAUD_RATE = 9600;

	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
		try {
			myDevice.connect();
		} catch (XBeeException e) {
			e.printStackTrace();
		} catch (InvalidOperatingModeException e) {
			e.printStackTrace();
		}
		
		myDevice.startlisteningForSerialData(new MySerialDataReceiveListener());
	}

}
