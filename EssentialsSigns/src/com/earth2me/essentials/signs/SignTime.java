package com.earth2me.essentials.signs;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.ChargeException;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.economy.Trade;


public class SignTime extends EssentialsSign
{
	public SignTime()
	{
		super("Time");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final IUser player, final String username, final IEssentials ess) throws SignException
	{
		validateTrade(sign, 2, ess);
		final String timeString = sign.getLine(1);
		if ("Day".equalsIgnoreCase(timeString))
		{
			sign.setLine(1, "§2Day");
			return true;
		}
		if ("Night".equalsIgnoreCase(timeString))
		{
			sign.setLine(1, "§2Night");
			return true;
		}
		throw new SignException(_("onlyDayNight"));
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final IUser player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		final Trade charge = getTrade(sign, 2, ess);
		charge.isAffordableFor(player);
		final String timeString = sign.getLine(1);
		long time = player.getWorld().getTime();
		time -= time % 24000;
		if ("§2Day".equalsIgnoreCase(timeString))
		{
			player.getWorld().setTime(time + 24000);
			charge.charge(player);
			return true;
		}
		if ("§2Night".equalsIgnoreCase(timeString))
		{
			player.getWorld().setTime(time + 37700);
			charge.charge(player);
			return true;
		}
		throw new SignException(_("onlyDayNight"));
	}
}
