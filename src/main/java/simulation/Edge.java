package simulation;

public class Edge {
    private final RouterNode source;
    private final RouterNode target;
    private int latency;
    private int congestion;

    public Edge(RouterNode source, RouterNode target, int latency, int congestion) {
        this.source = source;
        this.target = target;
        this.latency = latency;
        this.congestion = congestion;
    }

    public RouterNode getSource() {
        return source;
    }

    public RouterNode getTarget() {
        return target;
    }

    public int getLatency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }

    public int getCongestion() {
        return congestion;
    }

    public void setCongestion(int congestion) {
        this.congestion = congestion;
    }
}