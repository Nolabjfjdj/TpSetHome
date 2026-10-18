# TpSetHome - Risugami ModLoader / Minecraft Alpha a1.0.16

## Status

This directory is an isolated compatibility investigation for Minecraft Alpha a1.0.16. It is not yet a loadable mod JAR.

The repository's existing Forge and Fabric modules are intentionally untouched.

## Compatibility blocker

Risugami's documented ModLoader API references begin at Alpha 1.2.6, and historical ModLoader releases are documented for later Alpha/Beta versions. I have not verified an official Risugami ModLoader build for Alpha a1.0.16. A normal `mod_*.class` targeting a later ModLoader API therefore cannot honestly be advertised as compatible with a1.0.16.

Before implementing the game integration, the exact loader build and target game's classes must be verified. If no compatible loader exists, the choices are to port ModLoader to a1.0.16 or use a separate legacy patching approach; the latter would not be a standard Risugami ModLoader mod.

## Planned implementation once compatibility is verified

- Keep all work inside this directory until it builds and launches independently.
- Implement `/sethome`, `/home`, and `/delhome` using APIs actually present in the verified target.
- Use storage compatible with the target runtime rather than assuming modern Java NIO APIs exist.
- Add a dedicated CI build only after the module has a real, verified loader dependency.
- Do not change or remove any existing version modules.

## Current validation

No mod JAR is produced by this directory yet. A build result must not be treated as game compatibility until the mod loads in Minecraft Alpha a1.0.16 with the selected loader.
