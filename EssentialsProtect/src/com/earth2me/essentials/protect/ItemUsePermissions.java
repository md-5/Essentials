package com.earth2me.essentials.protect;

import com.earth2me.essentials.api.IPermission;
import com.earth2me.essentials.permissions.AbstractSuperpermsPermission;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.permissions.PermissionDefault;

public class ItemUsePermissions extends AbstractSuperpermsPermission{
	private static Map<Material,IPermission> permissions = new EnumMap<Material, IPermission>(Material.class);
	private static final String base = "essentials.protect.itemuse.";
	private final String permission;
	
	public static IPermission getPermission(Material mat)
	{
		IPermission perm = permissions.get(mat);
		if (perm == null) {
			perm = new ItemUsePermissions(mat.toString().toLowerCase(Locale.ENGLISH));
			permissions.put(mat, perm);
		}
		return perm;
	}

	private ItemUsePermissions(String matName)
	{
		this.permission = base + matName;
	}
	
	@Override
	public String getPermission()
	{
		return this.permission;
	}

	@Override
	public PermissionDefault getPermissionDefault()
	{
		return PermissionDefault.TRUE;
	}
}
