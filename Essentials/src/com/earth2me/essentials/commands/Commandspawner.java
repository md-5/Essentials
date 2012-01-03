package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Mob;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.ISettings;
import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;


public class Commandspawner extends EssentialsCommand
{
	@Override
	protected void run(final IUser user, final String[] args) throws Exception
	{
		if (args.length < 1 || args[0].length() < 2)
		{
			throw new NotEnoughArgumentsException(_("mobsAvailable", Util.joinList(Mob.getMobList())));
		}

		final Location target = Util.getTarget(user);
		if (target == null || target.getBlock().getType() != Material.MOB_SPAWNER)
		{
			throw new Exception(_("mobSpawnTarget"));
		}

		try
		{
			String name = args[0];

			Mob mob = null;
			mob = Mob.fromName(name);
			if (mob == null)
			{
				user.sendMessage(_("invalidMob"));
				return;
			}
			if (!user.isAuthorized("essentials.spawner." + mob.name.toLowerCase()))
			{
				throw new Exception(_("unableToSpawnMob"));
			}
			((CreatureSpawner)target.getBlock().getState()).setCreatureType(mob.getType());
			user.sendMessage(_("setSpawner", mob.name));
		}
		catch (Throwable ex)
		{
			throw new Exception(_("mobSpawnError"), ex);
		}
	}
}
