# Multi-Threaded Online Auction System

## Project Overview
This project implements a **multi-threaded online auction system** using **Java sockets and TCP**. The system consists of a **multi-threaded server** that manages auction items and client connections, and a **client application** that allows users to interact with the auction. Users can view available items, add new items, and place bids in real time. The server ensures concurrency control using a **thread pool executor**, logs client requests, and maintains bid history.

## Features
- **Multi-threaded server** using a fixed thread pool to handle multiple clients concurrently.
- **Client-server communication** via TCP sockets.
- **Command-based interaction** for users to list, add, and bid on items.
- **Bid tracking system**, storing the highest bid and bidder’s IP address.
- **Logging system** that records all client requests in `log.txt`.

## Technologies Used
- Java
- Java Sockets (TCP)
- Multi-threading (ExecutorService)
- File Handling (Logging requests to a text file)

## Installation & Execution
### 1. Compile the Server and Client
```sh
javac Server.java
javac Client.java
```

### 2. Run the Server
```sh
java Server
```

### 3. Run the Client with Commands
#### Show all auction items:
```sh
java Client show
```

#### Add an item to the auction:
```sh
java Client item <itemname>
```
Example:
```sh
java Client item Laptop
```

#### Place a bid on an item:
```sh
java Client bid <itemname> <bidamount>
```
Example:
```sh
java Client bid Laptop 500.0
```

## How It Works
### Server Functionality
1. **Listens on port 6500** for incoming client connections.
2. **Manages concurrent clients** using a **thread pool** (30 threads max).
3. **Handles commands** sent by clients (`show`, `item`, `bid`).
4. **Stores auction data** (items and bids) in a **HashMap**.
5. **Writes logs** for each client request in `log.txt`.
6. **Processes bid validation** to ensure only valid bids are accepted.

### Client Functionality
1. **Connects to the server** via TCP sockets.
2. **Sends commands** (`show`, `item <itemname>`, `bid <itemname> <bidamount>`).
3. **Receives and displays server responses**.
4. **Validates user input** to prevent invalid operations.

## Example Output
### Server Output
```
Server listening to port: 6500
Client connected: 127.0.0.1
```

### Client Output
#### Adding an item:
```
Success.
```
#### Placing a bid:
```
Accepted.
```
#### Viewing auction items:
```
Laptop : 500.0 : 127.0.0.1
```

## Error Handling
- **Invalid commands:** Client receives an error message.
- **Duplicate items:** Server prevents adding items with the same name.
- **Invalid bids:** Bids must be greater than the current highest bid and greater than 0.
- **Connection issues:** Client displays an error if the server is unreachable.

## Log File (`log.txt`)
Each client request is logged in the following format:
```
dd-MM-yyyy|HH:mm:ss|client IP address|java Client [request]
```
Example:
```
10-02-2025|12:45:30|127.0.0.1|java Client bid Laptop 500.0
```

## Conclusion
This project demonstrates **multi-threaded server design**, **network communication using Java sockets**, and **real-time auction management**. It provides a robust and scalable framework for handling multiple users in an online auction system.
