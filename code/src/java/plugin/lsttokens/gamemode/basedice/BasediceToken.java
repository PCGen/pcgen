package plugin.lsttokens.gamemode.basedice;

import java.util.Map;

import pcgen.persistence.lst.BaseDiceLoader;
import pcgen.persistence.lst.BaseDiceLstToken;

/**
 * Class deals with BASEDICE Token
 */
public class BasediceToken implements BaseDiceLstToken {

	public String getTokenName() {
		return "BASEDICE";
	}

	public boolean parse(Map baseDice, String value) {
		baseDice.put(BaseDiceLoader.BASEDICE, value);
		return true;
	}
}
