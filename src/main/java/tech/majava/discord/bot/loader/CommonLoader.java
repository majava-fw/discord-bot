/*
 *  global-bot - tech.majava.discord.bots.loader.CommonLoader
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

import org.reflections.Reflections;
import tech.majava.context.ApplicationContext;
import tech.majava.discord.bot.RequestParser;
import tech.majava.discord.bot.annotations.SlashCommand;
import tech.majava.discord.bot.annotations.Subcommand;
import tech.majava.discord.commands.management.CommandsManager;
import tech.majava.discord.commands.registry.RegistrableCommand;
import tech.majava.discord.commands.structure.CommandStructure;
import tech.majava.discord.commands.structure.ComplexCommand;
import tech.majava.discord.commands.structure.ExecutableCommand;
import tech.majava.discord.commands.structure.SimpleCommand;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * <p><b>Class {@link tech.majava.discord.bot.loader.CommonLoader}</b></p>
 *
 * @author majksa
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class CommonLoader {


    public static final String PLACEHOLDER_DESCRIPTION = "Placeholder description";
    @Nonnull
    protected final ApplicationContext context;
    @Nonnull
    protected final Reflections reflections;

    protected CommonLoader(@Nonnull ApplicationContext context) {
        this.context = context;
        reflections = new Reflections();
    }

    public CompletableFuture<List<net.dv8tion.jda.api.interactions.commands.Command>> register(@Nonnull CommandsManager manager) {
        return manager.register(
                load()
                        .stream()
                        .map(this::createRegistrable)
                        .toArray(RegistrableCommand[]::new)
        );
    }

    protected abstract RegistrableCommand createRegistrable(@Nonnull CommandStructure structure);

    private List<CommandStructure> load() {
        final List<CommandStructure> commands = new ArrayList<>();
        final Map<String, ComplexCommand> parents = new ConcurrentHashMap<>();
        final Function<String, ComplexCommand> getParent = name -> {
            parents.putIfAbsent(name, new ComplexCommand(name, PLACEHOLDER_DESCRIPTION, true));
            return parents.get(name);
        };
        reflections.getSubTypesOf(tech.majava.discord.commands.Command.class)
                .forEach(clazz -> {
                    if (clazz.isAnnotationPresent(SlashCommand.class)) {
                        commands.add(createSimpleCommand(clazz));
                    }
                    if (clazz.isAnnotationPresent(Subcommand.class)) {
                        commands.add(createComplexCommand(clazz, getParent));
                    }
                });
        return commands;
    }

    @Nonnull
    @SuppressWarnings("rawtypes")
    private SimpleCommand createSimpleCommand(@Nonnull Class<? extends tech.majava.discord.commands.Command> commandClass) {
        final SlashCommand annotation = commandClass.getAnnotation(SlashCommand.class);
        final tech.majava.discord.commands.Command<?> command1 = context.getContainer().get(commandClass);
        final SimpleCommand command = new SimpleCommand(command1, annotation.name(), annotation.description(), annotation.defaultPermission());
        addOptions(command, command1);
        return command;
    }

    @Nonnull
    @SuppressWarnings("rawtypes")
    private ComplexCommand createComplexCommand(@Nonnull Class<? extends tech.majava.discord.commands.Command> commandClass, @Nonnull Function<String, ComplexCommand> getParent) {
        final Subcommand annotation = commandClass.getAnnotation(Subcommand.class);
        final ComplexCommand structure = getParent.apply(annotation.parentCommand());
        if (annotation.parentGroup().equals("")) {
            structure.addSubcommand(context.getContainer().get(commandClass), annotation.name(), annotation.description(), this::addOptions);
        } else {
            structure.addSubcommandGroup(annotation.parentGroup(), PLACEHOLDER_DESCRIPTION)
                    .addSubcommand(context.getContainer().get(commandClass), annotation.name(), annotation.description(), this::addOptions);
        }
        return structure;
    }

    private void addOptions(@Nonnull ExecutableCommand command) {
        command.addOptions(RequestParser.parseOptions(command.getCommand()));
    }

    private void addOptions(@Nonnull SimpleCommand structure, @Nonnull tech.majava.discord.commands.Command<?> command) {
        structure.addOptions(RequestParser.parseOptions(command));
    }

}
