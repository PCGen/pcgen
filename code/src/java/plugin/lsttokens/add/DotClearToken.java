package plugin.lsttokens.add;

import pcgen.core.PObject;
import pcgen.persistence.lst.AddLstToken;
import pcgen.util.Logging;

public class DotClearToken implements AddLstToken
{

	public boolean parse(PObject target, String value, int aLevel)
	{
		if (aLevel > 0)
		{
			Logging
				.errorPrint("Warning: You performed a Dangerous .CLEAR in a ADD: Token");
			Logging
				.errorPrint("  A non-level limited .CLEAR was used in a Class Level line");
			Logging
				.errorPrint("  Today, this performs a .CLEAR on the entire PCClass");
			Logging
				.errorPrint("  However, you are using undocumented behavior that is subject to change");
			Logging.errorPrint("  Hint: It will change after PCGen 5.12");
			Logging
				.errorPrint("  Please level limit the .CLEAR (e.g. .CLEAR.LEVEL2)");
			Logging
				.errorPrint("  ... or put the ADD:.CLEAR on a non-level Class line");
		}
		target.clearAdds();
		return true;
	}

	public String getTokenName()
	{
		return "VFEAT";
	}
}
