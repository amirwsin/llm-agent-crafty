package institution;

/*
*This class is experimental, and it is not used in the paper's experiments.
* It is kept here for potential contributors who want to include predictions
* in the policymaking process.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.antlr.analysis.SemanticContext.AND;

import crafty.DataCenter;
import modelRunner.ModelRunner;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class PolicyMaker {

	protected HashMap<String, SimplePolicy> policyMap = new HashMap<>();
	protected double supplyPredicted;
	protected double learningRate = 1.;
	protected Integer id = null;
	FunctionBlock functionBlock;
	double initialGuess;
	int policyLag = 5;

	// 1. Set goals.
	// 1.1 Using two-points method decompose the targets linearly.
	public void setGoals(String serviceType, int startYear, int endYear, Double startQuantity, Double endQuantity) {
		List<Double> goalsDoubles = new ArrayList<>();
		double slope = (double) (endQuantity - startQuantity) / (double) (endYear - startYear);
		double intercept = startQuantity - slope * startYear;
		for (int i = startYear; i <= endYear; i++) {
			goalsDoubles.add(slope * (double) i + intercept);
		}
		SimplePolicy policy = new SimplePolicy();
		policy.setServiceType(serviceType);
		policy.setPolicyGoal(endQuantity);
		policy.setDecomposedGoals(goalsDoubles);
		policyMap.put(serviceType, policy);
	}

	public void setGoals(ModelRunner modelRunner, int serviceIndex, Double quantity) {

		String serviceType = modelRunner.getState(DataCenter.class).getServiceNameList().get(serviceIndex);
		int startYear = 0;
		int endYear = (int) modelRunner.schedule.getTime();
		Double startQuantity = modelRunner.getState(DataCenter.class).getInitSupplyMap().get(serviceType);
		Double endQuantity = quantity * startQuantity;
		setGoals(serviceType, startYear, endYear, startQuantity, endQuantity);
		fuzzyPrepare();
	}

	public void setFinalGoal(ModelRunner modelRunner, int serviceIndex, double quantity, double initialGuess) {
		String serviceType = modelRunner.getState(DataCenter.class).getServiceNameList().get(serviceIndex);
		int startYear = 0;
		int endYear = modelRunner.totalTicks;
		Double startQuantity = quantity * modelRunner.getState(DataCenter.class).getInitSupplyMap().get(serviceType);
		Double endQuantity = startQuantity;
		setGoals(serviceType, startYear, endYear, startQuantity, endQuantity);
		fuzzyPrepare();
		this.initialGuess = initialGuess;
	}

	public List<Double> getGoals(String serviceType) {
		return policyMap.get(serviceType).getDecomposedGoals();
	}

	// 2. Predict next-year demand.
	public Double predictDemand(List<Double> list) {
		return exponentialSmoothing(list, 0.2);
	}

	// 3. Evaluate historic policy effectiveness.
	// 4. Adjust policy intervention coefficient.
	public void updatePolicyModifier(String service, List<Double> historicalSupply, double predictedSupply) {
		SimplePolicy policy = policyMap.get(service);
		double interventionModifier = policy.getIntervModifier();
		// policyLag = 5;
		if (historicalSupply.size() >= policyLag & historicalSupply.size() % 5 == 0) {
			int start = historicalSupply.size() - policyLag;
			int end = historicalSupply.size();
			List<Double> recentSupply = historicalSupply.subList(start, end);
			List<Double> recentGoals;
			if (end <= policy.getDecomposedGoals().size()) {
				recentGoals = policy.getDecomposedGoals().subList(start, end);
			} else {
				double finalGoal = policy.getDecomposedGoals().get(policy.getDecomposedGoals().size() - 1);
				recentGoals = new ArrayList<>(Collections.nCopies(policyLag, finalGoal));
			}

			double averageGap = 0;
			for (int i = 0; i < policyLag; i++) {
				averageGap = averageGap + (recentGoals.get(i) - recentSupply.get(i)) / recentGoals.get(i);
			}
			averageGap = averageGap / policyLag;
			// policy.setIntervModifier((0 + averageGap * this.learningRate) +
			// interventionModifier);
			functionBlock.setVariable("gap", averageGap);
			functionBlock.evaluate();
			policy.setIntervModifier(functionBlock.getVariable("intervention").getValue() + interventionModifier);
			System.out.println("average gap: " + averageGap + "; intervention: "
					+ functionBlock.getVariable("intervention").getValue());

		}

	}

	// 3. Predict next-year supply.
//	public void predictSupply(ArrayList<Double> supplyHistory) {
//		supplyPredicted = interventionModifier * exponentialSmoothing(supplyHistory, 0.8);
//	}

	public Double predictSupply(List<Double> list) {
		return exponentialSmoothing(list, 0.5);
	}

	// 6. Make policy
	public Double makePolicy(String service, int ticks, double predictedAnualDemand, double predictedSupply) {
		SimplePolicy policy = policyMap.get(service);
		// making policy for next tick, so the goal should be the goal in next tick.
//		double intervention = (policy.getIntervModifier() * policy.getDecomposedGoals().get(ticks + 1)
//				- predictedAnualDemand) / predictedSupply;//
//		double intervention = (1 * policy.getDecomposedGoals().get(ticks + 1) - predictedAnualDemand) / predictedSupply;//
		// initialGuess = 30000.;
		double intervention = policy.getIntervModifier() * initialGuess;/// predictedSupply;
		return intervention;
	}

	private double exponentialSmoothing(List<Double> list, double alpha) {
		double prevSmoothed = list.get(0);

		// Apply exponential smoothing to each data point
		for (int i = 1; i < list.size(); i++) {
			double currValue = list.get(i);
			double currSmoothed = alpha * currValue + (1 - alpha) * prevSmoothed;
			prevSmoothed = currSmoothed;
		}

		return prevSmoothed;
	}

//	public double getIntervention() {
//		return intervention;
//	}

	public double getSupplyPredicted() {
		return supplyPredicted;
	}

	public void setSupplyPredicted(double supplyPredicted) {
		this.supplyPredicted = supplyPredicted;
	}

//	public double sigmoid(double x) {
//		return 1.1 / (1 + Math.exp(-x));
//	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public HashMap<String, SimplePolicy> getPolicyMap() {
		return policyMap;
	}

	public void fuzzyPrepare() {
		// Load from 'FCL' file
		String fileName = "resources/fcl/tipper.fcl";
		FIS fis = FIS.load(fileName, true);

		// Error while loading?
		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return;
		}

		// Get policy function block
		functionBlock = fis.getFunctionBlock("policy");

		// Show
		// JFuzzyChart.get().chart(functionBlock);

//		// Set inputs
//		functionBlock.setVariable("gap", -0.0);
//
//		// Evaluate
//		functionBlock.evaluate();

		// Show output variable's chart
		Variable interven = functionBlock.getVariable("intervention");
		// JFuzzyChart.get().chart(interven, interven.getDefuzzifier(), true);
	}

	public double getInitialGuess() {
		return initialGuess;
	}

	public void setInitialGuess(double initialGuess) {
		this.initialGuess = initialGuess;
	}

	public int getPolicyLag() {
		return policyLag;
	}

	public void setPolicyLag(int policyLag) {
		this.policyLag = policyLag;
	}
}
