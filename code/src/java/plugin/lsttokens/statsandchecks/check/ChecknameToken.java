package plugin.lsttokens.statsandchecks.check;

import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.persistence.lst.PCCheckLstToken;

/**
 * Class deals with CHECKNAME Token
 */
public class ChecknameToken implements PCCheckLstToken {

	public String getTokenName() {
		return "CHECKNAME";
	}

	public boolean parse(PObject obj, String value) {
		obj.setName(value);
		for (PObject testObj : SettingsHandler.getGame().getUnmodifiableCheckList())
		{
			if (testObj.getKeyName().equals(obj.getKeyName())) {
				return true; //we already have this object in our list, so just return
			}
		}

		SettingsHandler.getGame().addToCheckList(obj);
		return true;
	}
}
