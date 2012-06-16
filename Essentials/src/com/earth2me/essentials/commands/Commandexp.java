package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.craftbukkit.SetExpFix;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandexp extends EssentialsCommand
{
	public Commandexp()
	{
		super("exp");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length == 0)
		{
			showExp(user, user);
		}
		else if (args[0].equalsIgnoreCase("set") && user.isAuthorized("essentials.exp.set"))
		{
			if (args.length == 3 && user.isAuthorized("essentials.exp.set.others"))
			{
				expMatch(server, user, args[1], args[2], false);
			}
			else
			{
				setExp(user, user, args[1], false);
			}
		}
		else if (args[0].equalsIgnoreCase("give") && user.isAuthorized("essentials.exp.give"))
		{
			if (args.length == 3 && user.isAuthorized("essentials.exp.give.others"))
			{
				expMatch(server, user, args[1], args[2], true);
			}
			else
			{
				setExp(user, user, args[1], true);
			}
		}
		else
		{
			String match = args[0].trim();
			if (args.length == 2)
			{
				match = args[1].trim();
			}
			if (match.equalsIgnoreCase("show") || !user.isAuthorized("essentials.exp.others"))
			{
				showExp(user, user);
			}
			else
			{
				showMatch(server, user, match);
			}
		}
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		else if (args.length > 2 && args[0].equalsIgnoreCase("set"))
		{
			expMatch(server, sender, args[1], args[2], false);
		}
		else if (args.length > 2 && args[0].equalsIgnoreCase("give"))
		{
			expMatch(server, sender, args[1], args[2], true);
		}
		else
		{
			String match = args[0].trim();
			if (args.length == 2)
			{
				match = args[1].trim();
			}
			showMatch(server, sender, match);
		}
	}

	private void showMatch(final Server server, final CommandSender sender, final String match) throws NotEnoughArgumentsException
	{
		boolean foundUser = false;
		for (Player matchPlayer : server.matchPlayer(match))
		{
			foundUser = true;
			final User target = ess.getUser(matchPlayer);
			showExp(sender, target);
		}
		if (!foundUser)
		{
			throw new NotEnoughArgumentsException(_("playerNotFound"));
		}
	}

	private void expMatch(final Server server, final CommandSender sender, final String match, final String amount, final boolean toggle) throws NotEnoughArgumentsException
	{
		boolean foundUser = false;
		for (Player matchPlayer : server.matchPlayer(match))
		{
			final User target = ess.getUser(matchPlayer);
			setExp(sender, target, amount, toggle);
			foundUser = true;
		}
		if (!foundUser)
		{
			throw new NotEnoughArgumentsException(_("playerNotFound"));
		}
	}

	private void showExp(final CommandSender sender, final User target)
	{
		final int totalExp = SetExpFix.getTotalExperience(target);
		final int expLeft = (int)Util.roundDouble(((((3.5 * target.getLevel()) + 6.7) - (totalExp - ((1.75 * (target.getLevel() * target.getLevel())) + (5.00 * target.getLevel())))) + 1));
		sender.sendMessage(_("exp", target.getDisplayName(), SetExpFix.getTotalExperience(target), target.getLevel(), expLeft));
	}

	private void setExp(final CommandSender sender, final User target, final String strAmount, final boolean give)
	{
		Long amount = Long.parseLong(strAmount);
		if (give)
		{
			amount += SetExpFix.getTotalExperience(target);
		}
		if (amount > Integer.MAX_VALUE)
		{
			amount = (long)Integer.MAX_VALUE;
		}
		SetExpFix.setTotalExperience(target, amount.intValue());
		sender.sendMessage(_("expSet", target.getDisplayName(), amount));
	}
}
