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
		Visibility vis;
		if (value.equals("YES"))
		{
			vis = Visibility.DEFAULT;
		}
		else if (value.equals("DISPLAY"))
		{
			vis = Visibility.DISPLAY_ONLY;
		}
		else if (value.equals("EXPORT"))
		{
			vis = Visibility.OUTPUT_ONLY;
		}
		else if (value.equals("NO"))
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
			cpr.addErrorMessage(" Valid values in Skill are YES, NO, DISPLAY, EXPORT");
			return cpr;
		}
		context.getObjectContext().put(eq, ObjectKey.VISIBILITY, vis);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Equipment eq)
	{
		Visibility vis = context.getObjectContext().getObject(eq,
				ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		String visString;
		if (vis.equals(Visibility.DEFAULT))
		{
			visString = "YES";
		}
		else if (vis.equals(Visibility.DISPLAY_ONLY))
		{
			visString = "DISPLAY";
		}
		else if (vis.equals(Visibility.OUTPUT_ONLY))
		{
			visString = "EXPORT";
		}
		else if (vis.equals(Visibility.HIDDEN))
		{
			visString = "NO";
		}
		else
		{
			context.addWriteMessage("Visibility " + vis
					+ " is not a valid Visibility for an Equipment");
			return null;
		}
		return new String[] { visString };
	}

	@Override
	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}

}

