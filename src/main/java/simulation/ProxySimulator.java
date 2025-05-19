package simulation;

import java.util.HashSet;
import java.util.Set;

public class ProxySimulator {
    private final int maxCapacity;
    private int currentLoad;
    private final Set<String> cache;

    public ProxySimulator(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.currentLoad = 0;
        this.cache = new HashSet<>();
        // Pre-cache intranet.edu for Students in Classroom or Lab
        this.cache.add("intranet.edu");
    }

    public boolean handleRequest(UserRequest request) {
        String domain = request.getDomain();
        String userType = request.getUserType();
        String routerType = request.getSourceRouter().getType();

        // Special case: Student accessing intranet.edu from Classroom or Lab
        boolean isStudentInClassroomOrLab = userType.equals("Student") &&
                (routerType.equals("Classroom") || routerType.equals("Lab")) &&
                domain.equals("intranet.edu");

        if (isStudentInClassroomOrLab) {
            System.out.println("[Proxy] Cache HIT for: " + domain + " (Student in " + routerType + ")");
            return true; // Always a cache hit, no load increase
        }

        // General case: Check cache and load
        if (cache.contains(domain)) {
            System.out.println("[Proxy] Cache HIT for: " + domain);
            return true;
        }

        if (currentLoad >= maxCapacity) {
            System.out.println("[Proxy] Proxy overloaded! Rerouting required.");
            return false;
        }

        cache.add(domain);
        currentLoad++;
        System.out.println("[Proxy] Cache MISS for: " + domain);
        return true;
    }

    // For simulation decay
    public void decayLoad() {
        if (currentLoad > 0) {
            currentLoad--;
            System.out.println("[Proxy] Load decayed. Current load: " + currentLoad);
        }
    }
}