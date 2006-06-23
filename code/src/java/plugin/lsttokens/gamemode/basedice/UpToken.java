package plugin.lsttokens.gamemode.basedice;

import java.util.Map;

import pcgen.persistence.lst.BaseDiceLoader;
import pcgen.persistence.lst.BaseDiceLstToken;

/**
 * Class deals with UP Token
 */
public class UpToken implements BaseDiceLstToken {

	public String getTokenName() {
		return "UP";
	}

	public boolean parse(Map<String, String> baseDice, String value) {
		baseDice.put(BaseDiceLoader.UP, value);
		return true;
	}
}
