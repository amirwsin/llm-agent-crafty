package crafty;
/*
 * ==========================================
 * Class: SensitivityMap
 * Package: crafty
 * ==========================================
 * Purpose:
 * - Extends `HashMap` to store and manage sensitivity values for combinations
 *   of services and capitals in the simulation.
 *
 * Key Methods:
 * - `putAll(SensitivityMap sensitivityMap)`: Adds all entries from another
 *   `SensitivityMap`.
 * - `add(String serviceName, String capitalName, Double sensitivity)`: Adds a
 *   sensitivity value for a specific service-capital pair.
 * - `get(String serviceName, String capitalName)`: Retrieves the sensitivity
 *   value for a specified service-capital combination.
 *
 * ==========================================
 */

import java.util.HashMap;

public class SensitivityMap extends HashMap<String, Double> {

	private static final long serialVersionUID = 1L;

	public void putAll(SensitivityMap sensitivityMap) {
		sensitivityMap.keySet().forEach(key -> {
			this.put(key, sensitivityMap.get(key));
		});
	}

	public void add(String serviceName, String capitalName, Double sensitivity) {
		this.put(serviceName + capitalName, sensitivity);
	}

	public double get(String serviceName, String capitalName) {
		return this.get(serviceName + capitalName);
	}
}
