/*
 *  global-bot - tech.majava.discord.bots.loader.GlobalLoader
 *  Copyright (C) 2022  Majksa
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

package tech.majava.discord.bot.loader;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import tech.majava.context.ApplicationContext;
import tech.majava.discord.commands.management.CommandsManager;
import tech.majava.discord.commands.registry.GlobalCommand;
import tech.majava.discord.commands.registry.RegistrableCommand;
import tech.majava.discord.commands.structure.CommandStructure;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * <p><b>Class {@link GlobalLoader}</b></p>
 *
 * @author majksa
 * @version 1.0.0
 * @since 1.0.0
 */
public class GlobalLoader extends CommonLoader {

    private final JDA jda;
    private final boolean production;

    public GlobalLoader(@Nonnull ApplicationContext context, @Nonnull JDA jda, boolean production) {
        super(context);
        this.jda = jda;
        this.production = production;
    }

    /**
     * If not running in production, commands will be registered for each guild.
     * Otherwise, it could take up to several hours, for the commands to update.
     *
     * @param manager {@link tech.majava.discord.commands.management.CommandsManager}
     * @return the list of registered commands
     */
    @Override
    public CompletableFuture<List<Command>> register(@Nonnull CommandsManager manager) {
        if (production) {
            return super.register(manager);
        }

        return CompletableFuture.supplyAsync(() -> {
            final ArrayList<Command> list = new ArrayList<>();
            final List<CompletableFuture<List<Command>>> results = jda.getGuilds()
                    .stream()
                    .map(guild -> new GuildLoader(context, guild))
                    .map(loader -> loader.register(manager))
                    .collect(Collectors.toList());
            for (CompletableFuture<List<Command>> result : results) {
                list.addAll(result.join());
            }
            return list;
        });
    }

    @Override
    protected RegistrableCommand createRegistrable(@Nonnull CommandStructure structure) {
        return new GlobalCommand(jda, structure);
    }

}
