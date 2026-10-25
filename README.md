<p align="center">
  <img src=".github/assets/banner.png" width="700" alt="Better Screenshot">
</p>

<p align="center">
  Press F2, then choose: <b>[Upload]</b> or <b>[Copy]</b>.<br>
  A client-side Fabric mod for Minecraft 1.21.8 - 1.21.11.
</p>

<p align="center">
  <a href="https://modrinth.com/mod/betterscreenshot"><img src="https://img.shields.io/modrinth/dt/betterscreenshot?logo=modrinth&label=Modrinth&color=00AF5C" alt="Modrinth"></a>
  <a href="https://discord.gg/KBAjPbwMx"><img src="https://img.shields.io/badge/Discord-join-5865F2?logo=discord&logoColor=white" alt="Discord"></a>
  <img src="https://img.shields.io/badge/license-MIT-blue" alt="MIT">
</p>

---

Better Screenshot upgrades the vanilla screenshot message. The screenshot is saved exactly like before, but the chat message gets two buttons:

- **[Upload]** uploads the screenshot online and copies the link to your clipboard
- **[Copy]** copies the image itself to your clipboard, ready to paste into Discord

![Screenshot message](docs/.gitbook/assets/gallery-1-buttons.png)

![Uploaded](docs/.gitbook/assets/gallery-2-uploaded.png)

## Download

[Modrinth](https://modrinth.com/mod/betterscreenshot) (requires [Fabric API](https://modrinth.com/mod/fabric-api)).

## Documentation

See the [docs](docs/README.md): installation, configuration, commands, FAQ.

<p>
  <img src=".github/assets/faq.png" width="260" alt="FAQ">
</p>

**Does it upload my screenshots automatically?**<br>
No. Nothing is uploaded unless you click **[Upload]**, or you enable `autoUpload` in the config.

**Where are screenshots uploaded?**<br>
To [catbox.moe](https://catbox.moe) by default (no account needed), or to Imgur with your own Client-ID. Uploaded images are public to anyone who has the link.

**Does it work on servers?**<br>
Yes. It's client-side only, so it works on any server, including vanilla ones.

**[Copy] doesn't work on Linux.**<br>
Install `wl-clipboard` (Wayland) or `xclip` (X11).

More answers in the [FAQ page](docs/help/faq.md).

<p>
  <img src=".github/assets/java.png" width="280" alt="Java">
</p>

**Requirements:** Java 21, Fabric Loader 0.17.2+ and Fabric API.

**Building:** requires JDK 21.

```
./gradlew build
```

The jar is created in `build/libs/`.

## License

[MIT](LICENSE)
