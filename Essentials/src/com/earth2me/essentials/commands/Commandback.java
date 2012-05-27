package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.economy.Trade;
import com.earth2me.essentials.api.IUser;


public class Commandback extends EssentialsCommand
{
	@Override
	protected void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		if (user.getWorld() != user.getLastLocation().getWorld() && ess.getSettings().isWorldTeleportPermissions()
			&& !user.isAuthorized("essentials.world." + user.getLastLocation().getWorld().getName()))
		{
			throw new Exception(_("noPerm", "essentials.world." + user.getLastLocation().getWorld().getName()));
		}
		final Trade charge = new Trade(this.getName(), ess);
		charge.isAffordableFor(user);
		user.sendMessage(_("backUsageMsg"));
		user.getTeleport().back(charge);
		throw new NoChargeException();
	}
}
