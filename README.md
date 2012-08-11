Instructions to run application

1. Make sure Maven is installed and setup to be available on your %PATH% environment variable.

2. Open up a terminal screen and go to the root directory of the application which will be referred to as %PROJECT_HOME%, and run the following Maven command.

$ mvn assembly:assembly.

This will assemble a standalone jar with all dependencies called simple-client-server-messaging-1.0-jar-with-dependencies.jar.

3. In the current terminal start up the server with the following commands.

$ cd %PROJECT_HOME%/target
$ java -cp simple-client-server-messaging-1.0-jar-with-dependencies.jar me.nickcarroll.server.Server

4. Open up a new terminal and start up the client with the following commands.

$ cd %PROJECT_HOME%/target
$ java -cp simple-client-server-messaging-1.0-jar-with-dependencies.jar me.nickcarroll.client.Client

5. In the terminal with the Client running, enter the number of messages to send from the command line and press enter.  This will execute the program.  When all the messages have been received and logged, both the Server and Client will shutdown.