package simulation;

import java.util.*;

public class LoadBalancer {
    private final RouterGraphManager graphManager;

    public LoadBalancer(RouterGraphManager graphManager) {
        this.graphManager = graphManager;
    }

    public List<String> getOptimalPath(UserRequest request, String destinationId) {
        String startId = request.getSourceRouter().getId();
        int penalty = calculatePenalty(request);

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<NodeDistance> queue = new PriorityQueue<>(Comparator.comparingInt(nd -> nd.distance));
        Set<String> visited = new HashSet<>();

        for (RouterNode node : graphManager.getNodes().values()) {
            distances.put(node.getId(), Integer.MAX_VALUE);
        }
        distances.put(startId, 0);
        queue.add(new NodeDistance(startId, 0));

        while (!queue.isEmpty()) {
            NodeDistance current = queue.poll();
            String currentId = current.nodeId;

            if (visited.contains(currentId)) continue;
            visited.add(currentId);

            if (currentId.equals(destinationId)) break;

            for (Edge edge : graphManager.getEdges(currentId)) {
                String neighborId = edge.getTarget().getId();
                if (visited.contains(neighborId)) continue;

                int edgeWeight = edge.getLatency() + edge.getCongestion() + penalty;
                int newDistance = distances.get(currentId) + edgeWeight;

                if (newDistance < distances.get(neighborId)) {
                    distances.put(neighborId, newDistance);
                    previous.put(neighborId, currentId);
                    queue.add(new NodeDistance(neighborId, newDistance));
                }
            }
        }

        List<String> path = new ArrayList<>();
        String step = destinationId;
        while (step != null) {
            path.add(step);
            step = previous.get(step);
        }
        Collections.reverse(path);

        if (!path.isEmpty() && path.get(0).equals(startId)) {
            return path;
        }
        return new ArrayList<>();
    }

    private int calculatePenalty(UserRequest request) {
        String userType = request.getUserType();
        String domain = request.getDomain();
        int penalty = 0;

        if (userType.equals("Student")) {
            penalty += 2;
        }

        if (userType.equals("Staff") && !domain.contains(".edu")) {
            penalty += 5;
        }

        if (!domain.contains("edu") && !domain.contains("intranet")) {
            penalty += 3;
        }

        return penalty;
    }

    private static class NodeDistance {
        String nodeId;
        int distance;

        NodeDistance(String nodeId, int distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }
    }
}