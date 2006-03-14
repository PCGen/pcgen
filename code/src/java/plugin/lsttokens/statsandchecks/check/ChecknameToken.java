package plugin.lsttokens.statsandchecks.check;

import java.util.Iterator;

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
		final Iterator iter = SettingsHandler.getGame().getUnmodifiableCheckList().iterator();

		while (iter.hasNext()) {
			final PObject testObj = (PObject) iter.next();

			if (testObj.getName().equals(obj.getName())) {
				return true; //we already have this object in our list, so just return
			}
		}

		SettingsHandler.getGame().addToCheckList(obj);
		return true;
	}
}
