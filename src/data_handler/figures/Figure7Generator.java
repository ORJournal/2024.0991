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