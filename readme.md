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
  - [x] Real-time scoreboard display on client side (configurable)
  - [x] Top 15 players ranking with colored positions
  - [x] Persistent score storage

- [x] **Comprehensive Commands**
  - [x] Player commands: `/sr question` (configurable permission)
  - [x] Admin commands: Question/reward management, scoreboard control
  - [x] Permission-based command access

- [x] **Easy Configuration**
  - [x] JSON configuration files
  - [x] Hot-reload support
  - [x] Customizable messages and intervals

- [x] **Internationalization (i18n)**
  - [x] Full English and Chinese language support
  - [x] Automatic language detection based on client settings
  - [x] Easy to add more languages

## Quick Start

### Installation
1. Download the latest `sphinxriddle-x.x-NeoForge-1.21.x.jar` from [Releases](https://github.com/SkyDreamLG/SphinxRiddle/releases)
2. Place it in your server's `mods` folder
3. Restart the server

### Basic Usage
**For Players:**
- Answer questions directly in chat when they appear
- Use `/sr question` to manually start a new question (if allowed by config)
- Check your ranking on the right-side scoreboard (if enabled)

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

### Main Configuration (config/sphinxriddle-client.toml)
The mod uses NeoForge's configuration system with the following options:

| Setting | Default | Description |
|---------|---------|-------------|
| `questionInterval` | 300 | Time between auto questions (seconds) |
| `questionTimeout` | 60 | Time before question expires (seconds) |
| `autoQuestionEnabled` | true | Enable automatic questions |
| `allowManualQuestion` | true | Allow players to use `/sr question` |
| `showScoreboard` | true | Display scoreboard on client side |
| `questionPrefix` | 	"&6[问答]&r " | Prefix for question messages |
| `rewardMessage` | "&a恭喜 %player% 正确回答问题！获得奖励: %reward%" | Reward announcement message |
| `newQuestionMessage` | "&e新问题: %question% &e(输入答案到聊天框)" | New question announcement |
| `configReloadedMessage` | "&aSphinxRiddle 配置重载完成" | Config reload message |

`The prompt information in the configuration file defaults to Chinese. If you need other languages, please modify the configuration file accordingly`

### Data Files (config/sphinxriddle/)
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
| `/sr question` | Start a new question | All players (if `allowManualQuestion=true`) or OP only |

### Admin Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/sr reload` | Reload configuration | OP (2) |
| `/sr add question <question> <answer>` | Add new question | OP (2) |
| `/sr add reward <item> <maxAmount>` | Add reward item | OP (2) |
| `/sr list question` | List all questions | OP (2) |
| `/sr list reward` | List all rewards | OP (2) |
| `/sr list ranking` | Show leaderboard | OP (2) |
| `/sr reset ranking` | Reset scores | OP (2) |
| `/sr remove question <question>` | Remove question | OP (2) |
| `/sr remove reward <item>` | Remove reward | OP (2) |

## Language Support

The mod supports multiple languages automatically based on the client's language setting:

- **English (en_us)** - Default language
- **Chinese (zh_cn)** - 简体中文
- **Spanish (es_es)** - Español
- **French (fr_fr)** - Français
- **German (de_de)** - Deutsch
- **Japanese (ja_jp)** - 日本語
- **Russian (ru_ru)** - Русский
- **Portuguese (pt_br)** - Português Brasileiro
- **Korean (ko_kr)** - 한국어
- **Italian (it_it)** - Italiano

The mod will automatically display text in the player's client language. If a language is not fully translated, it will fall back to English.

### Adding New Languages
To add support for a new language:

1. Create a new JSON file in `assets/sphinxriddle/lang/` (e.g., `fr_fr.json`)
2. Add translations using the same keys as in `en_us.json`
3. The mod will automatically detect and use the appropriate language

Example language file structure:
```json
{
  "sphinxriddle.command.help.title": "=== Commandes SphinxRiddle ===",
  "sphinxriddle.command.help.question": "/sr question - Démarrer une nouvelle question"
  // ... more translations
}
```

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

### Internationalization
The mod uses Minecraft's `Component.translatable()` system for all user-facing text. When adding new features:

- Use translation keys instead of hardcoded strings
- Provide translations in both `en_us.json` and `zh_cn.json`
- Use string interpolation with `%s` placeholders when needed

Example:
```java
// Instead of:
Component.literal("Configuration reloaded")

// Use:
Component.translatable("sphinxriddle.command.reload.success")
```

## Support

- [GitHub Discussions](https://github.com/SkyDreamLG/SphinxRiddle/discussions) - For questions and support
- [Issue Tracker](https://github.com/SkyDreamLG/SphinxRiddle/issues) - For bug reports and feature requests

## License

This project is licensed under the GNU LGPL 2.1 License - see the [LICENSE](LICENSE) file for details.

---

**Note**: The scoreboard display and manual question commands can be disabled in the configuration file for server customization.