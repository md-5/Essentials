package net.ess3.utils;

import de.bananaco.bpermissions.imp.Permissions;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Pattern;
import lombok.Cleanup;
import static net.ess3.I18n._;
import net.ess3.api.IEssentials;
import net.ess3.api.ISettings;
import net.ess3.api.IUser;

public class FormatUtil {
	static final transient Pattern REPLACE_COLOR_PATTERN = Pattern.compile("&([0-9a-f])");
	static final transient Pattern REPLACE_MAGIC_PATTERN = Pattern.compile("&(k)");
	static final transient Pattern REPLACE_PATTERN = Pattern.compile("&([0-9a-fk-or])");
	static final transient Pattern VANILLA_PATTERN = Pattern.compile("\u00a7+[0-9A-FK-ORa-fk-or]");
	static final transient Pattern VANILLA_COLOR_PATTERN = Pattern.compile("\u00a7+[0-9A-Fa-f]");
	static final transient Pattern REPLACE_FORMAT_PATTERN = Pattern.compile("&([l-or])");
	static final transient Pattern VANILLA_FORMAT_PATTERN = Pattern.compile("\u00a7+[L-ORl-or]");
	static final transient Pattern VANILLA_MAGIC_PATTERN = Pattern.compile("\u00a7+[Kk]");
	static final transient Pattern URL_PATTERN = Pattern.compile("((?:(?:https?)://)?[\\w-_\\.]{2,})\\.([a-z]{2,3}(?:/\\S+)?)");
	static DecimalFormat dFormat = new DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.US));

	static String stripColor(final String input, final Pattern pattern)
	{
		return pattern.matcher(input).replaceAll("");
	}

	public static String stripColor(final String input)
	{
		if (input == null)
		{
			return null;
		}
		return VANILLA_COLOR_PATTERN.matcher(input).replaceAll("");
	}

	public static String blockURL(final String input)
	{
		if (input == null)
		{
			return null;
		}
		String text = URL_PATTERN.matcher(input).replaceAll("$1 $2");
		while (URL_PATTERN.matcher(text).find())
		{
			text = URL_PATTERN.matcher(text).replaceAll("$1 $2");
		}
		return text;
	}

	static String replaceColor(final String input, final Pattern pattern)
	{
		return pattern.matcher(input).replaceAll("\u00a7$1");
	}

	public static String stripFormat(final String input)
	{
		if (input == null)
		{
			return null;
		}
		return VANILLA_PATTERN.matcher(input).replaceAll("");
	}

	public static String replaceFormat(final String input)
	{
		if (input == null)
		{
			return null;
		}
		return REPLACE_PATTERN.matcher(input).replaceAll("\u00a7$1");
	}

	public static String formatString(final IUser user, final String permBase, final String input)
	{
		if (input == null)
		{
			return null;
		}
		String message;
		if (Permissions.hasPermission(user.getPlayer(), permBase + ".color"))
		{
			message = replaceColor(input, REPLACE_COLOR_PATTERN);
		}
		else
		{
			message = stripColor(input, VANILLA_COLOR_PATTERN);
		}
		if (Permissions.hasPermission(user.getPlayer(), permBase + ".magic"))
		{
			message = replaceColor(message, REPLACE_MAGIC_PATTERN);
		}
		else
		{
			message = stripColor(message, VANILLA_MAGIC_PATTERN);
		}
		if (Permissions.hasPermission(user.getPlayer(), permBase + ".format"))
		{
			message = replaceColor(message, REPLACE_FORMAT_PATTERN);
		}
		else
		{
			message = stripColor(message, VANILLA_FORMAT_PATTERN);
		}
		return message;
	}

	public static String formatMessage(final IUser user, final String permBase, final String input)
	{
		if (input == null)
		{
			return null;
		}
		String message = formatString(user, permBase, input);
		if (!Permissions.hasPermission(user.getPlayer(), permBase + ".url"))
		{
			message = blockURL(message);
		}
		return message;
	}

	public static String shortCurrency(final double value, final IEssentials ess)
	{
		@Cleanup
		final ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		return settings.getData().getEconomy().getCurrencySymbol() + formatAsCurrency(value);
	}

	public static String displayCurrency(final double value, final IEssentials ess)
	{
		@Cleanup
		final ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		return _("currency", settings.getData().getEconomy().getCurrencySymbol(), formatAsCurrency(value));
	}

	public static String formatAsCurrency(final double value)
	{
		String str = dFormat.format(value);
		if (str.endsWith(".00"))
		{
			str = str.substring(0, str.length() - 3);
		}
		return str;
	}

}
