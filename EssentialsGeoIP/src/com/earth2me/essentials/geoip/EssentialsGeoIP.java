package com.earth2me.essentials.geoip;

import com.earth2me.essentials.Essentials;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsGeoIP extends JavaPlugin
{
	private static final Logger logger = Logger.getLogger("Minecraft");

	public EssentialsGeoIP()
	{
	}

	@Override
	public void onDisable()
	{
	}

	@Override
	public void onEnable()
	{
		PluginManager pm = getServer().getPluginManager();
		Plugin ess = pm.getPlugin("Essentials");
		if (ess != null)
		{
			if (!pm.isPluginEnabled(ess))
			{
				pm.enablePlugin(ess);
			}
		}
		EssentialsGeoIPPlayerListener playerListener = new EssentialsGeoIPPlayerListener(getDataFolder());
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);

		logger.info("Loaded " + this.getDescription().getName() + " build " + this.getDescription().getVersion() + " by " + Essentials.AUTHORS);

		logger.log(Level.INFO, "This product includes GeoLite data created by MaxMind, available from http://www.maxmind.com/.");
	}
}
