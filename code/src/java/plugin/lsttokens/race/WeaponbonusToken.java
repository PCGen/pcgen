package plugin.lsttokens.race;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with WEAPONBONUS Token
 */
public class WeaponbonusToken implements RaceLstToken
{

	public String getTokenName()
	{
		return "WEAPONBONUS"; //$NON-NLS-1$
	}

	public boolean parse(Race race, String value)
	{
		final StringTokenizer aTok =
				new StringTokenizer(value, Constants.PIPE, false);

		while (aTok.hasMoreTokens())
		{
			race.addWeaponProfBonus(aTok.nextToken());
		}

		return true;
	}
}
