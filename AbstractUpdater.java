package updaters;

/*
 * ==========================================
 * Class: AbstractUpdater
 * Package: updaters
 * ==========================================
 * Purpose:
 * - Serves as an abstract base class for any model component requiring setup,
 *   scheduling, or step-by-step updates within the simulation.
 * - Provides a flexible yet structured way to organize dynamic components in
 *   the CRAFTY model, enabling components to be integrated seamlessly into
 *   the model lifecycle.
 *
 * Main Components:
 * - Implements:
 *   - `ModelState`: Allows instances to be managed within the model state framework.
 *   - `Steppable`: Enables instances to be scheduled and updated on each simulation tick.
 * - Fields:
 *   - `modelRunner`: Reference to the `AbstractModelRunner` managing the simulation.
 *
 * Usage:
 * - Any component that requires initialization (setup), scheduling within the simulation,
 *   or step-based updates can extend `AbstractUpdater`.
 * - Concrete classes implement specific functionality in the setup, schedule, or step phases.
 *
 * Dependencies:
 * - Imports from `crafty`, `modelRunner`, and `sim.engine` for model state management
 *   and scheduling functionality.
 * ==========================================
 */

import crafty.ModelState;
import modelRunner.AbstractModelRunner;
import sim.engine.Steppable;

public abstract class AbstractUpdater implements ModelState, Steppable {
	protected AbstractModelRunner modelRunner;
}
