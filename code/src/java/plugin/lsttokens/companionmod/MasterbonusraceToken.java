package plugin.lsttokens.companionmod;

import java.util.Collection;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Race;
import pcgen.core.character.CompanionMod;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with MASTERBONUSRACE Token
 */
public class MasterbonusraceToken extends AbstractToken implements
		CDOMPrimaryToken<CompanionMod>
{
	public static final Class<Race> RACE_CLASS = Race.class;

	@Override
	public String getTokenName()
	{
		return "MASTERBONUSRACE";
	}

	public boolean parse(LoadContext context, CompanionMod cMod, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();

			CDOMSingleRef<Race> ref =
					context.ref.getCDOMReference(RACE_CLASS, token);
			context.getObjectContext().addToList(cMod, ListKey.APPLIED_RACE,
				ref);
		}
		return true;
	}

	public String[] unparse(LoadContext context, CompanionMod cMod)
	{
		Changes<CDOMSingleRef<Race>> changes =
				context.getObjectContext().getListChanges(cMod,
					ListKey.APPLIED_RACE);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		SortedSet<String> set = new TreeSet<String>();
		Collection<CDOMSingleRef<Race>> added = changes.getAdded();

		for (CDOMSingleRef<Race> ref : added)
		{
			set.add(ref.getLSTformat());
		}
		return new String[]{StringUtil.join(set, Constants.PIPE)};
	}

	public Class<CompanionMod> getTokenClass()
	{
		return CompanionMod.class;
	}

}
