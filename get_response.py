from google import genai
from openai import OpenAI
from connection_handler import retry_with_exponential_backoff
from key import key, base_url as custom_base_url


LLM_PROVIDER = "gemini"  # "openai", "gemini", or "custom"


GEMINI_MODELS_MAP = {
    # ===== Defaults =====
    "default": "gemini-2.5-flash",
    "fast": "gemini-2.5-flash",
    "lite": "gemini-2.5-flash-lite",

    # ===== Stronger reasoning =====
    "pro": "gemini-2.5-pro",

    # ===== Legacy / compatibility =====
    "legacy": "gemini-1.0-pro",

    # ===== Embeddings =====
    "embedding": "text-embedding-004",
}


def get_model(task="default"):
    if LLM_PROVIDER == "gemini":
        return GEMINI_MODELS_MAP.get(task, GEMINI_MODELS_MAP["default"])
    return "gpt-4"


def get_client():
    if LLM_PROVIDER == "gemini":
        return genai.Client(api_key=key) 
    elif LLM_PROVIDER == "openai":
        return OpenAI(api_key=key)
    else:
        return OpenAI(api_key=key, base_url=custom_base_url)


client = get_client()


@retry_with_exponential_backoff
def get_response(prompt: str) -> str:
    try:
        if LLM_PROVIDER == "gemini":
            response = client.models.generate_content(
                model=get_model(),
                contents=prompt,
                config={"temperature": 0.0},
            )

            if response and response.text:
                return response.text

            return "[LLM_EMPTY_RESPONSE]"
        
        else:
            response = client.chat.completions.create(
                model=get_model(),
                temperature=0,
                messages=[{"role": "user", "content": prompt}],
            )
            return response.choices[0].message.content

    except Exception as e:
        # CRITICAL for ABM stability
        return f"[LLM_ERROR]: {str(e)}"
