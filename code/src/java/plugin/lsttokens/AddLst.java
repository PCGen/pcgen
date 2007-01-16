/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.PObject;
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
		validate(value);
		obj.addAddList(anInt, value);
		return true;
	}

	private void validate(String value) {
		if ("FEAT".equals(value)) {
			Logging.errorPrint("ADD:FEAT should not be used with no parameters");
			Logging.errorPrint("  This usage is deprecated");
			Logging.errorPrint("  Please use BONUS:FEAT|POOL|1 instead");
		} else if (value.startsWith("INIT(")) {
			Logging.errorPrint("ADD:INIT is deprecated");
			Logging.errorPrint("  Note that the code does not function - "
					+ "you are not getting what you expect!");
		} else if (value.startsWith("SPECIAL(")) {
			Logging.errorPrint("ADD:SPECIAL is deprecated");
			Logging.errorPrint("  Note that the code does not function - "
					+ "you are not getting what you expect!");
		}
		return;
		// once * ADD (Global Add) is invalid, we can do another test:
//		else if (value.startsWith(".CLEAR") || value.startsWith("CLASSSKILLS(")
//				|| value.startsWith("EQUIP(") || value.startsWith("FEAT(")
//				|| value.startsWith("LANGUAGE(") || value.startsWith("SKILL(")
//				|| value.startsWith("SPELLCASTER(")
//				|| value.startsWith("SPELLLEVEL(")
//				|| value.startsWith("VFEAT(")) {
//			// OK
//			return;
//		}
		//Logging.errorPrint(value + " is not a valid ADD");
	}
}
