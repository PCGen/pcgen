package plugin.lsttokens.gamemode.wieldcategory;

import java.util.StringTokenizer;

import pcgen.core.character.WieldCategory;
import pcgen.persistence.lst.WieldCategoryLstToken;

/**
 * Class deals with DOWN Token
 */
public class DownToken implements WieldCategoryLstToken {

	public String getTokenName() {
		return "DOWN";
	}

	public boolean parse(WieldCategory cat, String value) {
		//The wield category steps
		StringTokenizer dTok = new StringTokenizer(value, "|");
		int count = -1;

		while (dTok.hasMoreTokens()) {
			String dString = dTok.nextToken();
			cat.setWCStep(count, dString);
			count--;
		}
		return true;
	}
}
