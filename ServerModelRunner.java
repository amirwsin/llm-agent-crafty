package llmExp;

/*
 * ServerModelRunner Class
 *
 * This class is designed to be invoked from the Python side via Py4J.
 * It provides the following key functionalities:
 *
 * 1. Policy Actions:
 *    - The `setActions` method allows passing a list of predefined policy actions
 *      from Python to the `ServerInstitution`. These actions adjust the meat tax
 *      dynamically based on Python-side computations.
 *
 * 2. Simulation Control:
 *    - The `run` method accepts an integer specifying the maximum number of simulation
 *      iterations. It executes the simulation and returns a string containing a list
 *      of errors generated during the run.
 *
 * 3. Error Aggregation:
 *    - The returned error string can to be converted into a Python list for computing aggregated
 *      error.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import crafty.DataCenter;
import display.GridOfCharts;
import modelRunner.ModelRunner;
import updaters.CapitalUpdater;
import updaters.DemandUpdater;
import updaters.InfluencedUtilityUpdater;
import updaters.SupplyInitializer;
import updaters.SupplyUpdater;
import sim.engine.SimState;


public class ServerModelRunner extends ModelRunner {

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	////////////Experimental parameters///////////////////
	private double meatGoal = 1;
	private double cropGoal = 4;
	private double divGoal = 2;
	private double limit = 0.66;
	private int policyLag = 5;
	private int meatLag = 5;
	private int cropLag = 5;
	private double paInertia = 0.1;
	protected double threshold = 0.3;
	protected int endYearProtc = 0;
	private List<Integer> actions;

	public ServerModelRunner(long seed) {
		super(seed);
		loadStateManager();
	}

	
	// To get data without any policy intervention, run this function.
	public static void main(String[] args) {
		SimState state = new ServerModelRunner(System.currentTimeMillis());
		List<Integer> actionList = new ArrayList<Integer>();
		for (int i = 0; i < 20; i++) {
		    actionList.add(0);
		}
		((ServerModelRunner) state).setActions(actionList.toString());
		state.start();
		do
		if (!state.schedule.step(state)) break;
		while(state.schedule.getSteps() < 20);
		state.finish();
	}

	public void loadStateManager() {
		DataCenter dataCenter = new DataCenter(serviceNameFile, capitalNameFile, agentFilePath, baselineMapFilePath,
                annualCapitalFilePath, annualDemandFile);
		stateManager.add(dataCenter);
		stateManager.add(new SupplyInitializer());
		stateManager.add(new CapitalUpdater());
		stateManager.add(new DemandUpdater());
		stateManager.add(new InfluencedUtilityUpdater());
		stateManager.add(dataCenter.getManagerSet());
		stateManager.add(new SupplyUpdater());

		stateManager.add(new ServerInstitution());
		
//		stateManager.add(new MapUpdater());
		stateManager.add(new GridOfCharts());
	}

	public String run(Integer totalSteps) {
		start();
		do {
			if (!schedule.step(this)) break;
		}
		while(schedule.getSteps() < totalSteps);
		finish();
		return (getState(ServerInstitution.class).getAverageArrayList()).toString();
	}
	
	public void runExit() {
		//System.exit(0);
		//System.gc();
	}

// Getters
	public double getMeatSupply() {
		if (getState(DataCenter.class).getGlobalProductionMap().get("Meat") != null) {
			return getState(DataCenter.class).getGlobalProductionMap().get("Meat");
		}
		return 0;
	}

// Getter for CropsSupply
	public double getCropsSupply() {
		if (getState(DataCenter.class).getGlobalProductionMap().get("Crops") != null) {
			return getState(DataCenter.class).getGlobalProductionMap().get("Crops");
		}
		return 0;
	}

// Getter for DiversitySupply
	public double getDiversitySupply() {
		if (getState(DataCenter.class).getGlobalProductionMap().get("Diversity") != null) {
			return getState(DataCenter.class).getGlobalProductionMap().get("Diversity");
		}
		return 0;
	}

// Getter for TimberSupply
	public double getTimberSupply() {
		if (getState(DataCenter.class).getGlobalProductionMap().get("Timber") != null) {
			return getState(DataCenter.class).getGlobalProductionMap().get("Timber");
		}
		return 0;
	}

// Getter for CarbonSupply
	public double getCarbonSupply() {
		if (getState(DataCenter.class).getGlobalProductionMap().get("Carbon") != null) {
			return getState(DataCenter.class).getGlobalProductionMap().get("Carbon");
		}
		return 0;
	}

// Getter for UrbanSupply
	public double getUrbanSupply() {
		if (getState(DataCenter.class).getGlobalProductionMap().get("Urban") != null) {
			return getState(DataCenter.class).getGlobalProductionMap().get("Urban");
		}
		return 0;
	}

// Getter for RecreationSupply
	public double getRecreationSupply() {
		if (getState(DataCenter.class).getGlobalProductionMap().get("Recreation") != null) {
			return getState(DataCenter.class).getGlobalProductionMap().get("Recreation");
		}
		return 0;
	}

	public int getVEP() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("VEP");
		}
		return 0;
	}

	public int getInt_AF() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("Int_AF");
		}
		return 0;
	}

	public int getMix_P() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("Mix_P");
		}
		return 0;
	}

	public int getIP() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("IP");
		}
		return 0;
	}

	public int getMin_man() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("Min_man");
		}
		return 0;
	}

	public int getEP() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("EP");
		}
		return 0;
	}

	public int getExt_AF() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("Ext_AF");
		}
		return 0;
	}

	public int getUMF() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("UMF");
		}
		return 0;
	}

	public int getMultifun() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("Multifun");
		}
		return 0;
	}

	public int getMix_For() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("Mix_For");
		}
		return 0;
	}

	public int getUL() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("UL");
		}
		return 0;
	}

	public int getIA() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("IA");
		}
		return 0;
	}

	public int getMF() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("MF");
		}
		return 0;
	}

	public int getMix_Fa() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("Mix_Fa");
		}
		return 0;
	}

	public int getInt_Fa() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("Int_Fa");
		}
		return 0;
	}

	public int getUr() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("Ur");
		}
		return 0;
	}

	public int getP_Ur() {
		if (!(getState(DataCenter.class).AFTCounter.isEmpty())) {
			return getState(DataCenter.class).AFTCounter.get("P-Ur");
		}
		return 0;
	}

	public HashMap<String, Integer> getAFTCounter() {
		return getState(DataCenter.class).AFTCounter;
	}

	public void setThreshold(double thres) {
		threshold = thres;
	}

	public double getThreshold() {
		return threshold;
	}

	public Object domThreshold() {
		return new sim.util.Interval(0.0, 1.0);
	}

	public void setServiceNameFile(String filePathString) {
		serviceNameFile = filePathString;
	}

	public String getServiceNameFile() {
		return serviceNameFile;
	}

	public String getCapitalNameFile() {
		return capitalNameFile;
	}

	public void setCapitalNameFile(String capitalNameFile) {
		this.capitalNameFile = capitalNameFile;
	}

	public String getAgentFilePath() {
		return agentFilePath;
	}

	public void setAgentFilePath(String agentFilePath) {
		this.agentFilePath = agentFilePath;
	}

	public String getBaselineMapFilePath() {
		return baselineMapFilePath;
	}

	public void setBaselineMapFilePath(String baselineMapFilePath) {
		this.baselineMapFilePath = baselineMapFilePath;
	}

	public String getAnualCapitalFilePath() {
		return annualCapitalFilePath;
	}

	public void setAnualCapitalFilePath(String anualCapitalFilePath) {
		this.annualCapitalFilePath = anualCapitalFilePath;
	}

	public String getAnualDemandFile() {
		return annualDemandFile;
	}

	public void setAnualDemandFile(String anualDemandFile) {
		this.annualDemandFile = anualDemandFile;
	}

	public void setMeatGoal(double meatGoal) {

		this.meatGoal = meatGoal;

	}

	public double getMeatGoal() {

		return meatGoal;

	}

	public void setCropGoal(double cropGoal) {

		this.cropGoal = cropGoal;

	}

	public double getCropGoal() {

		return cropGoal;

	}

	public void setUnprotectionLimit(double limit) {
		this.limit = limit;
	}

	public double getUnprotectionLimit() {
		return limit;
	}

	public int getPolicyLag() {
		return policyLag;
	}

	public void setPolicyLag(int policyLag) {
		this.policyLag = policyLag;
	}

	public double getDivGoal() {
		return divGoal;
	}

	public void setDivGoal(double divGoal) {
		this.divGoal = divGoal;
	}

	public int getMeatLag() {

		return meatLag;
	}

	public void setMeatLag(int meatLag) {
		this.meatLag = meatLag;
	}

	public int getCropLag() {
		// TODO Auto-generated method stub
		return cropLag;
	}

	public void setCropLag(int cropLag) {
		this.cropLag = cropLag;
	}

	public double getPaInertia() {
		return paInertia;
	}

	public void setPaInertia(double paInertia) {
		this.paInertia = paInertia;
	}

	public int getEndYearProtc() {
		return endYearProtc;
	}

	public void setEndYearProtc(int endYearProtc) {
		this.endYearProtc = endYearProtc;
	}
	
	public void setActions(String actions) {
		this.actions = stringToArrayList(actions);
	}
	
	public List<Integer> getActions() {
		return actions;
	}
	
	public ArrayList<Integer> stringToArrayList(String str) {
        // Remove the brackets and trim to avoid leading and trailing spaces
        str = str.substring(1, str.length() - 1).trim();

        // Split the string into an array of strings
        String[] stringNumbers = str.split("\\s*,\\s*");

        // Create an ArrayList
        ArrayList<Integer> numbers = new ArrayList<>();

        // Parse and add each number to the ArrayList
        for (String number : stringNumbers) {
            numbers.add(Integer.parseInt(number));
        }

        return numbers;
    }

}
