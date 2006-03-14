package plugin.lsttokens.gamemode.wieldcategory;

import pcgen.core.PObject;
import pcgen.core.character.WieldCategory;
import pcgen.persistence.lst.Deprecated;
import pcgen.persistence.lst.WieldCategoryLstToken;

/**
 * Class deals with DAMAGEMULT Token
 */
public class DamagemultToken implements WieldCategoryLstToken, Deprecated {

	public String getTokenName() {
		return "DAMAGEMULT";
	}

	public boolean parse(WieldCategory cat, String value) {
		/*
		// The damage multiplier based on
		// number of hands used to wield weapon
		// dString is of form:
		// 1=1,2=1.5
		StringTokenizer dTok = new StringTokenizer(val, ",");

		while (dTok.hasMoreTokens()) {
			String cString = dTok.nextToken();

			// cString is of form: 2=1.5
			StringTokenizer cTok = new StringTokenizer(cString, "=");

			if (cTok.countTokens() < 2) {
				continue;
			}

			//TODO: (DJ) what the hell is this?
			cTok.nextToken();
			cTok.nextToken();
		}
		*/
		return false;
	}

	public String getMessage(PObject obj, String value) {
		return "DAMAGEMULT broken, needs re-implementing";
	}
}
