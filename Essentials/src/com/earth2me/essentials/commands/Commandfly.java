package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.permissions.Permissions;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandfly extends EssentialsCommand
{
	@Override
	protected void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		flyOtherPlayers(server, sender, args);
	}

	@Override
	protected void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length > 0 && args[0].trim().length() > 2 && Permissions.FLY_OTHERS.isAuthorized(user))
		{
			flyOtherPlayers(server, user, args);
			return;
		}
		user.setAllowFlight(!user.getAllowFlight());
		if (!user.getAllowFlight())
		{
			user.setFlying(false);
		}
		user.sendMessage(_("flyMode", _(user.getAllowFlight() ? "enabled" : "disabled"), user.getDisplayName()));
	}

	private void flyOtherPlayers(final Server server, final CommandSender sender, final String[] args)
	{
		for (Player matchPlayer : server.matchPlayer(args[0]))
		{
			final IUser player = ess.getUser(matchPlayer);
			if (player.isHidden())
			{
				continue;
			}
			if (args.length > 1)
			{
				if (args[1].contains("on") || args[1].contains("ena") || args[1].equalsIgnoreCase("1"))
				{
					player.setAllowFlight(true);
				}
				else
				{
					player.setAllowFlight(false);
				}
			}
			else
			{
				player.setAllowFlight(!player.getAllowFlight());
			}
			
			if (!player.getAllowFlight())
			{
				player.setFlying(false);
			}
			sender.sendMessage(_("flyMode", _(player.getAllowFlight() ? "enabled" : "disabled"), player.getDisplayName()));
		}
	}
}
