package com.earth2me.essentials.commands;

import com.earth2me.essentials.api.server.ICommandSender;
import com.earth2me.essentials.utils.textreader.IText;
import com.earth2me.essentials.utils.textreader.KeywordReplacer;
import com.earth2me.essentials.utils.textreader.TextInput;
import com.earth2me.essentials.utils.textreader.TextPager;
import org.bukkit.command.CommandSender;


public class Commandinfo extends EssentialsCommand
{
	@Override
	protected void run(final ICommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		final IText input = new TextInput(sender, "info", true, ess);
		final IText output = new KeywordReplacer(input, sender, ess);
		final TextPager pager = new TextPager(output);
		pager.showPage(args.length > 0 ? args[0] : null, args.length > 1 ? args[1] : null, commandLabel, sender);
	}
}
