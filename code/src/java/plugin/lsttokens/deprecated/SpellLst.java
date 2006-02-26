/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens.deprecated;
import pcgen.core.PCSpell;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.Deprecated;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author djones4
 *
 */
public class SpellLst implements GlobalLstToken, Deprecated {

	public String getTokenName() {
		return "SPELL";
	}

	public boolean parse(PObject obj, String value, int anInt) {
		obj.getSpellSupport().addSpells(anInt, createSpellList(value));
		return true;
	}

	public String getMessage(PObject obj, String value) {
		return "Use SPELLS: instead.";
	}

	private static List createSpellList(final String sourceLine) {
		List spellList = new ArrayList();

		String preTag = "";
		String spellSrc = "";

		StringBuffer spellSrcBuffer = new StringBuffer();

		// Search for pre-reqs. Tokenize by "|" and look for strings starting with
		// PRE
		StringBuffer preTagBuffer = new StringBuffer();
		StringTokenizer st = new StringTokenizer(sourceLine, "|");

		while (st.hasMoreTokens()) {
			String token = st.nextToken();

			if (token.startsWith("PRE") || token.startsWith("!PRE")) {
				// check as mixed case. Wouldn't want to
				// mistake Prestidigitation as a PRE tag.
				preTagBuffer.append(token);
				preTagBuffer.append("|");
			} else {
				spellSrcBuffer.append(token);
				spellSrcBuffer.append("|");
			}
		}

		// remove final "|" when creating string
		if ((preTagBuffer.length() > 0) && preTagBuffer.toString().endsWith("|")) {
			preTag = preTagBuffer.substring(0, preTagBuffer.length() - 1);
		}
		if ((spellSrcBuffer.length() > 0) && spellSrcBuffer.toString().endsWith("|")) {
			spellSrc = spellSrcBuffer.substring(0, spellSrcBuffer.length() - 1);
		}

		// The following is deprecated but will remain
		// here for backwards compatability
		final int i = sourceLine.lastIndexOf('[');

		if (i >= 0) {
			int j = sourceLine.lastIndexOf(']');

			// If there's a line of LST syntax such as:
			// SPELL:Bless|1|Innate[PREMULT:2,[PRELEVEL:1],[PRESTAT:1,INT=8,WIS=8]]
			// Then not even the deprecated method can handle it. Throw a different
			// warning.
			if (sourceLine.charAt(j - 1) == ']') {
				Logging.errorPrint("The SPELL: tag with [PRExxx] syntax with mulitiple [] characters is no longer supported.");
				Logging.errorPrint("Please change: " + sourceLine);
			}

			if (j < i) {
				j = sourceLine.length();
			}

			preTag = sourceLine.substring(i + 1, j);
			spellSrc = sourceLine.substring(0, i);

			// In the future the "[]" notation to delimite PRExxx
			// tags for the SPELL tag should not be used
			Logging.errorPrint("The SPELL: tag with [PRExxx] syntax has been deprecated.");
			Logging.errorPrint("Please change: " + sourceLine);
		}

		final StringTokenizer aTok = new StringTokenizer(spellSrc, "|");

		while (aTok.hasMoreTokens()) {
			PCSpell spell = new PCSpell();

			// Get the name/key out of the string
			spell.setName(aTok.nextToken());
			spell.setKeyName(spell.getName());

			// Get the number of times per day (default is 1)
			if (aTok.hasMoreTokens()) {
				spell.setTimesPerDay(aTok.nextToken());
			} else {
				spell.setTimesPerDay("1");
			}

			// Get the spellbook (default is Innate)
			if (aTok.hasMoreTokens()) {
				spell.setSpellbook(aTok.nextToken());
			} else {
				spell.setSpellbook("Innate");
			}

			// Set the pre-reqs if needed
			if (preTag != null) {
				StringTokenizer preTok = new StringTokenizer(preTag, "|");

				while (preTok.hasMoreTokens()) {
					try {
						PreParserFactory factory = PreParserFactory.getInstance();
						spell.addPreReq(factory.parse(preTok.nextToken()));
					} catch (PersistenceLayerException ple) {
						Logging.errorPrint(ple.getMessage(), ple);
					}
				}
			}

			// add the spell to the list
			spellList.add(spell);
		}
		return spellList;
	}
}
