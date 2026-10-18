# TpSetHome - Minecraft Alpha a1.0.16 legacy port

## Status

This folder is isolated from all existing Forge and Fabric versions. It is currently a compatibility investigation, not a finished or tested mod.

## What the existing Alpha mods actually show

- Old-MC-Coordinates publishes a build named for Alpha a1.0.16. Its author describes it as a **jarmod** that replaces `GuiIngame.class`, and explicitly warns that Modrinth's loader metadata can be inaccurate for old releases. It is evidence that direct jar patching is used for this era, not proof that a normal Risugami ModLoader plugin API is available in a1.0.16.
- Old-MC-Commands is also described as a jarmod for Alpha and Infdev, but its public repository contains builds for a1.0.9, a1.1.2_01, a1.2.6 and Infdev, not a verified a1.0.16 build.

References:
- https://github.com/Skellz64/Old-MC-Coordinates
- https://modrinth.com/mod/coordinates-old-mc
- https://github.com/Skellz64/Old-MC-Commands

## Implementation approach

Do not assume a modern Forge/Fabric API or a later ModLoader API. The likely route for a1.0.16 is a version-specific jar patch, unless an actual compatible ModLoader binary and API can be verified.

Before producing a patch, inspect the exact a1.0.16 client classes and identify:
- where chat input is accepted and submitted;
- how the local player position and world are represented;
- how a teleport can safely update the local player;
- where per-world or per-player home data can be persisted with APIs available in this old runtime.

Then implement `/sethome`, `/home`, and `/delhome` against those exact classes. Keep the patch and any build scripts inside this directory. Do not change existing version modules.

## Validation

No TpSetHome jar for Alpha a1.0.16 has been produced or tested yet. A successful compilation alone would not prove the patch works in-game; the resulting jar must be installed into a disposable Alpha a1.0.16 instance and tested there.


## Automated reverse-engineering step

The dedicated workflow `.github/workflows/alpha-a1.0.16.yml` downloads the exact Alpha client from Mojang's version manifest, decompiles it in CI, and publishes a short report of the chat and player-coordinate code. This avoids guessing method names from newer Minecraft versions.

The first successful inspection confirms the chat input class is obfuscated as `ab` and contains `keyTyped(KeyEvent)`. The workflow now records its method/field references and signatures for likely entity classes, without publishing the game's source. The exact chat-send hook and coordinate fields must be confirmed from that report before a patch can be compiled. The workflow is an inspection step, not the mod build itself.


## Patch implementation

The first compiled patch was rejected during review because `ab` is the AWT canvas, not the chat screen, so that archive must not be treated as a working release. The inspection workflow now searches the exact Alpha client's keyboard-event dispatch and current-screen classes to find the real chat input hook before rebuilding.

The eventual patch will contain only changed classes and TpSetHome's handler, not Mojang's client JAR. It must still be tested in a disposable Alpha a1.0.16 instance. The Modrinth "Risugami's ModLoader" label is not treated as proof that this is a conventional ModLoader API mod; this route is a direct JAR patch.


The current bytecode inspection found that the canvas class receives AWT key events, while the font renderer is a separate class. The next build is mapping the keyboard dispatch to the active screen's text-entry method. Do not install the first patch artifact; it was compiled successfully but did not target the actual chat input.


The latest inspection is checking the AWT canvas's `keyPressed` path and the active-screen class before selecting the bytecode injection point. The currently generated ZIP remains a diagnostic prototype only, not a usable mod.


The inspection report is uploaded before the patch step so the exact keyboard-dispatch signatures remain available even if the build correctly rejects an unverified injection point. No Alpha patch ZIP should be used until the input hook is verified.
