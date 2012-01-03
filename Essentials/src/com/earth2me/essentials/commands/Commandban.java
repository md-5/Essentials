package com.earth2me.essentials.commands;

import com.earth2me.essentials.Console;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.user.Ban;
import lombok.Cleanup;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandban extends EssentialsCommand
{
	@Override
	public void run(final CommandSender sender, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		@Cleanup
		final IUser user = getPlayer(args, 0, true);
		if (user.getBase() instanceof OfflinePlayer)
		{
			if (sender instanceof Player
				&& !ess.getUser((Player)sender).isAuthorized("essentials.ban.offline"))
			{
				sender.sendMessage(_("banExempt"));
				return;
			}
		}
		else
		{
			if (user.isAuthorized("essentials.ban.exempt"))
			{
				sender.sendMessage(_("banExempt"));
				return;
			}
		}

		user.acquireWriteLock();
		String banReason;
		user.getData().setBan(new Ban());
		if (args.length > 1)
		{
			banReason = getFinalArg(args, 1);
			user.getData().getBan().setReason(banReason);
		}
		else
		{
			banReason = _("defaultBanReason");
		}
		user.setBanned(true);
		user.kickPlayer(banReason);
		final String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : Console.NAME;

		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			final IUser player = ess.getUser(onlinePlayer);
			if (player.isAuthorized("essentials.ban.notify"))
			{
				onlinePlayer.sendMessage(_("playerBanned", senderName, user.getName(), banReason));
			}
		}
	}
}
