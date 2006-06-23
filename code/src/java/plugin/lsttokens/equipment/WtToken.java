package plugin.lsttokens.equipment;

import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with WT token 
 */
public class WtToken implements EquipmentLstToken {

	/**
     * Get token name
     * @return token name
	 */
    public String getTokenName() {
		return "WT";
	}

    /**
     * Parse WT token, set the equipment weight
     * @param eq 
     * @param value 
     * @return true
     */
	public boolean parse(Equipment eq, String value) {
		eq.setWeight(value);
		return true;
	}
}
