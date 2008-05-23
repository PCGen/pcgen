package plugin.lsttokens.skill;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Skill;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements CDOMPrimaryToken<Skill>
{

	public String getTokenName()
	{
		return "VISIBLE";
	}

	public boolean parse(LoadContext context, Skill skill, String value)
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
				Logging.errorPrint("Misunderstood text after pipe on Tag: "
					+ value);
				return false;
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
		else
		{
			Logging.errorPrint("Unexpected value used in " + getTokenName()
					+ " in Skill");
			Logging.errorPrint(" " + value + " is not a valid value for "
					+ getTokenName());
			Logging
					.errorPrint(" Valid values in Skill are YES, ALWAYS, DISPLAY, GUI, EXPORT, CSHEET");
			return false;
		}
		context.getObjectContext().put(skill, ObjectKey.VISIBILITY, vis);
		if (readOnly)
		{
			if (vis.equals(Visibility.OUTPUT_ONLY))
			{
				Logging.errorPrint("|READONLY suffix not valid with "
						+ getTokenName() + " EXPORT or CSHEET");
				return false;
			}
			context.getObjectContext().put(skill, ObjectKey.READ_ONLY,
					Boolean.TRUE);
		}
		return true;
	}

	public String[] unparse(LoadContext context, Skill skill)
	{
		Visibility vis = context.getObjectContext().getObject(skill,
				ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		if (!vis.equals(Visibility.DEFAULT) && !vis.equals(Visibility.DISPLAY_ONLY)
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
			if (vis.equals(Visibility.OUTPUT_ONLY))
			{
				context.addWriteMessage("ReadOnly is not allowed on a "
						+ "Skill with Visibility " + vis);
				return null;
			}
			sb.append('|').append("READONLY");
		}
		return new String[] { sb.toString() };
	}

	public Class<Skill> getTokenClass()
	{
		return Skill.class;
	}
}
