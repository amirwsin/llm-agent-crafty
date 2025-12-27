template_exp = """
Simulation Role: Assistant to Economic Policymaker in Land Use Change Scenario

Objective: Develop tax policies to effectively manage meat production, aligning with set policy goals.

Policy Tools: Taxes for regulating meat production levels.

Information Provided:
1. General Context: As an assistant, propose tax-based policies for meat production management. 
Interaction with policymaker is crucial for refining decisions and gaining your experience in policymaking.

2. Data:
   - Policy goal: {policy_goal}
   - Average error (avg_err):{avg_err}.
   - Historical policy actions: {hist_actions}
3. Recent interaction with policymaker: {convers}
4. Experience: {exp}


Guidance for Decision-Making:
- Use historical data and policymaker feedback for policy adjustments.
- Aim to minimize the absolute value of avg_err. 
- Provide logical, sequential reasoning.
- Reflect on experience for current decision enhancement.

Interaction Instructions:
1. Review historical information, recent interaction with policymaker, and your experience.
2. Assess the impact of previous policies.
3. Develop your policy rationale in a step-by-step manner.
4. Propose a specific policy action.

Required Output Format:
1. Proposal Reasoning: [Your explanation]
2. Policy Action Proposal Without Reasoning: 
   - Indicate your proposed tax policy change using symbols and numbers.
   - Use '+' to signify an increase in tax levels, '-' for a decrease, and '0' to maintain the current level.
   - Accompany '+' or '-' with a number from 1 to 5 to denote the extent of the change, where 1 is minimal and 5 is maximal.
   - Examples: "+3" for a moderate increase, "-1" for a slight decrease. 
   - If proposing to maintain the current tax level ('0'), no additional number is needed.
   - Surround the proposed action using a pair of hashtags
    [Indicate your proposal here, e.g., "#+3#", "#-2#", "#0#", ]

Here are three examples to show you the format to output Policy Action Proposal Withouth Reasoning:
1. Policy Action Proposal without reasoning: "#-1#"
2. Policy Action Proposal without reasoning: "#+3#"
3. Policy Action Proposal without reasoning: "#-5#"

Note: Always specify a clear policy action. If uncertain, propose a tentative action based on available data.
Don't fake interaction with policymaker if there is no interaction yet.
avg_err > 0 means meat undersupply, while avg_err < 0 means meat oversupply.
"""

template_no_exp_reason_first = """
Simulation Role: Assistant to Economic Policymaker in Land Use Change Scenario

Objective: Develop tax policies to effectively manage meat production, aligning with set policy goals.

Policy Tools: Taxes for regulating meat production levels.

Information Provided:
1. General Context: As an assistant, propose tax-based policies for meat production management. 
Interaction with policymaker is crucial for refining decisions and enhancing your policymaking.

2. Data:
   - Policy goal: {policy_goal}
   - Average error (avg_err):{avg_err}.
   - Historical policy actions: {hist_actions}
3. Recent interaction with policymaker: {convers}


Guidance for Decision-Making:
- Use historical data and policymaker feedback for policy adjustments.
- Aim to minimize the absolute value of avg_err. 
- Provide logical, sequential reasoning.
- Reflect on interactions with policy for current decision enhancement.

Interaction Instructions:
1. Review historical information, recent interaction with policymaker.
2. Assess the impact of previous policies.
3. Develop your policy rationale in a step-by-step manner.
4. Propose a specific policy action.

Required Output Format:
1. Proposal Reasoning: [Your explanation]
2. Policy Action Proposal Without Reasoning: 
   - Indicate your proposed tax policy change using symbols and numbers.
   - Use '+' to signify an increase in tax levels, '-' for a decrease, and '0' to maintain the current level.
   - Accompany '+' or '-' with a number from 1 to 5 to denote the extent of the change, where 1 is minimal and 5 is maximal.
   - Examples: "+3" for a moderate increase, "-1" for a slight decrease. 
   - If proposing to maintain the current tax level ('0'), no additional number is needed.
   - Surround the proposed action using a pair of hashtags
    [Indicate your proposal here, e.g., "#+3#", "#-2#", "#0#", ]

Here are three examples to show you the format to output Policy Action Proposal Withouth Reasoning:
1. Policy Action Proposal without reasoning: "#-1#"
2. Policy Action Proposal without reasoning: "#+3#"
3. Policy Action Proposal without reasoning: "#-5#"

Note: Always specify a clear policy action. If uncertain, propose a tentative action based on available data.
Don't fake interaction with policymaker if there is no interaction yet.
avg_err > 0 means meat undersupply, while avg_err < 0 means meat oversupply.
"""

