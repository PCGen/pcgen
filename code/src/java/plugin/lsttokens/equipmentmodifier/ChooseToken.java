/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens.equipmentmodifier;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.Constants;
import pcgen.core.EquipmentModifier;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class ChooseToken implements EquipmentModifierLstToken
{

	public String getTokenName()
	{
		return "CHOOSE";
	}

	public boolean parse(EquipmentModifier mod, String value)
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
					Logging.errorPrint("COUNT in CHOOSE must be a formula: "
							+ value);
					return false;
				}
				activeLoc += key.length() + 1;
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
				activeLoc += key.length() + 1;
			}
			else
			{
				break;
			}
		}
		String prefixString = CoreUtility.join(prefixList, "|");
		if (ChooseLoader.isEqModChooseToken(key))
		{
			if (ChooseLoader.parseEqModToken(mod, prefixString, key, val))
			{
				return true;
			}
		}
		Logging.deprecationPrint("CHOOSE: in EqMod with Title as first argument is deprecated");
		Logging.deprecationPrint("  Please use CHOOSE:<subtoken>|<args>|TITLE=<title>");
		Logging.deprecationPrint("  Offending CHOOSE was: " + value);
		if (value.indexOf("TITLE=") != -1)
		{
			Logging.errorPrint("Unexpected Condition: Deprecated Syntax EqMod with Chooser Type and TITLE= " + value);
			Logging.errorPrint("Please check error messages above for syntax errors or contact the PCGen team on the PCGenListFileHelp Yahoo Group for support");
			return false;
		}
		mod.setChoiceString(value);
		return true;
	}

}
