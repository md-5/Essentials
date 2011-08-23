package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandsethome extends EssentialsCommand
{
	public Commandsethome()
	{
		super("sethome");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length > 0)
		{
			if (args.length < 2)
			{
				if (user.isAuthorized("essentials.sethome.multiple"))
				{
					if (user.isAuthorized("essentials.sethome.multiple.unlimited") || user.getHomes().size() <= ess.getSettings().getMultipleHomes())
					{
						user.setHome(args[0]);
					}

				}
			}
			else
			{
				if (user.isAuthorized("essentials.sethome.others"))
				{
					User usersHome = ess.getUser(ess.getServer().getPlayer(args[0]));
					if (usersHome == null)
					{
						usersHome = ess.getOfflineUser(args[0]);
					}
					if (usersHome == null)
					{
						throw new Exception(Util.i18n("playerNotFound"));
					}
					usersHome.setHome(args[1], user.getLocation());
				}
			}
		}
		else
		{
			user.setHome();
		}
		charge(user);
		user.sendMessage(Util.i18n("homeSet"));

	}
}
