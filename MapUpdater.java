package updaters;

import java.util.HashMap;
import java.util.Set;

import crafty.CellSet;
import crafty.DataCenter;
import crafty.ModelState;
import modelRunner.AbstractModelRunner;
import modelRunner.ModelRunner;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.IntGrid2D;

public class MapUpdater extends AbstractUpdater {

	IntGrid2D landMap;
	HashMap<String, Integer> AFTIndexMap = new HashMap<String, Integer>();
	CellSet cellSet;

	@Override
	public void setup(AbstractModelRunner modelRunner) {
		this.modelRunner = modelRunner;
		this.landMap = modelRunner.landMap;
		this.cellSet = modelRunner.getState(DataCenter.class).getCellSet();
		Set<String> typeSet = modelRunner.getState(DataCenter.class).getAgentTypeMap().keySet();
		int i = 0;
		for (String typeString : typeSet) {
			AFTIndexMap.put(typeString, i);
			i++;
		}

	}

	@Override
	public void step(SimState arg0) {
		cellSet.forEach(cell -> {
			int x = cell.getInformationTable().getInt("x");
			int y = cell.getInformationTable().getInt("y");
			landMap.field[x][landMap.getHeight() - y] = AFTIndexMap.get(cell.getOwner().getManagerType());
			// landMap.field[x][y] = AFTIndexMap.get(cell.getOwner().getManagerType());
		});
	}

	@Override
	public void toSchedule() {
		modelRunner.schedule.scheduleRepeating(0, modelRunner.indexOf(this), this, 1.0);

	}

}
