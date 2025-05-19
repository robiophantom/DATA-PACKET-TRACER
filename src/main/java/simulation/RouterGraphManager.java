package simulation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RouterGraphManager {
    private final Map<String, RouterNode> nodes;
    private final List<Edge> edges;

    public RouterGraphManager() {
        this.nodes = new HashMap<>();
        this.edges = new ArrayList<>();
    }

    public void loadUserGraphData(String path) {
        try (FileReader reader = new FileReader(path)) {
            Gson gson = new Gson();
            Map<String, List<Map<String, Map<String, Object>>>> userGraphData = gson.fromJson(
                    reader,
                    new TypeToken<Map<String, List<Map<String, Map<String, Object>>>>>() {}.getType()
            );

            if (userGraphData == null || userGraphData.get("elements") == null) {
                return;
            }

            List<Map<String, Map<String, Object>>> elements = userGraphData.get("elements");

            // First, add all nodes
            for (Map<String, Map<String, Object>> element : elements) {
                Map<String, Object> data = element.get("data");
                if (data != null && data.containsKey("id") && !data.containsKey("source")) {
                    String id = (String) data.get("id");
                    String type = (String) data.get("type");
                    int priority = ((Number) data.get("priority")).intValue();
                    if (!nodes.containsKey(id)) {
                        addRouter(id, type, priority);
                        System.out.println("[RouterGraphManager] Loaded user node: " + id + " (" + type + ")");
                    }
                }
            }

            // Then, add all edges
            for (Map<String, Map<String, Object>> element : elements) {
                Map<String, Object> data = element.get("data");
                if (data != null && data.containsKey("source") && data.containsKey("target")) {
                    String sourceId = (String) data.get("source");
                    String targetId = (String) data.get("target");
                    int latency = ((Number) data.get("latency")).intValue();
                    int congestion = ((Number) data.get("congestion")).intValue();
                    if (nodes.containsKey(sourceId) && nodes.containsKey(targetId) &&
                            !edges.stream().anyMatch(e -> e.getSource().getId().equals(sourceId) && e.getTarget().getId().equals(targetId))) {
                        addConnection(sourceId, targetId, latency, congestion);
                        System.out.println("[RouterGraphManager] Loaded user edge: " + sourceId + " -> " + targetId);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[RouterGraphManager] No user graph data found or error loading: " + e.getMessage());
        }
    }

    public void addRouter(String id, String type, int priority) {
        nodes.put(id, new RouterNode(id, type, priority));
    }

    public void addConnection(String sourceId, String targetId, int latency, int congestion) {
        RouterNode source = nodes.get(sourceId);
        RouterNode target = nodes.get(targetId);
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source or Target router does not exist.");
        }
        edges.add(new Edge(source, target, latency, congestion));
    }

    public void removeNode(String nodeId) {
        nodes.remove(nodeId);
        edges.removeIf(edge -> edge.getSource().getId().equals(nodeId) || edge.getTarget().getId().equals(nodeId));
    }

    public void removeEdge(String sourceId, String targetId) {
        edges.removeIf(edge -> edge.getSource().getId().equals(sourceId) && edge.getTarget().getId().equals(targetId));
    }

    public Map<String, RouterNode> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges(String nodeId) {
        return edges.stream()
                .filter(edge -> edge.getSource().getId().equals(nodeId))
                .collect(Collectors.toList());
    }

    public List<Edge> getAllEdges() {
        return new ArrayList<>(edges);
    }

    public void exportToJSON(String path) {
        List<Map<String, Object>> elements = new ArrayList<>();

        for (RouterNode node : nodes.values()) {
            Map<String, Object> nodeData = new HashMap<>();
            nodeData.put("id", node.getId());
            nodeData.put("label", node.getId());
            nodeData.put("type", node.getType());
            nodeData.put("priority", node.getPriority());
            elements.add(Map.of("data", nodeData));
        }

        for (Edge edge : edges) {
            Map<String, Object> edgeData = new HashMap<>();
            edgeData.put("source", edge.getSource().getId());
            edgeData.put("target", edge.getTarget().getId());
            edgeData.put("latency", edge.getLatency());
            edgeData.put("congestion", edge.getCongestion());
            elements.add(Map.of("data", edgeData));
        }

        Map<String, List<Map<String, Object>>> graphData = Map.of("elements", elements);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(graphData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}