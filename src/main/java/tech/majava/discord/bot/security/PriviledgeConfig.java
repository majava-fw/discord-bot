/*
 *  global-bot - tech.majava.discord.bots.global.GlobalBotConfig
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

package tech.majava.discord.bot.security;

import lombok.Data;
import lombok.NoArgsConstructor;
import tech.majava.context.config.Config;
import tech.majava.discord.commands.registry.CommandPermissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p><b>Class {@link tech.majava.discord.bot.security.PriviledgeConfig}</b></p>
 *
 * @author majksa
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
public final class PriviledgeConfig implements Config {

    private List<Long> users = Collections.emptyList();

    private List<Long> roles = Collections.emptyList();

}
