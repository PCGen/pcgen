/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.PObject;
import pcgen.io.EntityEncoder;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 *
 */
public class TempdescLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "TEMPDESC";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		obj.setTempDescription(EntityEncoder.decode(value));
		return true;
	}
}
