/*
 * LLMInstitution Class
 *
 * This class represents an institution within a simulation that integrates with
 * a Python-based LLM (Large Language Model) agent via the Py4J library.
 * The institution collects data on supply and demand, evaluates policies,
 * adapts them based on the agent's recommendations, and implements policy changes
 * to influence the simulated system. Key responsibilities include:
 *
 * 1. Initialization and Configuration:
 *    - Set up communication with the Python LLM agent using GatewayServer.
 *    - Define and register policies for managing supply and demand dynamics.
 *
 * 2. Simulation Lifecycle:
 *    - Collect data (e.g., supply and demand information) during each simulation step.
 *    - Evaluate and adapt policies based on observed trends and agent inputs.
 *    - Apply policies to modify system states, such as utility and production levels.
 *
 * 3. Interaction with the Python Agent:
 *    - Provide the Python agent with historical data and estimated average errors.
 *    - Receive policy adjustments and incorporate them into the simulation.
 *
 * 4. Utility Methods:
 *    - Perform calculations such as converting evaluation metrics to percentages
 *      and averaging data over specified intervals.
 */

package llmExp;

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
import py4j.GatewayServer;
import sim.engine.SimState;

public class LLMInstitution extends AbstractInstitution {

	InformCollector demandCollector;
	InformCollector supplyCollector;
	String averageErrors = "";
	String actionHistory = "0";
	double initialMeatSupply;
	AgentEntry agentEntry;


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

		// Initialize GatewayServer and AgentEntry
		GatewayServer.turnLoggingOff();
		GatewayServer server = new GatewayServer();
		server.start();
		System.out.println("Server established...");
		agentEntry = (AgentEntry) server.getPythonServerEntryPoint(new Class[] { AgentEntry.class });

		initialMeatSupply = modelRunner.getState(DataCenter.class).getInitSupplyMap().get("Meat");

		Policy policy = new Policy.Builder().policyName("decrease meat").type(PolicyType.ECO)
				.goal(((LLMRunner) modelRunner).getMeatGoal() * initialMeatSupply).initialGuess(1000000.).inertia(1)
				.policyLag(((LLMRunner) modelRunner).getMeatLag()).targetService("Meat").build();
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

				//Chooese to uncomment either of the two situations below
				//======>For individual institution
				int response = agentEntry.agentRun(actionHistory, averageErrors);
				//----------------------------
				
				//======>For role-playing experiment
//				String meatDemand = averageEveryFive(demandCollector.get("Meat"),initialMeatSupply);
//				String meatSupply = averageEveryFive(supplyCollector.get("Meat"), initialMeatSupply);
//				int response = agentEntry.agentRun(actionHistory, meatDemand, meatSupply);
				//-----------------------------
				
				double incrementalIntervention = ((double) response) / 10.0;

				double interventionModifier = policy.getInterventionModifier();

				policy.setInterventionModifier(incrementalIntervention + interventionModifier);

				policy.updateInterventionNeeded();

				String currentAction = String.format("%+d", response);
				actionHistory = actionHistory + ", " + currentAction;
				System.out.println("averageErrors: " + averageErrors + "; response: " + response + "; increment: "
						+ incrementalIntervention);
				System.out.println("Meat supply: " + supplyCollector.get("Meat"));
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
		policyMap.values().forEach(policy -> {
			policy.updatePolicyHistory();
		});
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

}
