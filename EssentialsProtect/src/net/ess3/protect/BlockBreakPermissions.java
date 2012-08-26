package net.ess3.protect;

import net.ess3.api.IPermission;
import net.ess3.permissions.AbstractSuperpermsPermission;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.permissions.PermissionDefault;


public final class BlockBreakPermissions extends AbstractSuperpermsPermission
{
	private static Map<Material, IPermission> permissions = new EnumMap<Material, IPermission>(Material.class);
	private static final String base = "essentials.protect.blockbreak.";
	private final String permission;

	public static IPermission getPermission(final Material mat)
	{
		IPermission perm = permissions.get(mat);
		if (perm == null)
		{
			perm = new BlockBreakPermissions(mat.toString().toLowerCase(Locale.ENGLISH));
			permissions.put(mat, perm);
		}
		return perm;
	}

	private BlockBreakPermissions(final String matName)
	{
		super();
		this.permission = base + matName;
	}

	@Override
	public String getPermissionName()
	{
		return this.permission;
	}

	@Override
	public PermissionDefault getPermissionDefault()
	{
		return PermissionDefault.TRUE;
	}
}
