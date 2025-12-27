package crafty;
/*
*======================================================
* The Methods class offers a set of static methods that
* can be used to calculate ecosystem service production
* and land user/manager competitiveness within each land
* cell.
* ======================================================
 */
import java.util.HashMap;

import modelRunner.AbstractModelRunner;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

public class Methods {

	public static Table cdProduction(LandCell landCell, Manager manager, DataCenter dataLoader) {
		// Table modifiableServiceTable = dataLoader.getInitialServiceTable();
		Table modifiableServiceTable = Table.create();
		for (String serviceName : dataLoader.getServiceNameList()) {
			double serviceProduction = 1;
			for (String capitalName : dataLoader.getCapitalNameList()) {
				double power = manager.getSensitivityMap().get(serviceName, capitalName);
				double base = landCell.getCapitalHashMap().get(capitalName);
				serviceProduction *= Math.pow(base, power);
			}

			serviceProduction *= manager.getSensitivityMap().get(serviceName, "Production");
			DoubleColumn thisScerviceColumn = DoubleColumn.create(serviceName, serviceProduction);
			modifiableServiceTable.addColumns(thisScerviceColumn);
		}
		return modifiableServiceTable;
	}

	public static HashMap<String, Double> cdProductionMap(LandCell landCell, Manager manager, DataCenter dataLoader) {
		// Table modifiableServiceTable = dataLoader.getInitialServiceTable();
		// Table modifiableServiceTable = Table.create();
		HashMap<String, Double> serviceProductionHashMap = new HashMap<>();
		for (String serviceName : dataLoader.getServiceNameList()) {
			double serviceProduction = 1;
			for (String capitalName : dataLoader.getCapitalNameList()) {
				double power = manager.getSensitivityMap().get(serviceName, capitalName);
				double base = landCell.getCapitalHashMap().get(capitalName);
				serviceProduction *= Math.pow(base, power);  // Cobb-Douglas production function
			}

			serviceProduction *= manager.getSensitivityMap().get(serviceName, "Production")
					* landCell.getProductionFilter().get(serviceName); // only when protected areas are effective
			serviceProductionHashMap.put(serviceName, serviceProduction);
		}
		return serviceProductionHashMap;
	}

	public static double simpleSumServiceProduction(Table serviceTable) {
		double simpleSum = 0;
		for (String serviceName : serviceTable.columnNames()) {
			simpleSum += serviceTable.doubleColumn(serviceName).get(0);
		}
		return simpleSum;
	}

	public static double simpleSumServiceProduction(HashMap<String, Double> serviceProductionMap) {
		double simpleSum = 0;
		for (String serviceName : serviceProductionMap.keySet()) {
			simpleSum += serviceProductionMap.get(serviceName);
		}
		return simpleSum;
	}

	public static double calculateCompetitivness(HashMap<String, Double> productionHashMap,
			HashMap<String, Double> utilityhaHashMap) {
		double competitivness = 0;
		for (String serviceName : productionHashMap.keySet()) {
			competitivness += productionHashMap.get(serviceName) * utilityhaHashMap.get(serviceName);
		}
		return competitivness;
	}

	public static double calculateCompetitivness(HashMap<String, Double> productionHashMap,
			HashMap<String, Double> utilityhaHashMap, AbstractModelRunner modelRunner) {
		double competitivness = 0;
		for (String serviceName : productionHashMap.keySet()) {
			competitivness += productionHashMap.get(serviceName) * utilityhaHashMap.get(serviceName);
		}
		return competitivness;
	}
}
