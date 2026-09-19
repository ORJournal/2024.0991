/**
 * MIT License

Copyright (c) 2025 Andres Gomez

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */

package data_handler.figures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Selects and groups the online experiments used by Figure 1. */
public final class Figure1DataBuilder {
    public static final int EXPECTED_TOTAL_PERIODS = 7022;
    public static final int EXPECTED_HORIZON = 200;
    public static final int EXPECTED_ROWS_PER_GROUP = 30;
    public static final List<Integer> BANDWIDTHS = List.of(2, 3);
    public static final List<Double> LAMBDA_VALUES = List.of(0.25, 0.5, 1.0, 2.0, 5.0);

    private static final Set<Double> MU_VALUES = Set.of(
            0.001, 0.005, 0.010, 0.020, 0.050, 0.100);
    private static final Set<Integer> SEEDS = Set.of(101, 102, 103, 104, 105);

    public Figure1Data build(List<OnlineResult> allRows) {
        Map<Integer, Map<Double, List<OnlineResult>>> groups = new LinkedHashMap<>();
        for (int bandwidth : BANDWIDTHS) {
            Map<Double, List<OnlineResult>> byLambda = new LinkedHashMap<>();
            for (double lambda : LAMBDA_VALUES) {
                byLambda.put(lambda, new ArrayList<>());
            }
            groups.put(bandwidth, byLambda);
        }

        Set<String> identities = new HashSet<>();
        int included = 0;
        for (OnlineResult row : allRows) {
            if (!isFigure1Candidate(row)) continue;
            if (!identities.add(row.experimentIdentity())) {
                throw new IllegalArgumentException(
                        "Duplicate online row for " + row.experimentIdentity());
            }
            groups.get(row.getBandwidth()).get(row.getLambda()).add(row);
            included++;
        }

        Map<Integer, Map<Double, List<OnlineResult>>> immutable = new LinkedHashMap<>();
        for (Map.Entry<Integer, Map<Double, List<OnlineResult>>> bandwidth
                : groups.entrySet()) {
            Map<Double, List<OnlineResult>> byLambda = new LinkedHashMap<>();
            for (Map.Entry<Double, List<OnlineResult>> group
                    : bandwidth.getValue().entrySet()) {
                byLambda.put(group.getKey(), Collections.unmodifiableList(group.getValue()));
            }
            immutable.put(bandwidth.getKey(), Collections.unmodifiableMap(byLambda));
        }
        return new Figure1Data(allRows.size(), included,
                Collections.unmodifiableMap(immutable));
    }

    private boolean isFigure1Candidate(OnlineResult row) {
        return row.getTotalPeriods() == EXPECTED_TOTAL_PERIODS
                && row.getHorizon() == EXPECTED_HORIZON
                && row.getMinimumConsecutiveOnes() == 0
                && row.getMethod() == OnlineResult.METHOD_DD
                && BANDWIDTHS.contains(row.getBandwidth())
                && LAMBDA_VALUES.contains(row.getLambda())
                && MU_VALUES.contains(row.getMu())
                && SEEDS.contains(row.getSeed());
    }

    public static final class Figure1Data {
        private final int rawRowCount;
        private final int includedRowCount;
        private final Map<Integer, Map<Double, List<OnlineResult>>> groups;

        private Figure1Data(int rawRowCount, int includedRowCount,
                Map<Integer, Map<Double, List<OnlineResult>>> groups) {
            this.rawRowCount = rawRowCount;
            this.includedRowCount = includedRowCount;
            this.groups = groups;
        }

        public int getRawRowCount() { return rawRowCount; }
        public int getIncludedRowCount() { return includedRowCount; }
        public Map<Integer, Map<Double, List<OnlineResult>>> getGroups() { return groups; }
        public Map<Double, List<OnlineResult>> getGroups(int bandwidth) {
            return groups.get(bandwidth);
        }
    }
}