package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandreloadall extends EssentialsCommand
{
	public Commandreloadall()
	{
		super("reloadall");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		charge(sender);
		server.reload();
		sender.sendMessage("§7Reloaded all plugins.");
	}
}
