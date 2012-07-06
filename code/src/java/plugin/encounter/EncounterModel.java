package plugin.encounter;

import gmgen.io.ReadXML;

import java.io.File;
import java.lang.reflect.Array;

import javax.swing.DefaultListModel;

import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;

/**
 * This <code>class</code> holds all the necessary data in order to have
 * functionality for the Encounter Generator.<br>
 * Created on February 19, 2003<br>
 * Updated on March 12, 2003
 * @author  Expires 2003
 * @author John Dells <JohnDells@woh.rr.com>
 * @version 2.10
 */
public class EncounterModel extends DefaultListModel
{
	private String dir;

	/** All the characters or creatures in combat. */
	private PlayerCharacter[] PCs;

	/**
	 * Creates a new instance of EncounterModel
	 */
	public EncounterModel()
	{
		this("");
	}

	/**
	 * Creates a new instance of EncounterModel
	 * @param parentDir
	 */
	public EncounterModel(String parentDir)
	{
		dir = parentDir;
	}

	/**
	 * Gets the challenge rating of the group of characters.
	 * @return the challenge rating.
	 */
	public int getCR()
	{
		ReadXML xml;
		File f = new File(dir + File.separator + "encounter_tables/4_1.xml");
		int i;
		float cr = 0;

		xml = new ReadXML(f);
		xml.getTable();

		for (i = 0; i < size(); i++)
		{
			Race aRace = Globals.getContext().ref.silentlyGetConstructedCDOMObject(Race.class, (String) elementAt(i));
			ChallengeRating rcr = aRace.get(ObjectKey.CHALLENGE_RATING);
			if (rcr != null)
			{
				/*
				 * TODO null may be a problem here?
				 */
				cr += mCRtoPL(rcr.getRating().resolve(null, "").floatValue());
			}
		}

		cr = mPLtoCR(cr);

		if (cr < 0)
		{
			cr = 0;
		}

		return (int) (cr + .5);
	}

	/**
	 * Sets the <code>Array</code> of <code>PlayerCharacters</code>.
	 * @param len the number of characters being created.
	 */
	public void setPCs(int len)
	{
		int x;
		PCs = (PlayerCharacter[]) Array.newInstance(PlayerCharacter.class, len);

		for (x = 0; x < len; x++)
		{
			PCs[x] = new PlayerCharacter();
		}
	}

	/**
	 * Gets all the characters in the encounter.
	 * @return the <code>Array</code> of characters.
	 */
	public PlayerCharacter[] getPCs()
	{
		return PCs;
	}

	/**
	 * Takes the CR of a monster and transforms it into "power level", used when summing monsters for total CR
	 * @param x
	 * @return "power level"
	 */
	public float mCRtoPL(float x)
	{
		float iReturn = 0;

		if (x < 1)
		{
			iReturn = x;
		}
		else
		{
			iReturn = (float) Math.exp((x - 1) / 2);
		}

		return iReturn;
	}

	/**
	 * Takes a "power level" into CR. See <code>mCRtoPL()</code> for details.
	 * @param x
	 * @return "power level"
	 */
	public int mPLtoCR(float x)
	{
		return (int) ((2 * Math.log(x)) + 1);
	}
}
