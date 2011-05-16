package com.earth2me.essentials;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;


public class EssentialsUpgrade
{
	private static boolean alreadyRun = false;
	private final static Logger logger = Logger.getLogger("Minecraft");
	private Essentials ess;

	EssentialsUpgrade(String version, Essentials essentials)
	{
		if (alreadyRun == true)
		{
			return;
		}
		alreadyRun = true;
		ess = essentials;
	}

	private void moveWorthValuesToWorthYml()
	{
		try
		{
			File configFile = new File(ess.getDataFolder(), "config.yml");
			if (!configFile.exists())
			{
				return;
			}
			EssentialsConf conf = new EssentialsConf(configFile);
			conf.load();
			Worth w = new Worth(ess.getDataFolder());
			for (Material mat : Material.values())
			{
				int id = mat.getId();
				double value = conf.getDouble("worth-" + id, Double.NaN);
				if (!Double.isNaN(value))
				{
					w.setPrice(new ItemStack(mat, 1, (short)0, (byte)0), value);
				}
			}
			removeLinesFromConfig(configFile, "\\s*#?\\s*worth-[0-9]+.*", "# Worth values have been moved to worth.yml");
		}
		catch (Throwable e)
		{
			logger.log(Level.SEVERE, Util.i18n("upgradingFilesError"), e);
		}
	}

	private void removeLinesFromConfig(File file, String regex, String info) throws Exception
	{
		boolean needUpdate = false;
		BufferedReader br = new BufferedReader(new FileReader(file));
		File tempFile = File.createTempFile("essentialsupgrade", ".tmp.yml", ess.getDataFolder());
		BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
		do
		{
			String line = br.readLine();
			if (line == null)
			{
				break;
			}
			if (line.matches(regex))
			{
				if (needUpdate == false && info != null)
				{
					bw.write(info, 0, info.length());
					bw.newLine();
				}
				needUpdate = true;
			}
			else
			{
				if (line.endsWith("\r\n"))
				{
					bw.write(line, 0, line.length() - 2);
				}
				else if (line.endsWith("\r") || line.endsWith("\n"))
				{
					bw.write(line, 0, line.length() - 1);
				}
				else
				{
					bw.write(line, 0, line.length());
				}
				bw.newLine();
			}
		}
		while (true);
		br.close();
		bw.close();
		if (needUpdate)
		{
			if (!file.renameTo(new File(file.getParentFile(), file.getName().concat("." + System.currentTimeMillis() + ".upgradebackup"))))
			{
				throw new Exception(Util.i18n("configFileMoveError"));
			}
			if (!tempFile.renameTo(file))
			{
				throw new Exception(Util.i18n("configFileRenameError"));
			}
		} else {
			tempFile.delete();
		}
	}

	private void updateUsersToNewDefaultHome()
	{
		File userdataFolder = new File(ess.getDataFolder(), "userdata");
		if (!userdataFolder.exists() || !userdataFolder.isDirectory())
		{
			return;
		}
		File[] userFiles = userdataFolder.listFiles();

		for (File file : userFiles)
		{
			if (!file.isFile() || !file.getName().endsWith(".yml"))
			{
				continue;
			}
			EssentialsConf config = new EssentialsConf(file);
			config.load();
			if (config.hasProperty("home") && !config.hasProperty("home.default"))
			{
				@SuppressWarnings("unchecked")
				List<Object> vals = (List<Object>)config.getProperty("home");
				if (vals == null) {
					continue;
				}
				World world = ess.getServer().getWorlds().get(0);
				if (vals.size() > 5)
				{
					world = ess.getServer().getWorld((String)vals.get(5));
				}
				if (world != null)
				{
					Location loc = new Location(
							world,
							((Number)vals.get(0)).doubleValue(),
							((Number)vals.get(1)).doubleValue(),
							((Number)vals.get(2)).doubleValue(),
							((Number)vals.get(3)).floatValue(),
							((Number)vals.get(4)).floatValue());

					String worldName = world.getName().toLowerCase();
					if (worldName != null && !worldName.isEmpty())
					{
						config.removeProperty("home");
						config.setProperty("home.default", worldName);
						config.setProperty("home.worlds." + worldName, loc);
						config.save();
					}
				}
			}
		}
	}

	private void moveUsersDataToUserdataFolder()
	{
		File usersFile = new File(ess.getDataFolder(), "users.yml");
		if (!usersFile.exists())
		{
			return;
		}
		EssentialsConf usersConfig = new EssentialsConf(usersFile);
		usersConfig.load();
		for (String username : usersConfig.getKeys(null))
		{
			User user = new User(new OfflinePlayer(username), ess);
			String nickname = usersConfig.getString(username + ".nickname");
			if (nickname != null && !nickname.isEmpty() && !nickname.equals(username))
			{
				user.setNickname(nickname);
			}
			List<String> mails = usersConfig.getStringList(username + ".mail", null);
			if (mails != null && !mails.isEmpty())
			{
				user.setMails(mails);
			}
			if (!user.hasHome())
			{
				@SuppressWarnings("unchecked")
				List<Object> vals = (List<Object>)usersConfig.getProperty(username + ".home");
				if (vals != null) {
					World world = ess.getServer().getWorlds().get(0);
					if (vals.size() > 5)
					{
						world = getFakeWorld((String)vals.get(5));
					}
					if (world != null)
					{
						user.setHome(new Location(world,
												  ((Number)vals.get(0)).doubleValue(),
												  ((Number)vals.get(1)).doubleValue(),
												  ((Number)vals.get(2)).doubleValue(),
												  ((Number)vals.get(3)).floatValue(),
												  ((Number)vals.get(4)).floatValue()), true);
					}
				}
			}
		}
		usersFile.renameTo(new File(usersFile.getAbsolutePath() + ".old"));
	}

