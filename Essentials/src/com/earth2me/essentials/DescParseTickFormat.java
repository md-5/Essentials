package com.earth2me.essentials;

import com.earth2me.essentials.commands.Commandtime;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This utility class is used for converting between the ingame
 * time in ticks to ingame time as a friendly string.
 * Note that the time is INGAME.
 * 
 * http://www.minecraftwiki.net/wiki/Day/night_cycle
 * 
 * @author Olof Larsson
 */
public class DescParseTickFormat
{
	// ============================================
	// First some information vars. TODO: Should this be in a config file?
	// --------------------------------------------
	public static final Map<String, Integer> nameToTicks = new LinkedHashMap<String, Integer>();
	public static final Set<String> resetAliases = new HashSet<String>();
	public static final int ticksAtMidnight = 18000;
	public static final int ticksPerDay = 24000;
	public static final int ticksPerHour = 1000;
	public static final double ticksPerMinute = 1000d / 60d;
	public static final double ticksPerSecond = 1000d / 60d / 60d;
	private static final SimpleDateFormat SDFTwentyFour = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
	private static final SimpleDateFormat SDFTwelve = new SimpleDateFormat("h:mmaa", Locale.ENGLISH);

	static
	{

		nameToTicks.put("sunrise", 22000);
		nameToTicks.put("rise", 22000);
		nameToTicks.put("dawn", 22000);

		nameToTicks.put("daystart", 0);
		nameToTicks.put("day", 0);

		nameToTicks.put("morning", 3000);

		nameToTicks.put("midday", 6000);
		nameToTicks.put("noon", 6000);

		nameToTicks.put("afternoon", 9000);

		nameToTicks.put("sunset", 12000);
		nameToTicks.put("set", 12000);
		nameToTicks.put("dusk", 12000);
		nameToTicks.put("sundown", 12000);
		nameToTicks.put("nightfall", 12000);

		nameToTicks.put("nightstart", 14000);
		nameToTicks.put("night", 14000);

		nameToTicks.put("midnight", 18000);

		resetAliases.add("reset");
		resetAliases.add("normal");
		resetAliases.add("default");
	}

	// ============================================
	// PARSE. From describing String to int
	// --------------------------------------------
	public static long parse(String desc) throws NumberFormatException
	{
		Long ret;

		// Only look at alphanumeric and lowercase
		desc = desc.toLowerCase().replaceAll("[^A-Za-z0-9]", "");

		// Detect ticks format
		try
		{
			return parseTicks(desc);
		}
		catch (Exception e)
		{
		}

		// Detect 24-hour format
		try
		{
			return parse24(desc);
		}
		catch (Exception e)
		{
		}

		// Detect 12-hour format
		try
		{
			return parse12(desc);
		}
		catch (Exception e)
		{
		}

		// Detect aliases
		try
		{
			return parseAlias(desc);
		}
		catch (Exception e)
		{
		}

		// Well we failed to understand...
		throw new NumberFormatException();
	}

	public static long parseTicks(String desc) throws NumberFormatException
	{
		if (!desc.matches("^[0-9]+ti?c?k?s?$"))
		{
			throw new NumberFormatException();
		}

		desc = desc.replaceAll("[^0-9]", "");

		return Long.parseLong(desc) % 24000;
	}

	public static long parse24(String desc) throws NumberFormatException
	{
		if (!desc.matches("^[0-9]{2}[^0-9]?[0-9]{2}$"))
		{
			throw new NumberFormatException();
		}

		desc = desc.toLowerCase().replaceAll("[^0-9]", "");

		if (desc.length() != 4)
		{
			throw new NumberFormatException();
		}

		int hours = Integer.parseInt(desc.substring(0, 2));
		int minutes = Integer.parseInt(desc.substring(2, 4));

		return hoursMinutesToTicks(hours, minutes);
	}

