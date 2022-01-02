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

package tech.majava.discord.bots.global;/*
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

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import tech.majava.context.ApplicationContext;
import tech.majava.discord.commands.CommandsModule;
import tech.majava.discord.commands.registry.GlobalCommand;
import tech.majava.discord.commands.registry.GuildCommand;
import tech.majava.discord.commands.registry.RegistrableCommand;
import tech.majava.discord.commands.structure.CommandStructure;
import tech.majava.logging.LoggingModule;
import tech.majava.modules.Module;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p><b>Class {@link tech.majava.discord.bots.global.GlobalBotModule}</b></p>
 *
 * @author majksa
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
public class GlobalBotModule extends Module<GlobalBotConfig> {

    private CommandsModule commands;
    private LoggingModule logging;
    private JDA jda;

    public GlobalBotModule(@Nonnull GlobalBotConfig config, @Nonnull ApplicationContext context) {
        super(config, context, "global-bot", "module for global discord bots");
        dependencies.add(CommandsModule.class);
        dependencies.add(LoggingModule.class);
    }

    @Nonnull
    @Override
    protected CompletableFuture<Void> onStart() {
        logging = context.getModules().get(LoggingModule.class);
        commands = context.getModules().get(CommandsModule.class);
        jda = commands.getDiscordModule().getJda();

        final Collection<CommandStructure> structures = new CommandsLoader(context, config.getNamespace()).load();
        final RegistrableCommand[] registrableCommands = config.isProduction() ? prepareGlobal(structures) : prepareGuilds(structures);
        return commands
                .getManager()
                .register(registrableCommands)
                .thenAccept(commands -> logging.getLogger().atDebug().log("Successfully registered {} global commands.", commands.size()));
    }

    private RegistrableCommand[] prepareGlobal(@Nonnull Collection<CommandStructure> structures) {
        return structures
                .stream()
                .map(structure -> new GlobalCommand(jda, structure))
                .toArray(RegistrableCommand[]::new);
    }

    private RegistrableCommand[] prepareGuilds(@Nonnull Collection<CommandStructure> structures) {
        return jda.getGuilds()
                .stream()
                .map(guild -> structures
                        .stream()
                        .map(structure -> new GuildCommand(guild, structure))
                        .collect(Collectors.toList())
                )
                .collect(() -> new ArrayList<RegistrableCommand>(), ArrayList::addAll, ArrayList::addAll)
                .toArray(new RegistrableCommand[0]);
    }

}