	private void convertWarps()
	{
		File warpsFolder = new File(ess.getDataFolder(), "warps");
		if (!warpsFolder.exists())
		{
			warpsFolder.mkdirs();
		}
		File[] listOfFiles = warpsFolder.listFiles();
		if (listOfFiles.length >= 1)
		{
			for (int i = 0; i < listOfFiles.length; i++)
			{
				String filename = listOfFiles[i].getName();
				if (listOfFiles[i].isFile() && filename.endsWith(".dat"))
				{
					try
					{
						BufferedReader rx = new BufferedReader(new FileReader(listOfFiles[i]));
						double x = Double.parseDouble(rx.readLine().trim());
						double y = Double.parseDouble(rx.readLine().trim());
						double z = Double.parseDouble(rx.readLine().trim());
						float yaw = Float.parseFloat(rx.readLine().trim());
						float pitch = Float.parseFloat(rx.readLine().trim());
						String worldName = rx.readLine();
						rx.close();
						World w = null;
						for (World world : ess.getServer().getWorlds())
						{
							if (world.getEnvironment() != World.Environment.NETHER)
							{
								w = world;
								break;
							}
						}
						if (worldName != null)
						{
							worldName.trim();
							World w1 = null;
							w1 = getFakeWorld(worldName);
							if (w1 != null)
							{
								w = w1;
							}
						}
						Location loc = new Location(w, x, y, z, yaw, pitch);
						Essentials.getWarps().setWarp(filename.substring(0, filename.length() - 4), loc);
						if (!listOfFiles[i].renameTo(new File(warpsFolder, filename + ".old")))
						{
							throw new Exception(Util.format("fileRenameError", filename));
						}
					}
					catch (Exception ex)
					{
						logger.log(Level.SEVERE, null, ex);
					}
				}
			}

		}
		File warpFile = new File(ess.getDataFolder(), "warps.txt");
		if (warpFile.exists())
		{
			try
			{
				BufferedReader rx = new BufferedReader(new FileReader(warpFile));
				for (String[] parts = new String[0]; rx.ready(); parts = rx.readLine().split(":"))
				{
					if (parts.length < 6)
					{
						continue;
					}
					String name = parts[0];
					double x = Double.parseDouble(parts[1].trim());
					double y = Double.parseDouble(parts[2].trim());
					double z = Double.parseDouble(parts[3].trim());
					float yaw = Float.parseFloat(parts[4].trim());
					float pitch = Float.parseFloat(parts[5].trim());
					if (name.isEmpty())
					{
						continue;
					}
					World w = null;
					for (World world : ess.getServer().getWorlds())
					{
						if (world.getEnvironment() != World.Environment.NETHER)
						{
							w = world;
							break;
						}
					}
					Location loc = new Location(w, x, y, z, yaw, pitch);
					Essentials.getWarps().setWarp(name, loc);
					if (!warpFile.renameTo(new File(ess.getDataFolder(), "warps.txt.old")))
					{
						throw new Exception(Util.format("fileRenameError", "warps.txt"));
					}
				}
			}
			catch (Exception ex)
			{
				logger.log(Level.SEVERE, null, ex);
			}
		}
	}

	private void sanitizeAllUserFilenames()
	{
		File usersFolder = new File(ess.getDataFolder(), "userdata");
		if (!usersFolder.exists())
		{
			return;
		}
		File[] listOfFiles = usersFolder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++)
		{
			String filename = listOfFiles[i].getName();
			if (!listOfFiles[i].isFile() || !filename.endsWith(".yml"))
			{
				continue;
			}
			String sanitizedFilename = Util.sanitizeFileName(filename.substring(0, filename.length() - 4)) + ".yml";
			if (sanitizedFilename.equals(filename))
			{
				continue;
			}
			File tmpFile = new File(listOfFiles[i].getParentFile(), sanitizedFilename + ".tmp");
			File newFile = new File(listOfFiles[i].getParentFile(), sanitizedFilename);
			if (!listOfFiles[i].renameTo(tmpFile)) {
				logger.log(Level.WARNING, Util.format("userdataMoveError", filename, sanitizedFilename));
				continue;
			}
			if (newFile.exists())
			{
				logger.log(Level.WARNING, Util.format("duplicatedUserdata", filename, sanitizedFilename));
				continue;
			}
			if (!tmpFile.renameTo(newFile)) {
				logger.log(Level.WARNING, Util.format("userdataMoveBackError", sanitizedFilename, sanitizedFilename));
			}
		}
	}
	
	private World getFakeWorld(String name)
	{
		File bukkitDirectory = ess.getDataFolder().getParentFile().getParentFile();
		File worldDirectory = new File(bukkitDirectory, name);
		if (worldDirectory.exists() && worldDirectory.isDirectory())
		{
			return new FakeWorld(worldDirectory.getName(), World.Environment.NORMAL);
		}
		return null;
	}

	void beforeSettings()
	{
		if (!ess.getDataFolder().exists())
		{
			ess.getDataFolder().mkdirs();
		}
		moveWorthValuesToWorthYml();
	}

	void afterSettings()
	{
		sanitizeAllUserFilenames();
		updateUsersToNewDefaultHome();
		moveUsersDataToUserdataFolder();
		convertWarps();
	}
}
