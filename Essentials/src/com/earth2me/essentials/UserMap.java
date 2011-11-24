package com.earth2me.essentials;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ConcurrentHashMultiset;
import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class UserMap extends CacheLoader<String, User> implements IConf
{
	private final transient IEssentials ess;
	private final transient Cache<String, User> users = CacheBuilder.newBuilder().softValues().build(this);
	private final transient ConcurrentHashMultiset<String> keys = ConcurrentHashMultiset.create();

	public UserMap(final IEssentials ess)
	{
		super();
		this.ess = ess;
		loadAllUsersAsync(ess);
	}

	private void loadAllUsersAsync(final IEssentials ess)
	{
		ess.scheduleAsyncDelayedTask(new Runnable()
		{
			@Override
			public void run()
			{
				final File userdir = new File(ess.getDataFolder(), "userdata");
				if (!userdir.exists())
				{
					return;
				}
				keys.clear();
				users.invalidateAll();
				for (String string : userdir.list())
				{
					if (!string.endsWith(".yml"))
					{
						continue;
					}
					final String name = string.substring(0, string.length() - 4);
					keys.add(name.toLowerCase(Locale.ENGLISH));
				}
			}
		});
	}

	public boolean userExists(final String name)
	{
		return keys.contains(name.toLowerCase(Locale.ENGLISH));
	}

	public User getUser(final String name) throws NullPointerException
	{
		try
		{
			return users.get(name.toLowerCase(Locale.ENGLISH));
		}
		catch (ExecutionException ex)
		{
			throw new NullPointerException();
		}
	}

	@Override
	public User load(final String name) throws Exception
	{
		for (Player player : ess.getServer().getOnlinePlayers())
		{
			if (player.getName().equalsIgnoreCase(name))
			{
				keys.add(name.toLowerCase(Locale.ENGLISH));
				return new User(player, ess);
			}
		}
		final File userFolder = new File(ess.getDataFolder(), "userdata");
		final File userFile = new File(userFolder, Util.sanitizeFileName(name) + ".yml");
		if (userFile.exists())
		{
			keys.add(name.toLowerCase(Locale.ENGLISH));
			return new User(new OfflinePlayer(name, ess), ess);
		}
		throw new Exception("User not found!");
	}

	@Override
	public void reloadConfig()
	{
		loadAllUsersAsync(ess);
	}

	public void removeUser(final String name)
	{
		keys.remove(name.toLowerCase(Locale.ENGLISH));
		users.invalidate(name.toLowerCase(Locale.ENGLISH));
	}

	public Set<User> getAllUsers()
	{
		final Set<User> userSet = new HashSet<User>();
		for (String name : keys)
		{
			try
			{
				userSet.add(users.get(name));
			}
			catch (ExecutionException ex)
			{
				Bukkit.getLogger().log(Level.INFO, "Failed to load user " + name, ex);
			}
		}
		return userSet;
	}

	public int getUniqueUsers()
	{
		return keys.size();
	}
}
