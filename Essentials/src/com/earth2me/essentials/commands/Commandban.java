package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandban extends EssentialsCommand
{
	public Commandban()
	{
		super("ban");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		User p = null;
		if (server.matchPlayer(args[0]).isEmpty())
		{
			((CraftServer)server).getHandle().a(args[0]);
			sender.sendMessage(Util.format("playerBanned",args[0]));
		}
		else
		{
			p = ess.getUser(server.matchPlayer(args[0]).get(0));
			String banReason =  Util.i18n("defaultBanReason");
			if(args.length > 1) {
				banReason = getFinalArg(args, 1);
				p.setBanReason(commandLabel);
			}
			p.kickPlayer(banReason);
			((CraftServer)server).getHandle().a(p.getName());
			sender.sendMessage(Util.format("playerBanned", p.getName()));
		}
		ess.loadBanList();
	}
}
