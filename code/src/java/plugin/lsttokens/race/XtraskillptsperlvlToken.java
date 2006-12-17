package plugin.lsttokens.race;

import pcgen.core.Race;
import pcgen.persistence.lst.RaceLstToken;

/**
 * Class deals with XTRASKILLPTSPERLVL Token
 */
public class XtraskillptsperlvlToken implements RaceLstToken
{

	/**
	 * Get token name
	 * @return token name
	 */
	public String getTokenName()
	{
		return "XTRASKILLPTSPERLVL";
	}

	/**
	 * Parse XTRASKILLPTSPERLVL 
	 * @param race 
	 * @param value 
	 * @return true if successful else false
	 */
	public boolean parse(Race race, String value)
	{
		try
		{
			race.setBonusSkillsPerLevel(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}
}
