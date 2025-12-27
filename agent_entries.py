import os
import re
from agents import AgentExp, AgentRolePlaying
from get_response import get_response
from window import get_user_response

file_path = r'test.txt'  # Define the path for the file to store data
interaction_on = False  # Flag to indicate interaction mode

if os.path.exists(file_path):
    os.remove(file_path)
else:
    pass


# Class for handling single-agent entries
class AgentEntrySingle:

    def __init__(self, template, llm, policy_goal, hist_actions, avg_err):
        # Initialize the agent with the provided parameters
        # Uncomment the desired agent type (AgentExp or AgentNoExp)
        self.agent = AgentExp(template, get_response, policy_goal, hist_actions, avg_err)
        # self.agent = AgentNoExp(template, get_response, policy_goal, hist_actions, avg_err)

    def agentRun(self, hist_actions, avg_err):
        # Open a file in write mode ('w')
        with open(file_path, 'a') as file:
            # Collect data using the agent
            self.agent.collect_data(hist_actions, avg_err)
            response = {'choice': None, 'comment': None}

            # Loop until the user's choice is "Yes"
            while response['choice'] != "Yes":
                proposal = self.agent.propose()  # Generate a proposal from the agent
                if interaction_on:
                    # Get user response in interactive mode
                    response = get_user_response()
                else:
                    # Simulate a default response in non-interactive mode.
                    response['choice'] = "Yes"
                    response['comment'] = "Ok"

                # Add the agent's and user's comments to the conversation
                self.agent.add_to_conversation("Assistant: ", proposal)
                self.agent.add_to_conversation("Policymaker: ", response["comment"])

            print("===============================")
            self.agent.update_exp()  # Update the agent's experience

            file.write(self.agent.prompt)

            proposed_action_string = self.agent.proposal
            # Using regular expressions to extract a clean output.
            match = re.search(r"#(.*?)#", proposed_action_string)
            
            if match:
                extracted_text = match.group(1)
                try:
                    # int() can handle signs like "+3" or "-2"
                    numerized_action = int(extracted_text)
                except ValueError:
                    # Fallback: if text is inside hashtags but not a pure integer, try to find the digit
                    digit_match = re.search(r"[-+]?\d+", extracted_text)
                    if digit_match:
                        numerized_action = int(digit_match.group())
                    else:
                        print(f"Warning: Could not parse action from '{extracted_text}'. Defaulting to 0.")
                        numerized_action = 0
            else:
                # Mitigation for formatting inconsistencies [cite: 660-661]
                print(f"Warning: No hashtag-enclosed action found in LLM response. Defaulting to 0.")
                numerized_action = 0

            print(f"++++++++++++++>>>>>>{numerized_action}")
        return numerized_action


# Template for formatting role-playing data
data_template = """
- **Historical Policy Actions** (updated every five years): {policy_actions} 
- **Meat Demand ** (averaged every five years): {meat_demand} 
- **Meat Supply** (averaged every five years): {meat_supply} 
"""


# Class for handling role-playing agent entries
class AgentEntryRolePlaying:

    def __init__(self, template, llm, policy_actions, meat_demand, meat_supply, policy_goal):
        # Initialize the agent with the provided parameters
        self.agent = AgentRolePlaying(template, llm, policy_actions, meat_demand, meat_supply, policy_goal)

    def agentRun(self, policy_actions, meat_demand, meat_supply):
        with open(file_path, 'a') as file:
            # Collect data from the agent
            self.agent.collect_data(policy_actions, meat_demand, meat_supply)
            # Generate a conversation
            conversation = self.agent.generate_conversation()

            print("===============================")
            formated_data = data_template.format(
                policy_actions=self.agent.policy_actions,
                meat_demand=self.agent.meat_demand,
                meat_supply=self.agent.meat_supply
            )

            file.write(formated_data + "\n")
            file.write(conversation + "\n")

            proposed_action_string = self.agent.response
            # Using regular expressions to extract a clean output.
            match = re.search(r"#\\?\+?(-?\d+)\\?#", proposed_action_string)
            
            if match:
                extracted_text = match.group(1)
                numerized_action = int(extracted_text)
            else:
                # Log the formatting error as identified in the PDF guide [cite: 660]
                print(f"Warning: Role-playing agent failed formatting. Defaulting to 0.")
                numerized_action = 0

            print(f"++++++++++++++>>>>>>{numerized_action}")
        return numerized_action
