package net.ess3.chat;

import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import net.ess3.economy.Trade;


public class ChatStore
{
	private final transient IUser user;
	private final transient String type;
	private final transient Trade charge;

	public ChatStore(final IEssentials ess, final IUser user, final String type)
	{
		this.user = user;
		this.type = type;
		this.charge = new Trade(getLongType(), ess);
	}

	public IUser getUser()
	{
		return user;
	}

	public Trade getCharge()
	{
		return charge;
	}

	public String getType()
	{
		return type;
	}

	public final String getLongType()
	{
		return type.length() == 0 ? "chat" : "chat-" + type;
	}
}
