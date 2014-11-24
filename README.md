Java Android Messenger  
===========
The application is client/server chat application which can be used to chat between devices. 

Configuration
===========
The application at the start would require you to enter the IP and port on which to start 
the server or to connect the running server on other andoird device or dekstop computer. 

Limitation
===========
The application is having some issues connecting to the chat server running on mobile network due 
to firewall from the netwrok provider. However, the application would work just fine with WI-FI connection.

Technologies
===========
Andoird SDK, Java Socket Programming

Android Chat Application
----------
The application is divided in two packages 
1. com.chat.client : Contains the interface for the chat client
		- impl: Contains the implementation of the chat client
2. com.chat.common: Contains the common classes used by the application
		- data: Contains the classes related to data objects used in the application
		- utils: Contains the utilities and helper classes
3. com.chat.server: Contains the interface for the chat server 
		- impl: Contains the implementation for the chat server 
4. com.chat.messenger: Contains the android activity class for the UI

Class Specifications
-----------
Following are the details of the classes
1. Client.java: Implementation of the chat client and defines the logic to connect to the chat server and start/stop chat client.
2. ClientSvc.java: Interface which is implemented by the Client.java and used to abstract the common methods which should be accessible by the outside world. This will also help decoupling to the client implementation.
3. MsgBroker.java: A runnable thread which actually reads the messages from the chat server.
4. Message.java: The data object which is passed between the server and client.
5. EncryptionUtils.java: Contains the logic to encrypt and decrypt the messages to and fro between clients
6. Type.java: Enumeration class defining the type of messages
7. Helper.java: Specific to android application. This is the helper class which provide helper methods for the android application.
8. ActMessenger.java: Specific to andoird application. This is the main activity class which initialize the view and provide the user with different functionality to work with.
9. Server.java: Implementation of the chat server and provide logic to start/stop server.
10. MsgBroadcaster.java: Runnable thread used to read and write message to the entire client connected to the server.
11. ServerSvc.java: Interface implemented by the Server.java. Provide decoupling of the implementation actual logic and provide methods to start and stop the server to the outside world.
12. ChatClient: To create a client ui, connect to chat server and start the client to send/receive messages. This will also start the server if one does not exists on the IP provided.
13. ChatServer: To start the server as standalone.

Procedure to start application
-----------
NOTE: You can start the chat server either on desktop or android (on android WIFI network connection is supported as the network provider has fire-walled the network internet). 

Please note that WIFI is required for the android application to start of network is not provided then the application would not start.

Please note that if not client is connected to the server the chat server would stop. Also if the chat server is not found on the host and port you provided, the application would start the chat server on the same device/dektop.

1.	Download and install the android application on your device
2.	Start the ChatClient.java on your system.
3.	If you want to start the chat server on the desktop, then please do so by clicking Join button after providing the username. Please take note of the host and port as it is required by other clients to connect
4.	If you want to start the chat server on the android device, just press Join on the dialog after providing appropriate user name. Please take note of the host and port as it is required by other clients to connect. You can get that information in the options menu on the top by clicking an icon with “i”.
5.	If you want to start a standalone server you can start ChatServer.java and take note of the host and port, as this is required for other client to connect to the server.
6.	Once the server is started, please provide the host and port on which ever client you want to connect and start chanting. 
7.	On android you have option menu to check the active users, leave the chat at any-time or re-join the chat group.
