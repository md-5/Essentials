package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandbalance extends EssentialsCommand
{
	public Commandbalance()
	{
		super("balance");
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		sender.sendMessage(Util.format("balance", Util.formatCurrency(getPlayer(server, args, 0, true).getMoney(), ess)));
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		final double bal = (args.length < 1
							|| !(user.isAuthorized("essentials.balance.others")
								 || user.isAuthorized("essentials.balance.other"))
							? user
							: getPlayer(server, args, 0, true)).getMoney();
		user.sendMessage(Util.format("balance", Util.formatCurrency(bal, ess)));
	}
}
