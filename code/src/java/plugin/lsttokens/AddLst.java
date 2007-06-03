/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.persistence.lst.AddLoader;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class AddLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "ADD";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		String key;
		if (value.startsWith("SPECIAL"))
		{
			key = "SPECIAL";
		}
		else if (value.startsWith("FEAT"))
		{
			key = "FEAT";
		}
		else if (value.startsWith("INIT"))
		{
			key = "INIT";
		}
		else if (value.startsWith("VFEAT"))
		{
			key = "VFEAT";
		}
		else if (value.startsWith("ABILITY"))
		{
			key = "ABILITY";
		}
		else if (value.startsWith("VABILITY"))
		{
			key = "VABILITY";
		}
		else if (value.startsWith("CLASSSKILLS"))
		{
			key = "CLASSSKILLS";
		}
		else if (value.startsWith("WEAPONBONUS"))
		{
			key = "WEAPONBONUS";
			Logging.errorPrint("ADD:LIST has been deprecated, please use a "
				+ "combination of CHOOSE:WEAPONPROF and BONUS:WEAPONPROF");
		}
		else if (value.startsWith("EQUIP"))
		{
			key = "EQUIP";
		}
		else if (value.startsWith("LIST"))
		{
			key = "LIST";
			Logging.errorPrint("ADD:LIST has been deprecated");
		}
		else if (value.startsWith("Language"))
		{
			Logging.deprecationPrint("Use of lower-case Language "
				+ "in ADD is deprecated. Use upper-case LANGUAGE");
			key = "LANGUAGE";
		}
		else if (value.startsWith("LANGUAGE"))
		{
			key = "LANGUAGE";
		}
		else if (value.startsWith("SKILL"))
		{
			key = "SKILL";
		}
		else if (value.startsWith("SPELLCASTER"))
		{
			key = "SPELLCASTER";
		}
		else if (value.startsWith("SPELLLEVEL"))
		{
			key = "SPELLLEVEL";
		}
		else if (value.startsWith("SA"))
		{
			key = "SA";
		}
		else
		{
			Logging
				.deprecationPrint("Lack of a SUBTOKEN for ADD:SA is deprecated.");
			Logging.deprecationPrint("Please use ADD:SA|name|[count|]X,X");
			key = "SA";
		}
		String contents;
		int keyLength = key.length();
		if (key.equals("SA"))
		{
			if (value.indexOf(Constants.PIPE) == -1)
			{
				obj.addAddList(anInt, value);
				return true;
			}
			contents = value;
		}
		else
		{
			if (key.equals("FEAT") && value.equals("FEAT"))
			{
				Logging.deprecationPrint("ADD:FEAT "
					+ "should not be used with no parameters");
				Logging.deprecationPrint("  This usage is deprecated");
				Logging
					.deprecationPrint("  Please use BONUS:FEAT|POOL|1 instead");
				return obj.addBonusList("FEAT|POOL|1");
			}
			contents = value.substring(keyLength + 1);
			if (value.charAt(keyLength) == '(')
			{
				Logging
					.deprecationPrint("ADD: syntax with parenthesis is deprecated.");
				Logging.deprecationPrint("Please use ADD:" + key + "|...");
				obj.addAddList(anInt, value);
				return true;
			}
			else if (key.equals("SPELLLEVEL"))
			{
				if (contents.charAt(keyLength) == ':')
				{
					Logging.deprecationPrint("Invalid ADD:SPELLLEVEL Syntax: "
						+ value);
					Logging.deprecationPrint("Please use ADD:SPELLLEVEL|...");
					obj.addAddList(anInt, value);
					return true;
				}
			}
		}
		if (value.charAt(keyLength) != '|')
		{
			Logging.errorPrint("Invalid ADD: Syntax: " + value);
			Logging.errorPrint("Please use ADD:" + key + "|...");
			return false;
		}
		// Guaranteed to be the new syntax here...
		return AddLoader.parseLine(obj, key, contents, anInt);
	}
}
