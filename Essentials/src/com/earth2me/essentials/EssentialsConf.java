package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import com.google.common.io.Files;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


public class EssentialsConf extends YamlConfiguration
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private transient File configFile;
	private transient String templateName = null;
	private transient Class<?> resourceClass = EssentialsConf.class;
	private static final Charset UTF8 = Charset.forName("UTF-8");

	public EssentialsConf(final File configFile)
	{
		super();
		this.configFile = configFile;
	}
	private final byte[] bytebuffer = new byte[1024];

	public synchronized void load()
	{
		configFile = configFile.getAbsoluteFile();
		if (!configFile.getParentFile().exists())
		{
			if (!configFile.getParentFile().mkdirs())
			{
				LOGGER.log(Level.SEVERE, _("failedToCreateConfig", configFile.toString()));
			}
		}
		// This will delete files where the first character is 0. In most cases they are broken.
		if (configFile.exists() && configFile.length() != 0)
		{
			try
			{
				final InputStream input = new FileInputStream(configFile);
				try
				{
					if (input.read() == 0)
					{
						input.close();
						configFile.delete();
					}
				}
				catch (IOException ex)
				{
					LOGGER.log(Level.SEVERE, null, ex);
				}
				finally
				{
					try
					{
						input.close();
					}
					catch (IOException ex)
					{
						LOGGER.log(Level.SEVERE, null, ex);
					}
				}
			}
			catch (FileNotFoundException ex)
			{
				LOGGER.log(Level.SEVERE, null, ex);
			}
		}

		if (!configFile.exists())
		{
			if (templateName != null)
			{
				LOGGER.log(Level.INFO, _("creatingConfigFromTemplate", configFile.toString()));
				createFromTemplate();
			}
			else
			{
				try
				{
					LOGGER.log(Level.INFO, _("creatingEmptyConfig", configFile.toString()));
					if (!configFile.createNewFile())
					{
						LOGGER.log(Level.SEVERE, _("failedToCreateConfig", configFile.toString()));
					}
				}
				catch (IOException ex)
				{
					LOGGER.log(Level.SEVERE, _("failedToCreateConfig", configFile.toString()), ex);
				}
			}
		}


		try
		{
			final FileInputStream inputStream = new FileInputStream(configFile);
			try
			{
				final ByteBuffer buffer = ByteBuffer.allocate((int)configFile.length());
				int length;
				while ((length = inputStream.read(bytebuffer)) != -1)
				{
					buffer.put(bytebuffer, 0, length);
				}
				buffer.rewind();
				final CharBuffer data = CharBuffer.allocate((int)configFile.length());
				CharsetDecoder decoder = UTF8.newDecoder();
				CoderResult result = decoder.decode(buffer, data, true);
				if (result.isError())
				{
					buffer.rewind();
					data.clear();
					LOGGER.log(Level.INFO, "File " + configFile.getAbsolutePath().toString() + " is not utf-8 encoded, trying " + Charset.defaultCharset().displayName());
					decoder = Charset.defaultCharset().newDecoder();
					result = decoder.decode(buffer, data, true);
					if (result.isError())
					{
						throw new InvalidConfigurationException("Invalid Characters in file " + configFile.getAbsolutePath().toString());
					}
					else
					{
						decoder.flush(data);
					}
				}
				else
				{
					decoder.flush(data);
				}
				final int end = data.position();
				data.rewind();
				super.loadFromString(data.subSequence(0, end).toString());
			}
			finally
			{
				inputStream.close();
			}
		}
		catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
		}
		catch (InvalidConfigurationException ex)
		{
			File broken = new File(configFile.getAbsolutePath() + ".broken." + System.currentTimeMillis());
			configFile.renameTo(broken);
			LOGGER.log(Level.SEVERE, "The file " + configFile.toString() + " is broken, it has been renamed to " + broken.toString(), ex.getCause());
		}
	}

	private void createFromTemplate()
	{
		InputStream istr = null;
		OutputStream ostr = null;
		try
		{
			istr = resourceClass.getResourceAsStream(templateName);
			if (istr == null)
			{
				LOGGER.log(Level.SEVERE, _("couldNotFindTemplate", templateName));
				return;
			}
			ostr = new FileOutputStream(configFile);
			byte[] buffer = new byte[1024];
			int length = 0;
			length = istr.read(buffer);
			while (length > 0)
			{
				ostr.write(buffer, 0, length);
				length = istr.read(buffer);
			}
		}
		catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, _("failedToWriteConfig", configFile.toString()), ex);
		}
		finally
		{
			try
			{
				if (istr != null)
				{
					istr.close();
				}
			}
			catch (IOException ex)
			{
				Logger.getLogger(EssentialsConf.class.getName()).log(Level.SEVERE, null, ex);
			}
			try
			{
				if (ostr != null)
				{
					ostr.close();
				}
			}
			catch (IOException ex)
			{
				LOGGER.log(Level.SEVERE, _("failedToCloseConfig", configFile.toString()), ex);
			}
		}
	}

	public void setTemplateName(final String templateName)
	{
		this.templateName = templateName;
	}

	public File getFile()
	{
		return configFile;
	}

	public void setTemplateName(final String templateName, final Class<?> resClass)
	{
		this.templateName = templateName;
		this.resourceClass = resClass;
	}

	public boolean hasProperty(final String path)
	{
		return isSet(path);
	}

	public Location getLocation(final String path, final Server server) throws Exception
	{
		final String worldName = getString((path == null ? "" : path + ".") + "world");
		if (worldName == null || worldName.isEmpty())
		{
			return null;
		}
		final World world = server.getWorld(worldName);
		if (world == null)
		{
			throw new Exception(_("invalidWorld"));
		}
		return new Location(world,
							getDouble((path == null ? "" : path + ".") + "x", 0),
							getDouble((path == null ? "" : path + ".") + "y", 0),
							getDouble((path == null ? "" : path + ".") + "z", 0),
							(float)getDouble((path == null ? "" : path + ".") + "yaw", 0),
							(float)getDouble((path == null ? "" : path + ".") + "pitch", 0));
	}

	public void setProperty(final String path, final Location loc)
	{
		set((path == null ? "" : path + ".") + "world", loc.getWorld().getName());
		set((path == null ? "" : path + ".") + "x", loc.getX());
		set((path == null ? "" : path + ".") + "y", loc.getY());
		set((path == null ? "" : path + ".") + "z", loc.getZ());
		set((path == null ? "" : path + ".") + "yaw", loc.getYaw());
		set((path == null ? "" : path + ".") + "pitch", loc.getPitch());
	}

	@Override
	public ItemStack getItemStack(final String path)
	{
		final ItemStack stack = new ItemStack(
				Material.valueOf(getString(path + ".type", "AIR")),
				getInt(path + ".amount", 1),
				(short)getInt(path + ".damage", 0));
		final ConfigurationSection enchants = getConfigurationSection(path + ".enchant");
		if (enchants != null)
		{
			for (String enchant : enchants.getKeys(false))
			{
				final Enchantment enchantment = Enchantment.getByName(enchant.toUpperCase(Locale.ENGLISH));
				if (enchantment == null)
				{
					continue;
				}
				final int level = getInt(path + ".enchant." + enchant, enchantment.getStartLevel());
				stack.addUnsafeEnchantment(enchantment, level);
			}
		}
		return stack;
		/*
		 * ,
		 * (byte)getInt(path + ".data", 0)
		 */
	}

	public void setProperty(final String path, final ItemStack stack)
	{
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", stack.getType().toString());
		map.put("amount", stack.getAmount());
		map.put("damage", stack.getDurability());
		Map<Enchantment, Integer> enchantments = stack.getEnchantments();
		if (!enchantments.isEmpty())
		{
			Map<String, Integer> enchant = new HashMap<String, Integer>();
			for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet())
			{
				enchant.put(entry.getKey().getName().toLowerCase(Locale.ENGLISH), entry.getValue());
			}
			map.put("enchant", enchant);
		}
		// getData().getData() is broken
		//map.put("data", stack.getDurability());
		set(path, map);
	}

	public long getLong(final String path, final long def)
	{
		try
		{
			final Number num = (Number)get(path);
			return num == null ? def : num.longValue();
		}
		catch (ClassCastException ex)
		{
			return def;
		}
	}

	@Override
	public double getDouble(final String path, final double def)
	{
		try
		{
			Number num = (Number)get(path);
			return num == null ? def : num.doubleValue();
		}
		catch (ClassCastException ex)
		{
			return def;
		}
	}

	public void save()
	{
		try
		{
			save(configFile);
		}
		catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	@Override
	public synchronized void save(final File file) throws IOException
	{
		if (file == null)
		{
			throw new IllegalArgumentException("File cannot be null");
		}

		Files.createParentDirs(file);

		final String data = saveToString();

		final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), UTF8);

		try
		{
			writer.write(data);
		}
		finally
		{
			writer.close();
		}
	}

	public Object getProperty(String path)
	{
		return get(path);
	}

	public void setProperty(String path, Object object)
	{
		set(path, object);
	}

	public void removeProperty(String path)
	{
		set(path, null);
	}

	@Override
	public synchronized Object get(String path)
	{
		return super.get(path);
	}

	@Override
	public synchronized Object get(String path, Object def)
	{
		return super.get(path, def);
	}

	@Override
	public synchronized boolean getBoolean(String path)
	{
		return super.getBoolean(path);
	}

	@Override
	public synchronized boolean getBoolean(String path, boolean def)
	{
		return super.getBoolean(path, def);
	}

	@Override
	public synchronized List<Boolean> getBooleanList(String path)
	{
		return super.getBooleanList(path);
	}

	@Override
	public synchronized List<Byte> getByteList(String path)
	{
		return super.getByteList(path);
	}

	@Override
	public synchronized List<Character> getCharacterList(String path)
	{
		return super.getCharacterList(path);
	}

	@Override
	public synchronized ConfigurationSection getConfigurationSection(String path)
	{
		return super.getConfigurationSection(path);
	}

	@Override
	public synchronized double getDouble(String path)
	{
		return super.getDouble(path);
	}

	@Override
	public synchronized List<Double> getDoubleList(String path)
	{
		return super.getDoubleList(path);
	}

	@Override
	public synchronized List<Float> getFloatList(String path)
	{
		return super.getFloatList(path);
	}

	@Override
	public synchronized int getInt(String path)
	{
		return super.getInt(path);
	}

	@Override
	public synchronized int getInt(String path, int def)
	{
		return super.getInt(path, def);
	}

	@Override
	public synchronized List<Integer> getIntegerList(String path)
	{
		return super.getIntegerList(path);
	}

	@Override
	public synchronized ItemStack getItemStack(String path, ItemStack def)
	{
		return super.getItemStack(path, def);
	}

	@Override
	public synchronized Set<String> getKeys(boolean deep)
	{
		return super.getKeys(deep);
	}

	@Override
	public synchronized List<?> getList(String path)
	{
		return super.getList(path);
	}

	@Override
	public synchronized List<?> getList(String path, List<?> def)
	{
		return super.getList(path, def);
	}

	@Override
	public synchronized long getLong(String path)
	{
		return super.getLong(path);
	}

	@Override
	public synchronized List<Long> getLongList(String path)
	{
		return super.getLongList(path);
	}

	public synchronized Map<String, Object> getMap()
	{
		return map;
	}

	@Override
	public synchronized List<Map<?, ?>> getMapList(String path)
	{
		return super.getMapList(path);
	}

	@Override
	public synchronized OfflinePlayer getOfflinePlayer(String path)
	{
		return super.getOfflinePlayer(path);
	}

	@Override
	public synchronized OfflinePlayer getOfflinePlayer(String path, OfflinePlayer def)
	{
		return super.getOfflinePlayer(path, def);
	}

	@Override
	public synchronized List<Short> getShortList(String path)
	{
		return super.getShortList(path);
	}

	@Override
	public synchronized String getString(String path)
	{
		return super.getString(path);
	}

	@Override
	public synchronized String getString(String path, String def)
	{
		return super.getString(path, def);
	}

	@Override
	public synchronized List<String> getStringList(String path)
	{
		return super.getStringList(path);
	}

	@Override
	public synchronized Map<String, Object> getValues(boolean deep)
	{
		return super.getValues(deep);
	}

	@Override
	public synchronized Vector getVector(String path)
	{
		return super.getVector(path);
	}

	@Override
	public synchronized Vector getVector(String path, Vector def)
	{
		return super.getVector(path, def);
	}

	@Override
	public synchronized boolean isBoolean(String path)
	{
		return super.isBoolean(path);
	}

	@Override
	public synchronized boolean isConfigurationSection(String path)
	{
		return super.isConfigurationSection(path);
	}

	@Override
	public synchronized boolean isDouble(String path)
	{
		return super.isDouble(path);
	}

	@Override
	public synchronized boolean isInt(String path)
	{
		return super.isInt(path);
	}

	@Override
	public synchronized boolean isItemStack(String path)
	{
		return super.isItemStack(path);
	}

	@Override
	public synchronized boolean isList(String path)
	{
		return super.isList(path);
	}

	@Override
	public synchronized boolean isLong(String path)
	{
		return super.isLong(path);
	}

	@Override
	public synchronized boolean isOfflinePlayer(String path)
	{
		return super.isOfflinePlayer(path);
	}

	@Override
	public synchronized boolean isSet(String path)
	{
		return super.isSet(path);
	}

	@Override
	public synchronized boolean isString(String path)
	{
		return super.isString(path);
	}

	@Override
	public synchronized boolean isVector(String path)
	{
		return super.isVector(path);
	}

	@Override
	public synchronized void set(String path, Object value)
	{
		super.set(path, value);
	}
}
