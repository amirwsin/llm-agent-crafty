package institution;
/*
 * ==========================================
 * Class: AgriInstitution
 * Package: institution
 * ==========================================
 * Purpose:
 * - Represents an agricultural institution that manages policies and resource allocation
 *   for agricultural outputs, adapting policies dynamically based on data collection and
 *   fuzzy logic for policy adjustments.
 * - Implements methods for collecting data, evaluating and adapting policies, updating budgets,
 *   and allocating resources to meet production goals (e.g., meat and crop production).
 *
 * Main Components:
 * - Fields:
 *   - `demandCollector`, `supplyCollector`: Objects to collect demand and supply information.
 *   - `functionBlock`, `fuzzyTax`, `fuzzySubsidy`, `fuzzyECO`: Fuzzy logic function blocks
 *      for adapting policies based on evaluation.
 *   - `averageGapList`, `incrementalList`: Lists for tracking policy evaluation metrics.
 * - Methods:
 *   - `initialize()`: Sets initial goals and policies, prepares fuzzy logic components.
 *   - `collectInformation()`: Collects current demand and supply information for target services.
 *   - `policyEvaluation()`, `policyAdaptation()`: Evaluate and adjust policies based on recent
 *     performance and fuzzy logic.
 *   - `budgetUpdate()`, `resourceAllocation()`: Updates the institution's budget and allocates
 *     resources to policies based on available budget and policy needs.
 *   - `implementPolicy()`: Implements policy interventions on utilities for target services.
 *   - `fuzzyPrepare()`: Loads and configures fuzzy logic blocks for policy evaluation.
 *
 * Usage:
 * - This class is intended to manage agricultural policies in the CRAFTY framework and
 *   dynamically adjust them using fuzzy logic.
 * - Note: the lines that register policies can be commented out for deactivation. But at least
 *   one policy should be registered.
 *
 * Dependencies:
 * - Depends on `AbstractInstitution`, `Policy`, and fuzzy logic libraries for policy
 *   evaluation and intervention adjustments.
 * ==========================================
 */
import java.util.ArrayList;
import java.util.List;
import crafty.DataCenter;
import experiments_fuzzy.Intra;
import modelRunner.AbstractModelRunner;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import sim.engine.SimState;

public class AgriInstitution extends AbstractInstitution {

	private static final long serialVersionUID = 1L;
	InformCollector demandCollector;
	InformCollector supplyCollector;
	FunctionBlock functionBlock;
	private FunctionBlock fuzzyTax;
	private FunctionBlock fuzzySubsidy;
	private FunctionBlock fuzzyECO;
	private ArrayList<Double> averageGapList = new ArrayList<>();
	private ArrayList<Double> incrementalList = new ArrayList<>();

	@Override
	public void initialize() {

		fuzzyPrepare();
		Policy policy = new Policy.Builder().policyName("decrease meat").type(PolicyType.ECO)
				.goal(((Intra) modelRunner).getMeatGoal()
						* modelRunner.getState(DataCenter.class).getInitSupplyMap().get("Meat"))
				.initialGuess(1000000.).inertia(0.2).policyLag(((Intra) modelRunner).getMeatLag())
				.targetService("Meat").build();
		this.register(policy);

		policy = new Policy.Builder().policyName("increase crop").type(PolicyType.SUBSIDY)
				 .type(PolicyType.ECO)
				.goal(((Intra) modelRunner).getCropGoal()
						* modelRunner.getState(DataCenter.class).getInitSupplyMap().get("Crops"))
				.initialGuess(1000000.).inertia(0.2).policyLag(((Intra) modelRunner).getCropLag())
				.targetService("Crops").build();
		this.register(policy);

		demandCollector = new InformCollector("Meat", "Crops");
		supplyCollector = new InformCollector("Meat", "Crops");

		totalBugdet = 0;
	}

