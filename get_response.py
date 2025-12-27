from openai import OpenAI
from connection_handler import retry_with_exponential_backoff
from key import key,base_url as custom_base_url

LLM_PROVIDER = "gemini"  # "openai", "gemini", or "custom"
MODEL_NAME = "gemini-2.5-flash" if LLM_PROVIDER == "gemini" else "gpt-4"

def get_client():
    if LLM_PROVIDER == "gemini":
        return OpenAI(
            api_key=key,
            base_url="https://generativelanguage.googleapis.com/v1beta/openai/"
        )
    elif LLM_PROVIDER == "openai":
        return OpenAI(api_key=key)
    else:
        # Support for other providers (Anthropic, Deepseek, local Ollama, etc.)
        return OpenAI(
            api_key=key,
            base_url=custom_base_url
        )

client = get_client()

@retry_with_exponential_backoff
def get_response(prompt):
    response = client.chat.completions.create(
        model=MODEL_NAME,
        temperature=0,
        messages=[{"role": "user", "content": prompt}]
    )
    return response.choices[0].message.content