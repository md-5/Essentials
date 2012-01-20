package com.earth2me.essentials.protect;

import com.earth2me.essentials.protect.data.IProtectedBlock;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsProtect extends JavaPlugin implements IProtect
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private static com.mchange.v2.log.MLogger C3P0logger;
	private final transient Map<ProtectConfig, Boolean> settingsBoolean = new EnumMap<ProtectConfig, Boolean>(ProtectConfig.class);
	private final transient Map<ProtectConfig, String> settingsString = new EnumMap<ProtectConfig, String>(ProtectConfig.class);
	private final transient Map<ProtectConfig, List<Integer>> settingsList = new EnumMap<ProtectConfig, List<Integer>>(ProtectConfig.class);
	private transient IProtectedBlock storage = null;
	private transient EssentialsConnect ess = null;

	@Override
	public void onLoad()
	{
		C3P0logger = com.mchange.v2.log.MLog.getLogger(com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource.class);
		C3P0logger.setFilter(new Filter()
		{
			public boolean isLoggable(LogRecord lr)
			{
				return lr.getLevel() != Level.INFO;
			}
		});
	}

	public void onEnable()
	{
		final PluginManager pm = this.getServer().getPluginManager();
		final Plugin essPlugin = pm.getPlugin("Essentials");
		if (essPlugin == null || !essPlugin.isEnabled())
		{
			enableEmergencyMode(pm);
			return;
		}
		ess = new EssentialsConnect(essPlugin, this);

		final EssentialsProtectPlayerListener playerListener = new EssentialsProtectPlayerListener(this);
		pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Low, this);

		final EssentialsProtectBlockListener blockListener = new EssentialsProtectBlockListener(this);
		pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_FROMTO, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_IGNITE, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_BURN, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_PISTON_EXTEND, blockListener, Priority.Highest, this);
		pm.registerEvent(Type.BLOCK_PISTON_RETRACT, blockListener, Priority.Highest, this);

		final EssentialsProtectEntityListener entityListener = new EssentialsProtectEntityListener(this);
		pm.registerEvent(Type.ENTITY_EXPLODE, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.CREATURE_SPAWN, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.ENTITY_TARGET, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.EXPLOSION_PRIME, entityListener, Priority.Highest, this);
		pm.registerEvent(Type.ENDERMAN_PICKUP, entityListener, Priority.Highest, this);

		final EssentialsProtectWeatherListener weatherListener = new EssentialsProtectWeatherListener(this);
		pm.registerEvent(Type.LIGHTNING_STRIKE, weatherListener, Priority.Highest, this);
		pm.registerEvent(Type.THUNDER_CHANGE, weatherListener, Priority.Highest, this);
		pm.registerEvent(Type.WEATHER_CHANGE, weatherListener, Priority.Highest, this);
	}

	private void enableEmergencyMode(final PluginManager pm)
	{
		final EmergencyListener emListener = new EmergencyListener();
		pm.registerEvents(emListener, this);
		for (Player player : getServer().getOnlinePlayers())
		{
			player.sendMessage("Essentials Protect is in emergency mode. Check your log for errors.");
		}
		LOGGER.log(Level.SEVERE, "Essentials not installed or failed to load. Essenials Protect is in emergency mode now.");
	}

	@Override
	public boolean checkProtectionItems(final ProtectConfig list, final int id)
	{
		final List<Integer> itemList = settingsList.get(list);
		return itemList != null && !itemList.isEmpty() && itemList.contains(id);
	}

	@Override
	public IProtectedBlock getStorage()
	{
		return storage;
	}

	@Override
	public void setStorage(IProtectedBlock pb)
	{
		storage = pb;
	}

	public EssentialsConnect getEssentialsConnect()
	{
		return ess;
	}
	
	public Map<ProtectConfig, Boolean> getSettingsBoolean()
	{
		return settingsBoolean;
	}

	public Map<ProtectConfig, String> getSettingsString()
	{
		return settingsString;
	}

	public Map<ProtectConfig, List<Integer>> getSettingsList()
	{
		return settingsList;
	}

	@Override
	public boolean getSettingBool(final ProtectConfig protectConfig)
	{
		final Boolean bool = settingsBoolean.get(protectConfig);
		return bool == null ? protectConfig.getDefaultValueBoolean() : bool;
	}

	@Override
	public String getSettingString(final ProtectConfig protectConfig)
	{
		final String str = settingsString.get(protectConfig);
		return str == null ? protectConfig.getDefaultValueString() : str;
	}

	public void onDisable()
	{
		if (storage != null)
		{
			storage.onPluginDeactivation();
		}
		// Sleep for a second to allow the database to close.
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException ex)
		{
		}
	}
}
