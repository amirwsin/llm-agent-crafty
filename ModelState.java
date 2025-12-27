package crafty;
/*
 * ==========================================
 * Interface: ModelState
 * Package: crafty
 * ==========================================
 * Purpose:
 * - Defines a contract for managing model state initialization and scheduling
 *   within the CRAFTY simulation framework.
 * - Ensures that implementing classes provide setup and scheduling behavior
 *   required for integration within the simulation's runtime environment.
 *
 * Methods:
 * - `setup(AbstractModelRunner abstractModelRunner)`: Sets up or initializes
 *    the model state with necessary parameters and configurations using the
 *    provided `AbstractModelRunner`.
 * - `toSchedule()`: Integrates the model state instance into the simulation
 *    scheduler, allowing for runtime execution as part of the model.
 *
 * Usage:
 * - Intended to be implemented by classes in `crafty` that need to initialize
 *   and manage their scheduling within the model's runtime.
 *
 * Dependencies:
 * - Requires `AbstractModelRunner` to initialize and configure model states.
 * ==========================================
 */


import modelRunner.AbstractModelRunner;

public interface ModelState {

	public void setup(AbstractModelRunner abstractModelRunner);

	public void toSchedule();

}
