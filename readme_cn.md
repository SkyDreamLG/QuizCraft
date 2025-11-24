# Sphinx Riddle - Minecraft 问答竞赛模组

<div align="center">
  <img alt="logo" src="https://raw.githubusercontent.com/SkyDreamLG/SphinxRiddle/refs/heads/master/logo.svg" width="120px">
  <br><br>
  <em>🎮 一个功能完整、可定制的 Minecraft 服务器问答竞赛系统</em>
</div>

<p align="center">
  <a href="https://github.com/SkyDreamLG/SphinxRiddle/releases"><img alt="release" src="https://img.shields.io/github/v/release/SkyDreamLG/SphinxRiddle?style=for-the-badge"></a>
  <a href="https://github.com/SkyDreamLG/SphinxRiddle/issues"><img alt="issues" src="https://img.shields.io/github/issues/SkyDreamLG/SphinxRiddle?style=for-the-badge"></a>
  <a href="./LICENSE"><img alt="license" src="https://img.shields.io/badge/license-LGPL--2.1-green?style=for-the-badge"></a>
</p>

---

[English](./readme.md) | 中文

## 功能特性

- [x] **自动问答系统**
  - [x] 可配置时间间隔的自动提问
  - [x] 问题超时处理
  - [x] 基于JSON的问题和奖励配置

- [x] **智能答案检测**
  - [x] 包含式答案匹配（如"龟派气功"匹配"气功"）
  - [x] 不区分大小写匹配
  - [x] 第一个回答正确的玩家获胜系统

- [x] **奖励系统**
  - [x] 从可配置池中随机选择奖励
  - [x] 可变的奖励数量
  - [x] 使用MC物品选择器的自动补全功能

- [x] **计分板和排名系统**
  - [x] 客户端实时计分板显示
  - [x] 前15名玩家排名，带颜色标识
  - [x] 持久化分数存储

- [x] **完整命令系统**
  - [x] 玩家命令：`/sr question`
  - [x] 管理员命令：问题/奖励管理，计分板控制
  - [x] 基于权限的命令访问

- [x] **简易配置**
  - [x] JSON配置文件
  - [x] 热重载支持
  - [x] 可自定义消息和时间间隔

## 快速开始

### 安装
1. 从[发布页面](https://github.com/SkyDreamLG/SphinxRiddle/releases)下载最新的 `sphinx_riddle.jar`
2. 将其放入服务器的 `mods` 文件夹
3. 重启服务器

### 基本使用
**玩家操作：**
- 问题出现时直接在聊天框回答
- 使用 `/sr question` 手动开始新问题
- 在右侧计分板查看排名

**管理员操作：**
```bash
# 重载配置
/sr reload

# 添加问题
/sr add question "什么工具可以挖钻石？" "铁镐"

# 添加奖励（支持自动补全）
/sr add reward minecraft:diamond 5

# 管理排名
/sr list ranking
/sr reset ranking
```

## 配置

配置文件位于 `config/sphinxriddle/`：

- `questions.json` - 问题和答案对
- `rewards.json` - 奖励物品和最大数量
- `scoreboard.json` - 玩家分数和排名

### 配置示例

**questions.json:**
```json
[
  {
    "question": "Minecraft中什么生物会爆炸？",
    "answer": "苦力怕"
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

## 命令参考

### 玩家命令
| 命令 | 描述 | 权限 |
|------|------|------|
| `/sr question` | 开始新问题 | 所有玩家 |

### 管理员命令
| 命令 | 描述 | 权限 |
|------|------|------|
| `/sr reload` | 重载配置 | OP |
| `/sr add question <问题> <答案>` | 添加新问题 | OP |
| `/sr add reward <物品> <最大数量>` | 添加奖励物品 | OP |
| `/sr list question` | 列出所有问题 | OP |
| `/sr list reward` | 列出所有奖励 | OP |
| `/sr list ranking` | 显示排行榜 | OP |
| `/sr reset ranking` | 重置分数 | OP |
| `/sr remove question <问题>` | 移除问题 | OP |
| `/sr remove reward <物品>` | 移除奖励 | OP |

## 开发

### 从源码构建
```bash
git clone https://github.com/SkyDreamLG/SphinxRiddle.git
cd SphinxRiddle
./gradlew build
```

### 要求
- Minecraft 1.20.1+
- NeoForge
- Java 17+

## 支持

- [GitHub讨论区](https://github.com/SkyDreamLG/SphinxRiddle/discussions) - 问题和支持
- [问题追踪](https://github.com/SkyDreamLG/SphinxRiddle/issues) - 错误报告和功能请求

## 许可证

本项目采用 GNU LGPL 2.1 许可证 - 详见 [LICENSE](LICENSE) 文件。
