package com.earth2me.essentials.chat;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerChatEvent;


public class EssentialsChatPlayerListenerHighest extends EssentialsChatPlayer
{
	public EssentialsChatPlayerListenerHighest(final Server server,
											   final IEssentials ess,
											   final Map<String, IEssentialsChatListener> listeners,
											   final Map<PlayerChatEvent, ChatStore> chatStorage)
	{
		super(server, ess, listeners, chatStorage);
	}

	@Override
	public void onPlayerChat(final PlayerChatEvent event)
	{
		final ChatStore chatStore = delChatStore(event);
		if (isAborted(event))
		{
			return;
		}

		/**
		 * This file should handle charging the user for the action before returning control back
		 */
		charge(event, chatStore);
	}
}
