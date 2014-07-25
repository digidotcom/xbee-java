XBee Java Library
=================

Introduction
------------
This project contains the source code of the XBee Java Library which allows
to easily write Java applications for communicating with Digi International's
[XBee] wireless radio modules in API mode.

The project includes the Java source code for the library and also multiple
examples, also available in source code format, that show how to use the
available APIs.

The main features provided by the library are:

- Feature 1
- Feature 2
- etc

This source code has been contributed by [Digi International] under the Mozilla
Public License v2.0.

Contact [missing email] to report bugs, request features or contribute
code to this project.

[Digi International]: http://www.digi.com/
[XBee]: http://www.digi.com/xbee/
[missing email]: missing@email.digi.com


Supported modules
-----------------
- Module A
- Module B
- Module C (through hole and SMT)
- etc


Requirements
------------
The following software components are required to use the XBee Java Library:

- A computer running Windows, Mac OS X or Linux.
- [Java Development Kit] version 6 or greater.
- [RxTx] serial communication library.
- [XCTU], optional but highly recommended.

To run the Unit Tests, the following additional Software Package are also required:

- [Power Mockito] (tested with v1.5.5).
- Mockito (tested with v1.9.5).
- Java Assist (tested with v3.18.2).

Note that by installing Power Mockito from the provided link it already includes Mockito and Java Assist.

[XCTU]: www.digi.com/xctu
[Java Development Kit]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[RxTx]: http://www.jcontrol.org/download/files/rxtx-2.1-7-bins-r2.zip
[Power Mockito]: http://dl.bintray.com/johanhaleby/generic/powermock-mockito-junit-1.5.5.zip


Repository structure
--------------------
- src: contains the XBee Java Library source code.
- test: contains the XBee Java Library unit tests.
- examples: contains the several sample applications using the XBee Java Library organized in categories.


Installation
------------
Check out the project or download a zip file by clicking  on the "Download ZIP" button. If you downloaded
the zip file, uncompress it. In any case you will end with a directory called XBeeJavaLibrary-master,
containing all the project files and directories.

Download the rest of the Software components listed in the requirements section, if not already present
in your computer.


Creating, building and running an application
---------------------------------------------
Create a java project using your preferred development environment (Eclipse, NetBeans, ...).
The next steps describe the process using Eclipse.

- Start your Eclipse IDE and select a workspace location (by default "C:\Users\<username>\workspace"). 
- Click on "File > New > Project..." and select "Java Project".
- Enter the new project name, for example "XBeeJavaLibrary". 
- Uncheck the "Use default location" checkbox and click the "Browse" button that is now enabled.
  Navigate to the folder where you extracted the sources and click "OK". 
- Click "Finish". Your project should now appear in the "Package Explorer" view at the left side of the IDE.
- Right click on the project (XBeeJavaLibrary) and select "Properties...".
- Add the RxTx jar file to the libraries build path and configure it to use the corresponding native 
  libraries, for the OS you are using. For further information about how to add RxTx support to your 
  project, refer to [missing link].
- If you want to run the Unit Test framework included with the project, you also have to add the Mockito,
  Power Mockito and Java Assist libraries to your project. Otherwise, exclude the test directory from the 
  build path.
  For further information about how to use the Unit Test framework included with the XBee Java Library 
  project, refer to [missing link].

To build the project, follow this steps:

- From the Eclipse menu, select "Project > Build Project".

To configure and run any of the application examples, follow this steps:

- Read the Readme.txt file included in the directory of each example to modify the code as needed.
- Once the code is completed, right click on the MainApp.java file and select "Run as > Java application".
- The console should be automatically open and you will see there the application input/output. Follow the
  information provided in the Readme.txt to verify the functionality.

  
Documentation
-------------
Additional documentation can be found at [missing link].


License
-------
This software is open-source software.  Copyright Digi International, 2014.

This Source Code Form is subject to the terms of the Mozilla Public
License, v. 2.0. If a copy of the MPL was not distributed with this file,
You can obtain one at http://mozilla.org/MPL/2.0/.

