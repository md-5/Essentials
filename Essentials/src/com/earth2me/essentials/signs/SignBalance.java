package com.earth2me.essentials.signs;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;


public class SignBalance extends EssentialsSign
{
	public SignBalance()
	{
		super("Balance");
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		player.sendMessage(_("balance", player.getMoney()));
		return true;
	}
}
