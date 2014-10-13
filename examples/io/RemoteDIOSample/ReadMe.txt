  Introduction
  ------------
  This is a sample Java application to show how to set and read XBee digital 
  lines of remote devices.
  
  The application configures two IO lines of the XBee devices, one in the remote
  device as a digital input (button) and the other in the local device as a 
  digital output (LED). Then, the application reads the status of the input line 
  periodically and updates the output to follow the input.
  
  While the push button is pressed the LED should be lighting.
  
  NOTE: This example uses the generic remote XBee device (RemoteXBeeDevice) 
        class, but it can be applied to any other remote XBee device class.


  Files
  ----------
    * com.digi.xbee.api.remotedio.MainApp.java:
      Main application class. It instantiates a local XBee device and a remote 
      XBee device, establishes a serial connection with the local one, 
      configures the IO lines and reads/sets the digital IOs.


  Requirements
  ------------
  To run this example you will need:
  
    * At least two XBee radios in API mode and their corresponding carrier board
      (XBIB or XBee Development Board).


  Example setup
  -------------
    1) Insert the 64-bit address of the remote XBee module in the MainApp class.
       Find the 64-bit address labeled on the back of the device, which is a 16 
       character string that follows the format 0013A20040XXXXXX.
       
    2) Plug the XBee radios into the XBee adapters and connect them to your
       computer's USB or serial ports.
       
    3) Ensure that the modules are in API mode and on the same network.
       For further information on how to perform this task, go to [...]
       
    4) Set the port and baud rate of the local XBee radio in the MainApp class.
       If you do not know the serial/USB port where your module is connected to,
       see [...]
       
    5) The final step is to configure the IO lines in the example. Depending 
       on the carrier board you are using you may need to change a couple of 
       lines in the example code:
         - XBIB-U-DEV board:
             * The example is already configured to use this carrier board. 
               The input line is configured to use the SW5 user button of the 
               board and the output line is connected to the DS2 user LED. No
               further changes are necessary.
         
         - XBee Development Board:
             * If you are using the XBee Development Board, update the IOLINE_IN
               constant accordingly. There are comments in the code indicating
               which fragment belongs to each board.

         NOTE: It is recommended to verify the capabilities of the pins used 
               in the example in the product manual of your XBee Device to 
               ensure that everything is configured correctly.


  Running the example
  -------------------
  First, build and launch the application.
  To test the functionality, follow these steps:
  
    1) Press the button corresponding to the digital input line in the remote
       XBee device. In the XBIB boards it is the DIO3; in the XBee Development 
       boards is the DIO4 or User Button.
       
    2) Verify that the status of the LED corresponding to the digital output 
       line in the local XBee device changes. In the XBIB boards it is the 
       DIO12; in the XBee Development boards is the DIO12 or User0/TCP.
       