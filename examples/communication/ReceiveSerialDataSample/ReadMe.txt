  Introduction
  ------------
  This is a sample Java application to show how to receive data packets from 
  another XBee device on the same network.
  
  The application prints the received data to the standard output in ASCII and 
  hexadecimal formats after the sender address.


  Files
  ----------
    * com.digi.xbee.api.receiveserialdata.MainApp.java:
      Main application class. Instantiates an XBee device and establishes a 
      serial connection with it.
      
    * com.digi.xbee.api.receiveserialdata.MyPacketReceiveListener.java:
      Class that handles the received data packets.


  Requirements
  ------------
  To run this example you will need:
  
    * At least two XBee radios in API mode and their corresponding carrier
      board (XBIB or equivalent).
    * The XCTU application (available at www.digi.com/xctu).


  Example setup
  -------------
    1) Look for the 64-bit address labeled on the back of the device, a 16 
       character string that follows the format 0013A20040XXXXXX.
       
    2) Plug the XBee radios into the XBee adapters and connect them to your
       computer's USB or serial ports.
       
    3) Ensure that the modules are in API mode and on the same network.
       For further information on how to perform this task, go to [...]
       
    4) Set the port and baud rate of the receiver XBee radio in the MainApp 
       class.
       If you do not know the serial/USB port where your module is connected to,
       see [...]


  Running the example
  -------------------
  First, build and launch the application.
  To test its functionality you need other device on the network to send unicast 
  data to the receiver module. If you want to send data to this module, follow 
  these steps:
  
    a) Use the 'SendSerialDataSample' example included in the library.
       Follow the instructions on its 'ReadMe' file to perform the task.
       
    b) Use the XCTU:
       
       1) Launch the XCTU application.
          
       2) Add the sender XBee module to the XCTU, specifying its port settings.
          
       3) Once the module is added, change to the 'Consoles' working mode and 
          open the serial connection.
          
       4) Create and add a frame using the 'Frames Generator' tool with the 
          following parameters:
          
          - Protocol:                               Select the protocol of your device.
          - Frame type:                             Select a 64-bit Transmit Request frame.
          - Frame ID:                               01
          - 64-bit dest. address:                   Use the 64-bit address you copied before.
          - 16-bit dest. address (only if present): FF FE
          - Broadcast radius (only if present):     00
          - Options:                                00
          - RF data (ASCII):                        Hello XBee!
          
       5) Send this frame and check the launched application output console: 
          The packet has been received and a line containing the data included 
          in the 'RF data' field of the sent frame is printed out:
          
          From 0013A20040XXXXXX >> 48 65 6C 6C 6F 20 58 42 65 65 21 | Hello XBee!
          