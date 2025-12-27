package updaters;

import java.util.HashMap;

import crafty.DataCenter;
import crafty.ModelState;
import modelRunner.AbstractModelRunner;
import modelRunner.ModelRunner;
import sim.engine.SimState;
import sim.engine.Steppable;

public class DemandUpdater extends AbstractUpdater {

	@Override
	public void step(SimState arg0) {
		modelRunner.getState(DataCenter.class).updateDemand();
		// System.out.println("Utility Updater step");
	}

	@Override
	public void setup(AbstractModelRunner modelRunner) {
		this.modelRunner = modelRunner;

	}

	@Override
	public void toSchedule() {
		modelRunner.schedule.scheduleRepeating(0, modelRunner.indexOf(this), this, 1.0);

	}

}
