package plugin.lsttokens.auto;

import pcgen.core.PObject;
import pcgen.persistence.lst.AutoLstToken;

public class WeaponProfToken implements AutoLstToken
{

	public String getTokenName()
	{
		return "WEAPONPROF";
	}

	public boolean parse(PObject target, String value)
	{
		target.addAutoArray(getTokenName(), value);
		return true;
	}

}
