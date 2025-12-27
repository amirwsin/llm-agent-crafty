from rhodium import *
from py4j.java_gateway import JavaGateway
import json

from Client import crafty

# Start the gateway between Python and Java
gateway = JavaGateway()

# Pass the crafty function to instantiate the optimization model
model = Model(crafty)
# Specifying the parameter names
model.parameters = [Parameter("actions")]
# Specifying object function name and choose the type of problem.
model.responses = [Response("result", Response.MINIMIZE)]
# There is no other constraints here, so an empty list is given.
model.constraints = []
# Specify the optimization variable name, range, and number of elements, i.e., length of the list.
# Indeed, we only need 21 elements, but 23 is used here. This redundancy does not harm.
model.levers = [IntegerLever("actions", -5, 5, length=23)]


if __name__ == "__main__":
    # Use a Process Pool evaluator if your computer has a large memory.
    # with ProcessPoolEvaluator(16) as evaluator:
    #     RhodiumConfig.default_evaluator = evaluator
    output = optimize(model, "NSGAII", 1000)
    # Define your own file path to save the results in a JSON file.
    with open(r'optimal_actions.json', 'w') as f:
        dictionaries = list(output)
        f.write(json.dumps(dictionaries))

