package plugin.lsttokens.gamemode.rollmethod;

import java.util.Map;

import pcgen.persistence.lst.RollMethodLoader;
import pcgen.persistence.lst.RollMethodLstToken;

/**
 * Class deals with ROLLMETHOD Token
 */
public class RollmethodToken implements RollMethodLstToken {

	public String getTokenName() {
		return "ROLLMETHOD";
	}

	public boolean parse(Map method, String value) {
		method.put(RollMethodLoader.ROLLMETHOD, value);
		return true;
	}
}
