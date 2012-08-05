package com.earth2me.essentials.signs;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.Commandrepair;


public class SignRepair extends EssentialsSign
{
	public SignRepair()
	{
		super("Repair");
	}

	@Override
	protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException
	{
		final String repairTarget = sign.getLine(1);
		if (repairTarget.isEmpty())
		{
			sign.setLine(1, "Hand");
		}
		else if (!repairTarget.equalsIgnoreCase("all") && !repairTarget.equalsIgnoreCase("hand") )
		{
			throw new SignException(_("invalidSignLine", 2));
		}		
		validateTrade(sign, 2, ess);
		return true;
	}

	@Override
	protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException
	{
		final Trade charge = getTrade(sign, 2, ess);
		charge.isAffordableFor(player);
		
		Commandrepair command = new Commandrepair();
		command.setEssentials(ess);
		String[] args = new String[]
		{
			sign.getLine(1)
		};
		try
		{
			command.run(ess.getServer(), player, "repair", args);
		}
		catch (Exception ex)
		{
			throw new SignException(ex.getMessage(), ex);
		}
		charge.charge(player);					
		return true;
	}
}
