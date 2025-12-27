import ast
import math
from py4j.java_gateway import JavaGateway

# Start the gateway between Python and Java
gateway = JavaGateway()


# Define a function named crafty to wrap the java model.
def crafty(actions):
    # get model runner from the java side.
    runner = gateway.entry_point.getRunner()
    # set actions. Here the actions are converted into strings to avoid potential type conflicts.
    runner.setActions(str(actions))
    # The run function will run the crafty emulator 110 iterations, and return a list of errors, but of string type.
    error_string = runner.run(110)
    print(error_string)
    # convert the string into list.
    error_list = ast.literal_eval(error_string)
    print(error_list)
    # Using the least square method to estimate the aggregated error.
    result = math.sqrt(sum(x ** 2 for x in error_list))
    return result


if __name__ == '__main__':
    # this list of optimal actions comes from the optimization.
    optimal_actions = [1, 5, 5, 3, 4, 1, 1, 4, 4, -1, 5, 1, -1, 1, 0, 0, -5, -1, 0, 5, 1, -5, 0]
    crafty(optimal_actions)
