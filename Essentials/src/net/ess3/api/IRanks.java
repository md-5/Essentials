package net.ess3.api;

import java.text.MessageFormat;


public interface IRanks
{
	String getMainGroup(IUser player);

	boolean inGroup(IUser player, String groupname);

	double getHealCooldown(IUser player);

	double getTeleportCooldown(IUser player);

	double getTeleportDelay(IUser player);

	String getPrefix(IUser player);

	String getSuffix(IUser player);

	int getHomeLimit(IUser player);

	MessageFormat getChatFormat(IUser player);
}
