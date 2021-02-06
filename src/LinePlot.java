import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LinePlot extends JFrame {
    public LinePlot(List<Double> metrics, String title, String yAxisLabel) {
        final XYSeries series = new XYSeries(title);
        for (int i = 0; i <= metrics.size() - 1; i++) {
            series.add(i + 1, metrics.get(i));
        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createScatterPlot(title, "X-Axis", "Y-Axis", dataset);
        NumberAxis xAxis = new NumberAxis();
        xAxis.setTickUnit(new NumberTickUnit(1));
        xAxis.setLabel("number of clusters K");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickUnit(new NumberTickUnit(1));
        yAxis.setLabel(yAxisLabel);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainAxis(xAxis);
        plot.setRangeAxis(yAxis);
        plot.setBackgroundPaint(new Color(235, 235, 235));

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();

        renderer.setDefaultLinesVisible(true);
        renderer.setDefaultShapesFilled(true);
        renderer.setDefaultShapesVisible(true);

        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }
}
