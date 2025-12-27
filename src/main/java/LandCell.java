package crafty;

/*
 *========================================
 * An implementation of AbstractCell.
 * The content that is commented out
 * is left for another experiments on protected areas,
 * which is not included in the current paper.
 * =======================================
 */

import modelRunner.AbstractModelRunner;


public class LandCell extends AbstractCell {
// Super class variables;
// protected Row cellData;
// protected Table ownerServiceTable;
// protected Manager owner;
	protected AbstractModelRunner modelRunner;

	public Manager getOwner() {
		return owner;
	}

	public void setOwner(Manager owner) {
		this.owner = owner;
	}

//	@Override
//	public void setup(AbstractModelRunner modelRunner) {
////		initializeCapitalFilter();
////		initializeProductionFilter();
//		this.modelRunner = modelRunner;
////		initializeNeighborSet(modelRunner.getState(CellSet.class));
//
//	}

	@Override
	public void initializeCapitalFilter() {
//		capitalFilter.put("Human", 0.0);
//		capitalFilter.put("Social", 0.0);
//		capitalFilter.put("Manufactured", 0.0);
//		capitalFilter.put("Financial", 0.0);
//		capitalFilter.put("Arable.suit", 0.0);
//		capitalFilter.put("Igrass.suit", 1.0);
//		capitalFilter.put("SNGrass.suit", 1.0);
//		capitalFilter.put("Bioenergy.suit", 1.0);
//		capitalFilter.put("AgroForestry.suit", 1.0);
//		capitalFilter.put("NNConifer.suit", 1.0);
//		capitalFilter.put("NConifer.suit", 1.0);
//		capitalFilter.put("NNBroadleaf.suit", 1.0);
//		capitalFilter.put("NBroadleaf.suit", 1.0);
//		capitalFilter.put("Tree.suit", 1.0);
	}

	@Override
	public void initializeProductionFilter(DataCenter dataLoader) {
//      This is default.
		dataLoader.getServiceNameList().forEach(service -> {
			productionFilter.put(service, 1.0);
		});
//		productionFilter.put("Food.crops", 1.0);
//		productionFilter.put("Fodder.crops", 1.0);
//		productionFilter.put("GF.redMeat", 1.0);
//		productionFilter.put("Fuel", 1.0);
//		productionFilter.put("Softwood", 1.0);
//		productionFilter.put("Hardwood", 1.0);
//		productionFilter.put("Biodiversity", 1.0);
//		productionFilter.put("Carbon", 1.0);
//		productionFilter.put("Recreation", 1.0);
//		productionFilter.put("Flood.reg", 1.0);
//		productionFilter.put("Employment", 1.0);
//		productionFilter.put("Ldiversity", 1.0);
//		productionFilter.put("GF.milk", 1.0);
//		productionFilter.put("Sus.Prod", 1.0);
	}

	@Override
	public void initializeNeighborSet(CellSet cellSet) {
		int[] xOffsets = { -1, 0, 1, -1, 1, -1, 0, 1 };
		int[] yOffsets = { -1, -1, -1, 0, 0, 1, 1, 1 };
		int x = getInformationTable().getInt("x");
		int y = getInformationTable().getInt("y");
// Iterate over the eight neighbors
		for (int i = 0; i < 8; i++) {
			int neighborx = x + xOffsets[i];
			int neighbory = y + yOffsets[i];
			if (cellSet.getCell(neighborx, neighbory) != null) {
				neighborSet.add(cellSet.getCell(neighborx, neighbory));
			}
		}
	}

	@Override
	public double calculateProtectionIndex() {
//		HashMap<String, Double> tempHashMap = new HashMap<>();
//		double filteredSum = 0.0;
//		Set<String> capitalKeySet = capitalHashMap.keySet();
//// calculate the filtered capitals and their sum
//		for (String capital : capitalKeySet) {
//			tempHashMap.put(capital, capitalHashMap.get(capital) * capitalFilter.get(capital));
//			filteredSum += tempHashMap.get(capital);
//		}
//// calculate Gini impurity
//		double proportionalSquaredCapitalSum = 0;
//		for (String capital : capitalKeySet) {
//			double proportionalCapital = tempHashMap.get(capital) / filteredSum;
//			double proportionalSquaredCapital = Math.pow(proportionalCapital, 2);
//			proportionalSquaredCapitalSum += proportionalSquaredCapital;
//		}
//		double giniImpurity = 1 - proportionalSquaredCapitalSum;
//		int protectedNeighborcount = 0;
//		for (AbstractCell cell : neighborSet) {
//			if (cell.isProtected()) {
//				protectedNeighborcount += 1;
//			}
//		}
//		double neighborindex = 1.15;
//		protectionIndex = giniImpurity * filteredSum * Math.pow(neighborindex, protectedNeighborcount);
		//protectionIndex = capitalHashMap.get("Diversity");
		protectionIndex = capitalHashMap.get("Forest.productivity") + capitalHashMap.get("Grassland.productivity");
		return protectionIndex;
	}

	public double getProtectionIndex() {
		// TODO Auto-generated method stub
		return protectionIndex;
	}

	@Override
	public void initializeProductionFilter() {
		// TODO Auto-generated method stub

	}

//	@Override
//	public void toSchedule() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void step(SimState arg0) {
//		// TODO Auto-generated method stub
//		
//	}

}
