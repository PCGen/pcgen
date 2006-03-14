package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with WEAPONBONUS Token
 */
public class WeaponbonusToken implements RaceLstToken {

	public String getTokenName() {
		return "WEAPONBONUS";
	}

	public boolean parse(Race race, String value) {
		race.setWeaponProfBonus(value);
		return true;
	}
}
