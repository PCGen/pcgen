package plugin.lsttokens.pcclass;

import java.util.StringTokenizer;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with WEAPONBONUS Token
 */
public class WeaponbonusToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "WEAPONBONUS";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		final StringTokenizer aTok = new StringTokenizer(value, "|", false);

		while (aTok.hasMoreTokens())
		{
			pcclass.addWeaponProfBonus(aTok.nextToken());
		}

		return true;
	}
}
