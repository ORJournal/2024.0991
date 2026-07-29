package data_handler.figures;

/** One row of results/resultsOffline.csv. */
public final class OfflineResult {
    public static final int METHOD_MOSEK = 0;
    public static final int METHOD_DD = 1;
    public static final double TIME_LIMIT_SECONDS = 1800.0;
    public static final double MOSEK_GAP_TOLERANCE = 1.0e-4;

    private final ExperimentKey key;
    private final int method;
    private final double ddNodes;
    private final double ddArcs;
    private final double ddConstructionSeconds;
    private final double shortestPathSeconds;
    private final double solverSeconds;
    private final double shortestPathObjective;
    private final double verifiedShortestPathObjective;
    private final double solverObjective;
    private final double verifiedSolverObjective;
    private final double branchAndBoundNodes;
    private final double relativeGap;
    private final double maximumLayerWidth;
    private final double truncationMemory;
    private final double truncationMaximumWidth;
    private final double conditionNumber;

    public OfflineResult(ExperimentKey key, int method, double ddNodes,
            double ddArcs, double ddConstructionSeconds,
            double shortestPathSeconds, double solverSeconds,
            double shortestPathObjective, double verifiedShortestPathObjective,
            double solverObjective, double verifiedSolverObjective,
            double branchAndBoundNodes, double relativeGap,
            double maximumLayerWidth, double truncationMemory,
            double truncationMaximumWidth, double conditionNumber) {
        this.key = key;
        this.method = method;
        this.ddNodes = ddNodes;
        this.ddArcs = ddArcs;
        this.ddConstructionSeconds = ddConstructionSeconds;
        this.shortestPathSeconds = shortestPathSeconds;
        this.solverSeconds = solverSeconds;
        this.shortestPathObjective = shortestPathObjective;
        this.verifiedShortestPathObjective = verifiedShortestPathObjective;
        this.solverObjective = solverObjective;
        this.verifiedSolverObjective = verifiedSolverObjective;
        this.branchAndBoundNodes = branchAndBoundNodes;
        this.relativeGap = relativeGap;
        this.maximumLayerWidth = maximumLayerWidth;
        this.truncationMemory = truncationMemory;
        this.truncationMaximumWidth = truncationMaximumWidth;
        this.conditionNumber = conditionNumber;
    }

    public ExperimentKey getKey() { return key; }
    public int getMethod() { return method; }
    public double getDdNodes() { return ddNodes; }
    public double getDdArcs() { return ddArcs; }
    public double getDdConstructionSeconds() { return ddConstructionSeconds; }
    public double getShortestPathSeconds() { return shortestPathSeconds; }
    public double getSolverSeconds() { return solverSeconds; }
    public double getShortestPathObjective() { return shortestPathObjective; }
    public double getVerifiedShortestPathObjective() { return verifiedShortestPathObjective; }
    public double getSolverObjective() { return solverObjective; }
    public double getVerifiedSolverObjective() { return verifiedSolverObjective; }
    public double getBranchAndBoundNodes() { return branchAndBoundNodes; }
    public double getRelativeGap() { return relativeGap; }
    public double getMaximumLayerWidth() { return maximumLayerWidth; }
    public double getTruncationMemory() { return truncationMemory; }
    public double getTruncationMaximumWidth() { return truncationMaximumWidth; }
    public double getConditionNumber() { return conditionNumber; }

    /** DD uses construction time; MOSEK uses branch-and-bound solver time. */
    public double getFigureRuntimeSeconds() {
        return method == METHOD_MOSEK ? solverSeconds : ddConstructionSeconds;
    }

    /** Success rules used in the computational study. */
    public boolean isSolved() {
        if (method == METHOD_MOSEK) {
            return Double.isFinite(relativeGap)
                    && relativeGap < MOSEK_GAP_TOLERANCE;
        }
        return method == METHOD_DD && ddArcs > 0.0;
    }

    /** Unsolved runs are plotted at the 1800-second time limit. */
    public double getCappedFigureRuntimeSeconds() {
        if (!isSolved() || !Double.isFinite(getFigureRuntimeSeconds())) {
            return TIME_LIMIT_SECONDS;
        }
        return Math.max(0.0,
                Math.min(TIME_LIMIT_SECONDS, getFigureRuntimeSeconds()));
    }
}