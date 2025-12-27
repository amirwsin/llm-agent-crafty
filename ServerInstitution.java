package llmExp;

/*
 * ServerInstitution Class
 *
 * This class is designed to test predefined policy actions received from the Python side.
 *
 * Key Differences from LLMInstitution:
 * 1. Action Control:
 *    - Unlike `LLMInstitution`, actions for `ServerInstitution` are not generated dynamically
 *      by an LLM but are predefined and passed from the Python side.
 *
 * 2. Integration with ServerModelRunner:
 *    - Actions are set through the `ServerModelRunner` class, which acts as an intermediary
 *      for receiving and applying actions provided by Python.
 *
 * This class is useful for scenarios where predefined actions need to be tested.
 */


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import crafty.DataCenter;
import institution.AbstractInstitution;
import institution.InformCollector;
import institution.Policy;
import institution.PolicyType;
import modelRunner.AbstractModelRunner;
import sim.engine.SimState;

public class ServerInstitution extends AbstractInstitution {

	InformCollector demandCollector;
	InformCollector supplyCollector;
	String averageErrors = "";
	String actionHistory = "0";
	double initialMeatSupply;
	AgentEntry agentEntry;
	private List<Integer> actions;
	int index = 0;
	ArrayList<Double> averageArrayList = new ArrayList<Double>();
	int response = 0;;

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
		budgetUpdate();
		resourceAllocation();
		// implementPolicy(); // this method is exectued in utilityUpdater
		updatePolicyHistory();

	}

	@Override
	protected void initialize() {

		setActions(((ServerModelRunner) modelRunner).getActions());

		initialMeatSupply = modelRunner.getState(DataCenter.class).getInitSupplyMap().get("Meat");

		Policy policy = new Policy.Builder().policyName("decrease meat").type(PolicyType.ECO)
				.goal(((ServerModelRunner) modelRunner).getMeatGoal() * initialMeatSupply).initialGuess(1000000.).inertia(1)
				.policyLag(((ServerModelRunner) modelRunner).getMeatLag()).targetService("Meat").build();
		this.register(policy);

		supplyCollector = new InformCollector("Meat");
		demandCollector = new InformCollector("Meat");
	}

	@Override
	protected void collectInformation() {
		supplyCollector.collect("Meat", modelRunner.getState(DataCenter.class).getGlobalProductionMap().get("Meat"));
		demandCollector.collect("Meat", modelRunner.getState(DataCenter.class).getAnualDemand().get("Meat"));
	}

	@Override
	protected void predict() {
		// TODO Auto-generated method stub

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
				averageArrayList.add(averageGap);
			}
		});

	}

	@Override
	protected void policyAdaptation() {
		policyMap.values().forEach(policy -> {

			if (policy.isStartChanging()) {

				String avg_err = doubleToPercentage(policy.getEvluation());
				if (averageErrors == "") {
					averageErrors = avg_err;
				} else {
					averageErrors = averageErrors + ", " + avg_err;
				}
			
				//=======>For benchmark non-llm agents, such as ilder doing nothing. The actions are
				// derived directly from ModelRunnerInterop, which can assign a list of zeros to actions
				// in its main function.
				if (index >= 0 && index < actions.size()) {
				    response = actions.get(index);
				    index += 1;
				}
				
				double incrementalIntervention = ((double) response) / 10.0;

				double interventionModifier = policy.getInterventionModifier();

				policy.setInterventionModifier(incrementalIntervention + interventionModifier);

				policy.updateInterventionNeeded();

				String currentAction = String.format("%+d", response);
				actionHistory = actionHistory + ", " + currentAction;
//				System.out.println("averageErrors: " + averageErrors + "; response: " + response + "; increment: "
//						+ incrementalIntervention);
				System.out.println("Meat supply: " + supplyCollector.get("Meat"));
				System.out.println("Meat demand: " + demandCollector.get("Meat"));
			}
		});
	}

	@Override
	protected void budgetUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void resourceAllocation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void implementPolicy() {
		policyMap.values().forEach(policy -> {
			if (policy.getType() == PolicyType.TAX || policy.getType() == PolicyType.SUBSIDY
					|| policy.getType() == PolicyType.ECO) {
				double utility = modelRunner.getState(DataCenter.class).getUtitlityMap().get(policy.getTargetService());
				utility = utility - policy.getIntervention();
				modelRunner.getState(DataCenter.class).getUtitlityMap().put(policy.getTargetService(), utility);
			}
		});
	}

	@Override
	public void updatePolicyHistory() {
		policyMap.values().forEach(Policy::updatePolicyHistory);
	}

	@Override
	protected void fuzzyPrepare() {
		// TODO Auto-generated method stub

	}

	public String doubleToPercentage(double value) {
		// Multiplying the value by 100 and formatting it as a string with a percentage
		// sign
		return String.format("%.0f%%", value * 100);
	}
	
	public String averageEveryFive(List<Double> list, double base) {
        ArrayList<Double> outputList = new ArrayList<>();

        // Check if the input list size is a multiple of 5
        if (list.size() % 5 != 0) {
            throw new IllegalArgumentException("List size must be a multiple of 5.");
        }

        for (int i = 0; i < list.size(); i += 5) {
            double sum = 0;
            for (int j = i; j < i + 5; j++) {
                sum += list.get(j);
            }
            double average = sum / (base * 5);
            average = (new BigDecimal(average).setScale(2,RoundingMode.HALF_UP)).doubleValue(); 
            outputList.add(average);
        }

        return outputList.toString();
	}
	
	public void setActions(List<Integer> actions) {
		this.actions = actions;
	}

	public ArrayList<Double> getAverageArrayList() {
		return averageArrayList;
	}
}
