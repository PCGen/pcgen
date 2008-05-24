package plugin.lsttokens.spell;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.spell.Spell;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with ITEM Token
 */
public class ItemToken extends AbstractToken implements CDOMPrimaryToken<Spell>
{

	@Override
	public String getTokenName()
	{
		return "ITEM";
	}

	public boolean parse(LoadContext context, Spell spell, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}

		StringTokenizer aTok = new StringTokenizer(value, Constants.COMMA);

		while (aTok.hasMoreTokens())
		{
			String tokString = aTok.nextToken();
			int bracketLoc = tokString.indexOf('[');
			if (bracketLoc == 0)
			{
				// Check ends with bracket
				if (tokString.lastIndexOf(']') != tokString.length() - 1)
				{
					Logging.errorPrint("Invalid " + getTokenName()
							+ ": mismatched open Bracket: " + tokString
							+ " in " + value);
					return false;
				}
				String substring = tokString.substring(1,
						tokString.length() - 1);
				if (substring.length() == 0)
				{
					Logging.errorPrint("Invalid " + getTokenName()
							+ ": cannot be empty item in brackets []");
					return false;
				}
				context.getObjectContext().addToList(spell,
						ListKey.PROHIBITED_ITEM, substring);
			}
			else
			{
				if (tokString.lastIndexOf(']') != -1)
				{
					Logging.errorPrint("Invalid " + getTokenName()
							+ ": mismatched close Bracket: " + tokString
							+ " in " + value);
					return false;
				}
				context.getObjectContext().addToList(spell, ListKey.ITEM,
						tokString);
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, Spell spell)
	{
		Changes<String> changes = context.getObjectContext().getListChanges(
				spell, ListKey.ITEM);
		Changes<String> proChanges = context.getObjectContext().getListChanges(
				spell, ListKey.PROHIBITED_ITEM);
		Collection<String> changeAdded = changes.getAdded();
		Collection<String> proAdded = proChanges.getAdded();
		StringBuilder sb = new StringBuilder();
		boolean needComma = false;
		if (changeAdded != null)
		{
			for (String t : changeAdded)
			{
				if (needComma)
				{
					sb.append(Constants.COMMA);
				}
				sb.append(t.toString());
				needComma = true;
			}
		}
		if (proAdded != null)
		{
			for (String t : proAdded)
			{
				if (needComma)
				{
					sb.append(Constants.COMMA);
				}
				sb.append('[').append(t.toString()).append(']');
				needComma = true;
			}
		}
		if (sb.length() == 0)
		{
			return null;
		}
		return new String[] { sb.toString() };
	}

	public Class<Spell> getTokenClass()
	{
		return Spell.class;
	}
}
