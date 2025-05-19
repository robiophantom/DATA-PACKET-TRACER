package simulation;

public class RouterNode {
    private final String id;
    private final String type;
    private final int priority;

    public RouterNode(String id, String type, int priority) {
        this.id = id;
        this.type = type;
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getPriority() {
        return priority;
    }
}