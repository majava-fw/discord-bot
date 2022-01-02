/*
 *  global-bot - tech.majava.discord.bots.global.GlobalBotModule
 *  Copyright (C) 2021  Majksa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tech.majava.discord.bot;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import tech.majava.context.ApplicationContext;
import tech.majava.discord.bot.loader.GlobalLoader;
import tech.majava.discord.bot.loader.GuildLoader;
import tech.majava.discord.commands.CommandsModule;
import tech.majava.logging.LoggingModule;
import tech.majava.modules.Module;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * <p><b>Class {@link BotModule}</b></p>
 *
 * @author majksa
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
public final class BotModule extends Module<BotConfig> {

    private CommandsModule commands;
    private LoggingModule logging;
    private JDA jda;

    public BotModule(@Nonnull BotConfig config, @Nonnull ApplicationContext context) {
        super(config, context, "bot", "module for global discord bots");
        dependencies.add(CommandsModule.class);
        dependencies.add(LoggingModule.class);
    }

    @Nonnull
    @Override
    protected CompletableFuture<Void> onStart() {
        logging = context.getModules().get(LoggingModule.class);
        commands = context.getModules().get(CommandsModule.class);
        jda = commands.getDiscordModule().getJda();

        return (config.getGuild().isEmpty() ? new GlobalLoader(context, jda, config.isProduction()) : new GuildLoader(context, Objects.requireNonNull(jda.getGuildById(config.getGuild())), config.getGroups(), config.getCommands()))
                .register(commands.getManager())
                .thenAccept(commands -> logging.getLogger().atDebug().log("Successfully registered {} global commands.", commands.size()));
    }

}
