package net.ess3;

import static net.ess3.I18n._;
import net.ess3.api.IEssentials;
import net.ess3.api.IWarp;
import net.ess3.api.IWarps;
import net.ess3.api.InvalidNameException;
import net.ess3.api.server.Location;
import net.ess3.commands.WarpNotFoundException;
import net.ess3.settings.WarpHolder;
import net.ess3.storage.StorageObjectMap;
import java.io.File;
import java.util.*;


public class Warps extends StorageObjectMap<IWarp> implements IWarps
{
	public Warps(IEssentials ess)
	{
		super(ess, "warps");
	}

	@Override
	public boolean isEmpty()
	{
		return getKeySize() == 0;
	}

	@Override
	public Collection<String> getList()
	{
		final List<String> names = new ArrayList<String>();
		for (String key : getAllKeys())
		{
			IWarp warp = getObject(key);
			if (warp == null)
			{
				continue;
			}
			warp.acquireReadLock();
			try
			{
				names.add(warp.getData().getName());
			}
			finally
			{
				warp.unlock();
			}
		}
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
		return names;
	}

	@Override
	public Location getWarp(final String name) throws Exception
	{
		IWarp warp = getObject(name);
		if (warp == null)
		{
			throw new WarpNotFoundException(_("warpNotExist"));
		}
		warp.acquireReadLock();
		try
		{
			return warp.getData().getLocation().getStoredLocation();
		}
		finally
		{
			warp.unlock();
		}
	}

	@Override
	public void setWarp(final String name, final Location loc) throws Exception
	{
		setWarp(name, new net.ess3.storage.StoredLocation(loc));
	}
	
	public void setWarp(final String name, final net.ess3.storage.StoredLocation loc) throws Exception
	{
		IWarp warp = getObject(name);
		if (warp == null)
		{
			warp = new WarpHolder(name, ess);
		}
		warp.acquireWriteLock();
		try
		{
			warp.getData().setLocation(loc);
		}
		finally
		{
			warp.unlock();
		}
	}

	@Override
	public void removeWarp(final String name) throws Exception
	{
		removeObject(name);
	}

	@Override
	public File getWarpFile(String name) throws InvalidNameException
	{
		return getStorageFile(name);
	}

	@Override
	public IWarp load(String name) throws Exception
	{
		final IWarp warp = new WarpHolder(name, ess);
		warp.onReload();
		return warp;
	}


	private static class StringIgnoreCase
	{
		private final String string;

		public StringIgnoreCase(String string)
		{
			this.string = string;
		}

		@Override
		public int hashCode()
		{
			return getString().toLowerCase(Locale.ENGLISH).hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof StringIgnoreCase)
			{
				return getString().equalsIgnoreCase(((StringIgnoreCase)o).getString());
			}
			return false;
		}

		public String getString()
		{
			return string;
		}
	}
}
