# chat_program_java

## Features implemented:
### Client:
* Upon program start, the client attempts to connect to the chat server on the same host
* Once connected, the client listens for input from the server, and prints it to the terminal
* The client also creates another thread that waits for user input from the terminal, and sends it to the server

### Server:
* Once started listens for conenctions on port 8888
* creates a new thread for connected clients
* Each client handler thread listens for input from the client, and prints it to terminal

## Todo:
### Client:
* allow the clients to connect to a chat server that is not on localhost
* allow user to enter their name upon starting the program
* append name to the beginning of messages sent

### Server:
* Server should have a data structure to track current active connection sockets
* client handler threads should redirect input they recieve from their client to the output of every active client socket
