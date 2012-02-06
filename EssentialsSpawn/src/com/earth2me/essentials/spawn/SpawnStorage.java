package com.earth2me.essentials.spawn;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IEssentialsModule;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.settings.Spawns;
import com.earth2me.essentials.storage.AsyncStorageObjectHolder;
import com.earth2me.essentials.storage.Location.WorldNotLoadedException;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventPriority;


public class SpawnStorage extends AsyncStorageObjectHolder<Spawns> implements IEssentialsModule
{
	public SpawnStorage(final IEssentials ess)
	{
		super(ess, Spawns.class);
		onReload();
	}

	@Override
	public File getStorageFile()
	{
		return new File(ess.getDataFolder(), "spawn.yml");
	}

	public void setSpawn(final Location loc, final String group)
	{
		acquireWriteLock();
		try
		{
			if (getData().getSpawns() == null)
			{
				getData().setSpawns(new HashMap<String, com.earth2me.essentials.storage.Location>());
			}
			getData().getSpawns().put(group.toLowerCase(Locale.ENGLISH), new com.earth2me.essentials.storage.Location(loc));
		}
		finally
		{
			unlock();
		}

		if ("default".equalsIgnoreCase(group))
		{
			loc.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		}
	}

	public Location getSpawn(final String group)
	{
		acquireReadLock();
		try
		{
			if (getData().getSpawns() == null || group == null)
			{
				return getWorldSpawn();
			}
			final Map<String, com.earth2me.essentials.storage.Location> spawnMap = getData().getSpawns();
			String groupName = group.toLowerCase(Locale.ENGLISH);
			if (!spawnMap.containsKey(groupName))
			{
				groupName = "default";
			}
			if (!spawnMap.containsKey(groupName))
			{
				return getWorldSpawn();
			}
			try
			{
				return spawnMap.get(groupName).getBukkitLocation();
			}
			catch (WorldNotLoadedException ex)
			{
				return getWorldSpawn();
			}
		}
		finally
		{
			unlock();
		}
	}

	private Location getWorldSpawn()
	{
		for (World world : ess.getServer().getWorlds())
		{
			if (world.getEnvironment() != World.Environment.NORMAL)
			{
				continue;
			}
			return world.getSpawnLocation();
		}
		return ess.getServer().getWorlds().get(0).getSpawnLocation();
	}

	public EventPriority getRespawnPriority()
	{
		acquireReadLock();
		try
		{
			for (EventPriority priority : EventPriority.values())
			{
				if (priority.toString().equalsIgnoreCase(getData().getRespawnPriority()))
				{
					return priority;
				}
			}
			return EventPriority.NORMAL;
		}
		finally
		{
			unlock();
		}
	}

	public Location getNewbieSpawn()
	{
		acquireReadLock();
		try
		{
			if (getData().getNewbieSpawn() == null || getData().getNewbieSpawn().isEmpty()
				|| getData().getNewbieSpawn().equalsIgnoreCase("none"))
			{
				return null;
			}
			return getSpawn(getData().getNewbieSpawn());
		}
		finally
		{
			unlock();
		}
	}

	public boolean getAnnounceNewPlayers()
	{
		acquireReadLock();
		try
		{
			return getData().getNewPlayerAnnouncement() != null && !getData().getNewPlayerAnnouncement().isEmpty();
		}
		finally
		{
			unlock();
		}
	}

	public String getAnnounceNewPlayerFormat(IUser user)
	{
		acquireReadLock();
		try
		{
			return getData().getNewPlayerAnnouncement().replace('&', '§').replace("§§", "&").replace("{PLAYER}", user.getDisplayName()).replace("{DISPLAYNAME}", user.getDisplayName()).replace("{GROUP}", ess.getGroups().getMainGroup(user)).replace("{USERNAME}", user.getName()).replace("{ADDRESS}", user.getAddress().toString());
		}
		finally
		{
			unlock();
		}
	}
}
