package com.earth2me.essentials.commands;

import com.earth2me.essentials.Util;
import com.earth2me.essentials.commands.EssentialsCommand;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandrules extends EssentialsCommand
{
	public Commandrules()
	{
		super("rules");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		charge(sender);
		for (String m : ess.getLines(sender, "rules", Util.i18n("noRules")))
		{
			sender.sendMessage(m);
		}
	}
}
