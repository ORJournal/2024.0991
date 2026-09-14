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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Writes the online rows and coverage counts behind Figure 1. */
public final class Figure1DataWriter {
    public void write(Figure1DataBuilder.Figure1Data data, Path outputDirectory)
            throws IOException {
        try (PrintWriter writer = writer(outputDirectory.resolve("figure1_data.csv"))) {
            writer.println("dataset,total_periods,horizon,mu,lambda,bandwidth,tau,seed,method,dd_nodes,dd_arcs,dd_construction_seconds,total_shortest_path_seconds,window_count,average_milliseconds");
            for (int bandwidth : Figure1DataBuilder.BANDWIDTHS) {
                for (double lambda : Figure1DataBuilder.LAMBDA_VALUES) {
                    for (OnlineResult row : data.getGroups(bandwidth).get(lambda)) {
                        writer.printf(Locale.US,
                                "%s,%d,%d,%.3f,%.2f,%d,%d,%d,%d,%.0f,%.0f,%.9f,%.9f,%d,%.9f%n",
                                csv(row.getDataset()), row.getTotalPeriods(),
                                row.getHorizon(), row.getMu(), row.getLambda(),
                                row.getBandwidth(), row.getMinimumConsecutiveOnes(),
                                row.getSeed(), row.getMethod(), row.getDdNodes(),
                                row.getDdArcs(), row.getDdConstructionSeconds(),
                                row.getTotalShortestPathSeconds(), row.getWindowCount(),
                                row.getAverageMilliseconds());
                    }
                }
            }
        }

        try (PrintWriter writer = writer(
                outputDirectory.resolve("figure1_coverage.csv"))) {
            writer.println("scope,available_rows,expected_rows,ignored_missing_rows");
            int totalExpected = 0;
            for (int bandwidth : Figure1DataBuilder.BANDWIDTHS) {
                for (double lambda : Figure1DataBuilder.LAMBDA_VALUES) {
                    List<OnlineResult> group = data.getGroups(bandwidth).get(lambda);
                    coverage(writer, String.format(Locale.US, "k%d_lambda_%.2f",
                            bandwidth, lambda), group.size(),
                            Figure1DataBuilder.EXPECTED_ROWS_PER_GROUP);
                    totalExpected += Figure1DataBuilder.EXPECTED_ROWS_PER_GROUP;
                }
            }
            coverage(writer, "figure1_total", data.getIncludedRowCount(), totalExpected);
        }
    }

    private static void coverage(PrintWriter writer, String scope,
            int available, int expected) {
        writer.printf(Locale.US, "%s,%d,%d,%d%n", scope, available, expected,
                Math.max(0, expected - available));
    }

    private static String csv(String value) {
        if (value.indexOf(',') < 0 && value.indexOf('"') < 0
                && value.indexOf('\n') < 0 && value.indexOf('\r') < 0) return value;
        return '"' + value.replace("\"", "\"\"") + '"';
    }

    private static PrintWriter writer(Path output) throws IOException {
        BufferedWriter buffered = Files.newBufferedWriter(output,
                StandardCharsets.UTF_8);
        return new PrintWriter(buffered);
    }
}