	// Collect necessary information about ecosystem services to support decision-making.
	@Override
	public void collectInformation() {
		demandCollector.collect("Meat", modelRunner.getState(DataCenter.class).getAnualDemand().get("Meat"));
		demandCollector.collect("Crops", modelRunner.getState(DataCenter.class).getAnualDemand().get("Crops"));
		supplyCollector.collect("Meat", modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Meat"));
		supplyCollector.collect("Crops", modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Crops"));
	}

	// There is no prediction in the current numerical experiments. We experimented with several predictive methods, but the results
	// did not manifest notable differences.
	@Override
	public void predict() {
		// Placeholder for prediction logic.
	}

	// Ths agricultural institution evaluates policy performance based on the annual average gaps over the periods of
	// policy time lags. That is the PID errors with the weight of the integral errors equalling 1 while other errors 0.
	// This is for simplifying the model parameterisation and avoiding potential output fluctuations that may be distracting.
	@Override
	public void policyEvaluation() {
		policyMap.values().forEach(policy -> {
			List<Double> historicalSupply = supplyCollector.get(policy.getTargetService());
			if (historicalSupply.size() >= policy.getPolicyLag()
					& historicalSupply.size() % policy.getPolicyLag() == 0) {
				policy.setStartChanging(true);
			} else {
				policy.setStartChanging(false);
			}
			if (policy.isStartChanging()) {
				int start = historicalSupply.size() - policy.getPolicyLag();
				int end = historicalSupply.size();
				List<Double> recentSupply = historicalSupply.subList(start, end);
				double averageGap = 0;
				for (int i = 0; i < policy.getPolicyLag(); i++) {
					averageGap = averageGap + (policy.getGoal() - recentSupply.get(i)) / policy.getGoal();
				}
				averageGap = averageGap / policy.getPolicyLag();

				policy.setEvluation(averageGap);
			}
		});
	}

	// Adjusts policies based on fuzzy logic evaluation results.
	@Override
	public void policyAdaptation() {
		policyMap.values().forEach(policy -> {
			// Decide which fuzzy controller setting to use
			if (policy.isStartChanging()) {
				if (policy.getType() == PolicyType.TAX) {
					functionBlock = fuzzyTax;
				}
				if (policy.getType() == PolicyType.SUBSIDY) {
					functionBlock = fuzzySubsidy;
				}
				if (policy.getType() == PolicyType.ECO) {
					functionBlock = fuzzyECO;
				}

				double interventionModifier = policy.getInterventionModifier();
				// Feed the fuzzy logic controller with the "gap" and let it return the result.
				functionBlock.setVariable("gap", policy.getEvluation());
				functionBlock.evaluate();
				double fuzzyResult = functionBlock.getVariable("intervention").getValue();
				// Applying policy intertia constraint while maintaining the sign of the output of
				// the fuzzy logic controller to bound the policy change.
				double bound = Math.signum(fuzzyResult) * policy.getInertia();
				double incrementalIntervention = (Math.abs(bound) < Math.abs(fuzzyResult)) ? bound : fuzzyResult;
				policy.setInterventionModifier(incrementalIntervention + interventionModifier);
				policy.updateIntervention();

				System.out.println(
						"average gap: " + policy.getEvluation() + "; intervention: " + policy.getIntervention() + "; modifier: " + policy.getInterventionModifier());// +
				averageGapList.add(policy.getEvluation());																								// functionBlock.getVariable("intervention").getValue());
				incrementalList.add(incrementalIntervention);
				System.out.println("Gaps: " + averageGapList);
				System.out.println("Incre: " + incrementalList);
			}
			
		});
	}

	@Override
	protected void budgetUpdate() {
		/*
		 * Budget should be updated every year. Budget comes from two ways: a
		 * proportion of the total agricultural GDP (reflecting the individual income
		 * tax), plus extra taxes imposed by this institution (reflecting the
		 * cross-subsidization). In the current experiments, no GDP is considered.
		 * The only source of institutional income is tax.
		 */

		policyMap.values().forEach(policy -> {
			if ((policy.getType() == PolicyType.ECO || policy.getType() == PolicyType.TAX)
					&& !policy.getHistory().isEmpty()) {
				if (policy.getLatestHistory() < 0) { // this is to ensure the economic policy is taxing the farmers
					totalBugdet += Math.abs(policy.getLatestHistory());
				}
			}
		});

		// The following code provides a simplified way to consider GDP, which is proportional to the total production of meat crop.
//		String[] agriProductionList = { "Meat", "Crops" };
//		double proportion = 1;
//		for (String production : agriProductionList) {
//			totalBugdet += (proportion
//					* modelRunner.getState(DataCenter.class).getGlobalProductionMap().get(production));
//		}

	}

	@Override
	public void resourceAllocation() {
		/*
		 * Every year the resources should be reallocated. Should consider priorities
		 * if multiple policies are competing for budgets.
		 */
		policyMap.values().forEach(policy -> {
			if (policy.getType() == PolicyType.SUBSIDY
					|| (policy.getType() == PolicyType.ECO && policy.getIntervention() > 0)) {
				double intervention = policy.getIntervention();
				intervention = (intervention < totalBugdet) ? intervention : totalBugdet;
				policy.setIntervention(intervention);
				totalBugdet += -intervention;
			}
		});

	}

	// Implementing the policies by influencing the perceived utility of land users.
	// This procedure is placed in an Updater because policies are not implemented immediately but in the next year.
	@Override
	public void implementPolicy() {
		policyMap.values().forEach(policy -> {
			if (policy.getType() == PolicyType.TAX || policy.getType() == PolicyType.SUBSIDY
					|| policy.getType() == PolicyType.ECO) {
				double utility = modelRunner.getState(DataCenter.class).getUtitlityMap().get(policy.getTargetService());
				utility = utility + policy.getIntervention();
				modelRunner.getState(DataCenter.class).getUtitlityMap().put(policy.getTargetService(), utility);
			}
		});
	}

	@Override
	public void updatePolicyHistory() {
		policyMap.values().forEach(policy -> {
			policy.updatePolicyHistory();
		});

	}

	// Initialising the fuzzy logic controllers.
	@Override
	public void fuzzyPrepare() {
		// Load from 'FCL' file
		String fileName = "resources/fcl/fuzzyPolicy.fcl";
		FIS fis = FIS.load(fileName, true);

		// Error while loading?
		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return;
		}

		// Get policy function block
		fuzzyTax = fis.getFunctionBlock("tax");
		fuzzySubsidy = fis.getFunctionBlock("subsidy");
		fuzzyECO = fis.getFunctionBlock("policy");
		// Show
		// JFuzzyChart.get().chart(fuzzyTax);
		// JFuzzyChart.get().chart(fuzzySubsidy);
		// JFuzzyChart.get().chart(fuzzyECO);
	}

	@Override
	public void setup(AbstractModelRunner modelRunner) {
		this.modelRunner = modelRunner;

	}

	@Override
	public void toSchedule() {
		modelRunner.schedule.scheduleRepeating(0, modelRunner.indexOf(this), this, 1.0);
	}

	@Override
	public void step(SimState arg0) {
		if (modelRunner.schedule.getTime() == 0) {
			initialize();
		}

		collectInformation();
		predict();
		policyEvaluation();
		policyAdaptation();
		budgetUpdate();
		resourceAllocation();
		// implementPolicy(); // this method is executed in InfluencedUtilityUpdater to maintain a consistent model architecture.
		updatePolicyHistory();
	}

}
