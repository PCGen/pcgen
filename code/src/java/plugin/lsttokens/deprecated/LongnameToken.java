package plugin.lsttokens.deprecated;

import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with LONGNAME token 
 */
public class LongnameToken implements EquipmentLstToken, DeprecatedToken {

	public String getTokenName() {
		return "LONGNAME";
	}

	public boolean parse(Equipment eq, String value) {
		eq.setLongName(value);
		return true;
	}

	public String getMessage(PObject obj, String value) {
		return "Use OUTPUTNAME: instead.";
	}
}
