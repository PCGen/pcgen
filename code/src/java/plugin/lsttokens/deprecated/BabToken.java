package plugin.lsttokens.deprecated;

import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.RaceLstToken;
import pcgen.util.PropertyFactory;

/**
 * Class deals with BAB Token
 */
public class BabToken implements RaceLstToken, DeprecatedToken 
{
	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName() 
	{
		return "BAB"; //$NON-NLS-1$
	}

	/**
	 * @see pcgen.persistence.lst.RaceLstToken#parse(pcgen.core.Race, java.lang.String)
	 */
	public boolean parse(Race race, String value) 
	{
		try 
		{
			final int bonus = Integer.parseInt(value);
			final BonusObj babBonus = Bonus.newBonus( "BONUS:COMBAT|BAB|" + bonus ); //$NON-NLS-1$
			babBonus.setCreatorObject(race);
			race.addBonusList(babBonus);
			return true;
		}
		catch(NumberFormatException nfe) 
		{
			return false;
		}
	}

	/**
	 * @see pcgen.persistence.lst.DeprecatedToken#getMessage(pcgen.core.PObject, java.lang.String)
	 */
	public String getMessage(@SuppressWarnings("unused")PObject anObj, 
							 @SuppressWarnings("unused")String anValue)
	{
		return PropertyFactory.getString("Warnings.LstTokens.Deprecated.BabToken"); //$NON-NLS-1$
	}
}
