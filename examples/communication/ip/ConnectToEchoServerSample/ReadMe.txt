  Introduction
  ------------
  This sample Java application shows how to connect an Internet radio module 
  to an echo server to send and receive data using the XBee Java Library. In 
  this example, data is sent to an echo server that echoes it back to be read 
  by the module. The application will block during the transmission and read 
  requests, but you will be notified if there is any error during the process.
  
  NOTE: This example uses the Cellular device (CellularDevice) class, but it 
        can be applied to other Internet capable XBee device classes such as
        WiFiDevice.


  Files
  -----
    * com.digi.xbee.api.connecttoechoserver.MainApp.java:
      Main application class. It instantiates a Cellular device and establishes 
      a serial connection with it. Then, sends the data to the echo server and 
      reads it back printing out the result of the process.


  Requirements
  ------------
  To run this example you will need:
  
    * One Internet capable XBee radio (for example a Cellular radio) in API 
      mode and its corresponding carrier board (XBIB or equivalent).
    * The XCTU application (available at www.digi.com/xctu).


  Example setup
  -------------
    1) Plug the Internet radio into the XBee adapter and connect it to your 
       computer's USB or serial port.
       
    2) Ensure that the module is in API mode and connected to the Internet.
       For further information on how to perform this task, read the 
       'Configuring Your XBee Modules' topic of the Getting Started guide.
       
    3) Set the port and baud rate of the XBee radio in the MainApp class.
       If you configured the module in the previous step with the XCTU, you 
       will see the port number and baud rate in the 'Port' label of the device 
       on the left view.
       
    4) Optionally, modify the text to be sent to the echo server changing the 
       value of the 'TEXT' variable.


  Running the example
  -------------------
  First, build and launch the application. When the application starts, it 
  connects to the echo server and sends the "Hello XBee!" message. To test 
  the functionality, check that the following message (among others) is 
  printed out in the console of the launched application:
    
    "Echo response received from 52.43.121.77:11001 >> '<TEXT>'"
    
   - Where <TEXT> is the text sent to the server and echoed back.
    
  That message indicates that the echo server echoed back the text sent and it 
  was read by the Internet radio successfully.
  