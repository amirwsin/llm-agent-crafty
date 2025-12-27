package crafty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import modelRunner.AbstractModelRunner;
import modelRunner.ModelRunner;
import tech.tablesaw.api.Table;

public class Manager extends AbstractManager {
/*
*===============================================================
* This class is an implementation of AbstractManager
*  The variables below are from AbtractManager
*	ModelRunner modelRunner;
*	Table sensitivityMatrixTable;
*	String managerType;
*	int id;
*	double competitiveness;
*	Set<AbstractCell> landSet = new HashSet<AbstractCell>();
*	Table servicesTable;
*	Table sensitivityTable;
* ==============================================================
 */

	// In CRAFTY representative agent functional types are used improve simulation speed.
	boolean representative = false;

	@Override
	public void setManagerType(String managerType) {
		// TODO Auto-generated method stub
		super.setManagerType(managerType);
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		super.setId(id);
	}

	@Override
	public void setup(AbstractModelRunner modelRunner) {
		this.modelRunner = modelRunner;

		managerProduce(); // manager should produce once to let the model calculate the utility for the 0th tick.

	}

	@Override
	public String getManagerType() {
		// TODO Auto-generated method stub
		return super.getManagerType();
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return super.getId();
	}

	@Override
	public double getCompetitiveness() {
		// TODO Auto-generated method stub
		return super.getCompetitiveness();
	}

	@Override
	public void setCompetitiveness(double competitiveness) {
		// TODO Auto-generated method stub
		super.setCompetitiveness(competitiveness);
	}

	@Override
	public Set<AbstractCell> getLandSet() {
		// TODO Auto-generated method stub
		return super.getLandSet();
	}

	@Override
	public void setLandSet(Set<AbstractCell> landSet) {
		// TODO Auto-generated method stub
		super.setLandSet(landSet);
	}

	@Override
	public Table getServicesTable() {
		// TODO Auto-generated method stub
		return super.getServicesTable();
	}

	@Override
	public void setServicesTable(Table servicesTable) {
		// TODO Auto-generated method stub
		super.setServicesTable(servicesTable);
	}

	@Override
	public Table getSensitivityTable() {
		// TODO Auto-generated method stub
		return super.getSensitivityTable();
	}

	@Override
	public void setSensitivityTable(Table sensitivityTable) {
		// TODO Auto-generated method stub
		super.setSensitivityTable(sensitivityTable);
	}

	@Override
	public void abandonCell(AbstractCell cell) {
		// TODO Auto-generated method stub
		super.abandonCell(cell);
	}

	@Override
	public void takeOverCell(AbstractCell cell) {
		// TODO Auto-generated method stub
		super.takeOverCell(cell);
	}

	@Override
	protected void managerProduce() {
		// initialize serviceProductionMap
		serviceProductionMap = new HashMap<>(modelRunner.getState(DataCenter.class).getInitialZeroProductionMap());
		List<String> serviceNameList = new ArrayList<String>(serviceProductionMap.keySet());
		// calculate and sum up service production in each owned land cells.
		landSet.forEach(landCell -> {
			HashMap<String, Double> cellServiceProductionMap = Methods.cdProductionMap((LandCell) landCell, this,
					modelRunner.getState(DataCenter.class));
			serviceNameList.forEach(serviceName -> {
				double existingServiceProduction = serviceProductionMap.get(serviceName);
				double cellServiceProduction = cellServiceProductionMap.get(serviceName);
				serviceProductionMap.put(serviceName, existingServiceProduction + cellServiceProduction);
			});
			/*
			 * Save the owner's service table in the cell for other agents to compete with.
			 * Here some performance could be improved.
			 */
			landCell.setServiceProductionMap(cellServiceProductionMap);
		});
	}

	// manager abandoning land cells has not been considered in the current implementation.
	@Override
	protected void managerAbandon() {

	}

