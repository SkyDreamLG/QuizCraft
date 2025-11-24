# Sphinx Riddle - Minecraft Quiz Competition Mod

<div align="center">
  <img alt="logo" src="https://raw.githubusercontent.com/SkyDreamLG/SphinxRiddle/refs/heads/master/logo.svg" width="120px">
  <br><br>
  <em>🎮 Quiz Competition System for Minecraft Servers</em>
</div>

<p align="center">
  <a href="https://github.com/SkyDreamLG/SphinxRiddle/releases"><img alt="release" src="https://img.shields.io/github/v/release/SkyDreamLG/SphinxRiddle?style=for-the-badge"></a>
  <a href="https://github.com/SkyDreamLG/SphinxRiddle/issues"><img alt="issues" src="https://img.shields.io/github/issues/SkyDreamLG/SphinxRiddle?style=for-the-badge"></a>
  <a href="./LICENSE"><img alt="license" src="https://img.shields.io/badge/license-LGPL--2.1-green?style=for-the-badge"></a>
</p>

---

English | [中文](./readme_cn.md)

## Features

- [x] **Auto Quiz System**
  - [x] Automatic question intervals with configurable timing
  - [x] Question timeout handling
  - [x] JSON-based question and reward configuration

- [x] **Smart Answer Detection**
  - [x] Contains-based answer matching (e.g., "龟派气功" matches "气功")
  - [x] Case-insensitive matching
  - [x] First-correct-player wins system

- [x] **Reward System**
  - [x] Random reward selection from configurable pool
  - [x] Variable reward amounts
  - [x] Item auto-completion with MC's item selector

- [x] **Scoreboard & Ranking**
  - [x] Real-time scoreboard display on client side
  - [x] Top 15 players ranking with colored positions
  - [x] Persistent score storage

- [x] **Comprehensive Commands**
  - [x] Player commands: `/sr question`
  - [x] Admin commands: Question/reward management, scoreboard control
  - [x] Permission-based command access

- [x] **Easy Configuration**
  - [x] JSON configuration files
  - [x] Hot-reload support
  - [x] Customizable messages and intervals

## Quick Start

### Installation
1. Download the latest `sphinx_riddle.jar` from [Releases](https://github.com/SkyDreamLG/SphinxRiddle/releases)
2. Place it in your server's `mods` folder
3. Restart the server

### Basic Usage
**For Players:**
- Answer questions directly in chat when they appear
- Use `/sr question` to manually start a new question
- Check your ranking on the right-side scoreboard

**For Admins:**
```bash
# Reload configuration
/sr reload

# Add questions
/sr add question "What tool mines diamond?" "Iron Pickaxe"

# Add rewards with auto-completion
/sr add reward minecraft:diamond 5

# Manage rankings
/sr list ranking
/sr reset ranking
```

## Configuration

Configuration files are located in `config/sphinxriddle/`:

- `questions.json` - Question and answer pairs
- `rewards.json` - Reward items and maximum amounts
- `scoreboard.json` - Player scores and rankings

### Example Configuration

**questions.json:**
```json
[
  {
    "question": "What mob explodes in Minecraft?",
    "answer": "Creeper"
  }
]
```

**rewards.json:**
```json
[
  {
    "itemId": "minecraft:diamond",
    "maxAmount": 5
  }
]
```

## Commands Reference

### Player Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/sr question` | Start a new question | All players |

### Admin Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/sr reload` | Reload configuration | OP |
| `/sr add question <question> <answer>` | Add new question | OP |
| `/sr add reward <item> <maxAmount>` | Add reward item | OP |
| `/sr list question` | List all questions | OP |
| `/sr list reward` | List all rewards | OP |
| `/sr list ranking` | Show leaderboard | OP |
| `/sr reset ranking` | Reset scores | OP |
| `/sr remove question <question>` | Remove question | OP |
| `/sr remove reward <item>` | Remove reward | OP |

## Development

### Building from Source
```bash
git clone https://github.com/SkyDreamLG/SphinxRiddle.git
cd SphinxRiddle
./gradlew build
```

### Requirements
- Minecraft 1.20.1+
- NeoForge
- Java 17+

## Support

- [GitHub Discussions](https://github.com/SkyDreamLG/SphinxRiddle/discussions) - For questions and support
- [Issue Tracker](https://github.com/SkyDreamLG/SphinxRiddle/issues) - For bug reports and feature requests

## License

This project is licensed under the GNU LGPL 2.1 License - see the [LICENSE](LICENSE) file for details.
