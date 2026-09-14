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

import data_handler.figures.FigureDataBuilder.ExperimentPair;
import data_handler.figures.FigureDataBuilder.FigureData;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Writes the exact paired data and coverage counts behind each generated plot. */
public final class FigureDataWriter {
    public void write(FigureData data, Path outputDirectory) throws IOException {
        writeFigure5(data.getFigure5Pairs(), outputDirectory.resolve("figure5_data.csv"));
        writeFigure6(data.getFigure6Groups(), outputDirectory.resolve("figure6_data.csv"));
        writeFigure7(data, outputDirectory.resolve("figure7_data.csv"));
        writeCoverage(data, outputDirectory.resolve("figure_coverage.csv"));
    }

    private void writeFigure5(List<ExperimentPair> pairs, Path output)
            throws IOException {
        try (PrintWriter writer = writer(output)) {
            writePairHeader(writer, null);
            for (ExperimentPair pair : pairs) writePair(writer, pair, null);
        }
    }

    private void writeFigure6(Map<Double, List<ExperimentPair>> groups, Path output)
            throws IOException {
        try (PrintWriter writer = writer(output)) {
            writePairHeader(writer, "group_mu");
            for (Map.Entry<Double, List<ExperimentPair>> group : groups.entrySet()) {
                for (ExperimentPair pair : group.getValue()) {
                    writePair(writer, pair, format(group.getKey()));
                }
            }
        }
    }

    private void writeFigure7(FigureData data, Path output) throws IOException {
        try (PrintWriter writer = writer(output)) {
            writePairHeader(writer, "panel");
            for (ExperimentPair pair : data.getFigure7aPairs()) writePair(writer, pair, "a");
            for (ExperimentPair pair : data.getFigure7bPairs()) writePair(writer, pair, "b");
        }
    }

    private void writeCoverage(FigureData data, Path output) throws IOException {
        try (PrintWriter writer = writer(output)) {
            writer.println("scope,available_complete_pairs,expected_pairs,ignored_missing_or_unpaired_pairs");
            coverage(writer, "figure5", data.getFigure5Pairs().size(),
                    FigureData.FIGURE_5_EXPECTED_PAIRS);
            for (Map.Entry<Double, List<ExperimentPair>> group
                    : data.getFigure6Groups().entrySet()) {
                coverage(writer, "figure6_mu_" + format(group.getKey()),
                        group.getValue().size(), 150);
            }
            coverage(writer, "figure6_total", data.getFigure6PairCount(),
                    FigureData.FIGURE_6_EXPECTED_PAIRS);
            coverage(writer, "figure7a", data.getFigure7aPairs().size(),
                    FigureData.FIGURE_7A_EXPECTED_PAIRS);
            coverage(writer, "figure7b", data.getFigure7bPairs().size(),
                    FigureData.FIGURE_7B_EXPECTED_PAIRS);
        }
    }

    private static void coverage(PrintWriter writer, String scope,
            int available, int expected) {
        writer.printf(Locale.US, "%s,%d,%d,%d%n", scope, available, expected,
                Math.max(0, expected - available));
    }

    private static void writePairHeader(PrintWriter writer, String prefix) {
        if (prefix != null) writer.print(prefix + ",");
        writer.println("dataset,n,mu,lambda,bandwidth,tau,seed,mosek_seconds,dd_seconds,mosek_solved,dd_solved");
    }

    private static void writePair(PrintWriter writer, ExperimentPair pair,
            String prefixValue) {
        ExperimentKey key = pair.getKey();
        if (prefixValue != null) writer.print(prefixValue + ",");
        writer.printf(Locale.US, "%s,%d,%s,%s,%d,%d,%d,%.9f,%.9f,%s,%s%n",
                csv(key.getDataset()), key.getN(), format(key.getMu()),
                format(key.getLambda()), key.getBandwidth(),
                key.getMinimumConsecutiveOnes(), key.getSeed(),
                pair.getMosek().getCappedFigureRuntimeSeconds(),
                pair.getDd().getCappedFigureRuntimeSeconds(),
                pair.getMosek().isSolved(), pair.getDd().isSolved());
    }

    private static String format(double value) {
        return String.format(Locale.US, "%.3f", value);
    }

    private static String csv(String value) {
        if (value.indexOf(',') < 0 && value.indexOf('"') < 0
                && value.indexOf('\n') < 0 && value.indexOf('\r') < 0) return value;
        return '"' + value.replace("\"", "\"\"") + '"';
    }

    private static PrintWriter writer(Path output) throws IOException {
        BufferedWriter buffered = Files.newBufferedWriter(
                output, StandardCharsets.UTF_8);
        return new PrintWriter(buffered);
    }
}