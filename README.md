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
   1. [Creating a command](#creating-a-command)
   2. [Creating a subcommand](#creating-a-subcommand)
   3. [Configuring a Global bot](#configuring-a-global-bot)
   4. [Configuring a one Guild bot](#configuring-a-one-guild-bot)
3. [Built With](#built-with)
4. [Authors](#authors)
5. [License](#license)

## Installation

Make sure to replace `%version%` with the latest version number, or a commit hash, e.g. `1.0.0`. You can find this
library [HERE](https://jitpack.io/#majava-fw/discord-bot)

### Maven

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

### Gradle

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

First you need to set up [Discord Module](//github.com/majava-fw/discord). \
Then you need to add all commands to the container. The recommended way is to
use [our Guice extension](https://github.com/majava-fw/guice), but you can also add them to the Container manually.

### Creating a command

As this library is request-response based, you first need to create a request. This is what the command will receive on
its execution and how arguments will look like. You do not need to worry about parsing the event object and assigning
values to request fields, that's done by the extension. You just need to create **getters and setters**.

Example request with one required argument `message` of type String and one optional `to` of type User.

```java
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import tech.majava.discord.bot.annotations.Option;
import tech.majava.discord.commands.io.Request;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Getter
@Setter
public class MessageRequest extends Request {

    @Nonnull
    @Option(description = "An epic message")
    private String message;

    @Nullable
    @Option(description = "Who will receive this epic message", optional = true)
    private User to;

    public MessageRequest(SlashCommandEvent event) {
        super(event);
    }

}
```

Now let's create the command itself. As you can see, the response is created asynchronously, to increase efficiency and
prevent timeouts. You can also throw CommandException, which will be transformed into a response by the extension.

```java
import tech.majava.discord.bot.annotations.SlashCommand;
import tech.majava.discord.commands.Command;
import tech.majava.discord.commands.io.CommandException;
import tech.majava.discord.commands.io.Response;
import tech.majava.discord.responses.MessageTemplateImpl;

@SlashCommand(name = "test", description = "An epic command")
public class TestCommand implements Command<MessageRequest> {

    @Nonnull
    @Override
    public Response run(@Nonnull MessageRequest request) throws CommandException {
        if (request.getMessage().length() == 0) {
            throw new CommandException("Message is empty!", true);
        }
        return new ResponseBuilder()
                .setEphemeral(true)
                .setTemplate(() -> {
                    final MessageTemplateImpl template = new MessageTemplateImpl();
                    if (request.getTo() != null) {
                        template.append("To: ").append(request.getTo().getAsMention()).append("\n");
                    }
                    template.append(request.getMessage());
                })
                .build();
    }

}
```

### Creating a subcommand

It's the same as creating a command, just instead of annotating it
with `tech.majava.discord.bot.annotations.SlashCommand`, you need to
use `tech.majava.discord.bot.annotations.Subcommand`.
Example for `/command test`:

```java
import tech.majava.discord.bot.annotations.Subcommand;

@Subcommand(name = "test", description = "An epic command", parentCommand = "command")
public class TestSubCommand implements Command<MessageRequest> {
    // ...
}
```
Example for `/command group test`:

```java
import tech.majava.discord.bot.annotations.Subcommand;

@Subcommand(name = "test", description = "An epic command", parentCommand = "command", parentGroup = "group")
public class TestSubCommandWithGroup implements Command<MessageRequest> {
    // ...
}
```

### Configuring a Global bot

Please note that the whole permissions part (groups, commands) is optional.

```yaml
discord:
    token: ...
bot:
    production: false # if true, commands are registered globally (can take some time for discord to update)

# skip this if you are using our Guice extension
modules:
    discord: tech.majava.discord.DiscordModule
    commands: tech.majava.discord.commands.CommandsModule
    bot: tech.majava.discord.bot.BotModule
```

### Configuring a one Guild bot

```yaml
discord:
    token: ...
bot:
    guild: 743167536425861211 # the id of your guild
    commands:
        my-epic-command: admins
    groups:
        # this is the default group. these permissions will be applied to all commands without a group
        default:
            allow:
                roles: [ 764517000323268638 ]
                users: [ ]
            deny:
                roles: [ ]
                users: [ ]
        # this is the default group. these permissions will be applied to all commands without a group
        admins:
            allow:
                roles: [ 764204633597018174 ]
                users: [ ]
            deny:
                roles: [ 764517000323268638 ]
                users: [ ]

# skip this if you are using our Guice extension
modules:
    discord: tech.majava.discord.DiscordModule
    commands: tech.majava.discord.commands.CommandsModule
    bot: tech.majava.discord.bot.BotModule
```

## Built With

* [Java 8](https://java.com)

## Authors

* [Majksa (@maxa-ondrej)](https://github.com/maxa-ondrej)

## License

This project is licensed under the GPL-3.0 License - see the [LICENSE](LICENSE) file for details