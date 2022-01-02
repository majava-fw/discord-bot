/*
 *  global-bot - tech.majava.discord.bots.RequestParser
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

package tech.majava.discord.bot;

import com.google.common.base.CaseFormat;
import lombok.Data;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import tech.majava.discord.bot.annotations.Option;
import tech.majava.discord.commands.Command;
import tech.majava.discord.commands.io.Request;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p><b>Class {@link RequestParser}</b></p>
 *
 * @author majksa
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public final class RequestParser<R extends Request> {

    private static final Map<Class<? extends Request>, OptionData[]> cache = new ConcurrentHashMap<>();

    private final Class<R> clazz;

    public static OptionData[] parseOptions(@Nonnull Command<?> command) {
        final Class<? extends Request> requestClass = command.getRequestClass();
        cache.computeIfAbsent(requestClass, unused ->
                Arrays.stream(requestClass.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(Option.class))
                        .map(RequestParser::parseOption)
                        .toArray(OptionData[]::new)
        );
        return cache.get(requestClass);
    }

    private static OptionData parseOption(@Nonnull Field field) {
        final Option annotation = field.getAnnotation(Option.class);
        return new OptionData(getType(field.getType()), convertOptionName(field.getName()), annotation.description(), !annotation.optional());
    }

    public static String convertOptionName(@Nonnull String text) {
        return CaseFormat.LOWER_CAMEL
                .converterTo(CaseFormat.LOWER_HYPHEN)
                .convert(text);
    }

    private static OptionType getType(@Nonnull Class<?> type) {
        if (type.isAssignableFrom(Member.class) || type.isAssignableFrom(User.class)) {
            return OptionType.USER;
        }
        if (type.isAssignableFrom(Role.class)) {
            return OptionType.ROLE;
        }
        if (type.isAssignableFrom(String.class)) {
            return OptionType.STRING;
        }
        if (type.isAssignableFrom(Boolean.class)) {
            return OptionType.BOOLEAN;
        }
        if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(Long.class)) {
            return OptionType.INTEGER;
        }
        if (type.isAssignableFrom(MessageChannel.class) || type.isAssignableFrom(GuildChannel.class)) {
            return OptionType.CHANNEL;
        }
        if (type.isAssignableFrom(IMentionable.class)) {
            return OptionType.MENTIONABLE;
        }
        throw new NullPointerException();
    }

}
