package net.ess3.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;


public abstract class AbstractDelayedYamlFileWriter implements Runnable
{
	private final transient IEssentials ess;
	private final transient ReentrantLock lock = new ReentrantLock();

	public AbstractDelayedYamlFileWriter(final IEssentials ess)
	{
		this.ess = ess;
	}

	public void schedule()
	{
		ess.getPlugin().scheduleAsyncDelayedTask(this);
	}

	public abstract File getFile() throws IOException;

	public abstract StorageObject getObject();

	@Override
	public void run()
	{
		lock.lock();
		try
		{
			final File file = getFile();
			PrintWriter pw = null;
			try
			{
				final StorageObject object = getObject();
				final File folder = file.getParentFile();
				if (!folder.exists())
				{
					folder.mkdirs();
				}
				pw = new PrintWriter(file);
				new YamlStorageWriter(pw).save(object);
			}
			catch (FileNotFoundException ex)
			{
				Bukkit.getLogger().log(Level.SEVERE, file.toString(), ex);
			}
			finally
			{
				onFinish();
				if (pw != null)
				{
					pw.close();
				}
			}
		}
		catch (IOException ex)
		{
			Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
		}
		finally
		{
			lock.unlock();
		}
	}

	public abstract void onFinish();
}
