/*
** This is a GUI model runner with the LLMRunner embedded. Nothing special.
 */

package llmExp;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.UIManager;

import crafty.DataCenter;
import modelRunner.AbstractModelRunner;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import sim.portrayal.grid.ValueGridPortrayal2D;
import sim.util.gui.SimpleColorMap;

public class LLMModelRunnerWithUI extends GUIState {

	public Display2D display;
	public JFrame displayFrame;
	ValueGridPortrayal2D grid2D = new ValueGridPortrayal2D();
	Inspector dataInspector;

	public LLMModelRunnerWithUI() {
		// super(new ModelRunner(System.currentTimeMillis()));
		super(new LLMRunner(System.currentTimeMillis()));
	}

	public LLMModelRunnerWithUI(SimState state) {
		super(state);

	}

	public static String getName() {
		return "CRAFTY";
	}

	public Object getSimulationInspectedObject() {
		return state;
	}

	public Inspector getInspector() {

		Inspector i = super.getInspector();
		i.setVolatile(true);
		return i;

	}

	public void init(Controller c) {
		super.init(c);
		display = new Display2D(500, 500, this);
		display.setClipping(false);
		displayFrame = display.createFrame();
		displayFrame.setTitle("Display Center");
		c.registerFrame(displayFrame);
		displayFrame.setVisible(true);
		display.attach(grid2D, "AFT map");

	}

	public void start() {
		super.start();
		setupPortrayals();
	}

	public void load(SimState state) {
		super.load(state);
		setupPortrayals();

	}

	public void setupPortrayals() {
		int AFTnumber = ((AbstractModelRunner) state).getState(DataCenter.class).getAgentTypeMap().size();
		grid2D.setMap(new SimpleColorMap(generateDistinctColors(AFTnumber + 1)));
		grid2D.setField(((AbstractModelRunner) state).landMap);
		display.reset();
		display.setBackdrop(Color.white);
		display.repaint();

	}

	public void quit() {
		super.quit();
		if (displayFrame != null)
			displayFrame.dispose();
		displayFrame = null;
		display = null;
	}

	public Color[] generateDistinctColors(int count) {
		if (count <= 0) {
			throw new IllegalArgumentException("Count must be positive");
		}

		Color[] distinctColors = new Color[count];
		// Set the first color to white
		distinctColors[0] = Color.WHITE;

		for (int i = 1; i < count; i++) {
			float hue = (float) i / (count - 1); // adjusted the denominator
			distinctColors[i] = Color.getHSBColor(hue, 1f, 1f); // 1f for max saturation & brightness
		}
		return distinctColors;
	}

	public static Object getInfo() {
		try {
			return new java.net.URL("https://landchange.imk-ifu.kit.edu/CRAFTY");
		} catch (java.net.MalformedURLException e) {
			return "Oops";
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		LLMModelRunnerWithUI vid = new LLMModelRunnerWithUI();
		Console c = new Console(vid);
		c.setVisible(true);
	}
}
