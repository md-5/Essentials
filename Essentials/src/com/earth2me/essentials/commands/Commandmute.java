package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.utils.Util;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.api.server.ICommandSender;
import com.earth2me.essentials.permissions.Permissions;
import com.earth2me.essentials.user.UserData.TimestampType;
import com.earth2me.essentials.utils.DateUtil;
import lombok.Cleanup;
import org.bukkit.command.CommandSender;


public class Commandmute extends EssentialsCommand
{
	@Override
	protected void run(final ICommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		@Cleanup
		final IUser player = getPlayer(args, 0, true);
		player.acquireReadLock();
		if (!player.getData().isMuted() && Permissions.MUTE_EXEMPT.isAuthorized(player))
		{
			throw new Exception(_("muteExempt"));
		}
		long muteTimestamp = 0;
		if (args.length > 1)
		{
			String time = getFinalArg(args, 1);
			muteTimestamp = DateUtil.parseDateDiff(time, true);
		}
		player.setTimestamp(TimestampType.MUTE, muteTimestamp);
		final boolean muted = player.toggleMuted();
		sender.sendMessage(
				muted
				? (muteTimestamp > 0
				   ? _("mutedPlayerFor", player.getDisplayName(), DateUtil.formatDateDiff(muteTimestamp))
				   : _("mutedPlayer", player.getDisplayName()))
				: _("unmutedPlayer", player.getDisplayName()));
		player.sendMessage(
				muted
				? (muteTimestamp > 0
				   ? _("playerMutedFor", DateUtil.formatDateDiff(muteTimestamp))
				   : _("playerMuted"))
				: _("playerUnmuted"));
	}
}
