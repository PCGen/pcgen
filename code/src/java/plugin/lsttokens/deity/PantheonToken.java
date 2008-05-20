package plugin.lsttokens.deity;

import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Pantheon;
import pcgen.core.Deity;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with PANTHEON Token
 */
public class PantheonToken extends AbstractToken implements CDOMPrimaryToken<Deity>
{

	@Override
	public String getTokenName()
	{
		return "PANTHEON";
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
			context.getObjectContext().addToList(deity, ListKey.PANTHEON,
					Pantheon.getConstant(tok.nextToken()));
		}
		return true;
	}

	public String[] unparse(LoadContext context, Deity deity)
	{
		Changes<Pantheon> changes =
				context.getObjectContext().getListChanges(deity,
					ListKey.PANTHEON);
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
