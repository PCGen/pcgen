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
	 * Template's LevelToken for AUTO:FEAT handled in
	 * rebuildAggregateAbilityListWorker() ; other subtokens do not support
	 * levels
	 * 
	 * TODO rebuildAggregateAbilityListWorker needs to be updated to use
	 * getCDOMObjects() once this is new token (due to class levels)
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
