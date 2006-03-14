package plugin.lsttokens.weaponprof;

import pcgen.core.WeaponProf;
import pcgen.persistence.lst.WeaponProfLstToken;

/**
 * Class deals with QUALIFY Token
 */
public class QualifyToken implements WeaponProfLstToken {

	public String getTokenName() {
		return "QUALIFY";
	}

	public boolean parse(WeaponProf prof, String value) {
		prof.setQualifyString(value);
		return true;
	}
}
