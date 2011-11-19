package com.earth2me.essentials.commands;

import com.earth2me.essentials.DescParseTickFormat;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.*;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;


public class Commandtime extends EssentialsCommand
{
	public Commandtime()
	{
		super("time");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		// Which World(s) are we interested in?
		String worldSelector = null;
		if (args.length == 2)
		{
			worldSelector = args[1];
		}
		final Set<World> worlds = getWorlds(server, sender, worldSelector);

		// If no arguments we are reading the time
		if (args.length == 0)
		{
			getWorldsTime(sender, worlds);
			return;
		}

		final User user = ess.getUser(sender);
		if (user != null && !user.isAuthorized("essentials.time.set"))
		{
			user.sendMessage(Util.i18n("timeSetPermission"));
			return;
		}

		// Parse the target time int ticks from args[0]
		long ticks;
		try
		{
			ticks = DescParseTickFormat.parse(args[0]);
		}
		catch (NumberFormatException e)
		{
			throw new NotEnoughArgumentsException();
		}

		setWorldsTime(sender, worlds, ticks);
	}

	/**
	 * Used to get the time and inform
	 */
	private void getWorldsTime(final CommandSender sender, final Collection<World> worlds)
	{
		if (worlds.size() == 1)
		{
			final Iterator<World> iter = worlds.iterator();
			sender.sendMessage(DescParseTickFormat.format(iter.next().getTime()));
			return;
		}

		for (World world : worlds)
		{
			sender.sendMessage(Util.format("timeCurrentWorld", world.getName(), DescParseTickFormat.format(world.getTime())));
		}
	}

	/**
	 * Used to set the time and inform of the change
	 */
	private void setWorldsTime(final CommandSender sender, final Collection<World> worlds, final long ticks)
	{
		// Update the time
		for (World world : worlds)
		{
			long time = world.getTime();
			time -= time % 24000;
			world.setTime(time + 24000 + ticks);
		}

		final StringBuilder output = new StringBuilder();
		for (World world : worlds)
		{
			if (output.length() > 0)
			{
				output.append(", ");
			}

			output.append(world.getName());
		}

		sender.sendMessage(Util.format("timeWorldSet", DescParseTickFormat.format(ticks), output.toString()));
	}

	/**
	 * Used to parse an argument of the type "world(s) selector"
	 */
	private Set<World> getWorlds(final Server server, final CommandSender sender, final String selector) throws Exception
	{
		final Set<World> worlds = new TreeSet<World>(new WorldNameComparator());

		// If there is no selector we want the world the user is currently in. Or all worlds if it isn't a user.
		if (selector == null)
		{
			final User user = ess.getUser(sender);
			if (user == null)
			{
				worlds.addAll(server.getWorlds());
			}
			else
			{
				worlds.add(user.getWorld());
			}
			return worlds;
		}

		// Try to find the world with name = selector
		final World world = server.getWorld(selector);
		if (world != null)
		{
			worlds.add(world);
		}
		// If that fails, Is the argument something like "*" or "all"?
		else if (selector.equalsIgnoreCase("*") || selector.equalsIgnoreCase("all"))
		{
			worlds.addAll(server.getWorlds());
		}
		// We failed to understand the world target...
		else
		{
			throw new Exception(Util.i18n("invalidWorld"));
		}

		return worlds;
	}
}


class WorldNameComparator implements Comparator<World>
{
	@Override
	public int compare(final World a, final World b)
	{
		return a.getName().compareTo(b.getName());
	}
}
