package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import org.bukkit.Server;


public class Commandignore extends EssentialsCommand
{
	public Commandignore()
	{
		super("ignore");
	}

	@Override
	protected void run(final Server server, final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		IUser player;
		try
		{
			player = getPlayer(server, args, 0);
		}
		catch (NoSuchFieldException ex)
		{
			player = ess.getUser(args[0]);
		}
		if (player == null)
		{
			throw new Exception(_("playerNotFound"));
		}
		final String name = player.getName();
		user.acquireWriteLock();
		if (user.isIgnoringPlayer(name))
		{
			user.setIgnoredPlayer(name, false);
			user.sendMessage(_("unignorePlayer", player.getName()));
		}
		else
		{
			user.setIgnoredPlayer(name, true);
			user.sendMessage(_("ignorePlayer", player.getName()));
		}
	}
}
