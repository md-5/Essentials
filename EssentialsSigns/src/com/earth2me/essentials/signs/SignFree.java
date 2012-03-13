package com.earth2me.essentials.signs;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class SignFree extends EssentialsSign
{
	public SignFree()
	{
		super("Free");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final IUser player, final String username, final IEssentials ess) throws SignException
	{
		getItemStack(sign.getLine(1), 1, ess);
		return true;
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final IUser player, final String username, final IEssentials ess) throws SignException
	{
		final ItemStack item = getItemStack(sign.getLine(1), 1, ess);
		if (item.getType() == Material.AIR)
		{
			throw new SignException(_("cantSpawnItem", "Air"));
		}

		item.setAmount(item.getType().getMaxStackSize());
		InventoryWorkaround.addItem(player.getInventory(), true, item);
		player.sendMessage("Item added to your inventory.");
		player.updateInventory();
		//TODO: wait for a fix in bukkit
		//Problem: Items can be duplicated
		//Inventory i = ess.getServer().createInventory(player, InventoryType.CHEST);
		//i.addItem(item);
		//player.openInventory(i);
		Trade.log("Sign", "Free", "Interact", username, null, username, new Trade(item, ess), sign.getBlock().getLocation(), ess);
		return true;
	}
}
