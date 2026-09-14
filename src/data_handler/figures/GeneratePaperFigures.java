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

import data_handler.figures.FigureDataBuilder.FigureData;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/** Command-line entry point for recreating Figures 5-7 from raw results. */
public final class GeneratePaperFigures {
    private GeneratePaperFigures() { }

    public static void main(String[] args) throws Exception {
        if (args.length > 2) {
            System.err.println("Usage: GeneratePaperFigures [resultsOffline.csv] [output-directory]");
            System.exit(2);
        }
        System.setProperty("java.awt.headless", "true");
        Path input = args.length >= 1
                ? Paths.get(args[0]) : Paths.get("results", "resultsOffline.csv");
        Path output = args.length >= 2
                ? Paths.get(args[1]) : Paths.get("results", "generated_figures");
        input = input.toAbsolutePath().normalize();
        output = output.toAbsolutePath().normalize();
        Files.createDirectories(output);

        List<OfflineResult> rows = new OfflineResultsReader().read(input);
        FigureData data = new FigureDataBuilder().build(rows);
        if (data.getFigure5Pairs().isEmpty()) {
            throw new IllegalStateException("No complete MOSEK/DD pairs matched the paper grid");
        }

        new Figure5Generator().generate(data.getFigure5Pairs(),
                output.resolve("Figure5.png"));
        new Figure6Generator().generate(data.getFigure6Groups(),
                output.resolve("Figure6.png"));
        Figure7Generator figure7 = new Figure7Generator();
        figure7.generate(data.getFigure7aPairs(), output.resolve("Figure7a.png"));
        figure7.generate(data.getFigure7bPairs(), output.resolve("Figure7b.png"));
        new FigureDataWriter().write(data, output);

        System.out.println("Read " + data.getRawRowCount() + " raw rows from " + input);
        System.out.println("Figure 5: " + data.getFigure5Pairs().size()
                + " complete pairs (" + data.getFigure5UnpairedRows()
                + " one-method keys ignored)");
        System.out.println("Figure 6: " + data.getFigure6PairCount() + " complete pairs");
        System.out.println("Figure 7a: " + data.getFigure7aPairs().size()
                + " complete pairs");
        System.out.println("Figure 7b: " + data.getFigure7bPairs().size()
                + " complete pairs");
        System.out.println("Wrote figures and processed data to " + output);
    }
}