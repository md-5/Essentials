package com.earth2me.essentials.signs;

import com.earth2me.essentials.IEssentials;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EndermanPickupEvent;
import org.bukkit.event.entity.EntityExplodeEvent;


public class SignEntityListener implements Listener
{
	private final transient IEssentials ess;

	public SignEntityListener(final IEssentials ess)
	{
		this.ess = ess;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityExplode(final EntityExplodeEvent event)
	{
		for (Block block : event.blockList())
		{
			if (((block.getType() == Material.WALL_SIGN
				  || block.getType() == Material.SIGN_POST)
				 && EssentialsSign.isValidSign(new EssentialsSign.BlockSign(block)))
				|| EssentialsSign.checkIfBlockBreaksSigns(block))
			{
				event.setCancelled(true);
				return;
			}
			for (Signs signs : Signs.values())
			{
				final EssentialsSign sign = signs.getSign();
				if (sign.getBlocks().contains(block.getType()))
				{
					event.setCancelled(!sign.onBlockExplode(block, ess));
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEndermanPickup(final EndermanPickupEvent event)
	{
		if (event.isCancelled() || ess.getSettings().areSignsDisabled())
		{
			return;
		}

		final Block block = event.getBlock();
		if (((block.getType() == Material.WALL_SIGN
			  || block.getType() == Material.SIGN_POST)
			 && EssentialsSign.isValidSign(new EssentialsSign.BlockSign(block)))
			|| EssentialsSign.checkIfBlockBreaksSigns(block))
		{
			event.setCancelled(true);
			return;
		}
		for (Signs signs : Signs.values())
		{
			final EssentialsSign sign = signs.getSign();
			if (sign.getBlocks().contains(block.getType())
				&& !sign.onBlockBreak(block, ess))
			{
				event.setCancelled(true);
				return;
			}
		}
	}
}
