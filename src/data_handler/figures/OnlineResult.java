package data_handler.figures;

/** One populated row of results/resultsOnline.csv. */
public final class OnlineResult {
    public static final int METHOD_DD = 1;

    private final String dataset;
    private final int totalPeriods;
    private final double mu;
    private final double lambda;
    private final int bandwidth;
    private final int minimumConsecutiveOnes;
    private final int horizon;
    private final int seed;
    private final int method;
    private final double ddNodes;
    private final double ddArcs;
    private final double ddConstructionSeconds;
    private final double totalShortestPathSeconds;
    private final double solverSeconds;

    public OnlineResult(String dataset, int totalPeriods, double mu,
            double lambda, int bandwidth, int minimumConsecutiveOnes,
            int horizon, int seed, int method, double ddNodes, double ddArcs,
            double ddConstructionSeconds, double totalShortestPathSeconds,
            double solverSeconds) {
        this.dataset = dataset;
        this.totalPeriods = totalPeriods;
        this.mu = mu;
        this.lambda = lambda;
        this.bandwidth = bandwidth;
        this.minimumConsecutiveOnes = minimumConsecutiveOnes;
        this.horizon = horizon;
        this.seed = seed;
        this.method = method;
        this.ddNodes = ddNodes;
        this.ddArcs = ddArcs;
        this.ddConstructionSeconds = ddConstructionSeconds;
        this.totalShortestPathSeconds = totalShortestPathSeconds;
        this.solverSeconds = solverSeconds;
        if (getWindowCount() <= 0) {
            throw new IllegalArgumentException("total periods must be at least the horizon");
        }
    }

    public String getDataset() { return dataset; }
    public int getTotalPeriods() { return totalPeriods; }
    public double getMu() { return mu; }
    public double getLambda() { return lambda; }
    public int getBandwidth() { return bandwidth; }
    public int getMinimumConsecutiveOnes() { return minimumConsecutiveOnes; }
    public int getHorizon() { return horizon; }
    public int getSeed() { return seed; }
    public int getMethod() { return method; }
    public double getDdNodes() { return ddNodes; }
    public double getDdArcs() { return ddArcs; }
    public double getDdConstructionSeconds() { return ddConstructionSeconds; }
    public double getTotalShortestPathSeconds() { return totalShortestPathSeconds; }
    public double getSolverSeconds() { return solverSeconds; }

    /** Number of rolling horizons solved by MINLPDDOnline's inclusive loop. */
    public int getWindowCount() {
        return totalPeriods - horizon + 1;
    }

    /** Average online shortest-path time used by Figure 1. */
    public double getAverageMilliseconds() {
        return totalShortestPathSeconds * 1000.0 / getWindowCount();
    }

    public String experimentIdentity() {
        return dataset + "|" + totalPeriods + "|" + mu + "|" + lambda
                + "|" + bandwidth + "|" + minimumConsecutiveOnes + "|"
                + horizon + "|" + seed + "|" + method;
    }
}