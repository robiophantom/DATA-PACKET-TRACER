SmartTrafficManager
SmartTrafficManager is a network traffic simulation system that models a dynamic network of routers, edges (connections), and user requests. It combines a Java backend for simulation and a JavaScript frontend for visualization using Cytoscape.js.

📌 Features
Dynamic Network Simulation: Add, remove, or update routers and connections.

Traffic Prioritization: Differentiates requests by domain (e.g., .edu) and user type (student/staff).

Load Balancing: Uses Dijkstra’s algorithm considering latency, congestion, and user-based penalties.

Interactive Visualization: Displays real-time network status including congestion and routing.

🧩 Project Structure
🔧 Backend (Java)
Core Classes

Class	Description
RouterNode.java	Represents a router (ID, type, priority).
Edge.java	Models a connection (source, destination, latency, congestion).
RouterGraphManager.java	Manages nodes and edges, and exports to JSON.
LoadBalancer.java	Implements Dijkstra’s algorithm with traffic rules.
ProxySimulator.java	Simulates cache hits/misses with user-based rules.
TrafficMonitor.java	Updates and decays congestion over time.
SimulationEngine.java	Initializes the network, simulates traffic, and exports data to frontend.

🌐 Frontend (HTML/JS/CSS)
File	Description
index.html	Graph interface with simulation controls and stats.
style.css	Styles for graph, buttons, and table.
data.json	Backend-generated data for network visualization.

Key JavaScript Functions:

simulateRequest(): Sends a simulated request and updates stats.

refreshGraphManually(): Reloads the graph from data.json.

initializeGraph(): Draws the network graph using Cytoscape.js.

⚙️ Simulation Workflow
🛠 Backend
Initialization

Create routers (e.g., R1, R2, Internet).

Define edges with latency and congestion values.

Dynamic Updates

Random latency/congestion changes.

Add/remove routers and edges.

Request Handling

Uses Dijkstra’s algorithm to compute optimal path.

Updates congestion and exports to data.json.

💻 Frontend
Graph Rendering

Loads data.json into Cytoscape.js.

Color-coded nodes (e.g., red star = Gateway).

Edge labels show latency, red color if congestion > 2.

User Interaction

Select router, user type, and domain to simulate.

Path, latency, and congestion are shown in a table.

Blue-highlighted edges for active paths (3 seconds).

✅ Test Cases
Backend Tests
Student in Lab accessing intranet.edu

Expected: Cache HIT, path via Internet.

Output: [Proxy] Cache HIT for: intranet.edu (Student in Lab)

Staff accessing netflix.com

Expected: Penalty +5, cache MISS.

Output: [Proxy] Cache MISS for: netflix.com

Dynamic Updates

Output:

csharp
Copy
Edit
[SimulationEngine] Added node: R8 (Server)
[SimulationEngine] Updated edge R1 -> R2: Latency=15, Congestion=3
Frontend Tests
Manual Graph Refresh

Action: Click "Refresh Graph"

Expected: Updated topology appears (e.g., node R8).

Invalid Source Node

Action: Select removed node (e.g., R4)

Expected: Alert: "Source router no longer exists in the graph."

Path Highlighting

Expected: Request path edges turn blue for 3 seconds.

🚀 How to Run
Backend
bash
Copy
Edit
javac SimulationEngine.java
java SimulationEngine
Output: Generates/updates data.json.

Frontend
bash
Copy
Edit
cd src/main/resources/visualization
npx http-server
Open in browser: http://localhost:8080/index.html

🛠 Future Enhancements
Backend
Integrate with Spring Boot for real-time updates via API.

Frontend
Add "Clear Stats Table" button.

Show loading indicators while refreshing.

Advanced Simulation
Simulate real-world traffic behavior (e.g., peak hours, denial of service).

📖 Key Takeaways
The backend dynamically manages routing, congestion, and proxy caching.

The frontend visualizes and interacts with live traffic data.

The system is testable and extensible, making it suitable for further research and enhancement in smart network systems.
