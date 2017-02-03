package pcgen.core.term;

import pcgen.util.Logging;

final class TermUtil
{
	private TermUtil()
	{
	}

	static Float convertToFloat(String element, String foo)
	{
		Float d = null;
		try
		{
			d = new Float(foo);
		}
		catch (NumberFormatException ignored)
		{
			// What we got back was not a number
		}

		Float retVal = null;
		if ((d != null) && !d.isNaN())
		{
			retVal = d;
			if (Logging.isDebugMode())
			{
				Logging.debugPrint("Export variable for: '" + element + "' = " + d);
			}
		}

		return retVal;
	}

}
