package institution;

import java.util.ArrayList;
import java.util.List;

public class SimplePolicy {
	String serviceType;
	double policyGoal;
	double interventionModifier = 0;
	double intervention = 0;
	List<Double> decomposedGoals = new ArrayList<>();
	List<Double> policyApplied = new ArrayList<>();
	List<Double> predictedDemand = new ArrayList<>();
	List<Double> predictedSupply = new ArrayList<>();

	public String getServiceType() {
		return serviceType;
	}

	public double getPolicyGoal() {
		return policyGoal;
	}

	public List<Double> getDecomposedGoals() {
		return decomposedGoals;
	}

	public List<Double> getPolicyApplied() {
		return policyApplied;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public void setPolicyGoal(double policyGoal) {
		this.policyGoal = policyGoal;
	}

	public void setDecomposedGoals(List<Double> decomposedGoals) {
		this.decomposedGoals = decomposedGoals;
	}

	public List<Double> getPredictedDemand() {
		return predictedDemand;
	}

	public List<Double> getPredictedSupply() {
		return predictedSupply;
	}

	public void setIntervModifier(double intervModifier) {
		this.interventionModifier = intervModifier;
	}

	public double getIntervModifier() {
		return this.interventionModifier;
	}

	public void setIntervention(double intervention) {
		this.intervention = intervention;

	}

	public double getIntervention() {
		return this.intervention;
	}

}
