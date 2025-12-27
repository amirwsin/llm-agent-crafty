package crafty;
/*
 * ==========================================
 * Class: ManagerSet
 * Package: crafty
 * ==========================================
 * Purpose:
 * - Represents a specialized set of `AbstractManager` instances that
 *   facilitates the management of multiple agents within the simulation.
 * - Provides methods for initializing and organizing manager agents
 *   and setting them up within the simulation model.
 *
 * Main Components:
 * - Fields:
 *   - `managerList`: A list representation of the set, allowing for
 *      ordered operations such as shuffling.
 * - Methods:
 *   - `generateAgents(int numberOfAManagers)`: Generates a specified number
 *     of `AbstractManager` agents (to be implemented). In the current model,
 *     this function is not used.
 *   - `findAgent(String nameString, int id)`: Searches for and retrieves an
 *     agent by name and ID (to be implemented). The same, not useful in the
 * 		current model, but might be useful for feature development.
 *   - `setup(AbstractModelRunner modelRunner)`: Initializes the manager list,
 *     shuffles agents for randomized access, and sets up each manager
 *     with the provided model runner.
 *   - `toSchedule()`: Schedules each agent in the set within the simulation.
 *
 * Usage:
 * - This class is intended to be used to manage and initialize agent
 *   managers in the `crafty` package, integrating them within the model’s
 *   runtime scheduling.
 *
 * Dependencies:
 * - Relies on `AbstractModelRunner` for integration with the model
 *   and `ModelState` for scheduling behavior.
 * ==========================================
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import modelRunner.AbstractModelRunner;
import modelRunner.ModelRunner;

public class ManagerSet extends HashSet<AbstractManager> implements ModelState {
	List<AbstractManager> managerList;

	public void generateAgents(int numberOfAManagers) {
		// TODO Auto-generated method stub

	}

	public AbstractManager findAgent(String nameString, int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setup(AbstractModelRunner modelRunner) {
		managerList = new ArrayList<>(this);
		Collections.shuffle(managerList);
		this.forEach(agent -> {
			agent.setup(modelRunner);
		});
	}

	@Override
	public boolean addAll(Collection<? extends AbstractManager> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void toSchedule() {
		this.forEach(agent -> {
			agent.toSchedule();
		});

	}

}
