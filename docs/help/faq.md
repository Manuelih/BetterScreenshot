# FAQ & troubleshooting

<details>

<summary>I don't see the buttons</summary>

Make sure you are using **F2** (the vanilla screenshot key) and that both Better Screenshot and Fabric API are installed. To click the buttons, open the chat with **T**.

</details>

<details>

<summary>The upload failed</summary>

The error is shown in chat. Common causes:

* no internet connection, or a firewall / antivirus blocking Minecraft
* `imgur` selected without a valid `imgurClientId`
* Imgur not available in your country: switch to `catbox`

Click **\[Retry]** to try again.

</details>

<details>

<summary>[Copy] doesn't work on Linux</summary>

Install `wl-clipboard` (Wayland) or `xclip` (X11). See [Installation](../getting-started/installation.md#linux-only).

</details>

<details>

<summary>Can I delete an uploaded image?</summary>

catbox.moe anonymous uploads can't be deleted by the uploader. Don't upload screenshots with personal information (coordinates of a private base, chat messages, usernames...) if you don't want them public.

</details>

<details>

<summary>Can I upload automatically like before?</summary>

Yes: set `"autoUpload": true` in the [config](../configuration/config-file.md).

</details>

<details>

<summary>Does it work with Iris / Sodium / other mods?</summary>

Yes. Better Screenshot only changes the chat message sent after a screenshot is saved.

</details>

Still stuck? Ask on [Discord](https://discord.gg/KBAjPbwMx) or open an [issue](https://github.com/Manuelih/BetterScreenshot/issues).
