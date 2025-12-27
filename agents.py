class AgentExp:

    def __init__(self, template, llm, policy_goal, hist_actions, avg_err):
        self.template = template
        self.prompt = None
        self.llm = llm
        self.proposal = None
        self.output = None
        self.conversation_list = []
        self.policy_goal = policy_goal
        self.hist_actions = hist_actions
        self.avg_err = avg_err
        self.convers = None
        self.exp = ""
        self.update_prompt()  # update prompt after initialization.

    # collect historical actions and average errors
    def collect_data(self, hist_actions, avg_err):
        self.hist_actions = hist_actions
        self.avg_err = avg_err
        self.update_prompt()
        print(self.prompt)

    # get the LLM response
    def propose(self):
        self.proposal = self.llm(self.prompt)
        print(self.proposal)
        return self.proposal

    # saving the conversations/responses
    def add_to_conversation(self, identity, content):
        self.conversation_list.append(f"{identity}: {content}")
        self.convers = " ".join(self.conversation_list)

        self.update_prompt()

    # update experience
    def update_exp(self):
        # if the length of the conversation string exceeds a threshold, summarize all the conversations
        if len(self.convers) / 4 > 500:
            self.exp = self.llm(
                "Based on the content below, summarize the recent interaction with policymaker into useful bullet "
                "points to update your experience: " + self.prompt)
            # clear conversation list and string for future interactions.
            self.conversation_list = []
            self.convers = "Cleared for saving memory"

            self.update_prompt()
            print(f"Experience:\n {self.exp}")

    # a function that update the information held in the prompt template
    def update_prompt(self):
        self.prompt = self.template.format(
            policy_goal=self.policy_goal,
            hist_actions=self.hist_actions,
            avg_err=self.avg_err,
            convers=self.convers,
            exp=self.exp,
        )


# No experience agent. We can simply let the update_exp do nothing.
class AgentNoExp:

    def __init__(self, template, llm, policy_goal, hist_actions, avg_err):
        self.template = template
        self.prompt = None
        self.llm = llm
        self.proposal = None
        self.output = None
        self.conversation_list = []
        self.policy_goal = policy_goal
        self.hist_actions = hist_actions
        self.avg_err = avg_err
        self.convers = None
        self.exp = ""
        self.update_prompt()

    def collect_data(self, hist_actions, avg_err):
        self.hist_actions = hist_actions
        self.avg_err = avg_err

        self.update_prompt()
        print(self.prompt)

    def propose(self):
        self.proposal = self.llm(self.prompt)
        print(self.proposal)
        return self.proposal

    def add_to_conversation(self, identity, content):
        self.conversation_list.append(f"{identity}: {content}")

        if len(self.conversation_list) > 3:
            self.conversation_list.pop(0)

        self.convers = " ".join(self.conversation_list)
        self.update_prompt()

    def update_exp(self):
        pass

    def update_prompt(self):
        self.prompt = self.template.format(
            policy_goal=self.policy_goal,
            hist_actions=self.hist_actions,
            avg_err=self.avg_err,
            convers=self.convers,
            exp=self.exp,
        )


# The quasi-multi-agent
class AgentRolePlaying:

    def __init__(self, template, llm, policy_actions, meat_demand, meat_supply, policy_goal):
        self.template = template
        self.llm = llm
        self.policy_actions = policy_actions
        self.meat_demand = meat_demand
        self.meat_supply = meat_supply
        self.policy_goal = policy_goal

        self.conversation_list = []
        self.prompt = None
        self.response = None
        self.output = None

        self.update_prompt()

    def collect_data(self, policy_actions, meat_demand, meat_supply):
        self.policy_actions = policy_actions
        self.meat_demand = meat_demand
        self.meat_supply = meat_supply
        self.update_prompt()

    def generate_conversation(self):
        self.response = self.llm(self.prompt)
        print(self.response)
        return self.response

    def update_prompt(self):
        self.prompt = self.template.format(
            policy_actions=self.policy_actions,
            meat_demand=self.meat_demand,
            meat_supply=self.meat_supply,
            policy_goal=self.policy_goal
        )
