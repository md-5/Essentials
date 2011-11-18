package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;


public class Commandafk extends EssentialsCommand
{
	public Commandafk()
	{
		super("afk");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length > 0 && user.isAuthorized("essentials.afk.others"))
		{
			User afkUser = ess.getUser(ess.getServer().matchPlayer(args[0]));
			if (afkUser != null)
			{
				toggleAfk(afkUser);
			}
		}
		else
		{
			toggleAfk(user);
		}
	}

	private final void toggleAfk(User user)
	{
		if (!user.toggleAfk())
		{
			//user.sendMessage(Util.i18n("markedAsNotAway"));
			if (!user.isHidden())
			{
				ess.broadcastMessage(user, Util.format("userIsNotAway", user.getDisplayName()));
			}
			user.updateActivity(false);
		}
		else
		{
			//user.sendMessage(Util.i18n("markedAsAway"));
			if (!user.isHidden())
			{
				ess.broadcastMessage(user, Util.format("userIsAway", user.getDisplayName()));
			}
		}
	}
}
