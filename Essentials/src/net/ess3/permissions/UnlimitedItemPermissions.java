package net.ess3.permissions;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import net.ess3.api.IPermission;
import org.bukkit.Material;

public class UnlimitedItemPermissions
{
	private static Map<Material, IPermission> permissions = new EnumMap<Material, IPermission>(Material.class);

	public static IPermission getPermission(final Material mat)
	{
		IPermission perm = permissions.get(mat);
		if (perm == null)
		{
			perm = new BasePermission("essentials.unlimited.item-", mat.toString().toLowerCase(Locale.ENGLISH).replace("_", ""));
			permissions.put(mat, perm);
		}
		return perm;
	}
}
