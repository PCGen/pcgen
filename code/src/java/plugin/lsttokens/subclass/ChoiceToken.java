package plugin.lsttokens.subclass;

import pcgen.core.Constants;
import pcgen.core.SubClass;
import pcgen.persistence.lst.SubClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with CHOICE Token
 */
public class ChoiceToken implements SubClassLstToken
{

	public String getTokenName()
	{
		return "CHOICE";
	}

	public boolean parse(SubClass subclass, String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.errorPrint(getTokenName() + " uses a deprecated format: "
				+ value + "\n  "
				+ "New format must be type|value, e.g. SCHOOL|Abjuration");
			subclass.setChoice(value);
			return true;
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.errorPrint(getTokenName() + " has invalid arguments: "
				+ value + "\n  cannot have two | characters");
			return false;
		}
		String type = value.substring(0, pipeLoc);
		if ("SCHOOL".equals(type) || "SUBSCHOOL".equals(type)
			|| "DESCRIPTOR".equals(type))
		{
			// Unfortunately, in 5.14, we have no way of validating that the
			// input
			// is correct
			subclass.setChoice(value.substring(pipeLoc + 1));
			return true;
		}
		Logging
			.errorPrint(getTokenName() + " did not understand type: " + type);
		return false;
	}
}
