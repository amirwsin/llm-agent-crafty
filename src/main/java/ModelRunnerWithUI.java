package display;
/*
 * ==========================================
 * Class: ModelRunnerWithUI
 * Package: display
 * ==========================================
 * Purpose:
 * - Provides a graphical user interface (GUI) for visualizing and interacting
 *   with the CRAFTY simulation model using the `Display2D` component.
 * - Extends `GUIState` to integrate model controls, a display window, and custom
 *   portrayals for visual representation of agent-based simulation data.
 *
 * Main Components:
 * - Fields:
 *   - `display`: Primary display for the 2D visualization of the model.
 *   - `displayFrame`: JFrame to host the display component.
 *   - `grid2D`: Represents the 2D portrayal of agent-based grid values.
 * - Methods:
 *   - `init(Controller c)`: Initializes and sets up the display within the GUI.
 *   - `start()`: Starts the simulation and sets up portrayals.
 *   - `setupPortrayals()`: Configures color maps and display parameters for the
 *     grid portrayal of agent functions.
 *   - `generateDistinctColors(int count)`: Generates a series of distinct colors
 *     for visualizing different agents in the simulation.
 *   - `quit()`: Closes and cleans up GUI components upon exit.
 *
 * Usage:
 * - Can be run as the main entry point for the GUI, allowing users to start, stop,
 *   and visualize the model.
 *
 * Dependencies:
 * - Relies on `Display2D`, `AbstractModelRunner`, and `DataCenter` for display and
 *   state management, and imports Java Swing components for GUI display.
 * ==========================================
 */


import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.UIManager;

import crafty.DataCenter;
import experiments_fuzzy.Intra;
import modelRunner.AbstractModelRunner;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.Inspector;
import sim.portrayal.grid.ValueGridPortrayal2D;
import sim.util.gui.SimpleColorMap;

public class ModelRunnerWithUI extends GUIState {

	public Display2D display;
	public JFrame displayFrame;
	ValueGridPortrayal2D grid2D = new ValueGridPortrayal2D();
	Inspector dataInspector;

	// Default constructor that initializes the model runner with `Intra`.
	public ModelRunnerWithUI() {
		// super(new ModelRunner(System.currentTimeMillis()));
		super(new Intra(System.currentTimeMillis()));
	}

	// Constructor that accepts a custom simulation state.
	public ModelRunnerWithUI(SimState state) {
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

	// Initializes the display and attaches it to the controller with custom settings.
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

	// Configures the color map and sets up the portrayal for the 2D grid display.
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

	// Generates an array of distinct colors for visualizing different agents in the grid.
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

	// Get the information from the CRAFTY websites for the information tab.
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
		ModelRunnerWithUI vid = new ModelRunnerWithUI();
		Console c = new Console(vid);
		c.setVisible(true);
	}
}
