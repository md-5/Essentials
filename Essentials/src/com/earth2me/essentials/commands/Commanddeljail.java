package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.server.ICommandSender;
import org.bukkit.command.CommandSender;


public class Commanddeljail extends EssentialsCommand
{
	@Override
	protected void run(final ICommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		ess.getJails().removeJail(args[0]);
		sender.sendMessage(_("deleteJail", args[0]));
	}
}
