package net.ess3.commands;

import net.ess3.Console;
import static net.ess3.I18n._;
import net.ess3.utils.Util;
import net.ess3.api.IReplyTo;
import net.ess3.api.IUser;
import net.ess3.permissions.Permissions;
import java.util.List;
import lombok.Cleanup;
import net.ess3.api.server.CommandSender;
import net.ess3.api.server.Player;



public class Commandmsg extends EssentialsCommand
{
	@Override
	protected void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2 || args[0].trim().length() < 3 || args[1].trim().isEmpty())
		{
			throw new NotEnoughArgumentsException();
		}

		String message = getFinalArg(args, 1);
		if (sender instanceof Player)
		{
			@Cleanup
			IUser user = player.getUser((Player)sender);
			user.acquireReadLock();
			if (user.getData().isMuted())
			{
				throw new Exception(_("voiceSilenced"));
			}
			if (Permissions.MSG_COLOR.isAuthorized(user))
			{
				message = Util.replaceFormat(message);
			}
			else
			{
				message = Util.stripColor(message);
			}
		}
		else
		{
			message = Util.replaceFormat(message);
		}

		final String translatedMe = _("me");

		final IReplyTo replyTo = sender instanceof Player ? player.getUser((Player)sender) : Console.getConsoleReplyTo();
		final String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : Console.NAME;

		if (args[0].equalsIgnoreCase(Console.NAME))
		{
			sender.sendMessage(_("msgFormat", translatedMe, Console.NAME, message));
			CommandSender cs = server.getConsoleSender();
			cs.sendMessage(_("msgFormat", senderName, translatedMe, message));
			replyTo.setReplyTo(cs);
			Console.getConsoleReplyTo().setReplyTo(sender);
			return;
		}

		final List<Player> matchedPlayers = server.matchPlayer(args[0]);

		if (matchedPlayers.isEmpty())
		{
			throw new Exception(_("playerNotFound"));
		}

		int i = 0;
		for (Player matchedPlayer : matchedPlayers)
		{
			final IUser u = player.getUser(matchedPlayer);
			if (u.isHidden())
			{
				i++;
			}
		}
		if (i == matchedPlayers.size())
		{
			throw new Exception(_("playerNotFound"));
		}

		for (Player matchedPlayer : matchedPlayers)
		{
			sender.sendMessage(_("msgFormat", translatedMe, matchedPlayer.getDisplayName(), message));
			final IUser matchedUser = player.getUser(matchedPlayer);
			if (sender instanceof Player && (matchedUser.isIgnoringPlayer(player.getUser((Player)sender)) || matchedUser.isHidden()))
			{
				continue;
			}
			matchedPlayer.sendMessage(_("msgFormat", senderName, translatedMe, message));
			replyTo.setReplyTo(player.getUser(matchedPlayer));
			player.getUser(matchedPlayer).setReplyTo(sender);
		}
	}
}
