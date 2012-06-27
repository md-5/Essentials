package net.ess3.commands;

import net.ess3.api.IUser;
import net.ess3.permissions.Permissions;
import static net.ess3.I18n._;

import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;


public class Commandsudo extends EssentialsCommand
{
	@Override
	public void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		final IUser user = getPlayer(args, 0, false);
		final String command = args[1];
		final String[] arguments = new String[args.length - 2];
		if (arguments.length > 0)
		{
			System.arraycopy(args, 2, arguments, 0, args.length - 2);
		}

		if (Permissions.SUDO_EXEMPT.isAuthorized(user))
		{
			throw new Exception(_("sudoExempt"));
		}

		sender.sendMessage(_("sudoRun", user.getDisplayName(), command, getFinalArg(arguments, 0)));

		final PluginCommand execCommand = ess.getServer().getPluginCommand(command);
		if (execCommand != null)
		{
			execCommand.execute(user.getBase(), command, arguments);
		}

	}
}
