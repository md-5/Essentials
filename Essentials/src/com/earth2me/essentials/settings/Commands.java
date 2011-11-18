package com.earth2me.essentials.settings;

import com.earth2me.essentials.settings.commands.*;
import com.earth2me.essentials.storage.Comment;
import com.earth2me.essentials.storage.ListType;
import com.earth2me.essentials.storage.StorageObject;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class Commands extends StorageObject
{
	private Afk afk = new Afk();
	private God god = new God();
	private Help help = new Help();
	private Home home = new Home();
	private Kit kit = new Kit();
	private Lightning lightning = new Lightning();
	private Spawnmob spawnmob = new Spawnmob();
	@ListType
	@Comment(
	{
		"When a command conflicts with another plugin, by default, Essentials will try to force the OTHER plugin to take",
		"priority.  If a command is in this list, Essentials will try to give ITSELF priority.  This does not always work:",
		"usually whichever plugin was updated most recently wins out.  However, the full name of the command will always work.",
		"For example, if WorldGuard and Essentials are both enabled, and WorldGuard takes control over /god, /essentials:god",
		"will still map to Essentials, whereas it might normally get forced upon WorldGuard.  Commands prefixed with an \"e\",",
		"such as /egod, will always grant Essentials priority.",
		"We should try to take priority over /god.  If this doesn't work, use /essentials:god or /egod.",
		"If god is set using WorldGuard, use /ungod to remove then use whichever you see fit."
	})
	private List<String> overwritten = new ArrayList<String>();
	
	@ListType
	@Comment("Disabled commands will be completelly unavailable on the server.")
	private List<String> disabled = new ArrayList<String>();
}
