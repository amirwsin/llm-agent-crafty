package updaters;

import java.util.HashMap;

import crafty.DataCenter;
import modelRunner.AbstractModelRunner;
import modelRunner.ModelRunner;
import sim.engine.SimState;

public class SupplyInitializer extends AbstractUpdater {

	@Override
	public void step(SimState arg0) {
		modelRunner.getState(DataCenter.class).updateSupply();
		HashMap<String, Double> initMap = modelRunner.getState(DataCenter.class).getGlobalProductionMap();
		initMap.keySet().forEach(service -> {
			modelRunner.getState(DataCenter.class).getInitSupplyMap().put(service, initMap.get(service));
		});

	}

	@Override
	public void setup(AbstractModelRunner modelRunner) {
		this.modelRunner = modelRunner;

	}

	@Override
	public void toSchedule() {
		modelRunner.schedule.scheduleOnce(0, modelRunner.indexOf(this), this);

	}
}
