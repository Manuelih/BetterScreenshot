# 1.0.1

- A "Screenshot saved" notification with the file name now appears when you press F2 inside a GUI (inventory, chests, menus...), where the chat message is hidden
- The notification follows the `showToasts` option

# 1.0.0

First release, for Minecraft 1.21.8 - 1.21.11.

- The vanilla "Saved screenshot as..." message now has `[Upload]` and `[Copy]` buttons
- `[Upload]` uploads the screenshot and copies the link to the clipboard
- `[Copy]` copies the image itself to the clipboard
- Hosts: catbox.moe (default, no key) and Imgur (own Client-ID)
- Optional auto upload mode (`autoUpload: true`)
- Toast notifications, `[Copy link]` and `[Retry]` buttons
- Commands: `/betterscreenshot upload|copy <file>` and `/betterscreenshot reload`
- English and Italian translations
