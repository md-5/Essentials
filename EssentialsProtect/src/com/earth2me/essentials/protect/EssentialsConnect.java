package com.earth2me.essentials.protect;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.protect.data.ProtectedBlockMemory;
import com.earth2me.essentials.protect.data.ProtectedBlockMySQL;
import com.earth2me.essentials.protect.data.ProtectedBlockSQLite;
import java.beans.PropertyVetoException;
import static com.earth2me.essentials.I18n._;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class EssentialsConnect
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private final transient IEssentials ess;
	private final transient IProtect protect;

	public EssentialsConnect(Plugin essPlugin, Plugin essProtect)
	{
		if (!essProtect.getDescription().getVersion().equals(essPlugin.getDescription().getVersion()))
		{
			LOGGER.log(Level.WARNING, _("versionMismatchAll"));
		}
		ess = (IEssentials)essPlugin;
		protect = (IProtect)essProtect;
		ProtectReloader pr = new ProtectReloader();
		pr.reloadConfig();
		ess.addReloadListener(pr);
		LOGGER.info(_("loadinfo", essProtect.getDescription().getName(), essProtect.getDescription().getVersion(), "essentials team"));

	}

	public void onDisable()
	{
	}

	public IEssentials getEssentials()
	{
		return ess;
	}

	public void alert(final User user, final String item, final String type)
	{
		final Location loc = user.getLocation();
		final String warnMessage = _("alertFormat", user.getName(), type, item,
									 loc.getWorld().getName() + "," + loc.getBlockX() + ","
									 + loc.getBlockY() + "," + loc.getBlockZ());
		LOGGER.log(Level.WARNING, warnMessage);
		for (Player p : ess.getServer().getOnlinePlayers())
		{
			final User alertUser = ess.getUser(p);
			if (alertUser.isAuthorized("essentials.protect.alerts"))
			{
				alertUser.sendMessage(warnMessage);
			}
		}
	}


	private class ProtectReloader implements IConf
	{
		@Override
		public void reloadConfig()
		{
			if (protect.getStorage() != null)
			{
				protect.getStorage().onPluginDeactivation();
			}
			for (ProtectConfig protectConfig : ProtectConfig.values())
			{
				if (protectConfig.isList())
				{
					protect.getSettingsList().put(protectConfig, ess.getSettings().getProtectList(protectConfig.getConfigName()));
				}
				else if (protectConfig.isString())
				{
					protect.getSettingsString().put(protectConfig, ess.getSettings().getProtectString(protectConfig.getConfigName()));
				}
				else
				{
					protect.getSettingsBoolean().put(protectConfig, ess.getSettings().getProtectBoolean(protectConfig.getConfigName(), protectConfig.getDefaultValueBoolean()));
				}

			}

			if (protect.getSettingString(ProtectConfig.datatype).equalsIgnoreCase("mysql"))
			{
				try
				{
					protect.setStorage(new ProtectedBlockMySQL(
							protect.getSettingString(ProtectConfig.mysqlDB),
							protect.getSettingString(ProtectConfig.dbUsername),
							protect.getSettingString(ProtectConfig.dbPassword)));
				}
				catch (PropertyVetoException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
			else
			{
				try
				{
					protect.setStorage(new ProtectedBlockSQLite("jdbc:sqlite:plugins/Essentials/EssentialsProtect.db"));
				}
				catch (PropertyVetoException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
			if (protect.getSettingBool(ProtectConfig.memstore))
			{
				protect.setStorage(new ProtectedBlockMemory(protect.getStorage(), protect));
			}

		}
	}
}
