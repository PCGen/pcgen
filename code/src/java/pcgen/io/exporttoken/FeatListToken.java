package pcgen.io.exporttoken;

import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author karianna
 * Class deals with FEATLIST Token
 */
public class FeatListToken extends Token
{
	String delim = "";
	List featList = null;
	PlayerCharacter lastPC = null;
	String lastType = "";

	/** Token Name */
	public static final String TOKENNAME = "FEATLIST";

	/**
	 * Get the TOKENNAME
	 * @return TOKENNAME
	 */
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		String retString = "";

		delim = getDelimiter(tokenSource);

		if (lastType != getTokenName() || pc != lastPC)
		{
			featList = getFeatList(pc);
		}

		if ((delim == null) || "".equals(delim))
		{
			delim = ", ";
		}

		String aString = "";

		if (delim.lastIndexOf('.') >= 0)
		{
			try
			{
				aString = delim.substring(delim.lastIndexOf('.'));
			}
			catch (StringIndexOutOfBoundsException e)
			{
				return retString;
			}
		}

		delim = ", ";

		int i = 0;
		int dotpos = aString.indexOf('.');
		String typeStr = "";

		if (aString.indexOf("TYPE") > 0)
		{
			typeStr = aString.substring(dotpos);
		}

		Globals.sortPObjectList(featList);

		Ability aFeat;

		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			aFeat = (Ability) e.next();

			String test; //will hold token we are testing against
			StringTokenizer st = null;
			int clusive = 0; //clusive 0 = no type, 1 = inclusive(.TYPE), 2 = exclusive(.!TYPE=)
			int match = 0; //match  0 = does not match type, 1 = feat matches type

			//Test for inclusive/exclusive
			if (aString.indexOf(".TYPE=") >= 0)
			{
				clusive = 1;
				st = new StringTokenizer(typeStr.substring(5), "=");
			}
			else if (aString.indexOf(".!TYPE=") >= 0)
			{
				clusive = 2;
				st = new StringTokenizer(typeStr.substring(6), "=");
			}

			if ((clusive == 1) || (clusive == 2))
			{
				while (st.hasMoreTokens())
				{
					test = st.nextToken();

					if (aFeat.isType(test))
					{
						match = 1;
					}
				}
			}

			int doIprint = 1;

			if (((clusive == 1) && (match == 0)) || ((clusive == 2) && (match == 1)))
			{
				doIprint = 0;
			}

			if (doIprint == 1)
			{
				if ((aFeat.getVisible() == Ability.VISIBILITY_DEFAULT)
					|| (aFeat.getVisible() == Ability.VISIBILITY_OUTPUT_ONLY))
				{
					if (i > 0)
					{
						retString += delim;
					}

					retString += aFeat.qualifiedName();
					++i;
				}
			}
		}

		return retString;
	}

	protected String getDelimiter(final String tokenSource)
	{
		return tokenSource.substring(8);
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
		List listOfFeats = new ArrayList();
		Iterator anIt = pc.getRealFeatsIterator();
		while (anIt.hasNext()) {
			listOfFeats.add(anIt.next());
		}
		return listOfFeats;
	}

	/**
	 * Set the deliminator
	 * @param aDelim
	 */
	protected void setDelim(String aDelim) {
		delim = aDelim;
	}

}
