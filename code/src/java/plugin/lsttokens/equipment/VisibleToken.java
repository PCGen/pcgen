package plugin.lsttokens.equipment;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.Visibility;

public class VisibleToken extends AbstractNonEmptyToken<Equipment>
	implements CDOMPrimaryToken<Equipment>
{

	@Override
	public String getTokenName()
	{
		return "VISIBLE";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, Equipment eq, String value)
	{
		String visString = value;
		int pipeLoc = value.indexOf(Constants.PIPE);
		boolean readOnly = false;
		if (pipeLoc != -1)
		{
			if (value.substring(pipeLoc + 1).equals("READONLY"))
			{
				visString = value.substring(0, pipeLoc);
				readOnly = true;
			}
			else
			{
				return new ParseResult.Fail("Misunderstood text after pipe on Tag: "
						+ value, context);
			}
		}
		Visibility vis;
		if (visString.equals("YES"))
		{
			vis = Visibility.DEFAULT;
		}
		else if (visString.equals("ALWAYS"))
		{
			vis = Visibility.DEFAULT;
		}
		else if (visString.equals("DISPLAY"))
		{
			vis = Visibility.DISPLAY_ONLY;
		}
		else if (visString.equals("GUI"))
		{
			vis = Visibility.DISPLAY_ONLY;
		}
		else if (visString.equals("EXPORT"))
		{
			vis = Visibility.OUTPUT_ONLY;
		}
		else if (visString.equals("CSHEET"))
		{
			vis = Visibility.OUTPUT_ONLY;
		}
		else if (visString.equals("NO"))
		{
			vis = Visibility.HIDDEN;
		}
		else
		{
			ComplexParseResult cpr = new ComplexParseResult();
			cpr.addErrorMessage("Unexpected value used in " + getTokenName()
					+ " in Skill");
			cpr.addErrorMessage(" " + value + " is not a valid value for "
					+ getTokenName());
			cpr.addErrorMessage(" Valid values in Skill are YES, ALWAYS, DISPLAY, GUI, EXPORT, CSHEET");
			return cpr;
		}
		context.getObjectContext().put(eq, ObjectKey.VISIBILITY, vis);
		if (readOnly)
		{
			if (vis.equals(Visibility.OUTPUT_ONLY))
			{
				return new ParseResult.Fail("|READONLY suffix not valid with "
						+ getTokenName() + " EXPORT or CSHEET", context);
			}
			context.getObjectContext().put(eq, ObjectKey.READ_ONLY,
					Boolean.TRUE);
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Equipment skill)
	{
		Visibility vis = context.getObjectContext().getObject(skill,
				ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		if (!vis.equals(Visibility.DEFAULT)
				&& !vis.equals(Visibility.DISPLAY_ONLY)
				&& !vis.equals(Visibility.OUTPUT_ONLY))
		{
			context.addWriteMessage("Visibility " + vis
					+ " is not a valid Visibility for a Skill");
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(vis.getLSTFormat());
		Boolean readOnly = context.getObjectContext().getObject(skill,
				ObjectKey.READ_ONLY);
		if (readOnly != null)
		{
			if (!vis.equals(Visibility.OUTPUT_ONLY))
			{
				/*
				 * Don't barf if OUTPUT and READONLY as .MOD will cause that to
				 * happen
				 */
				sb.append('|').append("READONLY");
			}
		}
		return new String[] { sb.toString() };
	}

	@Override
	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}

}

