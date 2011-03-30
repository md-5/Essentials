package com.earth2me.essentials;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;


public class EssentialsEcoBlockListener extends BlockListener
{
	@Override
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (event.isCancelled()) return;
		if (Essentials.getSettings().areSignsDisabled()) return;
		User user = User.get(event.getPlayer());
		if (event.getBlock().getType() != Material.WALL_SIGN && event.getBlock().getType() != Material.SIGN_POST)
			return;
		Sign sign = new CraftSign(event.getBlock());

		if (sign.getLine(0).equals("§1[Trade]"))
		{
			if (!sign.getLine(3).substring(2).equals(user.getName())) {
				if (!user.isOp()) {
					event.setCancelled(true);
				}
				return;
			}
			try
			{
				String[] l1 = sign.getLines()[1].split("[ :-]+");
				String[] l2 = sign.getLines()[2].split("[ :-]+");
				boolean m1 = l1[0].matches("\\$[0-9]+");
				boolean m2 = l2[0].matches("\\$[0-9]+");
				int q1 = Integer.parseInt(m1 ? l1[0].substring(1) : l1[0]);
				int q2 = Integer.parseInt(m2 ? l2[0].substring(1) : l2[0]);
				int r1 = Integer.parseInt(l1[m1 ? 1 : 2]);
				int r2 = Integer.parseInt(l2[m2 ? 1 : 2]);
				if (q1 < 1 || q2 < 1) throw new Exception("Quantities must be greater than 0.");

				ItemStack i1 = m1 || r1 <= 0 ? null : ItemDb.get(l1[1], r1);
				ItemStack i2 = m2 || r2 <= 0 ? null : ItemDb.get(l2[1], r2);

				if (m1)
					user.giveMoney(r1);
				else if (i1 != null)
					user.getWorld().dropItem(user.getLocation(), i1);

				if (m2)
					user.giveMoney(r2);
				else if (i2 != null)
					user.getWorld().dropItem(user.getLocation(), i2);

				sign.setType(Material.AIR);
			}
			catch (Throwable ex)
			{
				user.sendMessage("§cError: " + ex.getMessage());
			}
			return;
		}
	}

	
	@Override
	public void onSignChange(SignChangeEvent event)
	{
		if (Essentials.getSettings().areSignsDisabled()) return;
		User user = User.get(event.getPlayer());

		if (event.getLine(0).equalsIgnoreCase("[Buy]") && user.isAuthorized("essentials.signs.buy.create"))
		{
			try
			{
				event.setLine(0, "§1[Buy]");
				event.setLine(1, "" + Math.abs(Integer.parseInt(event.getLine(1))));
				ItemDb.get(event.getLine(2));
				event.setLine(3, "$" + Integer.parseInt(event.getLine(3).replaceAll("[^0-9]", "")));
			}
			catch (Throwable ex)
			{
				user.sendMessage("§cError: " + ex.getMessage());
				event.setLine(0, "§4[Buy]");
				event.setLine(1, "#");
				event.setLine(2, "Item");
				event.setLine(3, "$Price");
			}
			return;
		}

		if (event.getLine(0).equalsIgnoreCase("[Sell]") && user.isAuthorized("essentials.signs.sell.create"))
		{
			try
			{
				event.setLine(0, "§1[Sell]");
				event.setLine(1, "" + Math.abs(Integer.parseInt(event.getLine(1))));
				ItemDb.get(event.getLine(2));
				event.setLine(3, "$" + Integer.parseInt(event.getLine(3).replaceAll("[^0-9]", "")));
			}
			catch (Throwable ex)
			{
				user.sendMessage("§cError: " + ex.getMessage());
				event.setLine(0, "§4[Sell]");
				event.setLine(1, "#");
				event.setLine(2, "Item");
				event.setLine(3, "$Price");
			}
			return;
		}

		if (event.getLine(0).equalsIgnoreCase("[Trade]") && user.isAuthorized("essentials.signs.trade.create"))
		{
			try
			{
				String[] l1 = event.getLines()[1].split("[ :-]+");
				String[] l2 = event.getLines()[2].split("[ :-]+");
				boolean m1 = l1[0].matches("\\$[0-9]+");
				boolean m2 = l2[0].matches("\\$[0-9]+");
				int q1 = Integer.parseInt(m1 ? l1[0].substring(1) : l1[0]);
				int q2 = Integer.parseInt(m2 ? l2[0].substring(1) : l2[0]);
				int r2 = Integer.parseInt(l2[m2 ? 1 : 2]);
				r2 = r2 - r2 % q2;
				if (q1 < 1 || q2 < 1 || r2 < 1) throw new Exception("Quantities must be greater than 0.");
				if (!m1) ItemDb.get(l1[1]);

				if (m2)
				{
					if (user.getMoney() < r2) throw new Exception("You do not have sufficient funds.");
					user.takeMoney(r2);
					user.sendMessage("r2: " + r2 + "    q2: " + q2);
				}
				else
				{
					ItemStack i2 = ItemDb.get(l2[1], r2);
					if (!InventoryWorkaround.containsItem((CraftInventory)user.getInventory(), true, i2)) throw new Exception("You do not have " + r2 + "x " + l2[1] + ".");
					InventoryWorkaround.removeItem((CraftInventory)user.getInventory(), true, i2);
					user.updateInventory();
				}

				event.setLine(0, "§1[Trade]");
				event.setLine(1, (m1 ? "$" + q1 : q1 + " " + l1[1]) + ":0");
				event.setLine(2, (m2 ? "$" + q2 : q2 + " " + l2[1]) + ":" + r2);
				event.setLine(3, "§8" + user.getName());
			}
			catch (Throwable ex)
			{
				user.sendMessage("§cError: " + ex.getMessage());
				event.setLine(0, "§4[Trade]");
				event.setLine(1, "# ItemOr$");
				event.setLine(2, "# ItemOr$:#");
				event.setLine(3, "§8" + user.getName());
			}
			return;
		}
	}
}
