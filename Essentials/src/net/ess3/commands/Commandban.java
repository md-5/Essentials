package net.ess3.commands;

import lombok.Cleanup;
import net.ess3.Console;
import static net.ess3.I18n._;
import net.ess3.api.IUser;
import net.ess3.permissions.Permissions;
import net.ess3.user.Ban;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandban extends EssentialsCommand
{
	@Override
	protected void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		@Cleanup
		final IUser user = ess.getUserMap().matchUser(args[0], false, true);
		if (!user.isOnline())
		{
			if (sender instanceof Player && Permissions.BAN_OFFLINE.isAuthorized(user))
			{
				sender.sendMessage(_("banExempt"));
				return;
			}
		}
		else
		{
			if (Permissions.BAN_EXEMPT.isAuthorized(sender))
			{
				sender.sendMessage(_("banExempt"));
				return;
			}
		}

		user.acquireWriteLock();
		final String senderName = sender instanceof Player ? ((Player)sender).getDisplayName() : Console.NAME;
		String banReason;
		user.getData().setBan(new Ban());
		if (args.length > 1)
		{
			
			banReason = _("banFormat", getFinalArg(args, 1), senderName);
			user.getData().getBan().setReason(banReason);
		}
		else
		{
			banReason = _("banFormat", _("defaultBanReason"), senderName);
			user.getData().getBan().setReason("");
		}
		
		user.setBanned(true);
		user.getPlayer().kickPlayer(banReason);
		for (Player player : server.getOnlinePlayers())
		{
			if (Permissions.BAN_NOTIFY.isAuthorized(player))
			{
				player.sendMessage(_("playerBanned", senderName, user.getName(), banReason));
			}
		}
	}
}
