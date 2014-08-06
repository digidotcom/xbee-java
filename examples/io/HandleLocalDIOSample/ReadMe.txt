  Introduction
  ------------
  This is a sample Java application to show how to set and read XBee digital 
  lines of the device attached to the serial/USB port of your PC.
  
  The application configures two IO lines of the XBee device, one as digital
  input (button) and the other one as digital output (LED). Then, the 
  application reads the status of the input line periodically and updates 
  the output to follow the input.
  
  Therefore while the push button is pressed the LED should be lighting.


  Files
  ----------
    * com.digi.xbee.api.handlelocaldio.MainApp.java:
      Main application class. Instantiates an XBee device, establishes a 
      serial connection with it, configures the IO lines and reads/sets the
      digital IOs.


  Requirements
  ------------
  To run this example you will need:
  
    * One XBee radio in API mode and its corresponding carrier board 
      (XBIB or XBee Development Board).


  Example setup
  -------------
    1) Plug the XBee radio into the XBee adapter and connect it to your
       computer's USB or serial port.
       
    2) Ensure that the module is in API mode.
       For further information on how to perform this task, go to [...]
       
    3) Set the port and baud rate of the XBee radio in the MainApp class.
       If you do not know the serial/USB port where your module is connected to,
       see [...]
       
    4) The final step is to configure the IO lines in the example. Depending 
       on the carrier board you are using you may need to change a couple of 
       lines in the example code:
         - XBIB-U-DEV board:
             * The example is already configured to use this carrier board. 
               The input line is configured to use the SW5 user button of the 
               board and the output line is connected to the DS2 user LED. So 
               you don't need to make further changes.
         
         - XBee Development Board:
             * If you are using the XBee Development Board you will need to 
               update the IOLINE_IN constant accordingly. A couple of TODOs 
               within the application code will indicate you what to do.

         NOTE: It is recommended to verify the capabilities of the pins used 
               in the example in the product manual of your XBee Device to 
               ensure that everything is configured correctly.


  Running the example
  -------------------
  First, build and launch the application.
  To test its functionality follow these steps:
  
    1) Press the button corresponding to the digital input line. In the XBIB
       boards it is the DIO3; in the XBee Development boards is the DIO4 or
       User Button.
       
    2) Verify that the status of the LED corresponding to the digital output
       line changes. In the XBIB boards it is the DIO12; in the XBee Development
       boards is the DIO12 or User0/TCP.
       