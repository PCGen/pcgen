package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.persistence.lst.PObjectLoader;

/**
 * Deals with AC token 
 */
public class AcToken implements EquipmentLstToken {

	/**
     * Return token name
     * @return token name 
	 */
    public String getTokenName() {
		return "AC";
	}

    /**
     * Parse the AC token for equipment
     * 
     * @param eq 
     * @param value 
     * @return true if parse OK 
     */
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

