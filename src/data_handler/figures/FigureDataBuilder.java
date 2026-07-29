package data_handler.figures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

/** Selects the experiments used by Figures 5-7 and pairs MOSEK/DD rows. */
public final class FigureDataBuilder {
    public static final List<Double> FIGURE_6_MU_VALUES = List.of(
            0.001, 0.005, 0.010, 0.020, 0.050);

    private static final Set<Integer> PAPER_N = Set.of(25, 50, 100, 200, 300, 500);
    private static final Set<Double> PAPER_MU = Set.of(
            0.001, 0.005, 0.010, 0.020, 0.050, 0.100);
    private static final Set<Double> PAPER_LAMBDA = Set.of(0.25, 0.5, 1.0, 2.0, 5.0);
    private static final Set<Integer> PAPER_BANDWIDTH = Set.of(2, 3);
    private static final Set<Integer> PAPER_TAU = Set.of(0, 5, 10);
    private static final Set<Integer> PAPER_SEED = Set.of(101, 102, 103, 104, 105);

    public FigureData build(List<OfflineResult> allRows) {
        Pairing pairing = pair(allRows, this::isFigure5Candidate);
        List<ExperimentPair> figure5 = sorted(pairing.completePairs);
        Map<Double, List<ExperimentPair>> figure6 = new LinkedHashMap<>();
        for (double mu : FIGURE_6_MU_VALUES) {
            figure6.put(mu, filterPairs(figure5, pair ->
                    pair.getKey().getN() == 200
                    && Double.compare(pair.getKey().getMu(), mu) == 0));
        }
        List<ExperimentPair> figure7a = filterPairs(figure5, pair ->
                pair.getKey().getN() == 200
                && pair.getKey().getMinimumConsecutiveOnes() == 0);
        List<ExperimentPair> figure7b = filterPairs(figure5, pair ->
                pair.getKey().getN() == 200
                && (pair.getKey().getMinimumConsecutiveOnes() == 5
                || pair.getKey().getMinimumConsecutiveOnes() == 10));
        return new FigureData(allRows.size(), figure5, pairing.unpairedRows,
                figure6, figure7a, figure7b);
    }

    private boolean isFigure5Candidate(OfflineResult result) {
        ExperimentKey key = result.getKey();
        return PAPER_N.contains(key.getN()) && PAPER_MU.contains(key.getMu())
                && PAPER_LAMBDA.contains(key.getLambda())
                && PAPER_BANDWIDTH.contains(key.getBandwidth())
                && PAPER_TAU.contains(key.getMinimumConsecutiveOnes())
                && PAPER_SEED.contains(key.getSeed());
    }

    private static Pairing pair(List<OfflineResult> rows,
            Predicate<OfflineResult> filter) {
        Map<ExperimentKey, MutablePair> pairs = new TreeMap<>(KEY_COMPARATOR);
        for (OfflineResult row : rows) {
            if (!filter.test(row)) continue;
            MutablePair pair = pairs.computeIfAbsent(row.getKey(), key -> new MutablePair());
            if (row.getMethod() == OfflineResult.METHOD_MOSEK) {
                if (pair.mosek != null) throw duplicate(row);
                pair.mosek = row;
            } else {
                if (pair.dd != null) throw duplicate(row);
                pair.dd = row;
            }
        }
        List<ExperimentPair> complete = new ArrayList<>();
        int unpaired = 0;
        for (Map.Entry<ExperimentKey, MutablePair> entry : pairs.entrySet()) {
            MutablePair pair = entry.getValue();
            if (pair.mosek != null && pair.dd != null) {
                complete.add(new ExperimentPair(entry.getKey(), pair.mosek, pair.dd));
            } else {
                unpaired++;
            }
        }
        return new Pairing(complete, unpaired);
    }

    private static IllegalArgumentException duplicate(OfflineResult row) {
        return new IllegalArgumentException("Duplicate method " + row.getMethod()
                + " row for experiment " + row.getKey());
    }

