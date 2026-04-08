# chat_program_java

## Features implemented:
### Client:
* Upon client start, the user is prompted to enter an IP address
* The client will attempt to connect to a chat server at the IP address
* If a connection is not successfull, the user is promted to enter a new IP
* Upon connection to a chat server the client prompts the user to enter their name, and sends the name to the server
* The client then listens for input from the user, and sends it to the server. Typing quit exits the program.
* The client creates another thread that waits for input from the server, and prints it to the terminal.

### Server:
* Once started listens for conenctions on port 8888
* creates a new thread for each connected client
* Threads get the username of connected clients
* Threads then listen for input from the client until the connection is closed, and prints it to terminal

## Todo:
### Client:
*

### Server:
* Server should have a data structure to track current active connection sockets
* Server should hava a data structure to track the usernames associated with active client connections, handle duplicate names.
* client handler threads should redirect input they recieve from their client to the output of every active client socket
* threads should also append the name of the client to output messages
