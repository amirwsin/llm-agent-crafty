@echo off
TITLE LLM-AGENT-POLICY Runner

echo [1/3] Starting Python LLM Agent Gateway...
:: Start Python in a separate window to keep logs visible
start "Python LLM Agent" cmd /k "venv\Scripts\activate && python main.py"

echo [2/3] Waiting for Gateway Server to initialize...
:: Wait 5 seconds to ensure the Py4J server is ready
timeout /t 5 /nobreak > nul

echo [3/3] Launching Java Simulation (CRAFTY)...
:: Execute the Maven command to run the UI version of the simulation
mvn exec:java "-Dexec.mainClass=llmExp.LLMModelRunnerWithUI"

pause