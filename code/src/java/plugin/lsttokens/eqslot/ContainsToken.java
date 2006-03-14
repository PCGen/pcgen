package plugin.lsttokens.eqslot;

import java.util.StringTokenizer;

import pcgen.core.character.EquipSlot;
import pcgen.persistence.lst.EquipSlotLstToken;

/**
 * Class deals with CONTAINS Token
 */
public class ContainsToken implements EquipSlotLstToken {

	public String getTokenName() {
		return "CONTAINS";
	}

	public boolean parse(EquipSlot eqSlot, String value) {
		final StringTokenizer token = new StringTokenizer(value, "=");

		if (token.countTokens() == 2) {
			final String type = token.nextToken();
			final String numString = token.nextToken();
			final int num;

			if (numString.equals("*")) {
				num = 9999;
			}
			else {
				num = Integer.parseInt(numString);
			}

			eqSlot.setContainType(type);
			eqSlot.setContainNum(num);
		}
		return true;
	}
}
