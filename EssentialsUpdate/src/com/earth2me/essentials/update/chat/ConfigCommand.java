package com.earth2me.essentials.update.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class ConfigCommand extends AbstractFileCommand implements Command
{
	public ConfigCommand(final Plugin plugin)
	{
		super(plugin);
	}

	@Override
	public void run(final IrcBot ircBot, final Player player)
	{
		BufferedReader page = null;
		try
		{
			page = getPluginConfig("Essentials", "config.yml");
			final StringBuilder input = new StringBuilder();
			do
			{
				final String line = page.readLine();
				if (line == null)
				{
					break;
				}
				else
				{
					input.append(line).append("\n");
				}
			}
			while (true);
			page.close();
			final String message = "Essentials config.yml: " + uploadToPastie(input);
			player.sendMessage("§6" + ircBot.getNick() + ": §7" + message);
			ircBot.sendMessage(message);
		}
		catch (IOException ex)
		{
			Bukkit.getLogger().log(Level.SEVERE, null, ex);
			player.sendMessage(ex.getMessage());
		}
		finally
		{
			try
			{
				if (page != null)
				{
					page.close();
				}
			}
			catch (IOException ex)
			{
				Bukkit.getLogger().log(Level.SEVERE, null, ex);
				player.sendMessage(ex.getMessage());
			}
		}

	}
}
