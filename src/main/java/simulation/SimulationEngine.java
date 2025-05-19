package simulation;

import java.util.List;
import java.util.Random;

public class SimulationEngine {

    public static void main(String[] args) throws InterruptedException {
        RouterGraphManager graphManager = new RouterGraphManager();

        // Load user-added nodes and edges
        graphManager.loadUserGraphData("src/main/resources/visualization/userGraphData.json");

        // Initial graph setup
        if (!graphManager.getNodes().containsKey("R1")) graphManager.addRouter("R1", "Lab", 1);
        if (!graphManager.getNodes().containsKey("R2")) graphManager.addRouter("R2", "Classroom", 2);
        if (!graphManager.getNodes().containsKey("R3")) graphManager.addRouter("R3", "Library", 3);
        if (!graphManager.getNodes().containsKey("R4")) graphManager.addRouter("R4", "Cafeteria", 4);
        if (!graphManager.getNodes().containsKey("R5")) graphManager.addRouter("R5", "Admin", 2);
        if (!graphManager.getNodes().containsKey("R6")) graphManager.addRouter("R6", "DataCenter", 1);
        if (!graphManager.getNodes().containsKey("Internet")) graphManager.addRouter("Internet", "Gateway", 0);

        if (!graphManager.getAllEdges().stream().anyMatch(e -> e.getSource().getId().equals("R1") && e.getTarget().getId().equals("R2")))
            graphManager.addConnection("R1", "R2", 10, 1);
        if (!graphManager.getAllEdges().stream().anyMatch(e -> e.getSource().getId().equals("R2") && e.getTarget().getId().equals("R3")))
            graphManager.addConnection("R2", "R3", 8, 2);
        if (!graphManager.getAllEdges().stream().anyMatch(e -> e.getSource().getId().equals("R3") && e.getTarget().getId().equals("R4")))
            graphManager.addConnection("R3", "R4", 5, 1);
        if (!graphManager.getAllEdges().stream().anyMatch(e -> e.getSource().getId().equals("R4") && e.getTarget().getId().equals("Internet")))
            graphManager.addConnection("R4", "Internet", 15, 0);
        if (!graphManager.getAllEdges().stream().anyMatch(e -> e.getSource().getId().equals("R1") && e.getTarget().getId().equals("R3")))
            graphManager.addConnection("R1", "R3", 20, 3);
        if (!graphManager.getAllEdges().stream().anyMatch(e -> e.getSource().getId().equals("R2") && e.getTarget().getId().equals("Internet")))
            graphManager.addConnection("R2", "Internet", 25, 2);
        if (!graphManager.getAllEdges().stream().anyMatch(e -> e.getSource().getId().equals("R5") && e.getTarget().getId().equals("R1")))
            graphManager.addConnection("R5", "R1", 12, 1);
        if (!graphManager.getAllEdges().stream().anyMatch(e -> e.getSource().getId().equals("R5") && e.getTarget().getId().equals("R6")))
            graphManager.addConnection("R5", "R6", 7, 0);
        if (!graphManager.getAllEdges().stream().anyMatch(e -> e.getSource().getId().equals("R6") && e.getTarget().getId().equals("R4")))
            graphManager.addConnection("R6", "R4", 10, 2);
        if (!graphManager.getAllEdges().stream().anyMatch(e -> e.getSource().getId().equals("R6") && e.getTarget().getId().equals("Internet")))
            graphManager.addConnection("R6", "Internet", 18, 1);

        // Randomly modify the graph (congestion, latency, nodes, edges)
        updateGraphDynamically(graphManager);

        LoadBalancer loadBalancer = new LoadBalancer(graphManager);
        ProxySimulator proxy = new ProxySimulator(2);
        TrafficMonitor monitor = new TrafficMonitor(graphManager, 3);

        // Test cases
        RouterNode labNode = graphManager.getNodes().get("R1");
        if (labNode != null) {
            UserRequest studentLabRequest = new UserRequest(labNode, "Student", "intranet.edu");
            handleRequest(studentLabRequest, loadBalancer, proxy, monitor, "Internet");
        }

        RouterNode classroomNode = graphManager.getNodes().get("R2");
        if (classroomNode != null) {
            UserRequest studentClassroomRequest = new UserRequest(classroomNode, "Student", "intranet.edu");
            handleRequest(studentClassroomRequest, loadBalancer, proxy, monitor, "Internet");
        }

        RouterNode adminNode = graphManager.getNodes().get("R5");
        if (adminNode != null) {
            UserRequest staffAdminRequest = new UserRequest(adminNode, "Staff", "netflix.com");
            handleRequest(staffAdminRequest, loadBalancer, proxy, monitor, "Internet");
        }

        // Random requests
        Random rand = new Random();
        String[] domains = {"facebook.com", "intranet.edu", "youtube.com", "reddit.com", "netflix.com", "email.edu", "twitch.tv"};
        String[] userTypes = {"Student", "Staff"};
        String[] sourceRouterIds = graphManager.getNodes().keySet().toArray(new String[0]);

        for (int i = 0; i < 5; i++) {
            System.out.println("\n--- Random Request " + (i + 1) + " ---");
            String sourceId = sourceRouterIds[rand.nextInt(sourceRouterIds.length)];
            RouterNode startNode = graphManager.getNodes().get(sourceId);
            UserRequest request = new UserRequest(
                    startNode,
                    userTypes[rand.nextInt(userTypes.length)],
                    domains[rand.nextInt(domains.length)]
            );
            handleRequest(request, loadBalancer, proxy, monitor, "Internet");
            monitor.decayCongestion();
            Thread.sleep(2000);
        }

        // Export the updated graph to data.json
        graphManager.exportToJSON("src/main/resources/visualization/data.json");
    }

