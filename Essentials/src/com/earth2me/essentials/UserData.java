package com.earth2me.essentials;

import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public abstract class UserData extends PlayerExtension implements IConf
{
	private final EssentialsConf config;
	private static final Logger logger = Logger.getLogger("Minecraft");

	protected UserData(Player base, IEssentials ess)
	{
		super(base, ess);
		File folder = new File(ess.getDataFolder(), "userdata");
		if (!folder.exists())
		{
			folder.mkdirs();
		}
		config = new EssentialsConf(new File(folder, Util.sanitizeFileName(base.getName()) + ".yml"));
		reloadConfig();
	}

	public final void reloadConfig()
	{
		config.load();
		money = _getMoney();
		unlimited = _getUnlimited();
		powertools = _getPowertools();
		homes = _getHomes();
		lastLocation = _getLastLocation();
		lastTeleportTimestamp = _getLastTeleportTimestamp();
		lastHealTimestamp = _getLastHealTimestamp();
		jail = _getJail();
		mails = _getMails();
		savedInventory = _getSavedInventory();
		teleportEnabled = getTeleportEnabled();
		ignoredPlayers = getIgnoredPlayers();
		godmode = getGodModeEnabled();
		muted = getMuted();
		muteTimeout = _getMuteTimeout();
		jailed = getJailed();
		jailTimeout = _getJailTimeout();
		lastLogin = _getLastLogin();
		lastLogout = _getLastLogout();
		afk = getAfk();
		newplayer = getNew();
		geolocation = _getGeoLocation();
		isSocialSpyEnabled = _isSocialSpyEnabled();
		isNPC = _isNPC();
		arePowerToolsEnabled = _arePowerToolsEnabled();
	}
	private double money;

	private double _getMoney()
	{
		double money = ess.getSettings().getStartingBalance();
		if (config.hasProperty("money"))
		{
			money = config.getDouble("money", money);
		}
		if (Math.abs(money) > ess.getSettings().getMaxMoney())
		{
			money = money < 0 ? -ess.getSettings().getMaxMoney() : ess.getSettings().getMaxMoney();
		}
		return money;
	}

	public double getMoney()
	{
		return money;
	}

	public void setMoney(double value)
	{
		money = value;
		if (Math.abs(money) > ess.getSettings().getMaxMoney())
		{
			money = money < 0 ? -ess.getSettings().getMaxMoney() : ess.getSettings().getMaxMoney();
		}
		config.setProperty("money", value);
		config.save();
	}
	private Map<String, Object> homes;

	private Map<String, Object> _getHomes()
	{
		Object o = config.getProperty("homes");

		if (o instanceof Map)
		{
			return (Map<String, Object>)o;
		}
		else
		{
			return new HashMap<String, Object>();
		}

	}

	public Location getHome(String name) throws Exception
	{
		Location loc = config.getLocation("homes." + name, getServer());
		if (loc == null)
		{
			try
			{
				loc = config.getLocation("homes." + getHomes().get(Integer.parseInt(name) - 1), getServer());
			}
			catch (IndexOutOfBoundsException e)
			{
				return null;
			}
			catch (NumberFormatException e)
			{
				return null;
			}
		}

		return loc;
	}

	public Location getHome(Location world) throws Exception
	{
		Location loc;
		for (String home : getHomes())
		{
			loc = config.getLocation("homes." + home, getServer());
			if (world.getWorld() == loc.getWorld())
			{
				return loc;
			}

		}
		loc = config.getLocation("homes." + getHomes().get(0), getServer());
		return loc;
	}

	public List<String> getHomes()
	{
		List<String> list = new ArrayList(homes.keySet());
		return list;

	}

	public void setHome(String name, Location loc)
	{
		homes.put(name, loc);
		config.setProperty("homes." + name, loc);
		config.save();
	}

	public void delHome(String name) throws Exception
	{
		if (getHome(name) != null)
		{
			homes.remove(name);
			config.removeProperty("homes." + name);
			config.save();
		}
		else
		{
			//TODO: move this message to messages file
			throw new Exception("Home " + name + " doesn't exist");
		}
	}

	public boolean hasHome()
	{
		if (config.hasProperty("home"))
		{
			return true;
		}
		return false;
	}

	public String getNickname()
	{
		return config.getString("nickname");
	}

	public void setNickname(String nick)
	{
		config.setProperty("nickname", nick);
		config.save();
	}
	private List<Integer> unlimited;

	private List<Integer> _getUnlimited()
	{
		return config.getIntList("unlimited", new ArrayList<Integer>());
	}

	public List<Integer> getUnlimited()
	{
		return unlimited;
	}

	public boolean hasUnlimited(ItemStack stack)
	{
		return unlimited.contains(stack.getTypeId());
	}

	public void setUnlimited(ItemStack stack, boolean state)
	{
		if (unlimited.contains(stack.getTypeId()))
		{
			unlimited.remove(Integer.valueOf(stack.getTypeId()));
		}
		if (state)
		{
			unlimited.add(stack.getTypeId());
		}
		config.setProperty("unlimited", unlimited);
		config.save();
	}
	private Map<Integer, Object> powertools;

	@SuppressWarnings("unchecked")
	private Map<Integer, Object> _getPowertools()
	{
		Object o = config.getProperty("powertools");

		if (o instanceof Map)
		{
			return (Map<Integer, Object>)o;
		}
		else
		{
			return new HashMap<Integer, Object>();
		}

	}

	public void clearAllPowertools() {
		powertools.clear();
		config.setProperty("powertools", powertools);
		config.save();
	}
	
	public List<String> getPowertool(ItemStack stack)
	{
		return (List<String>)powertools.get(stack.getTypeId());
	}

	public void setPowertool(ItemStack stack, List<String> commandList)
	{
		if (commandList == null || commandList.isEmpty())
		{
			powertools.remove(stack.getTypeId());
		}
		else
		{
			powertools.put(stack.getTypeId(), commandList);
		}
		config.setProperty("powertools", powertools);
		config.save();
	}
	
	public boolean hasPowerTools()
	{
		return powertools.size() > 0;
	}
	
	private Location lastLocation;

	private Location _getLastLocation()
	{
		try
		{
			return config.getLocation("lastlocation", getServer());
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public Location getLastLocation()
	{
		return lastLocation;
	}

	public void setLastLocation(Location loc)
	{
		lastLocation = loc;
		config.setProperty("lastlocation", loc);
		config.save();
	}
	private long lastTeleportTimestamp;

	private long _getLastTeleportTimestamp()
	{
		return config.getLong("timestamps.lastteleport", 0);
	}

	public long getLastTeleportTimestamp()
	{
		return lastTeleportTimestamp;
	}

	public void setLastTeleportTimestamp(long time)
	{
		lastTeleportTimestamp = time;
		config.setProperty("timestamps.lastteleport", time);
		config.save();
	}
	private long lastHealTimestamp;

	private long _getLastHealTimestamp()
	{
		return config.getLong("timestamps.lastheal", 0);
	}

	public long getLastHealTimestamp()
	{
		return lastHealTimestamp;
	}

	public void setLastHealTimestamp(long time)
	{
		lastHealTimestamp = time;
		config.setProperty("timestamps.lastheal", time);
		config.save();
	}
	private String jail;

	private String _getJail()
	{
		return config.getString("jail");
	}

	public String getJail()
	{
		return jail;
	}

	public void setJail(String jail)
	{
		if (jail == null || jail.isEmpty())
		{
			this.jail = null;
			config.removeProperty("jail");
		}
		else
		{
			this.jail = jail;
			config.setProperty("jail", jail);
		}
		config.save();
	}
	private List<String> mails;

	private List<String> _getMails()
	{
		return config.getStringList("mail", new ArrayList<String>());
	}

	public List<String> getMails()
	{
		return mails;
	}

	public void setMails(List<String> mails)
	{
		if (mails == null)
		{
			config.removeProperty("mail");
			mails = _getMails();
		}
		else
		{
			config.setProperty("mail", mails);
		}
		this.mails = mails;
		config.save();
	}

	public void addMail(String mail)
	{
		mails.add(mail);
		setMails(mails);
	}
	private ItemStack[] savedInventory;

	public ItemStack[] getSavedInventory()
	{
		return savedInventory;
	}

	private ItemStack[] _getSavedInventory()
	{
		int size = config.getInt("inventory.size", 0);
		if (size < 1 || (getInventory() != null && size > getInventory().getSize()))
		{
			return null;
		}
		ItemStack[] is = new ItemStack[size];
		for (int i = 0; i < size; i++)
		{
			is[i] = config.getItemStack("inventory." + i);
		}
		return is;
	}

	public void setSavedInventory(ItemStack[] is)
	{
		if (is == null || is.length == 0)
		{
			savedInventory = null;
			config.removeProperty("inventory");
		}
		else
		{
			savedInventory = is;
			config.setProperty("inventory.size", is.length);
			for (int i = 0; i < is.length; i++)
			{
				if (is[i] == null || is[i].getType() == Material.AIR)
				{
					continue;
				}
				config.setProperty("inventory." + i, is[i]);
			}
		}
		config.save();
	}
	private boolean teleportEnabled;

	private boolean getTeleportEnabled()
	{
		return config.getBoolean("teleportenabled", true);
	}

	public boolean isTeleportEnabled()
	{
		return teleportEnabled;
	}

	public void setTeleportEnabled(boolean set)
	{
		teleportEnabled = set;
		config.setProperty("teleportenabled", set);
		config.save();
	}

	public boolean toggleTeleportEnabled()
	{
		boolean ret = !isTeleportEnabled();
		setTeleportEnabled(ret);
		return ret;
	}

	public boolean toggleSocialSpy()
	{
		boolean ret = !isSocialSpyEnabled();
		setSocialSpyEnabled(ret);
		return ret;
	}
	private List<String> ignoredPlayers;

	public List<String> getIgnoredPlayers()
	{
		return config.getStringList("ignore", new ArrayList<String>());
	}

	public void setIgnoredPlayers(List<String> players)
	{
		if (players == null || players.isEmpty())
		{
			ignoredPlayers = new ArrayList<String>();
			config.removeProperty("ignore");
		}
		else
		{
			ignoredPlayers = players;
			config.setProperty("ignore", players);
		}
		config.save();
	}

	public boolean isIgnoredPlayer(String name)
	{
		return ignoredPlayers.contains(name.toLowerCase());
	}

	public void setIgnoredPlayer(String name, boolean set)
	{
		if (set)
		{
			ignoredPlayers.add(name.toLowerCase());
		}
		else
		{
			ignoredPlayers.remove(name.toLowerCase());
		}
		setIgnoredPlayers(ignoredPlayers);
	}
	private boolean godmode;

	private boolean getGodModeEnabled()
	{
		return config.getBoolean("godmode", false);
	}

	public boolean isGodModeEnabled()
	{
		return godmode;
	}

	public void setGodModeEnabled(boolean set)
	{
		godmode = set;
		config.setProperty("godmode", set);
		config.save();
	}

	public boolean toggleGodModeEnabled()
	{
		boolean ret = !isGodModeEnabled();
		setGodModeEnabled(ret);
		return ret;
	}
	private boolean muted;

	private boolean getMuted()
	{
		return config.getBoolean("muted", false);
	}

	public boolean isMuted()
	{
		return muted;
	}

	public void setMuted(boolean set)
	{
		muted = set;
		config.setProperty("muted", set);
		config.save();
	}

	public boolean toggleMuted()
	{
		boolean ret = !isMuted();
		setMuted(ret);
		return ret;
	}
	private long muteTimeout;

	private long _getMuteTimeout()
	{
		return config.getLong("timestamps.mute", 0);
	}

	public long getMuteTimeout()
	{
		return muteTimeout;
	}

	public void setMuteTimeout(long time)
	{
		muteTimeout = time;
		config.setProperty("timestamps.mute", time);
		config.save();
	}
	private boolean jailed;

	private boolean getJailed()
	{
		return config.getBoolean("jailed", false);
	}

	public boolean isJailed()
	{
		return jailed;
	}

	public void setJailed(boolean set)
	{
		jailed = set;
		config.setProperty("jailed", set);
		config.save();
	}

	public boolean toggleJailed()
	{
		boolean ret = !isJailed();
		setJailed(ret);
		return ret;
	}
	private long jailTimeout;

	private long _getJailTimeout()
	{
		return config.getLong("timestamps.jail", 0);
	}

	public long getJailTimeout()
	{
		return jailTimeout;
	}

	public void setJailTimeout(long time)
	{
		jailTimeout = time;
		config.setProperty("timestamps.jail", time);
		config.save();
	}

	public String getBanReason()
	{
		return config.getString("ban.reason");
	}

	public void setBanReason(String reason)
	{
		config.setProperty("ban.reason", reason);
		config.save();
	}

	public long getBanTimeout()
	{
		return config.getLong("ban.timeout", 0);
	}

	public void setBanTimeout(long time)
	{
		config.setProperty("ban.timeout", time);
		config.save();
	}
	private long lastLogin;

	private long _getLastLogin()
	{
		return config.getLong("timestamps.login", 0);
	}

	public long getLastLogin()
	{
		return lastLogin;
	}

	public void setLastLogin(long time)
	{
		lastLogin = time;
		config.setProperty("timestamps.login", time);
		config.save();
	}
	private long lastLogout;

	private long _getLastLogout()
	{
		return config.getLong("timestamps.logout", 0);
	}

	public long getLastLogout()
	{
		return lastLogout;
	}

	public void setLastLogout(long time)
	{
		lastLogout = time;
		config.setProperty("timestamps.logout", time);
		config.save();
	}
	private boolean afk;

	private boolean getAfk()
	{
		return config.getBoolean("afk", false);
	}

	public boolean isAfk()
	{
		return afk;
	}

	public void setAfk(boolean set)
	{
		afk = set;
		config.setProperty("afk", set);
		config.save();
	}

	public boolean toggleAfk()
	{
		boolean ret = !isAfk();
		setAfk(ret);
		return ret;
	}
	private boolean newplayer;

	private boolean getNew()
	{
		return config.getBoolean("newplayer", true);
	}

	public boolean isNew()
	{
		return newplayer;
	}

	public void setNew(boolean set)
	{
		newplayer = set;
		config.setProperty("newplayer", set);
		config.save();
	}
	private String geolocation;

	private String _getGeoLocation()
	{
		return config.getString("geolocation");
	}

	public String getGeoLocation()
	{
		return geolocation;
	}

	public void setGeoLocation(String geolocation)
	{
		if (geolocation == null || geolocation.isEmpty())
		{
			this.geolocation = null;
			config.removeProperty("geolocation");
		}
		else
		{
			this.geolocation = geolocation;
			config.setProperty("geolocation", geolocation);
		}
		config.save();
	}
	private boolean isSocialSpyEnabled;

	private boolean _isSocialSpyEnabled()
	{
		return config.getBoolean("socialspy", false);
	}

	public boolean isSocialSpyEnabled()
	{
		return isSocialSpyEnabled;
	}

	public void setSocialSpyEnabled(boolean status)
	{
		isSocialSpyEnabled = status;
		config.setProperty("socialspy", status);
		config.save();
	}
	private boolean isNPC;

	private boolean _isNPC()
	{
		return config.getBoolean("npc", false);
	}

	public boolean isNPC()
	{
		return isNPC;
	}

	public void setNPC(boolean set)
	{
		isNPC = set;
		config.setProperty("npc", set);
		config.save();
	}
	
	private boolean arePowerToolsEnabled;
	
	public boolean arePowerToolsEnabled()
	{
		return arePowerToolsEnabled;
	}
	
	public void setPowerToolsEnabled(boolean set)
	{
		arePowerToolsEnabled = set;
		config.setProperty("powertoolsenabled", set);
		config.save();
	}
	
	public boolean togglePowerToolsEnabled()
	{
		boolean ret = !arePowerToolsEnabled();
		setPowerToolsEnabled(ret);
		return ret;
	}
	
	private boolean _arePowerToolsEnabled()
	{
		return config.getBoolean("powertoolsenabled", true);
	}
	
}
