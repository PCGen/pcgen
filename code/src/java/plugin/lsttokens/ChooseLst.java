/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class ChooseLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "CHOOSE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (!value.startsWith("CHOOSE:LANGAUTO"))
		{
			String key;
			String val = value;
			int activeLoc = 0;
			String count = null;
			String maxCount = null;
			List<String> prefixList = new ArrayList<String>(2);
			while (true)
			{
				int pipeLoc = val.indexOf(Constants.PIPE, activeLoc);
				if (pipeLoc == -1)
				{
					if (val.startsWith("FEAT="))
					{
						key = "FEAT";
						val = val.substring(5);
					}
					else
					{
						key = val;
						val = null;
					}
				}
				else
				{
					key = val.substring(activeLoc, pipeLoc);
					val = val.substring(pipeLoc + 1);
				}
				if (key.startsWith("COUNT="))
				{
					if (count != null)
					{
						Logging
							.errorPrint("Cannot use COUNT more than once in CHOOSE: "
								+ value);
						return false;
					}
					prefixList.add(key);
					count = key.substring(6);
					if (count == null)
					{
						Logging
							.errorPrint("COUNT in CHOOSE must be a formula: "
								+ value);
						return false;
					}
				}
				else if (key.startsWith("NUMCHOICES="))
				{
					if (maxCount != null)
					{
						Logging
							.errorPrint("Cannot use NUMCHOICES more than once in CHOOSE: "
								+ value);
						return false;
					}
					prefixList.add(key);
					maxCount = key.substring(11);
					if (maxCount == null || maxCount.length() == 0)
					{
						Logging
							.errorPrint("NUMCHOICES in CHOOSE must be a formula: "
								+ value);
						return false;
					}
				}
				else
				{
					break;
				}
			}
			String prefixString = CoreUtility.join(prefixList, "|");
			boolean parse = ChooseLoader.parseToken(obj, prefixString, key, val, anInt);
			if (!parse)
			{
				parseOld(obj, value, anInt);
			}
			return true;
		}
		return false;
	}

	private void parseOld(PObject obj, String value, int anInt)
	{
		Logging.deprecationPrint("CHOOSE: syntax you are using is deprecated: "
			+ value);
		Logging.deprecationPrint("  Please use CHOOSE:SUBKEY|choices");
		Logging.deprecationPrint("  ... see the PCGen docs");
		obj.setChoiceString(value);
	}
}
