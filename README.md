# Real-Time Network Weakness Finder (Backend)
This project provides the backend services for a tool that simulates network failures and analyzes resilience. It handles graph processing, attack simulations, and streams results in real-time via WebSockets.
## You can access the frontend here 
`https://github.com/rahulaharma/netfortify_frontend`
## 🧠 What It Does
This backend service is designed to answer a critical question: "If some parts of a network fail, will the rest still work?"
It provides endpoints to:
* **Upload a Network**: Accept a file representing a connected system (e.g., computer networks, social graphs).
* **Simulate Attacks**: Systematically remove nodes using different strategies:
  * Random Failure: Simulates accidental outages.Targeted Attack: Removes the most connected nodes first, simulating a strategic attack.

* **Broadcast Results**: Stream real-time metrics over WebSockets as the simulation runs, allowing a client to visualize the network's degradation.


## ✨ Backend Features
* **File Upload API**: An endpoint to load network structures from simple edge-list text files.
* **Concurrent Simulations**: Asynchronous processing to handle attack simulations without blocking API requests.
* **Multiple Attack Strategies**: Logic for both random node removal and high-degree targeted attacks.
* **Real-Time Metrics**: Calculates the size of the Largest Connected Component (LCC) and the number of disconnected sub-graphs after each removal step.
* **WebSocket Broadcasting**: Streams simulation results via STOMP over WebSockets for consumption by any connected client.

## 🔧 Tech Stack

| **Component**       | **Technology**     | **Purpose**                                      |
|---------------------|--------------------|--------------------------------------------------|
| **Backend**         | Java 11+           | Core programming language                        |
|                     | Spring Boot        | Framework for building the application           |
|                     | Spring Web         | Creating REST APIs for file upload/simulation    |
|                     | Spring WebSocket   | Real-time communication with clients             |
|                     | Maven              | Project build and dependency management          |


## 📖 API Endpoints

### REST APIs

| **Endpoint**            | **Method** | **Description**                               | **Body / Parameters**                                                                 | **Success Response**                                           |
|-------------------------|------------|-----------------------------------------------|----------------------------------------------------------------------------------------|----------------------------------------------------------------|
| `/api/graph/upload`     | POST       | Uploads a network file.                       | `multipart/form-data` with key `file` holding the network text file.                  | `200 OK` with a text message, e.g., `"Graph loaded successfully."` |
| `/api/graph/simulate`   | POST       | Starts a simulation on the currently loaded graph. | Request Parameter: `strategy` (string). Values: `random` or `high-degree`.            | `200 OK` with a text message, e.g., `"Simulation started."`     |



# 📡 WebSocket Endpoint

| **Endpoint** | **Protocol**         | **Description**                                                  | **Subscription Topic** | **Message Payload (JSON)**                                                                                                                                       |
|--------------|----------------------|------------------------------------------------------------------|-------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `/ws`        | STOMP over WebSocket | Establishes a real-time connection for receiving simulation updates. | `/topic/metrics`        | `{ "nodesRemoved": 10, "percentageRemoved": 5.0, "largestConnectedComponentSize": 180, "numberOfComponents": 2, "status": "RUNNING" }` |


