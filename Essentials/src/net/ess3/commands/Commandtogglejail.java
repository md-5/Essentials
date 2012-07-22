package net.ess3.commands;

import static net.ess3.I18n._;

import net.ess3.api.IUser;
import net.ess3.permissions.Permissions;
import net.ess3.user.UserData.TimestampType;
import net.ess3.utils.DateUtil;
import lombok.Cleanup;
import net.ess3.api.server.CommandSender;


public class Commandtogglejail extends EssentialsCommand
{
	@Override
	protected void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		@Cleanup
		final IUser player = ess.getUserMap().matchUser(args[0], false, true);
		player.acquireReadLock();

		if (args.length >= 2 && !player.getData().isJailed())
		{
			if (!player.isOnline())
			{
				if (Permissions.TOGGLEJAIL_OFFLINE.isAuthorized(sender))
				{
					sender.sendMessage(_("mayNotJail"));
					return;
				}
			}
			else
			{
				if (Permissions.JAIL_EXEMPT.isAuthorized(player))
				{
					sender.sendMessage(_("mayNotJail"));
					return;
				}
			}
			if (player.isOnline())
			{
				ess.getJails().sendToJail(player, args[1]);
			}
			else
			{
				// Check if jail exists
				ess.getJails().getJail(args[1]);
			}
			player.acquireWriteLock();
			player.getData().setJailed(true);
			player.sendMessage(_("userJailed"));
			player.getData().setJail(args[1]);
			long timeDiff = 0;
			if (args.length > 2)
			{
				final String time = getFinalArg(args, 2);
				timeDiff = DateUtil.parseDateDiff(time, true);
				player.setTimestamp(TimestampType.JAIL, timeDiff);
			}
			sender.sendMessage((timeDiff > 0
								? _("playerJailedFor", player.getName(), DateUtil.formatDateDiff(timeDiff))
								: _("playerJailed", player.getName())));
			return;
		}

		if (args.length >= 2 && player.getData().isJailed() && !args[1].equalsIgnoreCase(player.getData().getJail()))
		{
			sender.sendMessage(_("jailAlreadyIncarcerated", player.getData().getJail()));
			return;
		}

		if (args.length >= 2 && player.getData().isJailed() && args[1].equalsIgnoreCase(player.getData().getJail()))
		{
			final String time = getFinalArg(args, 2);
			final long timeDiff = DateUtil.parseDateDiff(time, true);
			player.acquireWriteLock();
			player.setTimestamp(TimestampType.JAIL, timeDiff);
			sender.sendMessage(_("jailSentenceExtended", DateUtil.formatDateDiff(timeDiff)));
			return;
		}

		if (args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase(player.getData().getJail())))
		{
			if (!player.getData().isJailed())
			{
				throw new NotEnoughArgumentsException();
			}
			player.acquireWriteLock();
			player.getData().setJailed(false);
			player.setTimestamp(TimestampType.JAIL, 0);
			player.sendMessage(_("jailReleasedPlayerNotify"));
			player.getData().setJail(null);
			if (player.isOnline())
			{
				player.getTeleport().back();
			}
			sender.sendMessage(_("jailReleased", player.getName()));
		}
	}
}
