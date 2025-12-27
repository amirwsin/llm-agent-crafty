package institution;
/*
 * ==========================================
 * Class: AbstractInstitution
 * Package: institution
 * ==========================================
 * Purpose:
 * - Represents an abstract institution framework within the CRAFTY model,
 *   defining essential methods for policy management, budget allocation,
 *   and institutional decision-making.
 * - Provides a structure for implementing various policy-related behaviors
 *   through an extensible and adaptable interface.
 *
 * Main Components:
 * - Fields:
 *   - `name`: Name of the institution.
 *   - `uncertainties`: Represents uncertainties associated with the institution.
 *   - `totalBudget`: Budget available for the institution's policies.
 *   - `policyMap`: Stores policies associated with the institution by name.
 * - Methods:
 *   - `initialize()`: Prepares the institution's initial configuration (abstract).
 *   - `collectInformation()`: Collects relevant data for policy decision-making (abstract).
 *   - `policyEvaluation()`, `policyAdaptation()`: Processes for evaluating and adapting policies (abstract).
 *   - `register(Policy policy)`: Registers a policy in the institution's policy map.
 *   - `implementPolicy()`: Applies active policies as defined by institution-specific logic (abstract).
 *
 * Usage:
 * - Intended to be extended by concrete institution classes, such as agricultural
 *   institutions or environmental agencies, which define specific behaviors.
 *
 * Dependencies:
 * - Depends on `AbstractUpdater`, `AbstractModelRunner`, and `Policy` for
 *   state management and policy handling.
 * ==========================================
 */
import java.util.HashMap;

import crafty.ModelState;
import modelRunner.AbstractModelRunner;
import modelRunner.ModelRunner;
import sim.engine.Steppable;
import updaters.AbstractUpdater;

public abstract class AbstractInstitution extends AbstractUpdater {

	private static final long serialVersionUID = 1L;
	protected String name;
	protected double uncertainties;
	protected double totalBugdet;
	protected HashMap<String, Policy> policyMap = new HashMap<String, Policy>();
	protected AbstractModelRunner modelRunner;

	protected abstract void initialize();

	protected abstract void collectInformation();

	protected abstract void predict();

	protected abstract void policyEvaluation();

	protected abstract void policyAdaptation();

	protected abstract void budgetUpdate();

	protected abstract void resourceAllocation();

	public abstract void implementPolicy();

	public abstract void updatePolicyHistory();

	protected abstract void fuzzyPrepare();

	public void register(Policy policy) {
		policyMap.put(policy.getName(), policy);
	}

	public Policy getPolicy(String policyName) {
		return policyMap.get(policyName);
	}

	public double getTotalBugdet() {
		return totalBugdet;
	}

//	public void setTotalBugdet(double totalBugdet) {
//		this.totalBugdet = totalBugdet;
//	}
	public HashMap<String, Policy> getPolicyMap() {
		return policyMap;
	}

}
