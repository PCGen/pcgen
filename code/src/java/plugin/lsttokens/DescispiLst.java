/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 *
 */
public class DescispiLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "DESCISPI";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		obj.setDescIsPI(value.toUpperCase().startsWith("Y"));
		return true;
	}
}
