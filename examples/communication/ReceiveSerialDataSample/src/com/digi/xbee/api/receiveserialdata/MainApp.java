package com.digi.xbee.api.receiveserialdata;

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.XBeeException;

/**
 * XBee Java Library Receive Data sample application.
 * 
 * <p>This example registers a listener to manage the received data.</p>
 * 
 * <p>For a complete description on the example, refer to the 'ReadMe.txt' file
 * included in the root directory.</p>
 */
public class MainApp {
	
	/* Constants */
	
	// TODO Replace with the serial port where your receiver module is connected.
	private static final String PORT = "COM5";
	// TODO Replace with the baud rate of you receiver module.
	private static final int BAUD_RATE = 9600;

	/**
	 * Application main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		XBeeDevice myDevice = new XBeeDevice(PORT, BAUD_RATE);
		try {
			myDevice.open();
		} catch (XBeeException e) {
			e.printStackTrace();
		} catch (InvalidOperatingModeException e) {
			e.printStackTrace();
		}
		
		myDevice.startListeningForSerialData(new MySerialDataReceiveListener());
	}
}
