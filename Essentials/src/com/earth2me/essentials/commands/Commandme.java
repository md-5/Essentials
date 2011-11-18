package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;


public class Commandme extends EssentialsCommand
{
	public Commandme()
	{
		super("me");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (user.isMuted())
		{
			throw new Exception(Util.i18n("voiceSilenced"));
		}

		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		String message = getFinalArg(args, 0);
		if (user.isAuthorized("essentials.chat.color"))
		{
			message = message.replaceAll("&([0-9a-f])", "§$1");
		}

		ess.broadcastMessage(user, Util.format("action", user.getDisplayName(), message));
	}
}
