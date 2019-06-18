  Introduction
  ------------
  This sample Java application shows how to receive and process data coming
  from the MicroPython interface of the XBee device using the XBee Java Library.

  The application registers a listener to be notified when new data coming from
  MicroPython is received and prints it to the standard output.

  NOTE: This example uses the generic XBee device (XBeeDevice) class,
        but it can be applied to any other local XBee device class.


  Files
  -----
    * com.digi.xbee.api.receivemicropythondata.MainApp.java:
      Main application class. It instantiates an XBee device, establishes a
      serial connection with it and registers the listener.

    * com.digi.xbee.api.receivemicropythondata.MyMicroPythonDataReceiveListener.java:
      Class that handles the received data from the MicroPython interface.


  Requirements
  ------------
  To run this example you will need:

    * One XBee3 module in API mode and its corresponding carrier board (XBIB
      or equivalent).
    * The XCTU application (available at www.digi.com/xctu).
    * The PyCharm IDE with the Digi XBee MicroPython plugin installed.


  Compatible protocols
  --------------------
    * 802.15.4
    * Cellular
    * DigiMesh
    * Zigbee


  Example setup
  -------------
    1) Plug the XBee radio into the XBee adapter and connect it to your
       computer's USB or serial port.

    2) Ensure that the module is in API mode.
       For further information on how to perform this task, read the
       'Configuring Your XBee Modules' topic of the Getting Started guide.

    3) Set the port and baud rate of the XBee radio in the MainApp class.
       If you configured the module in the previous step with XCTU, you
       will see the port number and baud rate in the 'Port' label of the
       device on the left view.


  Running the example
  -------------------
  First, build the application. Then, you need to run the MicroPython
  'relay_frames_button' sample in the module. Follow these steps to do so:

    1) Launch PyCharm.

    2) In the Welcome screen, click the 'Import XBee MicroPython Sample Project'
       option and import the 'Relay Frames Button' sample (XBEE > COMMUNICATION).

    3) Run the sample project.
       NOTE: when asked to enable the MicroPython REPL mode, click No.

  Finally, launch this sample application. To verify that the communication
  works properly, press the SW2 button (if you have an XBIB-U-DEV carrier board)
  or the Comm DIO0 one (if you have an XBIB-C). The application should print
  the following line in the standard output:

    Data received from MicroPython >> 42 75 74 74 6F 6E 20 70 72 65 73 73 65 64 | Button pressed

  Every time you press that button, the MicroPython application will send a User
  Data Relay frame to the XBee serial interface.
