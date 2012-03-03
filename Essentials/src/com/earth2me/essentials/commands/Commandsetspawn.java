package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.settings.SpawnsHolder;


public class Commandsetspawn extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		final String group = args.length > 0 ? getFinalArg(args, 0) : "default";
		((SpawnsHolder)module).setSpawn(user.getLocation(), group);
		user.sendMessage(_("spawnSet", group));
	}
}
