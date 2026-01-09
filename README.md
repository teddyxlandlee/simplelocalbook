# SimpleLocalBook

A lightweight NeoForge mod that adds a client-only notebook for each world, accessible via command.

## Features

- **Client-Only**: No server-side installation required
- **Per-World Books**: Each world (singleplayer or multiplayer) gets its own separate notebook
- **Persistent Storage**: Books are saved locally in your game directory
- **Async Operations**: Loading and saving happens in the background to prevent game freezes

## Usage

### Command
- `/localnotebook` - Opens the local notebook for the current world

### How It Works

1. **Singleplayer**: Books are stored per singleplayer world folder
2. **Multiplayer**: Books are stored per server IP address
3. **Storage Location**: `/.minecraft/SimpleLocalBook/[singleplayer|multiplayer]/[worldId|serverIp]/book.json`

## Technical Details

- **Mod ID**: `simplelocalbook`
- **Minecraft Version**: 1.21.6
- **NeoForge Version**: 21.6.20-beta
- **Java Version**: 21
- **License**: See LICENSE file

## Installation

1. Download the mod JAR from [Modrinth](https://modrinth.com/mod/simple-local-notebook)
2. Place the JAR file in your `.minecraft/mods/` directory
3. Launch Minecraft with NeoForge
4. Join a world and use `/localnotebook` to access your book

## Building from Source

1. Clone the repository
2. Run `./gradlew build`
3. Find the built JAR in `build/libs/`

## License

This project is licensed under the [MIT License](license.txt).

        