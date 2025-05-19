package simulation;

public class RequestClassifier {

    public static int getPenalty(UserRequest request) {
        int penalty = 0;

        if ("Student".equalsIgnoreCase(request.getUserType())) {
            penalty += 2;
        }

        // Example: Add penalty for non-critical domains
        if (!isCriticalDomain(request.getDomain())) {
            penalty += 3;
        }

        return penalty;
    }

    public static boolean isCriticalDomain(String domain) {
        // Simple check - you can extend this
        return domain.contains("edu") || domain.contains("intranet") || domain.contains("gov") || domain.contains("net");
    }
}