/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.cdom.base.Constants;
import pcgen.core.PObject;
import pcgen.persistence.lst.AutoLoader;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 *
 */
public class AutoLst implements GlobalLstToken
{

	/*
	 * FIXME Template's LevelToken needs adjustment before this can be converted
	 * to the new syntax, since this is level-dependent
	 */
	public String getTokenName()
	{
		return "AUTO";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		int barLoc = value.indexOf(Constants.PIPE);
		if (barLoc == -1)
		{
			Logging.errorPrint(getTokenName() + " must contain a PIPE (|)");
			return false;
		}
		String subKey = value.substring(0, barLoc);
		return AutoLoader.parseLine(obj, subKey, value.substring(barLoc + 1),
				anInt);
	}
}
