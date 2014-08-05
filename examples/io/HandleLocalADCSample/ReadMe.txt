  Introduction
  ------------
  This is a sample Java application to show how to handle the local XBee
  analog input channels.
  
  The application configures an IO line of the XBee device as ADC 
  (potentiometer). Then periodically reads its value and prints it in the
  output console.


  Files
  ----------
    * com.digi.xbee.api.handlelocaladc.MainApp.java:
      Main application class. Instantiates an XBee device, establishes a 
      serial connection with it, configures the IO line and reads its analog 
      value.


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
       
    4) If you are using the XBIB-U-DEV instead of the XBee Development Board,
       connect a potentiometer to the DIO1 pin.


  Running the example
  -------------------
  First, build and launch the application.
  To test its functionality follow these steps:
  
    1) Rotate the potentiometer.
       
    2) Verify that the value displayed in the output console is changing.
       