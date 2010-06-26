package pcgen.cdom.helper;

import pcgen.core.Globals;
import pcgen.core.PCClass;

public class SpellLevel implements Comparable<SpellLevel>
{

	public final PCClass pcc;
	public final int level;

	public SpellLevel(PCClass cl, int lvl)
	{
		pcc = cl;
		level = lvl;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("CLASS.");
		sb.append(pcc.getKeyName());
		sb.append(";LEVEL.");
		sb.append(level);
		return sb.toString();
	}

	public static SpellLevel decodeChoice(String s)
	{
		int loc = s.indexOf(";LEVEL.");
		String classString;
		String levelString;
		if (loc == -1)
		{
			/*
			 * Handle old persistence
			 */
			int spaceLoc = s.indexOf(' ');
			classString = s.substring(0, spaceLoc);
			levelString = s.substring(spaceLoc + 1);
		}
		else
		{
			String classText = s.substring(0, 6);
			if (!classText.equals("CLASS."))
			{
				return null;
			}
			classString = s.substring(6, loc);
			levelString = s.substring(loc + 7);
		}
		PCClass cl =
				Globals.getContext().ref.silentlyGetConstructedCDOMObject(
					PCClass.class, classString);
		try
		{
			int level = Integer.parseInt(levelString);
			return new SpellLevel(cl, level);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	public int hashCode()
	{
		return level ^ pcc.hashCode();
	}

	public boolean equals(Object o)
	{
		if (o instanceof SpellLevel)
		{
			SpellLevel other = (SpellLevel) o;
			return level == other.level && pcc.equals(other.pcc);
		}
		return false;
	}

	public int compareTo(SpellLevel sl)
	{
		int cc = pcc.compareTo(sl.pcc);
		if (cc == 0)
		{
			if (level < sl.level)
			{
				return -1;
			}
			else if (level > sl.level)
			{
				return 1;
			}
		}
		return cc;
	}
}
