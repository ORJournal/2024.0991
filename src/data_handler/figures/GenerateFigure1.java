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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/** Command-line entry point for recreating Figure 1 from online results. */
public final class GenerateFigure1 {
    private GenerateFigure1() { }

    public static void main(String[] args) throws Exception {
        if (args.length > 2) {
            System.err.println("Usage: GenerateFigure1 [resultsOnline.csv] [output-directory]");
            System.exit(2);
        }
        System.setProperty("java.awt.headless", "true");
        Path input = args.length >= 1
                ? Paths.get(args[0]) : Paths.get("results", "resultsOnline.csv");
        Path output = args.length >= 2
                ? Paths.get(args[1]) : Paths.get("results", "generated_figures");
        input = input.toAbsolutePath().normalize();
        output = output.toAbsolutePath().normalize();
        Files.createDirectories(output);

        List<OnlineResult> rows = new OnlineResultsReader().read(input);
        Figure1DataBuilder.Figure1Data data = new Figure1DataBuilder().build(rows);
        if (data.getIncludedRowCount() == 0) {
            throw new IllegalStateException(
                    "No online rows matched the Figure 1 experiment grid");
        }
        new Figure1Generator().generate(data, output);
        new Figure1DataWriter().write(data, output);

        System.out.println("Read " + data.getRawRowCount() + " online rows from " + input);
        System.out.println("Figure 1: " + data.getIncludedRowCount()
                + " available rows; missing rows and empty groups ignored");
        for (int bandwidth : Figure1DataBuilder.BANDWIDTHS) {
            int count = data.getGroups(bandwidth).values().stream()
                    .mapToInt(List::size).sum();
            System.out.println("  k=" + bandwidth + ": " + count + " rows");
        }
        System.out.println("Wrote Figure 1 and processed data to " + output);
    }
}