package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.ItemDb;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.inventory.ItemStack;


public class Commandsetworth extends EssentialsCommand
{
	public Commandsetworth()
	{
		super("setworth");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		ItemStack stack = ItemDb.get(args[0]);
		charge(user);
		ess.getWorth().setPrice(stack, Double.parseDouble(args[1]));
		user.sendMessage(Util.i18n("worthSet"));
	}
}
