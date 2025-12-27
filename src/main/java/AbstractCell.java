package crafty;
/*
 * ==========================================
 * Class: AbstractCell
 * Package: crafty
 * ==========================================
 * Purpose:
 * - Represents an abstract cell that stores and manages data for
 *   various capital and service production values in the land-use
 *   simulation.
 * - Designed to interact with data from Tablesaw API and support
 *   functionality within CRAFTY model frameworks.
 *
 * Main Components:
 * - Fields: Stores capital, service production, protection status,
 *   and ownership information.
 * - Methods: Provides access and modification methods for capitals,
 *   service productions, and other key attributes. Includes
 *   abstract methods for initializing capital/production filters
 *   and calculating protection indices, to be defined by subclasses.
 *
 * Usage:
 * - This abstract class is meant to be extended by specific land cell
 *   types that implement land-use functionalities within a model.
 *
 * ==========================================
 */


import java.util.HashMap;
import java.util.HashSet;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import updaters.AbstractUpdater;

public abstract class AbstractCell {
	protected Row cellData;
	protected Table cellServiceTable;
	protected Manager owner;
	protected HashMap<String, Double> capitalHashMap = new HashMap<>();
	protected HashMap<String, Double> serviceProductionMap = new HashMap<>();
	protected boolean isProtected = false;
	protected HashMap<String, Double> capitalFilter = new HashMap<>();
	protected HashMap<String, Double> productionFilter = new HashMap<>();
	protected double protectionIndex;
	protected HashSet<AbstractCell> neighborSet = new HashSet<>();

	public HashMap<String, Double> getCapitalHashMap() {
		return capitalHashMap;
	}

	public Manager getOwner() {
		return owner;
	}

	public void setOwner(Manager owner) {
		this.owner = owner;
	}

	public Table getCellServiceTable() {
		return cellServiceTable;
	}

	public void setCellServiceTable(Table cellServiceTable) {
		this.cellServiceTable = cellServiceTable;
	}

	public Row getInformationTable() {
		return cellData;
	}

	public void setInformationTable(Row cellData) {
		this.cellData = cellData;
	}

	public HashMap<String, Double> getServiceProductionMap() {
		return serviceProductionMap;
	}

	public void setServiceProductionMap(HashMap<String, Double> serviceProductionMap) {
		this.serviceProductionMap = serviceProductionMap;
	}

	public abstract void initializeCapitalFilter();

	public abstract void initializeProductionFilter();

	public abstract void initializeNeighborSet(CellSet cellSet);

	public abstract double calculateProtectionIndex();

	public boolean isProtected() {
		return isProtected;
	}

	public void setProteced(boolean protectOrNot) {
		this.isProtected = protectOrNot;
	}

	public HashMap<String, Double> getProductionFilter() {
		return productionFilter;
	}

	public void initializeProductionFilter(DataCenter dataLoader) {
		// TODO Auto-generated method stub

	}
}
