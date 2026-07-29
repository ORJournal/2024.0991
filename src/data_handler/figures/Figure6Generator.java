package data_handler.figures;

import data_handler.figures.FigureDataBuilder.ExperimentPair;
import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.text.DecimalFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

/** Recreates Figure 6: runtime box plots grouped by the L0 parameter mu. */
public final class Figure6Generator {
    public void generate(Map<Double, List<ExperimentPair>> groups, Path output)
            throws IOException {
        DefaultBoxAndWhiskerCategoryDataset dataset =
                new DefaultBoxAndWhiskerCategoryDataset();
        for (Map.Entry<Double, List<ExperimentPair>> entry : groups.entrySet()) {
            String category = String.format(Locale.US, "\u03bc = %.3f", entry.getKey());
            List<Double> mosek = new ArrayList<>();
            List<Double> dd = new ArrayList<>();
            for (ExperimentPair pair : entry.getValue()) {
                mosek.add(pair.getMosek().getCappedFigureRuntimeSeconds());
                dd.add(pair.getDd().getCappedFigureRuntimeSeconds());
            }
            if (!mosek.isEmpty()) {
                dataset.add(mosek, "MOSEK", category);
                dataset.add(dd, "DD", category);
            }
        }

        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(
                null, null, "Time", dataset, true);
        PaperChartTheme.configureCategoryChart(chart);
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis categoryAxis = plot.getDomainAxis();
        categoryAxis.setTickLabelFont(PaperChartTheme.TICK_FONT);
        categoryAxis.setLabelFont(PaperChartTheme.LABEL_FONT);
        categoryAxis.setCategoryMargin(0.18);

        NumberAxis timeAxis = (NumberAxis) plot.getRangeAxis();
        timeAxis.setRange(0.0, OfflineResult.TIME_LIMIT_SECONDS);
        timeAxis.setTickUnit(new NumberTickUnit(200.0));
        PaperChartTheme.configureAxis(timeAxis);
        timeAxis.setNumberFormatOverride(new DecimalFormat("0"));

        MeanMarkerBoxAndWhiskerRenderer renderer =
                new MeanMarkerBoxAndWhiskerRenderer();
        plot.setRenderer(renderer);
        renderer.setFillBox(true);
        renderer.setMedianVisible(true);
        renderer.setUseOutlinePaintForWhiskers(true);
        renderer.setItemMargin(0.16);
        renderer.setSeriesPaint(0, new Color(229, 28, 35, 150));
        renderer.setSeriesPaint(1, new Color(0, 102, 204, 150));
        renderer.setSeriesOutlinePaint(0, PaperChartTheme.MOSEK_RED);
        renderer.setSeriesOutlinePaint(1, PaperChartTheme.DD_BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(1.4f));
        renderer.setSeriesStroke(1, new BasicStroke(1.4f));
        renderer.setArtifactPaint(Color.BLACK);

        ChartUtils.saveChartAsPNG(output.toFile(), chart, 1200, 800);
    }
}