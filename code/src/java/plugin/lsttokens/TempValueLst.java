package plugin.lsttokens;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class TempValueLst extends AbstractTokenWithSeparator<CDOMObject>
		implements CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "TEMPVALUE";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
		CDOMObject obj, String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			return new ParseResult.Fail(getTokenName()
				+ " must have two or more | delimited arguments : " + value,
				context);
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		if (tok.countTokens() != 3)
		{
			return new ParseResult.Fail(
				getTokenName()
					+ " requires three arguments, MIN=, MAX= and TITLE= : "
					+ value, context);
		}
		if (!tok.nextToken().startsWith("MIN="))
		{
			return new ParseResult.Fail("COUNT:" + getTokenName()
				+ " first argument was not MIN=", context);
		}
		if (!tok.nextToken().startsWith("MAX="))
		{
			return new ParseResult.Fail("COUNT:" + getTokenName()
				+ " second argument was not MAX=", context);
		}
		if (!tok.nextToken().startsWith("TITLE="))
		{
			return new ParseResult.Fail("COUNT:" + getTokenName()
				+ " third argument was not TITLE=", context);
		}
		StringBuilder sb = new StringBuilder(value.length() + 20);
		sb.append(getTokenName()).append('|').append(value);
		context.getObjectContext().put(obj, StringKey.TEMPVALUE,
			sb.toString());
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		String tv =
				context.getObjectContext().getString(cdo,
					StringKey.TEMPVALUE);
		if (tv == null)
		{
			return null;
		}
		return new String[]{tv};
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

}
