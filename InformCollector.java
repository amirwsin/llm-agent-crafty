package institution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InformCollector {

	private HashMap<String, List<Double>> informationMap = new HashMap<String, List<Double>>();

	public InformCollector(String... serivce) {
		for (String serv : serivce) {
			informationMap.put(serv, new ArrayList<Double>());
		}
	}

	public void collect(String service, double newValue) {
		informationMap.get(service).add(newValue);
	}

	public List<Double> get(String serviceName) {
		return informationMap.get(serviceName);
	}

}
