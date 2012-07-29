package net.ess3.api.server;

import lombok.Delegate;

public class Block {

	public Block(ItemStack stack, Location location)
	{
		this.stack = stack;
		this.location = location;
	}
	
	
	@Delegate
	private final ItemStack stack;
	@Delegate
	private final Location location;

	public ItemStack convertToItem()
	{
		final ItemStack is = ItemStack.create(this.getType(), 1, this.getDurability());
		return this.getType().convertToItem(is);
	}
}