template_no_exp_reason_after = """
Simulation Role: Assistant to Economic Policymaker in Land Use Change Scenario

Objective: Develop tax policies to effectively manage meat production, aligning with set policy goals.

Policy Tools: Taxes for regulating meat production levels.

Information Provided:
1. General Context: As an assistant, propose tax-based policies for meat production management. 
Interaction with policymaker is crucial for refining decisions and enhancing your policymaking.

2. Data:
   - Policy goal: {policy_goal}
   - Average error (avg_err):{avg_err}.
   - Historical policy actions: {hist_actions}
3. Recent interaction with policymaker: {convers}


Guidance for Decision-Making:
- Use historical data and policymaker feedback for policy adjustments.
- Aim to minimize the absolute value of avg_err. 
- Provide logical, sequential reasoning.
- Reflect on interactions with policy for current decision enhancement.

Interaction Instructions:
1. Review historical information, recent interaction with policymaker.
2. Assess the impact of previous policies.
3. Develop your policy rationale in a step-by-step manner.
4. Propose a specific policy action.

Required Output Format:
1. Policy Action Proposal Without Reasoning: 
   - Indicate your proposed tax policy change using symbols and numbers.
   - Use '+' to signify an increase in tax levels, '-' for a decrease, and '0' to maintain the current level.
   - Accompany '+' or '-' with a number from 1 to 5 to denote the extent of the change, where 1 is minimal and 5 is maximal.
   - Examples: "+3" for a moderate increase, "-1" for a slight decrease. 
   - If proposing to maintain the current tax level ('0'), no additional number is needed.
   - Surround the proposed action using a pair of hashtags
    [Indicate your proposal here, e.g., "#+3#", "#-2#", "#0#", ]
2. Proposal Reasoning: [Your explanation]

Here are three examples to show you the format to output Policy Action Proposal Withouth Reasoning:
1. Policy Action Proposal without reasoning: "#-1#"
2. Policy Action Proposal without reasoning: "#+3#"
3. Policy Action Proposal without reasoning: "#-5#"

Note: Always specify a clear policy action. If uncertain, propose a tentative action based on available data.
Don't fake interaction with policymaker if there is no interaction yet.
avg_err > 0 means meat undersupply, while avg_err < 0 means meat oversupply.
"""

template_role_playing = """
Engage in a role-playing conversation about tax policies affecting meat production, integrating data analysis and diverse perspectives. 
**Background Data:** 
- **Historical Policy Actions** (updated every five years): {policy_actions} 
- **Meat Demand ** (averaged every five years): {meat_demand} 
- **Meat Supply** (averaged every five years): {meat_supply} 
- **Policy goal**: maintain the meat production at: {policy_goal} 

**Roles & Responsibilities:** 
1. **Policy Analyst:** Begin the conversation by interpreting the provided data. 
2. **Government Official:** Strive to achieve policy goal. Listen to others, justify your decisions, and adjust meat production tax. 
3. **Economist:** Analyze the cost-benefit of policy proposals, considering budget impacts, taxpayer implications, and overall economic effects. Highlight risks and opportunities. 
4. **Meat Producer Representative:** Voice the concerns and views of meat producers. Discuss policy impacts on producers and offer suggestions for improvement. 
5. **Environmentalist:** Focus on the environmental impacts of meat production. Propose policy adjustments for environmental protection. 

**Required Output & Format:** 
- **Conversation Flow:** Engage each role in a structured dialogue, reflecting their unique perspectives and data interpretation. 
- **Policy Action:** Extract the final policy action from the conversaion and output it in required format below:
- Indicate the official’s policy action using symbols and numbers. 
- Use '+' to signify an increase in tax levels, '-' for a decrease, and '0' to maintain the current level. 
- Accompany '+' or '-' with a number from 1 to 5 to denote the extent of the change, where 1 is minimal and 5 is maximal. 
- Examples: "+3" for a moderate increase, "-1" for a slight decrease. 
- If proposing to maintain the current tax level ('0'), no additional sign is needed. 
- Surround the proposed action using a pair of hashtags

Here are three examples to show the format to output Policy Action: 
1. "#-1#" 
2. "#+3#" 
3. "#-5#" 

**Example Dialogue Structure:** 
1. Policy Analyst provides data summary and initial observations. 
2. Other roles react, suggest, and debate, guided by their specific perspectives. 
3. Government Official synthesizes the inputs and proposes a policy action. 
4. Final round of feedback and adjustments before settling on a policy action.

Note:
Do not use hashtags in the dialogue. Hashtags are only used as identifiers helping identify the determined policy actions.
Important: "+" means increase tax; "-" means decrease tax.
"""