	public static long parse12(String desc) throws NumberFormatException
	{
		if (!desc.matches("^[0-9]{1,2}([^0-9]?[0-9]{2})?(pm|am)$"))
		{
			throw new NumberFormatException();
		}

		int hours = 0;
		int minutes = 0;
		if (desc.endsWith("pm"))
		{
			hours += 12;
		}

		desc = desc.toLowerCase().replaceAll("[^0-9]", "");

		if (desc.length() > 4)
		{
			throw new NumberFormatException();
		}

		if (desc.length() == 4)
		{
			hours += Integer.parseInt(desc.substring(0, 2));
			minutes += Integer.parseInt(desc.substring(2, 4));
		}
		else if (desc.length() == 3)
		{
			hours += Integer.parseInt(desc.substring(0, 1));
			minutes += Integer.parseInt(desc.substring(1, 3));
		}
		else if (desc.length() == 2)
		{
			hours += Integer.parseInt(desc.substring(0, 2));
		}
		else if (desc.length() == 1)
		{
			hours += Integer.parseInt(desc.substring(0, 1));
		}
		else
		{
			throw new NumberFormatException();
		}

		return hoursMinutesToTicks(hours, minutes);
	}

	public static long hoursMinutesToTicks(int hours, int minutes)
	{
		long ret = ticksAtMidnight;
		ret += (hours - 1) * ticksPerHour;

		ret += (minutes / 60.0) * ticksPerHour;

		ret %= ticksPerDay;
		return ret;
	}

	public static long parseAlias(String desc) throws NumberFormatException
	{
		Integer ret = nameToTicks.get(desc);
		if (ret == null)
		{
			throw new NumberFormatException();
		}

		return ret;
	}

	public static boolean meansReset(String desc)
	{
		return resetAliases.contains(desc);
	}

	// ============================================
	// FORMAT. From int to describing String
	// --------------------------------------------
	public static String format(long ticks)
	{
		StringBuilder msg = new StringBuilder();
		msg.append(Commandtime.colorHighlight1);
		msg.append(format24(ticks));
		msg.append(Commandtime.colorDefault);
		msg.append(" or ");
		msg.append(Commandtime.colorHighlight1);
		msg.append(format12(ticks));
		msg.append(Commandtime.colorDefault);
		msg.append(" or ");
		msg.append(Commandtime.colorHighlight1);
		msg.append(formatTicks(ticks));
		return msg.toString();
	}

	public static String formatTicks(long ticks)
	{
		return "" + ticks % ticksPerDay + "ticks";
	}

	public static String format24(long ticks)
	{
		return formatDateFormat(ticks, SDFTwentyFour);
	}

	public static String format12(long ticks)
	{
		return formatDateFormat(ticks, SDFTwelve);
	}

	public static String formatDateFormat(long ticks, SimpleDateFormat format)
	{
		Date date = ticksToDate(ticks);
		return format.format(date);
	}

	public static Date ticksToDate(long ticks)
	{
		// Assume the server time starts at 0. It would start on a day.
		// But we will simulate that the server started with 0 at midnight.
		ticks = ticks - ticksAtMidnight + ticksPerDay;

		// How many ingame days have passed since the server start?
		long days = ticks / ticksPerDay;
		ticks = ticks - days * ticksPerDay;

		// How many hours on the last day?
		long hours = ticks / ticksPerHour;
		ticks = ticks - hours * ticksPerHour;

		// How many minutes on the last day?
		long minutes = (long)Math.floor(ticks / ticksPerMinute);
		double dticks = ticks - minutes * ticksPerMinute;

		// How many seconds on the last day?
		long seconds = (long)Math.floor(dticks / ticksPerSecond);

		// Now we create an english GMT calendar (We wan't no daylight savings)
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.ENGLISH);
		cal.setLenient(true);

		// And we set the time to 0! And append the time that passed!
		cal.set(0, Calendar.JANUARY, 1, 0, 0, 0);
		cal.add(Calendar.DAY_OF_YEAR, (int)days);
		cal.add(Calendar.HOUR_OF_DAY, (int)hours);
		cal.add(Calendar.MINUTE, (int)minutes);
		cal.add(Calendar.SECOND, (int)seconds + 1); // To solve rounding errors.

		return cal.getTime();
	}
}
