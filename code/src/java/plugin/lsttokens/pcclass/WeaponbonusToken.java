package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with WEAPONBONUS Token
 */
public class WeaponbonusToken implements PCClassLstToken {

	public String getTokenName() {
		return "WEAPONBONUS";
	}

	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setWeaponProfBonus(value);
		return true;
	}
}
