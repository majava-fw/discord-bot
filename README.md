# Discord Bot
_[Majava Framework](//github.com/majava-fw)_

<p>
    <a href="//github.com/majava-fw/discord-bot/releases"><img src="https://img.shields.io/github/v/release/majava-fw/discord-bot"></a>
    <a href="https://jitpack.io/#majava-fw/discord-bot"><img src="https://img.shields.io/jitpack/v/majava-fw/discord-bot"></a>
    <a href="//github.com/majava-fw/discord-bot/commits/main"><img src="https://img.shields.io/github/last-commit/majava-fw/discord-bot"></a>
    <a href="//github.com/majava-fw/discord-bot/releases"><img src="https://img.shields.io/github/downloads/majava-fw/discord-bot/total"></a>
    <a href="//github.com/majava-fw/discord-bot/blob/main/LICENSE.md"><img src="https://img.shields.io/github/license/majava-fw/discord-bot"></a>
    <a href="//github.com/majava-fw/discord-bot"><img src="https://img.shields.io/github/languages/code-size/majava-fw/discord-bot"></a>
    <a href="//github.com/majava-fw/discord-bot/issues"><img src="https://img.shields.io/github/issues-raw/majava-fw/discord-bot"></a>
    <a href="//java.com"><img src="https://img.shields.io/badge/java-8-orange"></a>
</p>

Java extension for Majava to simplify the creation of global discord bots.

## Summary
1. [Installation](#installation)
    1. [Gradle](#gradle)
    2. [Maven](#maven)  
2. [How to use](#how-to-use)
3. [Built With](#built-with)
4. [Authors](#authors)
5. [License](#license)

## Installation
Make sure to replace `%version%` with the latest version number, or a commit hash, e.g. `1.0.0`.
You can find this library [HERE](https://jitpack.io/#majava-fw/discord-bot)

###  Maven
Register the repository
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
Now add the dependency itself
```xml
<dependency>
    <groupId>com.github.majava-fw</groupId>
    <artifactId>discord-bot</artifactId>
    <version>%version%</version>
</dependency>
```
###  Gradle
Register the repository
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
```
Now add the dependency itself
```gradle
dependencies {
    implementation 'com.github.majava-fw:discord-bot:%version%'
}
```

## How to use
First you need to set up [Discord Module](//github.com/majava-fw/discord).<br>
Next, you need to tell the annotation loader in what package are your commands located.<br>
Example config:
```yaml
modules:
    discord: tech.majava.discord.DiscordModule
    commands: tech.majava.discord.commands.CommandsModule
    global-bot: tech.majava.discord.bot.global.GlobalBotModule
discord:
    token: ...
global-bot:
    production: false # if true, commands are registered globally (can take some time for discord to update)
```
Then you need to add all commands to the container.
You can do this by either creating a new Module and adding it to the Container manually or by using a Dependency Injection library.

### How to set up a command with annotations

## Built With

* [Java 8](https://java.com)

## Authors
* [Majksa (@maxa-ondrej)](https://github.com/maxa-ondrej)

## License

This project is licensed under the GPL-3.0 License - see the [LICENSE](LICENSE) file for details