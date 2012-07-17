package net.ess3.commands;

import java.util.Locale;
import lombok.Cleanup;
import static net.ess3.I18n._;
import net.ess3.api.ISettings;
import net.ess3.api.IUser;
import net.ess3.api.server.CommandSender;
import net.ess3.api.server.Player;
import net.ess3.api.server.IServer;
import net.ess3.permissions.Permissions;
import net.ess3.utils.Util;


public class Commandnick extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		@Cleanup
		final ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		if (!settings.getData().getChat().getChangeDisplayname())
		{
			throw new Exception(_("nickDisplayName"));
		}
		if (args.length > 1)
		{
			if (!Permissions.NICK_OTHERS.isAuthorized(user))
			{
				throw new Exception(_("nickOthersPermission"));
			}
			setNickname(getPlayer(args, 0), formatNickname(user, args[1]));
			user.sendMessage(_("nickChanged"));
			return;
		}
		setNickname(user, formatNickname(user, args[0]));
	}

	@Override
	protected void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}
		@Cleanup
		final ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		if (!settings.getData().getChat().getChangeDisplayname())
		{
			throw new Exception(_("nickDisplayName"));
		}
		if ((args[0].equalsIgnoreCase("*") || args[0].equalsIgnoreCase("all")) && args[1].equalsIgnoreCase("off"))
		{
			resetAllNicknames(server);
		}
		else
		{
			setNickname(getPlayer(args, 0), formatNickname(null, args[1]));
		}
		sender.sendMessage(_("nickChanged"));
	}

	private String formatNickname(final IUser user, final String nick)
	{
		if (user == null || Permissions.NICK_COLOR.isAuthorized(user))
		{
			return Util.replaceFormat(nick);
		}
		else
		{
			return Util.formatString(user, "essentials.nick", nick);
		}
	}

	private void resetAllNicknames(final Server server)
	{
		for (Player player : server.getOnlinePlayers())
		{
			try
			{
				setNickname(player.getUser(player), "off");
			}
			catch (Exception ex)
			{
			}
		}
	}

	private void setNickname(final IUser target, final String nick) throws Exception
	{
		if (!nick.matches("^[a-zA-Z_0-9\u00a7]+$"))
		{
			throw new Exception(_("nickNamesAlpha"));
		}
		else if ("off".equalsIgnoreCase(nick) || target.getName().equalsIgnoreCase(nick))
		{
			target.getData().setNickname(null);
			target.updateDisplayName();
			target.sendMessage(_("nickNoMore"));
		}
		else
		{
			for (Player p : server.getOnlinePlayers())
			{
				if (target.getBase() == p)
				{
					continue;
				}
				String dn = p.getDisplayName().toLowerCase(Locale.ENGLISH);
				String n = p.getName().toLowerCase(Locale.ENGLISH);
				String nk = nick.toLowerCase(Locale.ENGLISH);
				if (nk.equals(dn) || nk.equals(n))
				{
					throw new Exception(_("nickInUse"));
				}
			}

			target.getData().setNickname(nick);
			target.updateDisplayName();
			target.sendMessage(_("nickSet", target.getDisplayName() + "§7."));
		}
	}
}
