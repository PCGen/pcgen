package plugin.lsttokens.statsandchecks.stat;

import java.util.StringTokenizer;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with STATRANGE Token
 */
public class StatrangeToken implements CDOMPrimaryToken<PCStat>
{

	public String getTokenName()
	{
		return "STATRANGE";
	}

	public ParseResult parseToken(LoadContext context, PCStat stat, String value)
	{
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);

		if (aTok.countTokens() == 2)
		{
			try
			{
				context.obj.put(stat, IntegerKey.MIN_VALUE, Integer
						.valueOf(aTok.nextToken()));
				context.obj.put(stat, IntegerKey.MAX_VALUE, Integer
						.valueOf(aTok.nextToken()));
				return ParseResult.SUCCESS;
			}
			catch (NumberFormatException ignore)
			{
				return new ParseResult.Fail("Error in specified Stat range, "
						+ "expected two comma separated integers, found: "
						+ value);
			}
		}
		else
		{
			return new ParseResult.Fail("Error in specified Stat range, "
					+ "expected two comma separated integers, found "
					+ aTok.countTokens() + " values in: " + value);
		}
	}

	public String[] unparse(LoadContext context, PCStat stat)
	{
		Integer min = context.getObjectContext().getInteger(stat,
				IntegerKey.MIN_VALUE);
		Integer max = context.getObjectContext().getInteger(stat,
				IntegerKey.MAX_VALUE);
		if (min == null && max == null)
		{
			return null;
		}
		if (min == null || max == null)
		{
			context.addWriteMessage("Must have both min and max in "
					+ getTokenName() + ": " + min + " " + max);
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(min).append(',').append(max);
		return new String[] { sb.toString() };
	}

	public Class<PCStat> getTokenClass()
	{
		return PCStat.class;
	}
}
