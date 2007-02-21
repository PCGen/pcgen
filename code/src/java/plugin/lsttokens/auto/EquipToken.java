package plugin.lsttokens.auto;

import pcgen.core.PObject;
import pcgen.persistence.lst.AutoLstToken;

public class EquipToken implements AutoLstToken
{

	public String getTokenName()
	{
		return "EQUIP";
	}

	public boolean parse(PObject target, String value)
	{
		target.addAutoArray(getTokenName(), value);
		return true;
	}

}
