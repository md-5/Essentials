package net.ess3.commands;

import static net.ess3.I18n._;
import net.ess3.api.IUser;
import net.ess3.permissions.Permissions;
import java.util.Locale;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;


public class Commandgamemode extends EssentialsCommand
{
	@Override
	protected void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		gamemodeOtherPlayers(sender, args[0]);
	}

	@Override
	protected void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length > 0 && !args[0].trim().isEmpty() && Permissions.GAMEMODE_OTHERS.isAuthorized(user))
		{
			gamemodeOtherPlayers(user, args[0]);
			return;
		}

		user.setGameMode(user.getGameMode() == GameMode.SURVIVAL ? GameMode.CREATIVE : GameMode.SURVIVAL);
		user.sendMessage(_("gameMode", _(user.getGameMode().toString().toLowerCase(Locale.ENGLISH)), user.getDisplayName()));
	}

	private void gamemodeOtherPlayers(final CommandSender sender, final String name)
	{
		for (Player matchPlayer : server.matchPlayer(name))
		{
			final IUser player = ess.getUser(matchPlayer);
			if (player.isHidden())
			{
				continue;
			}

			player.setGameMode(player.getGameMode() == GameMode.SURVIVAL ? GameMode.CREATIVE : GameMode.SURVIVAL);
			sender.sendMessage(_("gameMode", _(player.getGameMode().toString().toLowerCase(Locale.ENGLISH)), player.getDisplayName()));
		}
	}
}
