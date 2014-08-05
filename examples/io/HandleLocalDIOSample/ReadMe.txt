  Introduction
  ------------
  This is a sample Java application to show how to handle the local XBee
  digital IOs.
  
  The application configures two IO lines of the XBee device, one as digital
  input (button) and the other one as digital output (LED). Then periodically 
  reads the status of the input line in order to change the status of the output 
  line.
  
  Thus, when you press the button corresponding to the input line, the status 
  of the LED will change.


  Files
  ----------
    * com.digi.xbee.api.handlelocaldio.MainApp.java:
      Main application class. Instantiates an XBee device, establishes a 
      serial connection with it, configures the IO lines and reads/sets the
      digital values.


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
       
    4) If you are using the XBee Development Board instead of the XBIB-U-DEV,
       comment the first occurrence of the constant IOLINE_IN and uncomment the
       second one in the MainApp class, as explained there.


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
       