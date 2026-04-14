# chat_program_java

## How to Run:
### Server:
compile javac Server.java
run java Server

### GUI Client:
compile javac Client.java
compile javac ClientGUI.java
run java ClientGUI



## Features implemented:
### Client:
* Client can hit "new connection" button in the file menu to initiate a conenction to a chat server using a username and IP
* The client will attempt to connect to a chat server at the IP address
* If a connection is not successfull, the client is notified and allowed to try again
* Upon connection to a chat server, a thread is created so messages broadcast from the server are printed to the GUI output area
* The client then is also able to put messages in the input area, and hit the send button to send the message to the server

### Server:
* Once started listens for conenctions on port 5000
* creates a new thread for each connected client
* Threads get the username of connected clients
* Threads then listen for input from the client until the connection is closed,
* Threads transmit input recieved from their client to every active client, and append username
* messages in the format @<username> <message> are treated as direct messages, and only sent to the specified client
* parts in the message in the format :emoji_name: have emojis inserted into the message

