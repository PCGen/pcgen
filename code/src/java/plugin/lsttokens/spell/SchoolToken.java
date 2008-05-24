package plugin.lsttokens.spell;

import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.SettingsHandler;
import pcgen.core.spell.Spell;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with SCHOOL Token
 */
public class SchoolToken extends AbstractToken implements
		CDOMPrimaryToken<Spell>
{

	@Override
	public String getTokenName()
	{
		return "SCHOOL";
	}

	public boolean parse(LoadContext context, Spell spell, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);

		boolean first = true;
		while (aTok.hasMoreTokens())
		{
			String tokString = aTok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tokString))
			{
				if (!first)
				{
					Logging.errorPrint("  Non-sensical " + getTokenName()
							+ ": .CLEAR was not the first list item: " + value);
					return false;
				}
				context.getObjectContext().removeList(spell,
						ListKey.SPELL_SCHOOL);
			}
			else
			{
				context.getObjectContext().addToList(spell,
						ListKey.SPELL_SCHOOL, tokString);
				SettingsHandler.getGame().addToSchoolList(tokString);
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, Spell spell)
	{
		Changes<String> changes = context.getObjectContext().getListChanges(
				spell, ListKey.SPELL_SCHOOL);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		if (changes.includesGlobalClear())
		{
			sb.append(Constants.LST_DOT_CLEAR);
		}
		if (changes.hasAddedItems())
		{
			if (sb.length() != 0)
			{
				sb.append(Constants.PIPE);
			}
			sb.append(StringUtil.joinToStringBuffer(changes.getAdded(),
					Constants.PIPE));
		}
		return new String[] { sb.toString() };
	}

	public Class<Spell> getTokenClass()
	{
		return Spell.class;
	}
}
