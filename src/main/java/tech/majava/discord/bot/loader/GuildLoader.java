/*
 *  global-bot - tech.majava.discord.bots.loader.GuildLoader
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

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import tech.majava.context.ApplicationContext;
import tech.majava.discord.bot.security.GroupConfig;
import tech.majava.discord.commands.management.CommandsManager;
import tech.majava.discord.commands.registry.CommandPermissions;
import tech.majava.discord.commands.registry.GuildCommand;
import tech.majava.discord.commands.registry.RegistrableCommand;
import tech.majava.discord.commands.structure.CommandStructure;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * <p><b>Class {@link GuildLoader}</b></p>
 *
 * @author majksa
 * @version 1.0.0
 * @since 1.0.0
 */
public class GuildLoader extends CommonLoader {

    @Nonnull
    private final Guild guild;

    @Nonnull
    private final Map<String, GroupConfig> groups;

    @Nonnull
    private final Map<String, String> commands;

    public GuildLoader(@Nonnull ApplicationContext context, @Nonnull Guild guild) {
        this(context, guild, Collections.emptyMap(), Collections.emptyMap());
    }

    public GuildLoader(@Nonnull ApplicationContext context, @Nonnull Guild guild, @Nonnull Map<String, GroupConfig> groups, @Nonnull Map<String, String> commands) {
        super(context);
        this.guild = guild;
        this.groups = groups;
        this.commands = commands;
    }

    /**
     * Registers commands and then apply permissions.
     *
     * @param manager {@link tech.majava.discord.commands.management.CommandsManager}
     * @return the list of registered commands
     */
    @Override
    public CompletableFuture<List<Command>> register(@Nonnull CommandsManager manager) {
        return super.register(manager);
    }

    @Override
    protected RegistrableCommand createRegistrable(@Nonnull CommandStructure structure) {
        final GuildCommand command = new GuildCommand(guild, structure);
        final String group = commands.getOrDefault(structure.getData().getName(), "default");
        if (!groups.isEmpty() && groups.containsKey(group)) {
            final CommandPermissions permissions = command.getPermissions();
            final GroupConfig config = groups.get(group);
            applyPermissions(permissions, CommandPrivilege.Type.ROLE, true, config.getAllow().getRoles());
            applyPermissions(permissions, CommandPrivilege.Type.ROLE, false, config.getDeny().getRoles());
            applyPermissions(permissions, CommandPrivilege.Type.USER, true, config.getAllow().getUsers());
            applyPermissions(permissions, CommandPrivilege.Type.USER, false, config.getDeny().getUsers());
        }
        return command;
    }

    private void applyPermissions(@Nonnull CommandPermissions permissions, @Nonnull CommandPrivilege.Type type, boolean enabled, @Nonnull List<Long> ids) {
        ids.forEach(id -> permissions.set(type, enabled, id));
    }

}
