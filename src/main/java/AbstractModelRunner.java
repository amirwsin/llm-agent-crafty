package modelRunner;
/*
 * ==========================================
 * Class: AbstractModelRunner
 * Package: modelRunner
 * ==========================================
 * Purpose:
 * - Acts as an abstract base class for running simulation models within the
 *   CRAFTY framework, managing the lifecycle of model states and scheduling.
 * - Provides core functionality to initialize and manage simulation components,
 *   track model states, and handle scheduling across simulation ticks.
 *
 * Main Components:
 * - Fields:
 *   - `stateManager`: Manages a list of model states used in the simulation.
 *   - `totalTicks`: Total ticks (steps) the model runs for.
 *   - `threshold`: Threshold value used in model evaluations.
 *   - `mapWidth`, `mapHeight`: Dimensions of the simulation grid.
 *   - `landMap`: 2D grid representing land use within the model.
 * - Methods:
 *   - `start()`: Initializes and starts the simulation, setting up states
 *     and scheduling.
 *   - `getState(Class<T> stateClass)`: Retrieves a specific model state from
 *     the `stateManager`.
 *   - `setup(AbstractModelRunner)`: Sets up each state within the model.
 *   - `toSchedule()`: Schedules each state within the simulation.
 *   - `loadStateManager()`: Abstract method to be implemented for loading
 *     and configuring model states.
 *
 * Usage:
 * - This class is extended by concrete model runners, defining custom
 *   state management and setup processes.
 *
 * Dependencies:
 * - Imports from `crafty`, `display`, and `sim.field.grid` for state and
 *   grid management, along with Swing utilities for error handling.
 * ==========================================
 */
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import crafty.ModelState;
import display.GridOfCharts;
import sim.engine.SimState;
import sim.field.grid.IntGrid2D;

public abstract class AbstractModelRunner extends SimState{

	private static final long serialVersionUID = 1L;
	protected List<ModelState> stateManager = new ArrayList<>();
	public int totalTicks = 70;
	protected double threshold = 0.3;
	protected int mapWidth;
	protected int mapHeight;
	public IntGrid2D landMap;

	public AbstractModelRunner(long seed) {
		super(seed);
	}

	public void start() {
		// catch errors and show them on a small window
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				SwingUtilities.invokeLater(() -> {
					JOptionPane.showMessageDialog(null, "An unexpected error occurred: " + e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				});
			}
		});
		super.start();
		if (getState(GridOfCharts.class) != null && getState(GridOfCharts.class).frame != null) {
			getState(GridOfCharts.class).frame.dispose();
		}
		stateManager.clear(); // this line is very important for the runs from the GUI.
		loadStateManager();
		setup(this);
		toSchedule();
	}

	public <T extends ModelState> T getState(Class<T> stateClass) {
		for (ModelState modelState : stateManager) {
			if (stateClass.isInstance(modelState)) {
				return stateClass.cast(modelState);
			}
		}
		return null;
	}

	public void setup(AbstractModelRunner abstractModelRunner) {
		for (ModelState modelState : stateManager) {
			modelState.setup((AbstractModelRunner) this);
		}

	}

	public void toSchedule() {
		for (ModelState modelState : stateManager) {
			modelState.toSchedule();
		}
	}

	public int indexOf(ModelState modelState) {
		return stateManager.indexOf(modelState);
	}

	public abstract void loadStateManager();

	public List<ModelState> getStateManager() {
		return stateManager;
	}

	public double getThreshold() {
		// TODO Auto-generated method stub
		return threshold;
	}
}
