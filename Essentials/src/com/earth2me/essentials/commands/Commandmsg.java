package com.earth2me.essentials.commands;

import com.earth2me.essentials.Console;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IReplyTo;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandmsg extends EssentialsCommand
{
	public Commandmsg()
	{
		super("msg");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2 || args[0].trim().isEmpty() || args[1].trim().isEmpty())
		{
			throw new NotEnoughArgumentsException();
		}

		if (sender instanceof Player)
		{
			User user = ess.getUser(sender);
			if (user.isMuted())
			{
				throw new Exception(_("voiceSilenced"));
			}
		}

		final String message = Util.stripColor(getFinalArg(args, 1));
		final String translatedMe = _("me");

		final IReplyTo replyTo = sender instanceof Player ? ess.getUser((Player)sender) : Console.getConsoleReplyTo();
		final String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : Console.NAME;

		if (args[0].equalsIgnoreCase(Console.NAME))
		{
			sender.sendMessage(_("msgFormat", translatedMe, Console.NAME, message));
			CommandSender cs = Console.getCommandSender(server);
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
			final User u = ess.getUser(matchedPlayer);
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
			final User matchedUser = ess.getUser(matchedPlayer);
			if (sender instanceof Player && (matchedUser.isIgnoredPlayer(((Player)sender).getName()) || matchedUser.isHidden()))
			{
				continue;
			}
			matchedPlayer.sendMessage(_("msgFormat", senderName, translatedMe, message));
			replyTo.setReplyTo(ess.getUser(matchedPlayer));
			ess.getUser(matchedPlayer).setReplyTo(sender);
		}
	}
}
