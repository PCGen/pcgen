/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.core.EquipmentModifier;
import pcgen.core.PObject;
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
		if (obj instanceof EquipmentModifier)
		{
			return false;
		}
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
				if (val.startsWith("FEAT="))
				{
					key = "FEAT";
					val = val.substring(5);
				}
				else if (pipeLoc == -1)
				{
					key = val;
					val = null;
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
					Logging.deprecationPrint("Support for COUNT= in CHOOSE"
							+ "is tenuous, at best, use the SELECT: token " + value);
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
			String prefixString = StringUtil.join(prefixList, "|");
			boolean parse =
					ChooseLoader.parseToken(obj, prefixString, key, val);
			if (!parse)
			{
				parseOld(obj, value);
			}
			return true;
		}
		return false;
	}

	private void parseOld(PObject obj, String value)
	{
		Logging.deprecationPrint("CHOOSE: syntax you are using is deprecated: "
			+ value);
		Logging.deprecationPrint("  Please use CHOOSE:SUBKEY|choices");
		Logging.deprecationPrint("  ... see the PCGen docs");
		obj.setChoiceString(value);
	}
}
