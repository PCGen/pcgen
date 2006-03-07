package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with WEAPONBONUS Token
 */
public class WeaponbonusToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "WEAPONBONUS";
	}

	public boolean parse(PCTemplate template, String value) {
		template.setWeaponProfBonus(value);
		return true;
	}
}
