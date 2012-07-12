package net.ess3.permissions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.ess3.api.IPermission;
import org.bukkit.permissions.PermissionDefault;


public class KitPermissions
{
	private static Map<String, IPermission> permissions = new HashMap<String, IPermission>();

	public static IPermission getPermission(final String kitName)
	{
		IPermission perm = permissions.get(kitName);
		if (perm == null)
		{
			perm = new BasePermission("essentials.kit.", kitName.toLowerCase(Locale.ENGLISH))
			{
				@Override
				public PermissionDefault getPermissionDefault()
				{
					return PermissionDefault.TRUE;
				}
			};
			permissions.put(kitName, perm);
		}
		return perm;
	}
}
