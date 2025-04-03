import time
import requests
from diki_translate import Diki

diki = Diki("english")

# my api url
api_url = "https://api.fluentweb.pl/api/admin/words"

# Headers to
headers = {
    "Content-Type": "application/json",
    "Authorization": "Bearer <Token>",
    "Origin": "https://fluentweb.pl"
}


# Function translating Polish words to English, and send it to my api
def translate_and_send(word):
    word = word.strip()
    target_word = diki.translation(word)
    first_translation = next(target_word, None)

    if first_translation:
        trimmed_text = first_translation.split(" ")[0]

        # Change wordCerfLevel for current cerf level
        payload = {
            "sourceWord": word.capitalize(),
            "translation": {"PL": trimmed_text.capitalize()},
            "wordCerfLevel": "A1"
        }

        try:
            response = requests.post(api_url, json=payload, headers=headers)
            if response.status_code == 201 or response.status_code == 443:
                print(f"Added: {word} -> {trimmed_text}")
            else:
                print(f"Error for {word}: {response.status_code}")
        except requests.exceptions.RequestException as e:
            print(f"Connect error for {word}: {e}")

    # wait so as not to send too many query
    time.sleep(1)


start_time = time.time()

with open("<Location>\\english-words-a1.txt", "r", encoding="utf-8") as f:
    for word in f:
        translate_and_send(word)

end_time = time.time()
print(f"Script duration : {end_time - start_time:.4f} seconds")
