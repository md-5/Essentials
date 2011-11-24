package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;


public class Commandgc extends EssentialsCommand
{
	public Commandgc()
	{
		super("gc");
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		sender.sendMessage(_("gcmax", (Runtime.getRuntime().maxMemory() / 1024 / 1024)));
		sender.sendMessage(_("gctotal", (Runtime.getRuntime().totalMemory() / 1024 / 1024)));
		sender.sendMessage(_("gcfree", (Runtime.getRuntime().freeMemory() / 1024 / 1024)));

		for (World w : server.getWorlds())
		{
			sender.sendMessage(
					(w.getEnvironment() == World.Environment.NETHER ? "Nether" : "World") + " \"" + w.getName() + "\": "
					+ w.getLoadedChunks().length + _("gcchunks")
					+ w.getEntities().size() + _("gcentities"));
		}
	}
}
