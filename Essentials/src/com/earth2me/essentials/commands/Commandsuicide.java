package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandsuicide extends EssentialsCommand
{
	public Commandsuicide()
	{
		super("suicide");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		charge(user);
		user.setHealth(0);
		user.sendMessage(Util.i18n("suicideMessage"));
		ess.broadcastMessage(user.getName(),
								Util.format("suicideSuccess",user.getDisplayName()));
	}
}
