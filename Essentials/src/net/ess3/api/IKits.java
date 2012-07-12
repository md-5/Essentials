package net.ess3.api;

import java.util.Collection;
import net.ess3.commands.NoChargeException;
import net.ess3.settings.Kit;


public interface IKits extends IReload
{
	Kit getKit(String kit) throws Exception;

	void sendKit(IUser user, String kit) throws Exception;

	void sendKit(IUser user, Kit kit) throws Exception;

	Collection<String> getList() throws Exception;

	boolean isEmpty();
	
	void checkTime(final IUser user, Kit kit) throws NoChargeException;
}
