package pcgen.io.exporttoken;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.util.enumeration.Visibility;

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
	private List<Ability> feat = new ArrayList<Ability>();
	private PlayerCharacter cachedPC = null;
	private int cachedPcSerial = 0;
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

		if (cachedPC != pc || !fString.equals(lastMode) || cachedPcSerial != pc.getSerial())
		{
			// Overridden by subclasses to return the right list.
			feat = getFeatList(pc);
		}
		cachedPC = pc;
		lastMode = fString;
		cachedPcSerial = pc.getSerial();

		List<String> types  = new ArrayList<String>();
		List<String> negate = new ArrayList<String>();
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

			while (aTok.hasMoreTokens())
			{
				final String typeStr = aTok.nextToken();

				int typeInd = typeStr.indexOf("TYPE");
				if (typeInd != -1 && typeStr.length() > 4)
				{
					if (typeInd > 0)
					{
						negate.add(typeStr.substring(typeInd + 5));
					}
					else
					{
						types.add(typeStr.substring(typeInd + 5));
					}
				}
			}
		}

		List<Ability> aList = new ArrayList<Ability>();

		Globals.sortPObjectListByName(feat);

		for (Ability aFeat : feat) {

			boolean matchTypeDef       = false;
			boolean matchVisibilityDef = false;

			if (featType != null) {
				if (aFeat.isType(featType)) {
					matchTypeDef = true;
				}
			} else {
				matchTypeDef = true;
			}

			boolean istype   = false;
			boolean isnttype = true;

			// is at leas one of the types we've asked for
			if (types.size() > 0)
			{
				for (String typeStr : types)
				{
					istype |= aFeat.isType(typeStr);
				}
			}
			else
			{
				istype = true;
			}

			// isn't all the types we've said it's not
			for (String typeStr : negate)
			{
				isnttype &= !aFeat.isType(typeStr);
			}

			matchTypeDef = matchTypeDef && istype && isnttype;

			switch ( visibility )
			{
			case FEAT_ALL:
				matchVisibilityDef = true;
				break;
			case FEAT_HIDDEN:
				if ( aFeat.getVisibility() == Visibility.HIDDEN 
				  || aFeat.getVisibility() == Visibility.DISPLAY_ONLY ) 
				{
					matchVisibilityDef = true;
				}
				break;
			case FEAT_VISIBLE: // Fall thru intentional
			default:
				if ( aFeat.getVisibility() == Visibility.DEFAULT 
				  || aFeat.getVisibility() == Visibility.OUTPUT_ONLY ) 
				{
					matchVisibilityDef = true;
				}
				break;	
			}

			if (matchTypeDef && matchVisibilityDef) {
				aList.add(aFeat);
			}
		}

		Ability aFeat;
		if (i < aList.size()) {
			aFeat = aList.get(i);

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
				retString += aFeat.getDefaultSourceString();
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
	protected List<Ability> getFeatList(PlayerCharacter pc)
	{
		List<Ability> featList = new ArrayList<Ability>();
		for (Ability aFeat : pc.getRealFeatList()) {
			featList.add(aFeat);
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
