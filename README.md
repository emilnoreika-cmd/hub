# Custom Donut HUD (Fabric, Minecraft Java 1.21.11)

Client-side, cosmetic scoreboard HUD with fully editable *fake* stats for staged footage:
username, money, shards, kills, deaths. Press **M** (rebindable under Options > Controls) to edit.
It is a local overlay only: it sends no packets/commands, touches no server data, and works offline in single-player.

## Requirements
- Java 21 (JDK) - https://adoptium.net
- Minecraft 1.21.11 + Fabric Loader (>= 0.18) via the official Fabric installer
- Fabric API for 1.21.11 in your `mods` folder (https://modrinth.com/mod/fabric-api)

## Build the JAR
1. Get a Gradle wrapper. Easiest: download the official template from https://fabricmc.net/develop/template
   (pick 1.21.11, Yarn) and copy its `gradlew`, `gradlew.bat` and `gradle/` folder into this project.
   Or install Gradle 9.x and run `gradle wrapper` once here.
2. Run `./gradlew build` (Windows: `gradlew.bat build`).
3. The mod is `build/libs/custom-donut-hud-1.0.0.jar` (not the `-sources` one).
4. Put it, plus Fabric API, in `.minecraft/mods`, launch the Fabric 1.21.11 profile.

To test from source instead: `./gradlew runClient`.

## Using it
- M opens the menu. Edit values, watch the live preview, then **Apply** (updates the HUD), **Save** (also writes
  `config/custom_donut_hud.json`), **Reset** (defaults), **Close** (discards unapplied edits).
- Money accepts `127000000`, `127M`, `1.5B`, `500K`, `$2.5T`. "Short" toggles compact display on the HUD.
- Username blank = your real account name. This never changes your actual account.
- F1 hides the HUD like the rest of the vanilla HUD.
- Icons are 16x16 PNGs in `src/main/resources/assets/customdonuthud/textures/gui/` - replace them to tweak the art.

## Troubleshooting
- **Dependency/plugin not found**: versions in `gradle.properties` may have moved. Copy the current
  `yarn_mappings`, `loader_version`, `loom_version`, `fabric_api_version` for 1.21.11 from https://fabricmc.net/develop/
- **"Unsupported class file major version"**: Gradle is running on the wrong Java. Use JDK 21 (`java -version`).
- **Game says mod requires fabric-api / wrong Minecraft version**: install Fabric API for 1.21.11 and use a 1.21.11 profile.
- **Compile error on a Minecraft class/method**: Yarn names shift between builds; check the error against the
  mapping version in `gradle.properties`.
- **Broken config**: the bad file is renamed to `custom_donut_hud.json.broken` and defaults are loaded.
