package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.persistence.lst.PObjectLoader;

/**
 * Deals with AC token 
 */
public class AcToken implements EquipmentLstToken {

	public String getTokenName() {
		return "AC";
	}

	public boolean parse(Equipment eq, String value) {
		try {
			final String aBonus = "BONUS:COMBAT|AC|" + value + "|TYPE=Armor.REPLACE";
			PObjectLoader.parseTag(eq, aBonus);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
}