	// Here, manager searching and competing for land cells are implemented together for their tight logical connections.
	@Override
	protected void managerSearch() {

		if (representative == true) {
			CellSet cellSet = modelRunner.getState(DataCenter.class).getCellSet();

			// search searchedNumber of others' landcells to see if there is any cell can be occupied.
			int searchedNumber = (int) (0.05 * (cellSet.size()));// - landSet.size());
			List<AbstractCell> landCells = new ArrayList<>(cellSet);
			Collections.shuffle(landCells);
			landCells = landCells.subList(0, searchedNumber + 1);
			for (AbstractCell landCell : landCells) {
				if (landCell.getOwner() != this) {

					HashMap<String, Double> mySerivceMapHere = Methods.cdProductionMap((LandCell) landCell, this,
							modelRunner.getState(DataCenter.class));
					double myCompetitiveness = Methods.calculateCompetitivness(mySerivceMapHere,
							modelRunner.getState(DataCenter.class).getUtitlityMap(), modelRunner);
					double otherCompetitiveness = Methods.calculateCompetitivness(landCell.getServiceProductionMap(),
							modelRunner.getState(DataCenter.class).getUtitlityMap(), modelRunner); // you might think
																									// this is
																									// unnecessary. This
																									// part is kept
																									// for
																									// one-owner-many-lands
																									// cases
					if (myCompetitiveness > otherCompetitiveness
							& new Random().nextDouble() < modelRunner.getThreshold()) {

						HashMap<String, Integer> AFTCounter = modelRunner.getState(DataCenter.class).AFTCounter;
						// 1.the previous owner remove this cell from its landSet and the AFTCounter
						// subtract 1 correspondingly.
						landCell.getOwner().getLandSet().remove(landCell);
						AFTCounter.put(landCell.getOwner().getManagerType(),
								AFTCounter.get(landCell.getOwner().getManagerType()) - 1);
						// 1.5 if the previous owner is not representative and has not land, then remove
						// it from ManagerSet.
						if (landCell.getOwner().isRepresentative() != true
								&& landCell.getOwner().getLandSet().size() == 0) {
							modelRunner.getState(DataCenter.class).getManagerSet().remove(landCell.getOwner());
						}
						// 2.this landCell set the owner to this new manager, and the AFTCounter plus 1
						// correspondingly.
						Manager newOwnerManager = this.clone();
						newOwnerManager.mutate(0.0, 0.1);
						modelRunner.getState(DataCenter.class).getManagerSet().add(newOwnerManager);
						landCell.setOwner(newOwnerManager);

						AFTCounter.put(landCell.getOwner().getManagerType(),
								AFTCounter.get(landCell.getOwner().getManagerType()) + 1);
						// 3.this new manager add this landCell to its landSet
						landCell.getOwner().getLandSet().add(landCell);
						// 4.this landCell update its service production according to the new owner
						HashMap<String, Double> newSerivceMapHere = Methods.cdProductionMap((LandCell) landCell,
								newOwnerManager, modelRunner.getState(DataCenter.class));
						landCell.setServiceProductionMap(newSerivceMapHere);
						landCell.getOwner().setServiceProductionMap(newSerivceMapHere);

					}
				}
			}
		}
	}

	// It is more straightforward to implement manager competition within the managerSearch method.
	@Override
	protected void managerCompete() {

	}

	// even 0.005 mutation will cause drastic fluctuations.
	public void mutate(double mutateProbability, double mutatePercentage) {
		if (representative != true) {
			Random random = new Random();
			sensitivityMap.keySet().forEach(key -> {
				if (new Random().nextDouble() < mutateProbability) {
					double value = sensitivityMap.get(key);
					double change = mutatePercentage; // max change percentage (10%)

					double percentage = (random.nextDouble() * change * 2) - change; // generate random percentage
																						// within
																						// -10%
																						// to +10%

					// change value by random percentage
					double mutatedValue = value * (1 + percentage);

					sensitivityMap.put(key, mutatedValue);
				}
			});
		}
	}

	public Manager clone() {
		if (representative = true) {
			Manager managerClone = new Manager();
			managerClone.modelRunner = this.modelRunner;
			managerClone.setLandSet(new HashSet<AbstractCell>());
			managerClone.setManagerType(this.managerType);
			managerClone.setRepresentative(false);
			// managerClone.setSensitivityTable(this.sensitivityTable.copy()); // this is
			// only meaningful for loading data.
			SensitivityMap sensitivityMapClone = new SensitivityMap();
			sensitivityMapClone.putAll(this.sensitivityMap); // create a (deep) copy of the sensitivity map
			managerClone.setSensitivityMap(sensitivityMapClone);
			// managerClone.mutate();
			return managerClone;
		}
		return null;
	}

	public boolean isRepresentative() {
		return representative;
	}

	public void setRepresentative(boolean representative) {
		this.representative = representative;
	}

	@Override
	public void toSchedule() {
		modelRunner.schedule.scheduleRepeating(0, modelRunner.indexOf(modelRunner.getState(ManagerSet.class)), this,
				1.0);
	}

}
