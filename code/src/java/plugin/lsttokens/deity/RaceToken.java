package plugin.lsttokens.deity;

import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Deity;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with RACE Token
 */
public class RaceToken extends AbstractToken implements CDOMPrimaryToken<Deity>
{

	@Override
	public String getTokenName()
	{
		return "RACE";
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		while (tok.hasMoreTokens())
		{
			context.getObjectContext().addToList(deity, ListKey.RACEPANTHEON,
					tok.nextToken());
		}
		return true;
	}

	public String[] unparse(LoadContext context, Deity deity)
	{
		Changes<String> changes =
				context.getObjectContext().getListChanges(deity,
					ListKey.RACEPANTHEON);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		return new String[]{StringUtil.join(changes.getAdded(), Constants.PIPE)};
	}

	public Class<Deity> getTokenClass()
	{
		return Deity.class;
	}
}
