package plugin.lsttokens.weaponprof;

import pcgen.core.WeaponProf;
import pcgen.persistence.lst.WeaponProfLstToken;

/**
 * Class deals with HANDS Token
 */
public class HandsToken implements WeaponProfLstToken
{

	public String getTokenName()
	{
		return "HANDS";
	}

	public boolean parse(WeaponProf prof, String value)
	{
		prof.setHands(value);
		return true;
	}
}
