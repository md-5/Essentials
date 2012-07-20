package net.ess3.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static net.ess3.I18n._;
import net.ess3.api.server.CommandSender;
import net.ess3.api.server.Location;
import net.ess3.api.server.Player;
import net.ess3.api.server.World;
import org.bukkit.entity.TNTPrimed;


public class Commandnuke extends EssentialsCommand
{
	@Override
	protected void run(final CommandSender sender, final String commandLabel, final String[] args) throws NoSuchFieldException, NotEnoughArgumentsException
	{
		List<Player> targets;
		if (args.length > 0)
		{
			targets = new ArrayList<Player>();
			int pos = 0;
			for (String arg : args)
			{
				targets.add(getPlayer(args, pos));
				pos++;
			}
		}
		else
		{
			targets = Arrays.asList(server.getOnlinePlayers());
		}
		ess.getTNTListener().enable();
		for (Player player : targets)
		{
			if (player == null)
			{
				continue;
			}
			player.sendMessage(_("nuke"));
			final Location loc = player.getLocation();
			final World world = loc.getWorld();
			for (int x = -10; x <= 10; x += 5)
			{
				for (int z = -10; z <= 10; z += 5)
				{
					final Location tntloc = Location(world, loc.getBlockX() + x, world.getMaxHeight(), loc.getBlockZ() + z);
					final TNTPrimed tnt = world.spawn(tntloc, TNTPrimed.class);
				}
			}
		}
	}
}
