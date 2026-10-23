# Config file

The config is created on first launch at:

```
.minecraft/config/betterscreenshot.json
```

```json
{
  "host": "catbox",
  "imgurClientId": "",
  "autoUpload": false,
  "copyToClipboard": true,
  "showToasts": true
}
```

## Options

| Option | Default | Description |
| --- | --- | --- |
| `host` | `"catbox"` | Where screenshots are uploaded: `"catbox"` or `"imgur"`. See [Upload hosts](upload-hosts.md). |
| `imgurClientId` | `""` | Your Imgur Client-ID, only needed when `host` is `"imgur"`. |
| `autoUpload` | `false` | `true` uploads every screenshot right after F2. The **\[Upload]** button is then hidden. |
| `copyToClipboard` | `true` | Copies the link to the clipboard after an upload. |
| `showToasts` | `true` | Shows the notifications in the top right corner. |

## Applying changes

Save the file, then run in game:

```
/betterscreenshot reload
```

No restart needed.

{% hint style="warning" %}
If the file contains invalid JSON, the mod logs an error and uses the default values.
{% endhint %}
