package llmExp;

/*
 * This class serves as an entry point for initiating communication between
 * Java and Python using the Py4J library. It is specifically designed for testing
 * non-LLM-based control scenarios, such as non-intervention, optimal actions,
 * or any given policy actions, from the Python end.
 *
 * Key Responsibilities:
 * 1. Initialize a ServerModelRunner instance for managing simulation states.
 * 2. Set up a Py4J GatewayServer to facilitate communication with Python.
 * 3. Start the gateway and make the ServerEntry instance accessible to Python.
 */

import py4j.GatewayServer;

public class ServerEntry {
	
	public ServerModelRunner getRunner() {
		ServerModelRunner state = new ServerModelRunner(System.currentTimeMillis());
		return state;
	}

	public static void main(String[] args) {
		GatewayServer gatewayServer = new GatewayServer(new ServerEntry());
		gatewayServer.start();
		System.out.println("Gateway Server Started");
	}
}
