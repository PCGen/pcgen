/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.template;

import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with REPEATLEVEL Token
 */
public class RepeatlevelToken implements CDOMPrimaryToken<PCTemplate>
{

	public String getTokenName()
	{
		return "REPEATLEVEL";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
			throws PersistenceLayerException
	{
		//
		// x|y|z:level:<level assigned item>
		//
		int endRepeat = value.indexOf(Constants.COLON);
		if (endRepeat < 0)
		{
			Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (No Colon): " + value);
			return false;
		}
		int endLevel = value.indexOf(Constants.COLON, endRepeat + 1);
		if (endLevel < 0)
		{
			Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (Only One Colon): " + value);
			return false;
		}
		int endAssignType = value.indexOf(Constants.COLON, endLevel + 1);
		if (endAssignType == -1)
		{
			Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (Only Two Colons): " + value);
			return false;
		}

		String repeatedInfo = value.substring(0, endRepeat);
		StringTokenizer repeatToken = new StringTokenizer(repeatedInfo,
				Constants.PIPE);
		if (repeatToken.countTokens() != 3)
		{
			Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (incorrect PIPE count in repeat): "
					+ repeatedInfo);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}

		String levelIncrement = repeatToken.nextToken();
		int lvlIncrement;
		try
		{
			lvlIncrement = Integer.parseInt(levelIncrement);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (Level Increment was not an Integer): "
					+ levelIncrement);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}
		if (lvlIncrement <= 0)
		{
			Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (Level Increment was <= 0): " + lvlIncrement);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}

		String consecutiveString = repeatToken.nextToken();
		int consecutive;
		try
		{
			consecutive = Integer.parseInt(consecutiveString);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (Consecutive Value was not an Integer): "
					+ consecutiveString);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}
		if (consecutive < 0)
		{
			Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (Consecutive String was <= 0): " + consecutive);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}

		String maxLevelString = repeatToken.nextToken();
		int maxLevel;
		try
		{
			maxLevel = Integer.parseInt(maxLevelString);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (Max Level was not an Integer): "
					+ maxLevelString);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}
		if (maxLevel <= 0)
		{
			Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (Max Level was <= 0): " + maxLevel);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}

		String levelString = value.substring(endRepeat + 1, endLevel);
		int iLevel;
		try
		{
			iLevel = Integer.parseInt(levelString);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (Level was not a number): " + levelString);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}
		if (iLevel <= 0)
		{
			Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (Level was <= 0): " + iLevel);
			Logging.errorPrint("  Line was: " + value);
			return false;
		}

		if (iLevel > maxLevel)
		{
			Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (Starting Level was > Maximum Level)");
			Logging.errorPrint("  Line was: " + value);
			return false;
		}
		if (iLevel + lvlIncrement > maxLevel)
		{
			Logging
					.errorPrint("Malformed "
							+ getTokenName()
							+ " Token (Does not repeat, Staring Level + Increment > Maximum Level)");
			Logging.errorPrint("  Line was: " + value);
			return false;
		}
		if (consecutive != 0
				&& ((maxLevel - iLevel) / lvlIncrement) < consecutive)
		{
			Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (Does not use Skip Interval value): "
					+ consecutive);
			Logging.errorPrint("  You should set the interval to zero");
			Logging.errorPrint("  Line was: " + value);
			return false;
		}

		String typeStr = value.substring(endLevel + 1, endAssignType);
		String contentStr = value.substring(endAssignType + 1);
		if (contentStr.length() == 0)
		{
			Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (No Content to SubToken)");
			Logging.errorPrint("  Line was: " + value);
			return false;
		}

		PCTemplate consolidator = new PCTemplate();
		consolidator.put(IntegerKey.CONSECUTIVE, Integer.valueOf(consecutive));
		consolidator.put(IntegerKey.MAX_LEVEL, Integer.valueOf(maxLevel));
		consolidator.put(IntegerKey.LEVEL_INCREMENT, Integer
				.valueOf(lvlIncrement));
		consolidator.put(IntegerKey.START_LEVEL, Integer.valueOf(iLevel));
		context.getObjectContext().addToList(template,
				ListKey.REPEATLEVEL_TEMPLATES, consolidator);

		for (int count = consecutive; iLevel <= maxLevel; iLevel += lvlIncrement)
		{
			if ((consecutive == 0) || (count != 0))
			{
				PCTemplate derivative = new PCTemplate();
				derivative.put(IntegerKey.LEVEL, count);
				context.getObjectContext().addToList(consolidator,
						ListKey.LEVEL_TEMPLATES, derivative);
				if (!context.processToken(derivative, typeStr, contentStr))
				{
					return false;
				}
			}
			if (consecutive != 0)
			{
				if (count == 0)
				{
					count = consecutive;
				}
				else
				{
					--count;
				}
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Changes<PCTemplate> changes = context.getObjectContext()
				.getListChanges(pct, ListKey.REPEATLEVEL_TEMPLATES);
		Collection<PCTemplate> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			return null;
		}
		Set<String> list = new TreeSet<String>();
		for (PCTemplate agg : added)
		{
			StringBuilder sb = new StringBuilder();
			Integer consecutive = agg.get(IntegerKey.CONSECUTIVE);
			Integer maxLevel = agg.get(IntegerKey.MAX_LEVEL);
			Integer lvlIncrement = agg.get(IntegerKey.LEVEL_INCREMENT);
			Integer iLevel = agg.get(IntegerKey.START_LEVEL);
			sb.append(lvlIncrement).append(Constants.PIPE);
			sb.append(consecutive).append(Constants.PIPE);
			sb.append(maxLevel).append(Constants.COLON);
			sb.append(iLevel).append(Constants.COLON);
			Changes<PCTemplate> subchanges = context.getObjectContext()
					.getListChanges(agg, ListKey.LEVEL_TEMPLATES);
			Collection<PCTemplate> perAddCollection = subchanges.getAdded();
			if (perAddCollection == null || perAddCollection.isEmpty())
			{
				context.addWriteMessage("Invalid Consolidator built in "
						+ getTokenName() + ": had no subTemplates");
				return null;
			}
			PCTemplate next = perAddCollection.iterator().next();
			Collection<String> unparse = context.unparse(next);
			if (unparse != null)
			{
				int masterLength = sb.length();
				for (String str : unparse)
				{
					sb.setLength(masterLength);
					list.add(sb.append(str).toString());
				}
			}
		}
		if (list.isEmpty())
		{
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}