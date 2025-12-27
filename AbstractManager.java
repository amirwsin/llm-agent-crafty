package crafty;
/*
 * ==========================================
 * Class: AbstractManager
 * Package: crafty
 * ==========================================
 * Purpose:
 * - Defines an abstract class representing a management entity
 *   within the land-use model, responsible for overseeing and
 *   interacting with a set of land cells.
 * - Provides foundational methods and fields to manage production,
 *   abandonment, competition, and search processes across land cells.
 *
 * Main Components:
 * - Fields:
 *   - `competitiveness`, `managerType`, `id`.
 *   - `landSet`: The collection of land cells under this manager's
 *      control.
 *   - `servicesTable`, `sensitivityTable`, `sensitivityMap`: Data
 *      structures for tracking production and sensitivity.
 * - Methods:
 *   - `managerProduce()`, `managerAbandon()`, `managerSearch()`,
 *     `managerCompete()`: Abstract methods defining key behaviors
 *     to be implemented by subclasses.
 *   - `takeOverCell(AbstractCell cell)`, `abandonCell(AbstractCell cell)`:
 *     Methods for adding/removing land cells to/from the manager's control.
 * - Implements the `Steppable` interface for integration into the
 *   simulation’s step-based execution.
 *
 * Usage:
 * - This abstract class is intended to be extended by specific
 *   manager types that define unique behaviors in the land-use
 *   management process.
 *
 * Dependencies:
 * - Relies on `AbstractUpdater` as a superclass and imports
 *   functionalities from `modelRunner`, `Tablesaw API`, and
 *   simulation libraries.
 * ==========================================
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import modelRunner.AbstractModelRunner;
import modelRunner.ModelRunner;
import sim.engine.SimState;
import sim.engine.Steppable;
import tech.tablesaw.api.Table;
import updaters.AbstractUpdater;

public abstract class AbstractManager extends AbstractUpdater {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected AbstractModelRunner modelRunner;
	protected String managerType;
	protected int id;
	protected double competitiveness;
	protected Set<AbstractCell> landSet = new HashSet<AbstractCell>();
	protected Table servicesTable; // to store all total production table
	protected Table sensitivityTable;
	protected SensitivityMap sensitivityMap = new SensitivityMap();
	protected HashMap<String, Double> serviceProductionMap = new HashMap<>();

	public HashMap<String, Double> getServiceProductionMap() {
		return serviceProductionMap;
	}

	public void setServiceProductionMap(HashMap<String, Double> serviceProductionMap) {
		this.serviceProductionMap = serviceProductionMap;
	}

	public void setManagerType(String managerType) {
		this.managerType = managerType;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public void setup(AbstractModelRunner modelRunner) {

	}

	protected abstract void managerProduce();

	protected abstract void managerAbandon();

	protected abstract void managerSearch();

	protected abstract void managerCompete();

	public String getManagerType() {
		return managerType;
	}

	public int getId() {
		return id;
	}

	public double getCompetitiveness() {
		return competitiveness;
	}

	public void setCompetitiveness(double competitiveness) {
		this.competitiveness = competitiveness;
	}

	public Set<AbstractCell> getLandSet() {
		return landSet;
	}

	public void setLandSet(Set<AbstractCell> landSet) {
		this.landSet = landSet;
	}

//	public Set<LandCell> getSearchedLandSet() {
//		return searchedLandSet;
//	}
//
//	public void setSearchedLandSet(Set<LandCell> searchedLandSet) {
//		this.searchedLandSet = searchedLandSet;
//	}

	public Table getServicesTable() {
		return servicesTable;
	}

	public void setServicesTable(Table servicesTable) {
		this.servicesTable = servicesTable;
	}

	public Table getSensitivityTable() {
		return sensitivityTable;
	}

	public void setSensitivityTable(Table sensitivityTable) {
		this.sensitivityTable = sensitivityTable;
	}

	public void abandonCell(AbstractCell cell) {
		landSet.remove(cell);
	}

	public void takeOverCell(AbstractCell cell) {
		landSet.add(cell);
	}

	public SensitivityMap getSensitivityMap() {
		return sensitivityMap;
	}

	public void setSensitivityMap(SensitivityMap sensitivityMap) {
		this.sensitivityMap = sensitivityMap;
	}

	@Override
	public void step(SimState args) {
//		managerProduce();
		managerAbandon();
		managerSearch();
		managerCompete();

	}
}
