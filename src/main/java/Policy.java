package institution;
/*
 * ==========================================
 * Class: Policy
 * Package: institution
 * ==========================================
 * Purpose:
 * - Represents an individual policy within the institution framework, containing
 *   all necessary attributes to define, evaluate, and adjust policy goals.
 * - Provides functionality to track policy history, update interventions, and manage
 *   policy changes over time using a builder pattern for flexible initialization.
 *
 * Main Components:
 * - Fields:
 *   - `policyName`: Name of the policy.
 *   - `targetService`: Service targeted by the policy (e.g., Meat, Crops).
 *   - `type`: Type of policy intervention (e.g., TAX, SUBSIDY).
 *   - `initialGuess`, `inertia`, `goal`: Core attributes defining policy intervention behavior.
 *   - `policyHistory`: List storing historical interventions for tracking policy performance.
 * - Inner Class:
 *   - `Builder`: Facilitates flexible instantiation of `Policy` objects with various configurations.
 * - Methods:
 *   - `updateIntervention()`: Updates the intervention based on the current modifier.
 *   - `updatePolicyHistory()`: Adds the current intervention to the policy history.
 *   - `toString()`: Provides a string representation of the policy’s main attributes.
 *
 * Usage:
 * - Instances of `Policy` are created using the `Builder` class, allowing different policy configurations
 *   within the institution.
 *
 * Dependencies:
 * - Requires `PolicyType` enum for defining types of policy interventions.
 * ==========================================
 */

import java.util.ArrayList;
import java.util.List;

public class Policy {

	private String policyName = "Default Policy";
	private String targetService;
	private PolicyType type = PolicyType.None;
	private double initialguess = 0.0;
	private double inertia = 0.0;
	private double goal;
	double interventionModifier = 0;
	double evluation;
	int policyLag = 5;
	double intervention = 0;
	boolean startChanging = false;
	private List<Double> policyHistory = new ArrayList<Double>();
	private double interventionNeeded = 0;

	private Policy(Builder builder) {
		this.policyName = builder.policyName;
		this.type = builder.type;
		this.initialguess = builder.initialguess;
		this.inertia = builder.inertia;
		this.goal = builder.goal;
		this.policyLag = builder.policyLag;
		this.targetService = builder.targetService;
	}

	public static class Builder {
		public String targetService;
		private String policyName;
		private PolicyType type;
		private double initialguess;
		private double inertia;
		private double goal;
		private int policyLag;

		public Builder policyName(String policyName) {
			this.policyName = policyName;
			return this;
		}

		public Builder type(PolicyType type) {
			this.type = type;
			return this;
		}

		public Builder initialGuess(double initialguess) {
			this.initialguess = initialguess;
			return this;
		}

		public Builder inertia(double inertia) {
			this.inertia = inertia;
			return this;
		}

		public Builder goal(double goal) {
			this.goal = goal;
			return this;
		}

		public Builder policyLag(int policyLag) {
			this.policyLag = policyLag;
			return this;
		}

		public Builder targetService(String serviceName) {
			this.targetService = serviceName;
			return this;
		}

		public Policy build() {

			return new Policy(this);
		}
	}

	@Override
	public String toString() {
		return "Policy{" + "policyName='" + policyName + '\'' + ", type='" + type + '\'' + ", initialguess="
				+ initialguess + ", inertia=" + inertia + ", goal=" + goal + '}';
	}

	public String getName() {
		return policyName;
	}

	public double getInterventionModifier() {
		return interventionModifier;
	}

	public void setInterventionModifier(double interventionModifier) {
		this.interventionModifier = interventionModifier;
	}

	public double getEvluation() {
		return evluation;
	}

	public void setEvluation(double evluation) {
		this.evluation = evluation;
	}

	public String getTargetService() {
		return targetService;
	}

	public int getPolicyLag() {
		return policyLag;
	}

	public double getGoal() {
		return goal;
	}

	public void updateIntervention() {
		intervention = interventionModifier * initialguess;
	}

	public double getIntervention() {
		return intervention;
	}

	public double getInertia() {
		return inertia;
	}

	public PolicyType getType() {
		return type;
	}

//	public void setIntervention(double intervention) {
//		this.intervention = intervention;
//	}

	public boolean isStartChanging() {
		return startChanging;
	}

	public void setStartChanging(boolean startChanging) {
		this.startChanging = startChanging;
	}

	public double getLatestHistory() {
		return policyHistory.get(policyHistory.size() - 1);
	}

	public List<Double> getHistory() {
		return policyHistory;
	}

	public void updatePolicyHistory() {
		policyHistory.add(intervention);
	}

	public void setIntervention(double constrainedIntervention) {
		this.intervention = constrainedIntervention;

	}

	public void setGoal(double goal2) {
		this.goal = goal2;

	}

	public double getInitialGuess() {
		// TODO Auto-generated method stub
		return initialguess;
	}

	public void updateInterventionNeeded() {
		interventionNeeded = interventionModifier * initialguess;
	}

}
