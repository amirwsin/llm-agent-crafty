from openai import OpenAI

from connection_handler import retry_with_exponential_backoff
from key import key

openai_api_key = key
# Initialize the LLM
client = OpenAI(api_key=key)


@retry_with_exponential_backoff
def get_response(prompt):
    response = client.chat.completions.create(
        model="gpt-4-1106-preview",
        # Temperature is a parameter that can adjust the randomness of the output.
        # 0 or very small non-negative values can make model more deterministic.
        # However, ironically since OpenAI is not open and is evolving rapidly,
        # setting temperature to zero may not always ensure the reproducibility of the results.
        temperature=0,
        messages=[
            {"role": "user", "content": prompt},
        ]
    )
    return response.choices[0].message.content
