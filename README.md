# chat_program_java

## Features implemented:
### Client:
* Upon client start, the user is prompted to enter an IP address
* The client will attempt to connect to a chat server at the IP address
* If a connection is not successfull, the user is promted to enter a new IP
* Upon connection to a chat server, the client prompts the user to enter their name, and sends the name to the server
* The client then listens for input from the user, and sends it to the server. Typing quit exits the program.
* The client creates another thread that waits for input from the server, and prints it to the terminal.

### Server:
* Once started listens for conenctions on port 5000
* creates a new thread for each connected client
* Threads get the username of connected clients
* Threads then listen for input from the client until the connection is closed,
* Threads transmit input recieved from their client to every active client, and append username

## Todo:
All core functionalities are implemented, further todos are extra credit
### Client:
* GUI
* emojis

### Server:
*

