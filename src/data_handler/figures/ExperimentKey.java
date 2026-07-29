package data_handler.figures;

import java.util.Objects;

/** Parameters uniquely identifying one offline experiment. */
public final class ExperimentKey {
    private final String dataset;
    private final int n;
    private final double mu;
    private final double lambda;
    private final int bandwidth;
    private final int minimumConsecutiveOnes;
    private final int seed;

    public ExperimentKey(String dataset, int n, double mu, double lambda,
            int bandwidth, int minimumConsecutiveOnes, int seed) {
        this.dataset = dataset;
        this.n = n;
        this.mu = mu;
        this.lambda = lambda;
        this.bandwidth = bandwidth;
        this.minimumConsecutiveOnes = minimumConsecutiveOnes;
        this.seed = seed;
    }

    public String getDataset() { return dataset; }
    public int getN() { return n; }
    public double getMu() { return mu; }
    public double getLambda() { return lambda; }
    public int getBandwidth() { return bandwidth; }
    public int getMinimumConsecutiveOnes() { return minimumConsecutiveOnes; }
    public int getSeed() { return seed; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ExperimentKey)) return false;
        ExperimentKey that = (ExperimentKey) other;
        return n == that.n && Double.compare(mu, that.mu) == 0
                && Double.compare(lambda, that.lambda) == 0
                && bandwidth == that.bandwidth
                && minimumConsecutiveOnes == that.minimumConsecutiveOnes
                && seed == that.seed && Objects.equals(dataset, that.dataset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataset, n, mu, lambda, bandwidth,
                minimumConsecutiveOnes, seed);
    }

    @Override
    public String toString() {
        return dataset + "|" + n + "|" + mu + "|" + lambda + "|"
                + bandwidth + "|" + minimumConsecutiveOnes + "|" + seed;
    }
}