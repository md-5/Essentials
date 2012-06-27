package net.ess3.signs;

import net.ess3.storage.StorageObject;
import java.util.HashMap;
import java.util.Map;


public class SignsConfig implements StorageObject
{
	private Map<String, Boolean> signs = new HashMap<String, Boolean>();

	public Map<String, Boolean> getSigns()
	{
		return signs;
	}

	public void setSigns(final Map<String, Boolean> signs)
	{
		this.signs = signs;
	}
}
