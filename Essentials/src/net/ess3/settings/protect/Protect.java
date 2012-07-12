package net.ess3.settings.protect;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.ess3.storage.Comment;
import net.ess3.storage.ListType;
import net.ess3.storage.StorageObject;
import org.bukkit.Material;


@Data
@EqualsAndHashCode(callSuper = false)
public class Protect implements StorageObject
{
	@Comment("Either mysql or sqlite")
	private String dbtype = "sqlite";
	@Comment("If you specified MySQL above, you MUST enter the appropriate details here.")
	private String dbuser = "root";
	private String dbpassword = "";
	private String dburl = "jdbc:mysql://localhost:3306/minecraft";
	@Comment("For which block types would you like to be alerted?")
	@ListType(Material.class)
	private Set<Material> alertOnPlacement = new HashSet<Material>();
	@ListType(Material.class)
	private Set<Material> alertOnUse = new HashSet<Material>();
	@ListType(Material.class)
	private Set<Material> alertOnBreak = new HashSet<Material>();
	@Comment("General physics/behavior modifications")
	private Prevent prevent = new Prevent();
	@Comment(
	{
		"Maximum height the creeper should explode. -1 allows them to explode everywhere.",
		"Set prevent.creeper-explosion to true, if you want to disable creeper explosions."
	})
	private int creeperMaxHeight = -1;
	@Comment("Should we tell people they are not allowed to build")
	private boolean warnOnBuildDisallow = true;
	@Comment("Disable weather options")
	private boolean disableStorm = false;
	private boolean disableThunder = false;
	private boolean disableLighting = false;
	private SignsAndRails signsAndRails = new SignsAndRails();
	
	public Protect()
	{
		alertOnPlacement.add(Material.LAVA);
		alertOnUse.add(Material.LAVA_BUCKET);
		alertOnBreak.add(Material.GLASS);
	}
}
