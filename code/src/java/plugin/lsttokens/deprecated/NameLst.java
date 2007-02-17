/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens.deprecated;

import pcgen.core.PObject;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 *
 */
public class NameLst implements GlobalLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "NAME";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		Logging.errorPrint("You are performing a dangerous action: "
				+ "You should not use the NAME Token");
		obj.setName(value);
		return true;
	}

	public String getMessage(PObject obj, String value) {
		return "You are performing a dangerous action: You should not use the NAME Token";
	}
}
