package pcgen.io.exporttoken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;

/**
 * @author karianna
 *
 * Class deals with FEAT Token
 */
public class FeatToken extends Token {

	/** Token Name */
	public static final String TOKENNAME = "FEAT";

	/** Default Feat = 0**/
	public final int FEAT_DEFAULT = 0;
	/** Visible Feat = 1 */
	public final int FEAT_VISIBLE = 1;
	/** Hidden Feat = 2 */
	public final int FEAT_HIDDEN = 2;
	/** All Feats = 3 */
	public final int FEAT_ALL = 3;

	private int visibility = FEAT_DEFAULT;
	private List feat = new ArrayList();
	private PlayerCharacter cachedPC = null;
	private String lastMode = "";

	/**
	 * Get the TOKENNAME
	 * @return TOKENNAME
	 */
	public String getTokenName() {
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String,
	 *      pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh) {

		String retString = "";

		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		final String fString = aTok.nextToken();

		if (cachedPC != pc || !fString.equals(lastMode))
		{
			// Overridden by subclasses to return the right list.
			feat = getFeatList(pc);
		}
		cachedPC = pc;
		lastMode = fString;

		String typeStr = "";
		String featType = null;

		// i holds the number of the feat we want, is decremented
		// as we iterate through the list. It is only decremented
		// if the current feat matches the desired feat
		int i = -1;


		if ("FEAT".equals(fString) || "VFEAT".equals(fString) || "FEATALL".equals(fString) || "FEATAUTO".equals(fString)) {
			while (aTok.hasMoreTokens()) {
				final String bString = aTok.nextToken();

				try {
					i = Integer.parseInt(bString);

					break;
				} catch (NumberFormatException exc) {
					if (bString.equals("VISIBLE")) {
						visibility = FEAT_VISIBLE;
						continue;
					} else if (bString.equals("HIDDEN")) {
						visibility = FEAT_HIDDEN;
						continue;
					} else if (bString.equals("ALL")) {
						visibility = FEAT_ALL;
						continue;
					} else {
						featType = bString;
					}
				}
			}

			if (aTok.hasMoreTokens()) {
				typeStr = aTok.nextToken();

				if (!(typeStr.startsWith("TYPE") || typeStr.startsWith("!TYPE"))) {
					typeStr = "";
				}
			}
		}

		List aList = new ArrayList();

		Globals.sortPObjectList(feat);

		Ability aFeat;

		for (Iterator e = feat.iterator(); e.hasNext();) {
			aFeat = (Ability) e.next();

			boolean matchTypeDef = false;
			boolean matchVisibilityDef = false;

			if (featType != null) {
				if (aFeat.isType(featType)) {
					matchTypeDef = true;
				}
			} else {
				matchTypeDef = true;
			}

			if ((tokenSource.indexOf(".!TYPE") >= 0) && (typeStr.length() > 6)) {
				matchTypeDef = !aFeat.isType(typeStr.substring(6));
			} else if ((tokenSource.indexOf(".TYPE") >= 0) && (typeStr.length() > 5)) {
				matchTypeDef = aFeat.isType(typeStr.substring(5));
			}

			if ((aFeat.getVisible() == Ability.VISIBILITY_HIDDEN) || (aFeat.getVisible() == Ability.VISIBILITY_DISPLAY_ONLY)) {
				// never display hidden feats unless asked for directly
				if (visibility == FEAT_HIDDEN) {
					matchVisibilityDef = true;
				}
			} else if (visibility == FEAT_ALL) {
				// We want all visible feats
				matchVisibilityDef = true;
			} else if ((aFeat.getVisible() == Ability.VISIBILITY_DEFAULT) || (aFeat.getVisible() == Ability.VISIBILITY_OUTPUT_ONLY)) {
				// default or output
				if (((visibility == FEAT_DEFAULT) || (visibility == FEAT_VISIBLE))) {
					matchVisibilityDef = true;
				}
			}

			if (matchTypeDef && matchVisibilityDef) {
				aList.add(aFeat);
			}
		}

		if (i < aList.size()) {
			aFeat = (Ability) aList.get(i);

			if (i == aList.size() - 1 && eh != null && eh.getExistsOnly())
			{
				eh.setNoMoreItems(true);
			}
			else if (eh != null && eh.getExistsOnly())
			{
				eh.setNoMoreItems(true);
			}

			if (tokenSource.endsWith(".DESC")) {
				retString += aFeat.getBenefitDescription();
			} else if (tokenSource.endsWith(".TYPE")) {
				retString += aFeat.getType();
			} else if (tokenSource.endsWith(".ASSOCIATED")) {
				StringBuffer buf = new StringBuffer();

				for (int j = 0; j < aFeat.getAssociatedCount(); j++) {
					if (j != 0) {
						buf.append(",");
					}
					buf.append(aFeat.getAssociated(j));
				}

				retString += buf.toString();
			} else if (tokenSource.endsWith(".ASSOCIATEDCOUNT")) {
				retString += Integer.toString(aFeat.getAssociatedCount());
			} else if (tokenSource.endsWith(".SOURCE")) {
				retString += aFeat.getSource();
			} else {
				retString += aFeat.qualifiedName();
			}
		}

		return retString;
	}

	/**
	 * Returns the correct list of feats for the character.
	 * This method is overridden in subclasses if they need to change the list
	 * of feats looked at.
	 *
	 * @param pc the character who's feats we are retrieving.
	 * @return List of feats.
	 */
	protected List getFeatList(PlayerCharacter pc)
	{
		List featList = new ArrayList();
		Iterator anIt = pc.getRealFeatsIterator();
		while (anIt.hasNext()) {
			featList.add(anIt.next());
		}
		return featList;
	}

	/**
	 * Set the visibility
	 * @param aVisibility
	 */
	protected void setVisibility(int aVisibility) {
		visibility = aVisibility;
	}
}
