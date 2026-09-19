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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

/** Recreates Figure 1's k=2 and k=3 online-runtime box plots. */
public final class Figure1Generator {
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 700;

    public void generate(Figure1DataBuilder.Figure1Data data,
            Path outputDirectory) throws IOException {
        JFreeChart k2 = createPanel(2, data.getGroups(2));
        JFreeChart k3 = createPanel(3, data.getGroups(3));
        ChartUtils.saveChartAsPNG(outputDirectory.resolve("Figure1a.png").toFile(),
                k2, PANEL_WIDTH, PANEL_HEIGHT);
        ChartUtils.saveChartAsPNG(outputDirectory.resolve("Figure1b.png").toFile(),
                k3, PANEL_WIDTH, PANEL_HEIGHT);

        BufferedImage combined = new BufferedImage(PANEL_WIDTH * 2, PANEL_HEIGHT,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = combined.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setPaint(Color.WHITE);
            graphics.fillRect(0, 0, combined.getWidth(), combined.getHeight());
            graphics.drawImage(k2.createBufferedImage(PANEL_WIDTH, PANEL_HEIGHT),
                    0, 0, null);
            graphics.drawImage(k3.createBufferedImage(PANEL_WIDTH, PANEL_HEIGHT),
                    PANEL_WIDTH, 0, null);
        } finally {
            graphics.dispose();
        }
        ImageIO.write(combined, "png", outputDirectory.resolve("Figure1.png").toFile());
    }

    private JFreeChart createPanel(int bandwidth,
            Map<Double, List<OnlineResult>> groups) {
        DefaultBoxAndWhiskerCategoryDataset dataset =
                new DefaultBoxAndWhiskerCategoryDataset();
        for (double lambda : Figure1DataBuilder.LAMBDA_VALUES) {
            List<OnlineResult> rows = groups.get(lambda);
            if (rows == null || rows.isEmpty()) continue;
            List<Double> milliseconds = new ArrayList<>();
            for (OnlineResult row : rows) {
                milliseconds.add(row.getAverageMilliseconds());
            }
            dataset.add(milliseconds, "DD", String.format(Locale.US,
                    "\u03bb = %.2f", lambda));
        }

        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(
                "k = " + bandwidth, null, "Time (milliseconds)", dataset, false);
        PaperChartTheme.configureCategoryChart(chart);
        chart.getTitle().setFont(PaperChartTheme.LABEL_FONT);
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis categoryAxis = plot.getDomainAxis();
        categoryAxis.setTickLabelFont(PaperChartTheme.TICK_FONT);
        categoryAxis.setLabelFont(PaperChartTheme.LABEL_FONT);
        categoryAxis.setCategoryMargin(0.22);

        NumberAxis timeAxis = (NumberAxis) plot.getRangeAxis();
        if (bandwidth == 2) {
            timeAxis.setRange(0.0, 25.0);
            timeAxis.setTickUnit(new NumberTickUnit(5.0));
        } else {
            timeAxis.setRange(0.0, 350.0);
            timeAxis.setTickUnit(new NumberTickUnit(50.0));
        }
        timeAxis.setNumberFormatOverride(new DecimalFormat("0"));
        PaperChartTheme.configureAxis(timeAxis);

        MeanMarkerBoxAndWhiskerRenderer renderer =
                new MeanMarkerBoxAndWhiskerRenderer();
        renderer.setMinOutlierVisible(true);
        renderer.setMaxOutlierVisible(true);
        renderer.setFillBox(true);
        renderer.setMedianVisible(true);
        renderer.setUseOutlinePaintForWhiskers(true);
        renderer.setMaximumBarWidth(0.11);
        renderer.setSeriesPaint(0, new Color(91, 155, 213, 170));
        renderer.setSeriesOutlinePaint(0, Color.BLACK);
        renderer.setSeriesStroke(0, new BasicStroke(1.3f));
        renderer.setArtifactPaint(Color.BLACK);
        plot.setRenderer(renderer);
        return chart;
    }
}