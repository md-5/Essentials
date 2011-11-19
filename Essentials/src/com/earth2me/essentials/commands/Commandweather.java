package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;


public class Commandweather extends EssentialsCommand
{
	public Commandweather()
	{
		super("weather");
	}
	
	//TODO: Remove duplication

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		final boolean isStorm = args[0].equalsIgnoreCase("storm");
		final World world = user.getWorld();
		if (args.length > 1)
		{

			world.setStorm(isStorm ? true : false);
			world.setWeatherDuration(Integer.parseInt(args[1]) * 20);
			user.sendMessage(isStorm
							 ? Util.format("weatherStormFor", world.getName(), args[1])
							 : Util.format("weatherSunFor", world.getName(), args[1]));
		}
		else
		{
			world.setStorm(isStorm ? true : false);
			user.sendMessage(isStorm
							 ? Util.format("weatherStorm", world.getName())
							 : Util.format("weatherSun", world.getName()));
		}
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2) //running from console means inserting a world arg before other args
		{
			throw new Exception("When running from console, usage is: /" + commandLabel + " <world> <storm/sun> [duration]");
		}

		final boolean isStorm = args[1].equalsIgnoreCase("storm");
		final World world = server.getWorld(args[0]);
		if (world == null)
		{
			throw new Exception("World named " + args[0] + " not found!");
		}
		if (args.length > 2)
		{

			world.setStorm(isStorm ? true : false);
			world.setWeatherDuration(Integer.parseInt(args[2]) * 20);
			sender.sendMessage(isStorm
							   ? Util.format("weatherStormFor", world.getName(), args[2])
							   : Util.format("weatherSunFor", world.getName(), args[2]));
		}
		else
		{
			world.setStorm(isStorm ? true : false);
			sender.sendMessage(isStorm
							   ? Util.format("weatherStorm", world.getName())
							   : Util.format("weatherSun", world.getName()));
		}
	}
}
