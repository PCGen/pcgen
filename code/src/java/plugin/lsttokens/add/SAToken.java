package plugin.lsttokens.add;

import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.persistence.lst.AddLstToken;
import pcgen.util.Logging;

public class SAToken implements AddLstToken
{

	public boolean parse(PObject target, String value, int level)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.errorPrint("Lack of a SUBTOKEN for ADD:SA "
				+ "is prohibited in new syntax.");
			Logging.errorPrint("Please use ADD:SA|name|[count|]X,X");
			return false;
		}
		String subToken = value.substring(0, pipeLoc);
		String countString;
		String items;
		int lastPipeLoc = value.lastIndexOf(Constants.PIPE);
		if (lastPipeLoc == pipeLoc)
		{
			items = value;
			countString = "1";
		}
		else
		{
			items = value.substring(pipeLoc + 1, lastPipeLoc);
			countString = value.substring(lastPipeLoc + 1);
		}
		target.addAddList(level, subToken + "(" + items + ")" + countString);
		return true;
	}

	public String getTokenName()
	{
		return "SA";
	}
}
