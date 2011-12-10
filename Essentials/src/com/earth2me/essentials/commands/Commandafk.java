package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.api.IUser;
import org.bukkit.Server;


public class Commandafk extends EssentialsCommand
{
	public Commandafk()
	{
		super("afk");
	}

	@Override
	public void run(Server server, IUser user, String commandLabel, String[] args) throws Exception
	{
		if (args.length > 0 && user.isAuthorized("essentials.afk.others"))
		{
			IUser afkUser = ess.getUser(ess.getServer().matchPlayer(args[0]));
			if (afkUser != null)
			{
				toggleAfk(afkUser);
			}
		}
		else
		{
			toggleAfk(user);
		}
	}

	private void toggleAfk(IUser user)
	{
		if (!user.toggleAfk())
		{
			//user.sendMessage(_("markedAsNotAway"));
			if (!user.isHidden())
			{
				ess.broadcastMessage(user, _("userIsNotAway", user.getDisplayName()));
			}
			user.updateActivity(false);
		}
		else
		{
			//user.sendMessage(_("markedAsAway"));
			if (!user.isHidden())
			{
				ess.broadcastMessage(user, _("userIsAway", user.getDisplayName()));
			}
		}
	}
}
