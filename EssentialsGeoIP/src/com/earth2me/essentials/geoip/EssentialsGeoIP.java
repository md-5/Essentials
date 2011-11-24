package com.earth2me.essentials.geoip;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
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
		final PluginManager pm = getServer().getPluginManager();
		final IEssentials ess = (IEssentials)pm.getPlugin("Essentials");
		final EssentialsGeoIPPlayerListener playerListener = new EssentialsGeoIPPlayerListener(getDataFolder(), ess);
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);

		if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion()))
		{
			logger.log(Level.WARNING, _("versionMismatchAll"));
		}
		logger.info(_("loadinfo", this.getDescription().getName(), this.getDescription().getVersion(), "essentials team"));

		logger.log(Level.INFO, "This product includes GeoLite data created by MaxMind, available from http://www.maxmind.com/.");
	}
}