    private static void updateGraphDynamically(RouterGraphManager graphManager) {
        Random rand = new Random();
        String[] nodeTypes = {"Lab", "Classroom", "Library", "Cafeteria", "Admin", "DataCenter", "Server"};

        // Update congestion and latency for existing edges
        for (Edge edge : graphManager.getAllEdges()) {
            int newLatency = Math.max(1, edge.getLatency() + (rand.nextInt(11) - 5)); // +/- 5
            int newCongestion = Math.max(0, edge.getCongestion() + (rand.nextInt(5) - 2)); // +/- 2
            edge.setLatency(newLatency);
            edge.setCongestion(newCongestion);
            System.out.println("[SimulationEngine] Updated edge " + edge.getSource().getId() + " -> " + edge.getTarget().getId() + ": Latency=" + newLatency + ", Congestion=" + newCongestion);
        }

        // Get current nodes
        List<String> nodeIds = new java.util.ArrayList<>(graphManager.getNodes().keySet());

        // Randomly add or remove a node (50% chance to add, 50% chance to remove)
        if (rand.nextBoolean() && nodeIds.size() > 3) { // Remove a node (ensure at least 3 nodes remain)
            List<String> removableNodes = nodeIds.stream()
                    .filter(id -> !id.equals("Internet") && !graphManager.getNodes().get(id).getType().equals("Gateway"))
                    .collect(java.util.stream.Collectors.toList());
            if (!removableNodes.isEmpty()) {
                String nodeToRemove = removableNodes.get(rand.nextInt(removableNodes.size()));
                graphManager.removeNode(nodeToRemove);
                System.out.println("[SimulationEngine] Removed node: " + nodeToRemove);
            }
        } else { // Add a node
            String newNodeId = "R" + (nodeIds.size() + 1);
            String newNodeType = nodeTypes[rand.nextInt(nodeTypes.length)];
            int priority = rand.nextInt(5);
            graphManager.addRouter(newNodeId, newNodeType, priority);
            System.out.println("[SimulationEngine] Added node: " + newNodeId + " (" + newNodeType + ")");
        }

        // Update node list after addition/removal
        nodeIds = new java.util.ArrayList<>(graphManager.getNodes().keySet());

        // Randomly add or remove an edge (50% chance to add, 50% chance to remove)
        List<Edge> currentEdges = graphManager.getAllEdges();
        if (rand.nextBoolean() && currentEdges.size() > 1) { // Remove an edge (ensure at least 1 edge remains)
            Edge edgeToRemove = currentEdges.get(rand.nextInt(currentEdges.size()));
            graphManager.removeEdge(edgeToRemove.getSource().getId(), edgeToRemove.getTarget().getId());
            System.out.println("[SimulationEngine] Removed edge: " + edgeToRemove.getSource().getId() + " -> " + edgeToRemove.getTarget().getId());
        } else if (nodeIds.size() >= 2) { // Add an edge
            String selectedSource, selectedTarget;
            boolean edgeExists;
            do {
                selectedSource = nodeIds.get(rand.nextInt(nodeIds.size()));
                selectedTarget = nodeIds.get(rand.nextInt(nodeIds.size()));
                final String sourceForLambda = selectedSource;
                final String targetForLambda = selectedTarget;
                edgeExists = graphManager.getAllEdges().stream()
                        .anyMatch(edge -> edge.getSource().getId().equals(sourceForLambda) && edge.getTarget().getId().equals(targetForLambda));
            } while (selectedSource.equals(selectedTarget) || edgeExists);
            int latency = rand.nextInt(20) + 1;
            int congestion = rand.nextInt(5);
            graphManager.addConnection(selectedSource, selectedTarget, latency, congestion);
            System.out.println("[SimulationEngine] Added edge: " + selectedSource + " -> " + selectedTarget + " (Latency=" + latency + ", Congestion=" + congestion + ")");
        }
    }

    private static void handleRequest(UserRequest request, LoadBalancer loadBalancer, ProxySimulator proxy, TrafficMonitor monitor, String destination) throws InterruptedException {
        System.out.println("\n--- Handling Request ---");
        System.out.println("From: " + request.getSourceRouter().getId() + " (" + request.getSourceRouter().getType() + "), User: " + request.getUserType() + ", Domain: " + request.getDomain());

        List<String> path = loadBalancer.getOptimalPath(request, destination);
        if (path.isEmpty()) {
            System.out.println("No path found.");
            return;
        }
        System.out.println("Selected Path: " + path);

        boolean handled = proxy.handleRequest(request);
        if (!handled) {
            System.out.println("Request rerouted due to overload.");
            return;
        }

        monitor.applyTrafficToPath(path);
        Thread.sleep(2000);
        proxy.decayLoad();
    }
}