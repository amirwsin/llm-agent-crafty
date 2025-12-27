#!/bin/bash

echo "[1/3] Starting Python LLM Agent Gateway..."
# Activate venv and run main.py in the background
source venv/bin/activate
python main.py &
PYTHON_PID=$!

echo "[2/3] Waiting for Gateway Server to initialize..."
# Wait 5 seconds for the server to be ready
sleep 5

echo "[3/3] Launching Java Simulation (CRAFTY)..."
# Run the Java simulation
mvn exec:java "-Dexec.mainClass=llmExp.LLMModelRunnerWithUI"

# When Java exits, kill the background Python process
kill $PYTHON_PID
echo "Cleanup complete."