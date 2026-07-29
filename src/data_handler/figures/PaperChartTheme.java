package data_handler.figures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.text.DecimalFormat;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleInsets;

/** Shared visual settings for the recreated paper figures. */
public final class PaperChartTheme {
    public static final Color DD_BLUE = new Color(0x00, 0x66, 0xCC);
    public static final Color MOSEK_RED = new Color(0xE5, 0x1C, 0x23);
    public static final Color GRID = new Color(0xD9, 0xD9, 0xD9);
    public static final Font LABEL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
    public static final Font TICK_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    public static final Font LEGEND_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 17);
    public static final BasicStroke GRID_STROKE = new BasicStroke(0.8f);

    private PaperChartTheme() { }

    public static void configureTimeAxis(NumberAxis axis) {
        axis.setRange(0.0, OfflineResult.TIME_LIMIT_SECONDS);
        axis.setTickUnit(new NumberTickUnit(300.0));
        axis.setNumberFormatOverride(new DecimalFormat("0"));
        configureAxis(axis);
    }

    public static void configurePercentAxis(NumberAxis axis) {
        axis.setRange(0.0, 100.0);
        axis.setTickUnit(new NumberTickUnit(10.0));
        configureAxis(axis);
    }

    public static void configureAxis(Axis axis) {
        axis.setLabelFont(LABEL_FONT);
        axis.setTickLabelFont(TICK_FONT);
    }

    public static void configureXYChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        chart.setPadding(new RectangleInsets(12.0, 12.0, 8.0, 12.0));
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(GRID);
        plot.setRangeGridlinePaint(GRID);
        plot.setDomainGridlineStroke(GRID_STROKE);
        plot.setRangeGridlineStroke(GRID_STROKE);
        plot.setOutlinePaint(Color.DARK_GRAY);
        configureLegend(chart.getLegend());
    }

    public static void configureCategoryChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        chart.setPadding(new RectangleInsets(12.0, 12.0, 8.0, 12.0));
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(GRID);
        plot.setRangeGridlineStroke(GRID_STROKE);
        plot.setOutlinePaint(Color.DARK_GRAY);
        configureLegend(chart.getLegend());
    }

    private static void configureLegend(LegendTitle legend) {
        if (legend != null) legend.setItemFont(LEGEND_FONT);
    }

    public static Shape diamond(double radius) {
        Path2D path = new Path2D.Double();
        path.moveTo(0.0, -radius);
        path.lineTo(radius, 0.0);
        path.lineTo(0.0, radius);
        path.lineTo(-radius, 0.0);
        path.closePath();
        return path;
    }

    public static Shape triangle(double radius) {
        Path2D path = new Path2D.Double();
        path.moveTo(0.0, -radius);
        path.lineTo(radius, radius);
        path.lineTo(-radius, radius);
        path.closePath();
        return path;
    }
}