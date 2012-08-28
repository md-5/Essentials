package net.ess3.commands;

import java.util.Locale;
import static net.ess3.I18n._;
import net.ess3.api.IUser;
import net.ess3.bukkit.LivingEntities;
import net.ess3.economy.Trade;
import net.ess3.permissions.SpawnerPermissions;
import net.ess3.utils.LocationUtil;
import net.ess3.utils.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;


public class Commandspawner extends EssentialsCommand
{
	@Override
	protected void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1 || args[0].length() < 2)
		{
			throw new NotEnoughArgumentsException(_("mobsAvailable", Util.joinList(LivingEntities.getLivingEntityList())));
		}

		final Location target = LocationUtil.getTarget(user.getPlayer());
		if (target == null || target.getBlock().getType() != Material.MOB_SPAWNER)
		{
			throw new Exception(_("mobSpawnTarget"));
		}

		try
		{
			String name = args[0];

			EntityType mob = null;
			mob = LivingEntities.fromName(name);
			if (mob == null)
			{
				user.sendMessage(_("invalidMob"));
				return;
			}
			if (!SpawnerPermissions.getPermission(mob.getName()).isAuthorized(user))
			{
				throw new Exception(_("unableToSpawnMob"));
			}
			final Trade charge = new Trade("spawner-" + mob.getName().toLowerCase(Locale.ENGLISH), ess);
			charge.isAffordableFor(user);
			((CreatureSpawner)target.getBlock().getState()).setSpawnedType(mob);
			charge.charge(user);
			user.sendMessage(_("setSpawner", mob.getName()));
		}
		catch (Throwable ex)
		{
			throw new Exception(_("mobSpawnError"), ex);
		}
	}
}
