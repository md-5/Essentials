package net.ess3.bukkit;

import java.util.logging.Level;
import net.ess3.Essentials;
import static net.ess3.I18n._;
import net.ess3.api.server.*;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsPlugin extends JavaPlugin
{
	private transient Essentials ess;

	public EssentialsPlugin()
	{
		Material.setInstance(BukkitMaterial.get(0));
		ItemStack.setFactory(new BukkitItemStack.BukkitItemStackFactory());
		Location.setFactory(new BukkitLocation.BukkitLocationFactory(getServer()));
		Permission.setFactory(new BukkitPermission.BukkitPermissionFactory());
	}

	@Override
	public void onEnable()
	{

		BukkitServer server = new BukkitServer(getServer());
		final PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(server, this);
		ess = new Essentials(server, getLogger(), new BukkitPlugin(this, server));
		if (VersionCheck.checkVersion(this))
		{
			try
			{
				ess.onEnable();
			}
			catch (RuntimeException ex)
			{
				if (pm.getPlugin("EssentialsUpdate") == null)
				{
					getLogger().log(Level.SEVERE, _("essentialsHelp1"));
				}
				else
				{
					getLogger().log(Level.SEVERE, _("essentialsHelp2"));
				}
				getLogger().log(Level.SEVERE, ex.toString());
				pm.registerEvents(new Listener()
				{
					@EventHandler(priority = EventPriority.LOW)
					public void onPlayerJoin(final PlayerJoinEvent event)
					{
						event.getPlayer().sendMessage("Essentials failed to load, read the log file.");
					}
				}, this);
				for (Player player : getServer().getOnlinePlayers())
				{
					player.sendMessage("Essentials failed to load, read the log file.");
				}
				this.setEnabled(false);
			}
		}
		else
		{
			this.setEnabled(false);
		}
	}

	@Override
	public void onDisable()
	{
		if (ess != null)
		{
			ess.onDisable();
		}
	}

	@Override
	public boolean onCommand(final org.bukkit.command.CommandSender sender, final Command command, final String label, final String[] args)
	{
		CommandSender commandSender;
		if (sender instanceof org.bukkit.entity.Player) {
			commandSender = ((BukkitServer)ess.getServer()).getPlayer((org.bukkit.entity.Player)sender);
		} else {
			commandSender = new BukkitCommandSender(sender);
		}
		return ess.getCommandHandler().handleCommand(commandSender, command, label, args);
	}
}
