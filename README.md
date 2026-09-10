# Multi-Client Online Reservation System (Java Sockets)

A concurrent, multi-client reservation platform developed in Java SE using TCP Sockets for real-time network communication and thread synchronization.

---

## Overview
The system enables multiple clients to connect simultaneously to a central server, perform user authentication, and reserve or cancel time slots in real time. Once a slot is claimed by a user, it immediately becomes unavailable to all other connected clients.

---

## Features & Supported Operations
* **User Authentication:** Account registration and credential verification (Login).
* **Real-Time Booking:** Instant slot reservation with concurrency protection.
* **Reservation Management:** View active user bookings and cancel existing slots.
* **Dual Interface:** Supports both command-line interaction and Java Swing GUI.
* **Thread Safety:** Implements synchronized access across shared collections to eliminate race conditions and double-booking.

---

## System Architecture & Network Design

* **Transport Protocol:** TCP (Port 5000).
* **Concurrency Model:** Multi-threaded server architecture utilizing a worker thread (`Runnable`) per client connection.
* **I/O Streams:** Line-based text protocol using `BufferedReader` and `PrintWriter` over raw TCP sockets.
* **Data Consistency:** Synchronized blocks wrap shared user and reservation data structures to guarantee atomic updates across concurrent sessions.

---

## Components
* `Server.java`: Headless console server managing socket listener and threading logic.
* `ServerGUI.java`: Server variant featuring real-time graphical logging and connection monitoring.
* `ConsoleClient.java`: Terminal-based client with an asynchronous background listener thread for server events.
* `ClientGUI.java`: Graphical desktop client built with Java Swing.

---

## Getting Started

### Prerequisites
* Java Development Kit (JDK 17 or higher)

### Compilation
```bash
javac Server.java ConsoleClient.java ServerGUI.java ClientGUI.java
```

### Running the System
1. Start the Server:

```
java Server
# Or run ServerGUI for the graphical view
```

2. Start Client Instances:

```
java ConsoleClient
# Or run ClientGUI for the Swing desktop interface
```

Enter the server's IP address (e.g., 127.0.0.1 for localhost or the host's LAN IP).
