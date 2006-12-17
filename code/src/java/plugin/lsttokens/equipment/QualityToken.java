package plugin.lsttokens.equipment;

import java.util.StringTokenizer;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with ACCHECK token 
 */
public class QualityToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "QUALITY";
	}

	public boolean parse(Equipment eq, String value)
	{
		StringTokenizer tok = new StringTokenizer(value, "|");
		String key = "";
		String val = "";
		if (tok.hasMoreTokens())
		{
			key = tok.nextToken();
		}
		if (tok.hasMoreTokens())
		{
			val = tok.nextToken();
		}
		eq.setQuality(key, val);
		return true;
	}
}
