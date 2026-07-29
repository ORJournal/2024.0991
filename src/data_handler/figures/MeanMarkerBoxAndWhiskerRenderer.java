package data_handler.figures;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;

/** Box-plot renderer using the mean and timeout markers seen in the paper. */
public final class MeanMarkerBoxAndWhiskerRenderer extends BoxAndWhiskerRenderer {
    private static final double MARKER_RADIUS = 5.0;

    public MeanMarkerBoxAndWhiskerRenderer() {
        setMeanVisible(false);
        setMaxOutlierVisible(false);
    }

    @Override
    public void drawVerticalItem(Graphics2D graphics,
            CategoryItemRendererState state, Rectangle2D dataArea,
            CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis,
            CategoryDataset dataset, int row, int column) {
        super.drawVerticalItem(graphics, state, dataArea, plot, domainAxis,
                rangeAxis, dataset, row, column);
        if (!(dataset instanceof BoxAndWhiskerCategoryDataset)) return;
        BoxAndWhiskerCategoryDataset boxes =
                (BoxAndWhiskerCategoryDataset) dataset;
        int visibleRow = state.getVisibleSeriesIndex(row);
        int visibleSeriesCount = state.getVisibleSeriesCount();
        if (visibleRow < 0 || visibleSeriesCount == 0) return;
        double centerX = itemCenter(state, dataArea, plot, domainAxis,
                dataset, visibleRow, visibleSeriesCount, column);

        graphics.setPaint(getItemOutlinePaint(row, column));
        graphics.setStroke(new BasicStroke(1.8f));
        Number mean = boxes.getMeanValue(row, column);
        if (mean != null) {
            double centerY = rangeAxis.valueToJava2D(mean.doubleValue(), dataArea,
                    plot.getRangeAxisEdge());
            graphics.draw(new Line2D.Double(centerX - MARKER_RADIUS,
                    centerY - MARKER_RADIUS, centerX + MARKER_RADIUS,
                    centerY + MARKER_RADIUS));
            graphics.draw(new Line2D.Double(centerX - MARKER_RADIUS,
                    centerY + MARKER_RADIUS, centerX + MARKER_RADIUS,
                    centerY - MARKER_RADIUS));
        }

        List<?> outliers = boxes.getOutliers(row, column);
        boolean hasTimeout = false;
        if (outliers != null) {
            for (Object value : outliers) {
                if (value instanceof Number
                        && ((Number) value).doubleValue()
                        >= OfflineResult.TIME_LIMIT_SECONDS) {
                    hasTimeout = true;
                    break;
                }
            }
        }
        if (hasTimeout) {
            double timeoutY = rangeAxis.valueToJava2D(
                    OfflineResult.TIME_LIMIT_SECONDS, dataArea,
                    plot.getRangeAxisEdge());
            timeoutY = Math.max(dataArea.getMinY() + MARKER_RADIUS + 1.0,
                    timeoutY);
            graphics.draw(new Ellipse2D.Double(centerX - MARKER_RADIUS,
                    timeoutY - MARKER_RADIUS, 2.0 * MARKER_RADIUS,
                    2.0 * MARKER_RADIUS));
        }
    }

    private double itemCenter(CategoryItemRendererState state,
            Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis,
            CategoryDataset dataset, int visibleRow, int visibleSeriesCount,
            int column) {
        if (visibleSeriesCount == 1) {
            return domainAxis.getCategoryMiddle(column,
                    dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        }
        double categoryStart = domainAxis.getCategoryStart(column,
                dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        double seriesGap = dataArea.getWidth() * getItemMargin()
                / (dataset.getColumnCount() * (visibleSeriesCount - 1));
        return categoryStart + visibleRow * (state.getBarWidth() + seriesGap)
                + state.getBarWidth() / 2.0;
    }
}