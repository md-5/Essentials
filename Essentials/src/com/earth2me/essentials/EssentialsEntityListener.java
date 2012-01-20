package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;


public class EssentialsEntityListener implements Listener
{
	private final IEssentials ess;

	public EssentialsEntityListener(IEssentials ess)
	{
		this.ess = ess;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent edEvent = (EntityDamageByEntityEvent)event;
			Entity eAttack = edEvent.getDamager();
			Entity eDefend = edEvent.getEntity();
			if (eDefend instanceof Player && eAttack instanceof Player)
			{
				User defender = ess.getUser(eDefend);
				User attacker = ess.getUser(eAttack);
				attacker.updateActivity(true);
				ItemStack is = attacker.getItemInHand();
				List<String> commandList = attacker.getPowertool(is);
				if (commandList != null && !commandList.isEmpty())
				{
					for (String command : commandList)
					{

						if (command != null && !command.isEmpty())
						{
							attacker.getServer().dispatchCommand(attacker, command.replaceAll("\\{player\\}", defender.getName()));
							event.setCancelled(true);
							return;
						}
					}
				}
			}
			if (eDefend instanceof Animals && eAttack instanceof Player)
			{
				User player = ess.getUser(eAttack);
				ItemStack hand = player.getItemInHand();
				if (hand != null && hand.getType() == Material.MILK_BUCKET)
				{
					((Animals)eDefend).setAge(-24000);
					hand.setType(Material.BUCKET);
					player.setItemInHand(hand);
					player.updateInventory();
					event.setCancelled(true);
				}
			}
		}
		if (event.getEntity() instanceof Player && ess.getUser(event.getEntity()).isGodModeEnabled())
		{
			final Player player = (Player)event.getEntity();
			player.setFireTicks(0);
			player.setRemainingAir(player.getMaximumAir());
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityCombust(EntityCombustEvent event)
	{
		if (event.getEntity() instanceof Player && ess.getUser(event.getEntity()).isGodModeEnabled())
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDeath(final EntityDeathEvent event)
	{
		if (event instanceof PlayerDeathEvent)
		{
			final PlayerDeathEvent pdevent = (PlayerDeathEvent)event;
			final User user = ess.getUser(pdevent.getEntity());
			if (user.isAuthorized("essentials.back.ondeath") && !ess.getSettings().isCommandDisabled("back"))
			{
				user.setLastLocation();
				user.sendMessage(_("backAfterDeath"));
			}
			if (!ess.getSettings().areDeathMessagesEnabled())
			{
				pdevent.setDeathMessage("");
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		if (event.getEntity() instanceof Player && ess.getUser(event.getEntity()).isGodModeEnabled())
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityRegainHealth(EntityRegainHealthEvent event)
	{
		if (event.getRegainReason() == RegainReason.SATIATED && event.getEntity() instanceof Player
			&& ess.getUser(event.getEntity()).isAfk() && ess.getSettings().getFreezeAfkPlayers())
		{
			event.setCancelled(true);
		}
	}
}
