/*
 *  global-bot - tech.majava.discord.bots.global.CommandsLoader
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

package tech.majava.discord.bots.global;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.reflections.Reflections;
import tech.majava.context.ApplicationContext;
import tech.majava.discord.DiscordModule;
import tech.majava.discord.bots.global.annotations.GlobalCommand;
import tech.majava.discord.bots.global.annotations.GlobalSubcommand;
import tech.majava.discord.commands.Command;
import tech.majava.discord.commands.io.RequestInjector;
import tech.majava.discord.commands.registry.RegistrableCommand;
import tech.majava.discord.commands.structure.CommandStructure;
import tech.majava.discord.commands.structure.ComplexCommand;
import tech.majava.discord.commands.structure.ExecutableCommand;
import tech.majava.discord.commands.structure.SimpleCommand;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p><b>Class {@link tech.majava.discord.bots.global.CommandsLoader}</b></p>
 *
 * @author majksa
 * @version 1.0.0
 * @since 1.0.0
 */
public class CommandsLoader {

    public static final String PLACEHOLDER_DESCRIPTION = "Placeholder description";
    @Nonnull
    private final ApplicationContext context;
    @Nonnull
    private final Reflections reflections;
    @Nonnull
    private final Map<String, CommandStructure> commands = new ConcurrentHashMap<>();

    public CommandsLoader(@Nonnull ApplicationContext context, @Nonnull String namespace) {
        this.context = context;
        reflections = new Reflections(namespace);
    }

    @SuppressWarnings("rawtypes")
    public Collection<CommandStructure> load() {
        commands.clear();
        final Set<Class<? extends Command>> classes = reflections.getSubTypesOf(Command.class);
        classes.forEach(clazz -> {
            if (clazz.isAnnotationPresent(GlobalCommand.class)) {
                createSimpleCommand(clazz);
            }
            if (clazz.isAnnotationPresent(GlobalSubcommand.class)) {
                createComplexCommand(clazz);
            }
        });
        return commands.values();
    }

    @SuppressWarnings("rawtypes")
    private void createSimpleCommand(@Nonnull Class<? extends Command> commandClass) {
        final GlobalCommand annotation = commandClass.getAnnotation(GlobalCommand.class);
        final Command<?> command1 = context.getContainer().get(commandClass);
        final SimpleCommand command = new SimpleCommand(command1, annotation.name(), annotation.description(), annotation.defaultPermission());
        addOptions(command, command1);
        commands.put(annotation.name(), command);
    }

    @SuppressWarnings("rawtypes")
    private void createComplexCommand(@Nonnull Class<? extends Command> commandClass) {
        final GlobalSubcommand annotation = commandClass.getAnnotation(GlobalSubcommand.class);
        commands.computeIfAbsent(annotation.parentCommand(), name -> new ComplexCommand(name, PLACEHOLDER_DESCRIPTION, true));
        final ComplexCommand structure = (ComplexCommand) commands.get(annotation.parentCommand());
        if (annotation.parentGroup().equals("")) {
            structure.addSubcommand(context.getContainer().get(commandClass), annotation.name(), annotation.description(), this::addOptions);
        } else {
            structure.addSubcommandGroup(annotation.parentGroup(), PLACEHOLDER_DESCRIPTION)
                    .addSubcommand(context.getContainer().get(commandClass), annotation.name(), annotation.description(), this::addOptions);
        }
    }

    private void addOptions(@Nonnull ExecutableCommand command) {
        command.addOptions(RequestParser.parseOptions(command.getCommand()));
    }

    private void addOptions(@Nonnull SimpleCommand structure, @Nonnull Command<?> command) {
        structure.addOptions(RequestParser.parseOptions(command));
    }

}
