package net.ess3.commands;

import static net.ess3.I18n._;
import net.ess3.api.IUser;


public class Commandignore extends EssentialsCommand
{
	@Override
	protected void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		IUser player = ess.getUserMap().matchUser(args[0], false, false);
		
		user.acquireWriteLock();
		if (user.isIgnoringPlayer(player))
		{
			user.setIgnoredPlayer(player, false);
			user.sendMessage(_("unignorePlayer", player.getName()));
		}
		else
		{
			user.setIgnoredPlayer(player, true);
			user.sendMessage(_("ignorePlayer", player.getName()));
		}
	}
}
