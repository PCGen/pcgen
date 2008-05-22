package plugin.lsttokens.template;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.core.PCTemplate;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with RACESUBTYPE Token
 */
public class RacesubtypeToken extends AbstractToken implements
		CDOMPrimaryToken<PCTemplate>
{

	@Override
	public String getTokenName()
	{
		return "RACESUBTYPE";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		while (tok.hasMoreTokens())
		{
			String aType = tok.nextToken();

			if (aType.startsWith(".REMOVE."))
			{
				String substring = aType.substring(8);
				if (substring.length() == 0)
				{
					Logging.errorPrint("Invalid .REMOVE. in " + getTokenName());
					Logging.errorPrint("  Requires an argument");
					return false;
				}
				context.getObjectContext().addToList(template,
						ListKey.REMOVED_RACESUBTYPE,
						RaceSubType.getConstant(substring));
			}
			else
			{
				context.getObjectContext().addToList(template,
						ListKey.RACESUBTYPE, RaceSubType.getConstant(aType));
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Changes<RaceSubType> addedChanges = context.getObjectContext()
				.getListChanges(pct, ListKey.RACESUBTYPE);
		Changes<RaceSubType> removedChanges = context.getObjectContext()
				.getListChanges(pct, ListKey.REMOVED_RACESUBTYPE);
		Collection<RaceSubType> added = addedChanges.getAdded();
		Collection<RaceSubType> removed = removedChanges.getAdded();
		if (added == null && removed == null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean needPipe = false;
		if (removed != null)
		{
			for (RaceSubType rst : removed)
			{
				if (needPipe)
				{
					sb.append(Constants.PIPE);
				}
				sb.append(".REMOVE.").append(rst);
				needPipe = true;
			}
		}
		if (added != null)
		{
			for (RaceSubType rst : added)
			{
				if (needPipe)
				{
					sb.append(Constants.PIPE);
				}
				sb.append(rst);
				needPipe = true;
			}
		}
		return new String[] { sb.toString() };
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}
