package com.earth2me.essentials.update.chat;

import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jibble.pircbot.Colors;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;


public class IrcBot extends PircBot
{
	private static final String CHANNEL = "#essentials";
	private static final int PORT = 6667;
	private static final String SERVER = "irc.esper.net";
	private transient boolean reconnect = true;
	private final transient Player player;
	private transient boolean kicked = false;

	public IrcBot(final Player player, final String nickName, final String versionString)
	{
		super();
		this.player = player;
		setName(nickName);
		setLogin("esshelp");
		setVersion(versionString);
		connect();
		joinChannel(CHANNEL);
	}

	private void connect()
	{
		try
		{
			connect(SERVER, PORT);
		}
		catch (IOException ex)
		{
			Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
		}
		catch (IrcException ex)
		{
			Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	public void quit()
	{
		reconnect = false;
		disconnect();
	}

	@Override
	protected void onConnect()
	{
		reconnect = true;
	}

	@Override
	protected void onDisconnect()
	{
		if (reconnect)
		{
			int tries = 10;
			while (!isConnected())
			{
				try
				{
					tries--;
					reconnect();
				}
				catch (Exception e)
				{
					Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
					try
					{
						Thread.sleep(10000);
					}
					catch (InterruptedException ex)
					{
						Bukkit.getLogger().log(Level.WARNING, e.getMessage(), e);
					}
				}
				if (tries <= 0)
				{
					player.sendMessage("Connection lost to server.");
					kicked = true;
					break;
				}
			}
		}
	}

	@Override
	protected void onKick(final String channel, final String kickerNick,
						  final String kickerLogin, final String kickerHostname,
						  final String recipientNick, final String reason)
	{
		if (recipientNick.equals(getNick()))
		{
			player.sendMessage("You have been kicked from the channel: " + reason);
			quit();
			kicked = true;
		}
	}

	public boolean isKicked()
	{
		return kicked;
	}

	@Override
	protected void onMessage(final String channel, final String sender,
							 final String login, final String hostname,
							 final String message)
	{
		player.sendMessage(formatChatMessage(sender, message, false));
	}

	@Override
	protected void onAction(final String sender, final String login,
							final String hostname, final String target,
							final String action)
	{
		player.sendMessage(formatChatMessage(sender, action, true));
	}

	@Override
	protected void onNotice(final String sourceNick, final String sourceLogin,
							final String sourceHostname, final String target,
							final String notice)
	{
		player.sendMessage(formatChatMessage(sourceNick, notice, false));
	}

	@Override
	protected void onTopic(final String channel, final String topic,
						   final String setBy, final long date,
						   final boolean changed)
	{
		player.sendMessage(formatChatMessage(channel, topic, false));
	}

	public String formatChatMessage(final String nick, final String message, final boolean action)
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("§6");
		if (action)
		{
			builder.append('*');
		}
		builder.append(nick);
		if (!action)
		{
			builder.append(':');
		}
		builder.append(" §7");
		builder.append(replaceColors(message));
		return builder.toString();
	}

	private String replaceColors(final String message)
	{
		String m = Colors.removeFormatting(message);
		m = m.replaceAll("\u000310(,(0?[0-9]|1[0-5]))?", "§b");
		m = m.replaceAll("\u000311(,(0?[0-9]|1[0-5]))?", "§f");
		m = m.replaceAll("\u000312(,(0?[0-9]|1[0-5]))?", "§9");
		m = m.replaceAll("\u000313(,(0?[0-9]|1[0-5]))?", "§d");
		m = m.replaceAll("\u000314(,(0?[0-9]|1[0-5]))?", "§8");
		m = m.replaceAll("\u000315(,(0?[0-9]|1[0-5]))?", "§7");
		m = m.replaceAll("\u00030?1(,(0?[0-9]|1[0-5]))?", "§0");
		m = m.replaceAll("\u00030?2(,(0?[0-9]|1[0-5]))?", "§1");
		m = m.replaceAll("\u00030?3(,(0?[0-9]|1[0-5]))?", "§2");
		m = m.replaceAll("\u00030?4(,(0?[0-9]|1[0-5]))?", "§c");
		m = m.replaceAll("\u00030?5(,(0?[0-9]|1[0-5]))?", "§4");
		m = m.replaceAll("\u00030?6(,(0?[0-9]|1[0-5]))?", "§5");
		m = m.replaceAll("\u00030?7(,(0?[0-9]|1[0-5]))?", "§6");
		m = m.replaceAll("\u00030?8(,(0?[0-9]|1[0-5]))?", "§e");
		m = m.replaceAll("\u00030?9(,(0?[0-9]|1[0-5]))?", "§a");
		m = m.replaceAll("\u00030?0(,(0?[0-9]|1[0-5]))?", "§f");
		m = m.replace("\u000f", "§7");
		m = Colors.removeColors(m);
		return m;
	}

	public void sendMessage(final String message)
	{
		sendMessage(CHANNEL, message);
	}

	public User[] getUsers()
	{
		return getUsers(CHANNEL);
	}
}
