package crafty;
/*
 * ==========================================
 * Class: DataCenter
 * Package: crafty
 * ==========================================
 * Purpose:
 * - Core class responsible for data loading, updating, and managing
 *   land-use simulation data within the CRAFTY model framework.
 * - Manages land cell data, agent types, demand, and production information,
 *   as well as updating the state of the model during simulations.
 *
 * Main Components:
 * - Fields:
 *   - `agentTypeMap`, `managerSet`, `cellSet`: Store and manage agents and
 *      land cells within the simulation.
 *   - `serviceNameList`, `capitalNameList`: Lists for names of services and
 *      capitals being tracked in the model.
 *   - `globalProductionMap`, `anualDemandMap`, `initialZeroProductionMap`:
 *      HashMaps for tracking production and demand metrics over time.
 * - Methods:
 *   - `setup(AbstractModelRunner modelRunner)`: Initializes the model by loading
 *     data files, setting up agents, and preparing internal data structures.
 *   - `loadAgentsData(String agentDataDirectory)`: Loads agent data from
 *     the specified directory, creating managers for each agent type.
 *   - `loadBaselineCellData(String cellDataPath)`: Loads baseline data for land
 *     cells and sets their ownership, production, and location information.
 *   - `updateAnualCellCapitals()`, `updateDemand()`, `updateSupply()`: Core
 *     functions that refresh cell capitals, demand, and supply metrics in
 *     each simulation step.
 *
 * Usage:
 * - Instantiated as a central data handler, `DataCenter` manages both the
 *   initial loading of data and real-time updates during simulation steps.
 * - Interfaces with other model components via `ModelState`.
 *
 * Dependencies:
 * - Imports from `modelRunner`, `Tablesaw API`, and `SimState` to interact
 *   with data sources and integrate into the simulation environment.
 * ==========================================
 */