    private static List<ExperimentPair> filterPairs(List<ExperimentPair> input,
            Predicate<ExperimentPair> filter) {
        List<ExperimentPair> output = new ArrayList<>();
        for (ExperimentPair pair : input) if (filter.test(pair)) output.add(pair);
        return Collections.unmodifiableList(output);
    }

    private static List<ExperimentPair> sorted(List<ExperimentPair> input) {
        List<ExperimentPair> output = new ArrayList<>(input);
        output.sort((left, right) -> KEY_COMPARATOR.compare(left.key, right.key));
        return Collections.unmodifiableList(output);
    }

    private static final Comparator<ExperimentKey> KEY_COMPARATOR =
            Comparator.comparing(ExperimentKey::getDataset)
                    .thenComparingInt(ExperimentKey::getN)
                    .thenComparingDouble(ExperimentKey::getMu)
                    .thenComparingDouble(ExperimentKey::getLambda)
                    .thenComparingInt(ExperimentKey::getBandwidth)
                    .thenComparingInt(ExperimentKey::getMinimumConsecutiveOnes)
                    .thenComparingInt(ExperimentKey::getSeed);

    private static final class MutablePair {
        private OfflineResult mosek;
        private OfflineResult dd;
    }

    private static final class Pairing {
        private final List<ExperimentPair> completePairs;
        private final int unpairedRows;
        private Pairing(List<ExperimentPair> pairs, int unpairedRows) {
            this.completePairs = pairs;
            this.unpairedRows = unpairedRows;
        }
    }

    public static final class ExperimentPair {
        private final ExperimentKey key;
        private final OfflineResult mosek;
        private final OfflineResult dd;
        private ExperimentPair(ExperimentKey key, OfflineResult mosek, OfflineResult dd) {
            this.key = key;
            this.mosek = mosek;
            this.dd = dd;
        }
        public ExperimentKey getKey() { return key; }
        public OfflineResult getMosek() { return mosek; }
        public OfflineResult getDd() { return dd; }
    }

    public static final class FigureData {
        public static final int FIGURE_5_EXPECTED_PAIRS = 5400;
        public static final int FIGURE_6_EXPECTED_PAIRS = 750;
        public static final int FIGURE_7A_EXPECTED_PAIRS = 300;
        public static final int FIGURE_7B_EXPECTED_PAIRS = 600;

        private final int rawRowCount;
        private final List<ExperimentPair> figure5Pairs;
        private final int figure5UnpairedRows;
        private final Map<Double, List<ExperimentPair>> figure6Groups;
        private final List<ExperimentPair> figure7aPairs;
        private final List<ExperimentPair> figure7bPairs;

        private FigureData(int rawRowCount, List<ExperimentPair> figure5Pairs,
                int figure5UnpairedRows,
                Map<Double, List<ExperimentPair>> figure6Groups,
                List<ExperimentPair> figure7aPairs,
                List<ExperimentPair> figure7bPairs) {
            this.rawRowCount = rawRowCount;
            this.figure5Pairs = figure5Pairs;
            this.figure5UnpairedRows = figure5UnpairedRows;
            this.figure6Groups = Collections.unmodifiableMap(figure6Groups);
            this.figure7aPairs = figure7aPairs;
            this.figure7bPairs = figure7bPairs;
        }

        public int getRawRowCount() { return rawRowCount; }
        public List<ExperimentPair> getFigure5Pairs() { return figure5Pairs; }
        public int getFigure5UnpairedRows() { return figure5UnpairedRows; }
        public Map<Double, List<ExperimentPair>> getFigure6Groups() { return figure6Groups; }
        public List<ExperimentPair> getFigure7aPairs() { return figure7aPairs; }
        public List<ExperimentPair> getFigure7bPairs() { return figure7bPairs; }
        public int getFigure6PairCount() {
            int count = 0;
            for (List<ExperimentPair> group : figure6Groups.values()) count += group.size();
            return count;
        }
    }
}