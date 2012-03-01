package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;


public class Commandsudo extends EssentialsCommand
{
	public Commandsudo()
	{
		super("sudo");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		final User user = getPlayer(server, args, 0, false);
		final String command = args[1];
		final String[] arguments = new String[args.length - 2];
		if (arguments.length > 0)
		{
			System.arraycopy(args, 2, arguments, 0, args.length - 2);
		}

		//TODO: Translate this.
		if (user.isAuthorized("essentials.sudo.exempt"))
		{
			throw new Exception("You cannot sudo this user");
		}

		//TODO: Translate this.
		sender.sendMessage("Forcing " + user.getDisplayName() + " to run: /" + command + " " + getFinalArg(arguments, 0));

		final PluginCommand execCommand = ess.getServer().getPluginCommand(command);
		if (execCommand != null)
		{
			execCommand.execute(user.getBase(), command, arguments);
		}

	}
}
