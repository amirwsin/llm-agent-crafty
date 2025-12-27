package updaters;

import institution.AbstractInstitution;
import sim.engine.SimState;

public class InfluencedUtilityUpdater extends UtilityUpdater {

	@Override
	public void step(SimState arg0) {
		super.step(arg0);

		// Institutions implement their policy here to modify utility map
		modelRunner.getStateManager().forEach(state -> {
			if (state instanceof AbstractInstitution) {
				((AbstractInstitution) state).implementPolicy();
			}
		});

	}

}
