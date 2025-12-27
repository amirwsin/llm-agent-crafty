package institution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import crafty.AbstractCell;
import crafty.DataCenter;
import crafty.LandCell;
import experiments_fuzzy.Intra;
import modelRunner.AbstractModelRunner;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import sim.engine.SimState;

public class NatureInstitution extends AbstractInstitution {

	private InformCollector supplyCollector;
	private InformCollector demandCollector;
	FunctionBlock functionBlock;
	private FunctionBlock fuzzyTax;
	private FunctionBlock fuzzySubsidy;
	private FunctionBlock fuzzyECO;
	private FunctionBlock fuzzyProect;
	HashSet<AbstractCell> unProtectedSet = new HashSet<>();

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
		// System.out.println("Institution step");
		if (modelRunner.schedule.getTime() == 0) {
			initialize();
		}

		collectInformation();
		predict();
		policyEvaluation();
		policyAdaptation();
	//	budgetUpdate();
//		resourceAllocation();
		// implementPolicy(); // this method is exectued in utilityUpdater
		updatePolicyHistory();
	}

	@Override
	protected void initialize() {
		fuzzyPrepare();
		Policy policy = new Policy.Builder().policyName("subsidy to increase diversity").type(PolicyType.SUBSIDY)
				.goal(((Intra) modelRunner).getDivGoal()
						* modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Diversity"))
				.initialGuess(10000.).inertia(0.2).policyLag(5).targetService("Diversity").build();
		this.register(policy);

		double paInertia = ((Intra) modelRunner).getPaInertia();
		policy = new Policy.Builder().policyName("Protected areas").type(PolicyType.PROTECTION)
				.goal(((Intra) modelRunner).getDivGoal()
						* modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Diversity"))
				.initialGuess(10000.).inertia(paInertia).policyLag(((Intra) modelRunner).getPolicyLag())
				.targetService("Diversity").build();
		this.register(policy);

		demandCollector = new InformCollector("Diversity");
		supplyCollector = new InformCollector("Diversity");

		modelRunner.getState(DataCenter.class).getCellSet().forEach(cell -> {
			if (cell.isProtected() == false) {
				unProtectedSet.add(cell);
			}
		});

	}

	@Override
	protected void collectInformation() {
		demandCollector.collect("Diversity", modelRunner.getState(DataCenter.class).getAnualDemand().get("Diversity"));
		supplyCollector.collect("Diversity",
				modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Diversity"));

	}

	@Override
	protected void predict() {

	}

	@Override
	protected void policyEvaluation() {
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

	@Override
	public void policyAdaptation() {
		policyMap.values().forEach(policy -> {
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
				if (policy.getType() == PolicyType.PROTECTION) {
					functionBlock = fuzzyProect;
				}
				double interventionModifier = policy.getInterventionModifier();
				functionBlock.setVariable("gap", policy.getEvluation());
				functionBlock.evaluate();
				double fuzzyResult = functionBlock.getVariable("intervention").getValue();
				double bound = Math.signum(fuzzyResult) * policy.getInertia();
				double incrementalIntervention = (Math.abs(bound) < Math.abs(fuzzyResult)) ? bound : fuzzyResult;
				policy.setInterventionModifier(incrementalIntervention + interventionModifier);
				policy.updateIntervention();
//				System.out.println("average gap: " + policy.getEvluation() + "; intervention: "
//						+ functionBlock.getVariable("intervention").getValue());
			}
		});
	}

	@Override
	protected void budgetUpdate() {
		/*
		 * The budget should be updated every year. Budget comes from two ways: a
		 * proportion of the total agricultural GDP (reflecting the individual income
		 * tax), plus extra taxes imposed by this institution (reflecting the
		 * cross-subsidization)
		 */
		// totalBugdet = 0;
		policyMap.values().forEach(policy -> {
			if ((policy.getType() == PolicyType.ECO || policy.getType() == PolicyType.TAX)
					&& !policy.getHistory().isEmpty()) {
				if (policy.getLatestHistory() < 0) { // this is to ensure the economic policy is taxing the farmers
					totalBugdet += Math.abs(policy.getLatestHistory());
				}
			}
		});

		String[] agriProductionList = { "Diversity" };
		double proportion = 0.;
		for (String production : agriProductionList) {
			totalBugdet += (proportion
					* modelRunner.getState(DataCenter.class).getGlobalProductionMap().get(production));
		}

	}

	@Override
	public void resourceAllocation() {
		/*
		 * Every year the resources should be reallocated. Should consider priority
		 * later.
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
		setProtectedAreas();
	}

	@Override
	public void updatePolicyHistory() {
		policyMap.values().forEach(policy -> {
			policy.updatePolicyHistory();
		});

	}

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
		fuzzyProect = fis.getFunctionBlock("policy");
		// Show
		// JFuzzyChart.get().chart(fuzzyTax);
		// JFuzzyChart.get().chart(fuzzySubsidy);
		// JFuzzyChart.get().chart(fuzzyECO);
	}

	public void setProtectedAreas() {
		policyMap.values().forEach(policy -> {
			if (policy.getType() == PolicyType.PROTECTION && policy.isStartChanging() == true) {
				double intervention = policy.getIntervention();
				System.out.println(intervention);
				int n = (int) intervention;
				if (n < 0) {
					n = 0;
				}
				int totalOfCellsToProtect = (int) (modelRunner.getState(DataCenter.class).getCellSet().size() * (1 - ((Intra) modelRunner).getUnprotectionLimit()));
				int numOfCellsToProtect = totalOfCellsToProtect - (modelRunner.getState(DataCenter.class).getCellSet().size() - unProtectedSet.size());
				
				List<AbstractCell> sortedList = new ArrayList<>(unProtectedSet);
				Collections.sort(sortedList, (a, b) -> Double.compare(((LandCell) b).calculateProtectionIndex(),
						((LandCell) a).calculateProtectionIndex()));

				List<AbstractCell> topN = sortedList.subList(0, Math.min(n, numOfCellsToProtect));// sortedList.size()));
				if (topN.size() != 0) {
					((Intra) modelRunner).setEndYearProtc((int) modelRunner.schedule.getSteps());
					//System.out.println(((Intra) modelRunner).getEndYearProtc());
				}

				// change the state of cells to protected and modify
				// landCell.getProductionFilter()
				topN.forEach(landCell -> {

					landCell.setProteced(true);
					landCell.getProductionFilter().put("Meat", 0.0);
					landCell.getProductionFilter().put("Crops", 0.0);
					landCell.getProductionFilter().put("Diversity", 1.0);
					landCell.getProductionFilter().put("Timber", 0.0);
					landCell.getProductionFilter().put("Carbon", 0.0);
					landCell.getProductionFilter().put("Urban", 0.0);
					landCell.getProductionFilter().put("Rereation", 0.0);

					// update production on protected area
					modelRunner.getState(DataCenter.class).getServiceNameList().forEach(service -> {
						double newProduction = landCell.getServiceProductionMap().get(service)
								* landCell.getProductionFilter().get(service);
						landCell.getServiceProductionMap().put(service, newProduction);

					});

				});

				// Remove the selected LandCell instances from unProtectedSet
				unProtectedSet.removeAll(topN);
			}
		});
		System.out.println(unProtectedSet.size()
						/ (double) (modelRunner.getState(DataCenter.class).getCellSet().size()));
	}

}
