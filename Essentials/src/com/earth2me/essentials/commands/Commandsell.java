package com.earth2me.essentials.commands;

import com.earth2me.essentials.InventoryWorkaround;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;


public class Commandsell extends EssentialsCommand
{
	public Commandsell()
	{
		super("sell");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		ItemStack is = null;
		if (args[0].equalsIgnoreCase("hand"))
		{
			is = user.getItemInHand();
		}
		else if (args[0].equalsIgnoreCase("inventory") || args[0].equalsIgnoreCase("invent") || args[0].equalsIgnoreCase("all"))
		{
			for (ItemStack stack : user.getInventory().getContents())
			{
				if (stack == null || stack.getType() == Material.AIR)
				{
					continue;
				}
				try
				{
					sellItem(user, stack, args, true);
				}
				catch (Exception e)
				{
				}
			}
			return;
		}
		else if (args[0].equalsIgnoreCase("blocks"))
		{
			for (ItemStack stack : user.getInventory().getContents())
			{
				if (stack == null || stack.getTypeId() > 255 || stack.getType() == Material.AIR)
				{
					continue;
				}
				try
				{
					sellItem(user, stack, args, true);
				}
				catch (Exception e)
				{
				}
			}
			return;
		}
		if (is == null)
		{
			is = ess.getItemDb().get(args[0]);
		}
		sellItem(user, is, args, false);
	}

	private void sellItem(User user, ItemStack is, String[] args, boolean isBulkSell) throws Exception
	{
		if (is == null || is.getType() == Material.AIR)
		{
			throw new Exception(Util.i18n("itemSellAir"));
		}
		int id = is.getTypeId();
		int amount = 0;
		if (args.length > 1)
		{
			amount = Integer.parseInt(args[1].replaceAll("[^0-9]", ""));
			if (args[1].startsWith("-"))
			{
				amount = -amount;
			}
		}
		double worth = ess.getWorth().getPrice(is);
		boolean stack = args.length > 1 && args[1].endsWith("s");
		boolean requireStack = ess.getSettings().isTradeInStacks(id);

		if (Double.isNaN(worth))
		{
			throw new Exception(Util.i18n("itemCannotBeSold"));
		}
		if (requireStack && !stack)
		{
			throw new Exception(Util.i18n("itemMustBeStacked"));
		}


		int max = 0;
		for (ItemStack s : user.getInventory().getContents())
		{
			if (s == null)
			{
				continue;
			}
			if (s.getTypeId() != is.getTypeId())
			{
				continue;
			}
			if (s.getDurability() != is.getDurability())
			{
				continue;
			}
			max += s.getAmount();
		}

		if (stack)
		{
			amount *= 64;
		}
		if (amount < 1)
		{
			amount += max;
		}

		if (requireStack)
		{
			amount -= amount % 64;
		}
		if (amount > max || amount < 1)
		{
			if (!isBulkSell)
			{
				user.sendMessage(Util.i18n("itemNotEnough1"));
				user.sendMessage(Util.i18n("itemNotEnough2"));
				throw new Exception(Util.i18n("itemNotEnough3"));
			}
			else
			{
				return;
			}
		}

		final ItemStack ris = new ItemStack(is.getType(), amount, is.getDurability());
		InventoryWorkaround.removeItem(user.getInventory(), true, ris);
		user.updateInventory();
		Trade.log("Command", "Sell", "Item", user.getName(), new Trade(ris, ess), user.getName(), new Trade(worth * amount, ess), user.getLocation(), ess);
		user.giveMoney(worth * amount);
		user.sendMessage(Util.format("itemSold", Util.formatCurrency(worth * amount, ess), amount, is.getType().toString().toLowerCase(), Util.formatCurrency(worth, ess)));
		logger.log(Level.INFO, Util.format("itemSoldConsole", user.getDisplayName(), is.getType().toString().toLowerCase(), Util.formatCurrency(worth * amount, ess), amount, Util.formatCurrency(worth, ess)));

	}
}
