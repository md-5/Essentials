package com.earth2me.essentials.commands;

import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.TNTPrimed;


public class Commandantioch extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length > 0)
		{
			ess.broadcastMessage(user, "...lobbest thou thy Holy Hand Grenade of Antioch towards thy foe,");
			ess.broadcastMessage(user, "who being naughty in My sight, shall snuff it.");
		}		

		final Location loc = LocationUtil.getTarget(user);
		loc.getWorld().spawn(loc, TNTPrimed.class);
	}
}
