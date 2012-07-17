package net.ess3.commands;

import static net.ess3.I18n._;
import net.ess3.api.IEssentials;
import net.ess3.api.IEssentialsModule;
import net.ess3.api.IUser;
import net.ess3.api.server.CommandSender;
import net.ess3.api.server.IServer;
import net.ess3.api.server.Player;
import net.ess3.economy.Trade;
import net.ess3.permissions.AbstractSuperpermsPermission;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.command.Command;


public abstract class EssentialsCommand extends AbstractSuperpermsPermission implements IEssentialsCommand
{
	protected transient String commandName;
	protected transient IEssentials ess;
	protected transient IEssentialsModule module;
	protected transient IServer server;
	protected transient Logger logger;
	private transient String permission;

	public void init(final IEssentials ess, final String commandName)
	{
		this.ess = ess;
		this.logger = ess.getLogger();
		this.server = ess.getServer();
		this.commandName = commandName;
		this.permission = "essentials." + commandName;
	}

	@Override
	public void setEssentialsModule(final IEssentialsModule module)
	{
		this.module = module;
	}

	protected IUser getPlayer(final String[] args, final int pos) throws NoSuchFieldException, NotEnoughArgumentsException
	{
		return getPlayer(args, pos, false);
	}

	protected IUser getPlayer(final String[] args, final int pos, final boolean getOffline) throws NoSuchFieldException, NotEnoughArgumentsException
	{
		if (args.length <= pos)
		{
			throw new NotEnoughArgumentsException();
		}
		if (args[pos].isEmpty())
		{
			throw new NoSuchFieldException(_("playerNotFound"));
		}
		final IUser user = ess.getUser(args[pos]);
		if (user != null)
		{
			if (!getOffline && (!user.isOnline() || user.isHidden()))
			{
				throw new NoSuchFieldException(_("playerNotFound"));
			}
			return user;
		}
		final List<Player> matches = server.matchPlayer(args[pos]);

		if (!matches.isEmpty())
		{
			for (Player player : matches)
			{
				final IUser userMatch = player.getUser();
				if (userMatch.getDisplayName().startsWith(args[pos]) && (getOffline || !userMatch.isHidden()))
				{
					return userMatch;
				}
			}
			final IUser userMatch = matches.get(0).getUser();
			if (getOffline || !userMatch.isHidden())
			{
				return userMatch;
			}
		}
		throw new NoSuchFieldException(_("playerNotFound"));
	}

	@Override
	public final void run(final IUser user, final Command cmd, final String commandLabel, final String[] args) throws Exception
	{
		final Trade charge = new Trade(commandName, ess);
		charge.isAffordableFor(user);
		run(user, commandLabel, args);
		charge.charge(user);
	}

	protected void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		run((CommandSender)user, commandLabel, args);
	}

	@Override
	public final void run(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) throws Exception
	{
		run(sender, commandLabel, args);
	}

	protected void run(final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		throw new Exception(_("onlyPlayers", commandName));
	}

	public static String getFinalArg(final String[] args, final int start)
	{
		final StringBuilder bldr = new StringBuilder();
		for (int i = start; i < args.length; i++)
		{
			if (i != start)
			{
				bldr.append(" ");
			}
			bldr.append(args[i]);
		}
		return bldr.toString();
	}

	@Override
	public String getPermissionName()
	{
		return permission;
	}
}
