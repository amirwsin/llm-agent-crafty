from py4j.java_gateway import JavaGateway, CallbackServerParameters
from templates import *
from agent_entries import AgentEntrySingle, AgentEntryRolePlaying
from get_response import get_response

# Some initial settings
policy_goal_for_single_agent = "Maintain avg_err at 0"
policy_goal_for_role_playing = "1"
hist_actions = "0,"
avg_err = "None"
convers = "None"
exp = "None"
# Choose a prompt template from templates. For single agent: template_exp, template_no_exp_reason first,
# template_no_exp_reason_after; For role-playing agent: template_role_playing
template = template_no_exp_reason_first
meat_demand = "None"
meat_supply = "1"


def main():
    """
     Choose to instantiate either AgentEntrySingle or AgentEntryRolePlaying for different experiments.
    """
    agent_entry = AgentEntrySingle(template, get_response, policy_goal_for_single_agent, hist_actions, avg_err)
    # agent_entry = AgentEntryRolePlaying(template, get_response, hist_actions, meat_demand, meat_supply, policy_goal_for_role_playing)
    gateway = JavaGateway(
        callback_server_parameters=CallbackServerParameters(),
        python_server_entry_point=agent_entry)
    print("Server started...")


if __name__ == '__main__':
    main()
