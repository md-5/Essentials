package com.earth2me.essentials.commands;

import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;


public class Commandbanip extends EssentialsCommand
{
	public Commandbanip()
	{
		super("banip");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		((CraftServer)server).getHandle().c(args[0]);
		sender.sendMessage(Util.i18n("banIpAddress"));
		ess.loadBanList();

	}
}
