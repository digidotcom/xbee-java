  Introduction
  ------------
  This is a sample Java application to show how to send data asynchronously from
  the XBee device to all remote devices on the same network (broadcast) using 
  the XBee Java Library. Transmitting data asynchronously means the execution 
  will not be blocked during the transmit request.
  
  NOTE: This example uses the generic XBee device (XBeeDevice) class, 
        but it can be applied to any other local XBee device class.


  Files
  ----------
    * com.digi.xbee.api.sendbroadcastserialdataasync.MainApp.java:
      Main application class. It instantiates an XBee device, establishes a 
      serial connection with it and sends a broadcast transmission. Finally it 
      prints out the result of the sent operation.


  Requirements
  ------------
  To run this example you will need:
  
    * At least two XBee radios in API mode and their corresponding carrier
      board (XBIB or equivalent).
    * The XCTU application (available at www.digi.com/xctu).


  Example setup
  -------------
    1) Plug the XBee radios into the XBee adapters and connect them to your
       computer's USB or serial ports.
       
    2) Ensure that the modules are in API mode and on the same network.
       For further information on how to perform this task, go to [...]
       
    3) Set the port and baud rate of the sender XBee radio in the MainApp class.
       If you do not know the serial/USB port where your module is connected to,
       see [...]


  Running the example
  -------------------
  First, build the application. To test the functionality you need the second 
  device to be listening to broadcast data. To do this:
  
    a) Use the 'ReceiveBroadcastSerialDataSample' example included in the 
       library.
       Follow the instructions in the 'ReadMe' file to perform the task.
       
    b) Use the XCTU:
       
       1) Launch the XCTU application.
          
       2) Add the receiver XBee module to the XCTU, specifying its port 
          settings.
          
       3) Once the module is added, change to the 'Consoles' working mode and 
          open the serial connection so you can see the data when it is received.
          
       4) Launch the sample application, some data is sent to all remote modules
          and a line with the result of the operation is printed to the standard 
          output:
          
          Sending broadcast data >> 48 65 6C 6C 6F 20 58 42 65 65 73 21 | Hello XBees!... Success
          
          Also, in the XCTU console a new RX frame has been received. Select it 
          and review the details, some of the details will be similar to:
          
          - Start delimiter:         7E
          - Length:                  Depends on the XBee protocol.
          - Frame type:              Depends on the XBee protocol.
          - 64-bit source address:   The XBee sender's 64-bit address.
          - RF data/Received data:   48 65 6C 6C 6F 20 58 42 65 65 73 21
                                     Hello XBees!
          