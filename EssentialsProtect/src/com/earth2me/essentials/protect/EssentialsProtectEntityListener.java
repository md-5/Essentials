package com.earth2me.essentials.protect;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.server.ChunkPosition;
import net.minecraft.server.Packet60Explosion;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;


public class EssentialsProtectEntityListener extends EntityListener
{
	private EssentialsProtect parent;

	public EssentialsProtectEntityListener(EssentialsProtect parent)
	{
		Essentials.loadClasses();
		this.parent = parent;
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (event.isCancelled()) return;
		if (event instanceof EntityDamageByBlockEvent)
		{
			DamageCause cause = event.getCause();

			if (EssentialsProtect.playerSettings.get("protect.disable.contactdmg") && cause == DamageCause.CONTACT)
			{
				event.setCancelled(true);
				return;
			}
			if (EssentialsProtect.playerSettings.get("protect.disable.lavadmg") && cause == DamageCause.LAVA)
			{
				event.setCancelled(true);
				return;
			}
			if (EssentialsProtect.guardSettings.get("protect.prevent.tnt-explosion") && cause == DamageCause.BLOCK_EXPLOSION)
			{
				event.setCancelled(true);
				return;
			}
		}

		if (event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent edEvent = (EntityDamageByEntityEvent)event;
			Entity eAttack = edEvent.getDamager();
			Entity eDefend = edEvent.getEntity();

			// PVP Settings
			if (eDefend instanceof Player && eAttack instanceof Player)
			{
				if (EssentialsProtect.playerSettings.get("protect.disable.pvp"))
				{
					User defender = User.get(eDefend);
					User attacker = User.get(eAttack);

					if (!defender.isAuthorized("essentials.protect.pvp") || !attacker.isAuthorized("essentials.protect.pvp"))
					{
						event.setCancelled(true);
						return;
					}
				}
			}
			//Creeper explode prevention
			if (eAttack != null && eAttack instanceof Monster)
			{
				if (eAttack instanceof Creeper && EssentialsProtect.guardSettings.get("protect.prevent.creeper-explosion"))
				{
					event.setCancelled(true);
					return;
				}
				
				if (eAttack instanceof Creeper && EssentialsProtect.guardSettings.get("protect.prevent.creeper-playerdamage"))
				{
					event.setCancelled(true);
					return;
				}
			}
		}

		if (event instanceof EntityDamageByProjectileEvent)
		{
			if (event.getEntity() instanceof Player)
			{
				event.setCancelled(EssentialsProtect.playerSettings.get("protect.disable.projectiles"));
				return;
			}
		}

		DamageCause cause = event.getCause();
		Entity casualty = event.getEntity();
		if (casualty instanceof Player)
		{
			if (EssentialsProtect.playerSettings.get("protect.disable.fall") && cause == DamageCause.FALL)
			{
				event.setCancelled(true);
				return;
			}

			if (EssentialsProtect.playerSettings.get("protect.disable.suffocate") && cause == DamageCause.SUFFOCATION)
			{
				event.setCancelled(true);
				return;
			}
			if (EssentialsProtect.playerSettings.get("protect.disable.firedmg") && (cause == DamageCause.FIRE
																					|| cause == DamageCause.FIRE_TICK))
			{
				event.setCancelled(true);
				return;
			}
			if (EssentialsProtect.playerSettings.get("protect.disable.drown") && cause == DamageCause.DROWNING)
			{
				event.setCancelled(true);
				return;
			}
		}
	}

	@Override
	public void onEntityExplode(EntityExplodeEvent event)
	{
		if (event.isCancelled()) return;
		if (event.getEntity() instanceof LivingEntity)
		{
			//Nicccccccccce plaaacccccccccce..
			int maxHeight = Essentials.getSettings().getEpCreeperMaxHeight();
			if (	EssentialsProtect.guardSettings.get("protect.prevent.creeper-explosion") ||
				EssentialsProtect.guardSettings.get("protect.prevent.creeper-blockdamage") || 
				(maxHeight >= 0 && event.getLocation().getBlockY() > maxHeight))
			{
				HashSet<ChunkPosition> set = new HashSet<ChunkPosition>(event.blockList().size());
				Player[] players = parent.getServer().getOnlinePlayers();
				List<ChunkPosition> blocksUnderPlayers = new ArrayList<ChunkPosition>(players.length);
				Location loc = event.getLocation();
				for (Player player : players) {
					if (player.getWorld().equals(loc.getWorld())) {
						blocksUnderPlayers.add(
							new ChunkPosition(
								player.getLocation().getBlockX(),
								player.getLocation().getBlockY() - 1,
								player.getLocation().getBlockZ()));
					}
				}
				for (Block block : event.blockList()) {
					ChunkPosition cp = new ChunkPosition(block.getX(), block.getY(), block.getZ());
					if (!blocksUnderPlayers.contains(cp)) {
						set.add(cp);
					}
				}
				
				((CraftServer)parent.getServer()).getServer().f.a(loc.getX(), loc.getY(), loc.getZ(), 64.0D, 
					new Packet60Explosion(loc.getX(), loc.getY(), loc.getZ(), 3.0f, set));
				event.setCancelled(true);
				return;
			}
		}
		else
		{ //OH NOES TNT
			if (EssentialsProtect.guardSettings.get("protect.prevent.tnt-explosion"))
			{
				event.setCancelled(true);
				return;
			}
		}
	}

	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		String creatureName = event.getCreatureType().toString().toLowerCase();
		if (creatureName == null || creatureName.isEmpty()) {
			return;
		}
		if (EssentialsProtect.guardSettings.get("protect.prevent.spawn."+creatureName)) {
			event.setCancelled(true);
		}
	}
}
