package plugin.lsttokens.gamemode.rollmethod;

import java.util.Map;

import pcgen.persistence.lst.RollMethodLoader;
import pcgen.persistence.lst.RollMethodLstToken;

/**
 * Class deals with METHOD Token
 */
public class MethodToken implements RollMethodLstToken {

	public String getTokenName() {
		return "METHOD";
	}

	public boolean parse(Map method, String value) {
		method.put(RollMethodLoader.METHOD, value);
		return true;
	}
}
