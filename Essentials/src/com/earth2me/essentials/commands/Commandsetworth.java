package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.ItemDb;
import com.earth2me.essentials.User;
import org.bukkit.inventory.ItemStack;


public class Commandsetworth extends EssentialsCommand
{
	public Commandsetworth()
	{
		super("setworth");
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if(args.length < 2)
		{
			user.sendMessage("§cUsage: /" + commandLabel + " [itemname|id] [price]");
			return;
		}
		ItemStack stack = ItemDb.get(args[0]);
		Essentials.getWorth().setPrice(stack, Double.parseDouble(args[1]));
		user.charge(this);
		user.sendMessage("§7Worth value set");
	}
}
