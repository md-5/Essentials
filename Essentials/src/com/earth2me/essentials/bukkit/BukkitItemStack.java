package com.earth2me.essentials.bukkit;

import com.earth2me.essentials.api.server.Enchantment;
import com.earth2me.essentials.api.server.ItemStack;
import com.earth2me.essentials.api.server.Material;
import lombok.Delegate;
import lombok.Getter;


public class BukkitItemStack extends com.earth2me.essentials.api.server.ItemStack
{
	public static class BukkitItemStackFactory implements ItemStackFactory
	{
		@Override
		public ItemStack create(int id, int amount, int data)
		{
			return new BukkitItemStack(new org.bukkit.inventory.ItemStack(id, amount, (short)data));
		}

		@Override
		public ItemStack clone(final ItemStack stack)
		{
			return new BukkitItemStack(((BukkitItemStack)stack).getItemStack().clone());
		}
	}


	private interface Excludes
	{
		org.bukkit.Material getType();
	}
	@Delegate(excludes =
	{
		Excludes.class
	})
	@Getter
	private final org.bukkit.inventory.ItemStack itemStack;

	public BukkitItemStack(final org.bukkit.inventory.ItemStack itemStack)
	{
		super();
		this.itemStack = itemStack;
	}
	
	@Override
	public void addEnchantment(Enchantment enchantment, int level)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Material getType()
	{
		return Material.get(itemStack.getTypeId());
	}

	@Override
	public boolean isAir()
	{
		return itemStack.getTypeId() == 0;
	}
}
