package net.ess3.xmpp;

import net.ess3.Console;
import net.ess3.commands.EssentialsCommand;
import net.ess3.commands.NotEnoughArgumentsException;
import org.bukkit.entity.Player;


public class Commandxmpp extends EssentialsCommand
{
	@Override
	protected void run(final CommandSender sender, final String commandLabel, final String[] args) throws NotEnoughArgumentsException
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		final String address = EssentialsXMPP.getInstance().getAddress(args[0]);
		if (address == null)
		{
			sender.sendMessage("§cThere are no players matching that name.");
		}
		else
		{
			final String message = getFinalArg(args, 1);
			final String senderName = sender instanceof Player ? ess.getUserMap().getUser((Player)sender).getDisplayName() : Console.NAME;
			sender.sendMessage("[" + senderName + ">" + address + "] " + message);
			if (!EssentialsXMPP.getInstance().sendMessage(address, "[" + senderName + "] " + message))
			{
				sender.sendMessage("§cError sending message.");
			}
		}
	}
}
