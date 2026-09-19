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
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/** Recreates Figure 5: percentage of instances solved by time. */
public final class Figure5Generator {
    public void generate(List<ExperimentPair> pairs, Path output) throws IOException {
        XYSeries dd = performanceSeries("DD", pairs, true);
        XYSeries mosek = performanceSeries("MOSEK", pairs, false);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(dd);
        dataset.addSeries(mosek);

        JFreeChart chart = ChartFactory.createXYLineChart(null, "Time", "% solved",
                dataset, PlotOrientation.VERTICAL, true, false, false);
        PaperChartTheme.configureXYChart(chart);
        NumberAxis timeAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();
        NumberAxis percentAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();
        PaperChartTheme.configureTimeAxis(timeAxis);
        PaperChartTheme.configurePercentAxis(percentAxis);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        renderer.setSeriesPaint(0, PaperChartTheme.DD_BLUE);
        renderer.setSeriesPaint(1, PaperChartTheme.MOSEK_RED);
        renderer.setSeriesShape(0, PaperChartTheme.diamond(3.2));
        renderer.setSeriesShape(1, PaperChartTheme.triangle(3.5));
        renderer.setSeriesShapesFilled(0, true);
        renderer.setSeriesShapesFilled(1, true);
        chart.getXYPlot().setRenderer(renderer);
        ChartUtils.saveChartAsPNG(output.toFile(), chart, 1200, 800);
    }

    private static XYSeries performanceSeries(String name,
            List<ExperimentPair> pairs, boolean useDd) {
        XYSeries series = new XYSeries(name, true, true);
        series.add(0.0, 0.0);
        List<Double> solvedTimes = new ArrayList<>();
        for (ExperimentPair pair : pairs) {
            OfflineResult result = useDd ? pair.getDd() : pair.getMosek();
            if (result.isSolved()) {
                solvedTimes.add(result.getCappedFigureRuntimeSeconds());
            }
        }
        Collections.sort(solvedTimes);
        for (int i = 0; i < solvedTimes.size(); i++) {
            double percentage = 100.0 * (i + 1) / pairs.size();
            series.add(solvedTimes.get(i).doubleValue(), percentage);
        }
        return series;
    }
}