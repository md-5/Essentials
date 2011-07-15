package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandban extends EssentialsCommand
{
	public Commandban()
	{
		super("ban");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		final User player = getPlayer(server, args, 0, true);
		if (player.isAuthorized("essentials.ban.exempt"))
		{
			sender.sendMessage(Util.i18n("banExempt"));
			return;
		}
		
		String banReason;
		if (args.length > 1)
		{
			banReason = getFinalArg(args, 1);
			player.setBanReason(commandLabel);
		}
		else
		{
			banReason = Util.i18n("defaultBanReason");
		}
		player.kickPlayer(banReason);
		ess.getBans().banByName(player.getName());
		server.broadcastMessage(Util.format("playerBanned", player.getName(), banReason));
	}
}

