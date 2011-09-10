package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandinfo extends EssentialsCommand
{
	public Commandinfo()
	{
		super("info");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		String pageStr = args.length > 0 ? args[0].trim() : null;

		List<String> lines = new ArrayList<String>();
		List<String> chapters = new ArrayList<String>();
		Map<String, Integer> bookmarks = new HashMap<String, Integer>();
		File file = null;
		if (sender instanceof Player)
		{
			User user = ess.getUser(sender);
			file = new File(ess.getDataFolder(), "info_"+Util.sanitizeFileName(user.getName()) +".txt");
			if (!file.exists())
			{
				file = new File(ess.getDataFolder(), "info_"+Util.sanitizeFileName(user.getGroup()) +".txt");
			}
		}
		if (file == null || !file.exists())
		{
			file = new File(ess.getDataFolder(), "info.txt");
		}
		if (file.exists())
		{
			final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			try
			{
				int lineNumber = 0;
				while (bufferedReader.ready())
				{
					final String line = bufferedReader.readLine();
					if (line.length() > 0 && line.charAt(0) == '#')
					{
						bookmarks.put(line.substring(1).toLowerCase().replaceAll("&[0-9a-f]", ""), lineNumber);
						chapters.add(line.substring(1).replace('&', '§'));
					}
					lines.add(line.replace('&', '§'));
					lineNumber++;
				}
			}
			finally
			{
				bufferedReader.close();
			}
		}
		else
		{
			file.createNewFile();
			throw new Exception(Util.i18n("infoFileDoesNotExist"));
		}

		if (bookmarks.isEmpty())
		{
			int page = 1;
			try
			{
				page = Integer.parseInt(pageStr);
			}
			catch (Exception ex)
			{
				page = 1;
			}

			int start = (page - 1) * 9;
			int pages = lines.size() / 9 + (lines.size() % 9 > 0 ? 1 : 0);

			sender.sendMessage(Util.format("infoPages", page, pages ));
			for (int i = start; i < lines.size() && i < start + 9; i++)
			{
				sender.sendMessage(lines.get(i));
			}
			return;
		}

		if (pageStr == null || pageStr.isEmpty() || pageStr.matches("[0-9]+"))
		{
			if (lines.get(0).startsWith("#"))
			{
				sender.sendMessage(Util.i18n("infoChapter"));
				StringBuilder sb = new StringBuilder();
				boolean first = true;
				for (String string : chapters)
				{
					if (!first)
					{
						sb.append(", ");
					}
					first = false;
					sb.append(string);
				}
				sender.sendMessage(sb.toString());
				return;
			}
			else
			{
				int page = 1;
				try
				{
					page = Integer.parseInt(pageStr);
				}
				catch (Exception ex)
				{
					page = 1;
				}

				int start = (page - 1) * 9;
				int end;
				for (end = 0; end < lines.size(); end++)
				{
					String line = lines.get(end);
					if (line.startsWith("#"))
					{
						break;
					}
				}
				int pages = end / 9 + (end % 9 > 0 ? 1 : 0);

				sender.sendMessage(Util.format("infoPages", page, pages ));
				for (int i = start; i < end && i < start + 9; i++)
				{
					sender.sendMessage(lines.get(i));
				}
				return;
			}
		}

		int chapterpage = 0;
		if (args.length >= 2)
		{
			try
			{
				chapterpage = Integer.parseInt(args[1]) - 1;
			}
			catch (Exception ex)
			{
				chapterpage = 0;
			}
		}

		if (!bookmarks.containsKey(pageStr.toLowerCase()))
		{
			sender.sendMessage(Util.i18n("infoUnknownChapter"));
			return;
		}
		int chapterstart = bookmarks.get(pageStr.toLowerCase()) + 1;
		int chapterend;
		for (chapterend = chapterstart; chapterend < lines.size(); chapterend++)
		{
			String line = lines.get(chapterend);
			if (line.startsWith("#"))
			{
				break;
			}
		}
		int start = chapterstart + chapterpage * 9;
		int page = chapterpage + 1;
		int pages = (chapterend - chapterstart) / 9 + ((chapterend - chapterstart) % 9 > 0 ? 1 : 0);

		sender.sendMessage(Util.format("infoChapterPages", pageStr, page , pages));
		for (int i = start; i < chapterend && i < start + 9; i++)
		{
			sender.sendMessage(lines.get(i));
		}
	}
}
