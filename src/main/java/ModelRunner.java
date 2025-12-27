package modelRunner;
/*
 * ==========================================
 * Class: ModelRunner
 * Package: modelRunner
 * ==========================================
 * Purpose:
 * - A concrete implementation of `AbstractModelRunner` that sets up and runs the CRAFTY model
 *   using data from the CRAFTY-EU project.
 * - Loads and configures necessary states, initial conditions, and updaters, and detects map size
 *   from the baseline data file for grid initialization.
 *
 * Main Components:
 * - Fields:
 *   - `serviceNameFile`, `capitalNameFile`, `agentFilePath`: Paths to core data files for the CRAFTY model.
 *   - `baselineMapFilePath`: Path to the baseline map file used to initialize map dimensions.
 *   - `annualCapitalFilePath`, `annualDemandFile`: Paths for annual capital and demand data.
 * - Methods:
 *   - `loadStateManager()`: Loads the state components and updaters required for simulation execution.
 *   - `detectMapSize(String cellDataPath)`: Determines map width and height from the baseline file.
 *   - `main(String[] args)`: Entry point for running the model from the command line.
 *
 * Usage:
 * - This class serves as the primary runner for the CRAFTY model using specific data sources.
 * - When executed, it initializes necessary components, loads data, and runs the model simulation.
 *
 * Dependencies:
 * - Requires data from `crafty`, `display`, `institution`, `sim.field.grid`, and `updaters` packages.
 * ==========================================
 */
import crafty.DataCenter;
import display.GridOfCharts;
import institution.AgriInstitution;
import sim.field.grid.IntGrid2D;
import tech.tablesaw.api.Table;
import updaters.CapitalUpdater;
import updaters.DemandUpdater;
import updaters.InfluencedUtilityUpdater;
import updaters.MapUpdater;
import updaters.SupplyInitializer;
import updaters.SupplyUpdater;

public class ModelRunner extends AbstractModelRunner {

	// ======================== CRAFTY EU data =======================
	// Here, only the file paths inside the CRAFTY-EU data are given. Please replace them with the complete file paths.
	protected String serviceNameFile = "data_EUpaper_nocsv\\csv\\Services.csv";
	protected String capitalNameFile = "data_EUpaper_nocsv\\csv\\Capitals.csv";
	protected String agentFilePath = "data_EUpaper_nocsv\\production";
	protected String baselineMapFilePath = "data_EUpaper_nocsv\\worlds\\EU\\regionalisations\\28\\capitals\\Baseline_map.csv";
	protected String annualCapitalFilePath = "data_EUpaper_nocsv\\worlds\\EU\\regionalisations\\28\\capitals\\RCP2_6-SSP1";
	protected String annualDemandFile = "data_EUpaper_nocsv\\worlds\\EU\\regionalisations\\28\\RCP2_6-SSP1\\RCP2_6-SSP1_demands_EU.csv";//


	public ModelRunner(long seed) {
		super(seed);
		detectMapSize(baselineMapFilePath);
		landMap = new IntGrid2D(mapWidth + 1, mapHeight + 1);

	}

@Override
	public void loadStateManager() {
		DataCenter dataCenter = new DataCenter(serviceNameFile, capitalNameFile, agentFilePath, baselineMapFilePath,
				annualCapitalFilePath, annualDemandFile);
		stateManager.add(dataCenter);
		stateManager.add(new SupplyInitializer());
		stateManager.add(new CapitalUpdater());
		stateManager.add(new DemandUpdater());
		stateManager.add(new InfluencedUtilityUpdater());
		stateManager.add(dataCenter.getManagerSet());
		stateManager.add(new SupplyUpdater());
		stateManager.add(new AgriInstitution());
		stateManager.add(new MapUpdater());
		stateManager.add(new GridOfCharts());
	}

	public void detectMapSize(String cellDataPath) {

		Table table = Table.read().csv(cellDataPath);
		int rowNumber = table.rowCount();
		for (int i = 0; i < rowNumber; i++) {
			int x = table.row(i).getInt("x");
			int y = table.row(i).getInt("y");
			mapWidth = Math.max(mapWidth, x);
			mapHeight = Math.max(mapHeight, y);
		}
		table = null;
	}

	public static void main(String[] args) {
		doLoop(ModelRunner.class, args);
		System.exit(0);
	}
}
