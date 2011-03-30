package com.earth2me.essentials.chat;

import com.earth2me.essentials.Essentials;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsChat extends JavaPlugin
{
	private static final Logger logger = Logger.getLogger("Minecraft");

	public EssentialsChat()
	{
		super();
	}

	public void onEnable()
	{
		Plugin p = this.getServer().getPluginManager().getPlugin("Essentials");
            if (p != null) {
                if (!this.getServer().getPluginManager().isPluginEnabled(p)) {
                    this.getServer().getPluginManager().enablePlugin(p);
                }
		}
		PluginManager pm = getServer().getPluginManager();
		EssentialsChatPlayerListener playerListener = new EssentialsChatPlayerListener(getServer());
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Highest, this);
		if (!this.getDescription().getVersion().equals(Essentials.getStatic().getDescription().getVersion())) {
			logger.log(Level.WARNING, "Version mismatch! Please update all Essentials jars to the same version.");
		}
		logger.info("Loaded " + this.getDescription().getName() + " build " + this.getDescription().getVersion() + " by " + Essentials.AUTHORS);
	}

	public void onDisable()
	{
	}
}
