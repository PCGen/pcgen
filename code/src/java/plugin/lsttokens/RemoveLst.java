/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.cdom.base.Constants;
import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.RemoveLoader;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class RemoveLst implements GlobalLstToken
{
	/*
	 * FIXME Template's LevelToken needs adjustment before this can be converted
	 * to the new syntax, since this is level-dependent
	 */

	public String getTokenName()
	{
		return "REMOVE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		int barLoc = value.indexOf(Constants.PIPE);
		if (barLoc == -1)
		{
			Logging.errorPrint("Invalid " + getTokenName() + " syntax: "
					+ value + " ... must have a PIPE");
			return false;
		}
		else if (barLoc == 0)
		{
			Logging.errorPrint("Invalid " + getTokenName() + " syntax: "
					+ value + " ... cannot start with a PIPE");
			return false;
		}
		String key = value.substring(0, barLoc);
		String contents = value.substring(barLoc + 1);
		if (contents == null || contents.length() == 0)
		{
			Logging.errorPrint("Invalid " + getTokenName() + " syntax: "
					+ value + " ... cannot end with a PIPE");
			return false;
		}
		// Guaranteed new format here
		return RemoveLoader.parseLine(obj, key, contents, anInt);
	}
}
