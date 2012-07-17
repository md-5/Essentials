package net.ess3.update.chat;

import net.ess3.update.PastieUpload;
import java.io.*;
import java.nio.charset.Charset;

import net.ess3.update.PastieUpload;
import org.bukkit.plugin.Plugin;


public abstract class AbstractFileCommand implements Command
{
	private final transient Plugin plugin;
	private final static Charset UTF8 = Charset.forName("utf-8");

	public AbstractFileCommand(final Plugin plugin)
	{
		this.plugin = plugin;
	}

	protected BufferedReader getServerLogReader() throws IOException
	{
		final File bukkitFolder = plugin.getDataFolder().getAbsoluteFile().getParentFile().getParentFile();
		if (bukkitFolder == null || !bukkitFolder.exists())
		{
			throw new IOException("Bukkit folder not found.");
		}
		final File logFile = new File(bukkitFolder, "server.log");
		if (!logFile.exists())
		{
			throw new IOException("Server log not found.");
		}
		final FileInputStream fis = new FileInputStream(logFile);
		try
		{
			if (logFile.length() > 1000000)
			{
				fis.skip(logFile.length() - 1000000);
			}
			return new BufferedReader(new InputStreamReader(fis));
		}
		catch (IOException ex)
		{
			fis.close();
			throw ex;
		}
	}

	protected BufferedReader getPluginConfig(final String pluginName, final String fileName) throws IOException
	{
		final File configFolder = new File(plugin.getDataFolder().getAbsoluteFile().getParentFile(), pluginName);
		if (!configFolder.exists())
		{
			throw new IOException(pluginName + " plugin folder not found.");
		}
		final File configFile = new File(configFolder, fileName);
		if (!configFile.exists())
		{
			throw new IOException(pluginName + " plugin file " + fileName + " not found.");
		}
		return new BufferedReader(new InputStreamReader(new FileInputStream(configFile), UTF8));

	}

	protected String uploadToPastie(final StringBuilder input) throws IOException
	{
		if (input.length() > 15000)
		{
			input.delete(0, input.length() - 15000);
			input.append("## Cropped after 15000 bytes");
		}
		final PastieUpload pastie = new PastieUpload();
		return pastie.send(input.toString());
	}
}
