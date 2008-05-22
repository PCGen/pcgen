package plugin.lsttokens.race;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.core.Race;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with RACESUBTYPE Token
 */
public class RacesubtypeToken extends AbstractToken implements
		CDOMPrimaryToken<Race>
{

	@Override
	public String getTokenName()
	{
		return "RACESUBTYPE";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		boolean first = true;

		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tokString))
			{
				if (!first)
				{
					Logging.errorPrint("  Non-sensical " + getTokenName()
							+ ": .CLEAR was not the first list item: " + value);
					return false;
				}
				context.obj.removeList(race, ListKey.RACESUBTYPE);
			}
			else if (tokString.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				String clearText = tokString.substring(7);
				context.getObjectContext()
						.removeFromList(race, ListKey.RACESUBTYPE,
								RaceSubType.getConstant(clearText));
			}
			else
			{
				context.getObjectContext().addToList(race, ListKey.RACESUBTYPE,
						RaceSubType.getConstant(tokString));
			}
			first = false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		Changes<RaceSubType> changes = context.getObjectContext()
				.getListChanges(race, ListKey.RACESUBTYPE);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		List<String> list = new ArrayList<String>();
		Collection<RaceSubType> removedItems = changes.getRemoved();
		if (changes.includesGlobalClear())
		{
			if (removedItems != null && !removedItems.isEmpty())
			{
				context.addWriteMessage("Non-sensical relationship in "
						+ getTokenName()
						+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			list.add(Constants.LST_DOT_CLEAR);
		}
		else if (removedItems != null && !removedItems.isEmpty())
		{
			list.add(Constants.LST_DOT_CLEAR_DOT
					+ StringUtil.join(removedItems, "|.CLEAR."));
		}
		Collection<RaceSubType> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			list.add(StringUtil.join(added, Constants.PIPE));
		}
		if (list.isEmpty())
		{
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}
}
