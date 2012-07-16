package com.earth2me.essentials.chat;

import net.ess3.api.IPermission;
import net.ess3.permissions.BasePermission;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatPermissions {
	private static Map<String, IPermission> permissions = new HashMap<String, IPermission>();

	public static IPermission getPermission(final String groupName)
	{
		IPermission perm = permissions.get(groupName);
		if (perm == null)
		{
			perm = new BasePermission("essentials.chat.",groupName.toLowerCase(Locale.ENGLISH));
			permissions.put(groupName, perm);
		}
		return perm;
	}
}
