package net.ess3.settings;

import net.ess3.storage.Location;
import net.ess3.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Warp implements StorageObject
{
	private String name;
	private Location location;
}
