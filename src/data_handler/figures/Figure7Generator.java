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
import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/** Recreates Figure 7a and 7b: paired MOSEK versus DD runtime plots. */
public final class Figure7Generator {
    public void generate(List<ExperimentPair> pairs, Path output) throws IOException {
        XYSeries points = new XYSeries("Instances", false, true);
        for (ExperimentPair pair : pairs) {
            points.add(pair.getMosek().getCappedFigureRuntimeSeconds(),
                    pair.getDd().getCappedFigureRuntimeSeconds());
        }
        XYSeriesCollection dataset = new XYSeriesCollection(points);
        JFreeChart chart = ChartFactory.createScatterPlot(null, "MOSEK", "DD",
                dataset, PlotOrientation.VERTICAL, false, false, false);
        PaperChartTheme.configureXYChart(chart);
        NumberAxis xAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();
        NumberAxis yAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();
        PaperChartTheme.configureTimeAxis(xAxis);
        PaperChartTheme.configureTimeAxis(yAxis);

        XYLineAndShapeRenderer renderer =
                (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
        renderer.setSeriesPaint(0, PaperChartTheme.DD_BLUE);
        renderer.setSeriesShape(0, PaperChartTheme.diamond(3.5));
        renderer.setSeriesShapesFilled(0, true);
        chart.getXYPlot().addAnnotation(new XYLineAnnotation(
                0.0, 0.0, OfflineResult.TIME_LIMIT_SECONDS,
                OfflineResult.TIME_LIMIT_SECONDS,
                new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_BEVEL, 0.0f, new float[]{8.0f, 6.0f}, 0.0f),
                Color.DARK_GRAY));
        ChartUtils.saveChartAsPNG(output.toFile(), chart, 900, 900);
    }
}