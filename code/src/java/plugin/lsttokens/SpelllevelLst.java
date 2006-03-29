/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 *
 */
public class SpelllevelLst implements GlobalLstToken {

	public String getTokenName() {
		return "SPELLLEVEL";
	}

	public boolean parse(PObject obj, String value, int anInt) {
		// SPELLLEVEL:CLASS|Name1,Name2=Level1|Spell1,Spell2,Spell3|Name3=Level2|Spell4,Spell5|PRExxx|PRExxx
		if (!(obj instanceof Campaign)) {
			final StringTokenizer tok = new StringTokenizer(value, "|");

			if (tok.countTokens() < 3) {
				Logging.errorPrint("Badly formed SPELLLEVEL tag1: " + value);
				return false;
			}

			final String tagType = tok.nextToken(); // CLASS or DOMAIN
			final List preList = new ArrayList();

			// The 2 lists below should always have the same number of items
			final List wNameList = new ArrayList();
			final List wSpellList = new ArrayList();

			while (tok.hasMoreTokens()) {
				final String nameList = tok.nextToken();

				if (nameList.startsWith("PRE") || nameList.startsWith("!PRE")) {
					preList.add(nameList);
					break;
				}

				if (nameList.indexOf("=") < 0) {
					Logging.errorPrint("Badly formed SPELLLEVEL tag2: " + value);
					return false;
				}

				wNameList.add(nameList);

				if (!tok.hasMoreTokens()) {
					Logging.errorPrint("Badly formed SPELLLEVEL tag3: " + value);
					return false;
				}

				wSpellList.add(tok.nextToken());
			}

			while (tok.hasMoreTokens()) {
				final String nameList = tok.nextToken();

				if (nameList.startsWith("PRE") || nameList.startsWith("!PRE")) {
					preList.add(nameList);
				}
				else {
					Logging.errorPrint("Badly formed SPELLLEVEL PRE tag: " + value);
					return false;
				}
			}

			for (Iterator iSpell = wSpellList.iterator(), iName = wNameList.iterator(); iSpell.hasNext() || iName.hasNext();) {
				// Check to see if both exists
				if (!(iSpell.hasNext() && iName.hasNext())) {
					Logging.errorPrint("Badly formed SPELLLEVEL tag4: " + value);
					return false;
				}

				final StringTokenizer bTok = new StringTokenizer((String) iSpell.next(), ",");
				final String classList = (String) iName.next();

				while (bTok.hasMoreTokens()) {
					final String spellLevel = classList.substring(classList.indexOf("=") + 1);
					final String spellName = bTok.nextToken();
					final StringTokenizer cTok = new StringTokenizer(classList.substring(0, classList.indexOf("=")), ",");

					while (cTok.hasMoreTokens()) {
						final String className = cTok.nextToken();

						if (className.startsWith("SPELLCASTER.")
								|| !obj.getSpellSupport().containsLevelFor(tagType, className, spellName)) {
							obj.getSpellSupport().addSpellLevel(tagType, className, spellName, spellLevel, preList);
						}
					}
				}
			}
			return true;
		}
		return false;
	}
}

