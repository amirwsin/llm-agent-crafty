package display;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import crafty.DataCenter;
import crafty.ModelState;
import modelRunner.AbstractModelRunner;
import modelRunner.ModelRunner;
import net.sourceforge.jFuzzyLogic.fcl.FclParser.declaration_return;
import sim.engine.SimState;
import sim.engine.Steppable;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;

public class GridOfCharts implements ModelState, Steppable {

	/**
	 * This is class can be used optionally. It provides an alternative functionality
	 * to visualise all the ecosystem services in the model.
	 */
	private static final long serialVersionUID = 1L;
	AbstractModelRunner modelRunner;
	public JFrame frame;
	private HashMap<String, XYSeries> totalProductionSeries = new HashMap<String, XYSeries>();
	private HashMap<String, XYSeries> totalDemandSeries = new HashMap<String, XYSeries>();
	private HashMap<String, Double> productionHashMap;
	private HashMap<String, Double> demandHashMap;

	public GridOfCharts() {

		frame = new JFrame("Ecoservices");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public void prepare() {
		productionHashMap = modelRunner.getState(DataCenter.class).getGlobalProductionMap();
		demandHashMap = modelRunner.getState(DataCenter.class).getAnualDemand();
		List<String> serviceNameList = modelRunner.getState(DataCenter.class).getServiceNameList();

		for (String serviceName : serviceNameList) {

			XYSeriesCollection dataset = new XYSeriesCollection();
			JFreeChart chart;

			XYSeries supplySeries = new XYSeries(serviceName + " Supply");
			XYSeries demandSeries = new XYSeries(serviceName + " Demand");

			totalProductionSeries.put(serviceName, supplySeries);
			totalDemandSeries.put(serviceName, demandSeries);

			dataset.addSeries(supplySeries);
			dataset.addSeries(demandSeries);

			chart = ChartFactory.createXYLineChart(serviceName, "Time", "Quantity", dataset, PlotOrientation.VERTICAL,
					false, false, false);

			// Set chart and plot background to white
			chart.setBackgroundPaint(Color.WHITE);
			XYPlot plot = (XYPlot) chart.getPlot();
			plot.setBackgroundPaint(Color.WHITE);

			// Make lines thicker
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
			BasicStroke thickStroke = new BasicStroke(2.0f);
			renderer.setBaseStroke(thickStroke);

			frame.add(new ChartPanel(chart));
		}

		int dataSize = modelRunner.getState(DataCenter.class).getGlobalProductionMap().size();
		int gridWidth = (int) Math.ceil(Math.sqrt(dataSize));
		frame.setLayout(new GridLayout(gridWidth, gridWidth));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	private void updateChart() {
		modelRunner.getState(DataCenter.class).getServiceNameList().forEach(productionName -> {
			totalProductionSeries.get(productionName).add(modelRunner.schedule.getSteps(),
					productionHashMap.get(productionName));
			totalDemandSeries.get(productionName).add(modelRunner.schedule.getSteps(),
					demandHashMap.get(productionName));
		});
	}

	@Override
	public void step(SimState arg0) {
		updateChart();
		// System.out.println("Charts step");
	}

	@Override
	public void setup(AbstractModelRunner modelRunner) {
		this.modelRunner = modelRunner;
		totalProductionSeries.clear();
		totalDemandSeries.clear();
		prepare();

	}

	@Override
	public void toSchedule() {
		modelRunner.schedule.scheduleRepeating(0.0, modelRunner.indexOf(this), this, 1.0);
	}

}
