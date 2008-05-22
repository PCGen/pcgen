package plugin.lsttokens.race;

import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Race;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with HITDICEADVANCEMENT Token
 */
public class HitdiceadvancementToken extends AbstractToken implements
		CDOMPrimaryToken<Race>
{

	@Override
	public String getTokenName()
	{
		return "HITDICEADVANCEMENT";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}

		final StringTokenizer commaTok = new StringTokenizer(value,
				Constants.COMMA);

		context.getObjectContext()
				.removeList(race, ListKey.HITDICE_ADVANCEMENT);
		int last = 0;
		while (commaTok.hasMoreTokens())
		{
			String tok = commaTok.nextToken();
			int hd;
			if ("*".equals(tok))
			{
				if (commaTok.hasMoreTokens())
				{
					Logging.errorPrint("Found * in " + getTokenName()
							+ " but was not at end of list");
					return false;
				}

				hd = Integer.MAX_VALUE;
			}
			else
			{
				try
				{
					hd = Integer.parseInt(tok);
					if (hd < last)
					{
						Logging
								.errorPrint("Found " + hd + " in "
										+ getTokenName() + " but was < 1 "
										+ "or the previous value in the list: "
										+ value);
						return false;
					}
					last = hd;
				}
				catch (NumberFormatException nfe)
				{
					return false;
				}
			}
			context.getObjectContext().addToList(race,
					ListKey.HITDICE_ADVANCEMENT, Integer.valueOf(hd));
		}
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		Changes<Integer> changes = context.getObjectContext().getListChanges(
				race, ListKey.HITDICE_ADVANCEMENT);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean needsComma = false;
		int last = 0;
		Collection<Integer> list = changes.getAdded();
		for (Iterator<Integer> it = list.iterator(); it.hasNext();)
		{
			if (needsComma)
			{
				sb.append(',');
			}
			needsComma = true;
			Integer hd = it.next();
			if (hd.intValue() == Integer.MAX_VALUE)
			{
				if (it.hasNext())
				{
					context.addWriteMessage("Integer MAX_VALUE found in "
							+ getTokenName()
							+ " was not at the end of the array.");
					return null;
				}
				sb.append('*');
			}
			else
			{
				if (hd.intValue() < last)
				{
					Logging.errorPrint("Found " + hd + " in " + getTokenName()
							+ " but was <= zero "
							+ "or the previous value in the list: " + list);
					return null;
				}
				last = hd.intValue();
				sb.append(hd);
			}
		}
		return new String[] { sb.toString() };
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}
}
