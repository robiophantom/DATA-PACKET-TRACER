package simulation;

import java.util.List;

public class TrafficMonitor {
    private final RouterGraphManager graphManager;

    public TrafficMonitor(RouterGraphManager graphManager, int congestionThreshold) {
        this.graphManager = graphManager;
    }

    public void applyTrafficToPath(List<String> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            String sourceId = path.get(i);
            String targetId = path.get(i + 1);
            for (Edge edge : graphManager.getEdges(sourceId)) {
                if (edge.getTarget().getId().equals(targetId)) {
                    int newCongestion = edge.getCongestion() + 1;
                    edge.setCongestion(newCongestion);
                    System.out.println("[TrafficMonitor] Updated congestion on edge " + sourceId + " -> " + targetId + ": " + newCongestion);
                    break;
                }
            }
        }
    }

    public void decayCongestion() {
        for (Edge edge : graphManager.getAllEdges()) {
            int currentCongestion = edge.getCongestion();
            if (currentCongestion > 0) {
                edge.setCongestion(currentCongestion - 1);
                System.out.println("[TrafficMonitor] Decayed congestion on edge " + edge.getSource().getId() + " -> " + edge.getTarget().getId() + ": " + (currentCongestion - 1));
            }
        }
    }
}