import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import modelRunner.AbstractModelRunner;
import modelRunner.ModelRunner;
import sim.engine.SimState;
import sim.engine.Steppable;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class DataCenter implements ModelState {// , Steppable{

	private HashMap<String, Manager> agentTypeMap = new HashMap<>();
	private CellSet cellSet = new CellSet();
	private ManagerSet managerSet = new ManagerSet();
	String serviceNameFile;
	String capitalNameFile;
	private List<String> capitalNameList;
	private List<String> serviceNameList;
	private Map<String, Integer> serviceNameIndexMap = new HashMap<>();
	private HashMap<String, Double> initialZeroProdutionMap = new HashMap<>();
	private Iterator<File> anualCapitalFileIterator;
	private HashMap<String, Double> anualDemandMap = new HashMap<>();
	private Table anualDemandTable;
	private Iterator<Row> anualDemandIterator;
	private HashMap<String, Double> globalProductionMap = new HashMap<>();
	private HashMap<String, Double> utitlityMap = new HashMap<>();
	HashMap<String, List<Double>> strategy = null;
	HashMap<String, Double> currentStrategy = new HashMap<>();
	AbstractModelRunner modelRunner;
	private int mapWidth = 0;
	private int mapHeight = 0;
	private Map<String, Double> initSupplyMap = new HashMap<>();
	public HashMap<String, Integer> AFTCounter = new HashMap<String, Integer>();
	int time = 0;

	private String agentDataDirectory, cellDataPath, anualCapitalFilePath, anualDemandFile;

	public DataCenter(String agentDataDirectory, String cellDataPath) {
		this.agentDataDirectory = agentDataDirectory;
		this.cellDataPath = cellDataPath;
	}

	public DataCenter(String agentDataDirectory, String cellDataPath, String anualCapitalFilePath) {
		this(agentDataDirectory, cellDataPath);
		this.anualCapitalFilePath = anualCapitalFilePath;
	}

	public DataCenter(String agentDataDirectory, String cellDataPath, String anualCapitalFilePath,
			String anualDemandFile) {
		this(agentDataDirectory, cellDataPath, anualCapitalFilePath);
		this.anualDemandFile = anualDemandFile;
	}

	public DataCenter(String serviceNameFile, String capitalNameFile, String agentDataDirectory, String cellDataPath,
			String anualCapitalFilePath, String anualDemandFile) {
		this(agentDataDirectory, cellDataPath, anualCapitalFilePath);
		this.anualDemandFile = anualDemandFile;
		this.serviceNameFile = serviceNameFile;
		this.capitalNameFile = capitalNameFile;
	}

	//Setup DataCenter by loading the data from the CRAFTY-EU data files.
	@Override
	public void setup(AbstractModelRunner modelRunner) {
		this.modelRunner = modelRunner;
		prepareSerivceNames(serviceNameFile);
		prepareCapitalNames(capitalNameFile);

		loadAgentsData(agentDataDirectory);
		loadBaselineCellData(cellDataPath);

		initializeHashMaps();
		refreshGlobalProductionMap();
		File anualCapitalFileDir = new File(anualCapitalFilePath);
		this.anualCapitalFileIterator = Arrays.stream(anualCapitalFileDir.listFiles()).iterator();
		loadAnualDemandDataToIterator(anualDemandFile);

		System.out.println(AFTCounter.keySet());
	}

	private void prepareSerivceNames(String serviceNameFile) {
		try {
			Table serviceNameTable = Table.read().csv(serviceNameFile);
			serviceNameList = serviceNameTable.stringColumn("Name").asList();
			int tableLength = serviceNameTable.rowCount();
			for (int i = 0; i < tableLength; i++) {
				String keyString = serviceNameTable.stringColumn("Name").getString(i);
				int index = serviceNameTable.intColumn("Index").get(i);
				serviceNameIndexMap.put(keyString, index);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error reading the file: " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void prepareCapitalNames(String capitalNameFile) {
		Table capitalNameTable = Table.read().csv(capitalNameFile);
		capitalNameList = capitalNameTable.stringColumn("Name").asList();
	}

	private void loadAgentsData(String agentDataDirectory) {
		File dir = new File(agentDataDirectory);
		File[] directoryListing = dir.listFiles();
		for (File agentFile : directoryListing) {
			Table agentTable = Table.read().csv(agentFile);
			agentTable.setName(agentFile.getName().replace(".csv", ""));
			Manager manager = new Manager();
			manager.setManagerType(agentTable.name());
			manager.setSensitivityTable(agentTable);
			manager.setRepresentative(true);
			agentTypeMap.put(agentTable.name(), manager);
			AFTCounter.put(agentTable.name(), 0);
			managerSet.add(manager);

			// Convert table into hashmap for improving calculation performance.
			serviceNameList.forEach(serviceName -> {
				capitalNameList.forEach(capitalName -> {
					double sensitivityValue = ((Number) manager.getSensitivityTable().column(capitalName)
							.get(serviceNameIndexMap.get(serviceName))).doubleValue();
					double productionValue = ((Number) manager.getSensitivityTable().column("Production")
							.get(serviceNameIndexMap.get(serviceName))).doubleValue();
					manager.getSensitivityMap().add(serviceName, capitalName, sensitivityValue);
					manager.getSensitivityMap().add(serviceName, "Production", productionValue);
				});
			});
		}
	}

	private void loadBaselineCellData(String cellDataPath) {

		Table table = Table.read().csv(cellDataPath);
		int rowNumber = table.rowCount();
		for (int i = 0; i < rowNumber; i++) {
			LandCell landCell = new LandCell();
			landCell.setInformationTable(table.row(i));
			landCell.initializeProductionFilter(this);

			cellSet.add(landCell);
			// int x = landCell.getInformationTable().getInt("x");
			// int y = landCell.getInformationTable().getInt("y");
			int x = landCell.getInformationTable().getInt("Longitude");
			int y = landCell.getInformationTable().getInt("Latitude");
			cellSet.addCellToMap(x, y, landCell);
			String managerTypeString = table.row(i).getString("FR");
			// clone and mutate
			Manager manager = agentTypeMap.get(managerTypeString).clone();
			manager.mutate(0.0, 0.1);
			//////////////
			landCell.setOwner(manager);
			manager.getLandSet().add(landCell);
			managerSet.add(manager);
			mapWidth = Math.max(mapWidth, x);// this variable is for map visualization
			mapHeight = Math.max(mapHeight, y);// this variable is for map visualization
			manager.setId(i);
			AFTCounter.put(managerTypeString, AFTCounter.get(managerTypeString) + 1);
		}
	}

	private void loadAnualDemandDataToIterator(String anualDemandFile) {
		anualDemandTable = Table.read().csv(anualDemandFile);
		int rowNumber = anualDemandTable.rowCount();
		Row[] anualDemandList = new Row[rowNumber];
		for (int i = 0; i < rowNumber; i++) {
			anualDemandList[i] = anualDemandTable.row(i);
		}
		anualDemandIterator = Arrays.stream(anualDemandList).iterator();
	}

	// use the table information to initalize HashMap. when calculating production,
	// using table is too slow.
	private void initializeHashMaps() {

		// initialize landCell HashMaps.
		cellSet.forEach(landCell -> {
			capitalNameList.forEach(capitalName -> {
				double capitalValue = ((Number) landCell.getInformationTable().getDouble(capitalName)).doubleValue();
				landCell.getCapitalHashMap().put(capitalName, capitalValue);
			});
		});

		serviceNameList.forEach(serviceName -> {
			initialZeroProdutionMap.put(serviceName, 0.);
		});

		// Initialize default strategy. All dimensions are 1 by default.
		serviceNameList.forEach(serviceName -> {
			currentStrategy.put(serviceName, 1.0);
		});
	}

	private void refreshGlobalProductionMap() {
		serviceNameList.forEach(serviceName -> {
			globalProductionMap.put(serviceName, 0.);
		});
	}

	public void updateAnualCellCapitals() {
		if (anualCapitalFileIterator.hasNext()) {
			File anualCapitalFile = anualCapitalFileIterator.next();
			Table anualCapitalTable = Table.read().csv(anualCapitalFile);
			int rowNumber = anualCapitalTable.rowCount();
			for (int i = 0; i < rowNumber; i++) {
				Row cellInformationTable = anualCapitalTable.row(i);
				int x = cellInformationTable.getInt("x");// not case-sensitive
				int y = cellInformationTable.getInt("y");
				AbstractCell cell = cellSet.getCell(x, y);
				if (cell != null) {
					cell.setInformationTable(cellInformationTable);
				} else {
					System.err.println("Warning: Cell not found at coordinates: " + x + ", " + y);
				}
				// cellSet.getCell(x, y).setInformationTable(cellInformationTable);
			}

			// update landCell HashMaps.
			cellSet.forEach(landCell -> {
				capitalNameList.forEach(capitalName -> {
					double capitalValue = ((Number) landCell.getInformationTable().getDouble(capitalName))
							.doubleValue();
					landCell.getCapitalHashMap().put(capitalName, capitalValue);
				});
			});
		}
	}

	public void updateDemand() {
		if (anualDemandIterator.hasNext()) {
			Row nextDemandRow = anualDemandIterator.next();
			serviceNameList.forEach(serviceName -> {
				double demandValue = ((Number) nextDemandRow.getObject(serviceName)).doubleValue();
				anualDemandMap.put(serviceName, demandValue);
			});
		}
	}

	public void updateSupply() {
		refreshGlobalProductionMap();
		for (AbstractManager manager : managerSet) {
			serviceNameList.forEach(serviceName -> {
				globalProductionMap.put(serviceName, globalProductionMap.get(serviceName)
						+ ((Manager) manager).getServiceProductionMap().get(serviceName));
			});
		}
	}

	public void updateUtility() {
		serviceNameList.forEach(serviceName -> {
			utitlityMap.put(serviceName, (anualDemandMap.get(serviceName) - globalProductionMap.get(serviceName)));
			/// globalProductionMap.get(serviceName));
		});
		// System.out.println("Utility updated: ---->> "+
		// modelRunner.schedule.getSteps() + " ----->>" + utitlityMap.get("Meat"));
	}

	public void updateCurrentStrategy(int ticks) {
		serviceNameList.forEach(serviceName -> {
			currentStrategy.put(serviceName, strategy.get(serviceName).get(ticks));
		});
	}

	public Map<String, Integer> getServiceNameIndexMap() {
		return serviceNameIndexMap;
	}

	public HashMap<String, Manager> getAgentTypeMap() {
		return agentTypeMap;
	}

	public CellSet getCellSet() {
		return cellSet;
	}

	public ManagerSet getManagerSet() {
		return managerSet;
	}

	public List<String> getCapitalNameList() {
		return capitalNameList;
	}

	public List<String> getServiceNameList() {
		return serviceNameList;
	}

//	public Table getInitialServiceTable() {
//		return initialServiceTable.copy();
//	}

	public HashMap<String, Double> getInitialZeroProductionMap() {
		return initialZeroProdutionMap;
	}

	public HashMap<String, Double> getAnualDemand() {
		return anualDemandMap;
	}

	public HashMap<String, Double> getGlobalProductionMap() {
		return globalProductionMap;
	}

	public void setGlobalProductionMap(HashMap<String, Double> globalProductionMap) {
		this.globalProductionMap = globalProductionMap;
	}

	public HashMap<String, Double> getUtitlityMap() {
		return utitlityMap;
	}

	public void setStrategy(HashMap<String, List<Double>> strategy) {
		this.strategy = strategy;
	}

	public HashMap<String, Double> getCurrentStrategy() {
		return currentStrategy;
	}

	public int getMapWidth() {
		return mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}

	public Map<String, Double> getInitSupplyMap() {
		return initSupplyMap;
	}

	public int getTime() {
		return time;
	}

	@Override
	public void toSchedule() {

	}

}
