# Upload hosts

## catbox.moe (default)

```json
"host": "catbox"
```

* No account and no API key
* Direct link to the image (`https://files.catbox.moe/xxxxxx.png`)
* Files are kept permanently

Nothing to set up: it works out of the box.

## Imgur

```json
"host": "imgur",
"imgurClientId": "your-client-id"
```

Imgur needs a free **Client-ID**:

1. Log in to Imgur and open [api.imgur.com/oauth2/addclient](https://api.imgur.com/oauth2/addclient).
2. **Application name:** anything, for example `Better Screenshot`.
3. **Authorization type:** *Anonymous usage without user authorization*.
4. **Authorization callback URL:** `https://localhost` (it's not used).
5. Fill in your email and submit.
6. Copy the **Client ID** (not the secret) into `imgurClientId`.
7. Run `/betterscreenshot reload` in game.

{% hint style="warning" %}
Imgur is not available in some countries (for example the United Kingdom). If uploads fail there, use catbox.
{% endhint %}
