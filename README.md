# chat_program_java

## Features implemented:
### Client:
* Upon program start, the client attempts to connect to the chat server on the same host
* The client prompts the user to enter their name, and sends the name to the server
* The client listens for input from the server, and prints it to terminal
* The client creates another thread that waits for user input from the terminal, and sends it to the server

### Server:
* Once started listens for conenctions on port 8888
* creates a new thread for each connected client
* Threads get the username of connected clients
* Threads then listen for input from the client until the connection is closed, and prints it to terminal

## Todo:
### Client:
* allow the clients to choose an IP address, and connect to a chat server that is not on localhost

### Server:
* Server should have a data structure to track current active connection sockets
* Server should hava a data structure to track the usernames associated with active client connections, handle duplicate names.
* client handler threads should redirect input they recieve from their client to the output of every active client socket
* threads should also append the name of the client to output messages
